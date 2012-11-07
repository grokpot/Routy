package org.routy.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import org.routy.exception.RoutyException;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

public class InternetService {

	private static final String TAG = "InternetService";
	
	
	/**
	 * Checks if the device currently has any connection to the internet.
	 * 
	 * @param context
	 * @return			true if there's an internet connection, false otherwise
	 */
	public static boolean deviceHasInternetConnection(Context context) {
        Log.v(TAG, "Checking for network.");
        ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        
        return networkInfo != null;
	}
	
	// Using this method: http://docs.oracle.com/javase/tutorial/networking/urls/readingWriting.html
	/**
	 * Reads JSON response from a given URL.
	 * 
	 * @param url
	 * @return
	 * @throws RoutyException	if the given URL is not valid
	 * @throws IOException		if a connection to the URL could not be made, or if data could not be 
	 * 							read from the URL
	 */
	public static String getJSONResponse(String url) throws RoutyException, IOException {
		URL distMatUrl = null;
		
		try {
			distMatUrl = new URL(url.toString());
		} catch (MalformedURLException e) {
			Log.e(TAG, "Distance Matrix URL [" + url + "] is malformed.");
			throw new RoutyException();
		}
		
		URLConnection conn;
		try {
			conn = distMatUrl.openConnection();
		} catch (IOException e) {
			conn = null;
		}
		
		if (conn != null) {
			try {
				BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
				StringBuffer jsonResp = new StringBuffer();
				
				try {
					String line = null;
					while ((line = in.readLine()) != null) {
						jsonResp.append(line);
					}
					
					return jsonResp.toString();
				} finally {
					in.close();
				}
			} catch (IOException e) {
				Log.e(TAG, e.getMessage() + "\nCould not read from URL: " + distMatUrl.toExternalForm());
				throw new IOException("Could not read from URL: " + distMatUrl.toExternalForm());
			}
		} else {
			Log.e(TAG, "Could not establish a connection to URL: " + distMatUrl.toExternalForm());
			throw new IOException("Could not establish a connection to URL: " + distMatUrl.toExternalForm());
		}
	}
}
