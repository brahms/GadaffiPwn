package hack.pwn.gadaffi.receivers.mms;

import hack.pwn.gadaffi.Constants;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;

class MmsContentChangedWorker implements Runnable {

	private MmsContentChangedHandler mHandler;
	private ContentResolver mContentResolver;
	private MmsMonitorService mService;
	private static final String TAG = "MmsContentChangedRunnable";
	private static final String MMS_URI = "content://mms";
	private static final String MMS_INBOX_URI = MMS_URI + "/inbox";
	
	public MmsContentChangedWorker(
			MmsContentChangedHandler handler, 
			MmsMonitorService service, 
			ContentResolver contentResolver) {
		mHandler = handler;
		mService = service;
		mContentResolver = contentResolver;
	}
	
	@Override
	public void run() {
		long start = System.currentTimeMillis();
		Log.v(TAG, "Worker started.");
		Cursor cursor = null;
		Cursor cursor2 = null;
		try
		{
			Uri mmsUri = Uri.parse(MMS_URI);
			cursor = mContentResolver.query(mmsUri, null, null, null, "_id");
			cursor2 = mContentResolver.query(Uri.parse(MMS_INBOX_URI), null, null, null, "_id");
			Log.v(TAG, "Cursor has: " + cursor.getCount() + " results.");
			Log.v(TAG, "Cursor2 has " + cursor.getCount() + " results.");

			
			Log.v(TAG, "Heres the columns for mms");
			for(int i = 0; i < cursor.getColumnCount(); i++) {
				Log.v(TAG, "Column " + (i+1) + " = " + cursor.getColumnName(i));
			}
			Log.v(TAG, "Heres the columns for mms_inbox:");
			for(int i = 0; i < cursor2.getColumnCount(); i++) {
				Log.v(TAG, "Column " + (i+1) + " = " + cursor2.getColumnName(i));
			}
			
		}
		catch(Exception ex)
		{
			Log.e(TAG, "Oops.", ex);
		}
		finally
		{
			if(cursor != null) cursor.close();
			if(cursor2 != null) cursor2.close();
		}
		
		long stop = System.currentTimeMillis();
		Log.v(TAG, "Worker finished in: " + (stop - start) + " ms.");
		informDone();

	}

	private void informDone() {
		Message m = mHandler.obtainMessage();
		Bundle b = new Bundle();
		b.putString(Constants.KEY_DATA, "Done");
		m.setData(b);
		
		mHandler.sendMessage(m);
		
	}

}
