package hack.pwn.gadaffi.database;

import hack.pwn.gadaffi.Utils;
import hack.pwn.gadaffi.steganography.Packet;
import hack.pwn.gadaffi.steganography.Part;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;


public class InboundPartPeer extends BasePeer{

	private static final String TAG = "database.InboundPartPeer";

	public static int insertPart(SQLiteDatabase db, Part part) throws Exception {
		int id = getNextId(db, InboundPartEntry.TABLE_NAME);
		
		Log.v(TAG, String.format("Inserting part. ID=%d PartNumb=%d SeqNum=%d PacketId=%d", id, part.getPartNumber(), part.getPacket().getSequenceNumber(), part.getPacket().getId()));
		
		ContentValues cv = new ContentValues();
		cv.put(InboundPartEntry._ID, id);
		cv.put(InboundPartEntry.COLUMN_NAME_PACKET_ID, part.getPacket().getId());
		cv.put(InboundPartEntry.COLUMN_NAME_PART_NUMBER, part.getPartNumber());
		cv.put(InboundPartEntry.COLUMN_NAME_TIME_RECEIVED, part.getTimeReceived().toMillis(false));
		cv.put(InboundPartEntry.COLUMN_NAME_PART_BLOB, part.getPart());
		cv.put(InboundPartEntry.COLUMN_NAME_FLAGS, part.getFlags());
		
		db.insertOrThrow(InboundPartEntry.TABLE_NAME, null, cv);
		Log.v(TAG, "Success.");
		return id;
		
	}

	public static TreeMap<Integer, Part> getPartsForPacket(SQLiteDatabase db, Packet packet) {
		
		List<Part> partsList = retrieve(db, "packet_id=?", new String[]{packet.getId().toString()});
		
		if(partsList.isEmpty() == false) {

			TreeMap<Integer, Part> parts = new TreeMap<Integer, Part>();
			for(Part part : partsList) {
				part.setPacket(packet);
				part.setSequenceNumber(part.getSequenceNumber());
				parts.put(part.getPartNumber(), part);
			}
			return parts;
		}
		
		return null;
	}

	protected static List<Part> retrieve(SQLiteDatabase db,
			String selection, String[] selectionArgs) {
		ArrayList<Part> list = new ArrayList<Part>();
		
		Cursor c = db.query(InboundPartEntry.TABLE_NAME, 
				new String[]{
					InboundPartEntry._ID,
					InboundPartEntry.COLUMN_NAME_FLAGS,
					InboundPartEntry.COLUMN_NAME_PART_NUMBER,
					InboundPartEntry.COLUMN_NAME_TIME_RECEIVED
				}, 
				selection, selectionArgs, null, null, null);
		
		while(c.moveToNext()) {
			Part part = new Part();
			part.setId(c.getInt(c.getColumnIndexOrThrow(InboundPartEntry._ID)));
			part.setFlags((byte) c.getInt(c.getColumnIndexOrThrow(InboundPartEntry.COLUMN_NAME_FLAGS)));
			part.setPartNumber(c.getInt(c.getColumnIndexOrThrow(InboundPartEntry.COLUMN_NAME_PART_NUMBER)));
			part.setTimeReceived(c.getLong(c.getColumnIndexOrThrow(InboundPartEntry.COLUMN_NAME_TIME_RECEIVED)));
			list.add(part);
		}
		c.close();
		return list;
		
	}

	public static byte[] getPartsPart(Part part) {
		Utils._assert(part.getId() != null);
		SQLiteDatabase db = getReadableDatabase();
		Cursor cursor = null;
		try {
			cursor = db.query(InboundPartEntry.TABLE_NAME, new String[]{InboundPartEntry.COLUMN_NAME_PART_BLOB}, InboundPartEntry._ID + "=?", new String[]{part.getId().toString()}, null, null, null);
			if(cursor.moveToFirst()) {
				return cursor.getBlob(0);
			}
			else {
				throw new Exception("No results.");
			}
		}
		catch(Exception ex) {
			Log.e(TAG, "Unable to get part's part for: " + part, ex);
		}
		finally {
			if(cursor != null) cursor.close();
			db.close();
		}
		
		return null;
		
	}

	public static void deletePart(SQLiteDatabase db, Part part) {
		if(part.getId() != null)
			db.delete(InboundPartEntry.TABLE_NAME, 
					InboundPartEntry._ID + "=?", 
					new String[]{part.getId().toString()});
	}
}
