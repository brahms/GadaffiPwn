package hack.pwn.gadaffi.database;

import hack.pwn.gadaffi.Utils;
import hack.pwn.gadaffi.steganography.Payload;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
 class PayloadPeer extends BasePeer {
	 private static final String TAG = "database.PayloadPeer";

	public static void insertPayload(SQLiteDatabase db, Payload payload) throws Exception {
		Log.v(TAG, "Entered insertPayload()");
		Utils._assert(payload.getPayloadId() == null);
		
		if(payload.getTimeReceived() == null) {
			payload.setTimeReceived(Utils.getNow());
		}
		
		int id = getNextId(db, PayloadEntry.TABLE_NAME);
		
		
		payload.setPayloadId(id);
		
		ContentValues cv = new ContentValues();
		cv.put(PayloadEntry._ID, payload.getPayloadId());
		cv.put(PayloadEntry.COLUMN_NAME_TIME_RECEIVED, payload.getTimeReceived().toMillis(false));
		cv.put(PayloadEntry.COLUMN_NAME_FROM, payload.getFrom());
		
		db.insertOrThrow(PayloadEntry.TABLE_NAME, null, cv);
		
		
	}

	public static void retrieve(SQLiteDatabase db, Payload payload) throws Exception {
		Log.v(TAG, "Entered retrieve()");
		Utils._assert(payload.getPayloadId() != null);
		
		Cursor cursor = db.query(PayloadEntry.TABLE_NAME, 
				new String[]{
					PayloadEntry.COLUMN_NAME_FROM,
					PayloadEntry.COLUMN_NAME_TIME_RECEIVED
				}, PayloadEntry._ID + "=?", new String[]{payload.getPayloadId().toString()}, null, null, null);
		
		if (cursor.moveToNext()) {
			payload.setFrom(cursor.getString(cursor.getColumnIndexOrThrow(PayloadEntry.COLUMN_NAME_FROM)));
			payload.setTimeReceived(cursor.getLong(cursor.getColumnIndexOrThrow(PayloadEntry.COLUMN_NAME_TIME_RECEIVED)));

			Log.v(TAG, String.format("Successfuly retrieved %s", payload));
		}
		else {
			throw new Exception(String.format("Could not retrieve payload for id %s", payload));
		}
		
	}

}
