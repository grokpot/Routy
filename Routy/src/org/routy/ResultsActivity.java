package org.routy;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class ResultsActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_results, menu);
        return true;
    }
}
