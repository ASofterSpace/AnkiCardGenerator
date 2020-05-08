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

import java.util.List;


public class AnkiCardGenerator {

	public final static String PROGRAM_TITLE = "AnkiCardGenerator";
	public final static String VERSION_NUMBER = "0.0.0.1(" + Utils.TOOLBOX_VERSION_NUMBER + ")";
	public final static String VERSION_DATE = "7. May 2020 - 7. May 2020";

	public final static String CARDS = "cards";


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

		JsonFile mainConf = new JsonFile("config/cards.json");
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
		// TODO


		System.out.println("Saving new Anki cards...");

		root.set(CARDS, ankiCards);
		mainConf.setAllContents(root);
		mainConf.save();


		System.out.println("Done! Have a nice day! :)");
	}

}
