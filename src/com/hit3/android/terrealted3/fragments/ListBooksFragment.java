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
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.hit3.android.terrealted3.Constant;
import com.hit3.android.terrealted3.R;
import com.hit3.android.terrealted3.db.DataBaseConstants;
import com.hit3.android.terrealted3.db.DataBaseHelper;
import com.hit3.android.terrealted3.db.tables.BooksTable;
import com.hit3.android.terrealted3.tasks.CachedImageTask;
import com.hit3.android.terrealted3.tasks.ConnectionTask;
import com.hit3.android.terrealted3.tasks.DownloadImageTask;



public class ListBooksFragment extends Fragment {

	private final static String LOG_TAG = "ListBooksFragment";
	public final static String TASK_TAG = "ListBooksFragment";
	
	private final static String SERVICE = Constant.SERVICE_21;
	private final static String SERVICE_LIBRARY_FILTER = Constant.SERVICE_23;
	public final static BooksTable TABLE_REFERENCE = new BooksTable();
	
	
	private Handler handler;
	private ListView listView;
	
	private Spinner librarySpinner;
	private int librarySpinnerSelectionCount = 0;
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
	public static ListBooksFragment getInstance(Object... args){
		ListBooksFragment fragment = new ListBooksFragment();
		int idTitle = (Integer)args[0];
		Bundle bundle = new Bundle();
		bundle.putInt(Constant.BUNDLE_ITEM_ID_TITLE, idTitle);
		fragment.setArguments(bundle);
		return fragment;
	}	
	
	
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_items_books_list, container, false);
        int idTitle = getArguments().getInt(Constant.BUNDLE_ITEM_ID_TITLE);
        textView = (TextView)rootView.findViewById(R.id.textView);
        textView.setText(getResources().getString(R.string.elenco_libri));
        librarySpinner = (Spinner)rootView.findViewById(R.id.spinnerLibrary);
        populateColorSpinner(librarySpinner);
        
		listView = (ListView)rootView.findViewById(R.id.listView);
		
		SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
		boolean offLineModality = sharedPrefs.getBoolean("prefOffLineModality", false);
		if (offLineModality)
			populateFromDb();
		else
			populateFromWebServer(SERVICE);
        return rootView;
    }
    
    
    
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
	}	  
    
	
	
	private void populateFromWebServer(String webService) {
		String service = Constant.WEB_SERVER + webService;
		
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
				String library = node.has(TABLE_REFERENCE.library) ? node.getString(TABLE_REFERENCE.library) : ""; 
				if (StringUtils.isEmpty(library)) library = "[?]";
				array.add(title  + Constant.SEPARATOR +  imageUrl + Constant.SEPARATOR + nid + Constant.SEPARATOR + library + Constant.SEPARATOR + library);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return array;
	}    	
 

	
	private void populateFromDb() {
		SQLiteDatabase db = DataBaseHelper.getInstance(getActivity().getApplicationContext()).getWritableDatabase();
		Cursor cursor = db.rawQuery(DataBaseConstants.QUERY_GET_BOOKS, null);
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

	
	private void populateFromDbByColor(String library) {
		SQLiteDatabase db = DataBaseHelper.getInstance(getActivity().getApplicationContext()).getWritableDatabase();
		String[] selectionArgs = new String[]{library};
		Cursor cursor = db.rawQuery(DataBaseConstants.QUERY_GET_BOOKS_BY_LIBRARY, selectionArgs);
		listView.setAdapter(new ViewListCursorAdapter(getActivity(), cursor));
	}
	
	
	private void populateColorSpinner(Spinner spinner) {
		ArrayList<String> spinnerArray = new ArrayList<String>();
		spinnerArray.add("tutti");
		spinnerArray.add("Itinerari");
		spinnerArray.add("Manuali tecnici");
		spinnerArray.add("Guide naturalistiche");
		spinnerArray.add("Narrativa");
		spinnerArray.add("Fotografici");
		ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, spinnerArray);
		spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); 
		librarySpinner.setAdapter(spinnerArrayAdapter);
		spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
				if (librarySpinnerSelectionCount++ < 1) {
	                return;
	            }
				String selected = parent.getItemAtPosition(pos).toString();
				SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
				boolean offLineModality = sharedPrefs.getBoolean("prefOffLineModality", false);
				if (offLineModality) {
					if (StringUtils.equalsIgnoreCase(selected,  "tutti"))
						populateFromDb();
					else
						populateFromDbByColor(selected);
				} else {
					if (StringUtils.equalsIgnoreCase(selected,  "tutti"))
						populateFromWebServer(SERVICE);
					else
						populateFromWebServer(SERVICE_LIBRARY_FILTER + "/" + StringUtils.replace(selected, " ", "-"));
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
			}
		});
	}	
	
	
