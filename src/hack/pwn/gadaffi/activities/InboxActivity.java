package hack.pwn.gadaffi.activities;

import hack.pwn.gadaffi.Constants;
import hack.pwn.gadaffi.R;
import hack.pwn.gadaffi.Utils;
import hack.pwn.gadaffi.database.BasePeer;
import hack.pwn.gadaffi.database.EmailEntry;
import hack.pwn.gadaffi.database.EmailPeer;
import hack.pwn.gadaffi.database.InboundPacketPeer;
import hack.pwn.gadaffi.receivers.mms.MmsMonitorService;
import hack.pwn.gadaffi.steganography.Email;
import hack.pwn.gadaffi.steganography.Packet;
import hack.pwn.gadaffi.steganography.PngStegoImage;

import java.io.File;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockListActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;


public class InboxActivity extends SherlockListActivity implements OnItemClickListener {



    static final int REQUEST_NEW = 0;
    static final int REQUEST_SHOW = 1;
    static final String TAG = "activities.InboxActivity";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        startService(new Intent(this, MmsMonitorService.class));
		Log.v(TAG, "Entered onCreate()");
        BasePeer.init(getApplicationContext());

        mAdapter = InboxActivityEmailArrayAdapter.create(this);
        setListAdapter(mAdapter);
        
