package org.routy.fragment;

import java.net.URISyntaxException;

import org.routy.R;
import org.routy.log.Log;
import org.routy.model.PreferencesModel;
import org.routy.model.RouteOptimizePreference;

import android.app.FragmentManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.SwitchPreference;

public class PreferencesFragment extends PreferenceFragment {
	private static final String TAG = "PreferenceFragment";
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
	    super.onCreate(savedInstanceState);
	    final FragmentManager fragmentManager = getFragmentManager();;
	    addPreferencesFromResource(R.xml.preferences);
	    
	    Preference myPref = (Preference) findPreference("pref_about");
	    myPref.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			public boolean onPreferenceClick(Preference preference) {
				String aboutText = getResources().getString(R.string.about_title);
				
				//Detect and add version number
				try {
					aboutText += " " + getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0).versionName;
				} catch (NameNotFoundException e) {
					Log.e(TAG, "problem getting the version number from the package");
					//Do Nothing...it just won't show a version number
				}
				
				TwoButtonDialog dialog = new TwoButtonDialog(aboutText, getResources().getString(R.string.about_text), new String[]{"Close", null, "Contact"}) {
					@Override
					public void onRightButtonClicked(DialogInterface dialog, int which) {
						dialog.dismiss();
					}

					@Override
					public void onLeftButtonClicked(DialogInterface dialog, int which) {
						// Send feedback
						try {
							Intent intent = Intent.parseUri("mailto:GoRouty@gmail.com?subject=Routy%20App%20Feedback", Intent.URI_INTENT_SCHEME);
							getActivity().startActivity(intent);
						} catch (URISyntaxException e) {
//							Log.e(TAG, "couldn't start mail activity to send feedback");
							e.printStackTrace();
						}
						    
					}
				};
				dialog.show(fragmentManager, TAG);
				return true;
			}
	    });
	    
	    Preference eula = (Preference) findPreference("pref_eula");
	    eula.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			public boolean onPreferenceClick(Preference preference) {
				OneButtonDialog dialog = new OneButtonDialog(getResources().getString(R.string.eula), getResources().getString(R.string.eula_text)) {
					@Override
					public void onButtonClicked(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				};
				dialog.show(fragmentManager, TAG);
				return true;
			}
	    });
	    
	    Preference reset_instructions = (Preference) findPreference("pref_reset_instruction_text");
	    reset_instructions.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			public boolean onPreferenceClick(Preference preference) {
				SharedPreferences defaultSharedPrefs = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
				Editor e = defaultSharedPrefs.edit();
				e.putBoolean("routy_noob", true);
				e.putBoolean("results_noob", true);
				e.commit();
				PreferencesModel.getSingleton().setRoutyNoob(true);
				PreferencesModel.getSingleton().setResultsNoob(true);
				
				OneButtonDialog dialog = new OneButtonDialog(getResources().getString(R.string.pref_instructions_reset), getResources().getString(R.string.pref_instructions_reset_text)) {
					@Override
					public void onButtonClicked(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				};
				dialog.show(fragmentManager, TAG);
				
				return true;
			}
	    });
	    
	    //Switch route optimization between shortest time/distance
	    SwitchPreference routeModeSwitch = (SwitchPreference) findPreference("route_mode");
	    routeModeSwitch.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
			
			@Override
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				if (preference.getKey().equals("route_mode")) {
					boolean mode = (Boolean) newValue;		// False = distance; True = time
					Log.v(TAG, "route mode changed to " + mode);
					
					if (!mode) {
						PreferencesModel.getSingleton().setRouteOptimizeMode(RouteOptimizePreference.PREFER_DURATION);
					} else {
						PreferencesModel.getSingleton().setRouteOptimizeMode(RouteOptimizePreference.PREFER_DISTANCE);
					}
				}
				return true;
			}
		});
	    
	    //Switch in-app sounds between on/off
	    SwitchPreference soundsModeSwitch = (SwitchPreference) findPreference("sounds_mode");
	    soundsModeSwitch.setChecked(PreferenceManager.getDefaultSharedPreferences(getActivity()).getBoolean("sounds_mode", true));
	    soundsModeSwitch.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
			
			@Override
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				if (preference.getKey().equals("sounds_mode")) {
					boolean mode = (Boolean) newValue;		// False = off; True = on
					Log.v(TAG, "sounds mode changed to " + mode);
					
					PreferencesModel.getSingleton().setSoundsOn(mode);
				}
				return true;
			}
		});
	    
	}
	
	public void onToggleClicked(boolean on) {
		if (on) {
			PreferencesModel.getSingleton().setRouteOptimizeMode(RouteOptimizePreference.PREFER_DURATION);
		} 
		else {
			PreferencesModel.getSingleton().setRouteOptimizeMode(RouteOptimizePreference.PREFER_DISTANCE);
		}
	}
}