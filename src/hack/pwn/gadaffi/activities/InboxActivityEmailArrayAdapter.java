package hack.pwn.gadaffi.activities;

import hack.pwn.gadaffi.Constants;
import hack.pwn.gadaffi.R;
import hack.pwn.gadaffi.Utils;
import hack.pwn.gadaffi.database.EmailPeer;
import hack.pwn.gadaffi.steganography.Email;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

public class InboxActivityEmailArrayAdapter extends ArrayAdapter<Email> {

	private final static String TAG = "activities.InboxActivityEmailArrayAdapter";
	static class ViewHolder {
		CheckBox checkBoxSelected;
		TextView textViewFrom;
		TextView textViewSubject;
		TextView textViewMessage;
		TextView textViewDate;
		TextView textViewAttachments;
	}
	
	@SuppressLint("UseSparseArrays")
	//
	// We do not use a sparse array here, because this is a tad easier to iterate through, and we don't have
	// the biggest dataset
	// 
    HashMap<Integer, Boolean> toDelete = new HashMap<Integer, Boolean>();
	
	class EmailOnClick implements OnClickListener {

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
			intent.putExtra(Constants.KEY_ID, (int) email.getEmailId());
			context.startActivityForResult(intent, InboxActivity.REQUEST_SHOW);
		}
		
	}
	
	class EmailCheckboxOnClick implements OnClickListener {

        private Email email;
        public EmailCheckboxOnClick (Email email) {
	        this.email = email;
	    }
        @Override
        public void onClick(View v)
        {
            Log.v(TAG, String.format("Entered onClick for email checkbox: " + email));
            Boolean currentVal = toDelete.get(email.getEmailId());
            boolean newVal = (currentVal == null || currentVal == false) ? true : false;
            
            Log.v(TAG, String.format("New value for email's toDelete: " + newVal));
            
            toDelete.put(email.getEmailId(), newVal);
            
        }
	    
	}

    private Integer mId = 0;
	
	public static InboxActivityEmailArrayAdapter create(InboxActivity context) {
	    List<Email> emails = EmailPeer.getLatestEmails();
        return new InboxActivityEmailArrayAdapter(context, emails);
	}

	public InboxActivityEmailArrayAdapter(InboxActivity context,
			List<Email> emails) {
		super(context, R.layout.row_inbox_email, emails);
		Log.v(TAG, "Entered constructer");
		updateMax();
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
			viewHolder.textViewAttachments = (TextView) rowView.findViewById(R.id.textViewAttachments);
			rowView.setTag(viewHolder);
			
			
		}
		
		ViewHolder holder = (ViewHolder) rowView.getTag();
		Email email = getItem(position);
		holder.checkBoxSelected.setChecked(false);
		holder.textViewDate.setText(Utils.getFormattedDate(email.getTimeReceived()));
		
		holder.textViewMessage.setText(email.getMessage());
		holder.textViewMessage.setMaxLines(1);
		
		holder.textViewSubject.setText(email.getSubject());
		holder.textViewSubject.setMaxLines(1);
		
		holder.textViewFrom.setText(email.getFrom());
		holder.textViewFrom.setMaxLines(1);
		
		rowView.setOnClickListener(new EmailOnClick(getContext(), email));
		holder.checkBoxSelected.setOnClickListener(new EmailCheckboxOnClick(email));
		
		holder.textViewAttachments.setText(String.format("%d attachment%s.",email.getAttachments().size(), email.getAttachments().size() == 1 ? "" : "s"));
		
		return rowView;
	}
	
	@Override
	public InboxActivity getContext() {
		return (InboxActivity) super.getContext();
	}
	
	public void update() {
	    List<Email> emails = EmailPeer.getLatestEmailsGreaterThanId(mId);
	    
	    Log.v(TAG, "Entered update(), total emails returned: " + emails.size());
	    
	    if(emails.size() > 1) {
	        Log.v(TAG, String.format("Email id 0: %d, Email id last: %d", emails.get(0).getEmailId(), emails.get(emails.size()-1).getEmailId()));
	    }
	    else {
	        Log.v(TAG, "Only " + emails.size() + " emails retrieved.");
	    }
	    
	    if(emails.isEmpty() == false) {
	        
	        for(Email email : emails) {
	            insert(email, 0);
	        }
	        
	        Toast toast = Toast.makeText(getContext(), "Received an email: " , Toast.LENGTH_SHORT);
	        toast.setGravity(Gravity.TOP, 0, 0);
	        toast.show();
	        
	        updateMax();
	        notifyDataSetChanged();
	    }
	    
	}
	
	public void deleteAllChecked() {
	    Log.v(TAG, "Entered deleteAllChecked()");
	    Iterator<Entry<Integer, Boolean>> it = toDelete.entrySet().iterator();
	    
	    while(it.hasNext()) {
	        Entry<Integer, Boolean> entry = it.next();
	        int id = entry.getKey();
	        boolean check = entry.getValue();
	        List<Integer> ids = new ArrayList<Integer>(toDelete.size());
	        if(check) {
	            Log.v(TAG, "Deleting email with id: " + id);
	            ids.add(id);
	        }
	        
	        EmailPeer.deleteEmailsByIds(ids);
	        reset();
	    }
	}

    private void reset()
    {
        Log.v(TAG, "Entered reset()");
        clear();
        addAll(EmailPeer.getLatestEmails());
        updateMax();
        notifyDataSetChanged();
    }

    private void updateMax()
    {
        Log.v(TAG, "Entered updateMax()");
        Log.v(TAG, "Current max id: "+ mId);
        mId = (isEmpty()) ? 0 : getItem(0).getEmailId();
        Log.v(TAG, "New max id: " + mId);
    }

    public void delete(int id)
    {
        Log.v(TAG, "Entered delete() " + id);
        EmailPeer.deleteEmailsByIds(Arrays.asList(new Integer[]{id}));
        reset();
        
    }
	

}
