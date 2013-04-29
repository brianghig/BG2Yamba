package com.bg2.bg2yamba;

import winterwell.jtwitter.Twitter;
import winterwell.jtwitter.TwitterException;
import android.app.Activity;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class StatusActivity extends Activity implements OnClickListener, TextWatcher {
	
	private static final String TAG = StatusActivity.class.getSimpleName();
	
	private int MAX_LENGTH = Integer.valueOf( getString(R.string.statusMaxCharacterLength) );
	
	private static final int YELLOW_THRESHOLD = 20;
	private static final int RED_THRESHOLD = 10;
	private static final int MAGENTA_THRESHOLD = 0;
	
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
		
		editText = (EditText) findViewById(R.id.statusEditText);
		characterLengthText = (TextView) findViewById(R.id.statusCharacterLength);
		updateButton = (Button) findViewById(R.id.statusButton);
		
		/*
		 * Initialize the value of Character Length Text to the max length,
		 * assuming that we start with a blank input form
		 */
		characterLengthText.setText(MAX_LENGTH);
		
		updateButton.setOnClickListener(this);
		
		editText.addTextChangedListener(this);
		
		twitter = new Twitter("gazattack", "Pa$$w0rd");
		twitter.setAPIRootUrl("http://yamba.marakana.com/api");
		
		Log.d(TAG, "StatusActivity finished onCreate");
		
	}

	@Override
	public void onClick(View arg0) {
		String text;
		
		text = editText.getText().toString();
		
		Log.d(TAG, "Attempting updating status with " + text);
		
		new PostToTwitter().execute(text);
			
	}
	
	class PostToTwitter extends AsyncTask<String, Integer, String> {

		@Override
		protected String doInBackground(String... statuses) {
			try {
				
				Log.d(TAG, "Async update status with " + statuses[0]);
				
				winterwell.jtwitter.Status status = twitter.updateStatus(statuses[0]);
				
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
			Toast.makeText(StatusActivity.this, result, Toast.LENGTH_LONG).show();
			
			Log.d(TAG, "Async update status finished");
		}
		
	}

	@Override
	public void afterTextChanged(Editable s) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count, int after) {
		// TODO Auto-generated method stub
		
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
		characterLengthText.setText(remaining);
		
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

}
