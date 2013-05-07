package com.bg2.bg2yamba;

import winterwell.jtwitter.Twitter;
import android.app.Application;
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
	
	@Override
	public void onCreate() {
		super.onCreate();
		this.prefs = PreferenceManager.getDefaultSharedPreferences(this);
		this.prefs.registerOnSharedPreferenceChangeListener(this);
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

}
