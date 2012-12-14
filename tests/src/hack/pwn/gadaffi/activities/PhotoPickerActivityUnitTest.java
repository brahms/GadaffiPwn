package hack.pwn.gadaffi.activities;

import hack.pwn.gadaffi.Constants;
import hack.pwn.gadaffi.R;
import hack.pwn.gadaffi.activities.PhotoPicker.State;
import android.content.Intent;
import android.test.ActivityUnitTestCase;

public class PhotoPickerActivityUnitTest extends
		ActivityUnitTestCase<PhotoPicker> {
	private State state;
	private Intent intent;

	public PhotoPickerActivityUnitTest() {
		super(PhotoPicker.class);
	}
	
	@Override
	public void setUp() throws Exception{
		super.setUp();
	    state = new State();
	    intent = new Intent();
		state.TotalBytesGotten = 0;
		state.TotalBytesNeeded = 1200;
		intent.putExtra(Constants.KEY_STATE, state);
	}
	public void testCreate() {

		startActivity(intent, null, null);
		assertNotNull(getActivity().mState);
		assertEquals(state.TotalBytesGotten, getActivity().mState.TotalBytesGotten);
		assertEquals(state.TotalBytesNeeded, getActivity().mState.TotalBytesNeeded);
	}
	
	public void testOnImageClick() {

		startActivity(intent, null, null);
		getActivity().mImageView.callOnClick();
		Intent request = getStartedActivityIntent();
		assertEquals(Intent.ACTION_PICK, request.getAction());
		assertEquals("image/*", request.getType());
		assertEquals(R.id.imageView, getStartedActivityRequest());
	}

}
