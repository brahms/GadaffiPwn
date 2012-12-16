package hack.pwn.gadaffi.receivers.png;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class PngBroadcastReceiver extends BroadcastReceiver
{
    static final String TAG = "receivers.png.PngBroadcastReceiver";

    @Override
    public void onReceive(Context context, Intent intent)
    {
        Log.v(TAG, "Entered onReceived() for intent: " + intent);
    }

}
