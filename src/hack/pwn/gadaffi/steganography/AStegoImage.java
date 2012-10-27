package hack.pwn.gadaffi.steganography;

import java.nio.ByteBuffer;

import hack.pwn.gadaffi.Constants;
import hack.pwn.gadaffi.MimeType;
import hack.pwn.gadaffi.exceptions.DecodingException;
import hack.pwn.gadaffi.exceptions.EncodingException;
import android.graphics.Bitmap;

/**
 * Abstract Class, all StegoImage which implement
 * algorithms to decode/encode an image are extended
 * from here.
 * 
 */
public abstract class AStegoImage {
	protected static String TAG = "StegoImage";
	
	protected byte[] mImageBytes;
	protected MimeType mMimeType;
	protected byte[]  mEmbeddedData;
	protected Bitmap mImageBitmap;
	
	
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
	
	public byte[] getMagicPlusLengthByteArray(short length) {
		ByteBuffer b = ByteBuffer
				.allocate(6)
				.order(Constants.BYTE_ORDER)
				.putInt(Constants.MAGIC_VALUE)
				.putShort(length);
		
		byte[] ret = new byte[6];
		b.get(ret);
		
		return ret;
	}
	
	
	

}
