package edu.neu.madcourse.adamgressen.alienevasion;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Serializable;
import java.io.Writer;
import java.util.LinkedList;
import java.util.List;

import com.google.gson.Gson;

import android.content.Context;
import android.os.Environment;

public class StoredEvasion {

	// Default serial ID
	private static final long serialVersionUID = 1L;
	
	// File name for the stored serialized class
	private static final String EVASION_FILE= "evasion.txt";
	private static final String EVASION_PREFS = "Stored_Evasion";
	private static final String EVASION_CURRENT = "Current_Evasion";

	// List of location overlays
	List<LocationOverlay> locOverlays;
	// List of enemy overlays
	List<EnemyOverlay> enOverlays;
	// Timestamp of the saved Game
	String name;

	// Constructor
	public StoredEvasion(Evade evade) {
		this.name = evade.startTime;
		this.locOverlays = evade.locOverlays;
		this.enOverlays = evade.enOverlays;
	}
	
	public StoredEvasion(){};

	// Store this StoredEvasion in memory
	public void store(Context context) {
		try{
			context.getSharedPreferences(EVASION_PREFS, Context.MODE_PRIVATE)
			.edit()
			.putString(EVASION_CURRENT, name)
			.commit();
			File sdCard = Environment.getExternalStorageDirectory();
			File dir = new File (sdCard.getAbsolutePath() + "/AlienEvasion");
			dir.mkdirs();
			File file = new File(dir, EVASION_FILE);

			FileWriter fos = new FileWriter(file);
			
			BufferedWriter output = new BufferedWriter(fos);
			
			Gson gson = new Gson();
			String json = gson.toJson(this);
			
			
			try{
				output.write(json);
			}
			finally{
				output.close();
			}
		}  
		catch(IOException ex){
			System.out.println("Unable to store Evasion");
		}
	}
	
	public StoredEvasion read(Context context){

		StoredEvasion se = null;
		
		try{
			
			File sdCard = Environment.getExternalStorageDirectory();
			File dir = new File (sdCard.getAbsolutePath() + "/AlienEvasion");
			
			File file = new File(dir, EVASION_FILE + name);
			FileReader fis = new FileReader(file);
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
		
		return se;
	}
	
	public List<String> readAll(Context context){
		
		List<String> evasions = new LinkedList<String>();
		File sdCard = Environment.getExternalStorageDirectory();
		File dir = new File (sdCard.getAbsolutePath() + "/AlienEvasion");
		
		for(File file : dir.listFiles()){
			if(!file.isDirectory())
				evasions.add(file.getName());
		}
		
		
		return evasions;
		
	}
	
	public void finishEvasion(Context context){
		
		context.getSharedPreferences(EVASION_PREFS, Context.MODE_PRIVATE)
		.edit()
		.putString(EVASION_CURRENT, "")
		.commit();
		
	}

	
}