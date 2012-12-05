package edu.neu.madcourse.adamgressen.alienevasion;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.FloatMath;

public class AccelerometerManager implements SensorEventListener {
	Evade evade;

	SensorManager mSensorManager;
	float mAccel = 0f; // acceleration apart from gravity
	float mAccelCurrent; // current acceleration including gravity
	float mAccelLast; // last acceleration including gravity

	public AccelerometerManager(Evade evade) {
		this.evade = evade;
		mSensorManager = (SensorManager) evade.getSystemService(Context.SENSOR_SERVICE);
		mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
		mAccelCurrent = SensorManager.GRAVITY_EARTH;
		mAccelLast = SensorManager.GRAVITY_EARTH;
	}

	public void onSensorChanged(SensorEvent se) {
		float x = se.values[0];
		float y = se.values[1];
		float z = se.values[2];
		mAccelLast = mAccelCurrent;
		mAccelCurrent = FloatMath.sqrt(x*x + y*y + z*z);
		float delta = mAccelCurrent - mAccelLast;
		mAccel = mAccel * 0.9f + delta; // perform low-cut filter
		//System.out.println("Acceleration: "+mAccel);
		if (mAccel > 30)
			evade.setToast("Motion Detected!!!");
	}

	public void pause() {
		mSensorManager.unregisterListener(this);
	}

	public void resume() {
		mSensorManager.registerListener(this, 
				mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
				SensorManager.SENSOR_DELAY_NORMAL);
	}

	public void onAccuracyChanged(Sensor sensor, int accuracy) {
	}
}