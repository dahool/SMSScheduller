package ar.sgt.android.smsscheduler.receiver;

import java.util.ArrayList;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;
import android.telephony.SmsManager;
import android.util.Log;
import ar.sgt.android.smsscheduler.MainActivity;
import ar.sgt.android.smsscheduler.R;
import ar.sgt.android.smsscheduler.db.dao.MessageDAO;
import ar.sgt.android.smsscheduler.db.model.Message;

public class SchedulerHandlerReceiver extends BroadcastReceiver {

	public static final String SMS_ACTION = "ar.sgt.android.smsscheduler.SMS_ACTION";
	public static final String SMS_ACTION_SENT = "ar.sgt.android.smsscheduler.SMS_ACTION_SENT";
	public static final String SMS_ID = "sms_id";
	
	@Override
	public void onReceive(Context ctx, Intent intent) {
		Log.d("onReceive", "Received " + intent.getAction());
		if (SMS_ACTION.equals(intent.getAction())) {
			MessageDAO messageDAO = new MessageDAO(ctx);
			messageDAO.open();
			Message m = messageDAO.get(intent.getLongExtra(SMS_ID, -1));
			messageDAO.close();
			if (m != null) {
				// send message using default device app
				/*// this will required user iteraction
				Intent i = new Intent(android.content.Intent.ACTION_VIEW);
		        i.putExtra("address", m.getContactId());
		        i.putExtra("sms_body", m.getMessage());
		        i.setType("vnd.android-dir/mms-sms");
		        ctx.startActivity(i);*/
				SmsManager smsManager = SmsManager.getDefault();
				
				intent.setAction(SMS_ACTION_SENT);
				
				PendingIntent pi = PendingIntent.getBroadcast(ctx, m.getId().intValue(), intent, PendingIntent.FLAG_UPDATE_CURRENT);
				
				if (m.getMessage().length() > 160) {
					smsManager.sendTextMessage(m.getContactId(), null, m.getMessage(), pi, null);
				} else {
					ArrayList<String> parts = smsManager.divideMessage(m.getMessage());
					ArrayList<PendingIntent> pis = new ArrayList<PendingIntent>(parts.size());
					pis.add(pi);
					for (int i = 1; i < parts.size(); i++) {
						pis.add(null);
					}
					smsManager.sendMultipartTextMessage(m.getContactId(), null, parts, pis, null);
				}
		        
			}
		} else if (SMS_ACTION_SENT.equals(intent.getAction())) {
			MessageDAO messageDAO = new MessageDAO(ctx);
			messageDAO.open();
			Message m = messageDAO.get(intent.getLongExtra(SMS_ID, -1));
			if (m != null) {
				if (Activity.RESULT_OK == getResultCode()) {
					m.setStatus(1);
					messageDAO.save(m);
			        // save in sent folder
			        ContentValues values = new ContentValues(); 
			        values.put("address", m.getContactId()); 
			        values.put("body", m.getMessage()); 
			        ctx.getContentResolver().insert(Uri.parse("content://sms/sent"), values);
				} else {
					m.setStatus(2);
					messageDAO.save(m);
				}
				showNotification(ctx, m);
				ctx.sendBroadcast(new Intent(MainActivity.class.getName()));
				
				Vibrator vibrator = (Vibrator) ctx.getSystemService(Context.VIBRATOR_SERVICE);
				if (vibrator != null) {
					vibrator.vibrate(500);	
				}
				
			}
			messageDAO.close();
		}
	}

	private void showNotification(Context context, Message m) {
		Bitmap bm = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_launcher);
		
		NotificationCompat.Builder notifb = new NotificationCompat.Builder(context)
			.setContentTitle(context.getResources().getString(R.string.app_name))
			.setContentText(m.getMessage())
			.setContentInfo(m.getStatus() == 1 ? context.getResources().getString(R.string.message_sent_ok) : context.getResources().getString(R.string.message_sent_fail))
			.setLargeIcon(bm)
			.setAutoCancel(true)
			.setSmallIcon(R.drawable.ic_notif_icon);

		Intent notIntent = new Intent(context.getApplicationContext(), MainActivity.class); 
		PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notIntent, 0);
		notifb.setContentIntent(contentIntent);
		NotificationManager notManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		notManager.notify(1, notifb.build());
	}

}
