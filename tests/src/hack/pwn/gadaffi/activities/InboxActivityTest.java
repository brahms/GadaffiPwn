package hack.pwn.gadaffi.activities;

import hack.pwn.gadaffi.Constants;
import hack.pwn.gadaffi.Utils;
import hack.pwn.gadaffi.database.BasePeer;
import hack.pwn.gadaffi.database.EmailPeer;
import hack.pwn.gadaffi.steganography.Attachment;
import hack.pwn.gadaffi.steganography.Email;

import java.util.Arrays;

import android.test.ActivityInstrumentationTestCase2;
import android.webkit.MimeTypeMap;
import android.widget.ListView;

public class InboxActivityTest extends ActivityInstrumentationTestCase2<InboxActivity> {

	InboxActivity mActivity  = null;
	
	InboxActivityEmailArrayAdapter mAdapter = null;
	
	ListView mListView = null;
	
	public InboxActivityTest() {
		super(InboxActivity.class);
	}
	
	@Override
	public void setUp() throws Exception {
		super.setUp();
		mActivity = getActivity();
		mAdapter = mActivity.mAdapter;
		
		BasePeer.initForTest(getActivity().getApplicationContext());
		
		for(int i = 0; i < 10; i++) {
			Email email = new Email();
			email.setFrom("+14-703-830-6734");
			email.setMessage("Hi there #" + i);
			email.setSubject("Subject");
			email.setTimeReceived(Utils.getNow());
			Attachment attachment3 = new Attachment();
			attachment3.setFilename("hello.txt");
			attachment3.setMimeType(MimeTypeMap.getSingleton().getMimeTypeFromExtension("txt"));
			attachment3.setData(new String("This is a text file.").getBytes(Constants.CHARSET));
            Attachment attachment4 = new Attachment();
            attachment4.setFilename("hello2.txt");
            attachment4.setMimeType(MimeTypeMap.getSingleton().getMimeTypeFromExtension("txt"));
            attachment4.setData(new String("This is a text file. #2").getBytes(Constants.CHARSET));
			email.setAttachments(Arrays.asList(new Attachment[]{attachment3,attachment4}));
			
			EmailPeer.insertEmail(email);
		}
		
		assertEquals(10, EmailPeer.getLatestEmails().size());
		
	}
	
	public void testPreConditions() {
		assertTrue(mActivity != null);
		assertTrue(mAdapter != null);
	}
	
	public void testWithData() {
			assertEquals(10, mAdapter.getCount());
	}
	
	public void testClearData() {
        BasePeer.initForTest(getActivity().getApplicationContext());
	}
	
	
}
