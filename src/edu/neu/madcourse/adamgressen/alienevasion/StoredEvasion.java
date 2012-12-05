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
import java.util.List;

import com.google.gson.Gson;

import android.content.Context;
import android.os.Environment;

public class StoredEvasion {

	// Default serial ID
	private static final long serialVersionUID = 1L;
	
	// File name for the stored serialized class
	private static final String EVASION_FILE= "evasion.txt";

	// List of location overlays
	List<LocationOverlay> locOverlays;
	// List of enemy overlays
	List<EnemyOverlay> enOverlays;

	// Constructor
	public StoredEvasion(Evade evade) {
		this.locOverlays = evade.locOverlays;
		this.enOverlays = evade.enOverlays;
	}
	
	public StoredEvasion(){};

	// Store this StoredEvasion in memory
	public void store(Context context) {
		try{
			
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
	
	public static StoredEvasion read(Context context){

		StoredEvasion se = null;
		
		try{
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
	
	public boolean removeEvasion(Context context){
		
		return context.deleteFile(EVASION_FILE);
		
	}

	
}