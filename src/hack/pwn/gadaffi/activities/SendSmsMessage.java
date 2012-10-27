package hack.pwn.gadaffi.activities;

import hack.pwn.gadaffi.Constants;
import hack.pwn.gadaffi.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class SendSmsMessage extends Activity {
	private static final String TAG = "SendSmsMessage";
	
	private EditText mMessage;

	private String mPhoneNumber;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_sms_message);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        
        Bundle extras = getIntent().getExtras();
        mPhoneNumber = extras.getString(Constants.KEY_RECEIVER);
        
        mMessage = (EditText) findViewById(R.id.smsText);
        
        Button sendSms = (Button) findViewById(R.id.sendSmsButton);
        
        sendSms.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				sendSms();
			}
		});
    }

    protected void sendSms() {
    	if(isMessageValid()) {
    		String message = mMessage.getText().toString();
    		Log.d(TAG, "Sending SMS Message to phone number: " + mPhoneNumber + " message: " + message);
    		SmsManager.getDefault().sendTextMessage(mPhoneNumber, null, message, null, null);
    		Toast.makeText(this, "SMS Sent", Toast.LENGTH_LONG).show();    	
    	}
    	else {
    		new AlertDialog.Builder(this)
    			.setMessage(getString(R.string.invalidSmsMessage)).create().show();
    	}
		
	}

	private boolean isMessageValid() {
		String message = mMessage.getText().toString();
		
		if(message == null) return false;
		if(message.trim().isEmpty()) return false;
		
		return true;
		
	}

	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_send_sms_message, menu);
        return true;
    }

    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
