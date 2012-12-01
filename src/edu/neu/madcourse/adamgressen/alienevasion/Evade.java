package edu.neu.madcourse.adamgressen.alienevasion;

import java.util.LinkedList;
import java.util.List;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import edu.neu.madcourse.adamgressen.R;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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
	// Map Controller
	MapController mc;

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
	private int pursuing;
	public int getPursuing() { return pursuing; }

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.alien_evasion_evade);

		locOverlays = new LinkedList<LocationOverlay>();
		enOverlays = new LinkedList<EnemyOverlay>();

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

	private void initMap() {
		mapView = (MapView) findViewById(R.id.mapview);
		//mapView.setBuiltInZoomControls(true); // Enables zoom controls

		mc = mapView.getController();
		mc.setZoom(18);

		locMan.initMyLocation();

		mc.animateTo(p);

		// Check for GPS
		gpsMan.checkForGPS();
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
		// Invalidate the map so it's redrawn
		mapView.invalidate();
	}

	// Add enemy overlays to the list
	public void addEnemies() {
		// Create enemyoverlay and add to list
		for (int e = 0; e < 5; e++) {
			enOverlays.add(new EnemyOverlay(p));
		}
	}

	// Updates enemy overlays to new positions
	private void updateEnemyOverlays() {
		for (EnemyOverlay e : enOverlays) {
			e.p = p;
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
		new StoredEvasion(this).store();
	}
}