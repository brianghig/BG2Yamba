package com.bg2.bg2yamba;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.TextView;

public class TimelineActivity extends Activity {

	protected TextView textView;
	
	protected DBHelper dbHelper;
	protected SQLiteDatabase db;
	protected Cursor cursor;
	
	/**
	 * Initializes layout views, and opens a connection
	 * resource to the database
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.timeline_basic);
		
		textView = (TextView)this.findViewById(R.id.textTimeline);
		
		// Connect to the database
		dbHelper = new DBHelper(this);
		db = dbHelper.getReadableDatabase();
		
	}
	
	/**
	 * Handles clean-up of the Database resource
	 */
	@Override
	protected void onDestroy() {
		super.onDestroy();
		db.close();
	}
	
	/**
	 * Retrieves the latest information from the database,
	 * and displays it in the associated TextView
	 */
	@Override
	protected void onResume() {
		super.onResume();
		
		cursor = db.query(DBHelper.TABLE_TIMELINE, null, null, null, null, null, DBHelper.C_CREATED_AT + " DESC");
		startManagingCursor(cursor);
		
		// Iterate over all the data and print it out
		String user, text, output;
		while(cursor.moveToNext()) {
			user = cursor.getString(cursor.getColumnIndex(DBHelper.C_USER));
			text = cursor.getString(cursor.getColumnIndex(DBHelper.C_TEXT));
			output = String.format("%s: %s\n", user, text);
			textView.append(output);
		}
		
	}
	
}
