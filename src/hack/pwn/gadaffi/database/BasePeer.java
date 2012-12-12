package hack.pwn.gadaffi.database;

import hack.pwn.gadaffi.Constants;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class BasePeer {
	@SuppressWarnings("unused")
	private static final String TAG = "database.BasePeer";
	
	public static DatabaseHelper databaseHelper;
	public static Context context;
	
	public static synchronized void init(Context context) {
		BasePeer.context = context;

		if(databaseHelper == null) 
			databaseHelper = new DatabaseHelper(context, Constants.DATABASE_NAME);
	}
	
	public static SQLiteDatabase getWriteableDatabase() {
		return databaseHelper.getWritableDatabase();
	}
	
	public static SQLiteDatabase getReadableDatabase() {
		return databaseHelper.getReadableDatabase();
	}
	
	public static Integer getNextId(SQLiteDatabase db, String table) throws Exception {
		Cursor c = db.rawQuery(BaseEntry.SQL_GET_NEXT_ID + table, null);
		if(c.moveToFirst())
			return c.getInt(0) + 1;
		else 
			throw new Exception("SQL didn't work, 0 results.");
	}

	public static Context getContext() {
		return context;
	}
	
	public static void initForTest(Context context) {
		BasePeer.context = context;

		if(databaseHelper == null) 
			databaseHelper = new DatabaseHelper(context, Constants.DATABASE_NAME);
		SQLiteDatabase db = getWriteableDatabase();
		databaseHelper.resetDb(db);
		db.close();
	}
	
}
