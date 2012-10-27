package hack.pwn.gadaffi.test.steganography;

import hack.pwn.gadaffi.Constants;
import hack.pwn.gadaffi.exceptions.DecodingException;
import hack.pwn.gadaffi.exceptions.EncodingException;
import hack.pwn.gadaffi.steganography.PngStegoImage;

import java.nio.ByteBuffer;

import hack.pwn.gadaffi.test.R;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.test.AndroidTestCase;

public class PngStegoImageTestCase extends AndroidTestCase {

	private byte[] mData;
	
	private Bitmap mCoverImage;
	
	
	protected void setUp() throws Exception {
		ByteBuffer byteBuffer = ByteBuffer
				.allocate(1000)
				.order(Constants.BYTE_ORDER);
		
		//
		// We are going to insert 6 ints for a total of 192 bits
		// which should take a total of 2048 pixels 
		//
		byteBuffer
		.putInt(1)
		.putInt(12)
		.putInt(123)
		.putInt(1234)
		.putInt(12345)
		.putInt(123456);
		
		//
		// Create our byte array
		//
		mData = new byte[byteBuffer.position()];
		byteBuffer.clear();
		byteBuffer.get(mData);
		
		
		mCoverImage = BitmapFactory
				.decodeResource(getContext().getResources(), R.drawable.flower);
	
	}

	public void testDecode() throws EncodingException, DecodingException {
		PngStegoImage encodedImage = new PngStegoImage();
		
		encodedImage.setImageBitmap(mCoverImage);
		encodedImage.setEmbeddedData(mData);
		encodedImage.encode();
		
		byte[] encodedImageBytes = encodedImage.getImageBytes();
		
		PngStegoImage decodedImage = new PngStegoImage();
		decodedImage.setImageBytes(encodedImageBytes);
		decodedImage.decode();
		
		byte[] decodedBytes = decodedImage.getEmbeddedData();
		
		assertNotNull(decodedBytes);
		
		assertTrue("Decoded bytes greater than or equal than encoded bytes", 
				decodedBytes.length >= mData.length);
		
		ByteBuffer b = ByteBuffer
				.wrap(decodedBytes)
				.order(Constants.BYTE_ORDER);

		//
		// We should get the same values back as we embedded
		//
		assertEquals(1, b.getInt());
		assertEquals(12, b.getInt());
		assertEquals(123, b.getInt());
		assertEquals(1234, b.getInt());
		assertEquals(12345, b.getInt());
		assertEquals(123456, b.getInt());
	}

	public void testEncode() throws EncodingException {
		PngStegoImage encodedImage = new PngStegoImage();
		
		encodedImage.setImageBitmap(mCoverImage);
		encodedImage.setEmbeddedData(mData);
		encodedImage.encode();
		
		assertNotNull("Image Bytes should not be null.", encodedImage.getImageBytes());
		
	}

}
