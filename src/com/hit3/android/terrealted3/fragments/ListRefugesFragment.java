package com.hit3.android.terrealted3.fragments;


import java.util.ArrayList;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.hit3.android.terrealted3.Constant;
import com.hit3.android.terrealted3.MainActivity;
import com.hit3.android.terrealted3.R;
import com.hit3.android.terrealted3.db.DataBaseConstants;
import com.hit3.android.terrealted3.db.DataBaseHelper;
import com.hit3.android.terrealted3.db.tables.MapsTable;
import com.hit3.android.terrealted3.db.tables.RefugesTable;
import com.hit3.android.terrealted3.listners.OnItemSelectedFragmentListner;
import com.hit3.android.terrealted3.tasks.ConnectionTask;
import com.hit3.android.terrealted3.tasks.DownloadImageTask;



public class ListRefugesFragment extends Fragment {

	private final static String LOG_TAG = "ListRefugesFragment";
	public final static String TASK_TAG = "ListRefugesFragment";
	
	private final static String SERVICE = Constant.SERVICE_51;
	public final static RefugesTable TABLE_REFERENCE = new RefugesTable();
	
	
	private Handler handler;
	private ListView listView;
	
	private TextView textView;
	
	
	
	
	/**
	 * Metodo statico di factory che permette di ottenere un riferimento ad
	 * un Fragment con le informazioni di primo dettaglio 
	 * 
	 * @param selectedIndex Indice elemento selezionato
	 * @param month Il mese di riferimento
	 * @param label La label associata
	 * @return Il Fragment associato al mese passato
	 */
	public static ListRefugesFragment getInstance(Object... args){
		ListRefugesFragment fragment = new ListRefugesFragment();
		int idTitle = (Integer)args[0];
		Bundle bundle = new Bundle();
		bundle.putInt(Constant.BUNDLE_ITEM_ID_TITLE, idTitle);
		fragment.setArguments(bundle);
		return fragment;
	}	
	
	
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_items_list, container, false);
        int idTitle = getArguments().getInt(Constant.BUNDLE_ITEM_ID_TITLE);
        textView = (TextView)rootView.findViewById(R.id.textView);
        textView.setText(getResources().getString(R.string.elenco_rifugi));
		listView = (ListView)rootView.findViewById(R.id.listView);
		
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
    
	
	
	private void populateFromWebServer() {
		String service = Constant.WEB_SERVER + SERVICE;
		
		handler = new Handler() {

			@Override
			public void handleMessage(Message msg) {
				String response = msg.obj.toString();
				ArrayList<String> array = parseRequest(response);
				String[] strarray = array.toArray(new String[0]); 
				
				listView.setAdapter(new ViewListArrayAdapter(getActivity(), strarray));
			}

		};
		new ConnectionTask(handler).execute(service);
	}
	
	
	private ArrayList<String> parseRequest(String response) {
		ArrayList<String> array = new ArrayList<String>();
		try {
			JSONArray jsonArray = new JSONArray(response);
			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject jsonObject = jsonArray.getJSONObject(i);
				String nodeStr = jsonObject.getString("node");
				JSONObject node = new JSONObject(nodeStr);
				int nid = node.getInt(TABLE_REFERENCE.nid);
				String title = node.has(TABLE_REFERENCE.title) ? node.getString(TABLE_REFERENCE.title) : ""; 
				String imageUrl = node.has(TABLE_REFERENCE.image2) ? node.getString(TABLE_REFERENCE.image2) : ""; 
				if (StringUtils.isEmpty(imageUrl))
					imageUrl = Constant.WEB_SERVER + Constant.WEB_THUMBNAIL_IMAGE;
				String refuge_type = node.has(TABLE_REFERENCE.refuge_type) ? node.getString(TABLE_REFERENCE.refuge_type) : "";
				String quote = node.has(TABLE_REFERENCE.quote) ? node.getString(TABLE_REFERENCE.quote) : ""; 
				array.add(title  + Constant.SEPARATOR +  imageUrl + Constant.SEPARATOR + nid + Constant.SEPARATOR + refuge_type + Constant.SEPARATOR + quote);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return array;
	}    	
 

	
	private void populateFromDb() {
		SQLiteDatabase db = DataBaseHelper.getInstance(getActivity().getApplicationContext()).getWritableDatabase();
		Cursor cursor = db.rawQuery(DataBaseConstants.QUERY_GET_REFUGES, null);
		if (cursor.getCount() == 0) {
			new AlertDialog.Builder(getActivity())
		    .setTitle(getResources().getString(R.string.dialog_title_warning))
		    .setMessage(getResources().getString(R.string.dialog_assenza_di_record_nel_db))
		    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
		        public void onClick(DialogInterface dialog, int which) { 
		        	 getActivity().onBackPressed();
		        }
		     })
		     .show();
		}		
		listView.setAdapter(new ViewListCursorAdapter(getActivity(), cursor));
	}	

	
	
