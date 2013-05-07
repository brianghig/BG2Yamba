package com.bg2.bg2yamba;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

public class DBHelper extends SQLiteOpenHelper {
	
	private static final String TAG = DBHelper.class.getSimpleName();
	private static final String DB_NAME = "timeline.db";
	private static final int DB_VERSION = 1;
	public static final String TABLE_TIMELINE = "timeline";
	public static final String C_ID = BaseColumns._ID;
	public static final String C_CREATED_AT = "created_at";
	public static final String C_SOURCE = "source";
	public static final String C_TEXT = "txt";
	public static final String C_USER = "user";
	
	private Context context;
	
	public DBHelper(Context context) {
		super(context, DB_NAME, null, DB_VERSION);
		this.context = context;
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		String sql = "create table " + TABLE_TIMELINE + " (" + C_ID + " int primary key, " +
				C_CREATED_AT + " int, " + C_USER + " text, " + C_SOURCE + " text, " + C_TEXT + " text)";
		
		db.execSQL(sql);
		
		Log.d(TAG, "onCreated sql: " + sql);
	}

	/**
	 * Invoked whenever the current DB version is not the same as the old DB version
	 */
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		
		/*
		 * Typically do ALTER statements, but since we're not storing
		 * any user input, ok to blow away the DB tables and recreate
		 */
		db.execSQL("drop table if exists " + TABLE_TIMELINE); // drop the old table
		
		Log.d(TAG, "onUpdated");
		
		onCreate(db); // run onCreate to get a fresh database (only one table)
		
	}

}
