package hack.pwn.gadaffi.steganography;

import hack.pwn.gadaffi.Constants;
import hack.pwn.gadaffi.Utils;
import hack.pwn.gadaffi.database.InboundPartPeer;
import hack.pwn.gadaffi.exceptions.DecodingException;
import hack.pwn.gadaffi.exceptions.EncodingException;

import java.nio.ByteBuffer;
import java.util.zip.CRC32;

import android.text.format.Time;
import android.util.Log;

/**
 * This class holds the information
 * from a single StegoData packet
 * 
 * It can be embedded into a Stego destination such as an image
 * 
 * @author cbrahms
 *
 */
public class Part {
	static final String TAG = "steganography.Part";

	public static final int FLAG_IS_LAST = 0x1;
	
	Integer       id              = null;
	Byte          sequenceNumber  = null;
	Integer       partNumber      = null;
	Byte          flags           = null;
	byte[]        part            = null;
	Packet        packet          = null;
	Time          timeReceived    = null;

	public static Part decode(byte[] embeddedData)  throws DecodingException
	{
		Log.v(TAG, "Entered decode().");
		Part part = new Part();
		
		if(embeddedData.length < Constants.PART_HEADER_PLUS_CHECKSUM_LENGTH) {
			throw new DecodingException("Data is not long enough for checksum");
		}
		CRC32 actualCrc = new CRC32();
		actualCrc.update(embeddedData, 0, embeddedData.length - Constants.PART_CHECKSUM_LENGTH);
			
		long expectedCrc = 
				ByteBuffer.wrap(embeddedData, 
						embeddedData.length - Constants.PART_CHECKSUM_LENGTH, 
						Constants.PART_CHECKSUM_LENGTH)
						.order(Constants.BYTE_ORDER)
						.getInt() & 0xFFFFFFFF;
		if(expectedCrc != (int) actualCrc.getValue()) {
			throw new DecodingException(String.format("Expected CRC32 (%x) doesn't match actual CRC32 (%x)", expectedCrc, actualCrc.getValue()));
		}
		
		ByteBuffer byteBuffer = 
				ByteBuffer
					.wrap(embeddedData, 0, embeddedData.length - Constants.PART_CHECKSUM_LENGTH)
					.order(Constants.BYTE_ORDER);
		
		
		byte[] data = new byte[embeddedData.length - Constants.PART_HEADER_PLUS_CHECKSUM_LENGTH];
		part.setSequenceNumber(byteBuffer.get());
		part.setPartNumber(byteBuffer.get() & 0xFF);
		part.setFlags(byteBuffer.get());
		
		byteBuffer.get(data);
		
		part.setPart(data);
		part.setTimeReceived(Utils.getNow());
		
		return part;
		
		
		
	}
	/**
	 * @return the partNumber
	 */
	public Integer getPartNumber() {
		return partNumber;
	}

	/**
	 * @param partNumber the partNumber to set
	 */
	public void setPartNumber(Integer partNumber) {
		this.partNumber = partNumber;
	}

	/**
	 * @return the flags
	 */
	public Byte getFlags() {
		return flags;
	}
	
	public boolean isLast() {
		return (flags & FLAG_IS_LAST) != 0;
	}

	/**
	 * @param flags the flags to set
	 */
	public void setFlags(Byte flags) {
		this.flags = flags;
	}

	/**
	 * @return the part
	 */
	public byte[] getPart() {
		if(part == null) {
			part = InboundPartPeer.getPartsPart(this);
		}
		
		return part;
	}

	/**
	 * @param part the part to set
	 */
	public void setPart(byte[] part) {
		this.part = part;
	}

	/**
	 * @param byte1 the sequenceNumber to set
	 */
	public void setSequenceNumber(Byte byte1) {
		this.sequenceNumber = byte1;
	}
	public Byte getSequenceNumber() {
		return (byte) (this.sequenceNumber == null ? getPacket().getSequenceNumber() : this.sequenceNumber);
	}
	
	public static Part fromByteBuffer(Packet packet, int partNumber, ByteBuffer buffer, int maxLength) {
		int bytesToRead = Math.min(maxLength - Constants.PART_HEADER_PLUS_CHECKSUM_LENGTH, buffer.remaining());
		Utils._assert(bytesToRead > 0);
		Part part = new Part();
		part.setPacket(packet);
		part.setPartNumber(partNumber);
		part.flags = 0;
		part.flags = (byte) ((buffer.remaining() == bytesToRead) ? (part.flags ^ FLAG_IS_LAST) : part.flags);
		byte[] bytes = new byte[bytesToRead];
		buffer.get(bytes);
		part.setPart(bytes);
		
		return part;
	}
	
	public void setPacket(Packet packet) {
		this.packet = packet;
		
	}
	public byte[] encode() throws EncodingException{
		Log.v(TAG, String.format("Entered encode()"));
		ByteBuffer outBuffer = ByteBuffer
		.allocate(Constants.PART_HEADER_PLUS_CHECKSUM_LENGTH + part.length)
		.order(Constants.BYTE_ORDER);

		CRC32 crc = new CRC32();
		byte bite = (byte) (getPacket().getSequenceNumber() & 0xFF);
		crc.update(bite);
		outBuffer.put(bite);
		bite = (byte) (getPartNumber() & 0xFF);
		crc.update(bite);
		outBuffer.put(bite);
		
		bite = getFlags();
		crc.update(flags);
		outBuffer.put(flags);
		

		outBuffer.put(getPart());
		
		crc.update(getPart());
		outBuffer.putInt((int) crc.getValue());

		
		return outBuffer.array();
		
	}
	public Packet getPacket() {
		return packet;
	}
	public Integer getId() {
		return id;
	}
	
	/**
	 * @return the timeReceived
	 */
	public Time getTimeReceived() {
		return timeReceived;
	}
	/**
	 * @param timeReceived the timeReceived to set
	 */
	public void setTimeReceived(Time timeReceived) {
		this.timeReceived = timeReceived;
	}
	public void setId(int id) {
		this.id = id;
	}
	public void setTimeReceived(long time) {
		this.timeReceived = new Time();
		this.timeReceived.set(time);
	}
	
	@Override
	public String toString() {
		int seq = getSequenceNumber() == null ? getPacket().getSequenceNumber() : getSequenceNumber();
		
		return String.format("[Id %d, Seq %d, PartNum %d, TotalBytes %d, Flags %d]", getId(), seq, getPartNumber(), getPart().length, getFlags());
	}
	
}
