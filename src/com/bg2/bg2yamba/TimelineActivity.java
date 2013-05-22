package com.bg2.bg2yamba;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter.ViewBinder;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

public class TimelineActivity extends BaseActivity {
	
	private static final String TAG = TimelineActivity.class.getSimpleName();

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
		this.adapter = new SimpleCursorAdapter(this, R.layout.row, cursor, FROM, TO, SimpleCursorAdapter.NO_SELECTION);
		
		/*
		 * Instead of subclassing and creating an entirely new Cursor Adapter,
		 * we can simply create a view binder that is [supposedly] more efficient.
		 * Not sure how it is more efficient though... apparently the call to
		 * bindView is heavy??
		 */
		adapter.setViewBinder( VIEW_BINDER );
		
		listTimeline.setAdapter(this.adapter);
		
	}
	
	/**
	 * ViewBinder constant that sets the View Value for the CREATED_AT
	 * timestamp to the Relative TimeSpan String to display to the user
	 * (e.g., "10 minutes ago" or "2 days ago")
	 */
	private static final ViewBinder VIEW_BINDER = new ViewBinder() {

		@Override
		public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
			
			/*
			 * No call to super() like many other implementation
			 * overrides in this project.
			 * 
			 * instead, return boolean true if override, false if default
			 */
			
			if( R.id.textCreatedAt != view.getId() ) {
				Log.d(TAG, "Skipping set View Value for non-custom field");
				return false;
			}
			
			Log.d(TAG, "Found Created At view, so updating display for timestamp...");
			
			// Update the Created At text to relative time
			long timestamp = cursor.getLong( columnIndex );
			CharSequence relTime = DateUtils.getRelativeTimeSpanString(view.getContext(), timestamp);
			((TextView) view).setText( relTime );
			
			/*
			 * We provided an override to the default view value,
			 * so return true in order to tell SimpleCursorAdapter
			 * to skip processing bindView on this element in its
			 * standard way
			 */
			return true;
		}
		
	};
	
}
