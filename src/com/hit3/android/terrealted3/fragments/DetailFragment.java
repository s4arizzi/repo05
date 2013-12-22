package com.hit3.android.terrealted3.fragments;


import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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

public class DetailFragment extends Fragment {

	private final static String LOG_TAG = "DetailFragment";
	public final static String TASK_TAG = "DetailFragment";
	
	private OnItemSelectedFragmentListner listner;
	private MainActivity mainActivity;
	
	private Integer nid;
	private String extra;
	private int bibliotecaType;

	private TextView textViewTitle;
	private TextView textViewCreationDate;
	private TextView textViewChangedDate;
	private TextView textViewText;
	private ImageView imageView;
	private ProgressBar progressBar;
	
	
	
	
	
	public DetailFragment() {
		// TODO Auto-generated constructor stub
	}
	
	public static DetailFragment getInstance(int nid, String extra){
		DetailFragment fragment = new DetailFragment();
		Bundle bundle = new Bundle();
		bundle.putInt(Constant.BUNDLE_NID, nid);
		bundle.putString(Constant.BUNDLE_EXTRA, extra);
		fragment.setArguments(bundle);
		Log.i(LOG_TAG, "First Fragment created");
		return fragment;
	}	
	
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        nid = getArguments().getInt(Constant.BUNDLE_NID);
        extra = getArguments().getString(Constant.BUNDLE_EXTRA);
        
        //String name = "";
        //String service = "http://" + Constant.DEFAULT_WEB_SERVER;

		//getActivity().setProgressBarIndeterminateVisibility(true);

		
		textViewTitle = (TextView)rootView.findViewById(R.id.textViewTitle);
		textViewCreationDate = (TextView)rootView.findViewById(R.id.textViewCreationDate);
		textViewChangedDate = (TextView)rootView.findViewById(R.id.textViewChangedDate);
		textViewText = (TextView)rootView.findViewById(R.id.textViewText);
		textViewTitle.setText("In progres ... (" + nid + ")");
		imageView = (ImageView)rootView.findViewById(R.id.imageViewDetail);

		String url = Constant.WEB_SERVER + "/json/node/" + nid;

