package hack.pwn.gadaffi.steganography;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * This class holds the information
 * from a single StegoData packet
 * 
 * It can be embedded into a Stego destination such as an image
 * 
 * @author cbrahms
 *
 */
public class StegoDataPacket {
	private static final String TAG = "StegoDataPacket";
	
	byte[] mData = null;
	
	public static StegoDataPacket fromData(byte[] embeddedData) {
		StegoDataPacket p = new StegoDataPacket();
		ByteBuffer byteBuffer = ByteBuffer
				.wrap(embeddedData)
				.order(ByteOrder.LITTLE_ENDIAN);
		
		try 
		{
			decodeHeader(byteBuffer);
			decodeData(byteBuffer);
					
		}
		catch(Exception ex)
		{
			
		}
		
		return p;
	}

	private static void decodeData(ByteBuffer embeddedData) {
		// TODO Auto-generated method stub
		
	}

	private static void decodeHeader(ByteBuffer embeddedData) {
		// TODO Auto-generated method stub
		
	}

	public boolean isValid() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isComplete() {
		// TODO Auto-generated method stub
		return false;
	}

	public byte[] getData() {
		// TODO Auto-generated method stub
		return null;
	}

	public Integer getGuid() {
		// TODO Auto-generated method stub
		return null;
	}
	
	public Integer getSequenceNumber()
	{
		return null;
		
	}
	
	public Integer getTotalPackets()
	{
		return null;
	}

}
