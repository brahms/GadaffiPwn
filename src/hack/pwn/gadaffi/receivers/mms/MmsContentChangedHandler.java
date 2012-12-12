package hack.pwn.gadaffi.receivers.mms;

import hack.pwn.gadaffi.Constants;
import android.os.Handler;
import android.os.Message;
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
		
		String action = msg.getData().getString(Constants.KEY_TYPE);
		if(action.equals(Constants.ACTION_NEW_PACKET)) {
			mService.handleNewPacket(msg.getData().getInt(Constants.KEY_ID));
		}
	}
	
	
}
