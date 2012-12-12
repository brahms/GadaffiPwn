package hack.pwn.gadaffi.database;

import hack.pwn.gadaffi.Utils;
import hack.pwn.gadaffi.steganography.Packet;
import hack.pwn.gadaffi.steganography.PacketType;
import hack.pwn.gadaffi.steganography.Part;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class InboundPacketPeer extends BasePeer{
	private static String TAG = "database.InboundPacketPeer";

	public static Integer insertPacket(Packet packet) throws Exception {
		SQLiteDatabase db = getWriteableDatabase();
		Integer id = null;
		
		Utils._assert(packet.getId() == null);
		Utils._assert(packet.getParts().size() == 1);
		Utils._assert(packet.getPacketType() == PacketType.UNKNOWN);
		Utils._assert(packet.getIsCompleted() == false);
		
		try {
			db.beginTransaction();
			
			id = getNextId(db, InboundPacketEntry.TABLE_NAME);
			Log.v(TAG, String.format("Inserting packet: Id %d, SeqNum %d, Parts %d, From %s", id, packet.getSequenceNumber(), packet.getParts().size(), packet.getFrom()));
			ContentValues cv = new ContentValues();
			cv.put(InboundPacketEntry._ID, id);
			cv.put(InboundPacketEntry.COLUMN_NAME_SEQUENCE_NUMBER, packet.getSequenceNumber());
			cv.put(InboundPacketEntry.COLUMN_NAME_FLAGS, packet.getFlags());
			cv.put(InboundPacketEntry.COLUMN_NAME_FROM, packet.getFrom());
			
			db.insertOrThrow(InboundPacketEntry.TABLE_NAME, null, cv);

			packet.setId(id);
			
			
			for(Part part : packet.getParts().values()) {
				InboundPartPeer.insertPart(db, part);
			}

			Log.v(TAG, "Packet insert success.");
			db.setTransactionSuccessful();
			
			
		}
		catch(Exception ex) {
			Log.e(TAG, "Unable to insert packet: " + packet, ex);
			throw ex;
		}
		finally {
			db.endTransaction();
			db.close();
		}
		
		return id;
		
	}

	public static Packet getInboundPacket(String from, Byte seqNumber) {
		SQLiteDatabase db = getReadableDatabase();
		Packet packet = null;
		Cursor packetCursor = null;
		try {
			db.beginTransaction();
			
			Log.v(TAG, String.format("Querying for packet with %s sequence number and %s from", seqNumber, from));
			packetCursor = db.query(InboundPacketEntry.TABLE_NAME, 
					new String[]{
						InboundPacketEntry._ID,
						InboundPacketEntry.COLUMN_NAME_FROM,
						InboundPacketEntry.COLUMN_NAME_FLAGS,
						InboundPacketEntry.COLUMN_NAME_SEQUENCE_NUMBER,
					}, 
					InboundPacketEntry.COLUMN_NAME_FROM + "= ? and " + InboundPacketEntry.COLUMN_NAME_SEQUENCE_NUMBER + "= ?",
					new String[]{
						from, 
						seqNumber.toString()
					}, null, null, null);
					
			if(packetCursor.moveToFirst()) {
				packet = new Packet();
				packet.setId(packetCursor.getInt(packetCursor.getColumnIndexOrThrow(InboundPacketEntry._ID)));
				packet.setFlags((byte) packetCursor.getInt(packetCursor.getColumnIndexOrThrow(InboundPacketEntry.COLUMN_NAME_FLAGS)));
				packet.setFrom(packetCursor.getString(packetCursor.getColumnIndexOrThrow(InboundPacketEntry.COLUMN_NAME_FROM)));
				packet.setSequenceNumber((byte) packetCursor.getInt(packetCursor.getColumnIndexOrThrow(InboundPacketEntry.COLUMN_NAME_SEQUENCE_NUMBER)));
				
				packet.setParts(InboundPartPeer.getPartsForPacket(db, packet));
			} 
		}
		catch(Exception ex) {
			Log.e(TAG, String.format("Couldn't retrieve packet for from_number '%s' and sequence number '%d'", from, seqNumber), ex);
		}
		finally {
			if(packetCursor != null) packetCursor.close();
			db.endTransaction();
			db.close();
		}
		
		return packet;
	}

	public static void updatePacket(Packet packet) {
		
		Utils._assert(packet.getId() != null);
		Utils._assert(packet.getSequenceNumber() != null);
		Utils._assert(packet.getFrom() != null);
		
		SQLiteDatabase db = getWriteableDatabase();
		
		try {
			Log.v(TAG, "Updating packet " + packet.toString());
			db.beginTransaction();
			for(Part part : packet.getParts().values()) {
				if(part.getId() == null) {
					InboundPartPeer.insertPart(db, part);
				}
				else {
					Log.v(TAG, String.format("Part: %s already in db.", part.toString()));
				}
			}
			Log.v(TAG, "Updated Packet: Success.");
			db.setTransactionSuccessful();
		}
		catch(Exception ex) {
			Log.e(TAG, String.format("Unable to update packet for from_number '%s' and sequence number '%d'", packet.getFrom(), packet.getSequenceNumber()), ex);
		}
		finally {
			db.endTransaction();
			db.close();
		}
		
	}
	
	public static void deletePacket(Packet packet) {
		Utils._assert(packet.getId() != null);
		Utils._assert(packet.getSequenceNumber() != null);
		Utils._assert(packet.getFrom() != null);
		
		SQLiteDatabase db = getWriteableDatabase();
		
		try {
			db.beginTransaction();
			for(Part part : packet.getParts().values()) {
				InboundPartPeer.deletePart(db, part);
			}
			db.delete(InboundPacketEntry.TABLE_NAME, 
					InboundPacketEntry._ID + "=?", new String[]{packet.getId().toString()});
			
			db.setTransactionSuccessful();
		}
		catch(Exception ex) {
			Log.e(TAG, String.format("Unable to update packet for from_number '%s' and sequence number '%d'", packet.getFrom(), packet.getSequenceNumber()));
		}
		finally {
			db.endTransaction();
			db.close();
		}
	}

}
