package com.bg2.bg2yamba;

import java.util.List;

import winterwell.jtwitter.Twitter;
import winterwell.jtwitter.Twitter.Status;
import android.app.Application;
import android.content.ContentValues;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.preference.PreferenceManager;
import android.util.Log;

public class YambaApplication extends Application implements OnSharedPreferenceChangeListener {

	private static final String TAG = YambaApplication.class.getSimpleName();
	
	/**
	 * Twitter client for Learning Android Marakana API
	 */
	private Twitter twitter;
	
	/**
	 * 
	 */
	private SharedPreferences prefs;
	
	/**
	 * Indicator for whether or not the updater service is running
	 * and retrieving status updates from the Twitter API
	 */
	private boolean serviceRunning;
	
	/**
	 * Common data accessor for Status Data
	 */
	private StatusData statusData;
	
	@Override
	public void onCreate() {
		super.onCreate();
		this.prefs = PreferenceManager.getDefaultSharedPreferences(this);
		this.prefs.registerOnSharedPreferenceChangeListener(this);
		this.statusData = new StatusData(this);
		Log.i(TAG, "onCreated");
	}
	
	/**
	 * Invalidates the Twitter object so that a new one will
	 * be created the next time that it is required
	 * 
	 * @param sharedPreferences
	 * @param key
	 */
	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
		this.twitter = null;
	}
	
	@Override
	public void onTerminate() {
		super.onTerminate();
		Log.i(TAG, "onTerminated");
	}
	
	/**
	 * Retrieves an instance of the Twitter class,
	 * either from the existing field, or a new instance
	 * created from the user's preferences
	 * 
	 * @return an instance of the Twitter API
	 */
	public synchronized Twitter getTwitter() {
		
		if(this.twitter == null) {
			String username, password, apiRoot;
			username = this.prefs.getString("username", "");
			password = this.prefs.getString("password", "");
			apiRoot = this.prefs.getString("apiRoot", "http://yamba.marakana.com/api");
			
			Log.d(TAG, "Username: " + username + ", Password: " + password + ", API URL: " + apiRoot);
			
			//Connect to Twitter with the latest information
			this.twitter = new Twitter(username, password);
			this.twitter.setAPIRootUrl(apiRoot);
		}
		
		return this.twitter;
		
	}

	public boolean isServiceRunning() {
		return serviceRunning;
	}

	public void setServiceRunning(boolean serviceRunning) {
		this.serviceRunning = serviceRunning;
	}
	
	public StatusData getStatusData() {
		return this.statusData;
	}
	
	/**
	 * Returns the count of new statuses, and persists those new statuses into the DB
	 * @return
	 */
	public synchronized int fetchStatusUpdates() {
		
		Log.d(TAG, "Fetching status updates");
		
		Twitter twitter = this.getTwitter();
		
		if( twitter == null ) {
			Log.d(TAG, "Twitter connection cannot be initialized");
			return 0;
		}
		
		try {
			List<Status> statusUpdates = twitter.getHomeTimeline();
			
			long latestStatusCreatedAtTime = this.getStatusData().getLatestStatusCreatedAtTime();
			int count = 0;
			
			ContentValues values = new ContentValues();
			for(Status status : statusUpdates) {
				values.clear();
				values.put(StatusData.C_ID, status.getId());
				long createdAt = status.getCreatedAt().getTime();
				values.put(StatusData.C_CREATED_AT, createdAt);
				values.put(StatusData.C_TEXT, status.getText());
				values.put(StatusData.C_SOURCE, status.source);
				values.put(StatusData.C_USER, status.getUser().getName());
				Log.d(TAG, "Got update with ID " + status.getId() + ". Saving");
				this.getStatusData().insertOrIgnore(values);
				if( latestStatusCreatedAtTime < createdAt ) {
					count++;
				}
			}
			
			Log.d(TAG, count > 0 ? "Got " + count + " status updates" : "No new status updates");
			
			return count;
			
		} catch( RuntimeException e ) {
			Log.e(TAG, "Failed to fetch status updates", e);
			return 0;
		}
		
	}

}
