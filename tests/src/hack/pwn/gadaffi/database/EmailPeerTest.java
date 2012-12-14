package hack.pwn.gadaffi.database;

import hack.pwn.gadaffi.Constants;
import hack.pwn.gadaffi.steganography.Attachment;
import hack.pwn.gadaffi.steganography.Email;

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
}
