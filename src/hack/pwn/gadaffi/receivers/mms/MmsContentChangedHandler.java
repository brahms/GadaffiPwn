package hack.pwn.gadaffi.receivers.mms;

import hack.pwn.gadaffi.Constants;
import hack.pwn.gadaffi.database.EmailEntry;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

class MmsContentChangedHandler extends Handler{

	public static final int EVENT_NEW_PACKET = 0;
	private static final String TAG = "MmsContentChangedHandler";
	private MmsMonitorService mService = null;

	
	public MmsContentChangedHandler(MmsMonitorService service) {
		super();
		mService = service;
	}
	/* (non-Javadoc)
	 * @see android.os.Handler#handleMessage(android.os.Message)
	 */
	@Override
	public void handleMessage(Message msg) {
		// TODO Auto-generated method stub
		super.handleMessage(msg);
		Log.v(TAG, "Entered handleMessage()");
		
		switch(msg.what) {
		    case Constants.MESSAGE_NEW_EMAIL:
		        Log.v(TAG, "Entered handleMessage: MESSAGE_NEW_EMAIL");
		     
		        Intent intent = new Intent();
		        
		        intent.setAction(Constants.ACTION_NEW_EMAIL);
		        intent.putExtra(EmailEntry._ID, msg.getData().getInt(EmailEntry._ID));   
		        Log.v(TAG, "Got a new email message, sending out intent: " + Constants.ACTION_NEW_EMAIL + " for email with id: " +  msg.getData().getInt(EmailEntry._ID));

		        
		        
		        LocalBroadcastManager
		            .getInstance(mService)
		            .sendBroadcast(intent);
		}
	}
	


	
	
}
