package edu.neu.madcourse.adamgressen.alienevasion;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import android.content.Context;
import com.google.android.maps.GeoPoint;
import com.google.gson.Gson;

public class StoredEvasion {	
	// File name for the stored serialized class
	private static final String EVASION_PREFS = "Stored_Evasion";
	private static final String EVASION_CURRENT = "Current_Evasion";
	
	//private static final String 

	// List of location positions
	LinkedList<GeoPoint> locPositions; 
	// List of enemy positions
	LinkedList<GeoPoint> enPositions;
	// Distance
	double totalDist;
	// Evaded
	int totalEvaded;
	// Time
	double totalTime;

	// Timestamp of the saved Game
	String name;
	//Context
	Context context;

	// Constructors
	public StoredEvasion(){};
	public StoredEvasion(String name){
		this.name = name;
	}
	public StoredEvasion(Evade evade) {
		this.locPositions = evade.getLocPositions();
		this.enPositions = evade.enPositions;
		this.name = evade.startTime;
		this.totalDist = evade.getDist();
		this.totalEvaded = evade.getEvaded();
		this.totalTime = evade.getTime();
	}

	// Store this StoredEvasion in memory
	public void store(Context context) {
		try {
			context.getSharedPreferences(EVASION_PREFS, Context.MODE_PRIVATE)
			.edit()
			.putString(EVASION_CURRENT, name)
			.commit();

			File dir = context.getDir("AlienEvasion", Context.MODE_PRIVATE); 
			File fileinDir = new File(dir, name); 
			FileWriter fos = new FileWriter(fileinDir);
			BufferedWriter output = new BufferedWriter(fos);

			Gson gson = new Gson();
			String json = gson.toJson(this);

			try {
				output.write(json);
			}
			finally {
				output.close();
			}
		}
		catch(IOException ex) {
			System.out.println("Unable to store Evasion");
		}
	}

	// Read a stored evasion
	public StoredEvasion read(Context context){
		StoredEvasion se = null;
		String savedEvasionName;

		try {
			if(locPositions!=null && enPositions != null){

				savedEvasionName = context.getSharedPreferences(EVASION_PREFS, Context.MODE_PRIVATE).getString(EVASION_CURRENT, "");
				System.out.println("Read Saved Game>" + savedEvasionName + "<");
				if (savedEvasionName == "")
					return se;
				else
					savedEvasionName = this.name;
			}

			File dir = context.getDir("AlienEvasion", Context.MODE_PRIVATE); 
			File fileinDir = new File(dir, name);
			FileReader fis = new FileReader(fileinDir);
			BufferedReader bf = new BufferedReader(fis);

			String read = "";
			String json ="";

			try {
				while((read=bf.readLine()) !=null){
					json = read;
					System.out.println("Saved JSON object: " +json);
				}

				if(json !=null || json!="null"){
					Gson gson = new Gson();
					se = gson.fromJson(json, StoredEvasion.class);
				}
			}
			finally{
				bf.close();
			}
		}
		catch(IOException e){
			System.out.println("Unable to read the Evasion");
		}
		System.out.println("Read the internal storage");

		return se;
	}

	// Read all evasions from preferences
	public List<String> readAll(Context context) throws IOException{

		List<String> evasions = new LinkedList<String>();
		File dir = context.getDir("AlienEvasion", Context.MODE_PRIVATE); 

		try{
			for(File file : dir.listFiles()){
				if(!file.isDirectory())
					evasions.add(file.getName());
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}

		return evasions;
	}

	// When an evasion is finished
	// Store the current evasion in preferences
	public void finishEvasion(Context context){
		context.getSharedPreferences(EVASION_PREFS, Context.MODE_PRIVATE).edit().clear().commit();

		String current = context.getSharedPreferences(EVASION_PREFS, Context.MODE_PRIVATE).getString(EVASION_CURRENT, "");
		System.out.println("Saved Game>" + current + "<");
	}

	// Return the name of the currently stored evasion
	public String getSavedEvasionName(Context context){
		return context.getSharedPreferences(EVASION_PREFS, Context.MODE_PRIVATE).getString(EVASION_CURRENT, "");
	}
}