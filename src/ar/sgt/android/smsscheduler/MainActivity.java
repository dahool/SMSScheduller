/**
 * Copyright (c) 2013 Sergio Gabriel Teves
 * All rights reserved.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package ar.sgt.android.smsscheduler;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import ar.sgt.android.smsscheduler.db.dao.MessageDAO;
import ar.sgt.android.smsscheduler.db.model.Message;
import ar.sgt.android.smsscheduler.receiver.SchedulerIntentFactory;
import ar.sgt.android.smsscheduler.widget.MessageAdapter;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.ActionMode;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

public class MainActivity extends SherlockActivity {

	private MessageDAO messageDAO;
	private com.actionbarsherlock.view.ActionMode mActionMode;
	private Set<Message> mSelectedItems;
	private MainReceiver mReceiver;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
		setContentView(R.layout.activity_main);
		ListView listView = (ListView) findViewById(R.id.messageList);
		
		listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
				if (mActionMode != null) return false;
				mActionMode = MainActivity.this.startActionMode(mActionModeCallback);
				mSelectedItems = new HashSet<Message>();
				updateItem((ListView) parent, view, position);
				return true;
			}
		});
		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				if (mActionMode != null) {
					updateItem((ListView) parent, view, position);
				} else {
					Intent intent = new Intent(getApplicationContext(), AddMessageActivity.class);
					intent.putExtra("MESSAGE", (Message) parent.getItemAtPosition(position));
					startActivity(intent);
				}
			}
		});
	}

	@Override
	protected void onResume() {
		super.onResume();
		mReceiver = new MainReceiver(this);
		loadMessages();
    	SchedulerIntentFactory factory = new SchedulerIntentFactory(this);
    	factory.createNextSchedule();		
    	registerReceiver(mReceiver, new IntentFilter(MainActivity.class.getName()));
	}
	
	@Override
	protected void onPause() {
		if (mReceiver != null) unregisterReceiver(mReceiver);
		super.onPause();
	}
	
	private void updateItem(ListView listView, View view, int pos) {
		Message message = (Message) listView.getItemAtPosition(pos);
		if (mSelectedItems.contains(message)) {
			mSelectedItems.remove(message);
			view.setBackgroundColor(getResources().getColor(R.color.row_background_normal));
		} else {
			mSelectedItems.add(message);
			view.setBackgroundColor(getResources().getColor(R.color.row_background_selected));
		}
	}
	
	private void loadMessages() {
		messageDAO = new MessageDAO(this);
		messageDAO.open();
		List<Message> messages = messageDAO.getAll();
		messageDAO.close();
		MessageAdapter adapter = new MessageAdapter(this, R.layout.listview_item_row, messages); 
		ListView listView = (ListView) findViewById(R.id.messageList);
		listView.setAdapter(adapter);
	}

	@Override
	public boolean onCreateOptionsMenu(com.actionbarsherlock.view.Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		// getMenuInflater().inflate(R.menu.main, menu);
		getSupportMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(com.actionbarsherlock.view.MenuItem item) {
	    switch (item.getItemId()) {
		    case R.id.action_new:
		    	startActivity(new Intent(this, AddMessageActivity.class));
		    	return true;
		    default:
	            return super.onOptionsItemSelected(item);
	    }
	}
	
	private ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {
		
		@Override
		public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
			return false;
		}
		
		@Override
		public void onDestroyActionMode(ActionMode mode) {
			if (!mSelectedItems.isEmpty()) {
				ListView listView = (ListView) findViewById(R.id.messageList);
				for (int i=0; i<listView.getChildCount(); i++) {
					View view = (View) listView.getChildAt(i);
					view.setBackgroundColor(getResources().getColor(R.color.row_background_normal));
				}
			}
			mActionMode = null;
			mSelectedItems = null;
		}
		
		@Override
		public boolean onCreateActionMode(ActionMode mode, Menu menu) {
			MenuInflater inflater = mode.getMenuInflater();
			inflater.inflate(R.menu.row_menu, menu);
			return true;
		}
		
		@Override
		public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
			switch (item.getItemId()) {
				case R.id.action_delete:
					if (!mSelectedItems.isEmpty()) {
						messageDAO = new MessageDAO(getApplicationContext());
						messageDAO.open();
						SchedulerIntentFactory factory = new SchedulerIntentFactory(getApplicationContext());
						for (Message m : mSelectedItems) {
							factory.cancelPendingIntent(m);
							messageDAO.delete(m);	
						}
						messageDAO.close();
						loadMessages();
						factory.createNextSchedule();
					}
					mode.finish();
					return true;
				default:
					return false;
			}
		}
	};
	
	public static class MainReceiver extends BroadcastReceiver {

		private final Activity activity;
		
		public MainReceiver(Activity activity) {
			this.activity = activity;
		}
		
		@Override
		public void onReceive(Context ctx, Intent intent) {
			((MainActivity) activity).loadMessages();
		}
		
	}
	
}
