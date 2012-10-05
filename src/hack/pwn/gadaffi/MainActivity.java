package hack.pwn.gadaffi;

import hack.pwn.gadaffi.receivers.mms.MmsMonitorService;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

public class MainActivity extends Activity {


	private static final String TAG = "MainActivity";
    
    private CheckBox mToggleEncryptionCheckbox;
    private EditText mReceiverText;
    private SharedPreferences mSettings;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        //
        // Initialize our shared preferences file, where we can save
        // some state
        // 
        mSettings = getSharedPreferences(Constants.PREFS_FILE, MODE_PRIVATE);
        
        //
        // Init our widgets for this activity, then load their data from the
        // shared preferences file.
        //
        mToggleEncryptionCheckbox = (CheckBox) findViewById(R.id.toggleEncryption);
        mReceiverText = (EditText) findViewById(R.id.receiverPhoneNumber);
        
        loadData();

        // 
        // Initialize our buttons, then set their on click listeners.
        //
        Button sendSmsButton = (Button) findViewById(R.id.sendSmsButton);
        Button sendMmsButton = (Button) findViewById(R.id.sendMmsButton);
        
        sendSmsButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				onSmsSendMessage();
			}
		});
        
        sendMmsButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				onMmsSendMessage();
			}
		});
        

		startService(new Intent(this, MmsMonitorService.class));
    }


	private void loadData() { 
		Log.v(TAG, "Loading data.");
		mToggleEncryptionCheckbox.setChecked(mSettings.getBoolean(Constants.KEY_USE_ENCRYPTION, false));
		mReceiverText.setText(mSettings.getString(Constants.KEY_RECEIVER, ""));
    
	}

	private void saveData() {
		Log.v(TAG, "Saving data.");
		Editor edit = mSettings.edit();
		
		edit.putBoolean(Constants.KEY_USE_ENCRYPTION, mToggleEncryptionCheckbox.isChecked());
		edit.putString(Constants.KEY_RECEIVER, mReceiverText.getText().toString());
		edit.commit();
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onPause()
	 */
	@Override
	protected void onPause() {
		super.onPause();
		saveData();
		
	}


	/* (non-Javadoc)
	 * @see android.app.Activity#onDestroy()
	 */
	@Override
	protected void onDestroy() {
		//
		// For now we stop the service whenever we aren't actually in the foreground.
		//
		stopService(new Intent(this, MmsMonitorService.class));
		// TODO Auto-generated method stub
		super.onDestroy();
	}


	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
    
    protected void onSmsSendMessage()
    {
    	Log.v(TAG, "onSmsSendMessage()");
    	if(isValidReceiver())
    	{
    		Intent i = new Intent(this, SendSmsMessage.class);
    		i.putExtra(Constants.KEY_RECEIVER, mReceiverText.getText().toString());
    		i.putExtra(Constants.KEY_USE_ENCRYPTION, mToggleEncryptionCheckbox.isChecked());
    		
    		startActivity(i);
    	}
    }


    protected void onMmsSendMessage() {
    	Log.v(TAG, "onMmsSendMessage()");
    	

    	if(isValidReceiver())
    	{
    		Intent i = new Intent(this, PhotoPicker.class);
    		i.putExtra(Constants.KEY_RECEIVER, mReceiverText.getText().toString());
    		i.putExtra(Constants.KEY_USE_ENCRYPTION, mToggleEncryptionCheckbox.isChecked());
    		
    		startActivity(i);
    	}
		
	}


	private boolean isValidReceiver() {
		String phoneNumber = mReceiverText.getText().toString().trim();
		
		if(phoneNumber.length() < 4) {
			new AlertDialog.Builder(this)
				.setMessage(getString(R.string.alertInvalidPhoneNumber))
				.create()
				.show();
			return false;
		}
		return true;
	}
}
