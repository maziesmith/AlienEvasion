package edu.neu.madcourse.adamgressen.alienevasion;

import java.io.Serializable;
import java.util.Random;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.Projection;

/***
 * 
 * The EnemyOverlay class is our Enemy class
 * It draws the enemy in addition to storing information
 * about the enemies it represents
 * 
 * **/
public class EnemyOverlay extends Overlay{
	GeoPoint p;
	double t;
	Point enemyPos;
	Random rand = new Random();

	// Set up paint
	Paint paint = new Paint();

	// Constructor
	public EnemyOverlay(GeoPoint p,double t) {
		this.p = p;
		//t is in radians
		this.t = t;

		paint.setStrokeWidth(4);
		paint.setARGB(80,255,0,0);
		paint.setStyle(Paint.Style.FILL_AND_STROKE);
	}

	@Override
	public boolean draw(Canvas canvas, MapView mapView, boolean shadow, long when) {
		super.draw(canvas, mapView, shadow);

		// Convert lat and long to screen coordinates
		Point point = new Point();
		Projection proj = mapView.getProjection();
		proj.toPixels(p, point);

		enemyPos = randomizePos(point);

		canvas.drawCircle(enemyPos.x, enemyPos.y, 20, paint);

		return true;
	}

	// Randomizes position
	private Point randomizePos(Point playerPoint) {
		//double t = 2*Math.PI*rand.nextDouble();
		double r = 100;
		
		int prevx = playerPoint.x;
		int prevy = playerPoint.y;
		
		Point point = new Point((int)(prevx+ r*Math.sin(t)), (int)(prevy + r*Math.cos(t)));

		return point;
	}

	public GeoPoint getPoint() {
		return this.p;
	}
}