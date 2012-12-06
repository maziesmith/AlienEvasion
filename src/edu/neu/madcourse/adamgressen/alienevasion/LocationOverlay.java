package edu.neu.madcourse.adamgressen.alienevasion;

import java.util.LinkedList;
import java.util.List;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.Projection;

public class LocationOverlay extends Overlay{
	GeoPoint p;
	GeoPoint prev;
	int index;
	int pursuing;
	
	// Set up paint
	Paint paint = new Paint();
	Paint textPaint = new Paint();

	public LocationOverlay(GeoPoint p, int index) {
		this.p = p;
		pursuing = Evade.getPursuing();
		
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
		Point point = new Point();
		Projection proj = mapView.getProjection();
		proj.toPixels(p, point);

		List<Overlay> overlays = mapView.getOverlays();
		LinkedList<LocationOverlay> locOverlays = new LinkedList<LocationOverlay>();
		for (Overlay o : overlays) {
			if (o.getClass().equals(LocationOverlay.class)) {
				locOverlays.add((LocationOverlay)o);
			}
		}
		
		// Draw line to previous GeoPoint
		if (index == 0)
			this.prev = null;
		else
			this.prev = Evade.locPositions.get(index-1);
		
		if (this.prev != null) {
			Point previousPoint = new Point();
			GeoPoint previousGeoPoint = prev;
			proj.toPixels(previousGeoPoint, previousPoint);
			canvas.drawLine(previousPoint.x, previousPoint.y, point.x, point.y, paint);
		}

		if (this.index == locOverlays.size()-1) {
			canvas.drawCircle(point.x, point.y, 20, paint);
			canvas.drawText(Evade.getDist()+" miles", point.x, point.y, textPaint);
		}

		return true;
	}

	public GeoPoint getPoint() {
		return this.p;
	}
}