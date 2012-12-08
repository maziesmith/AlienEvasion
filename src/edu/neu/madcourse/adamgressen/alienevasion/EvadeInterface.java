package edu.neu.madcourse.adamgressen.alienevasion;

import java.util.LinkedList;

import com.google.android.maps.GeoPoint;

public interface EvadeInterface {
	public double getDist();
	public int getEvaded();
	public LinkedList<GeoPoint> getLocPositions();
}
