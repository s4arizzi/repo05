package com.hit3.android.terrealted3.fragments;


import java.io.InputStream;
import java.util.ArrayList;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.hit3.android.terrealted3.Constant;
import com.hit3.android.terrealted3.MainActivity;
import com.hit3.android.terrealted3.R;
import com.hit3.android.terrealted3.R.id;
import com.hit3.android.terrealted3.R.layout;
import com.hit3.android.terrealted3.R.string;
import com.hit3.android.terrealted3.listners.OnItemSelectedFragmentListner;
import com.hit3.android.terrealted3.utilities.RestServerUtilities;

public class ItemsListFragment extends Fragment {

	private final static String LOG_TAG = "ItemsListFragment";
	public final static String TASK_TAG = "ItemsListFragment";
	
	private OnItemSelectedFragmentListner listner;
	
	private Handler handler;
	private ListView lista;
	
	private TextView textView;
	
	private MainActivity mainActivity;  // Riferimento all'Activity a cui questo Fragment e' legato
	
	
	public ItemsListFragment() {
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * Metodo statico di factory che permette di ottenere un riferimento ad
	 * un Fragment con le informazioni di primo dettaglio 
	 * 
	 * @param selectedIndex Indice elemento selezionato
	 * @param month Il mese di riferimento
	 * @param label La label associata
	 * @return Il Fragment associato al mese passato
	 */
	public static ItemsListFragment getInstance(int idTitle, String webService, String rowLayout){
		ItemsListFragment fragment = new ItemsListFragment();
		Bundle bundle = new Bundle();
		bundle.putInt(Constant.BUNDLE_ITEM_ID_TITLE, idTitle);
		bundle.putString(Constant.BUNDLE_WEB_SERVICE, webService);
		bundle.putString(Constant.BUNDLE_ROW_LAYOUT, rowLayout);
		fragment.setArguments(bundle);
		Log.i(LOG_TAG, "First Fragment created");
		return fragment;
	}	
	
	
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_items_list, container, false);
        //int index = getArguments().getInt(Constant.BUNDLE_ITEM_INDEX);
        int idTitle = getArguments().getInt(Constant.BUNDLE_ITEM_ID_TITLE);
        String webService = getArguments().getString(Constant.BUNDLE_WEB_SERVICE);
        final String rowLayout = getArguments().getString(Constant.BUNDLE_ROW_LAYOUT);
        
        textView = (TextView)rootView.findViewById(R.id.textView);
        String title = "Elenco";
        if (StringUtils.contains(webService, "rivista"))
        	title = getResources().getString(R.string.elenco_riviste);
        else if (StringUtils.contains(webService, "libro"))
        	title = getResources().getString(R.string.elenco_libri);
        else if (StringUtils.contains(webService, "cartina"))
        	title = getResources().getString(R.string.elenco_cartine);
        else if (StringUtils.contains(webService, "pdf"))
        	title = getResources().getString(R.string.elenco_pdf);
        else if (StringUtils.contains(webService, "rifugio"))
        	title = getResources().getString(R.string.elenco_rifugi);
        else if (StringUtils.contains(webService, "materiale"))
        	title = getResources().getString(R.string.elenco_materiale);
        else if (StringUtils.contains(webService, "canto"))
        	title = getResources().getString(R.string.elenco_canti);
        textView.setText(title);
        
        String service = Constant.WEB_SERVER + "/" + webService;
		lista = (ListView)rootView.findViewById(R.id.listView);
		handler = new Handler() {

			@Override
			public void handleMessage(Message msg) {
				String response = msg.obj.toString();
				ArrayList<String> array = parseRequest(response);
				String[] strarray = array.toArray(new String[0]); 
				
				lista.setAdapter(new ViewListArrayAdapter(getActivity(), strarray, rowLayout));
			}

		};
		//getActivity().setTitle("pippo");
		
