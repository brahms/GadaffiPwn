package hack.pwn.gadaffi.receivers.mms;

import hack.pwn.gadaffi.Constants;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

class MmsContentChangedHandler extends Handler{

	private static final String TAG = "MmsContentChangedHandler";

	/* (non-Javadoc)
	 * @see android.os.Handler#handleMessage(android.os.Message)
	 */
	@Override
	public void handleMessage(Message msg) {
		// TODO Auto-generated method stub
		super.handleMessage(msg);
		Log.v(TAG, "Entered handleMessage()");
		String data = msg.getData().getString(Constants.KEY_DATA);
		Log.v(TAG, "Got message from worker: " + data);
	}
	
	
}
