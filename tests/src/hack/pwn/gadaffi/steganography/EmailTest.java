package hack.pwn.gadaffi.steganography;

import hack.pwn.gadaffi.Constants;
import hack.pwn.gadaffi.exceptions.DecodingException;

import java.nio.ByteBuffer;

import android.test.AndroidTestCase;

public class EmailTest extends AndroidTestCase{

	public byte[] testEncode() { 
		
		byte[] data1 = new byte[1000];
		byte[] data2 = new byte[1002];
		
		for(int i = 0 ; i< data1.length ;i++){ 
			data1[i] = (byte) (i % 255);
		}

		for(int i = 0 ; i< data2.length ;i++){ 
			data2[i] = (byte) (i % 255);
		}
		
		Email email = new Email();
		
		email.setFrom("12345");
		email.setMessage("Hello");
		email.setSubject("Intro");
		
		Attachment attachment1 = new Attachment();
		attachment1.setFilename("data1.bin");
		attachment1.setMimeType(Constants.MIME_TYPE_OCTET_STREAM);
		attachment1.setData(data1);
		

		Attachment attachment2 = new Attachment();
		attachment2.setFilename("data2.bin");
		attachment2.setMimeType(Constants.MIME_TYPE_OCTET_STREAM);
		attachment2.setData(data2);
		
		email.addAttachment(attachment1);
		email.addAttachment(attachment2);
		
		byte[] bytes = email.toBytes();
		
		assertNotNull(bytes);
		assertFalse(bytes.length == 0);
		assertEquals(2, email.getAttachments().size());
		
		return bytes;
		
	}
	
	public void testDecode() throws DecodingException {
		Email email = Email.fromByteBuffer(ByteBuffer.wrap(testEncode()).order(Constants.BYTE_ORDER));
		
		assertNotNull(email);
		assertEquals("Hello", email.getMessage());
		assertEquals("Intro", email.getSubject());
		assertEquals(2, email.getAttachments().size());
		
		Attachment a1 = email.getAttachments().get(0);
		Attachment a2 = email.getAttachments().get(1);

		assertEquals("data1.bin", a1.getFilename());
		assertEquals("data2.bin", a2.getFilename());

		assertEquals(Constants.MIME_TYPE_OCTET_STREAM, a1.getMimeType());
		assertEquals(Constants.MIME_TYPE_OCTET_STREAM, a2.getMimeType());
		
		assertEquals((Integer) 1000, a1.getDataLength());
		assertEquals((Integer) 1002, a2.getDataLength());
		
		for(int i = 0; i < a1.getDataLength(); i++) {
			assertEquals(i % 255, a1.getData()[i] & 0xFF);
		}
		for(int i = 0; i < a2.getDataLength(); i++) {
			assertEquals(i % 255, a2.getData()[i] & 0xFF);
		}
		
		
		
		
		
	}
	
	
}