//////////////////////////////////////////////////////////////////////////////////////////////////
	
	public class ViewListArrayAdapter extends ArrayAdapter<String> {
		private final Context context;
		private final String[] values;
		
		public static final String LAYOUT = "items_list_row_2";
		private ProgressBar progressBar;
	 
		public ViewListArrayAdapter(Context context, String[] values) {
			super(context, getResources().getIdentifier(LAYOUT, "layout", getActivity().getPackageName()), values);
			this.context = context;
			this.values = values;
		}
		
	 
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	 
			String s = values[position];
			String[] array = StringUtils.split(s, Constant.SEPARATOR);
			int nid = new Integer(array[2]).intValue();
			View rowView = inflater.inflate(getResources().getIdentifier(LAYOUT, "layout", getActivity().getPackageName()), parent, false);
			rowView.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					int nid = (Integer)v.getTag();
					
					// meccanismo di chiamata verso la main activity passandoci i parametri attraverso il listner
					//Bundle bundle = new Bundle();
					//bundle.putString(Constant.BUNDLE_FRAGMENT_TAG, this.getClass().getName());
					//bundle.putInt(Constant.BUNDLE_NID, nid);
					//listner.onItemSelected(bundle);
					
					Fragment fragment = new DetailBookFragment().getInstance(nid);
					FragmentManager fragmentManager = getFragmentManager();
	    			fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).addToBackStack(null).commit();
				}
			});
			TextView textView1 = (TextView) rowView.findViewById(R.id.labell);
			textView1.setText(Html.fromHtml(array[0]));
			TextView textView2 = (TextView) rowView.findViewById(R.id.label2);
			textView2.setText(Html.fromHtml(array[3]));
			
			progressBar = (ProgressBar)rowView.findViewById(R.id.idProgressBar);
			ImageView imageView = (ImageView) rowView.findViewById(R.id.logo);
			SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
			if (sharedPrefs.getBoolean("prefEnableImageCacheModality", false)) {
				String imageKey = StringUtils.substringAfterLast(array[1], "/");
				String imageToken = StringUtils.substringAfterLast(imageKey, "?");
				imageKey = StringUtils.substringBefore(imageKey, "?");
				imageToken = StringUtils.remove(imageToken, "itok=");
				new CachedImageTask(progressBar, imageView, getActivity()).execute(array[1], imageKey, imageToken, Constant.CACHE_IMAGE_TYPE_THUMBNAIL, Constant.CONTENT_TYPE.BOOKS.toString().toLowerCase());
			} else {
				if (sharedPrefs.getBoolean("prefOffLineModality", false)) {
					imageView.setImageBitmap(BitmapFactory.decodeResource(getActivity().getResources(), Constant.THUMBNAIL_IMAGE));
				} else {
					new DownloadImageTask(progressBar, imageView).execute(array[1]);
				}
			}
			
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
			int libraryIndex = cursor.getColumnIndex(TABLE_REFERENCE.library);
			int image1Index = cursor.getColumnIndex(TABLE_REFERENCE.image1);
			int image2Index = cursor.getColumnIndex(TABLE_REFERENCE.image2);
			int image3Index = cursor.getColumnIndex(TABLE_REFERENCE.image3);
			int image4Index = cursor.getColumnIndex(TABLE_REFERENCE.image4);
			int image5Index = cursor.getColumnIndex(TABLE_REFERENCE.image5);
			view.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					int nid = (Integer)v.getTag();
					
					// meccanismo di chiamata verso la main activity passandoci i parametri attraverso il listner
					//Bundle bundle = new Bundle();
					//bundle.putString(Constant.BUNDLE_FRAGMENT_TAG, this.getClass().getName());
					//bundle.putInt(Constant.BUNDLE_NID, nid);
					//listner.onItemSelected(bundle);
					
					Fragment fragment = new DetailBookFragment().getInstance(nid);
					FragmentManager fragmentManager = getFragmentManager();
	    			fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).addToBackStack(null).commit();
				}
			});
			TextView textView1 = (TextView) view.findViewById(R.id.labell);
			textView1.setText(Html.fromHtml(cursor.getString(titleIndex)));
			TextView textView2 = (TextView) view.findViewById(R.id.label2);
			textView2.setText(cursor.getString(libraryIndex));
			
			ImageView imageView = (ImageView) view.findViewById(R.id.logo);
			SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
			if (sharedPrefs.getBoolean("prefEnableImageCacheModality", false)) {
				String imageKey = StringUtils.substringAfterLast(cursor.getString(image2Index), "/");
				String imageToken = StringUtils.substringAfterLast(imageKey, "?");
				imageKey = StringUtils.substringBefore(imageKey, "?");
				imageToken = StringUtils.remove(imageToken, "itok=");
				new CachedImageTask(null, imageView, getActivity()).execute(cursor.getString(image2Index), imageKey, imageToken, Constant.CACHE_IMAGE_TYPE_THUMBNAIL, Constant.CONTENT_TYPE.BOOKS.toString().toLowerCase());
			} else {
				if (sharedPrefs.getBoolean("prefOffLineModality", false)) {
					//imageView.setImageDrawable(getResources().getDrawable(Constant.THUMBNAIL_IMAGE));
					imageView.setImageBitmap(BitmapFactory.decodeResource(getActivity().getResources(), Constant.THUMBNAIL_IMAGE));
				} else {
					new DownloadImageTask(null, imageView).execute(cursor.getString(image2Index));
				}
			}
			
			
			view.setTag(cursor.getInt(nidIndex));
		}
		

	}	
	
}
