package edu.neu.madcourse.adamgressen.alienevasion;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import android.content.Context;
import android.os.Environment;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;
import com.google.gson.Gson;

public class StoredEvasion {	
	// File name for the stored serialized class
	private static final String EVASION_FILE = "evasion.json";
	private static final String EVASION_PREFS = "Stored_Evasion";
	private static final String EVASION_CURRENT = "Current_Evasion";

	// Aliens evaded
	// Aliens in pursuit
	// Distance
	// Time
	// List of location overlays
	LinkedList<GeoPoint> locPositions = new LinkedList<GeoPoint>();
	// List of enemy overlays
	LinkedList<GeoPoint> enPositions = new LinkedList<GeoPoint>();
	// Timestamp of the saved Game
	String name;
	//Context
	Context context;
	//Evade
	Evade evade;

	// Constructors
	public StoredEvasion(){};
	public StoredEvasion(String name){
		this.name = name;
	}
	public StoredEvasion(Evade evade) {
		this.evade = evade;
		this.locPositions = evade.locPositions;
		this.enPositions = evade.enPositions;
		this.name = evade.startTime;
	}

	// Store this StoredEvasion in memory
	public void store(Context context) {
		try {
			context.getSharedPreferences(EVASION_PREFS, Context.MODE_PRIVATE)
			.edit()
			.putString(EVASION_CURRENT, name)
			.commit();
			/*
			File sdCard = Environment.getExternalStorageDirectory();
			File dir = new File (sdCard.getAbsolutePath() + "/AlienEvasion");
			dir.mkdirs();
			File file = new File(dir, name);
			
			FileWriter fos = new FileWriter(file);
			BufferedWriter output = new BufferedWriter(fos);
			*/
			File dir = context.getDir("AlienEvasion", Context.MODE_PRIVATE); //Creating an internal dir;
			File fileinDir = new File(dir, name); //Getting a file within the dir.
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
	
	public StoredEvasion read(Context context){

		StoredEvasion se = null;
		String savedEvasionName;
		
		try {
			if(this.evade != null){
				savedEvasionName = context.getSharedPreferences(EVASION_PREFS, Context.MODE_PRIVATE).getString(EVASION_CURRENT, "");
				System.out.println("Read Saved Game>" + savedEvasionName + "<");
				if (savedEvasionName == "")
					return se;
			}
			else
				savedEvasionName = this.name;

			File mydir = context.getDir("AlienEvasion", Context.MODE_PRIVATE); 
			File fileinMyDir = new File(mydir, name);
			FileReader fis = new FileReader(fileinMyDir);
			BufferedReader bf = new BufferedReader(fis);
			/*
			File sdCard = Environment.getExternalStorageDirectory();
			File dir = new File (sdCard.getAbsolutePath() + "/AlienEvasion");
			
			File file = new File(dir, savedEvasionName);
			FileReader fis = new FileReader(file);
			BufferedReader bf = new BufferedReader(fis);
			*/
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
		System.out.println("Read");
		
		return se;
	}
	
	
	
	public List<String> readAll(Context context) throws IOException{
		
		List<String> evasions = new LinkedList<String>();
		
		File sdCard = Environment.getExternalStorageDirectory();
		File dir = new File (sdCard.getAbsolutePath() + "/AlienEvasion");
		//sdcard support
		
			for(File file : dir.listFiles()){
				if(!file.isDirectory())
					evasions.add(file.getName());
			}
		

		
		return evasions;
		
	}
	
	public void finishEvasion(Context context){
		
		context.getSharedPreferences(EVASION_PREFS, Context.MODE_PRIVATE).edit().clear().commit();
		
		String current = context.getSharedPreferences(EVASION_PREFS, Context.MODE_PRIVATE).getString(EVASION_CURRENT, "");
		Toast.makeText(context, current, Toast.LENGTH_SHORT).show();
		System.out.println("Saved Game>" + current + "<");
		
	}
	
	public String getSavedEvasionName(Context context){
		return context.getSharedPreferences(EVASION_PREFS, Context.MODE_PRIVATE).getString(EVASION_CURRENT, "");
	}

	
}