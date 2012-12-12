package hack.pwn.gadaffi.database;

import hack.pwn.gadaffi.Constants;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
	public DatabaseHelper(Context context, String name) {
		super(context, name, new CursorFactoryImpl(), Constants.DATABASE_VERSION);
	}

	
	
	/* (non-Javadoc)
	 * @see android.database.sqlite.SQLiteOpenHelper#onOpen(android.database.sqlite.SQLiteDatabase)
	 */
	@Override
	public void onOpen(SQLiteDatabase db) {
		super.onOpen(db);
		if(!db.isReadOnly()) {
			db.execSQL("PRAGMA foreign_keys=ON");
		}
	}



	@Override
	public void onCreate(SQLiteDatabase db) {
		createTables(db);

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}
	
	public void createTables(SQLiteDatabase db) {

		db.execSQL(PayloadEntry.SQL_CREATE_TABLE);
		db.execSQL(FileEntry.SQL_CREATE_TABLE);
		db.execSQL(TextEntry.SQL_CREATE_TABLE);
		db.execSQL(InboundPacketEntry.SQL_CREATE_TABLE);
		db.execSQL(InboundPartEntry.SQL_CREATE_TABLE);
		db.execSQL(OutboundMmsEntry.SQL_CREATE_TABLE);
	}
	
	public void resetDb(SQLiteDatabase db) {
		db.execSQL(PayloadEntry.SQL_DROP_TABLE);
		db.execSQL(FileEntry.SQL_DROP_TABLE);
		db.execSQL(TextEntry.SQL_DROP_TABLE);
		db.execSQL(InboundPacketEntry.SQL_DROP_TABLE);
		db.execSQL(InboundPartEntry.SQL_DROP_TABLE);
		db.execSQL(OutboundMmsEntry.SQL_DROP_TABLE);
		
		createTables(db);
	}
	

}
