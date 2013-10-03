package ar.sgt.android.smsscheduler.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootCompleteReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context ctx, Intent intent) {
		SchedulerIntentFactory factory = new SchedulerIntentFactory(ctx);
		factory.createNextSchedule();
	}

}