//////////////////////////////////////////////////////////////////////////////////////////////////
	
	public class ViewListArrayAdapter extends ArrayAdapter<String> {
		private final Context context;
		private final String[] values;
		
		
		private ProgressBar progressBar;
	 
		public ViewListArrayAdapter(Context context, String[] values) {
			super(context, getResources().getIdentifier("items_list_row_2", "layout", getActivity().getPackageName()), values);
			this.context = context;
			this.values = values;
		}
		
	 
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	 
			String s = values[position];
			String[] array = StringUtils.split(s, Constant.SEPARATOR);
			int nid = new Integer(array[2]).intValue();
			View rowView = inflater.inflate(getResources().getIdentifier("items_list_row_2", "layout", getActivity().getPackageName()), parent, false);
			rowView.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					int nid = (Integer)v.getTag();
					
					// meccanismo di chiamata verso la main activity passandoci i parametri attraverso il listner
					//Bundle bundle = new Bundle();
					//bundle.putString(Constant.BUNDLE_FRAGMENT_TAG, this.getClass().getName());
					//bundle.putInt(Constant.BUNDLE_NID, nid);
					//listner.onItemSelected(bundle);
					
					Fragment fragment = new DetailRefugeFragment().getInstance(nid);
					FragmentManager fragmentManager = getFragmentManager();
	    			fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).addToBackStack(null).commit();
				}
			});
			TextView textView1 = (TextView) rowView.findViewById(R.id.labell);
			textView1.setText(Html.fromHtml(array[0]));
			TextView textView2 = (TextView) rowView.findViewById(R.id.label2);
			textView2.setText("");
			if (array.length == 5) {
				textView2.setText(Html.fromHtml(array[4]));
			} 
			
			ImageView imageView = (ImageView) rowView.findViewById(R.id.logo);
			progressBar = (ProgressBar)rowView.findViewById(R.id.idProgressBar);
			new DownloadImageTask(progressBar, imageView).execute(array[1]);
			rowView.setTag(nid);
			return rowView;
		}
	}	
	

	
//////////////////////////////////////////////////////////////////////////////////////////////////	
	
	private class ViewListCursorAdapter extends CursorAdapter {
		
		public static final int LAYOUT_ID = R.layout.items_list_row_2;
		
		
		
		public ViewListCursorAdapter(Context context, Cursor cursor) {
			super(context, cursor);
		}

		
		@Override
		public View newView(Context context, Cursor cursor, ViewGroup parent) {
			LayoutInflater li = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	        return li.inflate(LAYOUT_ID, parent, false);
		}

		
		@Override
		public void bindView(View view, Context context, Cursor cursor) {
			int nidIndex = cursor.getColumnIndex(TABLE_REFERENCE.nid);
			int titleIndex = cursor.getColumnIndex(TABLE_REFERENCE.title);
			int quoteIndex = cursor.getColumnIndex(TABLE_REFERENCE.quote);
			int image1Index = cursor.getColumnIndex(TABLE_REFERENCE.image1);
			view.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					int nid = (Integer)v.getTag();
					
					// meccanismo di chiamata verso la main activity passandoci i parametri attraverso il listner
					//Bundle bundle = new Bundle();
					//bundle.putString(Constant.BUNDLE_FRAGMENT_TAG, this.getClass().getName());
					//bundle.putInt(Constant.BUNDLE_NID, nid);
					//listner.onItemSelected(bundle);
					
					Fragment fragment = new DetailRefugeFragment().getInstance(nid);
					FragmentManager fragmentManager = getFragmentManager();
	    			fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).addToBackStack(null).commit();
				}
			});
			TextView textView1 = (TextView) view.findViewById(R.id.labell);
			textView1.setText(Html.fromHtml(cursor.getString(titleIndex)));
			TextView textView2 = (TextView) view.findViewById(R.id.label2);
			textView2.setText(cursor.getString(quoteIndex));
			ImageView imageView = (ImageView) view.findViewById(R.id.logo);
			imageView.setImageDrawable(getResources().getDrawable(Constant.THUMBNAIL_IMAGE));
			view.setTag(cursor.getInt(nidIndex));
		}
		

	}	
	
}
