package com.hit3.android.terrealted3.fragments;

import java.io.IOException;
import java.io.InputStream;

import com.hit3.android.terrealted3.R;
import com.hit3.android.terrealted3.R.id;
import com.hit3.android.terrealted3.R.layout;

import android.app.Activity;
import android.app.Fragment;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ContentFragment extends Fragment {

	
	private LinearLayout linearLayout;
	private TextView textView;
	private ImageView mImage;
	
	
	
	public ContentFragment() {
		// TODO Auto-generated constructor stub
	}
	
	
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_content, container, false);
        //int i = getArguments().getInt(MENU_ITEM_NUMBER);
        //String name = getResources().getStringArray(R.array.menu_items1)[i]; 
        
        linearLayout = (LinearLayout)rootView.findViewById(R.id.content_layout);
        textView = (TextView)rootView.findViewById(R.id.contentText);
        //textView.setText("Benvenuto apri il menu di sx.");
        mImage = (ImageView)rootView.findViewById(R.id.imageView);
        loadDataFromAsset();
        
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
		setBackground();
	}
	
	@Override
	public void onPause() {
		super.onPause();
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
	}
	
	@Override
	public void onLowMemory() {
		super.onLowMemory();
	}
    
    
    
    private void setBackground() {
    	SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
		String drawableBackground = sharedPrefs.getString("prefSyncBackgrounds", "background_repeat5");
		int drawableResourceId = this.getResources().getIdentifier(drawableBackground, "drawable", getActivity().getPackageName());
		Drawable drawable = this.getResources().getDrawable(drawableResourceId);  
		linearLayout.setBackgroundDrawable(drawable);
    }
    
    
	public void loadDataFromAsset() {
		// load text
		/*
		try {
			// get input stream for text
			InputStream is = getAssets().open("text.txt");
			// check size
			int size = is.available();
			// create buffer for IO
			byte[] buffer = new byte[size];
			// get data to buffer
			is.read(buffer);
			// close stream
			is.close();
			// set result to TextView
			mText.setText(new String(buffer));
		} catch (IOException ex) {
			return;
		}
		 */
		// load image
		try {
			// get input stream
			//InputStream ims = getActivity().getAssets().open("logo_407x340.png");
			InputStream ims = getActivity().getAssets().open("info_640x960.png");
			// load image as Drawable
			Drawable d = Drawable.createFromStream(ims, null);
			// set image to ImageView
			mImage.setImageDrawable(d);
		} catch (IOException ex) {
			return;
		}
	}	

    

}
