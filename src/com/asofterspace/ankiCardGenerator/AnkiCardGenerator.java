/**
 * Unlicensed code created by A Softer Space, 2020
 * www.asofterspace.com/licenses/unlicense.txt
 */
package com.asofterspace.ankiCardGenerator;

import com.asofterspace.toolbox.io.JSON;
import com.asofterspace.toolbox.io.JsonFile;
import com.asofterspace.toolbox.io.JsonParseException;
import com.asofterspace.toolbox.io.SimpleFile;
import com.asofterspace.toolbox.utils.Record;
import com.asofterspace.toolbox.Utils;
import com.asofterspace.toolbox.web.WebAccessor;
import com.asofterspace.toolbox.web.WebExtractor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class AnkiCardGenerator {

	public final static String PROGRAM_TITLE = "AnkiCardGenerator";
	public final static String VERSION_NUMBER = "0.0.0.1(" + Utils.TOOLBOX_VERSION_NUMBER + ")";
	public final static String VERSION_DATE = "7. May 2020 - 7. May 2020";

	public final static String CARDS = "cards";
	public final static String NAME = "name";
	public final static String MANA = "mana";
	public final static String ARTIST = "artist";
	public final static String IMAGE = "image";
	public final static String SMALL_IMAGE = "small_image";
	public final static String ORACLE_TEXT = "oracle_text";
	public final static String SET = "set";
	public final static String TAGS = "tags";
	public final static String TYPE = "type";
	public final static String STATS = "stats";

	private final static Map<String, String> setToFullSet = new HashMap<>();


	public static void main(String[] args) {

		// let the Utils know in what program it is being used
		Utils.setProgramTitle(PROGRAM_TITLE);
		Utils.setVersionNumber(VERSION_NUMBER);
		Utils.setVersionDate(VERSION_DATE);

		if (args.length > 0) {
			if (args[0].equals("--version")) {
				System.out.println(Utils.getFullProgramIdentifierWithDate());
				return;
			}

			if (args[0].equals("--version_for_zip")) {
				System.out.println("version " + Utils.getVersionNumber());
				return;
			}
		}

		System.out.println("Loading previous files...");

		JsonFile mainConf = new JsonFile("config/database.json");
		JSON root = null;
		try {
			root = mainConf.getAllContents();
		} catch (JsonParseException e) {
			System.err.println("Oh no!");
			e.printStackTrace(System.err);
			System.exit(1);
		}

		List<Record> ankiCards = root.getArray(CARDS);


		List<String> sets = new ArrayList<>();
		sets.add("iko");
		setToFullSet.put("iko", "Ikoria: Lair of Behemoths (IKO)");

		for (String set : sets) {

			String fullSet = setToFullSet.get(set);
			if (fullSet == null) {
				System.err.println("The set " + set + " is unknown to me! Aborting...");
				System.exit(2);
			}

			boolean alreadyLoaded = false;

			for (Record card : ankiCards) {
				if (fullSet.equals(card.getString(SET))) {
					System.out.println("Cards of the set " + fullSet +
						" have already been loaded. It will not be crawled again.");
					alreadyLoaded = true;
					break;
				}
			}

			if (alreadyLoaded) {
				continue;
			}

			System.out.println("Loading new data from the web for set " + fullSet + "...");

			String html = WebAccessor.get("https://scryfall.com/sets/" + set + "?as=grid&order=set");
			String cardStart = "<a class=\"card-grid-item-card\" href=\"";

			while (html.contains(cardStart)) {
				html = html.substring(html.indexOf(cardStart) + cardStart.length());
				String link = html.substring(0, html.indexOf("\""));

				Utils.sleep(1000);

				String linkHtml = WebAccessor.get(link);

				Record card = Record.emptyObject();

				card.set(NAME, WebExtractor.extract(linkHtml, "<h1 class=\"card-text-title\" lang=\"en\">", "<").trim());

				String manaStr = WebExtractor.extract(linkHtml, "<abbr", "<");
				manaStr = manaStr.substring(manaStr.indexOf(">") + 1);
				manaStr = manaStr.trim();
				card.set(MANA, manaStr);

				String artistStr = WebExtractor.extract(linkHtml, "<p class=\"card-text-artist\">", "</a>");
				artistStr = artistStr.substring(artistStr.indexOf(">") + 1);
				artistStr = artistStr.trim();
				card.set(ARTIST, artistStr);

				card.set(TYPE, WebExtractor.extract(linkHtml, "<p class=\"card-text-type-line\" lang=\"en\">", "<").trim());

				String oracleStr = WebExtractor.extract(linkHtml, "<div class=\"card-text-oracle\">", "</div>");
				if (oracleStr == null) {
					card.set(ORACLE_TEXT, "");
				} else {
					oracleStr = oracleStr.replaceAll("<p>", "");
					oracleStr = oracleStr.replaceAll("</p>", "");
					oracleStr = oracleStr.trim();
					card.set(ORACLE_TEXT, oracleStr);
				}

				String statsStr = WebExtractor.extract(linkHtml, "<div class=\"card-text-stats\">", "</div>");
				if (statsStr == null) {
					card.set(STATS, "");
				} else {
					card.set(STATS, statsStr.trim());
				}

				List<String> tags = new ArrayList<>();
				card.set(TAGS, tags);

				card.set(SET, WebExtractor.extract(linkHtml, "<span class=\"prints-current-set-name\">", "<").trim());

				card.set(IMAGE, "https://img.scryfall.com/cards/normal/front/" +
					WebExtractor.extract(linkHtml, "https://img.scryfall.com/cards/normal/front/", "\"").trim());

				card.set(SMALL_IMAGE, "https://img.scryfall.com/cards/art_crop/front/" +
					WebExtractor.extract(linkHtml, "https://img.scryfall.com/cards/art_crop/front/", "\"").trim());

				ankiCards.add(card);

				// save the database
				root.set(CARDS, ankiCards);
				mainConf.setAllContents(root);
				mainConf.save();
			}
		}


		System.out.println("Saving new Anki cards...");

		SimpleFile ankiFile = new SimpleFile("anki_cards.txt");
		List<String> contents = new ArrayList<>();
		contents.add("name;mana;type;oracle text;image;artist;set;small image;stats;Tags");
		for (Record card : ankiCards) {
			String line = "";
			line += sanitizeOut(card.getString(NAME)) + ";";
			line += sanitizeOut(card.getString(MANA)) + ";";
			line += sanitizeOut(card.getString(TYPE)) + ";";
			line += sanitizeOut(card.getString(ORACLE_TEXT)) + ";";
			line += sanitizeOut(card.getString(IMAGE)) + ";";
			line += sanitizeOut(card.getString(ARTIST)) + ";";
			line += sanitizeOut(card.getString(SET)) + ";";
			line += sanitizeOut(card.getString(SMALL_IMAGE)) + ";";
			line += sanitizeOut(card.getString(STATS)) + ";";
			// TODO: if we want to use tags, figure out how anki wants them to be passed in
			line += sanitizeOut("");
			contents.add(line);
		}
		ankiFile.saveContents(contents);


		System.out.println("Done! Have a nice day! :)");
	}

	private static String sanitizeOut(String str) {
		str = str.replace(";", ",");
		if (str.contains("\n")) {
			str = "\"" + str + "\"";
		}
		return str;
	}

}
