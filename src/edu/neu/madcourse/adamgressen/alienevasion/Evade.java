package edu.neu.madcourse.adamgressen.alienevasion;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.Projection;

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
	// Map overlays
	List<Overlay> mapOverlays;
	// List of location positions
	static LinkedList<GeoPoint> locPositions;
	// List of location overlays
	LinkedList<LocationOverlay> locOverlays;
	// List of enemy positions
	LinkedList<GeoPoint> enPositions;
	// List of enemy overlays
	LinkedList<EnemyOverlay> enOverlays;
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
	private static double distance;
	public static String getDist() {
		double d = ((double)((int)(distance*100.0)))/100.0;
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

		System.out.println("onCreate Evade");

		StoredEvasion sevasion = getEvasion();

		if(sevasion != null){
			// Get stored location positions
			locPositions = sevasion.locPositions;
			// Current position equals the last stored position
			p = locPositions.getLast();
			
			// Populate location overlays
			for (int gp = 0; gp < locPositions.size(); gp++) {
				locOverlays.add(new LocationOverlay(locPositions.get(gp), gp));
			}
			
			// Get stored enemy positions
			enPositions = sevasion.enPositions;
			
			// Populate enemy overlays
			for (GeoPoint gp : enPositions) {
				enOverlays.add(new EnemyOverlay(gp));
			}
			
			saved = true;
			
			System.out.println("Used stored evasion");
			setToast("Using stored evasion");
		}
		else{
			locPositions = new LinkedList<GeoPoint>();
			locOverlays = new LinkedList<LocationOverlay>();
			enPositions = new LinkedList<GeoPoint>();
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

		System.out.println("onPause Evade");

		accMan.pause();
		locMan.pause();
		gpsMan.pause();

		// Store the current evasion
		storeEvasion();
	}

	@Override
	public void onResume() {
		super.onResume();

		System.out.println("onResume Evade");

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

		mapOverlays = mapView.getOverlays();

		mc = mapView.getController();
		mc.setZoom(18);

		if(!saved){
			locMan.initMyLocation();
			mc.animateTo(p);
			// Check for GPS
			gpsMan.checkForGPS();
		}
		else{
			mapOverlays.clear();
			mapOverlays.addAll(locOverlays);
			mapOverlays.addAll(enOverlays);
		}
	}

	// Moves to a new location and adds an overlay
	public void handleNewLocation() {
		mc.animateTo(p);
		distance = locMan.calculateDistance();
		// Add position to list
		locPositions.add(p);
		//---Add a location marker---
		LocationOverlay mapOverlay = new LocationOverlay(p, locPositions.size()-1);
		locOverlays.add(mapOverlay);
		// Clear the map's overlays
		mapOverlays.clear();
		// Add the new list of location overlays
		mapOverlays.addAll(locOverlays);
		// Update enemy overlays
		updateEnemyOverlays();
		mapOverlays.addAll(enOverlays);
		// Invalidate the map so it's redrawn
		mapView.invalidate();
	}

	// Add enemy overlays to the list
	public void addEnemies() {
		randomizePos();
		pursuing = 5;
		// Create enemyoverlay and add to list
		for (int e = 0; e < 5; e++) {
			GeoPoint newEnPos = randomizePos();
			enPositions.add(newEnPos);
			enOverlays.add(new EnemyOverlay(newEnPos));
		}
	}

	// Updates enemy overlays to new positions
	private void updateEnemyOverlays() {
		for (int g = 0; g < enPositions.size(); g++) {
			enOverlays.get(g).enemyPos = randomizePos();
		}
	}

	// Randomizes position
	private GeoPoint randomizePos() {
		alienRadius = new LinkedList<Double>();
		Random rand = new Random();
		double t;

		t = 2*Math.PI*rand.nextDouble();

		// Convert lat and long to screen coordinates
		Point playerPoint = new Point();
		Projection proj = mapView.getProjection();
		proj.toPixels(p, playerPoint);

		int prevx = playerPoint.x;
		int prevy = playerPoint.y;
		double r = 100;

		Point newEnPoint = new Point((int)(prevx+ r*Math.sin(t)), (int)(prevy + r*Math.cos(t)));

		return proj.fromPixels(newEnPoint.x, newEnPoint.y);
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