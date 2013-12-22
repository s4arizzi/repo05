package com.hit3.android.terrealted3.fragments;


import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.Fragment;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
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
import com.hit3.android.terrealted3.db.tables.SongsTable;
import com.hit3.android.terrealted3.tasks.ConnectionTask;

public class DetailSongFragment extends Fragment {

	private final static String LOG_TAG = "DetailSongFragment";
	public final static String TASK_TAG = "DetailSongFragment";
	
	private final static String SERVICE = Constant.SERVICE_72;
	
	private Handler handler;
	private Integer nid;
	private TextView textViewTitle;
	private TextView textViewChangedDate;
	private TextView textViewText;
	private ImageView imageView;
	private ProgressBar progressBar;
	
	
	
	
	
	public DetailSongFragment() {
		// TODO Auto-generated constructor stub
	}
	
	public static DetailSongFragment getInstance(int nid){
		DetailSongFragment fragment = new DetailSongFragment();
		Bundle bundle = new Bundle();
		bundle.putInt(Constant.BUNDLE_NID, nid);
		fragment.setArguments(bundle);
		return fragment;
	}	
	
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_detail_song, container, false);
        nid = getArguments().getInt(Constant.BUNDLE_NID);
		textViewTitle = (TextView)rootView.findViewById(R.id.textViewTitle);
		textViewChangedDate = (TextView)rootView.findViewById(R.id.textViewChangedDate);
		textViewText = (TextView)rootView.findViewById(R.id.textViewText);
		textViewTitle.setText("In progres ... (" + nid + ")");
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
				List<Map<String, String>> array = (new SongsTable()).parseRequest(response);
				show(array);
			}

		};
		new ConnectionTask(handler).execute(service);
	} 	
	
	
	private void populateFromDb() {
		SQLiteDatabase db = DataBaseHelper.getInstance(getActivity().getApplicationContext()).getWritableDatabase();
		String[] selectionArgs = new String[]{nid+""};   
		//List<Map<String, String>> array = (new SongsTable()).getDataset(db, DataBaseConstants.QUERY_GET_SONG, selectionArgs);
		List<Map<String, String>> array = QueryBuilder.getDataset(getActivity(), new SongsTable(),  DataBaseConstants.QUERY_GET_SONG, selectionArgs);
		show(array);
	}
	
	
	private void show(List<Map<String, String>> array) {
		if (array.size() == 1) {
			textViewTitle.setText(array.get(0).get(SongsTable.title));
			textViewChangedDate.setText(getResources().getString(R.string.modificato) + " " + array.get(0).get(SongsTable.modify_date));
			textViewText.setText(array.get(0).get(SongsTable.text));
		}
	}	
	

}
