package edu.neu.madcourse.adamgressen.alienevasion;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.List;

public class StoredEvasion implements Serializable {

	// Default serial ID
	private static final long serialVersionUID = 1L;

	// List of location overlays
	List<LocationOverlay> locOverlays;
	// List of enemy overlays
	List<EnemyOverlay> enOverlays;

	// Constructor
	public StoredEvasion(Evade evade) {
		this.locOverlays = evade.locOverlays;
		this.enOverlays = evade.enOverlays;
	}

	// Store this StoredEvasion in memory
	public void store() {
		try{
			ObjectOutput output = new ObjectOutputStream(
					new BufferedOutputStream(
							new FileOutputStream("evasion.ser")));
			try{
				output.writeObject(this);
			}
			finally{
				output.close();
			}
		}  
		catch(IOException ex){
			System.out.println("Unable to store Evasion");
		}
	}
}