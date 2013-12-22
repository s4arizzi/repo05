package com.hit3.android.terrealted3.fragments;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.hit3.android.terrealted3.Constant;
import com.hit3.android.terrealted3.R;
import com.hit3.android.terrealted3.db.DataBaseConstants;
import com.hit3.android.terrealted3.db.QueryBuilder;
import com.hit3.android.terrealted3.db.tables.RefugesTable;
import com.hit3.android.terrealted3.support.GPSTracker;
import com.hit3.android.terrealted3.tasks.ConnectionTask;
import com.hit3.android.terrealted3.utilities.BaseUtilities;
import com.hit3.android.terrealted3.utilities.DisplayUtilities;
import com.hit3.android.terrealted3.utilities.MapsUtilities;



public class RefugesAroundMeFragment extends Fragment {

	private final static String LOG_TAG = "RefugesAroundMeFragment";
	public final static String TASK_TAG = "RefugesAroundMeFragment";
	
	private final static String SERVICE = Constant.SERVICE_52;
	public final static RefugesTable TABLE_REFERENCE = new RefugesTable();
	private Handler handler;
	
	private GoogleMap map;
	private MapView mapView;
	private LatLng STARTING_POINT;
	double latitude;
    double longitude;
    private Map<String, LatLng> refugesLatLng;
    private Map<String, Double> refugesDistance;
	private double selectedRadius;
	
    private Spinner spinner1;
	private TextView textView;
	
	
	
	
	public static RefugesAroundMeFragment getInstance(Object... args){
		RefugesAroundMeFragment fragment = new RefugesAroundMeFragment();
		int idTitle = (Integer)args[0];
		Bundle bundle = new Bundle();
		bundle.putInt(Constant.BUNDLE_ITEM_ID_TITLE, idTitle);
		fragment.setArguments(bundle);
		return fragment;
	}	
	
	
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_refuges_around_me, container, false);
        int idTitle = getArguments().getInt(Constant.BUNDLE_ITEM_ID_TITLE);
        textView = (TextView)rootView.findViewById(R.id.textView);
