package hack.pwn.gadaffi.receivers.mms;

import hack.pwn.gadaffi.Constants;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.IBinder;
import android.util.Log;

public class MmsMonitorService extends Service {

	private static final String TAG = "MmsMonitorService";
	
	private MmsContentChangedHandler mMmsHandler;
	private MmsContentObserver       mMmsObserver;
	private SharedPreferences mPreferences;
	
	private Object mPrefLock = new Object();
	
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	
	/* (non-Javadoc)
	 * @see android.app.Service#onCreate()
	 */
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		Log.v(TAG, "Starting service.");
		mMmsHandler = new MmsContentChangedHandler();
		mMmsObserver= new MmsContentObserver(this, mMmsHandler, getContentResolver());
		
		getContentResolver().registerContentObserver(
				Uri.parse("content://mms-sms"), 
				true, 
				mMmsObserver);
		
		mPreferences = getSharedPreferences(Constants.PREFS_FILE, MODE_PRIVATE);
		
	}
	

	/* (non-Javadoc)
	 * @see android.app.Service#onDestroy()
	 */
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		Log.v(TAG, "onDestroy()");
		
		getContentResolver().unregisterContentObserver(mMmsObserver);
	}
	
	public int getMmsCount()
	{
		synchronized (mPrefLock) {
			return mPreferences.getInt(Constants.KEY_MMS_COUNT, 0);
		}
	}
	
	public int getLastMmdId()
	{
		synchronized (mPrefLock) {
			return mPreferences.getInt(Constants.KEY_MMS_ID, 0);
		}
	}
	
	public void setMmsCountAndId(int count, int id) {
		synchronized (mPrefLock) {
			Log.v(TAG, "Updating mms count and id to : " + count + " and " + id);
			mPreferences.edit()
				.putInt(Constants.KEY_MMS_COUNT, count)
				.putInt(Constants.KEY_MMS_ID,id).commit();
		}
	}
	
}
