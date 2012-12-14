package hack.pwn.gadaffi.providers;

import hack.pwn.gadaffi.database.BasePeer;
import hack.pwn.gadaffi.database.OutboundMmsPeer;
import hack.pwn.gadaffi.images.BitmapScaler;
import hack.pwn.gadaffi.steganography.Email;
import hack.pwn.gadaffi.steganography.OutboundMms;
import hack.pwn.gadaffi.steganography.Packet;
import hack.pwn.gadaffi.steganography.PngStegoImage;
import hack.pwn.gadaffi.test.R;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import android.content.ContentResolver;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.test.ProviderTestCase2;
import android.test.RenamingDelegatingContext;
import android.webkit.MimeTypeMap;

public class MmsProviderTest extends ProviderTestCase2<MmsProvider> {

	private static final String BASE_URI = "content://" + MmsProvider.PROVIDER_NAME + "/mms/";
	private Bitmap mCoverImage;
	private OutboundMms mMms;
	private RenamingDelegatingContext renamingContext = null;
	private Uri mUri;
	public MmsProviderTest() throws Exception {
		super(MmsProvider.class, MmsProvider.PROVIDER_NAME);
	}

	protected void setUp() throws Exception {
		super.setUp();

		if(renamingContext == null) {
			renamingContext = new RenamingDelegatingContext(getContext(), "test");
		}
		BasePeer.initForTest(renamingContext);
		
		int target = 100;
		BitmapScaler scaler = new BitmapScaler(getContext().getResources(), R.drawable.flower, target);
		mCoverImage = scaler.getScaled();
		assertEquals(target, mCoverImage.getHeight());
		assertEquals(target, mCoverImage.getWidth());
		
		Email email = new Email();
		
		Packet p = Packet.encode(email, Arrays.asList(new Integer[]{
				PngStegoImage.getMaxBytesEncodable(mCoverImage)}));
		
		List<OutboundMms> mmses = OutboundMmsPeer.insertPngStegoImage(p, "12345", Arrays.asList(new Bitmap[]{mCoverImage}));
		
		assertEquals(1, mmses.size());
		
	    mMms = mmses.get(0);
	    assertEquals((Integer) 1, mMms.getId());
	    assertEquals("12345", mMms.getTo());
	    
	    mUri =  Uri.parse(BASE_URI + "1");
	}

	public void testGetStreamTypesUriString() {
		String[] types = getMockContentResolver().getStreamTypes(mUri, null);
		assertNotNull(types);
		assertEquals(1, types.length);
		assertEquals(MimeTypeMap.getSingleton().getMimeTypeFromExtension("png"), types[0]);
		
	}

	public void testOpenFileUriString() throws IOException {
		ParcelFileDescriptor fd = getMockContentResolver().openFileDescriptor(mUri, ContentResolver.SCHEME_FILE);
		
		assertNotNull(fd);
		assertTrue(fd.getStatSize() > 0);
		fd.close();
	}

}
