package hack.pwn.gadaffi.providers;


import hack.pwn.gadaffi.database.BasePeer;
import hack.pwn.gadaffi.database.OutboundMmsPeer;
import hack.pwn.gadaffi.steganography.OutboundMms;

import java.io.File;
import java.io.FileNotFoundException;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import android.webkit.MimeTypeMap;

public class MmsProvider extends ContentProvider {

	private static final UriMatcher mUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
	public static final String PROVIDER_NAME = "hack.pwn.gadaffi.provider.Mms";
	private static final int CODE_SINGLE_MMS = 1;
	private static final String TAG = "providers.MmsProvider";
	public static final String URI_SINGLE_MMS = "content://" + PROVIDER_NAME + "/mms/";
	static {
		mUriMatcher.addURI(PROVIDER_NAME, "mms/#", CODE_SINGLE_MMS);
	}
	SQLiteDatabase mDb = null;
	
	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		Log.v(TAG, "Entered delete()");
		throw new RuntimeException("Operation not supported.");
		
	}

	@Override
	public String getType(Uri uri) {
		Log.v(TAG, "Entered getType()");
		throw new RuntimeException("Operation not supported.");
	}

	/* (non-Javadoc)
	 * @see android.content.ContentProvider#getStreamTypes(android.net.Uri, java.lang.String)
	 */
	@Override
	public String[] getStreamTypes(Uri uri, String mimeTypeFilter) {
		Log.v(TAG, String.format("Entered getStreamTypes() for uri=%s, filter=%s", uri.toString(), mimeTypeFilter));
		switch(mUriMatcher.match(uri)) {
		case CODE_SINGLE_MMS:
			Log.v(TAG, String.format("Uri matches to CODE_SINGLE_MMS"));
			return new String[]{MimeTypeMap.getSingleton().getMimeTypeFromExtension("png")};
		}
		Log.v(TAG, String.format("Uri did not match anything."));
		return null;
	}
	
	/* (non-Javadoc)
	 * @see android.content.ContentProvider#openFile(android.net.Uri, java.lang.String)
	 */
	@Override
	public ParcelFileDescriptor openFile(Uri uri, String mode)
			throws FileNotFoundException {
		Log.v(TAG, String.format("Entered open() uri=%s, mode=%s",uri.toString(), mode));
		
		switch(mUriMatcher.match(uri)) {
		case CODE_SINGLE_MMS:
			try {
				int id = Integer.parseInt(uri.getLastPathSegment());
				Log.v(TAG, String.format("Uri matches CODE_SINGLE_MMS with an id of %d", id));
				OutboundMms mms = OutboundMmsPeer.getOutboundMmsById(id);
				if(mms != null) {

					File file = mms.getFile(getContext());

					Log.v(TAG, String.format("Returning file ", file.toString()));
					return ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY);
				}
			}
			catch (Exception e) {
				Log.e(TAG, "Error in openFile for CODE_SINGLE_MMS.", e);
			}
			
			break;
		}
		Log.v(TAG, "throwing FileNotFoundException()");
		throw new FileNotFoundException();
	}

	@Override
	public boolean onCreate() {
		Log.v(TAG, String.format("Entered onCreate()"));
		BasePeer.init(getContext());
		mDb = BasePeer.getReadableDatabase();
		return true;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		Log.v(TAG, String.format("Entered query()"));
		Log.v(TAG, String.format(
				"URI: %s\n" +
				"Project:%s\n" +
				"Select:%s\n" +
				"SelectionArgs: %s" +
				"sortOrder %s",uri,projection,selection,selectionArgs,sortOrder));
	    
		return null;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {

		Log.v(TAG, String.format("Entered update()"));
	    throw new RuntimeException("Operation not supported");
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {

		Log.v(TAG, String.format("Entered insert()"));
	    throw new RuntimeException("Operation not supported");
	}

}
