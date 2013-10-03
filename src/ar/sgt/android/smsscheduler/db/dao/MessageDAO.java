package ar.sgt.android.smsscheduler.db.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import ar.sgt.android.smsscheduler.db.DatabaseHelper;
import ar.sgt.android.smsscheduler.db.model.Message;

public class MessageDAO {

	public static final String TABLE_NAME = "messages";
	public static final String[] columns = {"id","contactId", "message", "sendDate", "status"};
	
	private SQLiteDatabase database;
	private DatabaseHelper dbHelper;

	
	public MessageDAO(Context context) {
		dbHelper = new DatabaseHelper(context);
	}
	
	public void open() {
		database = dbHelper.getWritableDatabase();
	}
	
	public void close() {
		dbHelper.close();
	}
	
	public List<Message> getAll() {
		List<Message> messages = new ArrayList<Message>();
		Cursor cursor = database.query("messages", columns, null, null, null, null, "sendDate DESC");
		cursor.moveToFirst();
		do {
			if (!cursor.isAfterLast()) {
				messages.add(parseMessage(cursor));
			}
		} while (cursor.moveToNext());

		cursor.close();
		return messages;
	}

	public Message getNextMessage() {
		Message message = null;
		String where = "status = ? AND sendDate > ?";
		String whereArg[] = {"0", Long.toString(new Date().getTime())};
		Cursor cursor = database.query("messages", columns, where, whereArg, null, null, "sendDate ASC", "1");
		if (cursor.moveToFirst()) {
			message = parseMessage(cursor);
		} 
		cursor.close();
		return message;
	}
	
	public Message get(Long id) {
		Message message = null;
		Cursor cursor = database.query("messages", columns, "id="+id, null, null, null, null);
		if (cursor.moveToFirst()) {
			message = parseMessage(cursor);
		} 
		cursor.close();
		return message;
	}
	
	public void save(Message message) {
		ContentValues values = new ContentValues();
		values.put("contactId", message.getContactId());
		values.put("message", message.getMessage());
		values.put("sendDate", message.getSendDate().getTime());
		if (message.getStatus() == null) message.setStatus(0); 
		values.put("status", message.getStatus());
		if (message.getId() == null) {
			long insertId = database.insert(TABLE_NAME, null, values);
			message.setId(insertId);
		} else {
			database.update(TABLE_NAME, values, "id="+message.getId(), null);
		}
	}

	public void delete(Message message) {
		if (message.getId() != null) delete(message.getId());
	}
	
	public void delete(long id) {
		database.delete(TABLE_NAME, "id="+Long.toString(id), null);
	}
	
	private Message parseMessage(Cursor cursor) {
		Message message = new Message();
		message.setId(cursor.getLong(0));
		message.setContactId(cursor.getString(1));
		message.setMessage(cursor.getString(2));
		try {
			long v = cursor.getLong(3);
			if (v > 0) {
				message.setSendDate(new Date(v));
			}
		} catch (Exception e) {
		}
		message.setStatus(cursor.getInt(4));		
		return message;
	}
	
}
