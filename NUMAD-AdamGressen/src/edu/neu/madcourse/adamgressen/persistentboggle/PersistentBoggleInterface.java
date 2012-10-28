package edu.neu.madcourse.adamgressen.persistentboggle;

import java.util.List;

public interface PersistentBoggleInterface {

	public void setUserID(String UserID);
	
	public void setOpponent(String opponent);
	
	public void setBoard(String board);
	
	public void setScore(int score);
	
	public void setRemoteTime(Long remotetime);
	
	public void setTime(int time);
	
	public void setUsedWordString(String usedwords);
	
}
