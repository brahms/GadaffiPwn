package hack.pwn.gadaffi.steganography;

import hack.pwn.gadaffi.Constants;
import hack.pwn.gadaffi.exceptions.DecodingException;
import hack.pwn.gadaffi.exceptions.EncodingException;
import hack.pwn.gadaffi.images.BitmapScaler;
import hack.pwn.gadaffi.steganography.PngStegoImage;
import hack.pwn.gadaffi.test.R;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
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
		.putInt(-12)
		.putInt(123)
		.putInt(-1234)
		.putInt(12345)
		.putInt(-123456);
		
		//
		// Create our byte array
		//
		mData = new byte[byteBuffer.position()];
		byteBuffer.clear();
		byteBuffer.get(mData);
		
		int target = 400;
		BitmapScaler scaler = new BitmapScaler(getContext().getResources(), R.drawable.flower, target);
		mCoverImage = scaler.getScaled();
		assertEquals(target, mCoverImage.getHeight());
		assertEquals(target, mCoverImage.getWidth());
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
		
		assertEquals(mData.length, decodedBytes.length);
		
		ByteBuffer b = ByteBuffer
				.wrap(decodedBytes)
				.order(Constants.BYTE_ORDER);

		//
		// We should get the same values back as we embedded
		//
		assertEquals(1, b.getInt());
		assertEquals(-12, b.getInt());
		assertEquals(123, b.getInt());
		assertEquals(-1234, b.getInt());
		assertEquals(12345, b.getInt());
		assertEquals(-123456, b.getInt());
	}

	public void testEncode() throws EncodingException {
		PngStegoImage encodedImage = new PngStegoImage();
		
		encodedImage.setImageBitmap(mCoverImage);
		encodedImage.setEmbeddedData(mData);
		encodedImage.encode();
		
		assertNotNull("Image Bytes should not be null.", encodedImage.getImageBytes());
		
	}
	
	public void testPngStaysSame() {
		Bitmap bitmap1 = mCoverImage.copy(Config.ARGB_8888, true);
		
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		
		bitmap1.setPixel(1, 1, 0xFFFF0F0F);
		assertEquals(0xFFFF0F0F, bitmap1.getPixel(1, 1));
		
		bitmap1.compress(CompressFormat.PNG, 100, bos);
		
		byte[] ba = bos.toByteArray();
		Bitmap bitmap2 = BitmapFactory.decodeByteArray(ba, 0, ba.length);
		assertEquals(bitmap1.getPixel(1, 1), bitmap2.getPixel(1, 1));
		
	}
	
	public void testPngCopy() {
		Bitmap bitmap1 = mCoverImage.copy(Config.ARGB_8888, true);
		
		
		assertEquals(bitmap1.getWidth(), mCoverImage.getWidth());
		assertEquals(bitmap1.getHeight(), mCoverImage.getHeight());
		boolean nonZero = false;
		for(int x = 0; x < bitmap1.getHeight(); x++) {
			for(int y = 0; y < bitmap1.getWidth(); y++) {
				if(bitmap1.getPixel(x, y) != 0) {
					nonZero = true;
				}
				
			}
		}
		
		assertTrue(nonZero);
	}
	
	public void testPngMaxData() throws EncodingException, DecodingException {
		int maxLength = PngStegoImage.getMaxBytesEncodable(mCoverImage);
		byte[] bytes = new byte[maxLength];
		for(int i = 0; i < bytes.length; i++) {
			bytes[i] = (byte) (i % 255);
		}
		
		PngStegoImage image = new PngStegoImage();
		image.setImageBitmap(mCoverImage);
		image.setEmbeddedData(bytes);
		image.encode();
		
		PngStegoImage decoder = new PngStegoImage();
		decoder.setImageBytes(image.getImageBytes());
		decoder.decode();

		int bitnum = 8 * Constants.STEGO_HEADER_LENGTH;
		for(int i = 0; i < bytes.length; i++) {
			assertEquals(String.format("Bytes length %d, Byte %d, Expected '%x' Actual '%x', bitnum is %d", bytes.length, i, bytes[i] & 0xFF, decoder.getEmbeddedData()[i] & 0xFF, bitnum), bytes[i], decoder.getEmbeddedData()[i]);
			bitnum +=8;
		}
		
	}
	
	public void testActualStegImage() throws DecodingException {
	    Bitmap bitmap = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.steg);
	    
	    PngStegoImage image = new PngStegoImage();
	    
	    image.setImageBitmap(bitmap);
	    
	    image.decode();
	    
	    assertTrue(image.hasEmbeddedData());
	}
}
