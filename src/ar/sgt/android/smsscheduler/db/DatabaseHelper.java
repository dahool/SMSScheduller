package ar.sgt.android.smsscheduler.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

	private static final String DATABASE_NAME = "smsscheduller.db";
	private static final int DATABASE_VERSION = 1;

	private static final String DATABASE_CREATE = "create table messages"
		      + "(id INTEGER primary key autoincrement, contactId VARCHAR(50) not null, message TEXT not null, sendDate DATETIME not null, status INTEGER);";
	  
	/*
	 * 
	 * 	private long id;
	private String contactId;
	private String message;
	private Date sendDate;
	private Date sentDate;
	 */
	public DatabaseHelper(Context context) {
		 super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(DATABASE_CREATE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// do nothing

	}

}
