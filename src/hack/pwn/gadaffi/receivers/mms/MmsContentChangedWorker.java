package hack.pwn.gadaffi.receivers.mms;

import hack.pwn.gadaffi.Constants;
import hack.pwn.gadaffi.MimeType;
import hack.pwn.gadaffi.Utils;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;

/**
 * This is the meat and potatoes of our MMS Reciver service, 
 * it handles incoming MMS and verifies if they pertain to our app
 * and if they do, processes them, all while running in the background.
 * @author cbrahms
 *
 */
class MmsContentChangedWorker implements Runnable {

	private MmsContentChangedHandler mHandler;
	private ContentResolver mContentResolver;
	private MmsMonitorService mService;
	private boolean mShouldRerun ;
	private boolean mDone;
	private static final String MMS_PART_URI = "content://mms/part";
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
		mShouldRerun = true;
		mDone = false;
	}
	/**
	 * The worker basically queries the mms content repository
	 * to see if we have any new MMS, and then to see if those new MMS
	 * pertain to app by checking their file to see if it has any stegnography stored
	 */
	@Override
	public void run() {
		Cursor cursor = null;
		do {

			long start = System.currentTimeMillis();
			Log.v(TAG, "Worker started to do some work.");
			try
			{
				String lastMmsId = mService.getLastMmdId();
				
				Log.v(TAG, "The last MMS ID we processed was: " + lastMmsId);
				
				String selection = " _id >= " + lastMmsId ;
				cursor = mContentResolver.query(Uri.parse(MMS_INBOX_URI), null, selection, null, "_id");
				Log.v(TAG, "MMS Content store has " + cursor.getCount() + " results MMS ids including the last MMS ID we used.");
				
				while(cursor.moveToNext()) {
					processMmsCursorRow(cursor);
				}
				
				
			}
			catch(Exception ex)
			{
				Log.e(TAG, "Oops this is embarassing.", ex);
			}
			finally
			{
				if(cursor != null) cursor.close();
			}
			long stop = System.currentTimeMillis();
			Log.v(TAG, "Worker finished work in: " + (stop - start) + " ms.");
		}
		while(shouldRerunOrDone());
		Log.v(TAG, "Worker done.");
		informDone();
	}

	private String getTypeString(int type) {
		switch(type) {
		case Cursor.FIELD_TYPE_BLOB: return "BLOB";
		case Cursor.FIELD_TYPE_FLOAT: return "FLOAT";
		case Cursor.FIELD_TYPE_INTEGER: return "INT";
		case Cursor.FIELD_TYPE_NULL: return "NULL";
		case Cursor.FIELD_TYPE_STRING: return "STRING";
		default: return "UNKNOWN";
		}
	}
	private void processMmsCursorRow(Cursor mmsCursor) {
		String mmsId = mmsCursor.getString(mmsCursor.getColumnIndexOrThrow("_id"));
		Log.v(TAG, "Processing Mms with ID: " + mmsId);
		String lastMmsId = mService.getLastMmdId();
		String lastPartId = (lastMmsId.equals(mmsId) ? mService.getLastPartId() : "");
		String whereClause = "mId = ?";
		if(lastPartId.isEmpty() == false) {
			Log.v(TAG, "This MMS has been previously proccessed so we'll only query for new parts, using part id: " + lastPartId);
			whereClause += " AND _id > " + lastPartId;
		}
		
		Cursor partCursor = null;
		try
		{
			partCursor = mContentResolver.query(
					Uri.parse(MMS_PART_URI), 
					null, 
					whereClause, 
					new String[]{mmsId}, 
					"_id");
			Log.v(TAG, "Our part query resulted in " + partCursor.getCount() + " parts.");
			while(partCursor.moveToNext()) {
				processMmsPartRow(partCursor, mmsId);
			}
		}
		catch(Exception ex) 
		{
			Log.e(TAG, "Oops in processMmsCursorRow()", ex);
		}
		finally 
		{
			if(partCursor != null) partCursor.close();
		}
		
		
		
	}
	private void processMmsPartRow(Cursor partCursor, String mmsId) {
		String partId = partCursor.getString(partCursor.getColumnIndexOrThrow("_id"));
		String contentType = partCursor.getString(partCursor.getColumnIndexOrThrow("ct"));
		Log.v(TAG, "Processing part with id: " + partId + " with content-type: " + contentType);
		
		MimeType type = MimeType.get(contentType); 
		
		Log.v(TAG, "Content Type: " + contentType + " maps to " + type.toString());
		switch(type) {
		case JPEG:
		case BITMAP:
		case GIF:
		case PNG:
			processMmsWithImage(partId, type, mmsId);
			break;
		case TEXT:
		default:
			Log.v(TAG, "Not doing anything for this type.");
			
		}

		//
		// Set the last ids we just used so, we don't reprocess them.
		//
		mService.setLastMmsId(mmsId);
		mService.setLastPartId(partId);
	}
	private void processMmsWithImage(String partId, MimeType type, String mmsId) {
		long start = System.currentTimeMillis();
		Log.v(TAG, MessageFormat.format("Entered processMmsWithImage() partId={0}, type={1}, mmsId={2}",
				partId, type, mmsId));
		
		
		//
		// Get the bytes for our image
		//
		byte[] imageBytes = getPart(partId);
		Log.v(TAG, "The images size is: " + Utils.formatBytes(imageBytes.length, 2));
		
		
		switch(type) {
		case JPEG:
			processMmsWithJpeg(imageBytes, mmsId, partId);
			break;
		default:
			Log.v(TAG, "Not able to process this sort of image yet.");
			break;
		}
		
		long stop = System.currentTimeMillis();
		Log.v(TAG, "Finished processMmsWithImage() in: " + (stop - start) + " ms.");
		
	}
	private void processMmsWithGif(byte[] bytes, String mmsId) {
		Log.v(TAG, "Entered processMmsWithGif()");
		
	}
	private void processMmsWithJpeg(byte[] bytes, String mmsId, String partId) {
		
		Log.v(TAG, "Entered processMmsWithJpeg()");
		
		
		Log.v(TAG, "Exited processMmsWithJpeg()");
	}
	/**
	 * The worker is asking if it should run again, if it shouldn't
	 * this sets the worker done and tells the worker not to rerun. 
	 * 
	 * Otherwise it sets should rerun to false and returns true to tell the worker
	 * to rerun.
	 * @return
	 */
	private boolean shouldRerunOrDone() {
		synchronized (this) {
			if(mShouldRerun) {
				mShouldRerun = false;
				return true;
			}
			else {
				mDone = true;
				return false;
			}
		}
	}

	/**
	 * If the worker is done, returns true.
	 * If the worker is not done, returns false and tells
	 * the worker to rerun.
	 * @return
	 */
	public boolean isDoneOrRunAgain() {
		synchronized (this) {
			if(mDone) {
				return true;
			}
			else {
				mShouldRerun = true;
				return false;
			}
		}
	}

	/**
	 * If we want to tell our handler we are done do that here.
	 */
	private void informDone() {
		Message m = mHandler.obtainMessage();
		Bundle b = new Bundle();
		b.putString(Constants.KEY_DATA, "Done");
		m.setData(b);
		
		mHandler.sendMessage(m);
		
	}
	
	private byte[] getPart(String partId) {
		BufferedInputStream partInputStream = null;
		ByteArrayOutputStream byteOutputStream = null;
		Log.v(TAG, "Attempting to retrieve bytes for part id: " + partId);
		try
		{
		    partInputStream = new BufferedInputStream(mContentResolver.openInputStream(Uri.parse(MMS_PART_URI + "/" + partId)));
		    Log.v(TAG, "Got a stream!");
		    
		    //
		    // We are usually working with big images so, start the buffer with a big buffer.
		    //
		    byteOutputStream = new ByteArrayOutputStream(20000);
		    
		    while(true) {
		    	int i = partInputStream.read();
		    	if(i == -1) break;
		    	
		    	byteOutputStream.write(i);
		    }
		    
		    byte[] ret = byteOutputStream.toByteArray();
		    Log.v(TAG, "Retrieved " + ret.length + " bytes. ");
		    if(ret.length > 0) return ret;
		}
		catch (Exception ex)
		{
			Log.e(TAG, "Exception in getPart()", ex);
		}
		finally
		{
			if(partInputStream != null) {
				try {
					partInputStream.close();
				} catch (IOException e) {
					Log.e(TAG, "Error closing input stream.", e);
				}
			}
			
			if(byteOutputStream != null) {
				try {
					byteOutputStream.close();
				} catch (IOException e) {
					Log.e(TAG, "Error closing byte stream." , e);
				}
			}
		}
		
		return null;
	}

}
