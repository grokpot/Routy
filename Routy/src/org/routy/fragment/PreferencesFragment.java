package org.routy.fragment;

import org.routy.R;

import android.app.FragmentManager;
import android.content.DialogInterface;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceFragment;

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
				OneButtonDialog dialog = new OneButtonDialog(getResources().getString(R.string.about_title), getResources().getString(R.string.about_text)) {
					@Override
					public void onButtonClicked(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				};
				dialog.show(fragmentManager, TAG);
				return true;
			}
	    });
	}
}