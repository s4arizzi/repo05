package com.hit3.android.terrealted3.fragments;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.GradientDrawable.Orientation;
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
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.hit3.android.terrealted3.Constant;
import com.hit3.android.terrealted3.R;
import com.hit3.android.terrealted3.WebViewActivity;
import com.hit3.android.terrealted3.db.DataBaseConstants;
import com.hit3.android.terrealted3.db.DataBaseHelper;
import com.hit3.android.terrealted3.db.QueryBuilder;
import com.hit3.android.terrealted3.db.tables.AnimalsTable;
import com.hit3.android.terrealted3.db.tables.MarkersTable;
import com.hit3.android.terrealted3.tasks.CachedImageTask;
import com.hit3.android.terrealted3.tasks.ConnectionTask;
import com.hit3.android.terrealted3.tasks.DownloadImageTask;



public class DetailAnimalFragment extends Fragment {

	private final static String LOG_TAG = "DetailAnimalFragment";
	public final static String TASK_TAG = "DetailAnimalFragment";
	
	private final static String SERVICE = Constant.SERVICE_92;
	
	private Handler handler;
	private Integer nid;

	private TextView textViewTitle;
	//private TextView textViewChangedDate;
	private TextView textViewText;
	private TextView textViewScientificName;
	private TextView textViewFamily;
	private TextView textViewHabitat;
	private TextView textViewPresence;
	private TextView textViewAltitude;
	private TextView textViewHatching;
	private TextView textViewFeed;
	private TextView textViewReproduction;
	private TextView textViewLifetime;
	private TextView textViewFlight;
	private TextView textViewWeight;
	private TextView textViewActivity;
	private TextView textViewEthology;
	
	private ImageView imageViewGuarded;
	private ImageButton imageButtonMarker;
	private ImageButton imageButtonNext;
	
	private ImageView imageView;
	private ProgressBar progressBar;
	
	
	
	public DetailAnimalFragment() {
		// TODO Auto-generated constructor stub
	}
	
	
	public static DetailAnimalFragment getInstance(int nid){
		DetailAnimalFragment fragment = new DetailAnimalFragment();
		Bundle bundle = new Bundle();
		bundle.putInt(Constant.BUNDLE_NID, nid);
		fragment.setArguments(bundle);
		return fragment;
	}	
	
	
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_detail_animal, container, false);
        nid = getArguments().getInt(Constant.BUNDLE_NID);
		textViewTitle = (TextView)rootView.findViewById(R.id.textViewTitle);
		textViewTitle.setText("In progres ... (" + nid + ")");
		//textViewChangedDate = (TextView)rootView.findViewById(R.id.textViewChangedDate);
		textViewText = (TextView)rootView.findViewById(R.id.textViewText);
		textViewScientificName = (TextView)rootView.findViewById(R.id.textViewScientificName);
		textViewFamily = (TextView)rootView.findViewById(R.id.textViewFamily);
		textViewHabitat = (TextView)rootView.findViewById(R.id.textViewHabitat);
		textViewPresence = (TextView)rootView.findViewById(R.id.textViewPresence);
		textViewAltitude = (TextView)rootView.findViewById(R.id.textViewAltitude);
		textViewHatching = (TextView)rootView.findViewById(R.id.textViewHatching);
		textViewFeed = (TextView)rootView.findViewById(R.id.textViewFeed);
		textViewReproduction = (TextView)rootView.findViewById(R.id.textViewReproduction);
		textViewLifetime = (TextView)rootView.findViewById(R.id.textViewLifetime);
		textViewFlight = (TextView)rootView.findViewById(R.id.textViewFlight);
		textViewWeight = (TextView)rootView.findViewById(R.id.textViewWeight);
		textViewActivity = (TextView)rootView.findViewById(R.id.textViewActivity);
		textViewEthology = (TextView)rootView.findViewById(R.id.textViewEthology);
		imageViewGuarded = (ImageView)rootView.findViewById(R.id.imageViewGuarded);
		
		progressBar = (ProgressBar)rootView.findViewById(R.id.idProgressBarDetail);
		FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(10, 10);
        progressBar.setLayoutParams(params);
		
		imageView = (ImageView)rootView.findViewById(R.id.imageViewDetail);
		
		imageButtonMarker = (ImageButton)rootView.findViewById(R.id.imageButtonMarker);
		if (isMarkerSetted()) {
//			imageButtonMarker.setOnTouchListener(new OnTouchListener() {
//				@Override
//				public boolean onTouch(View v, MotionEvent event) {
//				 // show interest in events resulting from ACTION_DOWN
//				 if(event.getAction()==MotionEvent.ACTION_DOWN) return true;
//				 // don't handle event unless its ACTION_UP so "doSomething()" only runs once.
//				 if(event.getAction()!=MotionEvent.ACTION_UP) return false;
//				 imageButtonMarker.setPressed(true);                   
//				 return true;
//				}
//				});
			//imageButtonMarker.setPressed(true); 
			imageButtonMarker.setBackgroundDrawable(getResources().getDrawable(R.drawable.button_pressed));
		}
		imageButtonMarker.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
				toggleMarker(nid, Constant.CONTENT_TYPE.ANIMALS);
