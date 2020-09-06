/**
 * Unlicensed code created by A Softer Space, 2020
 * www.asofterspace.com/licenses/unlicense.txt
 */
package com.asofterspace.ankiCardGenerator;


public class HumanCardEntry {

	private HumanCard parent;

	private String dataStr;


	public HumanCardEntry(HumanCard parent, String dataStr) {

		this.parent = parent;

		parent.addEntry(this);

		if (dataStr.startsWith("-")) {
			dataStr = dataStr.substring(1);
		}

		dataStr = dataStr.trim();

		this.dataStr = dataStr;
	}

	public String getQuestion() {
		if (!dataStr.contains(" ")) {
			System.out.println(dataStr + " does not contain a space!");
			return parent.getTopic() + "\n\n" + "* " + dataStr.substring(0, 4) + "... (nur ein Wort!)";
		}
		return parent.getTopic() + "\n\n" + "* " + dataStr.substring(0, dataStr.indexOf(" ")) + "...";
	}

	public String getAnswer() {
		return parent.getTopic() + "\n\n" + "* " + dataStr;
	}

}
