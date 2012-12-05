package edu.neu.madcourse.adamgressen.alienevasion;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;

import edu.neu.madcourse.adamgressen.R;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.graphics.Point;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.widget.Toast;

/***
 * 
 * The Evade class handles the GPS, Accelerometer and Locations
 * while displaying everything on a Map with help from Google
 * 
 * **/
public class Evade extends MapActivity  {	
	// Map View
	MapView mapView;
	// List of location overlays
	List<LocationOverlay> locOverlays;
	// List of enemy overlays
	List<EnemyOverlay> enOverlays;
	// List of Random numbers for Aliens
	List<Double> alienRadius;
	// Map Controller
	MapController mc;
	// boolean for saved maps
	boolean saved = false;

	// Current location
	GeoPoint p;

	// Managers
	EvadeLocationManager locMan;
	GPSManager gpsMan;
	AccelerometerManager accMan;

	// Distance traveled
	private double distance;
	public String getDist() {
		double d = ((double)((int)(this.distance*100.0)))/100.0;
		return String.valueOf(d);
	}
	// Elapsed time
	private int time;
	public int getTime() { return time; }
	// Aliens evaded
	private int evaded;
	public int getEvaded() { return evaded; }
	// Aliens in pursuit
	private static int pursuing;
	public static int getPursuing() { return pursuing; }

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.alien_evasion_evade);

		StoredEvasion sevasion = getEvasion();
		
		if(sevasion!=null){
			locOverlays = sevasion.locOverlays;
			enOverlays = sevasion.enOverlays;
			saved = true;
			System.out.println("Used stored evasion");
			setToast("Using stored evasion");
		}
		else{
		locOverlays = new LinkedList<LocationOverlay>();
		enOverlays = new LinkedList<EnemyOverlay>();
		setToast("New Locations used");
		}

		locMan = new EvadeLocationManager(this);
		gpsMan = new GPSManager(this);
		accMan = new AccelerometerManager(this);

		// If there's no network then the map won't display
		if (!isNetworkAvailable()) {
			// Ask for another attempt
			new AlertDialog.Builder(this)
			.setTitle("No Network Available")
			.setMessage("You will not be able to view the map, but you can still track your movement and load the map later.")
			.setCancelable(true)
			.setPositiveButton("OK", new DialogInterface.OnClickListener() {			
				public void onClick(DialogInterface dialog, int id) {
					dialog.dismiss();
				}
			}).show();
		}

		// Initialize the map
		initMap();
	}

	@Override
	public void onPause() {
		super.onPause();

		accMan.pause();
		locMan.pause();

		// Store the current evasion
		storeEvasion();
	}

	@Override
	public void onResume() {
		super.onResume();

		accMan.resume();
		locMan.resume();
		gpsMan.resume();
	}
	
	@Override
	public void onBackPressed() {
	    new AlertDialog.Builder(this)
	        .setTitle("Really Exit?")
	        .setMessage("Are you sure you want to exit?")
	        .setNegativeButton(android.R.string.no, null)
	        .setPositiveButton(android.R.string.yes, new OnClickListener() {

	            public void onClick(DialogInterface arg0, int arg1) {
	            	boolean bool = deleteEvasion();
	            	if(bool)
	            	setToast("File Deleted");
	                Evade.super.onBackPressed();
	            }
	        }).create().show();
	}

	private void initMap() {
		mapView = (MapView) findViewById(R.id.mapview);
		//mapView.setBuiltInZoomControls(true); // Enables zoom controls

		mc = mapView.getController();
		mc.setZoom(18);

		if(!saved){
			locMan.initMyLocation();
			mc.animateTo(p);
		// Check for GPS
		gpsMan.checkForGPS();
		}
		else{
			mapView.getOverlays().clear();
			mapView.getOverlays().addAll(locOverlays);
			mapView.getOverlays().addAll(enOverlays);
		}
	}

	// Moves to a new location and adds an overlay
	public void handleNewLocation() {
		mc.animateTo(p);
		distance = locMan.calculateDistance();
		//---Add a location marker---
		LocationOverlay mapOverlay = new LocationOverlay(p, locOverlays.size(), getDist());
		locOverlays.add(mapOverlay);
		// Clear the map's overlays
		mapView.getOverlays().clear();
		// Add the new list of location overlays
		mapView.getOverlays().addAll(locOverlays);
		// Update enemy overlays
		updateEnemyOverlays();
		mapView.getOverlays().addAll(enOverlays);
		// Invalidate the map so it's redrawn
		mapView.invalidate();
	}

	// Add enemy overlays to the list
	public void addEnemies() {
		randomizePos();
		pursuing = 5;
		// Create enemyoverlay and add to list
		for (int e = 0; e < 5; e++) {
			enOverlays.add(new EnemyOverlay(p,alienRadius.get(e)));
		}
	}

	// Updates enemy overlays to new positions
	private void updateEnemyOverlays() {
		for (EnemyOverlay e : enOverlays) {
			e.p = p;
		}
	}
	
	// Randomizes position
	private void randomizePos() {
		alienRadius = new LinkedList<Double>();
		Random rand = new Random();
		double t;

		for(int i=0;i < 5; i++){
			t = rand.nextDouble();
			alienRadius.add(t);
		}
		
	}

	// Check if network is available
	private boolean isNetworkAvailable() {
		ConnectivityManager connectivityManager 
		= (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
		return activeNetworkInfo != null;
	}

	// Display a toast
	public void setToast(String string) {
		Toast.makeText(this, string, Toast.LENGTH_SHORT).show();
	}

	// Store this evasion in memory
	private void storeEvasion() {
		// Create a new StoredEvasion and store it
		new StoredEvasion(this).store(getApplicationContext());
	}
	
	// Get any saved evasion from memory
	private StoredEvasion getEvasion(){
		 return StoredEvasion.read(this);
	}
	
	private boolean deleteEvasion(){
		return new StoredEvasion().removeEvasion(getApplicationContext());
	}
}