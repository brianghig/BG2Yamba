package com.bg2.bg2yamba;

import winterwell.jtwitter.Twitter;
import winterwell.jtwitter.TwitterException;
import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class StatusActivity extends Activity implements OnClickListener {
	private static final String TAG = StatusActivity.class.getSimpleName();
	protected EditText editText;
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
		updateButton = (Button) findViewById(R.id.statusButton);
		
		updateButton.setOnClickListener(this);
		
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

}