        getListView().setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Log.v(TAG, "Entered onItemClick anon");
			}
		});
        
        checkForLoadIntent();
        Log.v(TAG, "Exiting onCreate()");
    }

    private void checkForLoadIntent()
    {
        Intent intent = getIntent();
        Log.v(TAG, "Entered checkForIntent() for: " + getIntent().getAction());
        if(intent != null && intent.getAction() != null && intent.getAction().equals(Intent.ACTION_VIEW)) {
            Log.v(TAG, "Ok we got an intent to view: " + getIntent().getData());
            LoadPng task = new LoadPng();
            task.execute(getIntent().getData());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
		Log.v(TAG, "Entered onCreateOptionsMenu()");
        getSupportMenuInflater().inflate(R.menu.activity_inbox, menu);
        return true;
    }

	@Override
    protected void onStart()
    {
        Log.v(TAG, "Entered onStart()");
        // TODO Auto-generated method stub
        super.onStart();
    }

    @Override
    protected void onStop()
    {
        Log.v(TAG, "Entered onStop()");
        // TODO Auto-generated method stub
        super.onStop();
    }

    /* (non-Javadoc)
	 * @see android.app.ListActivity#onDestroy()
	 */
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		Log.v(TAG, "Entered onDestroy()");
		super.onDestroy();
	}

	/* (non-Javadoc)
	 * @see android.app.ListActivity#onListItemClick(android.widget.ListView, android.view.View, int, long)
	 */
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		// TODO Auto-generated method stub
		Log.v(TAG, "Item clicked position %d" + position);
		super.onListItemClick(l, v, position, id);
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {

		Log.v(TAG, "Entered onItemClick()");
		
	}
	
	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {	
		switch(item.getItemId()) {
		case R.id.send_email:
			Log.v(TAG, "Got a send email menu click.");
			startActivityForResult(new Intent(this,  CreateEmail.class), REQUEST_NEW);
			break;
		case R.id.discard_email:
			Log.v(TAG, "Got a discard menu click.");
			mAdapter.deleteAllChecked();
			break;
		}
		return super.onMenuItemSelected(featureId, item);
	}
	
	

    @Override
    protected void onPause()
    {
        Log.v(TAG, "Entered onPause()");
        Log.v(TAG, String.format("Unregistering receiver: %s", mReceiver));
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mReceiver);
        super.onPause();
    }

    @Override
    protected void onRestart()
    {
        // TODO Auto-generate
        Log.v(TAG, "Entered onRestart()");
        super.onRestart();
    }

    @Override
    protected void onResume()
    {
        // TODO Auto-generated method stub
        Log.v(TAG, "Entered onResume()");        
        mAdapter.update();
        Log.v(TAG, String.format("Registering receiver %s for intentfilter: %s", mReceiver, mIntentFilter));
        LocalBroadcastManager.getInstance(this).registerReceiver(mReceiver, mIntentFilter);
        super.onResume();
    }

    InboxActivityEmailArrayAdapter mAdapter = null;
    IntentFilter mIntentFilter = new IntentFilter(Constants.ACTION_NEW_EMAIL);
    BroadcastReceiver mReceiver = new BroadcastReceiver(){
        
        @Override
        public void onReceive(Context context, Intent intent)
        {
            int id = intent.getIntExtra(EmailEntry._ID, -1);
            Utils._assert(id != -1);
            

            Log.v(TAG, "Entered insertNewEmail() " + id);
            Email email = EmailPeer.getEmailById(id);
            
            Log.v(TAG, String.format("Retrieved email: %s", email));
            
            Log.v(TAG, "Notifying adapter of new email");
            mAdapter.update();
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
        Log.v(TAG, String.format("Entered onActivityResult() %d, %d, %s", requestCode, requestCode, data));
        switch(requestCode)  {
            case REQUEST_NEW:
                switch(resultCode) {
                    
                }
            break;
            case REQUEST_SHOW:
                switch(resultCode) {
                    case Constants.RESULT_DELETE:
                        if (data != null) {
                            int id = data.getIntExtra(EmailEntry._ID, -1);
                            Log.v(TAG, "Got delete result for REQUEST_SHOW: " + id);
                            Utils._assert(id != -1);
                            mAdapter.delete(id);
                        }
                        else {
                            Log.e(TAG, "Got a REQUEST_SHOW returned with a RESULT_DELETE, without any data");
                        }
                }
            break;
        }
    }
    
   
    
    
    class LoadPng extends AsyncTask<Uri, Integer, Integer>{
        static final String TAG = InboxActivity.TAG;
        
        public final int RESULT_NOT_DECODABLE = 0;
        public final int RESULT_VALID_BUT_NOT_FINISHED =1;
        public final int RESULT_VALID_AND_NEW_EMAIL = 2;
        public final int RESULT_VALID_AND_ALREADY_EXISTS = 3;

        private ProgressDialog progress;
        
        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            progress = ProgressDialog.show(InboxActivity.this, "Decoding File", "Loading...", true);
        }
        @Override
        protected void onPostExecute(Integer result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            Log.v(TAG, "Entered onPostExecute()");
            progress.dismiss();
            switch(result) {
                case RESULT_NOT_DECODABLE:
                    Toast.makeText(InboxActivity.this, "Could not decode Png.", Toast.LENGTH_SHORT).show();
                    return;
                case RESULT_VALID_BUT_NOT_FINISHED:
                    Toast.makeText(InboxActivity.this, "Valid steg image, but has more parts to decode.", Toast.LENGTH_SHORT).show();
                    return;
                case RESULT_VALID_AND_NEW_EMAIL:
                    Toast.makeText(InboxActivity.this, "Valid steg image, and we have a new email!", Toast.LENGTH_SHORT).show();
                    mAdapter.update();
                    return;
                case RESULT_VALID_AND_ALREADY_EXISTS:
                    Toast.makeText(InboxActivity.this, "Valid steg image, but it already exists.", Toast.LENGTH_SHORT).show();
                    return;
                    
            }
        }
        @Override
        protected Integer doInBackground(Uri... params)
        {
            Uri uri = params[0];
            Log.v(TAG, "Entered doInBackground for: " + uri);
            
            if(uri.toString().startsWith("file")) {
                Log.v(TAG, "The uri is a file uri, but can we load.");
                File file = new File(uri.toString().substring(uri.toString().indexOf(':') + 1));
                
                Log.v(TAG, String.format("File is: %s, can_read %b", file, file.canRead()));
                if(file.canRead() == false) return RESULT_NOT_DECODABLE;
                
                BitmapFactory.Options opts = new BitmapFactory.Options();
                opts.inScaled = false;
                Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                
                if(bitmap != null) {
                    Log.v(TAG, "Succesfully decoded png to bitmap.");
                    PngStegoImage image = new PngStegoImage();
                    image.setImageBitmap(bitmap);
                    try {
                        image.decode();
                    }
                    catch(Exception ex) {
                        Log.v(TAG, "Could not decode image, ex");
                    }

                    if(image.hasEmbeddedData()) {
                        Log.v(TAG, "Trying to process packet.");
                        try {
                            Packet packet = Packet.processIncomingData(Constants.LOCAL_FROM, image.getEmbeddedData());
                            if(packet.getIsCompleted()) {
                                switch(packet.getPacketType()) {
                                    case EMAIL:
                                        Email email = (Email) packet.getPayload();
                                        Log.v(TAG, "Got email: " + email.toString());
                                        try {
                                            email.setFrom(Constants.LOCAL_FROM);
                                            EmailPeer.insertEmail(email);
                                        }
                                        catch (Exception ex) {
                                            Log.v(TAG, "Couldn't insert email", ex);
                                            return RESULT_VALID_AND_ALREADY_EXISTS;
                                        }
                                        return RESULT_VALID_AND_NEW_EMAIL;
                                }
                            } else {
                                return RESULT_VALID_BUT_NOT_FINISHED;
                            }
                        }
                        catch(Exception ex) {
                            Log.e(TAG, "Could not decode packet", ex);
                            return RESULT_NOT_DECODABLE;
                        }
                    }
                    else {
                        Log.v(TAG, "Image doesn't have any embedded data.");
                        return RESULT_NOT_DECODABLE;
                    }
                }
                else {
                    Log.v(TAG, "Could not read bitmap.");
                    return RESULT_NOT_DECODABLE;
                }
            }
            return RESULT_NOT_DECODABLE;
        }
        
    }
    
}
