package com.bg2.bg2yamba;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

public class StatusData {
	
	private static final String TAG = StatusData.class.getSimpleName();
	
	private static final String DB_NAME = "timeline.db";
	private static final int DB_VERSION = 1;
	public static final String TABLE_TIMELINE = "timeline";
	public static final String C_ID = BaseColumns._ID;
	public static final String C_CREATED_AT = "created_at";
	public static final String C_SOURCE = "source";
	public static final String C_TEXT = "txt";
	public static final String C_USER = "user";
	
	private static final String GET_ALL_ORDER_BY = C_CREATED_AT + " DESC";
	
	private static final String[] MAX_CREATED_AT_COLUMNS = {
		"max(" + StatusData.C_CREATED_AT + ")"
	};
	
	private static final String[] DB_TEXT_COLUMNS = { C_TEXT };
	
	private final DBHelper dbHelper;
	
	public StatusData(Context context) {
		this.dbHelper = new DBHelper(context);
		Log.d(TAG, "Initialized data");
	}
	
	public void close() {
		this.dbHelper.close();
	}
	
	public void insertOrIgnore(ContentValues values) {
		Log.d(TAG, "insertOrIgnore on " + values);
		
		SQLiteDatabase db = this.dbHelper.getWritableDatabase();
		try {
			db.insertWithOnConflict(TABLE_TIMELINE, null, values, SQLiteDatabase.CONFLICT_IGNORE);
		} finally {
			db.close();
		}
	}
	
	/**
	 * 
	 * @return Cursor where the columns are _id, created_at, source, text, user
	 */
	public Cursor getStatusUpdates() {
		SQLiteDatabase db = this.dbHelper.getReadableDatabase();
		return db.query(TABLE_TIMELINE, null, null, null, null, null, GET_ALL_ORDER_BY);
	}
	
	/**
	 * 
	 * @return Timestamp of the latest status we have in the database
	 */
	public long getLatestStatusCreatedAtTime() {
		
		SQLiteDatabase db = this.dbHelper.getReadableDatabase();
		try {
			Cursor cursor = db.query(TABLE_TIMELINE, MAX_CREATED_AT_COLUMNS, null, null, null, null, null);
			try {
				return cursor.moveToNext() ? cursor.getLong(0) : Long.MIN_VALUE;
			} finally {
				cursor.close();
			}
		} finally {
			db.close();
		}
		
	}
	
	/**
	 * 
	 * @param id of the status we are looking for
	 * @return Text of the status
	 */
	public String getStatusTextById(long id) {
		
		SQLiteDatabase db = this.dbHelper.getReadableDatabase();
		
		try {
			Cursor cursor = db.query(TABLE_TIMELINE, DB_TEXT_COLUMNS, C_ID + "=" + id, null, null, null, null);
			try {
				return cursor.moveToNext() ? cursor.getString(0) : null;
			} finally {
				cursor.close();
			}
		} finally {
			db.close();
		}
		
	}
	
	/**
	 * DBHelper Implementation
	 * 
	 * @author brianghig
	 *
	 */
	class DBHelper extends SQLiteOpenHelper {
		
		public DBHelper(Context context) {
			super(context, DB_NAME, null, DB_VERSION);
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
			
			this.onCreate(db); // run onCreate to get a fresh database (only one table)
			
		}
		
	}

}
