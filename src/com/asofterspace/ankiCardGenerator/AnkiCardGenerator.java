/**
 * Unlicensed code created by A Softer Space, 2020
 * www.asofterspace.com/licenses/unlicense.txt
 */
package com.asofterspace.ankiCardGenerator;

import com.asofterspace.toolbox.io.JSON;
import com.asofterspace.toolbox.io.JsonFile;
import com.asofterspace.toolbox.io.JsonParseException;
import com.asofterspace.toolbox.utils.Record;
import com.asofterspace.toolbox.Utils;
import com.asofterspace.toolbox.web.WebAccessor;
import com.asofterspace.toolbox.web.WebExtractor;

import java.util.List;


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


		System.out.println("Loading new data from the web...");
		String set = "iko";
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

			/*
			card.set(IMAGE, TODO);
			card.set(SMALL_IMAGE, TODO);
			card.set(ORACLE_TEXT, TODO);
			card.set(SET, TODO);
			card.set(TAGS, TODO);
			card.set(TYPE, TODO);
			*/

			ankiCards.add(card);

			// save the database
			root.set(CARDS, ankiCards);
			mainConf.setAllContents(root);
			mainConf.save();
		}


		System.out.println("Saving new Anki cards...");

		// TODO


		System.out.println("Done! Have a nice day! :)");
	}

}
