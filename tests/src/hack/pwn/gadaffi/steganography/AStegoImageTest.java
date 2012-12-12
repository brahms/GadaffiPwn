package hack.pwn.gadaffi.steganography;

import hack.pwn.gadaffi.Constants;
import hack.pwn.gadaffi.steganography.AStegoImage;

import java.util.LinkedList;

import android.test.AndroidTestCase;


public class AStegoImageTest extends AndroidTestCase {

	protected void setUp() throws Exception {
		super.setUp();
	}

	public void testGetBitsForData() {
		byte firstByte = (byte) 0xFA;
		byte secondByte = (byte) 0xBA;
		LinkedList<Boolean> bits = AStegoImage.getBitsForData(
				new byte[]{
						firstByte,
						secondByte
				});
		
		assertEquals(Constants.MAGIC_VALUE_LENGTH*8 + Constants.LENGTH_VALUE_LENGTH*8 + 2*8, bits.size());
		
		Boolean bit;
		int position;
		
		//
		// Check first 4 bytes for our magic value
		//
		position = 3 * 8;
		bit = (Constants.MAGIC_VALUE & (Constants.EIGTH_BIT << position)) != 0;
		assertEquals(bit, bits.pop());
		bit = (Constants.MAGIC_VALUE & (Constants.SEVENTH_BIT << position)) != 0;
		assertEquals(bit, bits.pop());
		bit = (Constants.MAGIC_VALUE & (Constants.SIXTH_BIT << position)) != 0;
		assertEquals(bit, bits.pop());
		bit = (Constants.MAGIC_VALUE & (Constants.FIFTH_BIT << position)) != 0;
		assertEquals(bit, bits.pop());
		bit = (Constants.MAGIC_VALUE & (Constants.FOURTH_BIT << position)) != 0;
		assertEquals(bit, bits.pop());
		bit = (Constants.MAGIC_VALUE & (Constants.THIRD_BIT << position)) != 0;
		assertEquals(bit, bits.pop());
		bit = (Constants.MAGIC_VALUE & (Constants.SECOND_BIT << position)) != 0;
		assertEquals(bit, bits.pop());
		bit = (Constants.MAGIC_VALUE & (Constants.FIRST_BIT << position)) != 0;
		assertEquals(bit, bits.pop());
		

		position = 2 * 8;
		bit = (Constants.MAGIC_VALUE & (Constants.EIGTH_BIT << position)) != 0;
		assertEquals(bit, bits.pop());
		bit = (Constants.MAGIC_VALUE & (Constants.SEVENTH_BIT << position)) != 0;
		assertEquals(bit, bits.pop());
		bit = (Constants.MAGIC_VALUE & (Constants.SIXTH_BIT << position)) != 0;
		assertEquals(bit, bits.pop());
		bit = (Constants.MAGIC_VALUE & (Constants.FIFTH_BIT << position)) != 0;
		assertEquals(bit, bits.pop());
		bit = (Constants.MAGIC_VALUE & (Constants.FOURTH_BIT << position)) != 0;
		assertEquals(bit, bits.pop());
		bit = (Constants.MAGIC_VALUE & (Constants.THIRD_BIT << position)) != 0;
		assertEquals(bit, bits.pop());
		bit = (Constants.MAGIC_VALUE & (Constants.SECOND_BIT << position)) != 0;
		assertEquals(bit, bits.pop());
		bit = (Constants.MAGIC_VALUE & (Constants.FIRST_BIT << position)) != 0;
		assertEquals(bit, bits.pop());
		
		

		position = 1 * 8;
		bit = (Constants.MAGIC_VALUE & (Constants.EIGTH_BIT << position)) != 0;
		assertEquals(bit, bits.pop());
		bit = (Constants.MAGIC_VALUE & (Constants.SEVENTH_BIT << position)) != 0;
		assertEquals(bit, bits.pop());
		bit = (Constants.MAGIC_VALUE & (Constants.SIXTH_BIT << position)) != 0;
		assertEquals(bit, bits.pop());
		bit = (Constants.MAGIC_VALUE & (Constants.FIFTH_BIT << position)) != 0;
		assertEquals(bit, bits.pop());
		bit = (Constants.MAGIC_VALUE & (Constants.FOURTH_BIT << position)) != 0;
		assertEquals(bit, bits.pop());
		bit = (Constants.MAGIC_VALUE & (Constants.THIRD_BIT << position)) != 0;
		assertEquals(bit, bits.pop());
		bit = (Constants.MAGIC_VALUE & (Constants.SECOND_BIT << position)) != 0;
		assertEquals(bit, bits.pop());
		bit = (Constants.MAGIC_VALUE & (Constants.FIRST_BIT << position)) != 0;
		assertEquals(bit, bits.pop());
		
		

		position = 0 * 8;
		bit = (Constants.MAGIC_VALUE & (Constants.EIGTH_BIT << position)) != 0;
		assertEquals(bit, bits.pop());
		bit = (Constants.MAGIC_VALUE & (Constants.SEVENTH_BIT << position)) != 0;
		assertEquals(bit, bits.pop());
		bit = (Constants.MAGIC_VALUE & (Constants.SIXTH_BIT << position)) != 0;
		assertEquals(bit, bits.pop());
		bit = (Constants.MAGIC_VALUE & (Constants.FIFTH_BIT << position)) != 0;
		assertEquals(bit, bits.pop());
		bit = (Constants.MAGIC_VALUE & (Constants.FOURTH_BIT << position)) != 0;
		assertEquals(bit, bits.pop());
		bit = (Constants.MAGIC_VALUE & (Constants.THIRD_BIT << position)) != 0;
		assertEquals(bit, bits.pop());
		bit = (Constants.MAGIC_VALUE & (Constants.SECOND_BIT << position)) != 0;
		assertEquals(bit, bits.pop());
		bit = (Constants.MAGIC_VALUE & (Constants.FIRST_BIT << position)) != 0;
		assertEquals(bit, bits.pop());
		

		position = 1 * 8;
		bit = (2 & (Constants.EIGTH_BIT << position)) != 0;
		assertEquals(bit, bits.pop());
		bit = (2 & (Constants.SEVENTH_BIT << position)) != 0;
		assertEquals(bit, bits.pop());
		bit = (2 & (Constants.SIXTH_BIT << position)) != 0;
		assertEquals(bit, bits.pop());
		bit = (2 & (Constants.FIFTH_BIT << position)) != 0;
		assertEquals(bit, bits.pop());
		bit = (2 & (Constants.FOURTH_BIT << position)) != 0;
		assertEquals(bit, bits.pop());
		bit = (2 & (Constants.THIRD_BIT << position)) != 0;
		assertEquals(bit, bits.pop());
		bit = (2 & (Constants.SECOND_BIT << position)) != 0;
		assertEquals(bit, bits.pop());
		bit = (2 & (Constants.FIRST_BIT << position)) != 0;
		assertEquals(bit, bits.pop());
		

		position = 0 * 8;
		bit = (2 & (Constants.EIGTH_BIT << position)) != 0;
		assertEquals(bit, bits.pop());
		bit = (2 & (Constants.SEVENTH_BIT << position)) != 0;
		assertEquals(bit, bits.pop());
		bit = (2 & (Constants.SIXTH_BIT << position)) != 0;
		assertEquals(bit, bits.pop());
		bit = (2 & (Constants.FIFTH_BIT << position)) != 0;
		assertEquals(bit, bits.pop());
		bit = (2 & (Constants.FOURTH_BIT << position)) != 0;
		assertEquals(bit, bits.pop());
		bit = (2 & (Constants.THIRD_BIT << position)) != 0;
		assertEquals(bit, bits.pop());
		bit = (2 & (Constants.SECOND_BIT << position)) != 0;
		assertEquals(bit, bits.pop());
		bit = (2 & (Constants.FIRST_BIT << position)) != 0;
		assertEquals(bit, bits.pop());
		

		position = 0 * 8;
		bit = (firstByte & (Constants.EIGTH_BIT << position)) != 0;
		assertEquals(bit, bits.pop());
		bit = (firstByte & (Constants.SEVENTH_BIT << position)) != 0;
		assertEquals(bit, bits.pop());
		bit = (firstByte & (Constants.SIXTH_BIT << position)) != 0;
		assertEquals(bit, bits.pop());
		bit = (firstByte & (Constants.FIFTH_BIT << position)) != 0;
		assertEquals(bit, bits.pop());
		bit = (firstByte & (Constants.FOURTH_BIT << position)) != 0;
		assertEquals(bit, bits.pop());
		bit = (firstByte & (Constants.THIRD_BIT << position)) != 0;
		assertEquals(bit, bits.pop());
		bit = (firstByte & (Constants.SECOND_BIT << position)) != 0;
		assertEquals(bit, bits.pop());
		bit = (firstByte & (Constants.FIRST_BIT << position)) != 0;
		assertEquals(bit, bits.pop());
		

		position = 0 * 8;
		bit = (secondByte & (Constants.EIGTH_BIT << position)) != 0;
		assertEquals(bit, bits.pop());
		bit = (secondByte & (Constants.SEVENTH_BIT << position)) != 0;
		assertEquals(bit, bits.pop());
		bit = (secondByte & (Constants.SIXTH_BIT << position)) != 0;
		assertEquals(bit, bits.pop());
		bit = (secondByte & (Constants.FIFTH_BIT << position)) != 0;
		assertEquals(bit, bits.pop());
		bit = (secondByte & (Constants.FOURTH_BIT << position)) != 0;
		assertEquals(bit, bits.pop());
		bit = (secondByte & (Constants.THIRD_BIT << position)) != 0;
		assertEquals(bit, bits.pop());
		bit = (secondByte & (Constants.SECOND_BIT << position)) != 0;
		assertEquals(bit, bits.pop());
		bit = (secondByte & (Constants.FIRST_BIT << position)) != 0;
		assertEquals(bit, bits.pop());
		
	}