//        spinner1 = (Spinner)rootView.findViewById(R.id.spinner1);
//        spinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//        	public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
//        		String[] spinnerValues = getResources().getStringArray(R.array.spinner_refuges_distance_values);
//        		selectedRadius = new Double(spinnerValues[pos]).doubleValue();
//    			Toast.makeText(parent.getContext(), "OnItemSelectedListener : " + parent.getItemAtPosition(pos).toString(), Toast.LENGTH_SHORT).show();
//    			//refugesLatLng = getRefugesLatLng();
//        	}
//
//    		@Override
//    		public void onNothingSelected(AdapterView<?> arg0) {
//    			// TODO Auto-generated method stub
//    		}	
//        });
        
        ImageButton imageButtonList = (ImageButton)rootView.findViewById(R.id.imageButtonList);
        imageButtonList.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
            	Fragment fragment = new ListRefugesAroundMeFragment().getInstance(refugesDistance);
        		FragmentManager fragmentManager = getFragmentManager();
        		fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).addToBackStack(null).commit();
            }
        });
        
        // Riferimento all'oggetto mappa
		mapView = (MapView)rootView.findViewById(R.id.mapGPS);
		mapView.onCreate(savedInstanceState);
		mapView.onResume();  //needed to get the map to display immediately
		try {
     		MapsInitializer.initialize(getActivity());
 		} catch (GooglePlayServicesNotAvailableException e) {
     		e.printStackTrace();
 		}
 		map = mapView.getMap();
        
 		selectedRadius = 1000.0;
 		//refugesLatLng = getRefugesLatLng();
 		spinner1 = (Spinner)rootView.findViewById(R.id.spinner1);
        spinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
        	public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        		String[] spinnerValues = getResources().getStringArray(R.array.spinner_refuges_distance_values);
        		selectedRadius = new Double(spinnerValues[pos]).doubleValue();
    			//Toast.makeText(parent.getContext(), "OnItemSelectedListener : " + parent.getItemAtPosition(pos).toString(), Toast.LENGTH_SHORT).show();
    			refugesLatLng = getRefugesLatLng();
        	}

    		@Override
    		public void onNothingSelected(AdapterView<?> arg0) {
    			// TODO Auto-generated method stub
    		}	
        });
 		
        return rootView;
    }

    
    
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
	}

	@Override
	public void onResume() {
		super.onResume();
		mapView.onResume();
	}
	
	@Override
	public void onPause() {
		super.onPause();
		mapView.onPause();
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		mapView.onDestroy();
	}
	
	@Override
	public void onLowMemory() {
		super.onLowMemory();
		mapView.onLowMemory();
	}
	
	
	private void getPosition() {
		GPSTracker gps = new GPSTracker(getActivity());
        if (gps.canGetLocation()) { // gps enabled} // return boolean true/false
        	//latitude = gps.getLatitude();
            //longitude = gps.getLongitude();
            try {
                Thread.sleep(1000);  // 1 secondi
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            latitude = gps.getLatitude();
            longitude = gps.getLongitude();
            Toast.makeText(getActivity(), getResources().getString(R.string.refugesaroundme_current_position)+ "\nLat: " + latitude + "\nLong: " + longitude, Toast.LENGTH_LONG).show(); 
            String latLon = MapsUtilities.convertDecimalDegreeIntoDegreesMinutesSeconds(latitude, longitude);
            textView.setGravity(Gravity.CENTER);
            textView.setText(latLon);
            gps.stopUsingGPS();
        } else {
        	gps.showSettingsAlert();
        }
	}
	
	
	private Map<String, LatLng> getRefugesLatLng() {
		Map<String, LatLng> refugesCoordinates = null;
		SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
		boolean offLineModality = sharedPrefs.getBoolean("prefOffLineModality", false);
		if (offLineModality) {
			populateFromDb();
		} else {
			populateFromWebServer();
		}	
		return refugesCoordinates;
	}
	
	
	
	
	private void populateFromWebServer() {
		String service = Constant.WEB_SERVER + SERVICE;
		
		handler = new Handler() {

			@Override
			public void handleMessage(Message msg) {
				String response = msg.obj.toString();
				refugesLatLng = parseRequest(response);
				show();
			}

		};
		new ConnectionTask(handler).execute(service);
	}
	
	private Map<String, LatLng> parseRequest(String response) {
		Map<String, LatLng> refugesCoordinates = new LinkedHashMap<String, LatLng>();
		try {
			JSONArray jsonArray = new JSONArray(response);
			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject jsonObject = jsonArray.getJSONObject(i);
				String nodeStr = jsonObject.getString("node");
				JSONObject node = new JSONObject(nodeStr);
				int nid = node.getInt(TABLE_REFERENCE.nid);
				String title = node.has(TABLE_REFERENCE.title) ? node.getString(TABLE_REFERENCE.title) : ""; 
				String quote = node.has(TABLE_REFERENCE.quote) ? node.getString(TABLE_REFERENCE.quote) : "0";
				String latStr = node.has(TABLE_REFERENCE.latitude) ? node.getString(TABLE_REFERENCE.latitude) : "";
				String lngStr = node.has(TABLE_REFERENCE.longitude) ? node.getString(TABLE_REFERENCE.longitude) : "";
				if (StringUtils.isNotEmpty(latStr) && StringUtils.isNotEmpty(lngStr)) {
					double lat = Double.parseDouble(latStr);
					double lng = Double.parseDouble(lngStr);
					String key = nid + Constant.SEPARATOR + quote + Constant.SEPARATOR + title;
					refugesCoordinates.put(key, new LatLng(lat, lng));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return refugesCoordinates;
	} 
	
	private void populateFromDb() {
		List<Map<String, String>> dataSet = QueryBuilder.getDataset(getActivity(), new RefugesTable(), DataBaseConstants.QUERY_GET_REFUGES, null);
		refugesLatLng = new LinkedHashMap<String, LatLng>();
		for (Map<String, String> map : dataSet) {
			String latStr = map.get(RefugesTable.latitude);
			String lngStr = map.get(RefugesTable.longitude);
			if (StringUtils.isNotEmpty(latStr) && StringUtils.isNotEmpty(lngStr)) {
				double lat = Double.parseDouble(latStr);
				double lng = Double.parseDouble(lngStr);
				String key = map.get(RefugesTable.nid) + Constant.SEPARATOR + map.get(RefugesTable.quote) + Constant.SEPARATOR + map.get(RefugesTable.title);
				refugesLatLng.put(key, new LatLng(lat, lng));
			}
		}
		show();
	}		

	
	private Map<String, Double> calculateRefugesDistance() {
		Map<String, LatLng> newRefugesLatLng = new LinkedHashMap<String, LatLng>();
        HashMap<String, Double> refugesDistance = new HashMap<String, Double>();
        Iterator iterator = refugesLatLng.entrySet().iterator();
    	while (iterator.hasNext()) {
    		Map.Entry mapEntry = (Map.Entry)iterator.next();
    		double lat = ((LatLng)mapEntry.getValue()).latitude;
    		double lon = ((LatLng)mapEntry.getValue()).longitude;
    		double distance = MapsUtilities.getDistanceFromLatLonInKm(latitude, longitude, lat, lon);
    		if (distance <= selectedRadius) {
    			refugesDistance.put(mapEntry.getKey().toString(), distance);
    			newRefugesLatLng.put(mapEntry.getKey().toString(), (LatLng)mapEntry.getValue());
    		} 
    	}
    	refugesLatLng = newRefugesLatLng;
    	return BaseUtilities.sortHashMap(refugesDistance);
	}

	
	private double calculateMaxDistance() {
		double result = 0;
		Iterator iterator = refugesDistance.entrySet().iterator();
    	while (iterator.hasNext()) {
    		Map.Entry mapEntry = (Map.Entry)iterator.next();
    		double distance = (Double)mapEntry.getValue();
    		if (result < distance)
    			result = distance;
    	}
		return result;
	}
	
	
	private LatLng calculateCenterPointOfMultipleCoordinate() {
		List<LatLng> coordList = new ArrayList<LatLng>();
		Iterator it = refugesLatLng.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry mapEntry = (Map.Entry)it.next();
			LatLng latLng = (LatLng)mapEntry.getValue();
			coordList.add(latLng);
		}
		LatLng center = MapsUtilities.getCentrePointFromListOfCoordinates(coordList);
		return center;
	}
	
	
	private void show() {
		// casa
        double latitudeCasa = 45.58583267;
        double longitudeCasa = 10.23768005;
        // ufficio
        double latitudeUff = 45.53069058;  // ufficio
        double longitudeUff = 10.23722804;
        
 		map.clear();
        
        getPosition();
        STARTING_POINT = new LatLng(latitude, longitude);
		
        if (map != null){
        	// Centrare la mappa sulle coordinate
        	map.moveCamera(CameraUpdateFactory.newLatLngZoom(STARTING_POINT, 5));
        	
        	// Animazione zoom
        	//map.animateCamera(CameraUpdateFactory.zoomTo(15), 2000, null);  // 2..21: non usare questo ma il posizionamento avanzato
        	
        	// Aggiunta marker su posizione corrente
//        	map.addMarker(new MarkerOptions().position(STARTING_POINT).title(getResources().getString(R.string.refugesaroundme_my_position))
//        			.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
        	
//        	Iterator iterator = refugesLatLng.entrySet().iterator();
//        	while (iterator.hasNext()) {
//        		Map.Entry mapEntry = (Map.Entry)iterator.next();
//        		String key = (String)mapEntry.getKey();
//        		String quote = StringUtils.split(key, Constant.SEPARATOR)[1];
//        		String title = StringUtils.split(key, Constant.SEPARATOR)[2];
//        		map.addMarker(new MarkerOptions().position((LatLng)mapEntry.getValue()).title(title + Constant.SEPARATOR + quote)
//            			.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));
//        	}
        	
        	// Aggiunta listener marker
        	map.setOnMarkerClickListener(
					new OnMarkerClickListener() {
						boolean doNotMoveCameraToCenterMarker = true;
						public boolean onMarkerClick(Marker marker) {
							map.setInfoWindowAdapter(new InfoWindowAdapter() {
								
								@Override
								public View getInfoWindow(Marker arg0) {
									return null;
								}
								
								@Override
								public View getInfoContents(Marker marker) {
									View v = getActivity().getLayoutInflater().inflate(R.layout.google_marker_snippet, null);
									TextView textViewTitle = (TextView) v.findViewById(R.id.mapMarkerSnippetInfoTitle);
									String title = StringUtils.substringBefore(marker.getTitle(), Constant.SEPARATOR);
									String quote = StringUtils.substringAfter(marker.getTitle(), Constant.SEPARATOR);
									textViewTitle.setText(title);
									if (StringUtils.isNotEmpty(quote))
										textViewTitle.setText(title + " " + quote + "m s.l.m.");
									TextView info = (TextView) v.findViewById(R.id.mapMarkerSnippetInfo);
									double lat = (Math.round(marker.getPosition().latitude * 100.0) / 100.0);  // serve per tagliare a 1 decimale
									double lon = (Math.round(marker.getPosition().longitude * 100.0) / 100.0);
									String latLon = MapsUtilities.convertDecimalDegreeIntoDegreesMinutesSeconds(lat, lon);
									info.setText("Lat: " + lat + ", Lon: " + lon + "\n" + latLon);
									return v;
								}
							});
							marker.showInfoWindow();
							
							return doNotMoveCameraToCenterMarker;
						}
					});
            	
            	// Controllo opzioni mappa
            	map.setMyLocationEnabled(true); // abilita pulsante mylocation
            	map.setTrafficEnabled(false);   // disabilita stato traffico
            	
            	// Posizionamento avanzato
            	int screenWidth = DisplayUtilities.getDisplayWidth(getActivity());
            	//float zoom = MapsUtilities.calculateZoomLevel(screenWidth, 40);
            	//float zoom = MapsUtilities.radiusToZoom(screenWidth, 10000);
            	//float zoom = MapsUtilities.zoomLevel(32);
            	refugesDistance = calculateRefugesDistance();
            	
            	Iterator iterator = refugesLatLng.entrySet().iterator();
            	while (iterator.hasNext()) {
            		Map.Entry mapEntry = (Map.Entry)iterator.next();
            		String key = (String)mapEntry.getKey();
            		String quote = StringUtils.split(key, Constant.SEPARATOR)[1];
            		String title = StringUtils.split(key, Constant.SEPARATOR)[2];
            		map.addMarker(new MarkerOptions().position((LatLng)mapEntry.getValue()).title(title + Constant.SEPARATOR + quote)
                			.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));
            	}
            	
            	// Aggiunta marker su posizione corrente
            	map.addMarker(new MarkerOptions().position(STARTING_POINT).title(getResources().getString(R.string.refugesaroundme_my_position))
            			.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
            	
            	float zoom = 13;
            	LatLng center = STARTING_POINT;
            	if (refugesDistance.size() > 0) {
            		zoom = MapsUtilities.zoomLevel(4*calculateMaxDistance());
            		center = calculateCenterPointOfMultipleCoordinate();
            	}	
            	//CameraPosition cameraPosition=new CameraPosition.Builder().target(STARTING_POINT).zoom(zoom).bearing(0).tilt(1).build();
            	CameraPosition cameraPosition=new CameraPosition.Builder().target(center).zoom(zoom).bearing(0).tilt(1).build();
            	map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            }			
	}



	
}
