package edu.neu.madcourse.adamgressen;

import java.util.List;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;
import com.google.android.maps.Overlay;
import android.location.GpsStatus;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Toast;

public class Tricky extends MapActivity implements LocationListener, GpsStatus.Listener  {
	MapView mapView;
	MyLocationOverlay myLocOverlay;
	MapController mc;
	LocationManager lm;
	GeoPoint p;
	Location lastLoc;
	Long lastLocTime;
	Long beginCheckTime;
	ProgressDialog pro;

	static final int TIME_DELAY = 10000;
	static final int DIST_DELAY = 20;
	static final int TIMEOUT_DELAY = 30000;

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.trickiest);

		mapView = (MapView) findViewById(R.id.mapview);
		//mapView.setBuiltInZoomControls(true); // Enables zoom controls

		pro = new ProgressDialog(this);
		pro.setMessage("Acquiring GPS Signal");

		lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);

		initMyLocation();

		checkForGPS();
	}

	@Override
	public void onPause() {
		super.onPause();

		lm.removeUpdates(this);
	}

	@Override
	public void onResume() {
		super.onResume();

		lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, TIME_DELAY, DIST_DELAY, this);
	}

	private void initMyLocation() {
		mc = mapView.getController();
		mc.setZoom(18);
		double lat, lng;
		// Check for last known location
		Location lastKnownLoc = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		//if (lastKnownLoc == null)
		//	lastKnownLoc = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
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

		p = new GeoPoint(
				(int) (lat * 1E6), 
				(int) (lng * 1E6));

		mc.animateTo(p);
	}

	public void onLocationChanged(Location location) {
		Log.d("Tricky","location changed");

		pro.cancel();

		// Handle a null location
		if (location == null) return;

		// Store location and time
		lastLocTime = SystemClock.elapsedRealtime();
		lastLoc = location;

		// Set GeoPoint
		p = new GeoPoint(
				(int) (location.getLatitude() * 1E6), 
				(int) (location.getLongitude() * 1E6));

		handleNewLocation();
	}

	// Moves to a new location and adds an overlay
	public void handleNewLocation() {
		mc.animateTo(p);
		//---Add a location marker---
		LocationOverlay mapOverlay = new LocationOverlay(p, mapView.getOverlays().size());
		List<Overlay> listOfOverlays = mapView.getOverlays();
		listOfOverlays.add(mapOverlay);
		mapView.invalidate();
	}

	public void onGpsStatusChanged(int event) {
		switch (event) {
		case GpsStatus.GPS_EVENT_SATELLITE_STATUS:
			
			// If there's a previous location and the time is outside of the timeout region
			if (lastLoc != null &&
			(SystemClock.elapsedRealtime() - lastLocTime > TIMEOUT_DELAY))
				Toast.makeText(this,
						"The aliens are blocking your transmissions.",
						Toast.LENGTH_SHORT).show();
			
			// If there is no previous time and we're within the acceptable range
			else if (SystemClock.elapsedRealtime() - beginCheckTime < TIMEOUT_DELAY)
				System.out.println("Waiting for a GPS fix");
			
			// Otherwise timeout
			else
				GPSTimeout();

			break;

		case GpsStatus.GPS_EVENT_FIRST_FIX:
			pro.hide();
			//Toast.makeText(this, "First GPS fix", Toast.LENGTH_LONG).show();
			break;
		}
	}

	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub

	}

	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub

	}

	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub

	}

	public void checkForGPS() {
		pro.show();
		beginCheckTime = SystemClock.elapsedRealtime();
		lm.addGpsStatusListener(this);
		lm.requestLocationUpdates(
				LocationManager.GPS_PROVIDER,
				TIME_DELAY,
				DIST_DELAY, 
				this);
	}

	public void GPSTimeout() {
		//pro.setMessage("Acquiring position from Network");
		System.out.println("GPS timed out");
		pro.hide();
		// Stop checking for location
		lm.removeGpsStatusListener(this);
		lm.removeUpdates(this);

		// Ask for another attempt
		new AlertDialog.Builder(this)
		.setTitle("Trouble Finding Location")
		.setMessage("The government agents can't locate you right now.\nTry again?")
		.setCancelable(true)
		.setPositiveButton("Yes", new DialogInterface.OnClickListener() {			
			public void onClick(DialogInterface dialog, int id) {
				// Start checking again
				checkForGPS();
				dialog.dismiss();
			}
		})
		.setNegativeButton("No", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		}).show();

		//System.out.println("Switching to Network locator.");
		//lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, TIME_DELAY, DIST_DELAY, this);
	}
}