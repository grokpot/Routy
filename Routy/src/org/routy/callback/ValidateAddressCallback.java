package org.routy.callback;

import org.routy.model.RoutyAddress;

public abstract class ValidateAddressCallback {
	public abstract void onAddressValidated(RoutyAddress validatedAddress);
}
