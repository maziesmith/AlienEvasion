package edu.neu.madcourse.adamgressen;

import java.util.List;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;
import com.google.android.maps.Overlay;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

public class Tricky extends MapActivity implements LocationListener  {

	MapView mapView;
	MyLocationOverlay myLocOverlay;
	MapController mc;
	LocationManager lm;
	GeoPoint p;
	

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.trickiest);

		mapView = (MapView) findViewById(R.id.mapview);
		mapView.setBuiltInZoomControls(true);

		initMyLocation();

		lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
		lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 20, this);
	}

	private void initMyLocation() {
		mc = mapView.getController();
		mc.setZoom(18);
		String coordinates[] = {"42.348332", "-71.087873"};
		double lat = Double.parseDouble(coordinates[0]);
		double lng = Double.parseDouble(coordinates[1]);

		p = new GeoPoint(
				(int) (lat * 1E6), 
				(int) (lng * 1E6));

		mc.animateTo(p);
		//---Add a location marker---
		LocationOverlay mapOverlay = new LocationOverlay(p, mapView.getOverlays().size());
		List<Overlay> listOfOverlays = mapView.getOverlays();
		listOfOverlays.add(mapOverlay);
		mapView.invalidate();
	}

	public void onLocationChanged(Location location) {
		Log.d("","location changed");

		p = new GeoPoint(
				(int) (location.getLatitude() * 1E6), 
				(int) (location.getLongitude() * 1E6));

		mc.animateTo(p);
		//---Add a location marker---
		LocationOverlay mapOverlay = new LocationOverlay(p, mapView.getOverlays().size());
		List<Overlay> listOfOverlays = mapView.getOverlays();
		listOfOverlays.add(mapOverlay);
		mapView.invalidate();
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

}