	public void testPushBytes() {
		
		byte[] bytes = {(byte) 0x83, (byte) 0x83};
		
		LinkedList<Boolean> bits = AStegoImage.getBitBuffer();
		
		AStegoImage.pushBytes(bytes, bits);

		assertEquals(16, bits.size());
		boolean bit = bits.pop();
		assertTrue(bit); // 1
		bit = bits.pop();
		assertFalse(bit);  // 0
		bit = bits.pop();
		assertFalse(bit);// 0
		bit = bits.pop();
		assertFalse(bit);// 0
		bit = bits.pop();
		assertFalse(bit);// 0
		bit = bits.pop();
		assertFalse(bit);// 0
		bit = bits.pop();
		assertTrue(bit);// 1
		bit = bits.pop();
		assertTrue(bit); // 1
		assertEquals(8, bits.size());
		bit = bits.pop();
		assertTrue(bit); // 1
		bit = bits.pop();
		assertFalse(bit);  // 0
		bit = bits.pop();
		assertFalse(bit);// 0
		bit = bits.pop();
		assertFalse(bit);// 0
		bit = bits.pop();
		assertFalse(bit);// 0
		bit = bits.pop();
		assertFalse(bit);// 0
		bit = bits.pop();
		assertTrue(bit);// 1
		bit = bits.pop();
		assertTrue(bit); // 1
		
		
		
	}

