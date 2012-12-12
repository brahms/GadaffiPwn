package hack.pwn.gadaffi.steganography;

import java.io.File;

import android.test.AndroidTestCase;

public class OutboundMmsTest extends AndroidTestCase {

	protected void setUp() throws Exception {
		super.setUp();
	}


	public void testGenerateImageFile() {
		assertEquals("1.png", OutboundMms.generateImageFile(1, "png"));
	}
	
	public void testGetFile() {
		OutboundMms mms = new  OutboundMms();
		
		mms.setImageFilename(OutboundMms.generateImageFile(1, "png"));
		
		File file = mms.getFile(getContext());
		
		assertEquals("1.png", file.getName());
	}

}
