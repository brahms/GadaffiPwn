package hack.pwn.gadaffi.steganography;

import hack.pwn.gadaffi.Constants;
import hack.pwn.gadaffi.exceptions.DecodingException;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import android.text.format.Time;

public class Email extends Payload {


	Time timeReceived;
	String from;
	String message;
	String subject;
	Integer emailId;
	List<Attachment> attachments = new ArrayList<Attachment>();
    String to;
	
	/**
	 * @param attachments the attachments to set
	 */
	public void setAttachments(List<Attachment> attachments) {
		this.attachments = attachments;
	}

	@Override
	public byte[] toBytes() {
		ByteBuffer buffer = ByteBuffer.allocate(toBytesLength()).order(Constants.BYTE_ORDER);
		
		toByteBuffer(buffer);
		
		return buffer.array();
	}

	@Override
	public void toByteBuffer(ByteBuffer buffer) {
		
		if(getSubject() == null || getSubject().isEmpty()) {
			buffer.put((byte)0);
		}
		else {
			byte[] subject = getSubject().getBytes(Constants.CHARSET);
			buffer.put((byte)subject.length);
			buffer.put(subject);
			
		}
		
		if (getMessage() == null || getMessage().isEmpty()) {
			buffer.putShort((short)0);
		}
		else {
			byte[] message = getMessage().getBytes(Constants.CHARSET);
			buffer.putShort((short) message.length);
			buffer.put(message);
			
		}
		
		for(Attachment attachment : getAttachments()) {
			attachment.toByteBuffer(buffer);
		}
		

	}

	@Override
	public int toBytesLength() {
		int subjectlength = 1;
		byte subjectbytes = getSubject() == null || getSubject().isEmpty() ? 0 : (byte) getSubject().getBytes(Constants.CHARSET).length;
		int messagelength = 2;
		int messagebytes =  getMessage() == null || getMessage().isEmpty() ? 0 : getMessage().getBytes(Constants.CHARSET).length;
		
		int attachmentLength = 0;
		for(Attachment attachment : getAttachments()) {
			attachmentLength += attachment.toBytesLength();
		}
		
		return subjectlength + subjectbytes + messagelength + messagebytes + attachmentLength;
	}

	/**
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * @param message the message to set
	 */
	public void setMessage(String message) {
		this.message = message;
	}

	/**
	 * @return the subject
	 */
	public String getSubject() {
		return subject;
	}

	/**
	 * @param subject the subject to set
	 */
	public void setSubject(String subject) {
		this.subject = subject;
	}

	/**
	 * @return the emailId
	 */
	public Integer getEmailId() {
		return emailId;
	}

	/**
	 * @param emailId the emailId to set
	 */
	public void setEmailId(Integer emailId) {
		this.emailId = emailId;
	}

	/**
	 * @return the attachments
	 */
	public List<Attachment> getAttachments() {
		return attachments;
	}
	
	public void addAttachment(Attachment attachment) {
		getAttachments().add(attachment);
	}

	public static Email fromByteBuffer(ByteBuffer byteBuffer) throws DecodingException {
		Email email = new Email();
		
		int subjectLength = byteBuffer.get() & 0xFF;
		
		
		if (subjectLength > 0) {
			byte[] subjectBytes = new byte[subjectLength];
			byteBuffer.get(subjectBytes);
			email.setSubject(new String(subjectBytes, Constants.CHARSET));
		}
		else {
			email.setSubject("");
		}
		
		int messageLength = byteBuffer.getShort() & 0xFFFF;
		
		if (messageLength > 0) { 
			byte[] messageBytes = new byte[messageLength];
			byteBuffer.get(messageBytes);
			email.setMessage(new String(messageBytes, Constants.CHARSET));
		}
		else {
			email.setMessage("");
		}
		
		Attachment attachment = Attachment.fromByteBuffer(byteBuffer);
		
		while(attachment != null) {
			email.addAttachment(attachment);
			attachment = Attachment.fromByteBuffer(byteBuffer);
		}
		
		return email;
		
	}
	
	@Override 
	public String toString() {
		StringBuilder attachmentString = new StringBuilder();
		attachmentString.append(String.format("Attachments(%d)[", getAttachments().size()));
		for (Attachment attachment : getAttachments()) {
			attachmentString.append(attachment.toString()).append(", ");
		}
		
		attachmentString.append("]");
		
		return String.format(
				"Email[" +
				"Id %d, " +
				"Subject %s, " + 
				"Message %s, " +
				"Attachments %s, " +
				"TimeReceived %s, " +
				"From %s" +
				"]", getEmailId(), getSubject(), getMessage(), attachmentString.toString(), getTimeReceived(), getFrom());
	}
	/**
	 * @return the timeReceived
	 */
	public Time getTimeReceived() {
		return timeReceived;
	}


	/**
	 * @param timeReceived the timeReceived to set
	 */
	public void setTimeReceived(Time timeReceived) {
		this.timeReceived = timeReceived;
	}


	/**
	 * @return the from
	 */
	public String getFrom() {
		return from;
	}


	/**
	 * @param from the from to set
	 */
	public void setFrom(String from) {
		this.from = from;
	}
	

	public void setTimeReceived(long ms) {
		Time time = new Time();
		time.set(ms);
		setTimeReceived(time);
	}

	public String getTo() {
		return to;
	}
	
	public void setTo(String number) {
		to = number;
	}

}
