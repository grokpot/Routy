package org.routy;

import java.util.TimerTask;

import android.os.AsyncTask;

public class Timeout extends TimerTask {

	private AsyncTask<?, ?, ?> task;
	private TimeoutCallback callback;
	
	public Timeout(AsyncTask<?, ?, ?> task, TimeoutCallback callback) {
		this.task = task;
		this.callback = callback;
	}
	
	
	@Override
	public void run() {
		task.cancel(true);
		callback.onTimeout();
	}

}
