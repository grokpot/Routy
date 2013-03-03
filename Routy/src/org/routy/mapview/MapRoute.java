package org.routy.mapview;

import java.util.ArrayList;

import android.os.Handler;

import com.google.android.maps.GeoPoint;

public class MapRoute {

	private GeoPoint startPoint 	= null;
	private GeoPoint endPoint 		= null;
	private ArrayList<GeoPoint> geoPoints = new ArrayList<GeoPoint>();
	private Handler haRoute 		= new Handler();
	
	public interface RouteListener {
	    public void onDetermined(ArrayList<GeoPoint> alPoint);
	    public void onError();
	}

	private RouteListener routeListener = null;

	public MapRoute(GeoPoint startPoint, GeoPoint endPoint){
	    this.startPoint	= startPoint;
	    this.endPoint 	= endPoint;
	}

	public void getPoints(RouteListener routeListener){
	    this.routeListener = routeListener;
	    new Thread(ruFetch).start();
	}
	
	private Runnable ruFetchOk = new Runnable(){
	    public void run(){
	        routeListener.onDetermined(geoPoints);
	    }
	};

	private Runnable ruFetchError = new Runnable(){
	    public void run(){
	        routeListener.onDetermined(geoPoints);
	    }
	};

	private Runnable ruFetch = new Runnable(){
	    public void run(){
	        String szUrl = "http://maps.googleapis.com/maps/api/directions/xml";
	        szUrl += "?origin=" + (startPoint.getLatitudeE6()/1e6) + "," + (startPoint.getLongitudeE6()/1e6);
	        szUrl += "&destination=" + (endPoint.getLatitudeE6()/1e6) + "," + (endPoint.getLongitudeE6()/1e6);
	        szUrl += "&sensor=true";

	        /*HttpClient oHttp = HttpClient.getInstance();
	        String szXml = oHttp.doGet(szUrl,"");

	        try{
	            XmlPullParserFactory xppfFactory = XmlPullParserFactory.newInstance();
	            xppfFactory.setNamespaceAware(true);
	            XmlPullParser xppParses = xppfFactory.newPullParser();

	            xppParses.setInput(new StringReader(szXml));
	            int iEventType = xppParses.getEventType();
	            String szTag = "";
	            String szText = "";
	            boolean bStep = false;
	            int iLat = 0;
	            int iLong = 0;

	            while(iEventType != XmlPullParser.END_DOCUMENT){     
	                 iEventType = xppParses.next();

	                 if(iEventType == XmlPullParser.START_TAG){
	                     szTag = xppParses.getName();

	                     if(szTag.equals("step"))
	                         bStep = true;
	                 }
	                 else if(iEventType == XmlPullParser.TEXT){  
	                     if(szTag.equals("points"))
	                         szText = "";
	                     else
	                         szText = xppParses.getText().trim();
	                 }          
	                 else if(iEventType == XmlPullParser.END_TAG){
	                     if(xppParses.getName().equals("step")){
	                         bStep = false;
	                     }
	                    else if(bStep && xppParses.getName().equals("start_location") || xppParses.getName().equals("end_location")){
	                         GeoPoint gpPoint = new GeoPoint(iLat,iLong);
	                         geoPoints.add(gpPoint);
	                     }
	                     else if(bStep && xppParses.getName().equals("lat")){
	                         iLat = (int)(Double.parseDouble(szText) * 1e6);
	                     }
	                     else if(bStep && xppParses.getName().equals("lng")){
	                         iLong = (int)(Double.parseDouble(szText) * 1e6);
	                     }
	                 }
	            }
	        }
	        catch(Exception e){
	            e.printStackTrace();
	            haRoute.post(ruFetchError);
	        }*/


	        if(geoPoints.size() == 0)
	            haRoute.post(ruFetchError);
	        else
	            haRoute.post(ruFetchOk);

	    }
	};
}

