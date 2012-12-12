package hack.pwn.gadaffi.steganography;

import hack.pwn.gadaffi.Constants;
import hack.pwn.gadaffi.database.FilePeer;
import hack.pwn.gadaffi.exceptions.DecodingException;

import java.nio.ByteBuffer;



/**
 * This class holds data that will be 
 * encoded or was decoded into/from a set of packets
 *
 */
public class FilePayload extends Payload{
	Integer  fileId = null;
	String   mimeType = null;
	byte[]   bytes = null;
	String   name = null;
	
	/**
	 * @return the id
	 */
	public Integer getFileId() {
		return fileId;
	}

	/**
	 * @param id the id to set
	 */
	public void setFileId(Integer id) {
		this.fileId = id;
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
	public byte[] getBinaryData() {
		if(bytes == null && getFileId() != null) {
			bytes = FilePeer.getBinaryData(this);
		}
		
		return bytes;
	}

	/**
	 * @param bytes the bytes to set
	 */
	public void setBinaryData(byte[] bytes) {
		this.bytes = bytes;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}


	public static Payload fromByteBuffer(ByteBuffer byteBuffer) throws DecodingException {
		int mimeTypeLength = byteBuffer.get();
		
		if(mimeTypeLength < 0 || mimeTypeLength > byteBuffer.remaining()) {
			throw new DecodingException("Mime Type Length is not valid " + mimeTypeLength);
		}
		
		FilePayload file = new FilePayload();
		
		byte[] mimeType = new byte[mimeTypeLength];
		
		byteBuffer.get(mimeType);
		
		file.setMimeType(new String(mimeType, Constants.CHARSET));
		
		int filenameLength = byteBuffer.get();
		
		byte[] fileName = new byte[filenameLength];
		byteBuffer.get(fileName);
		
		file.setName(new String(fileName, Constants.CHARSET));
		
		byte[] data = new byte[byteBuffer.remaining()];
		
		byteBuffer.get(data);
		
		file.setBinaryData(data);
		
		return file;
	}

	@Override
	public byte[] toBytes() {
		
		ByteBuffer byteBuffer = 
				ByteBuffer
				.allocate(toBytesLength())
				.order(Constants.BYTE_ORDER);
		toByteBuffer(byteBuffer);
		
		return byteBuffer.array();
		
	}

	@Override
	public void toByteBuffer(ByteBuffer byteBuffer) {
		byte[] mimeType = getMimeType().getBytes(Constants.CHARSET);
		byte[] filename = getName().getBytes(Constants.CHARSET);
	
		
		byteBuffer.put((byte)mimeType.length).put(mimeType);
		byteBuffer.put((byte)filename.length).put(filename);
		byteBuffer.put(getBinaryData());
		
	}

	@Override
	public int toBytesLength() {
		byte[] mimeType = getMimeType().getBytes(Constants.CHARSET);
		byte[] filename = getName().getBytes(Constants.CHARSET);
		return mimeType.length + 1 +
				  filename.length + 1 +
				  getBinaryData().length;
	}
	
	@Override
	public String toString() {
		return String.format("FilePayload[FilePayload Id %d, MimeTime %s, Filename %s, Payload %s]", getFileId(), getMimeType(), getName(), super.toString());
	}
	
	
	

}
