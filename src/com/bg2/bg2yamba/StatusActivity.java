package com.bg2.bg2yamba;

import winterwell.jtwitter.Twitter;
import android.os.Bundle;
import android.app.Activity;

public class StatusActivity extends Activity {

	/**
	 * Twitter client for Learning Android Marakana API
	 */
	protected Twitter twitter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.status);
		
		twitter = new Twitter("student", "password");
		twitter.setAPIRootUrl("http://yamba.marakana.com/api");
		
	}

}
