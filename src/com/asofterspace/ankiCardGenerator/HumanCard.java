/**
 * Unlicensed code created by A Softer Space, 2020
 * www.asofterspace.com/licenses/unlicense.txt
 */
package com.asofterspace.ankiCardGenerator;
import java.util.ArrayList;
import java.util.List;


public class HumanCard {

	private String topic;

	private List<HumanCardEntry> entries;


	public HumanCard(String topic) {
		this.topic = topic;
		this.entries = new ArrayList<>();
	}

	public String getTopic() {
		return topic;
	}

	public void setTopic(String topic) {
		this.topic = topic;
	}

	public List<HumanCardEntry> getEntries() {
		return entries;
	}

	public void setEntries(List<HumanCardEntry> entries) {
		this.entries = entries;
	}

	public void addEntry(HumanCardEntry entry) {
		entries.add(entry);
	}

}
