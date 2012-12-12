package hack.pwn.gadaffi.database;

import hack.pwn.gadaffi.Constants;
import hack.pwn.gadaffi.steganography.FilePayload;


public class FilePayloadPeerTest extends DatabaseTestCase {
	
	byte[] mBytes = null;

	
	@Override
	public void setUp() throws Exception {
		super.setUp();
		
		mBytes = new byte[12000];
		
		for(int i = 0; i < mBytes.length; i++) {
			mBytes[i] = (byte) (i%255);
		}
	}
	
	public FilePayload testInsertFilePayload() throws Exception {
		
		FilePayload file = new FilePayload();
		file.setBinaryData(mBytes);
		file.setMimeType(Constants.MIME_TYPE_OCTET_STREAM);
		file.setName("data.bin");
		file.setFrom("12345");
		
		FilePeer.insertFile(file);
		
		return file;
	}
	
	public void testGetSingleFile() throws Exception {
		FilePayload original = testInsertFilePayload();
		FilePayload saved    = FilePeer.getFilePayloadById(original.getFileId());
		
		assertEquals(original.getFileId(), saved.getFileId());
		assertEquals(original.getPayloadId(), saved.getPayloadId());
		assertEquals(original.getMimeType(), saved.getMimeType());
		assertEquals(original.getName(), saved.getName());
		assertEquals(original.getTimeReceived().toMillis(false), saved.getTimeReceived().toMillis(false));
	}
	
	
	
	
	
}
