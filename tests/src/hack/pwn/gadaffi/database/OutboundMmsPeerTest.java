package hack.pwn.gadaffi.database;

import hack.pwn.gadaffi.Constants;
import hack.pwn.gadaffi.images.BitmapScaler;
import hack.pwn.gadaffi.steganography.FilePayload;
import hack.pwn.gadaffi.steganography.OutboundMms;
import hack.pwn.gadaffi.steganography.Packet;
import hack.pwn.gadaffi.steganography.PacketType;
import hack.pwn.gadaffi.steganography.PngStegoImage;
import hack.pwn.gadaffi.steganography.Text;
import hack.pwn.gadaffi.test.R;

import java.util.ArrayList;
import java.util.Arrays;
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
	
	public void testTextToOutboundMms() throws Exception {
		Text text = new Text();
		
		String to = "12345";
		
		text.setText("Hello there!");
		Packet p = Packet.encode(text, Arrays.asList(
				new Integer[]{PngStegoImage.getMaxBytesEncodable(mCoverImage)}));
		
		assertTrue(p.getParts().size() == 1);
		List<Bitmap> images = new ArrayList<Bitmap>();
		images.add(mCoverImage);
		OutboundMmsPeer.insertPngStegoImage(p, to, images);
		
		
	}
	
	public List<OutboundMms> testFileToOutboundMms() throws Exception {
		FilePayload file = new FilePayload();
		file.setBinaryData(bytes);
		file.setName("data");
		file.setMimeType(Constants.MIME_TYPE_OCTET_STREAM);
		
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
		
		Packet p = Packet.encode(file, maxLengths);
		
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
		assertEquals(PacketType.FILE, p.getPacketType());
		FilePayload file = (FilePayload) p.getPayload();
		
		assertNotNull(file);
		assertEquals("data", file.getName());
		assertEquals(Constants.MIME_TYPE_OCTET_STREAM, file.getMimeType());
		assertEquals(20000, file.getBinaryData().length);
		
	}

}
