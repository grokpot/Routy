package org.routy.validator;

import java.util.List;

import junit.framework.Assert;

import org.routy.adapter.PlacesListAdapter;
import org.routy.exception.RoutyException;
import org.routy.fragment.ListPickerDialog;
import org.routy.model.AppProperties;
import org.routy.model.GooglePlace;
import org.routy.model.ValidateDestinationRequest;
import org.routy.task.ValidateDestinationTask;
import org.routy.view.DestinationInputRow;

import android.support.v4.app.FragmentActivity;
import android.util.Log;

/**
 * Validates a string query to get Google Places result(s).  If the validator is 
 * created with a {@link FragmentActivity}, then it has the power to prompt the 
 * user to select a result from a list of multiple query results.
 * 
 * TODO Code for the case where the validator is used without being pass a {@link FragmentActivity}.
 * @author jtran
 *
 */
public abstract class MultiplePlaceValidator {

	private final String TAG = "PlaceValidator";
	
	private FragmentActivity fragmentActivity;
	private double centerLat;
	private double centerLng;
	private int radius;
	
	public abstract void onValidated(GooglePlace validated);
	public abstract void onZeroResults();
	public abstract void onValidationFailed(RoutyException exception);
	
	
	/**
	 * Creates a PlaceValidator for use from a FragmentActivity.  This allows the validator to prompt 
	 * the user to select a result when there are multiple choices.  The default radius from {@link AppProperties} 
	 * will be used for the search.
	 * 
	 * @param fragmentActivity
	 * @param centerLat
	 * @param centerLng
	 */
	public MultiplePlaceValidator(FragmentActivity fragmentActivity, double centerLat, double centerLng) {
		this(fragmentActivity, centerLat, centerLng, AppProperties.G_PLACES_SEARCH_RADIUS_M);
	}
	
	
	public MultiplePlaceValidator(FragmentActivity fragmentActivity, double centerLat, double centerLng, int radius) {
		super();
		
		this.fragmentActivity = fragmentActivity;
		
		this.centerLat = centerLat;
		this.centerLng = centerLng;
		this.radius = radius;
	}
	
	
	public void validate(String query) {
		Assert.assertTrue(query != null && query.length() > 0);
		
		// Doing this in an AsyncTask because it uses the network.
		/*new ValidateDestinationTask() {
			
			@Override
			public void onResult(List<GooglePlace> results) {
				if (results == null || results.size() < 1) {
					// No results.  Display a message.
					Log.v(TAG, "No places found for query");
					row.setInvalid();
					showErrorDialog("No places or addresses found for this destination.  Try broadening your search.");
					onZeroResults();
				} else if (results.size() == 1) {
					// Only one result.  Turn it into an address, set it, and set the valid status
					Log.v(TAG, "1 place found for query");
					row.setAddress(results.get(0).getAddress());
					row.setValid();
					onValidated(results.get(0));
				} else {
					// More than 1 result.  Display the pickable list dialog.
					Log.v(TAG, "More than 1 place found for query -- " + results.size() + " results");
					showPlacePickerDialog(row, results);
				}
			}

			@Override
			public void onFailure(RoutyException exception) {
//				showErrorDialog(exception.getMessage());
				onValidationFailed(exception);
			}
		}.execute(new ValidateDestinationRequest(addressString, origin.getLatitude(), origin.getLongitude(), AppProperties.G_PLACES_SEARCH_RADIUS_M));*/
	}
	
	
	private void showPlacePickerDialog(final DestinationInputRow row, List<GooglePlace> options) {
		Log.v(TAG, "Show place picker dialog");
		final PlacesListAdapter adapter = new PlacesListAdapter(fragmentActivity, options);
		ListPickerDialog dialog = new ListPickerDialog("Select...", adapter) {

			@Override
			public void onSelection(int which) {
				row.setAddress(((GooglePlace) adapter.getItem(which)).getAddress());
				row.setValid();
				Log.v(TAG, "Address: " + row.getAddress().getLatitude() + ", " + row.getAddress().getLongitude());
			}
			
		};
		dialog.show(fragmentActivity.getSupportFragmentManager(), TAG);
	}
}
