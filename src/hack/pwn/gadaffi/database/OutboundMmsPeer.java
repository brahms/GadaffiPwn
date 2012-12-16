package hack.pwn.gadaffi.database;

import hack.pwn.gadaffi.Constants;
import hack.pwn.gadaffi.Utils;
import hack.pwn.gadaffi.providers.MmsProvider;
import hack.pwn.gadaffi.steganography.AStegoImage;
import hack.pwn.gadaffi.steganography.OutboundMms;
import hack.pwn.gadaffi.steganography.Packet;
import hack.pwn.gadaffi.steganography.Part;
import hack.pwn.gadaffi.steganography.PngStegoImage;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.MimeTypeMap;


public class OutboundMmsPeer extends BasePeer{
	private static final String TAG = "database.OutboundMmsPeer";
	public static List<OutboundMms> insertPngStegoImage(Packet p, String to, List<Bitmap> images) throws Exception {
		SQLiteDatabase db = getWriteableDatabase();
		Cursor cursor = null;
		List<OutboundMms> mmsList = new ArrayList<OutboundMms>();
		Utils._assert(images.size() == p.getParts().size());
		
		try {
			db.beginTransaction();
			String sql = 
					String.format("SELECT MAX(%s) " +
					"FROM %s " +
					"WHERE %s=? ",
					OutboundMmsEntry.COLUMN_NAME_SEQUENCE_NUMBER, 
					OutboundMmsEntry.TABLE_NAME,
					OutboundMmsEntry.COLUMN_NAME_TO);
			Log.v(TAG, "Querying for sequence number: " + sql);
			cursor = db.rawQuery(
					sql, 
					new String[]{to});
			Byte seqNumber = 0;
			if(cursor.moveToFirst()) {
				seqNumber = (byte) ((cursor.getInt(0) + 1) % 255);
			}
			Log.v(TAG, "Using sequence number " + seqNumber.toString());
			
			p.setSequenceNumber(seqNumber);
			int i = 0;
			for(Part part : p.getParts().values()) {
				PngStegoImage image = new PngStegoImage();
				
				Log.v(TAG, "Encoding part " + part);
				image.setEmbeddedData(part.encode());
				image.setImageBitmap(images.get(i++));
				
				Log.v(TAG, "Encoding image.");
				image.encode();
				
				
				OutboundMms mms = new OutboundMms();
				mms.setMimeType(MimeTypeMap.getSingleton().getMimeTypeFromExtension(Constants.EXTENSION_PNG));
				
				mms.setTo(to);
				mms.setTimeSent(null);
				mms.setTimeQueued(Utils.getNow());
				mms.setPartNumber(part.getPartNumber());
				mms.setSequenceNumber(part.getSequenceNumber());
				insertOutboundMms(db, mms, image);
				
				mmsList.add(mms);
				
				Log.v(TAG, "Success inserting part/png");
				
			}
			
			db.setTransactionSuccessful();
		}
		catch(Exception ex) {
			Log.e(TAG, "Unable to insert OutboundMms PngStegoImage.");
			throw ex;
		}
		finally {

			db.endTransaction();
			if (cursor != null) cursor.close();
			db.close();
		}
		
		return mmsList;
	}
	@SuppressLint("WorldReadableFiles")
	public static void insertOutboundMms(SQLiteDatabase db, OutboundMms mms, AStegoImage image) throws IOException {
		Utils._assert(mms.getId() == null);
		
		int id = 1;
		
		Cursor c = db.rawQuery(String.format("SELECT MAX(%s) FROM %s", OutboundMmsEntry._ID, OutboundMmsEntry.TABLE_NAME), 
						null);
		
		if(c.moveToFirst()) {
			id = c.getInt(0) + 1;
		}
		c.close();
		
		mms.setId(id);
		mms.setImageFilename(
			OutboundMms.generateImageFile(
				id, MimeTypeMap.getSingleton().getExtensionFromMimeType(
					mms.getMimeType())));
		
		Log.v(TAG, "Inserting outbound mms " + mms);
		
		ContentValues cv = new ContentValues();
		
		cv.put(OutboundMmsEntry._ID, id);
		cv.put(OutboundMmsEntry.COLUMN_IMAGE_FILE_NAME, mms.getImageFilename());
		cv.put(OutboundMmsEntry.COLUMN_IMAGE_TYPE, mms.getMimeType());
		cv.put(OutboundMmsEntry.COLUMN_NAME_PART_NUMBER,mms.getPartNumber());
		cv.put(OutboundMmsEntry.COLUMN_NAME_SEQUENCE_NUMBER, mms.getSequenceNumber());
		cv.put(OutboundMmsEntry.COLUMN_NAME_DATE_ADDED, mms.getTimeQueued().toMillis(false));
		cv.put(OutboundMmsEntry.COLUMN_TIME_SENT, mms.getTo());
		cv.put(OutboundMmsEntry.COLUMN_NAME_HEIGHT, image.getImageBitmap().getHeight());
        cv.put(OutboundMmsEntry.COLUMN_NAME_WIDTH, image.getImageBitmap().getWidth());
		cv.put(OutboundMmsEntry.COLUMN_NAME_SIZE, image.getImageBytes().length);
		cv.put(OutboundMmsEntry.COLUMN_NAME_DATA_STREAM, MmsProvider.URI_SINGLE_MMS_STREAM + id);
		
		db.insertOrThrow(OutboundMmsEntry.TABLE_NAME, null, cv);

		OutputStream out = getContext().openFileOutput(mms.getImageFilename(), Context.MODE_WORLD_READABLE);
		Log.v(TAG, String.format("Writing %d bytes to file: %s", image.getImageBytes().length, mms.getImageFilename()));
		out.write(image.getImageBytes());
		out.flush();
		out.close();
		Log.v(TAG, "Success inserting mms.");
	}
	public static OutboundMms getOutboundMmsById(int id) throws Exception {
		List<OutboundMms> mmses = retrieve(OutboundMmsEntry._ID + "=?", new String[]{Integer.toString(id)});
		return mmses.isEmpty() ? null : mmses.get(0);
	}
	
