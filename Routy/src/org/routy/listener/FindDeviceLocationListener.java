package org.routy.listener;

import android.location.Location;

public abstract class FindDeviceLocationListener {

	public abstract void onDeviceFound(Location deviceLocation);
}
