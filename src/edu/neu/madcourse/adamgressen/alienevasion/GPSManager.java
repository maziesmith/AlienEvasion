package edu.neu.madcourse.adamgressen.alienevasion;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.location.GpsStatus;
import android.os.SystemClock;
import android.widget.Toast;

public class GPSManager implements GpsStatus.Listener {
	static final int TIMEOUT_DELAY = 30000;

	Evade evade;
	
	ProgressDialog pro;
	Long beginCheckTime;
	
	Boolean blockMessageShown = false;
	Boolean timeout = false;

	public GPSManager(Evade evade) {
		this.evade = evade;
		
		createProgressDialog();
	}
	
	private void createProgressDialog() {
		pro = new ProgressDialog(evade);
		pro.setMessage("Acquiring GPS Signal");
	}

	// When the GPS status changes
	public void onGpsStatusChanged(int event) {
		switch (event) {
		case GpsStatus.GPS_EVENT_SATELLITE_STATUS:

			// If there's a previous location
			// and the time is outside of the timeout period
			if (!blockMessageShown && evade.locMan.lastLoc != null && SystemClock.elapsedRealtime() - evade.locMan.lastLocTime > TIMEOUT_DELAY) {
				// If the time is outside of the timeout period
				Toast.makeText(evade,
						"The aliens are blocking your transmissions.",
						Toast.LENGTH_SHORT).show();
				blockMessageShown = true;
			}

			// If there's no previous location
			else if (evade.locMan.lastLoc == null) {
				// If we're within the acceptable wait range
				if (SystemClock.elapsedRealtime() - beginCheckTime < TIMEOUT_DELAY) {
					System.out.println("Waiting for a GPS fix");
				}
				// Otherwise, we need to timeout
				else {
					GPSTimeout();
					timeout = true;
				}
			}
			break;

		case GpsStatus.GPS_EVENT_FIRST_FIX:
			pro.hide();
			break;
		}
	}

	// Begin checking for GPS signal
	public void checkForGPS() {
		pro.show();
		// Reset begin check time
		beginCheckTime = SystemClock.elapsedRealtime();
		evade.locMan.beginLocationUpdating();
	}

	// Handle GPS Timeout
	public void GPSTimeout() {
		//pro.setMessage("Acquiring position from Network");
		System.out.println("GPS timed out");
		pro.hide();
		evade.locMan.stopLocationUpdating();

		// Ask for another attempt
		new AlertDialog.Builder(evade)
		.setTitle("Trouble Finding Location")
		.setMessage("The government agents can't locate you right now.\nTry again?")
		.setCancelable(true)
		.setPositiveButton("Yes", new DialogInterface.OnClickListener() {			
			public void onClick(DialogInterface dialog, int id) {
				// Start checking again
				checkForGPS();
				// Makes it so timeout will occurr again if necessary
				timeout = false;
				dialog.dismiss();
			}
		})
		.setNegativeButton("No", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				Toast.makeText(evade, 
						"Retry by returning to the main menu and relaunching Trickiest Part.", 
						Toast.LENGTH_LONG).show();
				dialog.dismiss();
			}
		}).show();
	}

	public void pause() {
		pro.dismiss();
	}
	
	public void resume() {
		createProgressDialog();
		// Reset begin check time variable
		beginCheckTime = SystemClock.elapsedRealtime();
	}
}