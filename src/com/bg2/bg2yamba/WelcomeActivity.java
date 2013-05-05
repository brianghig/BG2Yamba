package com.bg2.bg2yamba;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class WelcomeActivity extends Activity implements OnClickListener, OnSharedPreferenceChangeListener {
	
	private static final String TAG = WelcomeActivity.class.getSimpleName();
	
	protected EditText username;
	protected EditText password;
	protected EditText apiRoot;
	protected Button startButton;
	
	protected SharedPreferences sharedPreferences;
	
	/**
	 * Twitter client for Learning Android Marakana API
	 */
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.welcome);
		
		//Setup preferences
		sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
		sharedPreferences.registerOnSharedPreferenceChangeListener(this);
		
		username = (EditText) findViewById(R.id.usernameText);
		password = (EditText) findViewById(R.id.passwordText);
		apiRoot = (EditText) findViewById(R.id.apiRootText);
		startButton = (Button) findViewById(R.id.welcomeButton);
		
		startButton.setOnClickListener(this);
		
		Log.d(TAG, "WelcomeActivity finished onCreate");
	}

	/*
	 * onResume() If username exists, go straight to StatusActivity.
	 * Otherwise, stay here and wait for valid input. 
	 */
	@Override
	protected void onResume() {
		super.onResume();
		
		Object[][] preferenceIdToFieldMatches = new Object[][]{
				{ R.string.preferenceKeyUsername, username },
				{ R.string.preferenceKeyPassword, password },
				{ R.string.preferenceKeyApiRoot, apiRoot }
			};
		
		boolean hasEmptyPreference = false;
		preferenceValidationLoop: for(int i=0; i < preferenceIdToFieldMatches.length; i++) {
			Object[] preferenceIdToFieldMatch = preferenceIdToFieldMatches[i];
			int preferenceKeyId = (Integer)preferenceIdToFieldMatch[0];
			
			if( this.isBlankPreference(preferenceKeyId) ) {
				Log.d(TAG, "Found blank preference value, so setting hasEmptyPreference to true");
				hasEmptyPreference = true;
				break preferenceValidationLoop;
			}
		}
		
		if( hasEmptyPreference ) {
			Log.d(TAG, "Some preference value is blank, so staying with WelcomeActivity");
			
			/*
			 * Sets the value of input preferences fields to match the
			 * currently saved Shared Preferences
			 */
			updateEditTextWithPreferenceValue(preferenceIdToFieldMatches);
			
			Toast.makeText(WelcomeActivity.this, "Enter login information", Toast.LENGTH_LONG).show();
						
		}
		else {
			Log.d(TAG, "All required preferences are set, so move to Status Activity");
			
			finish(); //TODO Should this be done before or after starting the StatusActivity?
			
			//Intent over to StatusActivity
			Intent intent = new Intent(this, StatusActivity.class);
			startActivity(intent);
		}
		
		Log.d(TAG, "Finished onResume");
		
	}
	
	/**
	 * Based on a mapping of Shared Preference Key ID - to - EditText field,
	 * set the EditText to any saved preference value
	 * 
	 * @param preferenceIdToFieldMatches
	 */
	protected void updateEditTextWithPreferenceValue(Object[][] preferenceIdToFieldMatches) {
		
		for(int i=0; i < preferenceIdToFieldMatches.length; i++) {
			Object[] idToFieldMatch = preferenceIdToFieldMatches[i];
			int preferenceKeyId = (Integer)idToFieldMatch[0];
			EditText editTextField = (EditText)idToFieldMatch[1];
			
			String preferenceKey = getString(preferenceKeyId);
			String value = sharedPreferences.getString(preferenceKey, null);
			editTextField.setText(value);
		}
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		// Return false to hide menu!
		
		//getMenuInflater().inflate(R.menu.welcome, menu);
		return false;
	}

	
	/*
	 * Check if input is valid.  If not, toast message "Please enter details"
	 * If so, save preferences and continue on to StatusActivity 
	 */
	@Override
	public void onClick(View arg0) {
		String usernameField, passwordField, apiRootValue;
		SharedPreferences.Editor editor;
		Boolean result;
		
		usernameField = username.getText().toString();
		passwordField = password.getText().toString();
		apiRootValue = apiRoot.getText().toString();
		
		String[] values = new String[]{ usernameField, passwordField, apiRootValue };
		
		boolean allValid = true;
		for(int i=0; i < values.length; i++) {
			String val = values[i];
			Log.d(TAG, "Checking value of input: [" + val + "]");
			if( this.isBlankString(val) ) {
				allValid = false;
				break;
			}
		}
		
		if( !allValid ) {
			
			Log.d(TAG, "Some required shared preference input is blank, stay here and wait");
		}
		else {
			Log.d(TAG, "Adding entries to preferences");
			editor = sharedPreferences.edit();
			
			editor.putString( getString(R.string.preferenceKeyUsername), usernameField);
			editor.putString( getString(R.string.preferenceKeyPassword), passwordField);
			editor.putString( getString(R.string.preferenceKeyApiRoot), apiRootValue);
			
			result = editor.commit();
			
			finish();
			
			//Intent over to StatusActivity
			Intent intent = new Intent(this, StatusActivity.class);
			startActivity(intent);
			
			Log.d(TAG, "Preference modification success: " + result);
		}

	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences arg0, String arg1) {
		// TODO Auto-generated method stub
		
	}
	
	/**
	 * Retrieves and checks the blankness of the value of a Shared Preference
	 * by the preference key. Returns true if blank, false otherwise.
	 * 
	 * @param preferenceKeyResourceId R.string ID of the key value of the Shared Preference
	 * @return
	 */
	protected boolean isBlankPreference(int preferenceKeyResourceId) {
		
		String preferenceKey = this.getString(preferenceKeyResourceId);
		Log.d(TAG, "Retrieved preference key: [" + preferenceKey + "]");
		
		String value = sharedPreferences.getString(preferenceKey, null);
		Log.d(TAG, "Retrieved preferenceKey: [" + preferenceKey + "] value of: [" + value + "]");
		
		boolean isEmpty = isBlankString(value);
		Log.d(TAG, "Returning " + isEmpty + " for blank check of preference: [" + preferenceKey + "]");
		
		return isEmpty;
		
	}

	/**
	 * Retrieves and checks the blankness of the value of a String.
	 * Returns true if blank, false otherwise.
	 * 
	 * Note: this could also be achieved with Apache Commons Lang StringUtils,
	 * but we're doing it here to avoid including that entire third party lib.
	 * 
	 * @param value
	 * @return
	 */
	protected boolean isBlankString(String value) {
		
		// default to invalid until we verify that all criteria pass
		boolean isEmpty = true;
		
		// Validate that the string is not null
		if( value != null ) {
			
			/*
			 * Validate that we have some non-blank value in
			 * the saved preference (eliminate empty, whitespace strings) 
			 */
			value = value.trim();
			if( !"".equals(value) ) {
				isEmpty = false;
			}
		}
		
		return isEmpty;
	}

}
