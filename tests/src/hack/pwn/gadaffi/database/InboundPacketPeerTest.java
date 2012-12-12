package hack.pwn.gadaffi.database;

import hack.pwn.gadaffi.Constants;
import hack.pwn.gadaffi.Utils;
import hack.pwn.gadaffi.exceptions.DecodingException;
import hack.pwn.gadaffi.exceptions.EncodingException;
import hack.pwn.gadaffi.steganography.FilePayload;
import hack.pwn.gadaffi.steganography.Packet;
import hack.pwn.gadaffi.steganography.Part;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.zip.CRC32;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class InboundPacketPeerTest extends DatabaseTestCase {
	
	
	public void testInsertInboundPacket() throws Exception {
		Part part = new Part();
		part.setPartNumber(2);
		part.setTimeReceived(Utils.getNow());
		part.setPart(new byte[]{1,2,3,4});
		part.setFlags((byte) 0);
		part.setSequenceNumber((byte) 54);
		
		
		Packet p = new Packet("703-888-5555", part);
		p.setIsCompleted(false);
		p.setFlags((byte) 0x01);
		p.setSequenceNumber((byte) 54);
		
		assertNotNull(InboundPacketPeer.insertPacket(p));
		assertNotNull(p.getId());
		
		SQLiteDatabase db = BasePeer.getReadableDatabase();
		
		Cursor c = db.rawQuery(String.format(
				"SELECT count(*) FROM %s",
				InboundPacketEntry.TABLE_NAME), null);
		
		assertTrue(c.moveToFirst());
		assertEquals(1, c.getInt(0));
		c.close();
		c = db.rawQuery(String.format(
				"SELECT count(*) FROM %s",
					InboundPartEntry.TABLE_NAME), null);
		assertTrue(c.moveToFirst());
		assertEquals(1, c.getInt(0));
		c.close();
		db.close();
		
	}
	
	
	public Packet testRetrievePacketById() throws Exception {
		testInsertInboundPacket();
		
		Packet p = InboundPacketPeer.getInboundPacket("703-888-5555", (byte) 54);
		
		assertNotNull(p);
		assertEquals("703-888-5555", p.getFrom());
		assertEquals(0x01, p.getFlags());
		assertEquals(new Byte((byte) 54), p.getSequenceNumber());
		assertEquals(1, p.getParts().size());
		assertEquals(new Integer(2), p.getParts().values().iterator().next().getPartNumber());
		assertEquals(new Integer(2), new Integer(p.getParts().values().iterator().next().getPart()[1]));
		
		return p;
	}
	
	public Packet testUpdatePacket() throws Exception {

		Packet p = testRetrievePacketById();
		
		Part newPart = new Part();
		
		newPart.setSequenceNumber(p.getSequenceNumber());
		newPart.setPart(new byte[]{1,2,3,4});
		newPart.setFlags((byte) 0x01);
		newPart.setPartNumber(3);
		newPart.setTimeReceived(Utils.getNow());
		assertTrue(newPart.isLast());
		
		p.addPart(newPart);
		
		assertEquals(2, p.getParts().size());
		
		InboundPacketPeer.updatePacket(p);
		
		SQLiteDatabase db = BasePeer.getReadableDatabase();
		Cursor c = db.rawQuery(String.format(
				"SELECT count(*) FROM %s",
					InboundPartEntry.TABLE_NAME), null);
		assertTrue(c.moveToFirst());
		assertEquals(2, c.getInt(0));
		c.close();
		db.close();
		
		return p;
	}
	
	public void testRetrievePacketWith2Parts() throws Exception {
		Packet b = testUpdatePacket();
		
		Packet p = InboundPacketPeer.getInboundPacket(b.getFrom(), b.getSequenceNumber());
		
		assertNotNull(p);
		assertEquals("703-888-5555", p.getFrom());
		assertEquals(0x01, p.getFlags());
		assertEquals(new Byte((byte) 54), p.getSequenceNumber());
		assertEquals(2, p.getParts().size());
		
		Iterator<Part> it = p.getParts().values().iterator();
		
		Part part2 = it.next();
		Part part3 = it.next();
		
		assertFalse(it.hasNext());
		
		assertEquals(new Integer(2), part2.getPartNumber());
		assertEquals(new Integer(3), part3.getPartNumber());
		assertTrue(part3.isLast());
		assertNotNull(part3.getTimeReceived());
		assertNotNull(part3.getPart());
		assertNotNull(part3.getId());
	}
	
	public void testInsertInboundPacketShouldThrowWhen() throws Exception {
		Packet p = new Packet();
		
		p.setId(1);
		
		try{
			InboundPacketPeer.insertPacket(p);
			fail("Should throw if id is set on insert");
		}
		catch(AssertionError ex) {}

		p = new Packet();
		
		p.setIsCompleted(true);
		
		try{
			InboundPacketPeer.insertPacket(p);
			fail("Should throw if setCompleted to true");
		}
		catch(AssertionError ex) {}
		
	}
	
	public void testProcessingIncomingData() throws EncodingException, DecodingException {
		byte[] fileData = new byte[]{1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30,31,32,33,34,35,36,37,38,39,40};
		FilePayload file1 = new FilePayload();
		file1.setBinaryData(fileData);
		file1.setName("data.stuff");
		file1.setMimeType(Constants.MIME_TYPE_OCTET_STREAM);
		
		List<Integer> maxLengths = Arrays.asList(new Integer[]{30, 30, 60});
		
		Packet packet1 = Packet.encode(file1, maxLengths);
		packet1.setSequenceNumber((byte) 67);
		
		assertEquals(3, packet1.getParts().size());
		
		byte[] part1bytes = packet1.getParts().get(0).encode();
		
		
		Packet packet2 = Packet.processIncomingData("12345", part1bytes);
		
		assertFalse(packet2.isValid());
		assertFalse(packet2.getIsCompleted());
		
		packet2.addPart(packet1.getParts().get(1));

		assertFalse(packet2.isValid());
		assertFalse(packet2.getIsCompleted());
		packet2.addPart(packet1.getParts().get(2));
		

		assertTrue(packet2.isValid());
		assertTrue(packet2.getIsCompleted());
		
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
	

}
