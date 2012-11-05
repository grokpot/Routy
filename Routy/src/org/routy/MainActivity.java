package org.routy;

import org.routy.fragment.OneButtonDialog;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Canvas;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;

public class MainActivity extends FragmentActivity {

  private final String TAG = "MainActivity";

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    new Handler().postDelayed(new Runnable() {

      @Override
      public void run() {
        // TODO This still happens too fast to display a splash image...keep the
        // delay in there?
        // Check for a network connection and display an error if there is none.
        Log.v(TAG, "Checking for network.");
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo == null) {
          Log.v(TAG, "No network.");
          OneButtonDialog error = new OneButtonDialog(getResources().getString(
              R.string.error_message_title), getResources().getString(
              R.string.no_internet_error)) {
            @Override
            public void onButtonClicked(DialogInterface dialog, int which) {
              dialog.dismiss();
            }
          };
          error.show(MainActivity.this.getSupportFragmentManager(), TAG);
        }
        else {
          Log.v(TAG, "Found a network.");
        }
        // Start an intent to bring up the origin screen
        Intent originIntent = new Intent(MainActivity.this,
            OriginActivity.class);
        startActivity(originIntent);
      }
    }, 1250);
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.activity_main, menu);
    return true;
  }
}
