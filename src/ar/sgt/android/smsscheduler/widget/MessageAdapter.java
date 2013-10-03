package ar.sgt.android.smsscheduler.widget;


import java.text.DateFormat;
import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import ar.sgt.android.smsscheduler.R;
import ar.sgt.android.smsscheduler.db.model.Message;
import ar.sgt.android.smsscheduler.utils.ContactHelper;

public class MessageAdapter extends ArrayAdapter<Message> {

	private Context context;
	private int layoutResourceId;
	private List<Message> objects = null;
	
	public MessageAdapter(Context context, int layoutResourceId,
			List<Message> objects) {
		super(context, layoutResourceId, objects);
		this.context = context;
		this.layoutResourceId = layoutResourceId;
		this.objects = objects;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View row = convertView;
		MessageHolder holder = null;
		
		if (row == null) {
			LayoutInflater inflater = ((Activity) context).getLayoutInflater();
			row = inflater.inflate(layoutResourceId, parent, false);
			holder = new MessageHolder();
			holder.title = (TextView) row.findViewById(R.id.txtTitle);
			holder.message = (TextView) row.findViewById(R.id.txtMessage);
			holder.schDate = (TextView) row.findViewById(R.id.txtDate);
			holder.status = (ImageView) row.findViewById(R.id.imgStatus);
			row.setTag(holder);
		} else {
			holder = (MessageHolder) row.getTag();
		}
		
		Message message = objects.get(position);
		holder.setId(message.getId());
		holder.setTitle(formatContact(message.getContactId()));
		holder.setMessage(message.getMessage());
		holder.setSchDate(formatDate(message.getSendDate()));
		holder.setStatus(message.getStatus());
		return row;
	}
	
	private String formatContact(String value) {
		String name = ContactHelper.findContactName(context, value);
		return name == null ? value : name;
	}

	private String formatDate(Date value) {
		return DateFormat.getDateTimeInstance().format(value);
	}
	
	static class MessageHolder {
	
		private long id;
		private TextView title;
		private TextView message;
		private TextView schDate;
		private ImageView status;
		
		public long getId() {
			return id;
		}
		public void setId(long id) {
			this.id = id;
		}
		public TextView getTitle() {
			return title;
		}
		public void setTitle(TextView title) {
			this.title = title;
		}
		public void setTitle(String title) {
			this.title.setText(title);
		}		
		public TextView getMessage() {
			return message;
		}
		public void setMessage(TextView message) {
			this.message = message;
		}
		public void setMessage(String message) {
			this.message.setText(message);
		}		
		public TextView getSchDate() {
			return schDate;
		}
		public void setSchDate(TextView schDate) {
			this.schDate = schDate;
		}
		public void setSchDate(String schDate) {
			this.schDate.setText(schDate);
		}
		public ImageView getStatus() {
			return status;
		}
		public void setStatus(int status) {
			switch (status) {
			case 1:
				this.status.setImageResource(R.drawable.ic_status_ok);
				break;
			case 2:
				this.status.setImageResource(R.drawable.ic_status_error);
				break;			
			default:
				this.status.setImageResource(R.drawable.ic_status_p);
				break;
			}
		}
		
	}

}
