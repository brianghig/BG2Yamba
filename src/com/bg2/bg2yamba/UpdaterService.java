package com.bg2.bg2yamba;

import android.app.Service;
import android.content.Intent;
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
	private static final int DELAY = 3000; // 60K ms = 60s = 1 minute
	
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
	
	@Override
	public IBinder onBind(Intent intent) {
		// We are not using a bound service, so OK to return null
		return null;
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		
		this.updater = new Updater();
		
		Log.d(TAG, "onCreated");
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		super.onStartCommand(intent, flags, startId);
		
		this.runFlag = true;
		this.updater.start();
		
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
		
		Log.d(TAG, "onDestroyed");
	}
	
	/**
	 * Thread that actually retrieves the updated
	 * statuses from the Twitter API
	 * 
	 * @author brianghig
	 *
	 */
	private class Updater extends Thread {
		
		public Updater() {
			// Thread Name to help identify in debugging
			super("UpdaterService-Updater");
		}
		
		@Override
		public void run() {
			UpdaterService updaterService = UpdaterService.this;
			
			while(updaterService.runFlag) {
				Log.d(TAG, "Updater running");
				try {
					
					//TODO Work goes here...
					
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
