package hack.pwn.gadaffi.steganography;

import hack.pwn.gadaffi.Constants;
import hack.pwn.gadaffi.Utils;
import hack.pwn.gadaffi.database.InboundPacketPeer;
import hack.pwn.gadaffi.exceptions.DecodingException;
import hack.pwn.gadaffi.exceptions.EncodingException;

import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import android.util.Log;

/**
 * This class holds a set of StegoDataPackets and can tell if the set of packets
 * is completed (we have received them all)
 * 
 * It can also encoded a given set of data into a set a packets
 * 
 * @author cbrahms
 * 
 */
public class Packet {
	Integer id = null;
	Boolean isCompleted = false;
	Byte    sequenceNumber = null;
	String from = null;
	TreeMap<Integer, Part> parts = new TreeMap<Integer, Part>();
	Payload payload = null;
	InboundPacketPeer peer = new InboundPacketPeer();
	private byte flags;
	
	
	static String TAG = "steganography.Packet";
	public Packet() {
		
	}
	public Packet(String from, Part part) {
		this.setSequenceNumber(part.getSequenceNumber());
		this.setFrom(from);
		this.addPart(part);
	}

	/**
	 * @return the isCompleted
	 */
	public Boolean getIsCompleted() {
		return isCompleted;
	}

	/**
	 * @param isCompleted
	 *            the isCompleted to set
	 */
	public void setIsCompleted(Boolean isCompleted) {
		this.isCompleted = isCompleted;
	}

	/**
	 * @return the sequenceNumber
	 */
	public Byte getSequenceNumber() {
		return sequenceNumber;
	}

	/**
	 * @param sequenceNumber
	 *            the sequenceNumber to set
	 */
	public void setSequenceNumber(byte sequenceNumber) {
		this.sequenceNumber =  sequenceNumber;
	}

	/**
	 * @return the from
	 */
	public String getFrom() {
		return from;
	}

	/**
	 * @param from
	 *            the from to set
	 */
	public void setFrom(String from) {
		this.from = from;
	}
	
	
	
	public static Packet encode(Payload payload, List<Integer> maxLengths) throws EncodingException
	{
		Packet packet = new Packet();
		packet.setPayload(payload);
		Part part = null;
		Iterator<Integer> it = maxLengths.iterator();
		ByteBuffer buffer = 
				ByteBuffer
				.allocate(Constants.PACKET_HEADER_LENGTH + payload.toBytesLength())
				.order(Constants.BYTE_ORDER);
		PacketType type = PacketType.getPacketType(payload);
		
		buffer.put(type.toByte());
		buffer.put((byte) 0x00); // flag byte
		payload.toByteBuffer(buffer);
		buffer.clear();
		
		Log.v(TAG, String.format("Encoding Payload of type: %s of length %d", type, buffer.remaining()));
		Integer maxLength = null;
		int partNumber = 0;
		do {	
			if(it.hasNext() == false) {
				throw new EncodingException("Not enough max lengths.");
			}
			maxLength = it.next();
			part = Part.fromByteBuffer(packet, partNumber, buffer, maxLength);
			Log.v(TAG, String.format("Part %d given a max length of %d will fit %d bytes of the payload.", partNumber, maxLength, part.getPart().length));
			packet.getParts().put(partNumber, part);
			partNumber++;
		} while (!part.isLast());
		
		
		return packet;
	}
	
	public void setPayload(Payload payload) {
		this.payload = payload;
	}
	
	public static Packet processIncomingData(String from, byte[] embeddedData)
			throws DecodingException {
		Part part = Part.decode(embeddedData);
		Packet packet = null;
		if (part.isLast() && part.getPartNumber() == 0) {
			packet = new Packet(from, part);
		} 
		else {
			packet = InboundPacketPeer
					.getInboundPacket(from, part.getSequenceNumber());
			if (packet == null) {
				packet = new Packet(from, part);
			} 
			else {
				packet.addPart(part);
			}
		}

		return packet;
	}

	public void addPart(Part newPart) {
		Log.v(TAG, "Entered addPart()");
		Utils._assert (getIsCompleted() == false);	
		Utils._assert(newPart.getSequenceNumber() == this.getSequenceNumber());

		newPart.setPacket(this);
		
		Log.v(TAG, "Put part into partsMap");
		this.parts.put(newPart.partNumber, newPart);
		
		int length = 0;
		Iterator<Entry<Integer, Part>> iterator = getParts().entrySet()
				.iterator();

		while (iterator.hasNext()) {
			Entry<Integer, Part> entry = iterator.next();

			Part part = entry.getValue();
			length += part.getPart().length;
			if (part.isLast() && 
				getParts().size() == part.getPartNumber() + 1) {
				byte[] packet = combineParts(length);
				this.payload = parsePacket(packet);
				this.setIsCompleted(true);
				return;
			}
		}
	}
	
	

	Payload parsePacket(byte[] packet) {

		ByteBuffer byteBuffer = ByteBuffer.wrap(packet).order(
				Constants.BYTE_ORDER);

		byte bite = byteBuffer.get();
		PacketType type = PacketType.fromByte(bite);
		
		byteBuffer.get(); //not used right now

		switch (type) {
		case FILE:
			try {
				return FilePayload.fromByteBuffer(byteBuffer);
			} catch (DecodingException e) {
				Log.e(TAG, "Error decoding file payloads.", e);
			}
		case TEXT:
			return Text.fromByteBuffer(byteBuffer);
		default:
			Log.d(TAG, String.format("Cannot decode packet of type: %x", bite));
			return null;
		}

	}

	public byte getFlags() {
		return this.flags;
	}
	public void setFlags(byte bite) {
		this.flags = bite;
		
	}
	public boolean isValid() {
		return this.payload != null;
	}

	byte[] combineParts(int length) {

		ByteBuffer buffer = ByteBuffer.allocate(length);

		for (Part part : parts.values()) {
			buffer.put(part.getPart());
		}

		return buffer.array();
	}

	public Map<Integer, Part> getParts() {
		return parts;
	}
	/**
	 * @param parts the parts to set
	 */
	public void setParts(TreeMap<Integer, Part> parts) {
		this.parts = parts;
	}
	public Payload getPayload() {
		return payload;
	}
	public Integer getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	
	public PacketType getPacketType()
	{
		return PacketType.getPacketType(getPayload());
	}
	@Override
	public String toString() {
		return String.format("[Id %d, SeqNum %d, From %s, Parts %d]", getId(), getSequenceNumber(), getFrom(), getParts() != null ? getParts().size() : 0);
	}
	public static Integer getMaxEncodableBytes(int maxBytesEncodable) {
		int total = maxBytesEncodable - Constants.PACKET_HEADER_LENGTH - Constants.PART_HEADER_PLUS_CHECKSUM_LENGTH;
		Log.v(TAG, String.format("getMaxEncodableBytes(%d) = %d", maxBytesEncodable, total));
		return total > 0 ? total : 0;
	}

}
