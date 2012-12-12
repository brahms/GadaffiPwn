package hack.pwn.gadaffi.steganography;

import hack.pwn.gadaffi.Constants;
import hack.pwn.gadaffi.exceptions.DecodingException;
import hack.pwn.gadaffi.exceptions.EncodingException;
import hack.pwn.gadaffi.steganography.FilePayload;
import hack.pwn.gadaffi.steganography.Packet;

import java.util.ArrayList;
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
		FilePayload file = new FilePayload();
		file.setBinaryData(fileData);
		file.setName("data.stuff");
		file.setMimeType(Constants.MIME_TYPE_OCTET_STREAM);
		
		List<Integer> maxLengths = Arrays.asList(new Integer[]{10, 10});
		
		try{
			Packet.encode(file, maxLengths);
			fail("Packet didn't throw an encoding exception.");
		}
		catch(EncodingException ex) {
			
		}
	}
	public void testEncodeFile() throws EncodingException {
		byte[] fileData = new byte[]{1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30,31,32,33,34,35,36,37,38,39,40};
		FilePayload file = new FilePayload();
		file.setBinaryData(fileData);
		file.setName("data.stuff");
		file.setMimeType(Constants.MIME_TYPE_OCTET_STREAM);
		
		List<Integer> maxLengths = Arrays.asList(new Integer[]{10, 10, 10, 10, 10, 10, 10, 100});
		
		Packet packet = Packet.encode(file, maxLengths);
		
		assertTrue(packet.isValid());
		assertNotNull(packet.getParts());
		assertFalse(packet.getParts().size() == 0);
	}
	
	public void testProcessIncomingDataOfOnePart() throws DecodingException, EncodingException {
		byte[] fileData = new byte[]{1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30,31,32,33,34,35,36,37,38,39,40};
		FilePayload file1 = new FilePayload();
		file1.setBinaryData(fileData);
		file1.setName("data.stuff");
		file1.setMimeType(Constants.MIME_TYPE_OCTET_STREAM);
		
		List<Integer> maxLengths = Arrays.asList(new Integer[]{300});
		
		Packet packet1 = Packet.encode(file1, maxLengths);
		packet1.setSequenceNumber((byte) 12345);
		
		byte[] part1bytes = packet1.getParts().get(0).encode();
		
		Packet packet2 = Packet.processIncomingData("12345", part1bytes);
		
		assertTrue(packet2.isValid());
		assertTrue(packet2.getIsCompleted());
		assertEquals(1, packet2.getParts().size());
		assertEquals("12345", packet2.getFrom());
		assertTrue(packet2.getPayload() instanceof FilePayload);
		FilePayload file2 = (FilePayload) packet2.getPayload();
		
		assertEquals(file1.getMimeType(), file2.getMimeType());
		assertEquals(file1.getName(), file2.getName());
		assertEquals(file1.getBinaryData().length, file2.getBinaryData().length);
		
		CRC32 crc1 = new CRC32();
		crc1.update(file1.getBinaryData());
		CRC32 crc2 = new CRC32();
		crc2.update(file2.getBinaryData());
		
		assertEquals(crc1.getValue(), crc2.getValue());
		
	}
	
	public void testEncodeAndDecodeText() throws EncodingException, DecodingException {
		Text text = new Text();
		String string = "Hello, how are you.";
		text.setText(string);
		
		Packet encoder = Packet.encode(text, Arrays.asList(new Integer[]{100}));
		encoder.setSequenceNumber((byte) 54);
		
		byte[] partBytes = encoder.getParts().get(0).encode();
		
		Packet decoder = Packet.processIncomingData("12345", partBytes);
		
		assertTrue(decoder.getIsCompleted());
		assertTrue(decoder.getSequenceNumber() == 54);
		assertTrue(decoder.isValid());
		assertTrue(decoder.getParts().size() == 1);
		assertEquals("12345", decoder.getFrom());
		assertTrue(decoder.getPayload() instanceof Text);
		assertTrue(decoder.getPacketType() == PacketType.TEXT);
		text = (Text) decoder.getPayload();
		
		assertEquals(string, text.getText());
	}
	

	

}
