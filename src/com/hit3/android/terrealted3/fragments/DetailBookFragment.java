package com.hit3.android.terrealted3.fragments;


import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.hit3.android.terrealted3.Constant;
import com.hit3.android.terrealted3.R;
import com.hit3.android.terrealted3.db.DataBaseConstants;
import com.hit3.android.terrealted3.db.DataBaseHelper;
import com.hit3.android.terrealted3.db.QueryBuilder;
import com.hit3.android.terrealted3.db.tables.BooksTable;
import com.hit3.android.terrealted3.db.tables.MagazinesTable;
import com.hit3.android.terrealted3.tasks.CachedImageTask;
import com.hit3.android.terrealted3.tasks.ConnectionTask;
import com.hit3.android.terrealted3.tasks.DownloadImageTask;



public class DetailBookFragment extends Fragment {

	private final static String LOG_TAG = "DetailBookFragment";
	public final static String TASK_TAG = "DetailBookFragment";
	
	private final static String SERVICE = Constant.SERVICE_22;
	
	private Handler handler;
	private Integer nid;

	private TextView textViewTitle;
	private TextView textViewChangedDate;
	private TextView textViewText;
	private TextView textViewTitleLibrary;
	private TextView textViewLibrary;
	private TextView textViewTitleAuthor;
	private TextView textViewAuthor;
	private TextView textViewTitlePublisher;
	private TextView textViewPublisher;
	private TextView textViewTitleYear;
	private TextView textViewYear;
	private TextView textViewTitlePagesNumber;
	private TextView textViewPagesNumber;
	private TextView textViewTitleCode;
	private TextView textViewCode;
	
	private ImageView imageView;
	private ProgressBar progressBar;
	
	
	
	
	
	public DetailBookFragment() {
		// TODO Auto-generated constructor stub
	}
	
	
	public static DetailBookFragment getInstance(int nid){
		DetailBookFragment fragment = new DetailBookFragment();
		Bundle bundle = new Bundle();
		bundle.putInt(Constant.BUNDLE_NID, nid);
		fragment.setArguments(bundle);
		return fragment;
	}	
	
	
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_detail_book, container, false);
        nid = getArguments().getInt(Constant.BUNDLE_NID);
		textViewTitle = (TextView)rootView.findViewById(R.id.textViewTitle);
		textViewTitle.setText("In progres ... (" + nid + ")");
		textViewChangedDate = (TextView)rootView.findViewById(R.id.textViewChangedDate);
		textViewText = (TextView)rootView.findViewById(R.id.textViewText);
		textViewTitleLibrary = (TextView)rootView.findViewById(R.id.textViewTitleLibrary);
		textViewLibrary = (TextView)rootView.findViewById(R.id.textViewLibrary);
		textViewTitleAuthor = (TextView)rootView.findViewById(R.id.textViewTitleAuthor);
		textViewAuthor = (TextView)rootView.findViewById(R.id.textViewAuthor);
		textViewTitlePublisher = (TextView)rootView.findViewById(R.id.textViewTitlePublisher);
		textViewPublisher = (TextView)rootView.findViewById(R.id.textViewPublisher);
		textViewTitleYear = (TextView)rootView.findViewById(R.id.textViewTitleYear);
		textViewYear = (TextView)rootView.findViewById(R.id.textViewYear);
		textViewTitlePagesNumber = (TextView)rootView.findViewById(R.id.textViewTitlePagesNumber);
		textViewPagesNumber = (TextView)rootView.findViewById(R.id.textViewPagesNumber);
		textViewTitleCode = (TextView)rootView.findViewById(R.id.textViewTitleCode);
		textViewCode = (TextView)rootView.findViewById(R.id.textViewCode);
		progressBar = (ProgressBar)rootView.findViewById(R.id.idProgressBarDetail);
		imageView = (ImageView)rootView.findViewById(R.id.imageViewDetail);
		
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
		String service = Constant.WEB_SERVER + SERVICE + "/" + nid;
		
		handler = new Handler() {

			@Override
			public void handleMessage(Message msg) {
				String response = msg.obj.toString();
				List<Map<String, String>> array = (new BooksTable()).parseRequest(response);
				show(array);
			}

		};
		new ConnectionTask(handler).execute(service);
	} 	
	
	
	private void populateFromDb() {
		SQLiteDatabase db = DataBaseHelper.getInstance(getActivity().getApplicationContext()).getWritableDatabase();
		String[] selectionArgs = new String[]{nid+""};   
		//List<Map<String, String>> array = (new BooksTable()).getDataset(db, DataBaseConstants.QUERY_GET_BOOK, selectionArgs);
		List<Map<String, String>> array = QueryBuilder.getDataset(getActivity(), new BooksTable(),  DataBaseConstants.QUERY_GET_BOOK, selectionArgs);
		show(array);
	}
	
	
	private void show(List<Map<String, String>> array) {
		if (array.size() == 1) {
			textViewTitle.setText(array.get(0).get(BooksTable.title));
			textViewChangedDate.setText(getResources().getString(R.string.modificato) + " " + array.get(0).get(BooksTable.modify_date));
			textViewText.setText(array.get(0).get(BooksTable.body));
			textViewTitleLibrary.setText(getResources().getString(R.string.libro_genere));
			textViewLibrary.setText(array.get(0).get(BooksTable.library));
			textViewTitleAuthor.setText(getResources().getString(R.string.libro_autore));
			textViewAuthor.setText(array.get(0).get(BooksTable.author));
			textViewTitlePublisher.setText(getResources().getString(R.string.libro_editore));
			textViewPublisher.setText(array.get(0).get(BooksTable.publisher));
			textViewTitleYear.setText(getResources().getString(R.string.libro_anno));
			textViewYear.setText(array.get(0).get(BooksTable.publication_year));
			textViewTitlePagesNumber.setText(getResources().getString(R.string.libro_numero_pagine));
			textViewPagesNumber.setText(array.get(0).get(BooksTable.pages_number));
			textViewTitleCode.setText(getResources().getString(R.string.libro_codice));
			textViewCode.setText(array.get(0).get(BooksTable.code));
			String image1 = array.get(0).get(BooksTable.image1);
			String image2 = array.get(0).get(BooksTable.image2);
			String image3 = array.get(0).get(BooksTable.image3);
			String image4 = array.get(0).get(BooksTable.image4);
			String image5 = array.get(0).get(BooksTable.image5);
			SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
			if (sharedPrefs.getBoolean("prefEnableImageCacheModality", false)) {
				String imageKey = StringUtils.substringAfterLast(image4, "/");
				String imageToken = StringUtils.substringAfterLast(imageKey, "?");
				imageKey = StringUtils.substringBefore(imageKey, "?");
				imageToken = StringUtils.remove(imageToken, "itok=");
				new CachedImageTask(null, imageView, getActivity()).execute(image4, imageKey, imageToken, Constant.CACHE_IMAGE_TYPE_LARGE, Constant.CONTENT_TYPE.BOOKS.toString().toLowerCase());
			} else {
				if (sharedPrefs.getBoolean("prefOffLineModality", false)) {
					imageView.setImageBitmap(BitmapFactory.decodeResource(getActivity().getResources(), Constant.THUMBNAIL_IMAGE));
				} else {
					new DownloadImageTask(progressBar, imageView).execute(image4);
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
	
}
