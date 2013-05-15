package org.routy.service;

import java.io.IOException;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.routy.exception.NoInternetConnectionException;
import org.routy.exception.RoutyException;
import org.routy.log.Log;
import org.routy.model.AppConfig;
import org.routy.model.GooglePlace;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;


/**
 * This class provides methods to interface with the Google Places API.
 * 
 * @author jtran
 *
 */
public class GooglePlacesService {

	
	private final String TAG = "GooglePlacesService";
	

	public GooglePlacesService() {
	}
	
	
	/**
	 * Number of results is limited to the max specified in AppProperties.
	 * 
	 * @param query
	 * @param centerLat
	 * @param centerLng
	 * @param radius
	 * @return
	 * @throws RoutyException
	 */
	public List<GooglePlace> getPlacesForKeyword(String query, Double centerLat, Double centerLng, int radius) throws RoutyException, NoInternetConnectionException {
		List<GooglePlace> results = new ArrayList<GooglePlace>();
		
		// We have to have a query
		if (query == null || query.length() == 0) {
			return results;
		}
		
		// Center is not required.  If they don't give us a center, we don't need a radius either.
		if (centerLat != null && centerLng != null) {
			if (radius < 0 || radius > 50000) {			// The limits on the radius are set by Google in their API docs
				return results;
			}
		}
		
		// Assemble the URL to get the Google Places result(s)
		StringBuffer placesUrl = new StringBuffer(AppConfig.G_PLACES_API_URL);
		placesUrl.append("key=");
		placesUrl.append(AppConfig.G_API_KEY);
		placesUrl.append("&query=");
		placesUrl.append(query.replaceAll(" ", "+"));
		
		if (centerLat != null && centerLng != null) {
			placesUrl.append("&location=");
			placesUrl.append(centerLat);
			placesUrl.append(",");
			placesUrl.append(centerLng);
			placesUrl.append("&radius=");
			placesUrl.append(radius);
		}

		placesUrl.append("&sensor=false");
		
		String xmlResp;
		xmlResp = getXMLResponse(placesUrl.toString());
		
		return parseGooglePlacesResponse(xmlResp);
	}
	
	
	private List<GooglePlace> parseGooglePlacesResponse(String xmlResp) throws RoutyException {
		List<GooglePlace> results = new ArrayList<GooglePlace>();
		
		if (xmlResp == null || xmlResp.length() == 0) {
			return results;
		}
		
		// Parse the Google Places xml response
		try {
			Log.v(TAG, "parsing Google Places response xml");
			XPath xpath = XPathFactory.newInstance().newXPath();
			String expr = "/";
			
			Node root = (Node) xpath.evaluate(expr, new InputSource(new StringReader(xmlResp)), XPathConstants.NODE);
			
			// Check the status first
			expr = "//PlaceSearchResponse/status";
			String status = (String) xpath.evaluate(expr, root, XPathConstants.STRING);
			Log.v(TAG, "Google Places response status=" + status);
			
			if (status.equalsIgnoreCase("ok")) {
				// Get the data from the response and build GooglePlace objects
				expr = "//PlaceSearchResponse/result";
				NodeList xmlResults = (NodeList) xpath.evaluate(expr, root, XPathConstants.NODESET);
				
				// Number of places results is capped
				for (int i = 0; i < Math.min(xmlResults.getLength(), AppConfig.G_PLACES_MAX_RESULTS); i++) {
					results.add(parseSingleResult(xmlResults.item(i)));
				}
				
			} else {
				Log.e(TAG, "BAD Google Places response status=" + status);
				throw new RoutyException("Google Places API status=" + status);
			}
			
		} catch (XPathExpressionException e) {
			Log.e(TAG, "XPathExpressionException while trying to parse Google Places API response.");
			throw new RoutyException("There was an internal problem looking up place names.");
		}
		
		Log.v(TAG, results.size() + " results");
		
		return results;
	}
	
	
	/**
	 * Parses a single <code>&lt;result&gt;</code> entry from the Google Places XML response.
	 * 
	 * @param resultNode
	 * @return
	 * @throws XPathExpressionException
	 */
	private GooglePlace parseSingleResult(Node resultNode) throws XPathExpressionException {
		XPath xpath = XPathFactory.newInstance().newXPath();
		String expr;
		GooglePlace place = new GooglePlace();
		
		if (resultNode != null) {
			expr = "name";
			place.setName((String) xpath.evaluate(expr, resultNode, XPathConstants.STRING));
			
			expr = "formatted_address";
			place.setFormattedAddress((String) xpath.evaluate(expr, resultNode, XPathConstants.STRING));
			
			expr = "geometry/location/lat";
			String lat = (String) xpath.evaluate(expr, resultNode, XPathConstants.STRING);
			if (lat != null && lat.length() > 0) {
				place.setLatitude(Double.parseDouble(lat));
			}
			
			expr = "geometry/location/lng";
			String lng = (String) xpath.evaluate(expr, resultNode, XPathConstants.STRING);
			if (lng != null && lng.length() > 0) {
				place.setLongitude(Double.parseDouble(lng));
			}
		}
		
		return place;
	}
	

	private String getXMLResponse(String url) throws RoutyException, NoInternetConnectionException {
		try {
			// Get response from Google Places API call
			URL u = new URL(url);
			Log.v(TAG, "Google Places API: " + u.toExternalForm());
			String xmlResp = InternetService.getStringResponse(u.toExternalForm());
			return xmlResp;
		} catch (MalformedURLException e) {
			Log.e(TAG, "Google Places URL is malformed");
			throw new RoutyException("There was an internal problem looking up place names.");
		} catch (IOException e) {
			Log.e(TAG, "IOException getting Google Places result");
			Log.e(TAG, "IOException message: " + e.getMessage());
			throw new RoutyException("There was an internal problem looking up place names.");
		} catch (NoInternetConnectionException e) {
			Log.e(TAG, "no internet connection");
			throw e;
		}
	}
	
}