	private static List<OutboundMms> retrieve(String selection, String[] selectionArgs) {
		List<OutboundMms> mmses = new ArrayList<OutboundMms>();
		SQLiteDatabase db = getReadableDatabase();
		Cursor cursor = null;
		try {
			cursor = db.query(OutboundMmsEntry.TABLE_NAME,
					new String[]{
						OutboundMmsEntry._ID,
						OutboundMmsEntry.COLUMN_IMAGE_FILE_NAME,
						OutboundMmsEntry.COLUMN_IMAGE_TYPE,
						OutboundMmsEntry.COLUMN_NAME_PART_NUMBER,
						OutboundMmsEntry.COLUMN_NAME_SEQUENCE_NUMBER,
						OutboundMmsEntry.COLUMN_NAME_TO,
						OutboundMmsEntry.COLUMN_NAME_DATE_ADDED,
						OutboundMmsEntry.COLUMN_TIME_SENT
					},
					selection,
					selectionArgs, 
					null, 
					null, 
					OutboundMmsEntry._ID + " ASC");
			
			while(cursor.moveToNext()) {
				OutboundMms mms = new OutboundMms();
				mms.setId(cursor.getInt(cursor.getColumnIndexOrThrow(OutboundMmsEntry._ID)));
				mms.setImageFilename(cursor.getString(cursor.getColumnIndexOrThrow(OutboundMmsEntry.COLUMN_IMAGE_FILE_NAME)));
				mms.setPartNumber(cursor.getInt(cursor.getColumnIndexOrThrow(OutboundMmsEntry.COLUMN_NAME_PART_NUMBER)));
				mms.setSequenceNumber((byte) cursor.getInt(cursor.getColumnIndexOrThrow(OutboundMmsEntry.COLUMN_NAME_SEQUENCE_NUMBER)));
				mms.setTo(cursor.getString(cursor.getColumnIndexOrThrow(OutboundMmsEntry.COLUMN_NAME_TO)));
				mms.setTimeQueued(cursor.getLong(cursor.getColumnIndexOrThrow(OutboundMmsEntry.COLUMN_NAME_DATE_ADDED)));
				mms.setTimeSent(cursor.getLong(cursor.getColumnIndexOrThrow(OutboundMmsEntry.COLUMN_TIME_SENT)));
				mms.setMimeType(cursor.getString(cursor.getColumnIndexOrThrow(OutboundMmsEntry.COLUMN_IMAGE_TYPE)));
				mmses.add(mms);
			}
		
		}
		catch(Exception ex) {
			Log.e(TAG, String.format("Error in retrieve()"), ex);
		}
		finally {
			if(cursor != null) cursor.close();
			db.close();
		}	
		
		Log.v(TAG, String.format("Retrieved %d outbound mms.", mmses.size()));
		return mmses;
	}
	public static void markMmsSent(List<OutboundMms> result) {
		SQLiteDatabase db = getWriteableDatabase();
		try {
			db.beginTransaction();
			List<Integer> ids = new ArrayList<Integer>(result.size());
			for(OutboundMms mms : result) {
				ids.add(mms.getId());
			}
			
			db.rawQuery(String.format(OutboundMmsEntry.SQL_UPDATE_IS_SENT, TextUtils.join(", ",ids.toArray())), null);
			
			db.setTransactionSuccessful();
		}
		catch (Exception ex) {
			Log.e(TAG, "Error marking mmses sent.",ex);
		}
		finally {
			db.endTransaction();
			db.close();
		}
		
	}
	
	public static Cursor getProviderCursor(SQLiteDatabase db, String id, String[] columns) {
	    Cursor cursor = db.query(OutboundMmsEntry.TABLE_NAME, columns, OutboundMmsEntry._ID + "=?", new String[]{id}, null, null, null);
	    Log.v(TAG, "Cursor has " + cursor.getCount() + " rows ");
	    return cursor;
	}



}
