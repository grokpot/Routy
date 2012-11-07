package org.routy.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import org.routy.exception.NoInternetConnectionException;

public class InternetService {

	private static final String TAG = "InternetService";
	
	// Using this method: http://docs.oracle.com/javase/tutorial/networking/urls/readingWriting.html
	public static String getJSONResponse(String url) throws MalformedURLException, IOException {
		URL distMatUrl = null;
		
		try {
			distMatUrl = new URL(url.toString());
		} catch (MalformedURLException e) {
			throw new MalformedURLException("Distance Matrix URL [" + url + "] is malformed.");
		}
		
		URLConnection conn = null;
		try {
			conn = distMatUrl.openConnection();
		} catch (IOException e) {
			throw new IOException("Could not establish a connection to URL: " + distMatUrl.toExternalForm());
		}
		
		if (conn != null) {
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
		} else {
			throw new IOException("Could not establish a connection to URL: " + distMatUrl.toExternalForm());
		}
	}
}
