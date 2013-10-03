package ar.sgt.android.smsscheduler.widget;

import java.util.Locale;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.Contacts;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filterable;
import android.widget.TextView;
import ar.sgt.android.smsscheduler.R;

public class ContactListAdapter extends CursorAdapter implements Filterable {

    private ContentResolver mContent;
    private Context mContext;
    
	private static final String[] CONTACTS_SUMMARY_PROJECTION = new String[] {
		Contacts._ID, 
		Contacts.DISPLAY_NAME, 
		Contacts.HAS_PHONE_NUMBER,
		Contacts.LOOKUP_KEY
	};
	
	private static final String DEFAULT_SORT_ORDER = ContactsContract.Contacts.DISPLAY_NAME + " COLLATE LOCALIZED ASC";
	private static final String DEFAULT_QUERY = Contacts.DISPLAY_NAME + " NOTNULL AND " + Contacts.HAS_PHONE_NUMBER + "=1 AND " + Contacts.DISPLAY_NAME + " != ''";
	private static final String FILTER_QUERY = Contacts.DISPLAY_NAME + " NOTNULL AND " + Contacts.HAS_PHONE_NUMBER + "=1 AND UPPER(" + Contacts.DISPLAY_NAME + ") GLOB ?";
	
    public ContactListAdapter(Context context) {
    	super(context, context.getContentResolver().query(Contacts.CONTENT_URI, CONTACTS_SUMMARY_PROJECTION,
    			DEFAULT_QUERY, null, DEFAULT_SORT_ORDER), false);
    	mContent = context.getContentResolver();
    	this.mContext = context;
    }
 
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
    	View view = LayoutInflater.from(context).inflate(R.layout.recipients_itemlist, null);

    	TextView mName, mNumber, mType;
    	
        mName = (TextView) view.findViewById(R.id.ccontName);
        mNumber = (TextView) view.findViewById(R.id.ccontNumber);
        mType = (TextView) view.findViewById(R.id.ccontType);

        mName.setText(cursor.getString(cursor.getColumnIndex("name")));
        mNumber.setText(cursor.getString(cursor.getColumnIndex("number")));
        mType.setText(cursor.getString(cursor.getColumnIndex("type")));
        
        return view;
/*    	final LinearLayout ret = new LinearLayout(context);
        final LayoutInflater inflater = LayoutInflater.from(context);
        mName = (TextView) inflater.inflate(android.R.layout.simple_dropdown_item_1line, parent, false);
        mNumber = (TextView) inflater.inflate(android.R.layout.simple_dropdown_item_1line, parent, false);
        ret.setOrientation(LinearLayout.VERTICAL);
 
        LinearLayout horizontal = new LinearLayout(context);
        horizontal.setOrientation(LinearLayout.HORIZONTAL);
         
        ImageView icon = new ImageView(context);
 
        mName.setText(cursor.getString(cursor.getColumnIndex("name")) + " - " + cursor.getString(cursor.getColumnIndex("type")));
        mNumber.setText(cursor.getString(cursor.getColumnIndex("number")));
 
        // setting the type specifics using JAVA
        mNumber.setTextSize(16);
        mNumber.setTextColor(Color.GRAY);
 
        Drawable image_icon  = context.getResources().getDrawable(R.drawable.ic_action_person);
        icon.setImageDrawable(image_icon);
        icon.setPadding(0, -2, 2, 6);
 
        // an example of how you can arrange your layouts programmatically
        // place the number and icon next to each other
        horizontal.addView(mNumber, new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        horizontal.addView(icon, new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
 
        ret.addView(mName, new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        ret.addView(horizontal, new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));    	

    	return ret;*/
    }
 

	@Override
    public void bindView(View view, Context context, Cursor cursor) {
		String name = cursor.getString(cursor.getColumnIndex("name"));
        String number = cursor.getString(cursor.getColumnIndex("number"));
        String type = cursor.getString(cursor.getColumnIndex("type"));
        
        ((TextView) view.findViewById(R.id.ccontName)).setText(name);
        ((TextView) view.findViewById(R.id.ccontNumber)).setText(number);
        ((TextView) view.findViewById(R.id.ccontType)).setText(type);
        
        //Drawable image_icon  = context.getResources().getDrawable(R.drawable.ic_action_person);
        // notice views have already been inflated and layout has already been set so all you need to do is set the data
//        ((TextView) ((LinearLayout) view).getChildAt(0)).setText(name);
//        LinearLayout horizontal = (LinearLayout) ((LinearLayout) view).getChildAt(1);
//        ((TextView) horizontal.getChildAt(0)).setText(number);
//        ((ImageView) horizontal.getChildAt(1)).setImageDrawable(image_icon);
    }
 
    @Override
    public String convertToString(Cursor cursor) {
        // this method dictates what is shown when the user clicks each entry in your autocomplete list
		//String name = cursor.getString(cursor.getColumnIndex("name")) + " - " + cursor.getString(cursor.getColumnIndex("type"));
        String number = cursor.getString(cursor.getColumnIndex("number"));
        return number;
    }
 
	@Override
	@SuppressLint("DefaultLocale")
    public Cursor runQueryOnBackgroundThread(CharSequence constraint) {
        // this is how you query for suggestions
        if (getFilterQueryProvider() != null) { return getFilterQueryProvider().runQuery(constraint); }
 
        String selection = null;
        String[] args = null;
        if (constraint != null) {
    		selection = FILTER_QUERY;
            args = new String[] { constraint.toString().toUpperCase(Locale.getDefault()) + "*" };
        }
        
        MatrixCursor mCursor = new MatrixCursor(new String[]{"_id", "name","number","type"});
        
        Cursor c = mContent.query( Contacts.CONTENT_URI, CONTACTS_SUMMARY_PROJECTION, selection, args, DEFAULT_SORT_ORDER);

        int i = 0;
        while (c.moveToNext()) {
        	String contactId = c.getString(c.getColumnIndex(ContactsContract.Contacts._ID));
        	Cursor phones = mContent.query(Phone.CONTENT_URI, null, Phone.CONTACT_ID + " = " + contactId, null, null);
        	while (phones.moveToNext()) {
        		String name = phones.getString(phones.getColumnIndex(Phone.DISPLAY_NAME));
        		String number = phones.getString(phones.getColumnIndex(Phone.NUMBER));
        		String type;
        		switch (phones.getInt(phones.getColumnIndex(Phone.TYPE))) {
    			case Phone.TYPE_MOBILE:
    				type = mContext.getResources().getString(R.string.type_mobile);
    				break;
    			case Phone.TYPE_WORK:
    				type = mContext.getResources().getString(R.string.type_work);
    				break;
    			case Phone.TYPE_HOME:
    				type = mContext.getResources().getString(R.string.type_home);
    				break;
    			default:
    				type = mContext.getResources().getString(R.string.type_other);
    			}
        		mCursor.addRow(new String[]{Integer.toString(i), name, number, type});
        		i++;
        	}
        }

        return mCursor;
    }

}
