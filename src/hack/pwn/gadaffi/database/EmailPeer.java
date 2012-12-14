package hack.pwn.gadaffi.database;

import hack.pwn.gadaffi.Constants;
import hack.pwn.gadaffi.Utils;
import hack.pwn.gadaffi.steganography.Email;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class EmailPeer extends BasePeer {
	private static final String TAG = "database.EmailPeer";

	public static void insertEmail(Email email) throws Exception {
		Log.v(TAG, "Entered insertEmail()");
		SQLiteDatabase db = getWriteableDatabase();
		Utils._assert(email.getEmailId() == null);
		
		try {
			db.beginTransaction();
			
			Log.v(TAG, "Inserting payload for email");
			ContentValues cv = new ContentValues();
			
			int id = getNextId(db, EmailEntry.TABLE_NAME);
			email.setEmailId(id);
			
			if (email.getMessage() == null) {
				email.setMessage("");
			}
			
			if (email.getSubject() == null) {
				email.setSubject("");
			}
			
			if (email.getTimeReceived() == null ) {
				email.setTimeReceived(Utils.getNow());
			}
			
			cv.put(EmailEntry._ID, id);
			cv.put(EmailEntry.COLUMN_NAME_MESSAGE, email.getMessage());
			cv.put(EmailEntry.COLUMN_NAME_SUBJECT, email.getSubject());
			cv.put(EmailEntry.COLUMN_NAME_TIME_RECEIVED, email.getTimeReceived().toMillis(false));
			cv.put(EmailEntry.COLUMN_NAME_FROM, email.getFrom());
			cv.put(EmailEntry.COLUMN_NAME_TO, email.getTo());
			db.insertOrThrow(EmailEntry.TABLE_NAME, null, cv);
			
			AttachmentPeer.insertAttachments(db, email);
			
			
			db.setTransactionSuccessful();
			Log.v(TAG, String.format("Succesfully inserted email %s", email));
		}
		catch (Exception ex) {
			Log.e(TAG, "Error inserting email: " + email.toString(), ex);
			throw ex;
		}
		finally {
			db.endTransaction();
			db.close();
		}
	}
	protected static List<Email> retrieve (String selection, String[] selectionArgs) {
		return retrieve(selection, selectionArgs, null);
	}
	protected static List<Email> retrieve (String selection, String[] selectionArgs, String limit) {
		Log.v(TAG, "Entered retrieve()");
		SQLiteDatabase db = getReadableDatabase();
		List<Email> emails = new ArrayList<Email>();
		try {
			db.beginTransaction();
			
			Cursor cursor = db.query(EmailEntry.TABLE_NAME,
					new String[] {
						EmailEntry.COLUMN_NAME_MESSAGE,
						EmailEntry.COLUMN_NAME_SUBJECT,
						EmailEntry._ID,
						EmailEntry.COLUMN_NAME_FROM,
						EmailEntry.COLUMN_NAME_TIME_RECEIVED,
						EmailEntry.COLUMN_NAME_TO
					}, selection, selectionArgs, null, null, Constants.ORDER_BY_ID_DESC, limit);
			
			while (cursor.moveToNext()) {
				Email email = new Email();
				email.setSubject(cursor.getString(cursor.getColumnIndexOrThrow(EmailEntry.COLUMN_NAME_SUBJECT)));
				email.setMessage(cursor.getString(cursor.getColumnIndexOrThrow(EmailEntry.COLUMN_NAME_MESSAGE)));
				email.setEmailId(cursor.getInt(cursor.getColumnIndexOrThrow(EmailEntry._ID)));
				email.setFrom(cursor.getString(cursor.getColumnIndexOrThrow(EmailEntry.COLUMN_NAME_FROM)));
				email.setTo(cursor.getString(cursor.getColumnIndexOrThrow(EmailEntry.COLUMN_NAME_TO)));
				email.setTimeReceived(cursor.getLong(cursor.getColumnIndexOrThrow(EmailEntry.COLUMN_NAME_TIME_RECEIVED)));
				email.setAttachments(AttachmentPeer.getAttachmentsForEmail(db, email));
				
				emails.add(email);
			}
			
			cursor.close();
		}
		catch (Exception ex) {
			Log.e(TAG, "Error retrieving emails.", ex);
		}
		finally {
			db.endTransaction();
			db.close();
		}
		Log.v(TAG, String.format("Returning %d emails.", emails.size()));
		return emails;
	}
	
	public static Email getEmailById(int id) {
		List<Email> emails = retrieve(EmailEntry._ID + "=?", new String[]{Integer.toString(id)});
		
		return emails.isEmpty() ? null : emails.get(0);
	}

	public static List<Email> getLatestEmails() {
		return retrieve(null, null, "50");
	}
}
