package hack.pwn.gadaffi.database;

import hack.pwn.gadaffi.Utils;
import hack.pwn.gadaffi.steganography.Text;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;


public class TextPeer extends BasePeer {
	private static final String TAG = "database.TextPeer";
	public static void insertText(Text text) throws Exception {
		Log.v(TAG, "Entered insertText()");
		
		Utils._assert(text.getTextId() == null);
		Utils._assert(text.getText() != null);
		
		SQLiteDatabase db = getWriteableDatabase();
		
		try {
			db.beginTransaction();
			
			PayloadPeer.insertPayload(db, text);
			
			int id = getNextId(db, TextEntry.TABLE_NAME);
			
			text.setTextId(id);
			
			ContentValues cv = new ContentValues();
			cv.put(TextEntry._ID, id);
			cv.put(TextEntry.COLUMN_NAME_TEXT, text.getText());
			cv.put(TextEntry.COLUMN_NAME_PAYLOAD_ID, text.getPayloadId());
			
			db.insert(TextEntry.TABLE_NAME, null, cv);
			
			db.setTransactionSuccessful();
			Log.v(TAG, "Succesfully inserted Text: " + text.toString());
		}
		catch (Exception ex) {
			Log.v(TAG, "Error inserting text: " +text.toString(), ex);
			throw ex;
		}
		finally {
			db.endTransaction();
			db.close();
		}
	}
	
	protected static List<Text> retrieve(String selection, String[] selectionArgs) {
		SQLiteDatabase db = getReadableDatabase();
		List<Text> texts = new ArrayList<Text>();
		
		Log.v(TAG, "Entered retrieve()");
		try {
			db.beginTransaction();
			
			Cursor c = db.query(TextEntry.TABLE_NAME, 
					new String[]{
						TextEntry._ID, 
						TextEntry.COLUMN_NAME_PAYLOAD_ID,
						TextEntry.COLUMN_NAME_TEXT
					}, selection, selectionArgs, null, null, null);
			
			while(c.moveToNext()) {
				Text text = new Text();
				text.setTextId(c.getInt(c.getColumnIndexOrThrow(TextEntry._ID)));
				text.setPayloadId(c.getInt(c.getColumnIndexOrThrow(TextEntry.COLUMN_NAME_PAYLOAD_ID)));
				text.setText(c.getString(c.getColumnIndexOrThrow(TextEntry.COLUMN_NAME_TEXT)));
				
				PayloadPeer.retrieve(db, text);
				
				texts.add(text);
			}
			
		}
		catch (Exception ex) {
			Log.e(TAG, "Error in retrieve()", ex);
		}
		finally {
			db.endTransaction();
			db.close();
		}
		
		Log.v(TAG, String.format("Returning %d results.", texts.size()));
		return texts;
	}
	
	public static Text getTextById(int id) {
		List<Text> texts = retrieve(TextEntry._ID + "= ?", new String[]{Integer.toString(id)});
		
		Utils._assert(texts.size() <= 1);
		
		if(texts.isEmpty()) {
			return null;
		}
		
		return texts.get(0);
	}
}
