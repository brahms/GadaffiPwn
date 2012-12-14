package hack.pwn.gadaffi.steganography;

import java.util.EnumSet;
import java.util.HashMap;

public enum PacketType {
	UNKNOWN((byte)0xFF),
	EMAIL((byte)0x01);
	
	private byte bite;
	private static HashMap<Byte, PacketType> lookup = new HashMap<Byte, PacketType>();
	
	private PacketType(byte bite) {
		this.bite = bite;
	}
	
	public static PacketType fromByte(byte bite) {
		PacketType type =  lookup.get(bite);
		
		return (type == null) ? UNKNOWN : type;
	}
	
	public byte toByte() {
		return this.bite;
	}
	
	public static PacketType getPacketType(Payload payload) {
		PacketType type = PacketType.UNKNOWN;
		if(payload instanceof Email) {
			type = PacketType.EMAIL;
		}
		return type;
	}
	
	
	static {
		EnumSet<PacketType>enumSet = EnumSet.allOf(PacketType.class);
		
		for(PacketType type : enumSet) {
			lookup.put(type.toByte(), type);
		}
	}
	
	
}
