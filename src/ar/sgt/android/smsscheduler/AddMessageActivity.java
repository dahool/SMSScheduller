package ar.sgt.android.smsscheduler;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import ar.sgt.android.smsscheduler.db.dao.MessageDAO;
import ar.sgt.android.smsscheduler.db.model.Message;

import com.actionbarsherlock.app.SherlockFragmentActivity;

public class AddMessageActivity extends SherlockFragmentActivity {

	private static final int GET_PHONE_NUMBER = 3007;
	
	private MessageDAO messageDAO;
	private Message mMessage;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_message_dialog);
		
		messageDAO = new MessageDAO(this);
		mMessage = (Message) getIntent().getSerializableExtra("MESSAGE");
		if (mMessage != null) {
			loadItem(mMessage);
		} else {
			mMessage = new Message();
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(com.actionbarsherlock.view.Menu menu) {
		getSupportMenuInflater().inflate(R.menu.add_message, menu);
		return true;
	}
	
	public void doLaunchContactPicker(View view) {
		//startActivityForResult(new Intent(this, ContactsPickerActivity.class), GET_PHONE_NUMBER);
	}

	public void doLaunchDatePicker(View view) {
		DatePickerDialogFragment timeFragment = new DatePickerDialogFragment();
		timeFragment.setValueSetListener(new OnValueSetListener() {
			@Override
			public void setValue(Object value) {
				((EditText)findViewById(R.id.sendDate)).setText((String) value);
			}
		});
		timeFragment.show(getSupportFragmentManager(), "datePicker");
	}
	
	public void doLaunchTimePicker(View view) {
		TimePickerDialogFragment timeFragment = new TimePickerDialogFragment();
		timeFragment.setValueSetListener(new OnValueSetListener() {
			@Override
			public void setValue(Object value) {
				((EditText)findViewById(R.id.sendTime)).setText((String) value);
			}
		});
		timeFragment.show(getSupportFragmentManager(), "timePicker");
	}

	private void loadItem(Message m) {
		((EditText) findViewById(R.id.contactId)).setText(m.getContactId());
		((EditText) findViewById(R.id.messageContent)).setText(m.getMessage());
		((EditText) findViewById(R.id.messageContent)).setText(m.getMessage());
		
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(mMessage.getSendDate());

		((EditText) findViewById(R.id.sendDate)).setText(java.text.DateFormat.getDateInstance().format(calendar.getTime()));
		((EditText) findViewById(R.id.sendTime)).setText(calendar.get(Calendar.HOUR_OF_DAY) + ":" + calendar.get(Calendar.MINUTE));
	}
	
	private void saveItem() {
		if (isValid()) {
			mMessage.setContactId(((EditText) findViewById(R.id.contactId)).getText().toString().trim());
			mMessage.setMessage(((EditText) findViewById(R.id.messageContent)).getText().toString());
			mMessage.setStatus(0);
			
			try {
				Calendar sendDate = new GregorianCalendar();
				sendDate.setTime(DateFormat.getDateInstance().parse(((EditText) findViewById(R.id.sendDate)).getText().toString()));
 
				String sendTime[] = ((EditText) findViewById(R.id.sendTime)).getText().toString().split(":");

				sendDate.set(Calendar.HOUR_OF_DAY, Integer.parseInt(sendTime[0]));
				sendDate.set(Calendar.MINUTE, Integer.parseInt(sendTime[1]));
				
				mMessage.setSendDate(sendDate.getTime());
			} catch (ParseException e) {
				e.printStackTrace();
			}
			
			if (isDateInFuture(mMessage)) {
				messageDAO.open();
				messageDAO.save(mMessage);
				messageDAO.close();
				finish();
			} else {
				Toast.makeText(this, getResources().getString(R.string.date_in_past), Toast.LENGTH_LONG).show();
			}
		}
	}
	
	private boolean isDateInFuture(Message message) {
		return (message.getSendDate().after(new Date()));
	}

	private boolean isValid() {
		boolean r = true;
		EditText contact = (EditText)findViewById(R.id.contactId);
		if (contact.getText().toString().trim().length() == 0) {
			contact.setError(getResources().getString(R.string.required));
			r = false;
		} else if (!contact.getText().toString().trim().matches("^[\\d-]+$")) {
			contact.setError(getResources().getString(R.string.invalid_number));
			r = false;
		} else {
			contact.setError(null);
		}
		
		EditText dateField = (EditText)findViewById(R.id.sendDate);
		if (dateField.getText().toString().length() == 0) {
			dateField.setError(getResources().getString(R.string.required));
			r = false;
		} else {
			dateField.setError(null);
		}
		EditText timeField = (EditText)findViewById(R.id.sendTime);
		if (timeField.getText().toString().length() == 0) {
			timeField.setError(getResources().getString(R.string.required));
			r = false;
		} else {
			timeField.setError(null);
		}
		EditText content = (EditText)findViewById(R.id.messageContent);
		if (content.getText().toString().length() == 0) {
			content.setError(getResources().getString(R.string.required));
			r = false;
		} else {
			content.setError(null);
		}		
		return r;
	}

//	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//		if (resultCode == RESULT_OK) {
//			switch (requestCode) {
//			case GET_PHONE_NUMBER:
//				String phoneNumber = (String) data.getExtras().get(ContactsPickerActivity.KEY_PHONE_NUMBER);
//				EditText numberEntry = (EditText) findViewById(R.id.contactId);
//				numberEntry.setText(phoneNumber);
//				break;
//			default:
//				break;
//			}
//		}
//		
//	};
	
	@Override
	public boolean onOptionsItemSelected(com.actionbarsherlock.view.MenuItem item) {
	    switch (item.getItemId()) {
	    case R.id.action_save:
	    	saveItem();
	    	return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
	
}
