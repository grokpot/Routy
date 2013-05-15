package org.routy.timer;

import java.util.TimerTask;

import org.routy.log.Log;

import android.os.AsyncTask;

public class Timeout extends TimerTask {

	private final String TAG = "Timeout";
	
	private AsyncTask<?, ?, ?> task;
	private TimeoutCallback callback;
	
	public Timeout(AsyncTask<?, ?, ?> task, TimeoutCallback callback) {
		this.task = task;
		this.callback = callback;
	}
	
	
	@Override
	public void run() {
		Log.v(TAG, "timing out task:" + task.getClass().getName());
		task.cancel(true);
		Log.v(TAG, "is task cancelled?" + task.isCancelled());
		callback.onTimeout();
	}

}
