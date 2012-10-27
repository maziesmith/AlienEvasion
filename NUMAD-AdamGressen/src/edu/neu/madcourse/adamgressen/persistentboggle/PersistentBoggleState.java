package edu.neu.madcourse.adamgressen.persistentboggle;

import edu.neu.madcourse.adamgressen.persistentboggle.PersistentBoggle.BoggleFields;
import android.content.Context;

public class PersistentBoggleState {
	
	public static final String GET_MODE = "get";
	public static final String SET_MODE = "set";
	public static final String CLEAR_MODE = "clear";
	
	public BoggleFields field;
	
	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
	
	public void getMode(PersistentBoggleInterface callable, String key, String defval,BoggleFields field){
		this.mode = GET_MODE;
		this.callable = callable;
		this.key = key;
		this.value = null;
		this.defval = defval;
		this.field = field;
	}
	
	public void setMode(PersistentBoggleInterface callable, String key, String value){
		this.mode = SET_MODE;
		this.callable = callable;
		this.key = key;
		this.value = value;
		
	}
	
	public void clearMode(String key){
		this.mode = CLEAR_MODE;
		this.key = key;
	}

	public String getMode() {
		return mode;
	}

	public void setMode(String mode) {
		this.mode = mode;
	}

	private String key;
	
	private String value;
	
	private PersistentBoggleInterface callable;
	
	public PersistentBoggleInterface getCallable() {
		return callable;
	}

	public void setCallable(PersistentBoggleInterface callable) {
		this.callable = callable;
	}

	private String defval;
	
	private String mode;

	public String getDefval() {
		return defval;
	}

	public void setDefval(String defval) {
		this.defval = defval;
	}

}
