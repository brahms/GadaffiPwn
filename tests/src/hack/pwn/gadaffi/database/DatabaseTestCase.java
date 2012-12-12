package hack.pwn.gadaffi.database;

import android.test.AndroidTestCase;
import android.test.RenamingDelegatingContext;
import android.util.Log;

public class DatabaseTestCase extends AndroidTestCase{
	static final String TAG = "test.database.DatabaseTestCase";
	RenamingDelegatingContext context = null;
	protected void setUp() throws Exception {
		super.setUp();
		Log.v(TAG, "Entering startUp()");
		if(context == null) {
			context = new RenamingDelegatingContext(getContext(), "test");
			
		}
		
		BasePeer.initForTest(context);

		assertNotNull(BasePeer.databaseHelper);
		Log.v(TAG, "Exiting startUp()");
	}
	
	protected void tearDown() {
		
	}
}
