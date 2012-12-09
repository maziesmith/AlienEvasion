package edu.neu.madcourse.adamgressen.alienevasion;

import java.util.Random;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Point;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.Projection;
import edu.neu.madcourse.adamgressen.R;

/***
 * 
 * The EnemyOverlay class is our Enemy class
 * It draws the enemy in addition to storing information
 * about the enemies it represents
 * 
 * **/
public class EnemyOverlay extends Overlay{
	GeoPoint enemyPos;
	Random rand = new Random();
	
	Bitmap enemyImage;
	EvadeInterface evade;

	// Constructor
	public EnemyOverlay(Context context, GeoPoint enPoint) {
		this.enemyPos = enPoint;

		this.evade = (EvadeInterface)context;
		
		enemyImage = BitmapFactory.decodeResource(context.getResources(), R.drawable.aliens);
	}

	@Override
	public boolean draw(Canvas canvas, MapView mapView, boolean shadow, long when) {
		super.draw(canvas, mapView, shadow);

		// Convert lat and long to screen coordinates
		Point point = new Point();
		Projection proj = mapView.getProjection();
		proj.toPixels(enemyPos, point);

		canvas.drawBitmap(enemyImage, point.x-(enemyImage.getWidth()/2), point.y-(enemyImage.getHeight()/2), null);

		return true;
	}
}