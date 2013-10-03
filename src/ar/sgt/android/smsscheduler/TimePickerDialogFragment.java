package ar.sgt.android.smsscheduler;

import java.util.Calendar;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.widget.TimePicker;

import com.actionbarsherlock.app.SherlockDialogFragment;

public class TimePickerDialogFragment extends SherlockDialogFragment implements OnTimeSetListener {

	private OnValueSetListener valueSetListener = null;
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current time as the default values for the picker
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        // Create a new instance of TimePickerDialog and return it
        return new TimePickerDialog(getActivity(), this, hour, minute,
                DateFormat.is24HourFormat(getActivity()));
	}

	@Override
	public void onTimeSet(TimePicker view, int selectedHour, int selectedMinute) {
		String value =  new StringBuilder().append(pad(selectedHour)).append(":").append(pad(selectedMinute)).toString();
		if (this.valueSetListener != null) this.valueSetListener.setValue(value);
	}

	private static String pad(int c) {
		if (c >= 10)
		   return String.valueOf(c);
		else
		   return "0" + String.valueOf(c);
	}
	
	public void setValueSetListener(OnValueSetListener valueSetListener) {
		this.valueSetListener = valueSetListener;
	}
	
}
