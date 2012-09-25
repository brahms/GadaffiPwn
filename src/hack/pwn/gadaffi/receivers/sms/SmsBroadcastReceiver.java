package hack.pwn.gadaffi.receivers.sms;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsMessage;
import android.util.Log;

public class SmsBroadcastReceiver extends BroadcastReceiver {
	private static final String TAG = "SmsMonitor";
	private static final String SMS_RECEIVED = "android.provider.Telephony.SMS_RECEIVED";

	@Override
	public void onReceive(Context context, Intent intent) {
		if (intent != null && intent.getAction() != null) {

			Log.v(TAG, "Received intent: " + intent.getAction());

			if (SMS_RECEIVED.compareToIgnoreCase(intent.getAction()) == 0) {
				handleSmsReceived(intent);
			} else {
				Log.d(TAG, "Received intent that SmsMonitor cannot handle.");
			}
		}

	}

	private void handleSmsReceived(Intent intent) {
		Log.v(TAG, "Entered handleSmsReceived()");
		long start = System.currentTimeMillis();
		
		Object[] pduArray = (Object[]) intent.getExtras().get("pdus");
		SmsMessage[] messages = new SmsMessage[pduArray.length];
		for (int i = 0; i < pduArray.length; i++) {
			messages[i] = SmsMessage.createFromPdu((byte[]) pduArray[i]);
			Log.d(TAG, "From: " + messages[i].getOriginatingAddress());
			Log.d(TAG, "Msg: " + messages[i].getMessageBody());
		}
		long stop = System.currentTimeMillis();
		Log.v(TAG, "Exited handleSmsReceived() -- " + (stop - start) + " ms");
	}

}