		if (RestServerUtilities.isNetworkConnected(getActivity()))
			new Connection().execute(service);
		else {
			//Toast.makeText(getApplicationContext(), "Necessaria connessione verso internet.", Toast.LENGTH_SHORT).show();
			AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
	    	alert.setTitle("Connessione mancante"); 
	    	alert.setMessage("Necessaria connessione verso internet per continuare."); 
	    	alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
	    	public void onClick(DialogInterface dialog, int whichButton) {
	    		getActivity().finish();
	    		//dialog.cancel();
	    	  }
	    	});
	    	AlertDialog alertDialog = alert.create();
        	alertDialog.show();
		}
        return rootView;
    }
    
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		this.mainActivity = (MainActivity)activity;
		if (activity instanceof OnItemSelectedFragmentListner) {
			listner = (OnItemSelectedFragmentListner)activity;
		} else {
			throw new ClassCastException(activity.toString() + " must implement OnItemSelectedFragmentListner");	
		}
	}	  
    
    
	
    
	private ArrayList<String> parseRequest(String response) {
		ArrayList<String> array = new ArrayList<String>();
		try {
			JSONArray jsonArray = new JSONArray(response);
			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject jsonObject = jsonArray.getJSONObject(i);
				String nodeStr = jsonObject.getString("node");
				JSONObject node = new JSONObject(nodeStr);
				int nid = node.getInt("Nid");
				
				String title = "";
				String body = "...";
				String imageUrl = "";
				String label = "";
				if (node.has("Titolo"))
					title = node.getString("Titolo");
				if (node.has("Corpo"))
					body = node.getString("Corpo");
				if (node.has("Immagine"))
					imageUrl = node.getString("Immagine");
				if (StringUtils.isEmpty(imageUrl))
					imageUrl = Constant.WEB_SERVER + Constant.WEB_THUMBNAIL_IMAGE;
				if (node.has("Sottotitolo"))
					label = node.getString("Sottotitolo");
				array.add(title  + Constant.SEPARATOR +  imageUrl + Constant.SEPARATOR + nid + Constant.SEPARATOR + body + Constant.SEPARATOR + label);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return array;
	}    
 


	
	
//////////////////////////////////////////////////////////////////////////////////////////////////
	
	public class ViewListArrayAdapter extends ArrayAdapter<String> {
		private final Context context;
		private final String[] values;
		
		private String rowLayout;
		
		private ProgressBar progressBar;
	 
		public ViewListArrayAdapter(Context context, String[] values, String rowLayout) {
			super(context, getResources().getIdentifier(rowLayout, "layout", getActivity().getPackageName()), values);
			this.context = context;
			this.values = values;
			this.rowLayout = rowLayout;
		}
		
	 
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	 
			String s = values[position];
			String[] array = StringUtils.split(s, Constant.SEPARATOR);
			int nid = new Integer(array[2]).intValue();
			//View rowView = inflater.inflate(R.layout.items_list_row, parent, false);
			View rowView = inflater.inflate(getResources().getIdentifier(rowLayout, "layout", getActivity().getPackageName()), parent, false);
			rowView.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					int nid = (Integer)v.getTag();
					//TextView textView1 = ((TextView)v.findViewById(R.id.labell));
					//int nid = (Integer)textView1.getTag();
					//mainActivity.showNextDetail(nid, bibliotecaType);
					Bundle bundle = new Bundle();
					bundle.putString(Constant.BUNDLE_FRAGMENT_TAG, this.getClass().getName());
					bundle.putInt(Constant.BUNDLE_NID, nid);
					listner.onItemSelected(bundle);
				}
			});
			if (StringUtils.equalsIgnoreCase(rowLayout, "items_list_row_1")) {
				TextView textView1 = (TextView) rowView.findViewById(R.id.label1);
				textView1.setText(Html.fromHtml(array[0]));
				//textView1.setTag(nid);
				
				ImageView imageView = (ImageView) rowView.findViewById(R.id.logo);
				progressBar = (ProgressBar)rowView.findViewById(R.id.idProgressBar);
				new DownloadImageTask(progressBar, imageView).execute(array[1]);
			} if (StringUtils.equalsIgnoreCase(rowLayout, "items_list_row_2")) {
				TextView textView1 = (TextView) rowView.findViewById(R.id.labell);
				textView1.setText(Html.fromHtml(array[0]));
				//textView1.setTag(nid);
				TextView textView2 = (TextView) rowView.findViewById(R.id.label2);
				textView2.setText("");
				if (array.length == 5) {
					textView2.setText(Html.fromHtml(array[4]));
				} 
				//textView2.setTag(nid);
				
				ImageView imageView = (ImageView) rowView.findViewById(R.id.logo);
				progressBar = (ProgressBar)rowView.findViewById(R.id.idProgressBar);
				new DownloadImageTask(progressBar, imageView).execute(array[1]);
			} if (StringUtils.equalsIgnoreCase(rowLayout, "items_list_row_3")) {
				TextView textView1 = (TextView) rowView.findViewById(R.id.labell);
				textView1.setText(Html.fromHtml(array[0]));
				//textView1.setTag(nid);
				TextView textView2 = (TextView) rowView.findViewById(R.id.label2);
				textView2.setText("");
				if (array.length == 5) {
					textView2.setText(Html.fromHtml(array[4]));
				} 
				//textView2.setTag(nid);
			} else {
				
			}
			rowView.setTag(nid);
			return rowView;
		}
	}	
	
	
//////////////////////////////////////////////////////////////////////////////////////////////////
	
	private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
		ProgressBar mProgressBar;
	    ImageView mImage;

	    public DownloadImageTask(ProgressBar mProgressBar, ImageView mImage) {
	        this.mImage = mImage;
	        this.mProgressBar = mProgressBar;
	    }

	    
	    @Override
		protected void onPreExecute() {
			super.onPreExecute();
			mImage.setVisibility(View.INVISIBLE);
			
		}

	    @Override
		protected Bitmap doInBackground(String... urls) {
	        String urldisplay = urls[0];
	        Bitmap mIcon11 = null;
	        try {
	            InputStream in = new java.net.URL(urldisplay).openStream();
	            mIcon11 = BitmapFactory.decodeStream(in);
	        } catch (Exception e) {
	            Log.e("Error", e.getMessage());
	            e.printStackTrace();
	        }
	        return mIcon11;
	    }

	    @Override
	    protected void onPostExecute(Bitmap result) {
	    	mProgressBar.setVisibility(View.GONE);
	    	mImage.setVisibility(View.VISIBLE);
	        mImage.setImageBitmap(result);
	    }
	}


	
//////////////////////////////////////////////////////////////////////////////////////////////////
	
	public class Connection extends AsyncTask<String, Void, String> {

		
		@Override
		protected String doInBackground(String... args) {
			String service = args[0];
			String response = RestServerUtilities.readResponse(service);
			return response;
		}
		
		@Override
	    protected void onPostExecute(String result) {
			Message msg = Message.obtain();
            msg.obj = result;
            handler.sendMessage(msg);
	    }

	}
	
	
}