	public void testPushByte() {
		byte bite = (byte) 0x83;
		
		// 1000 0011
		
		LinkedList<Boolean> bits = AStegoImage.getBitBuffer();
		AStegoImage.pushByte(bite, bits);
		assertEquals(8, bits.size());
		boolean bit = bits.pop();
		assertTrue(bit); // 1
		bit = bits.pop();
		assertFalse(bit);  // 0
		bit = bits.pop();
		assertFalse(bit);// 0
		bit = bits.pop();
		assertFalse(bit);// 0
		bit = bits.pop();
		assertFalse(bit);// 0
		bit = bits.pop();
		assertFalse(bit);// 0
		bit = bits.pop();
		assertTrue(bit);// 1
		bit = bits.pop();
		assertTrue(bit); // 1
		
	}

	public void testGetBitBuffer() {
		assertNotNull(AStegoImage.getBitBuffer());
	}

	public void testPushBit() {
		LinkedList<Boolean> bits = AStegoImage.getBitBuffer();
		
		Byte b;
		
		assertNull(AStegoImage.pushBit(true, bits)); // 1
		assertNull(AStegoImage.pushBit(false, bits)); // 0
		assertNull(AStegoImage.pushBit(false, bits)); // 0
		assertNull(AStegoImage.pushBit(false, bits)); // 0
		assertNull(AStegoImage.pushBit(false, bits)); // :: 0
		assertNull(AStegoImage.pushBit(false, bits)); // :: 0
		assertNull(AStegoImage.pushBit(true, bits));// ::  1
		b = (AStegoImage.pushBit(true, bits)); // :: 1
		
		assertNotNull(b);
		assertEquals((Byte) (byte) 0x83, b);
		
	}

}
