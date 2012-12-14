package hack.pwn.gadaffi.activities;

import hack.pwn.gadaffi.R;
import hack.pwn.gadaffi.database.BasePeer;
import hack.pwn.gadaffi.database.EmailPeer;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class InboxActivity extends ListActivity implements OnItemClickListener {

	InboxActivityEmailArrayAdapter mAdapter = null;
	private String TAG;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
		Log.v(TAG, "Entered onCreate()");
        BasePeer.init(getApplicationContext());
        
        
        mAdapter = new InboxActivityEmailArrayAdapter(this, EmailPeer.getLatestEmails());
        setListAdapter(mAdapter);
        
        getListView().setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Log.v(TAG, "Entered onItemClick anon");
			}
		});
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
	
	




    
}
