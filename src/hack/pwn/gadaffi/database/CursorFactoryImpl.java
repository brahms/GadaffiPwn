package hack.pwn.gadaffi.database;

import hack.pwn.gadaffi.Constants;
import android.database.Cursor;
import android.database.sqlite.SQLiteCursor;
import android.database.sqlite.SQLiteCursorDriver;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteQuery;
import android.util.Log;

public class CursorFactoryImpl implements CursorFactory {
	private static final String TAG = "SQLDEBUG";
	@Override
	public Cursor newCursor(SQLiteDatabase db, SQLiteCursorDriver masterQuery,
			String editTable, SQLiteQuery query) {
		
		if(Constants.LOG_SQL_QUERIES) {
			Log.d(TAG, query.toString());
		}
		
		return new SQLiteCursor(db, masterQuery, editTable, query);
	}

}
