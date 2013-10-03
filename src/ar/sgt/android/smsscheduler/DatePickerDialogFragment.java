package ar.sgt.android.smsscheduler;

import java.util.Calendar;
import java.util.GregorianCalendar;

import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
import android.os.Bundle;
import android.widget.DatePicker;

import com.actionbarsherlock.app.SherlockDialogFragment;

public class DatePickerDialogFragment extends SherlockDialogFragment implements OnDateSetListener {
	
	private OnValueSetListener valueSetListener = null;
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DATE);
        
        return new DatePickerDialog(getActivity(), this, year, month, day);
	}

//	private static String pad(int c) {
//		if (c >= 10)
//		   return String.valueOf(c);
//		else
//		   return "0" + String.valueOf(c);
//	}
	
	public void setValueSetListener(OnValueSetListener valueSetListener) {
		this.valueSetListener = valueSetListener;
	}

	@Override
	public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
		Calendar calendar = new GregorianCalendar();
		calendar.set(year, monthOfYear, dayOfMonth);
		String value = java.text.DateFormat.getDateInstance().format(calendar.getTime());
		if (this.valueSetListener != null) this.valueSetListener.setValue(value);
	}
	
}
