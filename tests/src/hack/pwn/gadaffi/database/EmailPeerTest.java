package hack.pwn.gadaffi.database;

import hack.pwn.gadaffi.Constants;
import hack.pwn.gadaffi.R;
import hack.pwn.gadaffi.steganography.Attachment;
import hack.pwn.gadaffi.steganography.Email;
import hack.pwn.gadaffi.steganography.Packet;
import hack.pwn.gadaffi.steganography.PngStegoImage;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

public class EmailPeerTest extends DatabaseTestCase {

	
	public Email testInsertEmail() throws Exception {
		Email email = new Email();
		email.setSubject("Subject");
		email.setMessage("Message");
		email.setFrom("12345");
		Attachment attachment = new Attachment();
		attachment.setData(new byte[]{1,2,3,4,5,6,7,8,9,10});
		attachment.setMimeType(Constants.MIME_TYPE_OCTET_STREAM);
		attachment.setFilename("data.bin");
		
		email.addAttachment(attachment);
		
		assertEquals("12345", email.getFrom());
		assertEquals("Subject", email.getSubject());
		assertEquals("Message", email.getMessage());
		assertEquals(1, email.getAttachments().size());
		
		EmailPeer.insertEmail(email);
		
		assertNotNull(email.getEmailId());
		
		return email;
	}
	
	public void testGetEmailById() throws Exception {
		Email original = testInsertEmail();
		Email saved    = EmailPeer.getEmailById(original.getEmailId());
		
		assertEquals(original.getEmailId(), saved.getEmailId());
		assertEquals(original.getFrom(), saved.getFrom());
		assertEquals(original.getAttachments().size(), saved.getAttachments().size());
		assertEquals(Constants.MIME_TYPE_OCTET_STREAM, saved.getAttachments().get(0).getMimeType());
		assertEquals("data.bin", saved.getAttachments().get(0).getFilename());
		assertEquals(original.getAttachments().get(0).getDataLength(), saved.getAttachments().get(0).getDataLength());
		assertEquals(original.getAttachments().get(0).getData().length, saved.getAttachments().get(0).getData().length);
	
	}
	

    public void testActualStegOf2Images() throws Exception {
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inScaled =false;
        
        Bitmap steg1 = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.steg_with_parts_1, opts);
        Bitmap steg2 = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.steg_with_parts_2, opts);
        
        assertNotNull(steg1);
        assertEquals(640, steg1.getWidth());
        assertEquals(480, steg1.getHeight());

        assertNotNull(steg2);
        assertEquals(540, steg2.getWidth());
        assertEquals(404, steg2.getHeight());
        
        PngStegoImage image1 = new PngStegoImage();
        PngStegoImage image2 = new PngStegoImage();
        
        image1.setImageBitmap(steg1);
        image2.setImageBitmap(steg2);
        
        image1.decode();
        assertTrue(image1.hasEmbeddedData());
        image2.decode();
        assertTrue(image2.hasEmbeddedData());
        
        Packet packet = Packet.processIncomingData("12345", image1.getEmbeddedData());
        
        assertFalse(packet.getIsCompleted());
        
        packet = Packet.processIncomingData("12345", image2.getEmbeddedData());
        
        assertTrue(packet.getIsCompleted());
        
        Email email = (Email) packet.getPayload();
        Log.v(TAG, "The email is: " + email.toString());
        
        
    }
}
