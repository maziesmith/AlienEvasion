package edu.neu.madcourse.adamgressen;

import java.util.List;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;
import com.google.android.maps.Overlay;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.FloatMath;
import android.location.GpsStatus;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Toast;

public class Tricky extends MapActivity implements LocationListener, GpsStatus.Listener, SensorEventListener  {
	MapView mapView;
	MyLocationOverlay myLocOverlay;
	MapController mc;
	LocationManager lm;
	GeoPoint p;
	Location lastLoc;
	Long lastLocTime;
	Long beginCheckTime;
	ProgressDialog pro;
	double distance;
	public String getDist() {
		double d = ((double)((int)(this.distance*100.0)))/100.0;
		return String.valueOf(d);
	}

	private SensorManager mSensorManager;
	private float mAccel; // acceleration apart from gravity
	private float mAccelCurrent; // current acceleration including gravity
	private float mAccelLast; // last acceleration including gravity

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

		mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
		mAccel = 0.00f;
		mAccelCurrent = SensorManager.GRAVITY_EARTH;
		mAccelLast = SensorManager.GRAVITY_EARTH;

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

		mSensorManager.unregisterListener(this);

		lm.removeUpdates(this);
	}

	@Override
	public void onResume() {
		super.onResume();

		mSensorManager.registerListener(this, 
				mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
				SensorManager.SENSOR_DELAY_NORMAL);

		// Remove last location
		lastLoc = null;
		// Reset begin check time variable
		beginCheckTime = SystemClock.elapsedRealtime();

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
		this.distance = this.calculateDistance();
		//---Add a location marker---
		LocationOverlay mapOverlay = new LocationOverlay(p, mapView.getOverlays().size(), this.getDist());
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
				Toast.makeText(getApplicationContext(), 
						"Retry by returning to the main menu and relaunching Trickiest Part.", 
						Toast.LENGTH_LONG).show();
				dialog.dismiss();
			}
		}).show();

		//System.out.println("Switching to Network locator.");
		//lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, TIME_DELAY, DIST_DELAY, this);
	}

	public void onSensorChanged(SensorEvent se) {
		float x = se.values[0];
		float y = se.values[1];
		float z = se.values[2];
		mAccelLast = mAccelCurrent;
		mAccelCurrent = FloatMath.sqrt(x*x + y*y + z*z);
		float delta = mAccelCurrent - mAccelLast;
		mAccel = mAccel * 0.9f + delta; // perform low-cut filter
		System.out.println("Acceleration: "+mAccel);
		if (mAccel > 30)
			setToast("Motion Detected!!!");
	}

	public void onAccuracyChanged(Sensor sensor, int accuracy) {
	}

	private void setToast(String string) {
		Toast.makeText(getApplicationContext(), string, Toast.LENGTH_SHORT).show();
	}

	private double calculateDistance() {
		double dist = 0.0;
		GeoPoint prevPoint = null;
		GeoPoint curPoint = null;
		for (Overlay o : mapView.getOverlays()) {
			if (prevPoint == null)
				prevPoint = ((LocationOverlay)o).getPoint();
			else {
				curPoint = ((LocationOverlay)o).getPoint();

				// Get the distance between the geo points
				float[] results = new float[3];
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
	
	// Check if network is available
	private boolean isNetworkAvailable() {
	    ConnectivityManager connectivityManager 
	          = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
	    return activeNetworkInfo != null;
	}
}