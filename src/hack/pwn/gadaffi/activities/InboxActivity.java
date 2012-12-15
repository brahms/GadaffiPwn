package hack.pwn.gadaffi.activities;

import hack.pwn.gadaffi.Constants;
import hack.pwn.gadaffi.R;
import hack.pwn.gadaffi.Utils;
import hack.pwn.gadaffi.database.BasePeer;
import hack.pwn.gadaffi.database.EmailEntry;
import hack.pwn.gadaffi.database.EmailPeer;
import hack.pwn.gadaffi.steganography.Email;
import android.app.ListActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class InboxActivity extends ListActivity implements OnItemClickListener {

	InboxActivityEmailArrayAdapter mAdapter = null;
	IntentFilter mIntentFilter = new IntentFilter(Constants.ACTION_NEW_EMAIL);
	BroadcastReceiver mReceiver = null; 


    private String TAG;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
 
		Log.v(TAG, "Entered onCreate()");
        BasePeer.init(getApplicationContext());

        getListView().setStackFromBottom(true);
        mAdapter = new InboxActivityEmailArrayAdapter(this, EmailPeer.getLatestEmails());
        setListAdapter(mAdapter);
        
        getListView().setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Log.v(TAG, "Entered onItemClick anon");
			}
		});
       
        mReceiver = new BroadcastReceiver(){
            
            @Override
            public void onReceive(Context context, Intent intent)
            {
                Toast.makeText(InboxActivity.this, "Received new email.", Toast.LENGTH_SHORT).show();
                int id = intent.getIntExtra(EmailEntry._ID, -1);
                Utils._assert(id != -1);
                

                Log.v(TAG, "Entered insertNewEmail() " + id);
                Email email = EmailPeer.getEmailById(id);
                
                Log.v(TAG, String.format("Retrieved email: %s", email));
                
                mAdapter.add(email);
                Log.v(TAG, "Notifiying adapter of new data.");
                mAdapter.notifyDataSetChanged();
            }
        };
        registerReceiver(mReceiver, mIntentFilter);
        Log.v(TAG, "Exiting onCreate()");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
		Log.v(TAG, "Entered onCreateOptionsMenu()");
        getMenuInflater().inflate(R.menu.activity_inbox, menu);
        return true;
    }

	/* (non-Javadoc)
	 * @see android.app.ListActivity#onDestroy()
	 */
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		Log.v(TAG, "Entered onDestroy()");
		super.onDestroy();
		unregisterReceiver(mReceiver);
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
			startActivityForResult(new Intent(this,  CreateEmail.class), 0);
			break;
		case R.id.discard_email:
			Log.v(TAG, "Got a discard menu click.");
			break;
		}
		return super.onMenuItemSelected(featureId, item);
	}
	
	

    @Override
    protected void onPause()
    {
        Log.v(TAG, "Entered onPause()");
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
        super.onResume();
    }


    
}
