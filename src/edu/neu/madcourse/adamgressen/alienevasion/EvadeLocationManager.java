package edu.neu.madcourse.adamgressen.alienevasion;

import com.google.android.maps.GeoPoint;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.SystemClock;

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

	// Calculate the total distance traveled
	public double calculateDistance() {
		double dist = 0.0;
		GeoPoint prevPoint = null;
		GeoPoint curPoint = null;
		float[] results = new float[3];

		// Add up all locations in list
		for (LocationOverlay o : evade.locOverlays) {
			if (prevPoint == null)
				prevPoint = o.getPoint();
			else {
				curPoint = o.getPoint();

				// Get the distance between the geo points
				Location.distanceBetween(
						prevPoint.getLatitudeE6()/1E6, 
						prevPoint.getLongitudeE6()/1E6,
						curPoint.getLatitudeE6()/1E6,
						curPoint.getLongitudeE6()/1E6,
						results);
				dist += (double)results[0]/1609.34;
				prevPoint = curPoint;
			}
		}

		return dist;
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
		lm.removeUpdates(this);
	}

	public void resume() {
		// Remove last location
		lastLoc = null;

		// Request location updates again
		lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, TIME_DELAY, DIST_DELAY, this);
	}

	public void onProviderDisabled(String arg0) {}

	public void onProviderEnabled(String provider) {}

	public void onStatusChanged(String provider, int status, Bundle extras) {}
}
