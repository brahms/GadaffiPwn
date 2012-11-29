package hack.pwn.gadaffi.steganography;

import hack.pwn.gadaffi.Constants;
import hack.pwn.gadaffi.MimeType;
import hack.pwn.gadaffi.exceptions.DecodingException;
import hack.pwn.gadaffi.exceptions.EncodingException;

import java.nio.ByteBuffer;
import java.util.LinkedList;

import android.graphics.Bitmap;

/**
 * Abstract Class, all StegoImage which implement
 * algorithms to decode/encode an image are extended
 * from here.
 * 
 */
public abstract class AStegoImage {
	protected static String TAG = "AStegoImage";
	
	protected byte[] mImageBytes;
	protected MimeType mMimeType;
	protected byte[]  mEmbeddedData;
	protected Bitmap mImageBitmap;
	
	protected static final int STATE_START = 0x00;
	protected static final int STATE_GOT_MAGIC = 0x01;
	protected static final int STATE_GOT_LENGTH = 0x02;
	
	public boolean hasEmbeddedData() {
		return mEmbeddedData != null;
	}
	
	public byte[] getEmbeddedData() {
		return mEmbeddedData;
	}
	
	public void setEmbeddedData(byte[] data) {
		mEmbeddedData = data;
	}
	
	public void setImageBytes(byte[] imageBytes) {
		mImageBytes = imageBytes;
	}
	
	/**
	 * The actual bytes that are the image we are trying
	 * to decode/encode from/into
	 * @return
	 */
	public byte[] getImageBytes() {
		return mImageBytes;
	}
	
	
	public void setImageBitmap(Bitmap image) {
		mImageBitmap = image;
	}
	
	
	public Bitmap getImageBitmap() 
	{
		return mImageBitmap;
	}
	
	public abstract void decode() throws DecodingException;
	public abstract void encode() throws EncodingException;
	
	public static LinkedList<Boolean> getBitsForData(byte[] embeddedData) {
		assert(embeddedData.length <= 0x0000FFFF);
		ByteBuffer b = ByteBuffer
				.allocate(Constants.STEGO_HEADER_LENGTH)
				.order(Constants.BYTE_ORDER)
				.putInt(Constants.MAGIC_VALUE)
				.putShort((short) embeddedData.length);
		
		b.clear();
		
		byte[] header = new byte[Constants.STEGO_HEADER_LENGTH];
		b.get(header);
		
		LinkedList<Boolean> bits = getBitBuffer();
		
		pushBytes(header, bits);
		pushBytes(embeddedData, bits);
		
		return bits;
		
	}
	
	public static void pushBytes(byte[] bytes, LinkedList<Boolean> bits) {
		for(byte bite : bytes) {
			pushByte(bite, bits);
		}
	}
	
	public static void pushByte(byte bite, LinkedList<Boolean> bits) {
		for(int i = 7; i >= 0; i--) {
			byte flag = (byte) (0x01 << i);
			
			boolean bit = (bite & flag) != 0;
			bits.addLast(bit);
		}
	}

	public static LinkedList<Boolean> getBitBuffer()
	{
		return new LinkedList<Boolean>();
	}
	
	public static Byte pushBit(boolean nextBit, LinkedList<Boolean> bitBuffer) 
	{
		assert(bitBuffer != null);
		assert(bitBuffer.size() < 8);
		
		bitBuffer.addLast(nextBit);
		
		if(bitBuffer.size() == 8) 
		{
			return getByteFromBitBuffer(bitBuffer);
		}
		
		return null;
	}
	
	public static Byte getByteFromBitBuffer(LinkedList<Boolean> bits) {
		if(bits.size() < 8) {
			return null;
		}
		
		byte bite = (byte) 0xFF;
		
		if(!bits.pop()) {
			bite ^= Constants.EIGTH_BIT;
		}
		if(!bits.pop()) {
			bite ^= Constants.SEVENTH_BIT;
		}
		if(!bits.pop()) {
			bite ^= Constants.SIXTH_BIT;
		}
		if(!bits.pop()) {
			bite ^= Constants.FIFTH_BIT;
		}
		if(!bits.pop()) {
			bite ^= Constants.FOURTH_BIT;
		}
		if(!bits.pop()) {
			bite ^= Constants.THIRD_BIT;
		}
		if(!bits.pop()) {
			bite ^= Constants.SECOND_BIT;
		}
		if(!bits.pop()) {
			bite ^= Constants.FIRST_BIT;
		}
		
		return bite;
		
	}
	

}
