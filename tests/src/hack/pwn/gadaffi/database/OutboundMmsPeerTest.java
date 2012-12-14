package hack.pwn.gadaffi.database;

import hack.pwn.gadaffi.Constants;
import hack.pwn.gadaffi.images.BitmapScaler;
import hack.pwn.gadaffi.steganography.Attachment;
import hack.pwn.gadaffi.steganography.Email;
import hack.pwn.gadaffi.steganography.OutboundMms;
import hack.pwn.gadaffi.steganography.Packet;
import hack.pwn.gadaffi.steganography.PacketType;
import hack.pwn.gadaffi.steganography.PngStegoImage;
import hack.pwn.gadaffi.test.R;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Bitmap;

public class OutboundMmsPeerTest extends DatabaseTestCase {

	private Bitmap mCoverImage = null;
	private byte[] bytes = null;
	protected void setUp() throws Exception {
		super.setUp();
		
		if(bytes == null) {
			bytes = new byte[20000];
			for(int i = 0; i < bytes.length; i++) {
				bytes[i] = (byte) (i % 255);
			}
		}
		BitmapScaler scaler = new BitmapScaler(getContext().getResources(), R.drawable.flower, 400);
		mCoverImage = scaler.getScaled();
		assertEquals(400, mCoverImage.getHeight());
		assertEquals(400, mCoverImage.getWidth());
	}
	
	public List<OutboundMms> testFileToOutboundMms() throws Exception {
		Email email = new Email();
		Attachment file = new Attachment();
		file.setData(bytes);
		file.setFilename("data");
		file.setMimeType(Constants.MIME_TYPE_OCTET_STREAM);
		email.addAttachment(file);
		
		String to = "12345";
		
		int totalBytes = file.toBytesLength();
		List<Bitmap> images = new ArrayList<Bitmap>();
		List<Integer> maxLengths = new ArrayList<Integer>();
		while(totalBytes > 0) {
			int imageBytes = PngStegoImage.getMaxBytesEncodable(mCoverImage);
			int maxBytes = Packet.getMaxEncodableBytes(imageBytes);
			totalBytes -= maxBytes;
			maxLengths.add(imageBytes);
			images.add(mCoverImage);
		}
		
		Packet p = Packet.encode(email, maxLengths);
		
		return OutboundMmsPeer.insertPngStegoImage(p, to, images);
		
	}
	
	public void testMmsToFile() throws Exception {
		List<OutboundMms> mmsList = testFileToOutboundMms();
		Packet p = null;
		for (OutboundMms outboundMms : mmsList) {
			PngStegoImage decoder = new PngStegoImage();
			decoder.setImageBytes(outboundMms.getImageBytes(context));
			decoder.decode();
			p = Packet.processIncomingData("12345", decoder.getEmbeddedData());
			if(p.getId() == null) InboundPacketPeer.insertPacket(p);
			else if(!p.getIsCompleted()) InboundPacketPeer.updatePacket(p);
		}
		
		assertNotNull(p);
		assertTrue(p.getIsCompleted());
		assertTrue(p.isValid());
		assertEquals(PacketType.EMAIL, p.getPacketType());
		Attachment file = (Attachment) ((Email) p.getPayload()).getAttachments().get(0);
		
		assertNotNull(file);
		assertEquals("data", file.getFilename());
		assertEquals(Constants.MIME_TYPE_OCTET_STREAM, file.getMimeType());
		assertEquals(20000, file.getData().length);
		
	}

}
