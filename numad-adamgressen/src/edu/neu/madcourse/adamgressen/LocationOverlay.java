package edu.neu.madcourse.adamgressen;

import java.util.List;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.Projection;

public class LocationOverlay extends Overlay {
	GeoPoint p;
	int index;
	
	public LocationOverlay(GeoPoint p, int index) {
		this.p = p;
		this.index = index;
	}
	
	@Override
	public boolean draw(Canvas canvas, MapView mapView, boolean shadow, long when) {
		super.draw(canvas, mapView, shadow);
		
		Paint paint = new Paint();
		// Convert lat and long to screen coordinates
		Point previousPoint = new Point();
		Point point = new Point();
		Projection proj = mapView.getProjection();
		proj.toPixels(p, point);
		paint.setStrokeWidth(4);
		paint.setARGB(80,0,0,255);
		paint.setStyle(Paint.Style.FILL_AND_STROKE);
		
		List<Overlay> overlays = mapView.getOverlays();
		if (this.index > 0) {
			LocationOverlay previous = (LocationOverlay)overlays.get(index-1);
			proj.toPixels(previous.getPoint(), previousPoint);
			canvas.drawLine(previousPoint.x, previousPoint.y, point.x, point.y, paint);
		}
		
		if (this.index == overlays.size()-1) {
			canvas.drawCircle(point.x, point.y, 20, paint);
		}
		
		return true;
	}
	
	public GeoPoint getPoint() {
		return this.p;
	}
}