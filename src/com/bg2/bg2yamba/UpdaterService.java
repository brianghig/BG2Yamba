package com.bg2.bg2yamba;

import java.util.List;

import winterwell.jtwitter.Twitter.Status;
import winterwell.jtwitter.TwitterException;
import android.app.Service;
import android.content.ContentValues;
import android.content.Intent;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.IBinder;
import android.util.Log;

/**
 * Background service that executes a thread to retrieve
 * the latest status updates from the Twitter API on a
 * configured interval
 * 
 * @author brianghig
 *
 */
public class UpdaterService extends Service {

	private static final String TAG = UpdaterService.class.getSimpleName();
	
	/**
	 * Millisecond delay indicating how often the Updater
	 * thread should check the Twitter API for status updates
	 */
	private static final int DELAY = 60000; // 60K ms = 60s = 1 minute
	
	/**
	 * boolean indicator of whether or not the
	 * UpdaterService is running
	 */
	private boolean runFlag = false;
	
	/**
	 * Updater class that actually performs the call to the Twitter
	 * API to retrieve the latest status updates
	 */
	private Updater updater;
	
	/**
	 * Handle on the application context that will receive updates
	 * for the running status of the UpdaterService 
	 */
	private YambaApplication yamba;
	
	/**
	 * DBHelper to provide CRUD methods to the database
	 */
	private DBHelper dbHelper;
	
	@Override
	public IBinder onBind(Intent intent) {
		// We are not using a bound service, so OK to return null
		return null;
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		
		this.updater = new Updater();
		this.yamba = (YambaApplication) this.getApplication();
		
		// Use this as the Context since UpdaterService is a Service is a Context
		this.dbHelper = new DBHelper(this);
		
		Log.d(TAG, "onCreated");
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		super.onStartCommand(intent, flags, startId);
		
		this.runFlag = true;
		this.updater.start();
		this.updateServiceStatus();
		
		Log.d(TAG, "onStarted");
		return START_STICKY;
	}
	
	/**
	 * Clean up anything that was initialized in onCreate(),
	 * such as runFlag and updater
	 */
	@Override
	public void onDestroy() {
		super.onDestroy();
		
		this.runFlag = false;
		this.updater.interrupt();
		this.updater = null;
		this.updateServiceStatus();
		
		Log.d(TAG, "onDestroyed");
	}
	
	/**
	 * Updates the running status of the service
	 * with the application context
	 */
	private void updateServiceStatus() {
		this.yamba.setServiceRunning(runFlag);
	}
	
	/**
	 * Thread that actually retrieves the updated
	 * statuses from the Twitter API
	 * 
	 * @author brianghig
	 *
	 */
	private class Updater extends Thread {
		
		List<Status> timeline;
		
		public Updater() {
			// Thread Name to help identify in debugging
			super("UpdaterService-Updater");
		}
		
		@Override
		public void run() {
			UpdaterService updaterService = UpdaterService.this;
			
			while(updaterService.runFlag) {
				
				try {
					
					Log.d(TAG, "Updater running");
					
					/*
					 * Clear the timeline so we don't attempt to enter
					 * duplicate data in the case of a TwitterException
					 */
					timeline = null;
					
					try {
						timeline = yamba.getTwitter().getHomeTimeline();
					} catch( TwitterException e ) {
						Log.e(TAG, "Failed to connect to Twitter service", e);
					}
					
					/*
					 * Print the results for now
					 */
					if( timeline != null ) {
						
						// Get a handle on the writable database
						SQLiteDatabase db = dbHelper.getWritableDatabase();
						
						/*
						 * simple name-value pair that maps database
						 * table names to their respective values
						 */
						ContentValues values = new ContentValues();
						
						for( Status status : timeline ) {
							
							Log.d(TAG, String.format("%s: %s", status.user.name, status.text));
							
							values.clear(); // make sure we're starting with a fresh value object
							values.put(DBHelper.C_ID, status.id);
							values.put(DBHelper.C_CREATED_AT, status.createdAt.getTime());
							values.put(DBHelper.C_SOURCE, status.source);
							values.put(DBHelper.C_TEXT, status.text);
							values.put(DBHelper.C_USER, status.user.name);
							
							/*
							 * Perform the DB insert as a prepared statement with the
							 * ContentValues object to avoid sql injection.
							 */
							try {
								db.insertOrThrow(DBHelper.TABLE_TIMELINE, null, values);
							} catch(SQLException e) {
								// ignore, but log error that likely came from duplicate ID constraint
								Log.e(TAG, "Catching and ignoring SQL exception while inserting retrieved status update: " + e.getMessage());
							}
							
						}
						
						// Close connection to the DB
						db.close();
						
					}
					else{
						Log.d(TAG, "Skipping timeline insert for null data");
					}
					
					Log.d(TAG, "Updater ran");
					
					/*
					 * Sleep the thread until the next configured
					 * time to run via the DELAY constant
					 */
					Thread.sleep(DELAY);
					
				} catch( InterruptedException e ) {
					/*
					 * On interrupt, stop running the service,
					 * and update the state of the service
					 */
					updaterService.runFlag = false;
				}
			}
		}
		
	}

}
