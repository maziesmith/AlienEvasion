package edu.neu.madcourse.adamgressen.alienevasion;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.graphics.Point;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.Projection;

import edu.neu.madcourse.adamgressen.R;

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
	LinkedList<GeoPoint> locPositions;
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
	// boolean for finishing
	boolean finished = false;
	// Timestamp of start of the game
	String startTime;
	// StoredEvasion Object
	StoredEvasion sevasion;

	// Current location
	GeoPoint p;

	// Managers
	EvadeLocationManager locMan;
	GPSManager gpsMan;
	AccelerometerManager accMan;

	// Average distance over time -- in mph
	int avgSpd;
	// Time between finding locations -- in seconds
	long timePassed;
	
	// Time interval for feedback -- in seconds
	final int TIME_INTERVAL = 20;

	// Distance during interval -- in miles
	private double intDist;
	public double getIntDist() {
		return intDist;
	}
	public void setIntDist(double dist) {
		intDist = dist;
	}
	// Distance traveled -- in miles
	private double distance;
	public String getDist() {
		double d = ((int)(distance*100.0))/100.0;
		return String.valueOf(d);
	}
	// Timer
	Timer timer;
	// Delay between timer ticks
	int TIMER_TICK = 1000;
	// Elapsed time -- in seconds
	private long time;
	public long getTime() { return time; }
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
		
		timer = new Timer();
		timePassed = 0;

		class Updater extends TimerTask{
			public void run() {
				// Increment the stored game time
				time++;
				// Increment the timePassed
				timePassed++;
				
				// Check if we've hit the time interval
				if (time % TIME_INTERVAL == 0) {
					// provide audio feedback
				}
			}
		}
		TimerTask updateTask = new Updater();
		timer.schedule(updateTask, 0, TIMER_TICK);

		Calendar cal = Calendar.getInstance();
		SimpleDateFormat dateFormat = new SimpleDateFormat("MMM-dd-yyyy@h@mm@ss@a");
		startTime = dateFormat.format(cal.getTime());

		sevasion = getEvasion();

		locPositions = new LinkedList<GeoPoint>();
		locOverlays = new LinkedList<LocationOverlay>();
		enPositions = new LinkedList<GeoPoint>();
		enOverlays = new LinkedList<EnemyOverlay>();

		if(sevasion != null){
			// Get stored location positions
			locPositions = sevasion.locPositions;
			// Current position equals the last stored position
			p = locPositions.getLast();

			// Populate location overlays
			for (int gp = 0; gp < locPositions.size(); gp++) {
				locOverlays.add(new LocationOverlay(this, locPositions.get(gp), gp));
			}

			// Get stored enemy positions
			enPositions = sevasion.enPositions;

			// Populate enemy overlays
			for (GeoPoint gp : enPositions) {
				enOverlays.add(new EnemyOverlay(this, gp));
			}

			saved = true;

			System.out.println("Used stored evasion");
			setToast("Using stored evasion");
		}
		else{

			setToast("New Locations used");
		}


		locMan = new EvadeLocationManager(this);
		gpsMan = new GPSManager(this);
		accMan = new AccelerometerManager(this);

		checkNetworkAvailability();

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
		if(!finished)
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
		.setMessage("Are you sure you want to exit? This will end your current run!")
		.setNegativeButton(android.R.string.no, null)
		.setPositiveButton(android.R.string.yes, new OnClickListener() {

	            public void onClick(DialogInterface arg0, int arg1) {
	            	deleteEvasion();
	            	finished = true;
	            	finish();
	                Evade.super.onBackPressed();
	                return;
	            }
	        }).create().show();
	}

	private void initMap() {
		mapView = (MapView) findViewById(R.id.mapview);
		//mapView.setBuiltInZoomControls(true); // Enables zoom controls

		mapOverlays = mapView.getOverlays();

		mc = mapView.getController();
		mc.setZoom(18);

		locMan.initMyLocation();
		mc.animateTo(p);
		// Check for GPS
		gpsMan.checkForGPS();

		/*
		if(saved){
			mapOverlays.clear();
			mapOverlays.addAll(locOverlays);
			mapOverlays.addAll(enOverlays);
		}
		 */
	}

	// Update the total distance traveled
	public void updateDistance() {
		// If there are previous locations
		if (!locPositions.isEmpty()) {
			// Get previous location
			GeoPoint prevPoint = locPositions.get(locPositions.size()-1);

			// Create results array
			float[] results = new float[3];
			// Get the distance between the geo points
			Location.distanceBetween(
					prevPoint.getLatitudeE6()/1E6, 
					prevPoint.getLongitudeE6()/1E6,
					p.getLatitudeE6()/1E6,
					p.getLongitudeE6()/1E6,
					results);

			// Get the distance value we care about
			double dist = results[0] / 1609.34;

			// Add dist to interval and total distance values
			intDist += dist;
			distance += dist;
		}
	}

	// Moves to a new location and adds an overlay
	public void handleNewLocation() {
		mc.animateTo(p);
		updateDistance();
		
		// calculate current speed -- in mph
		double curSpd = intDist * timePassed / 360;
		
		// calculate the score -- the number of evaded aliens
		evaded += curSpd;
		
		// Reset intDist to 0
		intDist = 0;
		
		// Add position to list
		locPositions.add(p);
		//---Add a location marker---
		LocationOverlay mapOverlay = new LocationOverlay(this, p, locPositions.size()-1);
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
			enOverlays.add(new EnemyOverlay(this, newEnPos));
		}
	}

	// Updates enemy overlays to new positions
	// This assumes that there is more than one GeoPoint in locPositions
	private void updateEnemyOverlays() {
		GeoPoint newEnPos = new GeoPoint(
				p.getLatitudeE6()-locPositions.get(locPositions.size()-2).getLatitudeE6(),
				p.getLongitudeE6()-locPositions.get(locPositions.size()-2).getLatitudeE6());
		
		for (int g = 0; g < enPositions.size(); g++) {
			enOverlays.get(g).enemyPos = newEnPos;
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
	
	@Override
    protected void onDestroy() {
        super.onStop();
    }

	// Show an alert if the network is unavailable
	private void checkNetworkAvailability() {
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
		new StoredEvasion(this).store(this);
	}

	// Get any saved evasion from memory
	private StoredEvasion getEvasion(){
		return new StoredEvasion(this).read(this);
	}

	private void deleteEvasion(){
		new StoredEvasion(this).finishEvasion(this);
	}
}