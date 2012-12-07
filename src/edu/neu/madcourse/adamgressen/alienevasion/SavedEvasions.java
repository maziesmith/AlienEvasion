package edu.neu.madcourse.adamgressen.alienevasion;

import java.util.LinkedList;
import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;

import edu.neu.madcourse.adamgressen.R;

public class SavedEvasions extends MapActivity {

	//LocationOverlays
	LinkedList<LocationOverlay> locOverlays;
	// EnemyOverlays
	LinkedList<EnemyOverlay> enOverlays;
	//List of location positions
	LinkedList<GeoPoint> locPositions;
	//List of Enemy Positions
	LinkedList<GeoPoint> enPositions;
	//Geopoint
	GeoPoint p;
	//MapView
	MapView mapView;
	//MapOverlays
	List<Overlay> mapOverlays;
	//MapView Controller
	MapController mc;

	@Override
	protected void onCreate(Bundle icicle) {
		// TODO Auto-generated method stub
		super.onCreate(icicle);

		Bundle b = getIntent().getExtras();
		String name = b.getString("EVASION_NAME");

		StoredEvasion sevasion = getEvasion(name);
		
		locPositions = new LinkedList<GeoPoint>();
		locOverlays = new LinkedList<LocationOverlay>();
		enPositions = new LinkedList<GeoPoint>();
		enOverlays = new LinkedList<EnemyOverlay>();
		
		if(sevasion !=null){


			// Get stored location positions
			locPositions = sevasion.locPositions;
			// Current position equals the last stored position
			p = locPositions.getLast();

			// Populate location overlays
			for (int gp = 0; gp < locPositions.size(); gp++) {
				//locOverlays.add(new LocationOverlay(locPositions.get(gp), gp));
			}

			// Get stored enemy positions
			enPositions = sevasion.enPositions;

			// Populate enemy overlays
			for (GeoPoint gp : enPositions) {
				//enOverlays.add(new EnemyOverlay(gp));
			}

		}
		
		// If there's no network then the map won't display
		if (!isNetworkAvailable()) {
			// Ask for another attempt
			new AlertDialog.Builder(this)
			.setTitle("No Network Available")
			.setMessage("You will not be able to view the map, but you can still see your Path and load the map later.")
			.setCancelable(true)
			.setPositiveButton("OK", new DialogInterface.OnClickListener() {			
				public void onClick(DialogInterface dialog, int id) {
					dialog.dismiss();
				}
			}).show();
		}
		
		mapView = (MapView) findViewById(R.id.mapview);
		//mapView.setBuiltInZoomControls(true); // Enables zoom controls

		mapOverlays = mapView.getOverlays();

		mc = mapView.getController();
		mc.setZoom(18);

		mapOverlays.clear();
		mapOverlays.addAll(locOverlays);
		mapOverlays.addAll(enOverlays);
	}



	private StoredEvasion getEvasion(String name) {
		StoredEvasion se = new StoredEvasion(name).read(getApplicationContext());
		return se;
	}
	
	// Check if network is available
		private boolean isNetworkAvailable() {
			ConnectivityManager connectivityManager 
			= (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
			return activeNetworkInfo != null;
		}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}

}
