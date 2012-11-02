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
	
	
	public static String getJSONResponse(String url) throws MalformedURLException, IOException, NoInternetConnectionException {
		URL distMatUrl = new URL(url.toString());
		
		URLConnection conn = null;
		try {
			conn = distMatUrl.openConnection();
		} catch (IOException e) {
			throw new NoInternetConnectionException("Could not get connection to URL: " + distMatUrl.toExternalForm());
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
			throw new NoInternetConnectionException("Could not get connection to URL: " + distMatUrl.toExternalForm());
		}
	}
}
