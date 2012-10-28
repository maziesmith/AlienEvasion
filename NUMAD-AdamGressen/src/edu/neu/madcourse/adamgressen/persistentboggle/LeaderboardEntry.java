package edu.neu.madcourse.adamgressen.persistentboggle;

import java.text.SimpleDateFormat;
import java.util.Date;

public class LeaderboardEntry implements Comparable<LeaderboardEntry> {
	String info;
	public String getInfo() { return this.info; }
	int score;

	/** Constructor for a LeaderboardEntry */
	public LeaderboardEntry(String user, int score) {
		Date d = new Date();
		SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy '@' hh:mm a");
		
		this.info = user+" on "+format.format(d)+": Score: "+String.valueOf(score);
		this.score = score;
	}

	/** Compares two entries based on the score */
	public int compareTo(LeaderboardEntry entry) {
		return(this.score - entry.score);
	}
}