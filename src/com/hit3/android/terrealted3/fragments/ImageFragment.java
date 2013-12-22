package com.hit3.android.terrealted3.fragments;

import java.io.InputStream;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.hit3.android.terrealted3.Constant;
import com.hit3.android.terrealted3.MainActivity;
import com.hit3.android.terrealted3.R;
import com.hit3.android.terrealted3.R.id;
import com.hit3.android.terrealted3.R.layout;
import com.hit3.android.terrealted3.listners.OnItemSelectedFragmentListner;
import com.hit3.android.terrealted3.support.TouchImageView;
import com.hit3.android.terrealted3.utilities.RestServerUtilities;

public class ImageFragment extends Fragment {

	private final static String LOG_TAG = "ImageFragment";
	public final static String TASK_TAG = "ImageFragment";
	
	//private OnItemSelectedFragmentListner listner;
	//private MainActivity mainActivity;

	private LinearLayout linearLayout;
	private String imageName;
	//private ImageView mImage;
	private TouchImageView mImage;

	

	public ImageFragment() {
		// TODO Auto-generated constructor stub
	}

	public static ImageFragment getInstance(String imageName) {
		ImageFragment fragment = new ImageFragment();
		Bundle bundle = new Bundle();
		bundle.putString(Constant.BUNDLE_IMAGE_NAME, imageName);
		fragment.setArguments(bundle);
		return fragment;
	}

	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_image, container, false);
		//getActivity().requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		//getActivity().setProgressBarIndeterminateVisibility(true);
		linearLayout = (LinearLayout)rootView.findViewById(R.id.layout_touch_image);
		imageName = getArguments().getString(Constant.BUNDLE_IMAGE_NAME);
		mImage = (TouchImageView)rootView.findViewById(R.id.imageView);
		//String url = Constant.WEB_SERVER + "/sites/default/files/" + imageName;
		//new DownloadImageTask(mImage).execute(url);
		setBackground();
		new DownloadImageTask(mImage).execute(imageName);

		return rootView;
	}

	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		// Salviamo il riferimento alla particolare Activity
//		this.mainActivity = (MainActivity) activity;
//		if (activity instanceof OnItemSelectedFragmentListner) {
//			listner = (OnItemSelectedFragmentListner) activity;
//		} else {
//			throw new ClassCastException(activity.toString() + " must implement OnItemSelectedFragmentListner");
//		}
	}

	
	@Override
	public void onResume() {
		super.onResume();
		setBackground();
	}
	
	
	public void onButtonBack(View view) {
		//http://stackoverflow.com/questions/8772921/how-to-pop-back-stack-for-activity-with-multiple-fragments
		//finish();
	}
	
	
	private void setBackground() {
    	SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
		String drawableBackground = sharedPrefs.getString("prefSyncBackgrounds", "background_repeat5");
		int drawableResourceId = this.getResources().getIdentifier(drawableBackground, "drawable", getActivity().getPackageName());
		Drawable drawable = this.getResources().getDrawable(drawableResourceId);  
		linearLayout.setBackgroundDrawable(drawable);
    }



//////////////////////////////////////////////////////////////////////////////////////////////////

	private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
	    ImageView mImage;

	    public DownloadImageTask(ImageView mImage) {
	        this.mImage = mImage;
	    }

	    
	    @Override
		protected void onPreExecute() {
			super.onPreExecute();
			
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
	    protected void onPostExecute(Bitmap bitmap) {
	    	//Display display = mainActivity.getWindowManager().getDefaultDisplay();
	    	Display display = getActivity().getWindowManager().getDefaultDisplay();
//			Point size = new Point();
//			display.getSize(size);
//			int width = size.x;
//			int height = size.y;
			int displayWidth = display.getWidth();     // deprecated
			int displayHeight = display.getHeight();   // deprecated
			boolean displayPortraitOrientation = true;
			if (displayWidth > displayHeight)
				displayPortraitOrientation = false;
	    	
			int bitmapWidth = bitmap.getWidth();    
			int bitmapWeight = bitmap.getHeight(); 
			boolean bitmapPortraitOrientation = true;
			if (bitmapWidth > bitmapWeight)
				bitmapPortraitOrientation = false;
			
			Bitmap result = bitmap;
			if ((displayPortraitOrientation && !bitmapPortraitOrientation) || (!displayPortraitOrientation && bitmapPortraitOrientation)) {
				Matrix matrix = new Matrix();
				matrix.postRotate(90);
				result = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
			}
	    	
			//getActivity().setProgressBarIndeterminateVisibility(false);
	        mImage.setImageBitmap(result);
	    }
	    
	    
	}		
}
