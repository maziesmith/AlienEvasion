package edu.neu.madcourse.adamgressen.alienevasion;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;

import com.google.android.maps.GeoPoint;
import com.google.gson.Gson;

import android.content.Context;
import android.os.Environment;

public class StoredEvasion {	
	// File name for the stored serialized class
	private static final String EVASION_FILE = "evasion.json";

	// Aliens evaded
	// Aliens in pursuit
	// Distance
	// Time
	// List of location overlays
	LinkedList<GeoPoint> locPositions = new LinkedList<GeoPoint>();
	// List of enemy overlays
	LinkedList<GeoPoint> enPositions = new LinkedList<GeoPoint>();

	// Constructors
	public StoredEvasion(){};
	public StoredEvasion(Evade evade) {
		this.locPositions = Evade.locPositions;
		this.enPositions = evade.enPositions;
	}

	// Store this StoredEvasion in memory
	public void store(Context context) {
		try {
			
			File sdCard = Environment.getExternalStorageDirectory();
			File dir = new File (sdCard.getAbsolutePath() + "/AlienEvasion");
			dir.mkdirs();
			File file = new File(dir, EVASION_FILE);

			FileWriter fos = new FileWriter(file);
			
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
	
	public static StoredEvasion read(Context context) {

		StoredEvasion se = null;
		
		try {
			File sdCard = Environment.getExternalStorageDirectory();
			File dir = new File (sdCard.getAbsolutePath() + "/AlienEvasion");
			
			File file = new File(dir, EVASION_FILE);
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
	
	public boolean removeEvasion(Context context) {
		
		return context.deleteFile(EVASION_FILE);
		
	}
}