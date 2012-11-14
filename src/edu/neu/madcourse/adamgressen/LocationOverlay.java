package edu.neu.madcourse.adamgressen;

import java.util.List;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.location.Location;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.Projection;

public class LocationOverlay extends Overlay {
	GeoPoint p;
	int index;
	String dist;
	
	// Set up paint
	Paint paint = new Paint();
	Paint textPaint = new Paint();

	public LocationOverlay(GeoPoint p, int index, String d) {
		this.p = p;
		this.index = index;
		this.dist = d;
		
		paint.setStrokeWidth(4);
		paint.setARGB(80,0,0,255);
		paint.setStyle(Paint.Style.FILL_AND_STROKE);
		
		textPaint.setStrokeWidth(4);
		textPaint.setTextSize(40);
		textPaint.setARGB(255, 0, 0, 255);
		textPaint.setStyle(Paint.Style.STROKE);
	}

	@Override
	public boolean draw(Canvas canvas, MapView mapView, boolean shadow, long when) {
		super.draw(canvas, mapView, shadow);
		
		// Convert lat and long to screen coordinates
		Point previousPoint = new Point();
		Point point = new Point();
		Projection proj = mapView.getProjection();
		proj.toPixels(p, point);

		List<Overlay> overlays = mapView.getOverlays();
		if (this.index > 0) {
			LocationOverlay previous = (LocationOverlay)overlays.get(index-1);
			GeoPoint previousGeoPoint = previous.getPoint();
			proj.toPixels(previousGeoPoint, previousPoint);
			canvas.drawLine(previousPoint.x, previousPoint.y, point.x, point.y, paint);

			// Get the distance between the geo points
			float[] results = new float[3];
			Location.distanceBetween(
					previousGeoPoint.getLatitudeE6()/1E6, 
					previousGeoPoint.getLongitudeE6()/1E6,
					p.getLatitudeE6()/1E6,
					p.getLongitudeE6()/1E6,
					results);
		}

		if (this.index == overlays.size()-1) {
			canvas.drawCircle(point.x, point.y, 20, paint);
			canvas.drawText(this.dist+" miles", point.x, point.y, textPaint);
		}

		return true;
	}

	public GeoPoint getPoint() {
		return this.p;
	}
}