//				if (v.isPressed())
//					v.setPressed(false);
//				else
//					v.setPressed(true);
            }
        });
		
		imageButtonNext = (ImageButton)rootView.findViewById(R.id.imageButtonNext);
		imageButtonNext.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
				String pkg = getActivity().getPackageName();
				Intent intent = new Intent(getActivity(), WebViewActivity.class);
				intent.putExtra(Constant.EXTRAS_WEBVIEW, "info_animals");
				startActivityForResult(intent, Constant.ACTIVITY_WEBVIEW);
            }
        });
		
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
    
	
	private boolean isMarkerSetted() {
		int num = QueryBuilder.countRowsByWhere(getActivity(), MarkersTable.TABLE_NAME, "nid", nid+"");
		if (num == 0)
			return false;
		else
			return true;
	}
	
	
	private void toggleMarker(Integer nid, Constant.CONTENT_TYPE contentType) {
		boolean result = false;
		if (isMarkerSetted()) {
			Map<Integer, String> bindMap = new HashMap<Integer, String>();
			bindMap.put(1, nid+"");
			result = QueryBuilder.bindQuery(getActivity(), DataBaseConstants.QUERY_DELETE_MARKER, bindMap);
			//imageButtonMarker.setPressed(false);
			imageButtonMarker.setBackgroundDrawable(getResources().getDrawable(R.drawable.button_normal));
		} else {
			Map<Integer, String> bindMap = new HashMap<Integer, String>();
			bindMap.put(1, nid+"");
			bindMap.put(2, contentType.toString());
			result = QueryBuilder.bindQuery(getActivity(), DataBaseConstants.QUERY_INSERT_MARKER, bindMap);
			//imageButtonMarker.setPressed(true);
			imageButtonMarker.setBackgroundDrawable(getResources().getDrawable(R.drawable.button_pressed));
		}
	}
	
	
	private void populateFromWebServer() {
		String service = Constant.WEB_SERVER + SERVICE + "/" + nid;
		
		handler = new Handler() {

			@Override
			public void handleMessage(Message msg) {
				String response = msg.obj.toString();
				List<Map<String, String>> array = (new AnimalsTable()).parseRequest(response);
				show(array);
			}

		};
		new ConnectionTask(handler).execute(service);
	} 	
	
	
	private void populateFromDb() {
		SQLiteDatabase db = DataBaseHelper.getInstance(getActivity().getApplicationContext()).getWritableDatabase();
		String[] selectionArgs = new String[]{nid+""};   
		List<Map<String, String>> array = QueryBuilder.getDataset(getActivity(), new AnimalsTable(),  DataBaseConstants.QUERY_GET_ANIMAL, selectionArgs);
		show(array);
	}
	
	
	private void show(List<Map<String, String>> array) {
		if (array.size() == 1) {
			textViewTitle.setText(Html.fromHtml(array.get(0).get(AnimalsTable.title)));
			textViewTitle.setTag(array.get(0).get(AnimalsTable.url));
			textViewTitle.setOnClickListener(new View.OnClickListener() {
			    public void onClick(View view) {
			    	String url = view.getTag().toString();
			    	Intent i = new Intent(Intent.ACTION_VIEW);
			    	i.setData(Uri.parse(url));
			    	startActivity(i);
			    }
			});
			
			//textViewChangedDate.setText(getResources().getString(R.string.modificato) + " " + array.get(0).get(AnimalsTable.modify_date));
			textViewText.setText(Html.fromHtml(array.get(0).get(AnimalsTable.characteristics)));
			textViewScientificName.setText(Html.fromHtml(array.get(0).get(AnimalsTable.scientific_name)));
			textViewFamily.setText(Html.fromHtml(array.get(0).get(AnimalsTable.family)));
			
			textViewHabitat.setText(Html.fromHtml(array.get(0).get(AnimalsTable.habitat)));
			textViewPresence.setText(Html.fromHtml(array.get(0).get(AnimalsTable.presence)));
			textViewAltitude.setText(array.get(0).get(AnimalsTable.altitude_from) + " - " + array.get(0).get(AnimalsTable.altitude_to) + " m. s.l.m.");
			textViewHatching.setText(Html.fromHtml(array.get(0).get(AnimalsTable.hatching)));
			textViewFeed.setText(Html.fromHtml(array.get(0).get(AnimalsTable.feed)));
			textViewReproduction.setText(Html.fromHtml(array.get(0).get(AnimalsTable.reproduction)));
			textViewLifetime.setText(Html.fromHtml(array.get(0).get(AnimalsTable.lifetime)));
			textViewFlight.setText(Html.fromHtml(array.get(0).get(AnimalsTable.flight)));
			textViewWeight.setText(Html.fromHtml(array.get(0).get(AnimalsTable.weight)));
			textViewActivity.setText(Html.fromHtml(array.get(0).get(AnimalsTable.activity)));
			textViewEthology.setText(Html.fromHtml(array.get(0).get(AnimalsTable.ethology)));
			
			
			if (!StringUtils.isEmpty(array.get(0).get(AnimalsTable.guarded)))
				imageViewGuarded.setVisibility(View.VISIBLE);
			
			String image1 = array.get(0).get(AnimalsTable.image1);
			String image2 = array.get(0).get(AnimalsTable.image2);
			String image3 = array.get(0).get(AnimalsTable.image3);
			String image4 = array.get(0).get(AnimalsTable.image4);
			String image5 = array.get(0).get(AnimalsTable.image5);
			SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
			if (sharedPrefs.getBoolean("prefEnableImageCacheModality", false)) {
				String imageKey = StringUtils.substringAfterLast(image4, "/");
				String imageToken = StringUtils.substringAfterLast(imageKey, "?");
				imageKey = StringUtils.substringBefore(imageKey, "?");
				imageToken = StringUtils.remove(imageToken, "itok=");
				new CachedImageTask(null, imageView, getActivity()).execute(image5, imageKey, imageToken, Constant.CACHE_IMAGE_TYPE_CUSTOM, Constant.CONTENT_TYPE.ANIMALS.toString().toLowerCase());
			} else {
				if (sharedPrefs.getBoolean("prefOffLineModality", false)) {
					imageView.setImageBitmap(BitmapFactory.decodeResource(getActivity().getResources(), Constant.THUMBNAIL_IMAGE));
				} else {
					new DownloadImageTask(progressBar, imageView).execute(image5);
				}
			}
			
			imageView.setTag(image1);
			if (!sharedPrefs.getBoolean("prefOffLineModality", false)) {
				imageView.setOnClickListener(new View.OnClickListener() {
					
					@Override
					public void onClick(View v) {
						ImageView imageView = ((ImageView)v.findViewById(R.id.imageViewDetail));
						String imageName = (String)imageView.getTag();
						//Bundle bundle = new Bundle();
						//bundle.putString(Constant.BUNDLE_FRAGMENT_TAG, this.getClass().getName());
						//bundle.putString(Constant.BUNDLE_IMAGE_NAME, image);
						//listner.onItemSelected(bundle);
						
						Fragment fragment = new ImageFragment().getInstance(imageName);
						FragmentManager fragmentManager = getFragmentManager();
						fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).addToBackStack(null).commit();
						
						
					}
				});
			}
		}
	}
	
	private void fillTextView(TextView textView, int fillColor) {
		GradientDrawable gradient = new GradientDrawable(Orientation.BOTTOM_TOP, new int[] {fillColor, fillColor});
		gradient.setShape(GradientDrawable.RECTANGLE);
		gradient.setStroke(4, Color.parseColor("#C7C3BC"));
		//gradient.setCornerRadius(10.f);
		textView.setBackgroundDrawable(gradient);
	}
	
}
