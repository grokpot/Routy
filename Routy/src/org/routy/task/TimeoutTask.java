package org.routy.task;

import android.os.AsyncTask;

public abstract class TimeoutTask extends AsyncTask<Long, Void, Void> {

	/**
	 * Stop doing whatever it is you want to timeout in this method (eg. stop location updates).
	 */
	public abstract void onTimeout();
	
	
	public TimeoutTask() {
		
	}
	
	
	@Override
	protected Void doInBackground(Long... params) {
		return null;
	}
	
	
	@Override
	protected void onPostExecute(Void v) {
		// Don't need to do anything with v, it's just a placeholder.
		onTimeout();
	}

}
