package org.routy.service;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.routy.Util;
import org.routy.exception.NoInternetConnectionException;
import org.routy.exception.RoutyException;
import org.routy.log.Log;
import org.routy.model.AppConfig;
import org.routy.model.GoogleDirections;
import org.routy.model.Leg;
import org.routy.model.RoutyAddress;
import org.routy.model.Step;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.google.android.maps.GeoPoint;

/**
 * Provides methods for accessing the Google Directions API
 * 
 * @author jtran
 *
 */
public class GoogleDirectionsService {

	private final String TAG = "GoogleDirectionsService";
	
	private final double E6 = 1000000.0;
	
	
	public GoogleDirectionsService() {
		super();
	}
	
	public GoogleDirections getDirections(List<RoutyAddress> addresses, boolean sensor) throws IOException, RoutyException, NoInternetConnectionException {
		// build Directions URL...
		String url = buildDirectionsURL(addresses, sensor);
		
		Log.v(TAG, "DIRECTIONS API url: " + url);
		
		// get the XML response from Google Directions
		String resp = InternetService.getStringResponse(url);
		
		Log.v(TAG, "DIRECTIOS API resp: " + resp);
		
		// parse the response into a GoogleDirections object
		return parseGoogleDirectionsResponse(resp);
	}
	
	
	private GoogleDirections parseGoogleDirectionsResponse(String resp) throws IOException {
		if (resp == null || resp.length() == 0) {
			return null;
		}
		
		try {
			XPath xpath = XPathFactory.newInstance().newXPath();
			Node root = (Node) xpath.evaluate("//DirectionsResponse", new InputSource(new StringReader(resp)), XPathConstants.NODE);
			
			String status = xpath.evaluate("status", root);
			if (status.equalsIgnoreCase("ok")) {
				GoogleDirections directions = new GoogleDirections();
				
				List<Step> steps = new ArrayList<Step>();
				
				NodeList legNodes = (NodeList) xpath.evaluate("route/leg", root, XPathConstants.NODESET);
				NodeList stepNodes = null;
				Node leg = null;
				for (int l = 0; l < legNodes.getLength(); l++) {
					leg = legNodes.item(l);
					
					stepNodes = (NodeList) xpath.evaluate("step", leg, XPathConstants.NODESET);
					Leg _leg = new Leg();
					
					for (int i = 0; i < stepNodes.getLength(); i++) {
						Node stepNode = stepNodes.item(i);
						
						Step step = new Step();
						String mode = (String) xpath.evaluate("travel_mode", stepNode, XPathConstants.STRING);
						step.setMode(mode);
						
						int startLat = (int) Double.parseDouble((String) xpath.evaluate("start_location/lat", stepNode, XPathConstants.STRING)) * 1000000;
						int startLng = (int) Double.parseDouble((String) xpath.evaluate("start_location/lng", stepNode, XPathConstants.STRING)) * 1000000;
						step.setStart(new GeoPoint(startLat, startLng));
						
						int endLat = (int) Double.parseDouble((String) xpath.evaluate("end_location/lat", stepNode, XPathConstants.STRING)) * 1000000;
						int endLng = (int) Double.parseDouble((String) xpath.evaluate("end_location/lng", stepNode, XPathConstants.STRING)) * 1000000;
						step.setEnd(new GeoPoint(endLat, endLng));
						
						String polyline = (String) xpath.evaluate("polyline/points", stepNode, XPathConstants.STRING);
						step.setPolyString(polyline);
						
						_leg.add(step);
					}
					
					directions.addLeg(_leg);
				}
				
				String overviewPolyString = (String) xpath.evaluate("route/overview_polyline/points", root, XPathConstants.STRING);
				directions.setOverviewPolyString(overviewPolyString);
				
				directions.setOverviewPolypoints(Util.decodePoly(directions.getOverviewPolyString()));
				
				return directions;
			} else {
				Log.e(TAG, String.format("google directions error -- status = ", status));
			}
		} catch (XPathExpressionException e) {
			throw new IOException("Failed parsing the Google Directions XML response.  Got an XPathExpressionException.");
		}
		
		return null;
	}
	
	/**
	 * We kindly borrowed this code from here: http://jeffreysambells.com/2010/05/27/decoding-polylines-from-google-maps-direction-api-with-java
	 *//*
	private List<LatLng> decodePoly(String encoded) {

		List<LatLng> poly = new ArrayList<LatLng>();
		int index = 0, len = encoded.length();
		int lat = 0, lng = 0;

		while (index < len) {
			int b, shift = 0, result = 0;
			do {
				b = encoded.charAt(index++) - 63;
				result |= (b & 0x1f) << shift;
				shift += 5;
			} while (b >= 0x20);
			int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
			lat += dlat;

			shift = 0;
			result = 0;
			do {
				b = encoded.charAt(index++) - 63;
				result |= (b & 0x1f) << shift;
				shift += 5;
			} while (b >= 0x20);
			int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
			lng += dlng;

			LatLng p = new LatLng(((double) lat / 1E5), ((double) lng / 1E5));
			poly.add(p);
		}

		return poly;
	}*/

	// ClassCastException coming from this area around here...
	public String buildDirectionsURL(List<RoutyAddress> addresses, boolean sensor) {
		StringBuffer url = new StringBuffer(AppConfig.G_DIRECTIONS_API_URL);
		
		if (addresses != null && addresses.size() > 0) {
			//Add the origin
			url.append("origin=");
			url.append(addresses.get(0).getLatitude());
			url.append(",");
			url.append(addresses.get(0).getLongitude());
			
			//Add destination (final destination)
			url.append("&destination=");
			url.append(addresses.get(addresses.size() - 1).getLatitude());
			url.append(",");
			url.append(addresses.get(addresses.size() - 1).getLongitude());
			
			if (addresses.size() > 2) {
				url.append("&waypoints=");
				
				//Add waypoints
				for (int i = 1; i < addresses.size() - 1; i++) {
//					url.append("optimize:true");		//DO NOT FUCKING UNCOMMENT THIS LINE!!!
					url.append(addresses.get(i).getLatitude());
					url.append(",");
					url.append(addresses.get(i).getLongitude());
					
					if (i != addresses.size() - 2) {
						url.append("|");
					}
				}
			}
			
			url.append("&sensor=");
			url.append(sensor);
			
			return url.toString();
		}
		
		return null;
	}
}
