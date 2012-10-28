package edu.neu.madcourse.adamgressen.persistentboggle;

import java.util.Collections;
import java.util.List;

public class Leaderboard {
	List<LeaderboardEntry> entries;
	
	public Leaderboard(List<LeaderboardEntry> entries) {
		this.entries = entries;
	}
	
	/** Add an entry to this leaderboard */
	public void addEntry(LeaderboardEntry entry) {
		boolean exists = false;
		for (LeaderboardEntry e : this.entries) {
			if (e == entry)
				exists = true;
		}
		if (!exists)
			this.entries.add(entry);
	}
	
	/** Given a leaderboard, combine it with this leaderboard */
	public void combineLeaderboards(Leaderboard leaderboard) {
		for (LeaderboardEntry entry : leaderboard.entries) {
			this.entries.add(entry);
		}
		
		this.sortAndRemove();
	}
	
	/** Sorts the leaderboard entries by score and
	 *  limits the number of entries to 10 */
	public void sortAndRemove() {
		Collections.sort(this.entries, Collections.reverseOrder());
		if (this.entries.size() > 10)
			this.entries = this.entries.subList(0, 10);
	}
}