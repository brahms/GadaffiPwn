package hack.pwn.gadaffi.steganography;

import hack.pwn.gadaffi.exceptions.DecodingException;
import hack.pwn.gadaffi.exceptions.EncodingException;

import java.io.ByteArrayOutputStream;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

/**
 * This class decodes and encodes PNG images.
 *
 */
public class PngStegoImage extends AStegoImage{
	protected static final String TAG = "PngStegoImage";
	/**
	 * This is the ratio of actual embedded bits per pixel
	 */
	private static final double RATIO = 3/32;

	/**
	 * Takes a image bytes (should be a png file) and gets
	 * the bytes out of the embedded data inside the png file,
	 * should check for the magic value, and cancel processing
	 */
	@Override
	public void decode() throws DecodingException{
		if(getImageBytes() == null) {
			Log.e(TAG, "Decode called without image bytes.");
			throw new DecodingException();
		}
		
		//
		// Given just bytes from an image, reconstuct it into a Bitmap
		//
		Bitmap b = BitmapFactory.decodeByteArray(getImageBytes(), 0, getImageBytes().length);
		setImageBitmap(b);
		
		//
		// TODO: Given a bitmap object b, use our PNG Algorithm to 
		// create a byte array output stream of data in the message
		// 
		byte currentByte;
		int currentByteCount = 0;
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		for(int x = 0; x < b.getHeight(); x++) {
			for(int y = 0; y < b.getWidth(); y++) {
				int pixel = b.getPixel(x, y);
				
				//
				//TODO: here we go do processing on each pixel
				// until we have an entire byte
				// then add the byte to the byte output stream
				//
				// (if we have a byte)
				// outputStream.write(currentByte);
				// currentByteCount++;
				//
				// Once we have 4 bytes, check for the magic value
				// Once we have 6 bytes, the length will be in the
				// the short contained in those two bytes
				// 
				// Bytes are stored big endian, ie a short for 16 would be stored as
				// 0x00 0x10, so you can concatenate easier
				//
				
			}
		}
	}

	@Override
	/**
	 * Takes a image bitmap (cover image), and embedded data
	 * and produced image bytes containing the embedded data inside
	 * the cover image, in png format.
	 */
	public void encode() throws EncodingException {
		
		if(getImageBitmap() == null) {
			Log.e(TAG, "Encode called without image bitmap.");
			throw new EncodingException();
		}
		if(getEmbeddedData() == null) {
			Log.e(TAG, "Encode called without embeded data.");
			throw new EncodingException();
		}
		
		
		//
		// TODO: use getImageBitMap() to get the cover image
		// use getEmbeddedData() to get the bytes
		// we need to put into the data
		//
		// Its the job of this class to insert
		// the magic value and length where it needs to be, not the caller
		//
		// 
		
		// insert the header
		byte[] header = getMagicPlusLengthByteArray((short) getEmbeddedData().length);
		
		// insert getEmbeddedData()
		
		
	}


}
