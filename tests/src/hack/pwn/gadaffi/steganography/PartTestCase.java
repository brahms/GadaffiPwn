package hack.pwn.gadaffi.steganography;

import hack.pwn.gadaffi.Constants;
import hack.pwn.gadaffi.exceptions.DecodingException;
import hack.pwn.gadaffi.exceptions.EncodingException;
import hack.pwn.gadaffi.steganography.Packet;
import hack.pwn.gadaffi.steganography.Part;

import java.nio.ByteBuffer;

import android.test.AndroidTestCase;

public class PartTestCase extends AndroidTestCase {

	protected void setUp() throws Exception {
		super.setUp();
	}

	public void testDecode() throws EncodingException, DecodingException {
		byte[] data = new byte[]{1,2,3,4,5,6};
		ByteBuffer byteBuffer = ByteBuffer.wrap(data);
		Packet packet = new Packet();
		packet.setSequenceNumber((byte) 23);
		
		Part part = Part.fromByteBuffer(packet, 0, byteBuffer, data.length + Constants.PART_HEADER_PLUS_CHECKSUM_LENGTH);
		
		byte[] toBytes = part.encode();
		
		Part part2 = Part.decode(toBytes);
		assertEquals(packet.getSequenceNumber(), part2.getSequenceNumber());
		assertEquals(part.getPartNumber(), part2.getPartNumber());
		
		assertEquals(data.length, part2.getPart().length);
		for(int i = 0; i< data.length; i++) {
			assertEquals("Checking index " + i, data[i], part2.getPart()[i]);
		}
		
		
	}
	public void testFromByteBufferWhenLast() {
		byte[] data = new byte[]{1,2,3,4,5,6,7,8,9,10,11,12};
		
		assertTrue(data.length % 2 == 0);
		
		ByteBuffer byteBuffer = ByteBuffer.wrap(data);
		Packet packet = new Packet();
		packet.setSequenceNumber((byte) 23);
		
		Part part = Part.fromByteBuffer(packet, 0, byteBuffer, (data.length/2) + Constants.PART_HEADER_PLUS_CHECKSUM_LENGTH);
		
		assertEquals(new Integer(0), part.getPartNumber());
		assertFalse(part.isLast());
		assertEquals(data.length/2, part.getPart().length);
		
		for(int i =  0; i < (data.length/2); i++) {
			assertEquals("Checking part1 index" + i, data[i], part.getPart()[i]);
		}
		
		Part part2 = Part.fromByteBuffer(packet, 1, byteBuffer, (data.length/2) + Constants.PART_HEADER_PLUS_CHECKSUM_LENGTH);
		
		assertEquals(new Integer(1), part2.getPartNumber());
		assertTrue(part2.isLast());
		assertEquals(data.length/2, part2.getPart().length);
		
		for(int i =  0; i < (data.length/2); i++) {
			assertEquals("Checking part2 index" + i, data[i+(data.length/2)], part2.getPart()[i]);
		}
	
	
	}
	
	public void testEncode() throws EncodingException {
		
		byte[] data = (new byte[]{1,2,3,4,5});
		Part part = new Part();
		part.setFlags((byte) 3);
		part.setPartNumber(1);
		part.setPart(data);
		Packet packet = new Packet();
		packet.setSequenceNumber((byte) 23);
		part.setPacket(packet);
		
		byte[] partencoded = part.encode();
		
		assertNotNull(partencoded);
		assertEquals(data.length + Constants.PART_HEADER_PLUS_CHECKSUM_LENGTH, partencoded.length);
		
	}
}
