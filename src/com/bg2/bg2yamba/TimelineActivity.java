package com.bg2.bg2yamba;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.widget.SimpleCursorAdapter;
import android.widget.ListView;

public class TimelineActivity extends Activity {

	protected ListView listTimeline;
	
	protected DBHelper dbHelper;
	protected SQLiteDatabase db;
	protected Cursor cursor;
	protected SimpleCursorAdapter adapter;
	
	/**
	 * String array specifying which columsn in the cursor we're
	 * binding from
	 */
	protected static final String[] FROM = {
		DBHelper.C_CREATED_AT, DBHelper.C_USER, DBHelper.C_TEXT
	};
	
	/**
	 * Array of integers representing IDs of views in row.xml to
	 * which we are binding data.  Indices of View IDs in this array
	 * should correspond to strings in the FROM array of column names
	 */
	protected static final int[] TO = {
		R.id.textCreatedAt, R.id.textUser, R.id.textText
	};
	
	/**
	 * Initializes layout views, and opens a connection
	 * resource to the database
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.timeline);
		
		this.listTimeline = (ListView) this.findViewById(R.id.listTimeline);
		
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
		
		// Set up the Adapter that will feed data into our List Timeline
		this.adapter = new SimpleCursorAdapter(this, R.layout.row, cursor, FROM, TO);
		listTimeline.setAdapter(this.adapter);
		
	}
	
}
