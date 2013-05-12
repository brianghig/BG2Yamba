package com.bg2.bg2yamba;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.SimpleCursorAdapter;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.TextView;

public class TimelineAdapter extends SimpleCursorAdapter {

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
	
	public TimelineAdapter(Context context, int layout, Cursor c) {
		super(context, layout, c, FROM, TO);
	}
	
	@Override
	public void bindView(View row, Context context, Cursor cursor) {
		
		super.bindView(row, context, cursor);
		
		// Manually bind "Created At" timestamp to its view with Relative display
		long timestamp = cursor.getLong( cursor.getColumnIndex( DBHelper.C_CREATED_AT ) );
		
		TextView textCreatedAt = (TextView) row.findViewById(R.id.textCreatedAt);
		
		// Convert and set to something like "10 minutes ago"
		textCreatedAt.setText( DateUtils.getRelativeTimeSpanString(timestamp) );
		
	}

}
