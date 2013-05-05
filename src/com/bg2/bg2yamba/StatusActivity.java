package com.bg2.bg2yamba;

import winterwell.jtwitter.Twitter;
import winterwell.jtwitter.TwitterException;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class StatusActivity extends Activity implements OnClickListener, TextWatcher, OnSharedPreferenceChangeListener {
	
	private static final String TAG = StatusActivity.class.getSimpleName();
	
	private int MAX_LENGTH;
	
	private static final int YELLOW_THRESHOLD = 20;
	private static final int RED_THRESHOLD = 10;
	private static final int MAGENTA_THRESHOLD = 0;
	
	protected SharedPreferences sharedPreferences;
	
	protected EditText editText;
	protected TextView characterLengthText;
	protected Button updateButton;
	
	/**
	 * Twitter client for Learning Android Marakana API
	 */
	protected Twitter twitter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.status);
		
		//Setup preferences
		sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
		sharedPreferences.registerOnSharedPreferenceChangeListener(this);
		
		editText = (EditText) findViewById(R.id.statusEditText);
		characterLengthText = (TextView) findViewById(R.id.statusCharacterLength);
		updateButton = (Button) findViewById(R.id.statusButton);
		
		MAX_LENGTH = Integer.valueOf( getString(R.string.statusMaxCharacterLength) );
		
		/*
		 * Initialize the value of Character Length Text to the max length,
		 * assuming that we start with a blank input form
		 */
		characterLengthText.setText( String.valueOf(MAX_LENGTH) );
		
		updateButton.setOnClickListener(this);
		
		editText.addTextChangedListener(this);
		
		Log.d(TAG, "StatusActivity finished onCreate");
		
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = this.getMenuInflater();
		inflater.inflate(R.menu.menu, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		switch( item.getItemId() ) {
			case R.id.itemPrefs:
				startActivity(new Intent(this, PrefsActivity.class));
				break;
		}
		
		return true;
	}
	
	@Override
	public void onClick(View arg0) {
		String text;
		
		text = editText.getText().toString();
		
		if (text.length() > MAX_LENGTH) {
			Log.d(TAG, text.length() + " character status, don't bother posting to server!");
			Toast.makeText(StatusActivity.this, R.string.warningExceedCharacterLimit, Toast.LENGTH_LONG).show();
		}
		
		else if(text.length() <= 0){
			Log.d(TAG, "0 character status, don't bother posting to server!");
			Toast.makeText(StatusActivity.this, R.string.warningZeroCharacter, Toast.LENGTH_LONG).show();
		}
		else {
			Log.d(TAG, "Attempting updating status with " + text);
			new PostToTwitter().execute(text);
		}
		
			
			
	}
	
	class PostToTwitter extends AsyncTask<String, Integer, String> {

		@Override
		protected String doInBackground(String... statuses) {
			try {
				
				Log.d(TAG, "Async update status with " + statuses[0]);
				
				winterwell.jtwitter.Status status = getTwitter().updateStatus(statuses[0]);
				
				return status.text;
			}
			catch (TwitterException te)
			{
				Log.e(TAG, "Async status update failed", te);
				
				return StatusActivity.this.getString(R.string.statusUpdateFailed);
			}

		}
		
		@Override
		protected void onPostExecute(String result) {
			if(result.equals("Status update failed")){
				Log.d(TAG, "Async update status failed");
				Toast.makeText(StatusActivity.this, result, Toast.LENGTH_LONG).show();
			}
			else {
				Log.d(TAG, "Async update status successful");
				Toast.makeText(StatusActivity.this, "Status update successful", Toast.LENGTH_LONG).show();
			}
			
			Log.d(TAG, "Async update status finished");
		}
		
	}

	@Override
	public void afterTextChanged(Editable s) {
		
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count, int after) {
		
	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		
		// Get the current count 
		int currentCount = s.length();
		
		Log.d(TAG, "Got current character length of: " + currentCount);
		
		// Get the remaining count from the max length
		int remaining = MAX_LENGTH - currentCount;
		
		Log.d(TAG, "Got remaining character length of: " + remaining);
		
		// Update the remaining count
		characterLengthText.setText( String.valueOf(remaining) );
		
		// Update the color to reflect a warning based on the remaining characters
		characterLengthText.setTextColor( 
				this.getColorFromRemainingLength(remaining) );
		
	}
	
	/**
	 * Returns an int representing the color for the remaining text based
	 * on the configured thresholds
	 * 
	 * @param remaining
	 * @return
	 */
	protected int getColorFromRemainingLength( int remaining ) {
		
		Log.d(TAG, "Finding color from remaining characters: " + remaining);
		
		int color;
		
		if( remaining < MAGENTA_THRESHOLD ) {
			Log.d(TAG, "Using Magenta");
			color = Color.MAGENTA;
		}
		else if( remaining < RED_THRESHOLD ) {
			Log.d(TAG, "Using Red");
			color = Color.RED;
		}
		else if( remaining < YELLOW_THRESHOLD ) {
			Log.d(TAG, "Using Yellow");
			color = Color.YELLOW;
		}
		else {
			Log.d(TAG, "Defaulting to Black");
			color = Color.BLACK;
		}
		
		Log.d(TAG, "Returning color: " + color);
		
		return color;
		
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
		
		/*
		 * invalidate the Twitter object so that a new one will
		 * be created the next time that it is required
		 */
		twitter = null;
		
	}
	
	/**
	 * Retrieves an instance of the Twitter class,
	 * either from the existing field, or a new instance
	 * created from the user's preferences
	 * 
	 * @return an instance of the Twitter API
	 */
	private Twitter getTwitter() {
		
		if(twitter == null) {
			String username, password, apiRoot;
			username = sharedPreferences.getString("username", "");
			password = sharedPreferences.getString("password", "");
			apiRoot = sharedPreferences.getString("apiRoot", "http://yamba.marakana.com/api");
			
			Log.d(TAG, "Username: " + username + ", Password: " + password + ", API URL: " + apiRoot);
			
			//Connect to Twitter with the latest information
			twitter = new Twitter(username, password);
			twitter.setAPIRootUrl(apiRoot);
		}
		
		return twitter;
		
	}

}
