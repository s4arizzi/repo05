package com.hit3.android.terrealted3.fragments;


import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

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
import com.hit3.android.terrealted3.db.DataBaseHelper;
import com.hit3.android.terrealted3.db.QueryBuilder;
import com.hit3.android.terrealted3.db.tables.MagazinesTable;
import com.hit3.android.terrealted3.db.tables.RefugesTable;
import com.hit3.android.terrealted3.tasks.ConnectionTask;
import com.hit3.android.terrealted3.utilities.EmailUtilities;
import com.hit3.android.terrealted3.utilities.MapsUtilities;

public class DetailRefugeFragment extends Fragment  {

	private final static String LOG_TAG = "DetailRefugeFragment";
	public final static String TASK_TAG = "DetailRefugeFragment";
	
	private final static String SERVICE = Constant.SERVICE_52;
	
	private Handler handler;
	private Integer nid;
	private TextView textViewTitle;
	private TextView textViewChangedDate;
	private TextView textViewTitoloGestore;
	private TextView textViewGestore;
	private TextView textViewTitoloLocalita;
	private TextView textViewLocalita;
	private TextView textViewTitoloComune;
	private TextView textViewComune;
	private TextView textViewTitoloProvincia;
	private TextView textViewProvincia;
	private TextView textViewTitoloNome;
	private TextView textViewNome;
	private TextView textViewTitoloTelefono;
	private TextView textViewTelefono;
	private Button buttonTelefono;
	private TextView textViewTitoloEmail;
	private TextView textViewEmail;
	private TextView textViewTitoloPostiPranzo;
	private TextView textViewPostiPranzo;
	private TextView textViewTitoloPostiLetto;
	private TextView textViewPostiLetto;
	private TextView textViewTitoloPostiLocInv;
	private TextView textViewPostiLocInv;
	private ImageView imageView;
	
	
	private LatLng STARTING_POINT = null;
	private GoogleMap map;
	
	private MapView mapView;
	//private Map<String, String> data;
	private String markerTitle;
	
	
	public DetailRefugeFragment() {
		// TODO Auto-generated constructor stub
	}
	
