package hack.pwn.gadaffi.steganography;

import hack.pwn.gadaffi.Constants;
import hack.pwn.gadaffi.exceptions.EncodingException;

import java.util.Arrays;
import java.util.List;
import java.util.zip.CRC32;

import android.test.AndroidTestCase;

public class PacketTestCase extends AndroidTestCase {

	protected void setUp() throws Exception {
		super.setUp();
	}

	public void testPacket() {
		
	}

	public void testEncodeFileShouldFailIfNotEnoughMaxLengths() {
		byte[] fileData = new byte[]{1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30,31,32,33,34,35,36,37,38,39,40};
		
		Attachment file = new Attachment();
		file.setData(fileData);
		file.setFilename("data.stuff");
		file.setMimeType(Constants.MIME_TYPE_OCTET_STREAM);
		
		Email email = new Email();
		email.addAttachment(file);
		List<Integer> maxLengths = Arrays.asList(new Integer[]{10, 10});
		
		try{
			Packet.encode(email, maxLengths);
			fail("Packet didn't throw an encoding exception.");
		}
		catch(EncodingException ex) {
			
		}
	}
	public void testEncodeFile() throws EncodingException {
		byte[] fileData = new byte[]{1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30,31,32,33,34,35,36,37,38,39,40};
		Attachment file = new Attachment();
		file.setData(fileData);
		file.setFilename("data.stuff");
		file.setMimeType(Constants.MIME_TYPE_OCTET_STREAM);
		Email email = new Email();
		email.addAttachment(file);
		List<Integer> maxLengths = Arrays.asList(new Integer[]{10, 10, 10, 10, 10, 10, 10, 100});
		
		Packet packet = Packet.encode(email, maxLengths);
		
		assertTrue(packet.isValid());
		assertNotNull(packet.getParts());
		assertFalse(packet.getParts().size() == 0);
	}
	
	public void testProcessIncomingDataOfOnePart() throws Exception {
		byte[] fileData = new byte[]{1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30,31,32,33,34,35,36,37,38,39,40};
		Email email = new Email();
		Attachment file1 = new Attachment();
		file1.setData(fileData);
		file1.setFilename("data.stuff");
		file1.setMimeType(Constants.MIME_TYPE_OCTET_STREAM);
		email.addAttachment(file1);
		List<Integer> maxLengths = Arrays.asList(new Integer[]{300});
		
		
		
		Packet packet1 = Packet.encode(email, maxLengths);
		packet1.setSequenceNumber((byte) 12345);
		
		byte[] part1bytes = packet1.getParts().get(0).encode();
		
		Packet packet2 = Packet.processIncomingData("12345", part1bytes);
		
		assertTrue(packet2.isValid());
		assertTrue(packet2.getIsCompleted());
		assertEquals(1, packet2.getParts().size());
		assertEquals("12345", packet2.getFrom());
		assertTrue(packet2.getPayload() instanceof Email);
		Attachment file2 = (Attachment) ((Email)packet2.getPayload()).getAttachments().get(0);
		
		assertEquals(file1.getMimeType(), file2.getMimeType());
		assertEquals(file1.getFilename(), file2.getFilename());
		assertEquals(file1.getData().length, file2.getData().length);
		
		CRC32 crc1 = new CRC32();
		crc1.update(file1.getData());
		CRC32 crc2 = new CRC32();
		crc2.update(file2.getData());
		
		assertEquals(crc1.getValue(), crc2.getValue());
		
	}
	

}
