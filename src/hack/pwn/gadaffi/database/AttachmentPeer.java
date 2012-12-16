package hack.pwn.gadaffi.database;

import hack.pwn.gadaffi.Utils;
import hack.pwn.gadaffi.steganography.Attachment;
import hack.pwn.gadaffi.steganography.Email;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import android.util.Log;

public class AttachmentPeer extends BasePeer {
	private static final String TAG = "database.AttachmentPeer";
	public static void insertAttachments(SQLiteDatabase db, Email email) throws Exception{
		Utils._assert(email != null);
		Utils._assert(email.getAttachments() != null);
		Log.v(TAG, "Entering insertAttachments");
		
		int i = 1;
		for(Attachment attachment : email.getAttachments()) {
			ContentValues cv = new ContentValues();
			int id = getNextId(db, AttachmentEntry.TABLE_NAME);
			attachment.setAttachmentId(id);
			
			Log.v(TAG, String.format("Inserting attachment %d with an id of %d: %s", i++, id, attachment));
			
			cv.put(AttachmentEntry._ID, id);
			cv.put(AttachmentEntry.COLUMN_NAME_DATA, attachment.getData());
			cv.put(AttachmentEntry.COLUMN_NAME_EMAIL_ID, email.getEmailId());
			cv.put(AttachmentEntry.COLUMN_NAME_FILENAME, attachment.getFilename());
			cv.put(AttachmentEntry.COLUMN_NAME_MIME_TYPE, attachment.getMimeType());
			cv.put(AttachmentEntry.COLUMN_NAME_DATA_LENGTH, attachment.getData().length);
			db.insertOrThrow(AttachmentEntry.TABLE_NAME, null, cv);
		}
		
	}
	
	public static List<Attachment> retrieve(SQLiteDatabase db, String selection, String[] selectionArgs) {
		ArrayList<Attachment> attachments = new ArrayList<Attachment>();

		Cursor cursor = db.query(AttachmentEntry.TABLE_NAME, 
				new String[] {
					AttachmentEntry._ID,
					AttachmentEntry.COLUMN_NAME_FILENAME,
					AttachmentEntry.COLUMN_NAME_MIME_TYPE,
					AttachmentEntry.COLUMN_NAME_DATA_LENGTH,
					AttachmentEntry.COLUMN_NAME_EMAIL_ID,
				}, selection, selectionArgs, null, null, AttachmentEntry._ID + " ASC");
		
		while (cursor.moveToNext()) {
			Attachment attachment = new Attachment();
			attachment.setMimeType(cursor.getString(cursor.getColumnIndexOrThrow(AttachmentEntry.COLUMN_NAME_MIME_TYPE)));
			attachment.setFilename(cursor.getString(cursor.getColumnIndexOrThrow(AttachmentEntry.COLUMN_NAME_FILENAME)));
			attachment.setAttachmentId(cursor.getInt(cursor.getColumnIndexOrThrow(AttachmentEntry._ID)));
			attachment.setBytesLength(cursor.getLong(cursor.getColumnIndexOrThrow(AttachmentEntry.COLUMN_NAME_DATA_LENGTH)));
			attachment.setEmailId(cursor.getInt(cursor.getColumnIndexOrThrow(AttachmentEntry.COLUMN_NAME_EMAIL_ID)));
			attachments.add(attachment);
		}
		cursor.close();
		return attachments;
	}

	public static byte[] getData(Attachment filePayload) {
		SQLiteDatabase db = getReadableDatabase();
		Cursor c = null;
		try {
		    c = db.query(AttachmentEntry.TABLE_NAME, new String[]{AttachmentEntry.COLUMN_NAME_DATA}, AttachmentEntry._ID + "=?", new String[]{filePayload.getAttachmentId().toString()}, null, null, null);
			if (c.moveToFirst()) {
				return c.getBlob(0);
			}
			else {
				throw new Exception("Unable to retrieve attachment");
			}
		}
		catch (Exception ex) {
			Log.e(TAG, "Error retrieving attachment data.", ex);
			return null;
		}
		finally {
			if (c != null) c.close();
			db.close();
		}
	}

	public static List<Attachment> getAttachmentsForEmail(SQLiteDatabase db, Email email) {
		return retrieve(db, AttachmentEntry.COLUMN_NAME_EMAIL_ID + "=?", new String[]{email.getEmailId().toString()});
	}

    public static void deleteAttachmentsForEmailsIds(SQLiteDatabase db, List<Integer> ids)
    {
        
        String where = AttachmentEntry.COLUMN_NAME_EMAIL_ID + " IN (" + TextUtils.join(",", ids) + ")";

        Log.v(TAG, "Executing sql: " + AttachmentEntry.SQL_DELETE + where);
        db.execSQL(AttachmentEntry.SQL_DELETE + where);
        
    }

}
