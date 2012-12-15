package hack.pwn.gadaffi.receivers.mms;

import android.content.ContentResolver;
import android.database.ContentObserver;
import android.util.Log;

class MmsContentObserver extends ContentObserver {

	private static final String TAG = "MmsContentObserver";
	private MmsContentChangedHandler mHandler;
	private ContentResolver mContentResolver;
	private MmsMonitorService mService;
	private MmsContentChangedWorker mWorker;
	public MmsContentObserver(MmsMonitorService mmsMonitorService,
			MmsContentChangedHandler handler, ContentResolver contentResolver) {
		super(handler);
		mHandler = handler;
		mContentResolver = contentResolver;
		mService = mmsMonitorService;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.database.ContentObserver#onChange(boolean)
	 */
	@Override
	public synchronized void onChange(boolean selfChange) {
		// TODO Auto-generated method stub
		super.onChange(selfChange);
		Log.v(TAG, "Entered onChange() -- " + Boolean.toString(selfChange));
		if(mWorker == null || mWorker.isDoneOrRunAgain()) {
			Log.v(TAG, "Starting a worker thread.");
			mWorker = new MmsContentChangedWorker(mHandler, mService,
					mContentResolver);
			new Thread(mWorker).start();
		}
		else {
			Log.v(TAG, "Signaled existing worker thread to rerun.");
		}

	}

}
