package ar.sgt.android.smsscheduler.receiver;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import ar.sgt.android.smsscheduler.db.dao.MessageDAO;
import ar.sgt.android.smsscheduler.db.model.Message;

public class SchedulerIntentFactory {

	private Context ctx;
	
	public SchedulerIntentFactory(Context ctx) {
		this.ctx = ctx;
	}
	
	public void cancelPendingIntent(Message m) {
		Intent intent = new Intent(ctx, SchedulerHandlerReceiver.class);
		intent.setAction(SchedulerHandlerReceiver.SMS_ACTION);
		intent.putExtra(SchedulerHandlerReceiver.SMS_ID, m.getId());
		PendingIntent pi = PendingIntent.getBroadcast(ctx, m.getId().intValue(), intent, PendingIntent.FLAG_UPDATE_CURRENT);
		AlarmManager alarmManager = (AlarmManager) ctx.getSystemService(Context.ALARM_SERVICE);
		alarmManager.cancel(pi);
	}
	
	public void createNextSchedule() {
		
		MessageDAO messageDAO = new MessageDAO(ctx);
		messageDAO.open();
		Message m = messageDAO.getNextMessage();
		messageDAO.close();

		if(m != null){
			Intent intent = new Intent(ctx, SchedulerHandlerReceiver.class);
			intent.setAction(SchedulerHandlerReceiver.SMS_ACTION);
			intent.putExtra(SchedulerHandlerReceiver.SMS_ID, m.getId());
			
			PendingIntent pi = PendingIntent.getBroadcast(ctx, m.getId().intValue(), intent, PendingIntent.FLAG_UPDATE_CURRENT);
			
			AlarmManager alarmManager = (AlarmManager) ctx.getSystemService(Context.ALARM_SERVICE);
			if(m.getSendDate().getTime() > System.currentTimeMillis()){
				Log.d("createNextSchedule", "Scheduled message to send on " + m.getSendDate());
				alarmManager.set(AlarmManager.RTC_WAKEUP, m.getSendDate().getTime(), pi);
			}else{
				Log.d("createNextSchedule", "Schedule is in the past. " + m.getSendDate());
				//When a devices boots up and this broadcast receiver fires up, there's a delay for the device to get functional as in
				//network and services. So we set the Pending Intent with a delay of 3 minutes ~ 180000 milliseconds.
				//alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 180000, pi);
			}
		} else {
			Log.d("createNextSchedule", "No message scheduled");
		}
	}
	
	
}
