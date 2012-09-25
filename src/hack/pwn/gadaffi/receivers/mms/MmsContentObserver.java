package hack.pwn.gadaffi.receivers.mms;

import android.content.ContentResolver;
import android.database.ContentObserver;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

class MmsContentObserver extends ContentObserver{

	private static final String TAG = "MmsContentObserver";
	private MmsContentChangedHandler mHandler;
	private ContentResolver mContentResolver;
	private MmsMonitorService mService;
	public MmsContentObserver(MmsMonitorService mmsMonitorService, MmsContentChangedHandler handler, 
			ContentResolver contentResolver) {
		super(handler);
		mHandler = handler;
		mContentResolver = contentResolver;
		mService = mmsMonitorService;
	}

	/* (non-Javadoc)
	 * @see android.database.ContentObserver#onChange(boolean)
	 */
	@Override
	public void onChange(boolean selfChange) {
		// TODO Auto-generated method stub
		super.onChange(selfChange);
		Log.v(TAG, "Entered onChange() -- " + Boolean.toString(selfChange));
		Log.v(TAG, "Starting a worker thread.");
		new Thread(new MmsContentChangedWorker(mHandler, mService, mContentResolver)).start();
		
		
	}
	
	
	
	

}
