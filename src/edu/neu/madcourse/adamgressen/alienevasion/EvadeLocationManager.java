package edu.neu.madcourse.adamgressen.alienevasion;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.SystemClock;

import com.google.android.maps.GeoPoint;

public class EvadeLocationManager implements LocationListener {
	static final int TIME_DELAY = 10000;
	static final int DIST_DELAY = 20;

	Evade evade;

	// Location Manager
	LocationManager lm;
	// Last received location
	Location lastLoc;
	// Last time location was received
	Long lastLocTime;

	// Constructor
	public EvadeLocationManager() {}
	public EvadeLocationManager(Evade evade) {
		this.evade = evade;

		lm = (LocationManager)evade.getSystemService(Context.LOCATION_SERVICE);
	}

	// When the location changes
	public void onLocationChanged(Location location) {
		evade.gpsMan.pro.hide();

		// Makes it possible to show block message again
		evade.gpsMan.blockMessageShown = false;

		// Handle a null location
		if (location == null) return;

		// Store location and time
		lastLocTime = SystemClock.elapsedRealtime();
		lastLoc = location;

		// Set GeoPoint
		evade.p = new GeoPoint(
				(int) (location.getLatitude() * 1E6), 
				(int) (location.getLongitude() * 1E6));

		evade.handleNewLocation();
	}

	// Initialize the location
	public void initMyLocation() {
		double lat, lng;
		// Check for last known location
		Location lastKnownLoc = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		if(evade.saved){
			LocationOverlay lastOverlay = evade.locOverlays.get(evade.locOverlays.size()-1);			
			evade.p = lastOverlay.p;
		}
		else{

			if (lastKnownLoc != null) {
				lat = lastKnownLoc.getLatitude();
				lng = lastKnownLoc.getLongitude();
			}
			else {
				// Set default latitude and longitude
				String coordinates[] = {"42.348332", "-71.087873"};
				lat = Double.parseDouble(coordinates[0]);
				lng = Double.parseDouble(coordinates[1]);
			}

			evade.p = new GeoPoint(
					(int) (lat * 1E6), 
					(int) (lng * 1E6));

			// Add enemies
			evade.addEnemies();
		}
	}

	public void beginLocationUpdating() {
		lm.addGpsStatusListener(evade.gpsMan);
		lm.requestLocationUpdates(
				LocationManager.GPS_PROVIDER,
				EvadeLocationManager.TIME_DELAY,
				EvadeLocationManager.DIST_DELAY, 
				this);
	}

	public void stopLocationUpdating() {
		// Stop checking for location
		lm.removeGpsStatusListener(evade.gpsMan);
		lm.removeUpdates(this);
	}

	public void pause() {
		stopLocationUpdating();
	}

	public void resume() {
		// Remove last location
		lastLoc = null;

		// Request location updates again
		beginLocationUpdating();
	}
	
	// Checks if GPS is available
	public static boolean isGPSAvailable(Context context) {
		LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000L, 1.0f, new EvadeLocationManager());
		return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
	}

	public void onProviderDisabled(String arg0) {}

	public void onProviderEnabled(String provider) {}

	public void onStatusChanged(String provider, int status, Bundle extras) {}
}
