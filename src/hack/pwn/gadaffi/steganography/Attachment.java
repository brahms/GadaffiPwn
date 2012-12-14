package hack.pwn.gadaffi.steganography;

import hack.pwn.gadaffi.Constants;
import hack.pwn.gadaffi.Utils;
import hack.pwn.gadaffi.database.AttachmentPeer;
import hack.pwn.gadaffi.exceptions.DecodingException;

import java.nio.ByteBuffer;



/**
 * This class holds data that will be 
 * encoded or was decoded into/from a set of packets
 *
 */
public class Attachment {
	Integer  attachmentId = null;
	String   mimeType = null;
	byte[]   bytes = null;
	Integer     bytesLength = null;
	String   name = null;
	Integer  emailId = null;
	
	/**
	 * @return the id
	 */
	public Integer getAttachmentId() {
		return attachmentId;
	}

	/**
	 * @param id the id to set
	 */
	public void setAttachmentId(Integer id) {
		this.attachmentId = id;
	}

	/**
	 * @return the from
	 */

	/**
	 * @return the mimeType
	 */
	public String getMimeType() {
		return mimeType;
	}

	/**
	 * @param mimeType the mimeType to set
	 */
	public void setMimeType(String mimeType) {
		this.mimeType = mimeType;
	}

	/**
	 * @return the bytes
	 */
	public byte[] getData() {
		if(bytes == null && getAttachmentId() != null) {
			bytes = AttachmentPeer.getData(this);
		}
		
		return bytes;
	}

	/**
	 * @param bytes the bytes to set
	 */
	public void setData(byte[] bytes) {
		this.bytes = bytes;
		setBytesLength((long) this.bytes.length);
	}

	/**
	 * @return the name
	 */
	public String getFilename() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setFilename(String name) {
		this.name = name;
	}


	public static Attachment fromByteBuffer(ByteBuffer byteBuffer) throws DecodingException {
		if(byteBuffer.remaining() == 0) {
			return null;
		}
		
		int mimeTypeLength = byteBuffer.get();
		
		if(mimeTypeLength < 0 || mimeTypeLength > byteBuffer.remaining()) {
			throw new DecodingException("Mime Type Length is not valid " + mimeTypeLength);
		}
		
		Attachment attachment = new Attachment();
		
		byte[] mimeType = new byte[mimeTypeLength];
		
		byteBuffer.get(mimeType);
		
		attachment.setMimeType(new String(mimeType, Constants.CHARSET));
		
		int filenameLength = byteBuffer.get();
		
		byte[] fileName = new byte[filenameLength];
		byteBuffer.get(fileName);
		
		attachment.setFilename(new String(fileName, Constants.CHARSET));
		
		int fileLength = byteBuffer.getInt() & 0xFFFFFFFF;
		byte[] data = new byte[fileLength];
		
		byteBuffer.get(data);
		
		attachment.setData(data);
		
		return attachment;
	}

	public byte[] toBytes() {
		
		ByteBuffer byteBuffer = 
				ByteBuffer
				.allocate(toBytesLength())
				.order(Constants.BYTE_ORDER);
		toByteBuffer(byteBuffer);
		
		return byteBuffer.array();
		
	}

	public void toByteBuffer(ByteBuffer byteBuffer) {

		Utils._assert(getMimeType() != null);
		Utils._assert(getFilename() != null);
		Utils._assert(getData() != null);
		
		byte[] mimeType = getMimeType().getBytes(Constants.CHARSET);
		byte[] filename = getFilename().getBytes(Constants.CHARSET);
	
		
		byteBuffer.put((byte)mimeType.length).put(mimeType);
		byteBuffer.put((byte)filename.length).put(filename);
		byteBuffer.putInt(getDataLength()).put(getData());
		
	}

	public int toBytesLength() {
		Utils._assert(getMimeType() != null);
		Utils._assert(getFilename() != null);
		Utils._assert(getDataLength() != null);
		byte[] mimeType = getMimeType().getBytes(Constants.CHARSET);
		byte[] filename = getFilename().getBytes(Constants.CHARSET);
		return mimeType.length + 1 +
				  filename.length + 1 +
				  getDataLength() + 4;
	}
	
	/**
	 * @return the bytesLength
	 */
	public Integer getDataLength() {
		return bytesLength;
	}

	/**
	 * @param bytesLength the bytesLength to set
	 */
	public void setBytesLength(long bytesLength) {
	    Utils._assert(bytesLength <= Integer.MAX_VALUE);
		this.bytesLength = (int) bytesLength;
	}

	@Override
	public String toString() {
		return String.format("Attachment[Id %d, MimeTime %s, Filename %s, Bytes %d]", getAttachmentId(), getMimeType(), getFilename(), getDataLength());
	}

	public void setEmailId(Integer id) {
		emailId = id;
		
	}
	
	public Integer getEmailId() {
		return emailId;
	}

	public void setDataLength(long size) {
	    Utils._assert(size <= Integer.MAX_VALUE);
		this.bytesLength = (int) size;
		
	}
	
	
	

}
