package com.hit3.android.terrealted3.fragments;


import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.hit3.android.terrealted3.Constant;
import com.hit3.android.terrealted3.R;



public class ListRefugesAroundMeFragment extends Fragment {

	private final static String LOG_TAG = "ListRefugesAroundMeFragment";
	public final static String TASK_TAG = "ListRefugesAroundMeFragment";
	
	private Map<String, Double> refugesDistance;
	private ListView listView;
	private TextView textView;
	
	
	public static ListRefugesAroundMeFragment getInstance(Object... args){
		ListRefugesAroundMeFragment fragment = new ListRefugesAroundMeFragment();
		Map<String, Double> refugesDistance = (Map<String, Double>)args[0];
		ArrayList<String> arrayList = new ArrayList<String>();
		Iterator iterator = refugesDistance.entrySet().iterator();
    	while (iterator.hasNext()) {
    		Map.Entry mapEntry = (Map.Entry)iterator.next();
    		String key = (String)mapEntry.getKey();
    		Double value = (Double)mapEntry.getValue();
    		arrayList.add(key + Constant.SEPARATOR + value);
    	}
		Bundle bundle = new Bundle();
		bundle.putStringArrayList(Constant.BUNDLE_ITEM_REFUGES_DISTANCE, arrayList);
		fragment.setArguments(bundle);
		return fragment;
	}	
	
	
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_list_refuges_around_me, container, false);
        ArrayList<String> arrayList = getArguments().getStringArrayList(Constant.BUNDLE_ITEM_REFUGES_DISTANCE);
        textView = (TextView)rootView.findViewById(R.id.textView);
        textView.setText(getResources().getString(R.string.listrefugesaroundme_title));
		listView = (ListView)rootView.findViewById(R.id.listView);
		String[] strarray = arrayList.toArray(new String[0]); 
		listView.setAdapter(new ViewListArrayAdapter(getActivity(), strarray));
        return rootView;
    }
    
    
    
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
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
	 
			String tmp = values[position];
			String[] array = StringUtils.split(tmp, Constant.SEPARATOR);
			int nid = new Integer(array[0]).intValue();
			String quote = new String(array[1]);
			String title = new String(array[2]);
			double distance = new Double(array[3]).doubleValue();
			DecimalFormat df2 = new DecimalFormat("#.0");
			String distanceStr = df2.format(distance);
			View rowView = inflater.inflate(getResources().getIdentifier(LAYOUT, "layout", getActivity().getPackageName()), parent, false);
			rowView.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					int nid = (Integer)v.getTag();
					Fragment fragment = new DetailRefugeFragment().getInstance(nid);
					FragmentManager fragmentManager = getFragmentManager();
	    			fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).addToBackStack(null).commit();
				}
			});
			TextView textView1 = (TextView) rowView.findViewById(R.id.labell);
			textView1.setText(Html.fromHtml(title));
			TextView textView2 = (TextView) rowView.findViewById(R.id.label2);
			String text = getResources().getString(R.string.listrefugesaroundme_distance) + " " +distanceStr + "km";
			text += "   " + getResources().getString(R.string.listrefugesaroundme_quote) + " " + quote + "m s.l.m.";
			textView2.setText(text);
			
			progressBar = (ProgressBar)rowView.findViewById(R.id.idProgressBar);
			ImageView imageView = (ImageView) rowView.findViewById(R.id.logo);
			imageView.setImageBitmap(BitmapFactory.decodeResource(getActivity().getResources(), Constant.THUMBNAIL_IMAGE));
			
//			SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
//			if (sharedPrefs.getBoolean("prefEnableImageCacheModality", false)) {
//				String imageKey = StringUtils.substringAfterLast(array[1], "/");
//				String imageToken = StringUtils.substringAfterLast(imageKey, "?");
//				imageKey = StringUtils.substringBefore(imageKey, "?");
//				imageToken = StringUtils.remove(imageToken, "itok=");
//				new CachedImageTask(progressBar, imageView, getActivity()).execute(array[1], imageKey, imageToken, Constant.CACHE_IMAGE_TYPE_THUMBNAIL, Constant.CONTENT_TYPE.BOOK.toString().toLowerCase());
//			} else {
//				if (sharedPrefs.getBoolean("prefOffLineModality", false)) {
//					imageView.setImageBitmap(BitmapFactory.decodeResource(getActivity().getResources(), Constant.THUMBNAIL_IMAGE));
//				} else {
//					new DownloadImageTask(progressBar, imageView).execute(array[1]);
//				}
//			}
			
			rowView.setTag(nid);
			return rowView;
		}
	}	
	

	
}
