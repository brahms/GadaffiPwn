package hack.pwn.gadaffi.database;

import hack.pwn.gadaffi.Utils;
import hack.pwn.gadaffi.steganography.FilePayload;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;


public class FilePeer extends BasePeer {
	private static final String TAG = "database.FilePeer";
	public static void insertFile(FilePayload file) throws Exception {
		Utils._assert(file.getFileId() == null);
		
		SQLiteDatabase db = getWriteableDatabase();
		try {
			
			db.beginTransaction();

			PayloadPeer.insertPayload(db, file);
			
			int fileId = getNextId(db, FileEntry.TABLE_NAME);
			
			file.setFileId(fileId);
			
			ContentValues cv = new ContentValues();
			cv.put(FileEntry._ID, fileId);
			cv.put(FileEntry.COLUMN_NAME_BINARY_DATA, file.getBinaryData());
			cv.put(FileEntry.COLUMN_NAME_FILE_NAME, file.getName());
			cv.put(FileEntry.COLUMN_NAME_MIME_TYPE, file.getMimeType());
			cv.put(FileEntry.COLUMN_NAME_PAYLOAD_ID, file.getPayloadId());
			db.insertOrThrow(FileEntry.TABLE_NAME, null, cv);
			
			db.setTransactionSuccessful();
			
			Log.v(TAG, "Successfully inserted File: " + file.toString());
		}
		catch (Exception ex) {
			throw ex;
		}
		finally {
			db.endTransaction();
			db.close();
			
		}
	}
	
	
	protected static List<FilePayload> retrieve(String selection, String[] selectionArgs) {
		SQLiteDatabase db = getReadableDatabase();
		ArrayList<FilePayload> files = new ArrayList<FilePayload>();
		Cursor cursor = null;
		try {
			db.beginTransaction();
			
			cursor = db.query(FileEntry.TABLE_NAME,
					new String[]{
						FileEntry._ID,
						FileEntry.COLUMN_NAME_FILE_NAME,
						FileEntry.COLUMN_NAME_MIME_TYPE,
						FileEntry.COLUMN_NAME_PAYLOAD_ID
					}, selection, selectionArgs, null, null, null);
			while (cursor.moveToNext()) {
				FilePayload file = new FilePayload();
				file.setFileId(cursor.getInt(cursor.getColumnIndexOrThrow(FileEntry._ID)));
				file.setMimeType(cursor.getString(cursor.getColumnIndexOrThrow(FileEntry.COLUMN_NAME_MIME_TYPE)));
				file.setName(cursor.getString(cursor.getColumnIndexOrThrow(FileEntry.COLUMN_NAME_FILE_NAME)));
				file.setPayloadId(cursor.getInt(cursor.getColumnIndexOrThrow(FileEntry.COLUMN_NAME_PAYLOAD_ID)));
				
				PayloadPeer.retrieve(db, file);
				
				files.add(file);
			}
		}
		catch (Exception ex) {
			Log.v(TAG, "Error in retrieve()", ex);
		}
		finally {
			if(cursor != null) cursor.close();
			db.endTransaction();
			db.close();
		}
		
		
		return files;
	}


	public static byte[] getBinaryData(FilePayload filePayload) {
		Utils._assert(filePayload.getFileId() != null);
		
		SQLiteDatabase db = getReadableDatabase();
		byte[] bytes = null;
		try {
			Cursor cursor = db.query(FileEntry.TABLE_NAME, new String[]{FileEntry.COLUMN_NAME_BINARY_DATA}, FileEntry._ID + "=?", new String[]{filePayload.getFileId().toString()}, null, null, null);
			
			if (cursor.moveToNext()) {
				bytes = cursor.getBlob(0); 
			}
			else {
				Log.e(TAG, "Unable to retrieve binary data for " + filePayload.toString());
			}
			
			cursor.close();
		}
		catch (Exception ex) {
			Log.e(TAG, "Error in getBinaryData()", ex);
		}
		finally {
			db.close();
		}
		
		return bytes;
	}
	
	public static FilePayload getFilePayloadById(int id) {
		List<FilePayload> files = retrieve(FileEntry._ID + "=?", new String[]{Integer.toString(id)});
		
		Utils._assert(files.size() <= 1);
		
		if (files.isEmpty()) {
			return null;
		}
		
		return files.get(0);
	}
}