	public static DetailRefugeFragment getInstance(int nid){
		DetailRefugeFragment fragment = new DetailRefugeFragment();
		Bundle bundle = new Bundle();
		bundle.putInt(Constant.BUNDLE_NID, nid);
		fragment.setArguments(bundle);
		return fragment;
	}	
	
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_detail_refuge, container, false);
        nid = getArguments().getInt(Constant.BUNDLE_NID);
		textViewTitle = (TextView)rootView.findViewById(R.id.textViewTitle);
		textViewChangedDate = (TextView)rootView.findViewById(R.id.textViewChangedDate);
		textViewTitle.setText("In progres ... (" + nid + ")");
		textViewTitoloGestore = (TextView)rootView.findViewById(R.id.textViewTitoloGestore);
		textViewGestore = (TextView)rootView.findViewById(R.id.textViewGestore);
		textViewTitoloNome = (TextView)rootView.findViewById(R.id.textViewTitoloNome);
		textViewNome = (TextView)rootView.findViewById(R.id.textViewNome);
		textViewTitoloLocalita = (TextView)rootView.findViewById(R.id.textViewTitoloLocalita);
		textViewLocalita = (TextView)rootView.findViewById(R.id.textViewLocalita);
		textViewTitoloComune = (TextView)rootView.findViewById(R.id.textViewTitoloComune);
		textViewComune = (TextView)rootView.findViewById(R.id.textViewComune);
		textViewTitoloProvincia = (TextView)rootView.findViewById(R.id.textViewTitoloProvincia);
		textViewProvincia = (TextView)rootView.findViewById(R.id.textViewProvincia);
		textViewTitoloTelefono = (TextView)rootView.findViewById(R.id.textViewTitoloTelefono);
		textViewTelefono = (TextView)rootView.findViewById(R.id.textViewTelefono);
		buttonTelefono = (Button)rootView.findViewById(R.id.buttonTelefono);
		buttonTelefono.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// alert dialog
				AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
				String title = getResources().getString(R.string.dialog_call_phone_title) + " " + StringUtils.substringBefore(textViewTitle.getText().toString(), "(");
				alertDialogBuilder.setTitle(title);
				//alertDialogBuilder.setIcon(R.drawable.icon);
				alertDialogBuilder.setMessage(getResources().getString(R.string.dialog_call_phone_text) + ":\n" + textViewTelefono.getText() + "?");
				alertDialogBuilder.setCancelable(false);
				alertDialogBuilder.setPositiveButton(getResources().getString(R.string.button_yes) ,new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,int id) {
						Intent callIntent = new Intent(Intent.ACTION_CALL);
						callIntent.setData(Uri.parse("tel:" + textViewTelefono.getText()));
						startActivity(callIntent);
					}
				  });
				alertDialogBuilder.setNegativeButton(getResources().getString(R.string.button_no) ,new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,int id) {
						dialog.cancel();
					}
				});
				AlertDialog alertDialog = alertDialogBuilder.create();
				alertDialog.show();
			}
 
		});
		textViewTitoloEmail = (TextView)rootView.findViewById(R.id.textViewTitoloEmail);
		textViewEmail = (TextView)rootView.findViewById(R.id.textViewEmail);
		textViewEmail.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
        		//Toast.makeText(getActivity().getBaseContext(), textViewEmail.getText().toString(), Toast.LENGTH_LONG).show();
        		//sendEMail(textViewEmail.getText().toString().trim());
        		EmailUtilities.sendEmail(getActivity(), new String[]{textViewEmail.getText().toString().trim()}, "Richiesta informazioni", null);
            }
        });
		
		textViewTitoloPostiPranzo = (TextView)rootView.findViewById(R.id.textViewTitoloPostiPranzo);
		textViewPostiPranzo = (TextView)rootView.findViewById(R.id.textViewPostiPranzo);
		textViewTitoloPostiLetto = (TextView)rootView.findViewById(R.id.textViewTitoloPostiLetto);
		textViewPostiLetto = (TextView)rootView.findViewById(R.id.textViewPostiLetto);
		textViewTitoloPostiLocInv = (TextView)rootView.findViewById(R.id.textViewPostiTitoloLocInv);
		textViewPostiLocInv = (TextView)rootView.findViewById(R.id.textViewPostiLocInv);

		// Riferimento all'oggetto mappa ...
		mapView = (MapView)rootView.findViewById(R.id.mapGPS);
		mapView.onCreate(savedInstanceState);
		mapView.onResume();//needed to get the map to display immediately
		try {
     		MapsInitializer.initialize(getActivity());
 		} catch (GooglePlayServicesNotAvailableException e) {
     		e.printStackTrace();
 		}
 		map = mapView.getMap();
 		
 		SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
		boolean offLineModality = sharedPrefs.getBoolean("prefOffLineModality", false);
		if (offLineModality)
			populateFromDb();
		else
			populateFromWebServer();
		return rootView;
    }
    
    
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
	}	  
    
	
	@Override
	public void onDetach() {
		super.onDetach();
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
	
    

	
	private void populateFromWebServer() {
		String service = Constant.WEB_SERVER + SERVICE + "/" + nid;
		
		handler = new Handler() {

			@Override
			public void handleMessage(Message msg) {
				String response = msg.obj.toString();
				List<Map<String, String>> array = (new RefugesTable()).parseRequest(response);
				show(array);
			}

		};
		new ConnectionTask(handler).execute(service);
	} 
	
	
	private void populateFromDb() {
		SQLiteDatabase db = DataBaseHelper.getInstance(getActivity().getApplicationContext()).getWritableDatabase();
		String[] selectionArgs = new String[]{nid+""};   
		//List<Map<String, String>> array = (new RefugesTable()).getDataset(db, DataBaseConstants.QUERY_GET_REFUGE, selectionArgs);
		List<Map<String, String>> array = QueryBuilder.getDataset(getActivity(), new RefugesTable(),  DataBaseConstants.QUERY_GET_REFUGE, selectionArgs);
		show(array);
	}	
	

	private void show(List<Map<String, String>> array) {
		if (array.size() == 1) {
			textViewTitle.setText(array.get(0).get(MagazinesTable.title));
			textViewChangedDate.setText(getResources().getString(R.string.modificato) + " " + array.get(0).get(MagazinesTable.modify_date));
			
			textViewTitoloNome.setText(getResources().getString(R.string.rifugio_nome));
			textViewNome.setText(Html.fromHtml(array.get(0).get(RefugesTable.refuge_name)));      
			
			textViewTitoloGestore.setText(getResources().getString(R.string.rifugio_gestore));
			textViewGestore.setText(Html.fromHtml(array.get(0).get(RefugesTable.manager)));
			
			textViewTitoloLocalita.setText(getResources().getString(R.string.rifugio_localita));
			textViewLocalita.setText(Html.fromHtml(array.get(0).get(RefugesTable.location)));
			
			textViewTitoloComune.setText(getResources().getString(R.string.rifugio_comune));
			textViewComune.setText(Html.fromHtml(array.get(0).get(RefugesTable.city)));
			
			textViewTitoloProvincia.setText(getResources().getString(R.string.rifugio_provincia));
			textViewProvincia.setText(Html.fromHtml(array.get(0).get(RefugesTable.province)));
			
			textViewTitoloTelefono.setText(getResources().getString(R.string.rifugio_telefono));
			textViewTelefono.setText(Html.fromHtml(array.get(0).get(RefugesTable.phone1)));
			
			textViewTitoloEmail.setText(getResources().getString(R.string.rifugio_email));
			textViewEmail.setText(Html.fromHtml(array.get(0).get(RefugesTable.email)));
			
			textViewTitoloPostiPranzo.setText(getResources().getString(R.string.rifugio_posti_pranzo));
			textViewPostiPranzo.setText(Html.fromHtml(array.get(0).get(RefugesTable.lunch_places)));
			
			textViewTitoloPostiLetto.setText(getResources().getString(R.string.rifugio_posti_letto));
			textViewPostiLetto.setText(Html.fromHtml(array.get(0).get(RefugesTable.beds_places)));
			
			textViewTitoloPostiLocInv.setText(getResources().getString(R.string.rifugio_posti_locale_invernale));
			textViewPostiLocInv.setText(Html.fromHtml(array.get(0).get(RefugesTable.seats_winter_room)));
			
			textViewTitle.setText(array.get(0).get(RefugesTable.title) + "   (" + array.get(0).get(RefugesTable.quote) +  "m.  s.l.m.)");
			
			
			if (StringUtils.isNotEmpty(array.get(0).get(RefugesTable.latitude)) && StringUtils.isNotEmpty(array.get(0).get(RefugesTable.latitude))) {
				STARTING_POINT = new LatLng(new Double(array.get(0).get(RefugesTable.latitude)).doubleValue(), new Double(array.get(0).get(RefugesTable.longitude)).doubleValue());
				markerTitle = array.get(0).get(MagazinesTable.title);
				if (map != null) {
					// Centrare la mappa sulle coordinate
					map.moveCamera(CameraUpdateFactory.newLatLngZoom(STARTING_POINT, 5));
					
					// Aggiunta pin
					//map.addMarker(new MarkerOptions().position(STARTING_POINT).title(title)
					//    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_launcher)));
					map.addMarker(new MarkerOptions()
					.position(STARTING_POINT).title(array.get(0).get(RefugesTable.title))
					.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
					
					// Aggiunta listener marker
					//map.setOnMarkerClickListener(this);
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
											TextView title = (TextView) v.findViewById(R.id.mapMarkerSnippetInfoTitle);
											title.setText(markerTitle);
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
					map.setMyLocationEnabled(true); // centra sulla mia pos.
					map.setTrafficEnabled(false); // mostra stato traffico
					
					// Posizionamento avanzato
					CameraPosition cameraPosition=new CameraPosition.Builder().target(STARTING_POINT).zoom(10).bearing(0).tilt(1).build();
					map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));			
				}
			}
				
		}
			
	}	
	
	

	
}
