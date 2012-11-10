package edu.neu.madcourse.adamgressen;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.Toast;

public class MotionSensor extends Activity implements SensorEventListener {
	
	private SensorManager mSensorManager;
	private Sensor mSensor;
	private long lastTime = 0L;
	
	
	//private static ArrayList<FloatArray> listAxis = new ArrayList<FloatArray>();
	
	private final float MINIMUM_ACCELERATION = (float)9;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.motion_sensor);
		
		mSensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
		mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
		
		mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_NORMAL);
		
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		mSensorManager.unregisterListener(this);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_NORMAL);
	}

	public void onSensorChanged(SensorEvent event) {
		// TODO Auto-generated method stub
	
		
		if(lastTime == 0){
			lastTime = System.currentTimeMillis();
		}
		else if(greaterMin(event.values)){
			
			long current = System.currentTimeMillis();
			if((current - lastTime > 2000) ){
				setToast("Motion Detected!!!");
				System.out.println("Motion");
				lastTime = current;
			}
			
			
		}
		
		/*
		if(listAxis.size() < 3){

			if(greaterMin(event.values)){
				listAxis.add(new FloatArray(event.values));

				System.out.println(event.values[0]);
				
			}
			return;
			//System.out.print(event.values[1]);
			//System.out.println(event.values[2]);
		}

		System.out.println("Size of list = " + listAxis.size());

		FloatArray firstPoint = new FloatArray(listAxis.get(0).array);
		FloatArray secondPoint = new FloatArray(listAxis.get(1).array);

		System.out.println("inside else");
		System.out.println(listAxis.get(0).array[0]);
		System.out.println(listAxis.get(1).array[0]);
		System.out.println(firstPoint.array[0]);
		System.out.println(secondPoint.array[0]);

		boolean xbool = legalMotion(firstPoint.array[0],secondPoint.array[0],event.values[0]);
		boolean ybool = legalMotion(firstPoint.array[1],secondPoint.array[1],event.values[1]);
		boolean zbool = legalMotion(firstPoint.array[2],secondPoint.array[2],event.values[2]);

		if(xbool || ybool || zbool){

			setToast("Motion Detected!!!!");
		}

		System.out.println("clear");
		listAxis.clear();
		
		*/
	}




	private boolean greaterMin(float[] axisPoint){
		if(Math.abs(axisPoint[0]) > MINIMUM_ACCELERATION
				|| Math.abs(axisPoint[1]) > MINIMUM_ACCELERATION
			|| Math.abs(axisPoint[2])> MINIMUM_ACCELERATION){ return true;}

		return false;
	}
	
	private boolean legalMotion(float firstPoint, float secondPoint, float thirdPoint){
		
			System.out.println("greater than MIN ");
			System.out.println(firstPoint);
			System.out.println(secondPoint);
			System.out.println(thirdPoint);
			
			if((firstPoint > (float)0 && secondPoint < (float)0 && thirdPoint > (float)0)
					|| (firstPoint < (float)0 && secondPoint > (float)0 && thirdPoint < (float)0))
			return true;
		
		return false;
	}

	private void setToast(String string) {
		// TODO Auto-generated method stub
		Toast.makeText(getApplicationContext(), string, Toast.LENGTH_SHORT).show();
	}

	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub
		
	}


}
