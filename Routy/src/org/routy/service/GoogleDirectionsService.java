package org.routy.service;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.routy.exception.RoutyException;
import org.routy.model.AppProperties;
import org.routy.model.GoogleDirections;
import org.routy.model.Step;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import android.util.Log;

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
	
	public GoogleDirections getDirections(GeoPoint start, GeoPoint end, boolean sensor) throws IOException, RoutyException {
		// build Directions URL...
		String url = buildDirectionsURL(start, end, sensor);
		
		// get the XML response from Google Directions
		String resp = InternetService.getStringResponse(url);
		
		// parse the response into a GoogleDirections object
		return parseGoogleDirectionsResponse(resp);
	}
	
	
	private GoogleDirections parseGoogleDirectionsResponse(String resp) throws IOException {
		if (resp == null || resp.length() == 0) {
//			Log.e(TAG, "no Google Directions response to parse");
			return null;
		}
		
		try {
			XPath xpath = XPathFactory.newInstance().newXPath();
			Node root = (Node) xpath.evaluate("//DirectionsResponse", new InputSource(new StringReader(resp)), XPathConstants.NODE);
			
			String status = xpath.evaluate("status", root);
			if (status.equalsIgnoreCase("ok")) {
				GoogleDirections directions = new GoogleDirections();
				
				List<Step> steps = new ArrayList<Step>();
				NodeList stepNodes = (NodeList) xpath.evaluate("route/leg/step", root, XPathConstants.NODESET);
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
					step.setPolyline(polyline);
					
					steps.add(step);
				}
				
				directions.setSteps(steps);
				
				return directions;
			} else {
//				Log.e(TAG, "google directions error -- status=" + status);
			}
		} catch (XPathExpressionException e) {
			throw new IOException("Failed parsing the Google Directions XML response.  Got an XPathExpressionException.");
		}
		
		return null;
	}

	public String buildDirectionsURL(GeoPoint start, GeoPoint end, boolean sensor) {
		StringBuffer url = new StringBuffer(AppProperties.G_DIRECTIONS_API_URL);
		
		if (start == null || end == null) {
			return null;
		} else {
			double startLat = start.getLatitudeE6() / E6;
			double startLng = start.getLongitudeE6() / E6;
			url.append("origin=");
			url.append(startLat);
			url.append(",");
			url.append(startLng);
			
			double endLat = end.getLatitudeE6() / E6;
			double endLng = end.getLongitudeE6() / E6;
			url.append("&destination=");
			url.append(endLat);
			url.append(",");
			url.append(endLng);
			
			url.append("&sensor=");
			url.append(sensor ? "true" : "false");
			
//			Log.v(TAG, "directions url: " + url.toString());
			return url.toString();
		}
	}
}
