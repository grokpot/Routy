package org.routy.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.routy.exception.NoInternetConnectionException;
import org.routy.exception.RoutyException;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.http.AndroidHttpClient;

public class InternetService {

	private static final String TAG = "InternetService";
	
	
	/**
	 * Checks if the device currently has any connection to the internet.
	 * 
	 * @param context
	 * @return			true if there's an internet connection, false otherwise
	 */
	public static boolean deviceHasInternetConnection(Context context) {
//        Log.v(TAG, "Checking for network.");
        ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        
        return networkInfo != null;
	}
	
	// Using this method: http://docs.oracle.com/javase/tutorial/networking/urls/readingWriting.html
	/**
	 * Reads back the response from a given URL as a string.
	 * 
	 * @param url
	 * @return
	 * @throws RoutyException	if the given URL is not valid
	 * @throws IOException		if a connection to the URL could not be made, or if data could not be 
	 * 							read from the URL
	 */
	public static String getStringResponse(String url) throws RoutyException, IOException, NoInternetConnectionException {
		InputStream inputStream;
		AndroidHttpClient client;
		try {
//			inputStream = getStreamResponse(url);
			client = AndroidHttpClient.newInstance(System.getProperty("http.agent"));
			HttpUriRequest request = new HttpGet(url);
			HttpResponse response = client.execute(request);
			HttpEntity entity = response.getEntity();
			inputStream = entity.getContent();
//			client.close();
		} catch (IOException e) {
//			Log.e(TAG, "Could not establish a connection to URL: " + url);
			throw new NoInternetConnectionException("Could not establish a connection to URL: " + url);
//			throw new IOException("Could not establish a connection to URL: " + url);
		}
		
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));
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
//			Log.e(TAG, e.getMessage() + "\nCould not read from URL: " + url);
			throw new IOException("Could not read from URL: " + url);
		} finally {
			if (client != null) {
				client.close();
			}
		}
	}
	
	
	/**
	 * Gets an {@link InputStream} to read a response from the given URL.
	 * 
	 * @param url
	 * @return
	 * @throws RoutyException
	 * @throws IOException
	 */
	public static InputStream getStreamResponse(String url) throws RoutyException, IOException {
		URL u = null;
		
		try {
			u = new URL(url);
		} catch (MalformedURLException e) {
//			Log.e(TAG, "Distance Matrix URL [" + url + "] is malformed.");
			throw new RoutyException();
		}
		
		URLConnection conn = u.openConnection();
		return conn.getInputStream();
	}
}
