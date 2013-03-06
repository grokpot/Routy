package org.routy.listener;

import android.location.Address;

public abstract class ReverseGeocodeListener {

	public abstract void onResult(Address address);
}
