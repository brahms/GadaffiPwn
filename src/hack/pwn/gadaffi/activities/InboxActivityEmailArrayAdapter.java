package hack.pwn.gadaffi.activities;

import hack.pwn.gadaffi.Constants;
import hack.pwn.gadaffi.R;
import hack.pwn.gadaffi.steganography.Email;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

public class InboxActivityEmailArrayAdapter extends ArrayAdapter<Email> {

	private final static String TAG = "activities.InboxActivityEmailArrayAdapter";
	static class ViewHolder {
		CheckBox checkBoxSelected;
		TextView textViewFrom;
		TextView textViewSubject;
		TextView textViewMessage;
		TextView textViewDate;
	}
	
	static class EmailOnClick implements OnClickListener {

		private Email email;
		private InboxActivity context;

		public EmailOnClick(InboxActivity context, Email email) {
			this.context = context;
			this.email = email;
		}
		
		@Override
		public void onClick(View v) {
			Log.v(TAG, String.format("Entered onClick for %s", email));
			Intent intent = new Intent(context, ShowEmailActivity.class);
			intent.putExtra(Constants.KEY_ID, email.getEmailId());
			context.startActivityForResult(intent, 1);
		}
		
	}
	public InboxActivityEmailArrayAdapter(InboxActivity context) {
		super(context, R.layout.row_inbox_email);
		
	}

	public InboxActivityEmailArrayAdapter(InboxActivity context,
			List<Email> emails) {
		super(context, R.layout.row_inbox_email, emails);
	}

	/* (non-Javadoc)
	 * @see android.widget.ArrayAdapter#getView(int, android.view.View, android.view.ViewGroup)
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View rowView = convertView;
		
		if(convertView == null) {
			LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			rowView = inflater.inflate(R.layout.row_inbox_email, null);
			ViewHolder viewHolder = new ViewHolder();
			viewHolder.checkBoxSelected = (CheckBox) rowView.findViewById(R.id.checkBoxSelected);
			viewHolder.textViewMessage  = (TextView) rowView.findViewById(R.id.textViewMessage);
			viewHolder.textViewSubject  = (TextView) rowView.findViewById(R.id.textViewSubject);
			viewHolder.textViewDate  = (TextView) rowView.findViewById(R.id.textViewDate);
			viewHolder.textViewFrom  = (TextView) rowView.findViewById(R.id.textViewFrom);
			rowView.setTag(viewHolder);
			
			
		}
		
		ViewHolder holder = (ViewHolder) rowView.getTag();
		Email email = getItem(position);
		holder.checkBoxSelected.setChecked(false);
		holder.textViewDate.setText(email.getTimeReceived().format("%v"));
		
		holder.textViewMessage.setText(email.getMessage());
		holder.textViewMessage.setMaxLines(1);
		
		holder.textViewSubject.setText(email.getSubject());
		holder.textViewSubject.setMaxLines(1);
		
		holder.textViewFrom.setText(email.getFrom());
		holder.textViewFrom.setMaxLines(1);
		
		rowView.setOnClickListener(new EmailOnClick(getContext(), email));
		
		return rowView;
	}
	
	@Override
	public InboxActivity getContext() {
		// TODO Auto-generated method stub
		return (InboxActivity) super.getContext();
	}

}
