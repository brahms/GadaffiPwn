package hack.pwn.gadaffi.activities;

import hack.pwn.gadaffi.Constants;
import hack.pwn.gadaffi.activities.PhotoPicker.StatePhoto;
import hack.pwn.gadaffi.steganography.PngStegoImage;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.test.ActivityUnitTestCase;

public class CreateEmailActivityUnitTest extends
		ActivityUnitTestCase<CreateEmail> {
	private Intent mIntent;

	public CreateEmailActivityUnitTest() {
		super(CreateEmail.class);
	}
	@Override
	public void setUp() throws Exception {
		super.setUp();
		mIntent = new Intent();
		
	}
	public void testOnCreateWithStateNoAttachments() {
		final Bundle bundle = new Bundle();
		CreateEmail.State state = new CreateEmail.State();
		state.message = "A message";
		state.phoneNumber = "15550005555";
		state.subject = "Subject";
		bundle.putParcelable(Constants.KEY_STATE, state);
		
		state = (CreateEmail.State) bundle.getParcelable(Constants.KEY_STATE);

		assertEquals("A message", state.message);
		assertEquals("15550005555", state.phoneNumber);
		assertEquals("Subject", state.subject);
		
		startActivity(mIntent, bundle, null);
		
		CreateEmail a = getActivity();
		assertNotNull(a.mState);
		assertEquals("A message", a.mState.message);
		assertEquals("15550005555", a.mState.phoneNumber);
		assertEquals("Subject", a.mState.subject);
		assertNotNull(a.mState.attachments);
		assertEquals(0, a.mState.attachments.size());
		
		Bundle b = new Bundle();
		a.onSaveInstanceState(b);
		state = (CreateEmail.State)bundle.getParcelable(Constants.KEY_STATE);
		assertNotNull(state);
		assertEquals("A message", state.message);
		assertEquals("15550005555", state.phoneNumber);
		assertEquals("Subject", state.subject);
		assertNotNull(state.attachments);
		
		
	}
	
	public void testActivityResult() {

		startActivity(mIntent, null, null);
		
		PhotoPicker.State state = new PhotoPicker.State();
		state.TotalBytesGotten = 1200;
		state.TotalBytesNeeded = 1200;
		StatePhoto photo = new StatePhoto();
		
		photo.filePath = getInstrumentation().getTargetContext().getFilesDir().toString();
		photo.height   = 400;
		photo.width    = 400;
		photo.maxBytes = PngStegoImage.getMaxBytesEncodable(400, 400);
		state.Photos.add(photo);
		Intent intent = new Intent();
		intent.putExtra(Constants.KEY_STATE, state);
		
		getActivity().onActivityResult(0, Activity.RESULT_OK, intent);
		
	}
	

}
