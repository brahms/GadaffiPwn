package hack.pwn.gadaffi.steganography;

import android.util.Log;
import hack.pwn.gadaffi.MimeType;
import hack.pwn.gadaffi.database.PacketPeer;
import hack.pwn.gadaffi.exceptions.DecodingException;

/**
 * This class holds data that will be 
 * encoded or was decoded into/from a set of packets
 *
 */
public class StegoData {
	private static final String TAG = "StegoData";
	public static StegoData decode(byte[] imageBytes, MimeType type,
			String phoneNumber) {
		switch(type) {
		case PNG:
			return decodePng(imageBytes, phoneNumber);
		default:
			return null;
		}
	}

	private static StegoData decodePng(byte[] imageBytes, String phoneNumber){
		StegoData data = null;
		
		long start = System.currentTimeMillis();
		
		Log.i(TAG, "Entered decodePng()");

		Log.i(TAG, "Decoding using PngStegoImage.");
		PngStegoImage image = new PngStegoImage();
		image.setImageBytes(imageBytes);
		try {
			image.decode();
			if(image.hasEmbeddedData()) {

				Log.i(TAG, "Image has embedded data, trying to parse packet");
				Part packet = Part.fromData(image.getEmbeddedData());
				if(packet.isValid()) {
					if(packet.isComplete()) {
						Log.i(TAG, "Packet is valid, and complete. Returning data");
						data = new StegoData();
						data.setData(packet.getData());
						data.setGuid(packet.getGuid());
						data.setPhoneNumber(phoneNumber);
					}
					else {
						Log.i(TAG, "Packet is valid, but not complete.");
						Packet packets = PacketPeer.loadRelatedPackets(phoneNumber, packet);
						packets.add(packet);
						if(packets.isCompleted()) {
							data = new StegoData();
							data.setData(packets.getData());
							data.setGuid(packets.getGuid());
							data.setPhoneNumber(phoneNumber);
							
						}
						else {
							PacketPeer.insertPacket(phoneNumber, packet);
						}
					}
						 
				}
			}
		} 
		catch (DecodingException e) {
			Log.e(TAG, "Error in decodePng", e);
		}
		
		long stop = System.currentTimeMillis();
		Log.i(TAG, "Exiting decodePng() in " + (stop - start) + " ms.");
		return data;
		
	}

	private void setGuid(Object guid) {
		// TODO Auto-generated method stub
		
	}

	private void setData(Object data) {
		// TODO Auto-generated method stub
		
	}

	private void setPhoneNumber(String phoneNumber) {
		// TODO Auto-generated method stub
		
	}

}
