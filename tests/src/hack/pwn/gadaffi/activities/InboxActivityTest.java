package hack.pwn.gadaffi.activities;

import hack.pwn.gadaffi.Utils;
import hack.pwn.gadaffi.database.BasePeer;
import hack.pwn.gadaffi.database.EmailPeer;
import hack.pwn.gadaffi.steganography.Attachment;
import hack.pwn.gadaffi.steganography.Email;

import java.util.Arrays;

import android.test.ActivityInstrumentationTestCase2;
import android.widget.ListView;

public class InboxActivityTest extends ActivityInstrumentationTestCase2<InboxActivity> {

	InboxActivity mActivity  = null;
	
	InboxActivityEmailArrayAdapter mAdapter = null;
	
	ListView mListView = null;
	
	public InboxActivityTest() {
		super("hack.pwn.gadaffi.activities", InboxActivity.class);
	}
	
	@Override
	public void setUp() throws Exception {
		super.setUp();
		mActivity = getActivity();
		mAdapter = mActivity.mAdapter;
		
		BasePeer.initForTest(getActivity().getApplicationContext());
		
		Email email1 = new Email();
		email1.setFrom("12345");
		email1.setMessage("Hi there.");
		email1.setTimeReceived(Utils.getNow());
		email1.setSubject("Subject");
		
		Attachment attachment = new Attachment();
		attachment.setData(new byte[]{1,2,3,4,5});
		
		email1.setAttachments(Arrays.asList(new Attachment[]{attachment}));
		
		Email email2 = new Email();
		email2.setFrom("12345");
		email2.setMessage("Hi there #2");
		email2.setSubject("Subject");
		email2.setTimeReceived(Utils.getNow());
		
		EmailPeer.insertEmail(email1);
		EmailPeer.insertEmail(email2);
		
		for(int i = 0; i < 10; i++) {
			Email email = new Email();
			email.setFrom("+14-703-830-6734");
			email.setMessage("Hi there #" + i);
			email.setSubject("Subject");
			email.setTimeReceived(Utils.getNow());
			Attachment attachment3 = new Attachment();
			attachment3.setData(new byte[]{1,1,1,1,1,1,1});
			email.setAttachments(Arrays.asList(new Attachment[]{attachment3}));
			
			EmailPeer.insertEmail(email);
		}
		
		assertEquals(12, EmailPeer.getLatestEmails().size());
	}
	
	public void testPreConditions() {
		assertTrue(mActivity != null);
		assertTrue(mAdapter != null);
	}
	
	public void testWithData() {
			assertEquals(12, mAdapter.getCount());
	}
	
}