		AsyncTask<String, Void, Map<String, Object>> simpleGetTask = new AsyncTask<String, Void, Map<String, Object>>() {
			@Override
			protected Map<String, Object> doInBackground(String... params) {
				String url = params[0];
				RestTemplate rest = new RestTemplate();
				MappingJackson2HttpMessageConverter jacksonConverter = new MappingJackson2HttpMessageConverter();
				rest.getMessageConverters().add(jacksonConverter);
//				Map<String, String> request = new HashMap<String, String>();
//				request.put("username", username);
//				request.put("password", password);
//				Map<String, Object> loginResult = rest.postForObject("http://www.siletto.it/json/user/login", request, HashMap.class);
//				String sessid = (String) loginResult.get("sessid");
//				String session_name = (String) loginResult.get("session_name");
//				CookieCache.clearCache();
//				CookieCache.storeObjectInCache("cookie", session_name + "="+ sessid);
				Map<String, Object> nodes = rest.getForObject(url, HashMap.class);
				return nodes;

			}

			@Override
			protected void onPostExecute(Map<String, Object> result) {
				SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy hh:mm");
				textViewTitle.setText(result.get("title").toString());
				String formattedDate = df.format(new Date(new Long(result.get("created").toString()).longValue() * 1000));
				textViewCreationDate.setText(getResources().getString(R.string.creato) + " " + formattedDate);
				formattedDate = df.format(new Date(new Long(result.get("changed").toString()).longValue() * 1000));
				textViewChangedDate.setText(getResources().getString(R.string.modificato) + " " + formattedDate);

				String immagineUrl = "";
				Map<String, String> immagineNode = parseUndNode(result, "field_biblioteca_immagine");
				if (null != immagineNode) {
					immagineUrl = immagineNode.get("uri");
					immagineUrl = StringUtils.remove(immagineUrl, "public://");
					//immagineUrl = StringUtils.replace(immagineUrl, " ", "%20");
					//ImageView imageView = (ImageView)findViewById(R.id.imageViewDetail);
					//progressBar = (ProgressBar)findViewById(R.id.idProgressBarDetail);
					String imageUrl = Constant.WEB_SERVER + "/sites/default/files/styles/scala_height_300/public/" + immagineUrl;
					imageUrl = RestServerUtilities.urlEncode(imageUrl);
					new DownloadImageTask(progressBar, imageView).execute(imageUrl);
					imageView.setTag(immagineUrl);
					imageView.setOnClickListener(new View.OnClickListener() {
						
						@Override
						public void onClick(View v) {
							ImageView imageView = ((ImageView)v.findViewById(R.id.imageViewDetail));
							String image = (String)imageView.getTag();
							//Intent intent = new Intent(ShowDetailActivity.this, ShowImageActivity.class);
							//String pkg = getPackageName();
							//intent.putExtra(Constant.EXTRAS_NID, nid);
							//intent.putExtra(Constant.EXTRAS_WEB_SERVER, webServer);
							//intent.putExtra(Constant.EXTRAS_IMAGE_URL, image);
							//startActivityForResult(intent, Constant.ACTIVITY_SHOW_IMAGE);
							Bundle bundle = new Bundle();
							bundle.putString(Constant.BUNDLE_FRAGMENT_TAG, this.getClass().getName());
							bundle.putString(Constant.BUNDLE_IMAGE_NAME, image);
							listner.onItemSelected(bundle);
						}
					});
				} 
				
				int bibliotecaType = new Integer(extra).intValue();
				switch (bibliotecaType) {
					case 1:
						String periodo = "";
						Map<String, String> periodoNode = parseUndNode(result, "field_rivista_periodo");
						if (null != periodoNode)
							periodo = periodoNode.get("value");
						textViewText.setText(periodo);
						break;
					case 2:
						String sottotiltolo = "";
						Map<String, String> bodyNode = parseUndNode(result, "body");
						if (null != bodyNode)
							sottotiltolo = bodyNode.get("value");
						String autori = "";
						Map<String, String> autoriNode = parseUndNode(result, "field_libro_autore");
						if (null != autoriNode)
							autori = autoriNode.get("value");
						textViewText.setText(Html.fromHtml(sottotiltolo) + "\n" + autori);
						break;
					case 3:
						String numero = "";
						Map<String, String> numeroNode = parseUndNode(result, "field_cartina_numero");
						if (null != numeroNode)
							numero = numeroNode.get("value");
						int scala = 0;
						Map<String, String> scalaNode = parseUndNode(result, "field_cartina_scala_topografica");
						if (null != scalaNode)
							scala = new Integer(scalaNode.get("tid")).intValue();
						String scalaTopografica = "";
						switch (scala) {
							case 41:
								scalaTopografica = Constant.TAXONOMY_SCALA_TOPOGRAFICA_41;
								break;
							case 42:
								scalaTopografica = Constant.TAXONOMY_SCALA_TOPOGRAFICA_42;
								break;
							case 43:
								scalaTopografica = Constant.TAXONOMY_SCALA_TOPOGRAFICA_43;
								break;
						}
						String inserto = "";
						Map<String, String> insertoNode = parseUndNode(result, "field_cartina_inserto");
						if (null != insertoNode)
							inserto = insertoNode.get("value");
						textViewText.setText("numero: " + numero + "   scala: " + scalaTopografica + "   inserto: " + inserto);
						break;
					case 4:
						break;	
				}
				
				//getActivity().setProgressBarIndeterminateVisibility(false);
			}
		};
		simpleGetTask.execute(url);
        
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
    
    
	private boolean isNetworkConnected() {
		ConnectivityManager cm = (ConnectivityManager)getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo ni = cm.getActiveNetworkInfo();
		if (ni == null) {
			// There are no active networks.
			return false;
		} else
			return true;
	}    

    
	private Map<String, String> parseUndNode(Map<String, Object> node, String key) {
		String value = node.get(key).toString();
		if (StringUtils.equalsIgnoreCase(value, "[]"))
			return null;
		value = value.replace("{und=[{", "");
		value = value.replace("}]}", "");
		String[] array = StringUtils.split(value, ",");
		Map<String, String> result = new HashMap<String, String>();
		for (String item : array) {
			String[] items = StringUtils.split(item, "=");
			if (items.length == 1)
				result.put(items[0].trim(), "");
			if (items.length == 2)
				result.put(items[0].trim(), items[1].trim());
		}
		return result;
	}
 

	
//////////////////////////////////////////////////////////////////////////////////////////////////
	
	private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
		//ProgressBar mProgressBar;
	    ImageView mImage;

	    public DownloadImageTask(ProgressBar mProgressBar, ImageView mImage) {
	        this.mImage = mImage;
	        //this.mProgressBar = mProgressBar;
	    }

	    
	    @Override
		protected void onPreExecute() {
			super.onPreExecute();
			//mImage.setVisibility(View.INVISIBLE);
			
		}

	    @Override
		protected Bitmap doInBackground(String... urls) {
	        String urldisplay = urls[0];
	        urldisplay = RestServerUtilities.urlEncode(urldisplay);
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
	    	//mProgressBar.setVisibility(View.GONE);
	    	//mImage.setVisibility(View.VISIBLE);
	        mImage.setImageBitmap(result);
	    }
	}

	
	
}
