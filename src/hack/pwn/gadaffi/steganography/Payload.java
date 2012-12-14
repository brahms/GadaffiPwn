package hack.pwn.gadaffi.steganography;

import java.nio.ByteBuffer;


abstract public class Payload {
	
	abstract public byte[] toBytes();
	abstract public void toByteBuffer(ByteBuffer buffer);
	abstract public int toBytesLength();
}
