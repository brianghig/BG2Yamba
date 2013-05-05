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
	
		String username = sharedPreferences.getString("username", null);
		String password = sharedPreferences.getString("password", null);
		
		if(username == null || password == null) {
			Log.d(TAG, "Username or password blank, stay here and wait");
			
			Toast.makeText(WelcomeActivity.this, "No preferences!", Toast.LENGTH_LONG).show();
		}
		else {
			Log.d(TAG, "Username exists, go to StatusActivity!");
			
			finish();
			
			//Intent over to StatusActivity
			Intent intent = new Intent(this, StatusActivity.class);
			startActivity(intent);
		}
		
		Log.d(TAG, "WelcomeActivity finished onResume");
		
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
		String usernameField, passwordField;
		SharedPreferences.Editor editor;
		Boolean result;
		
		usernameField = username.getText().toString();
		passwordField = password.getText().toString();
		
		if(usernameField.equals("") || passwordField.equals("")) {
			Log.d(TAG, "Username or password blank, stay here and wait");
		}
		else {
			Log.d(TAG, "Adding entries to preferences");
			editor = sharedPreferences.edit();
			
			editor.putString("username", usernameField);
			editor.putString("password", passwordField);
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

}
