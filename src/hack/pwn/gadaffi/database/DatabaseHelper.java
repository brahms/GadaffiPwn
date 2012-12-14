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
		resetDb(db);
		createTables(db);
	}
	
	public void createTables(SQLiteDatabase db) {

		db.execSQL(EmailEntry.SQL_CREATE_TABLE);
		db.execSQL(AttachmentEntry.SQL_CREATE_TABLE);
		db.execSQL(InboundPacketEntry.SQL_CREATE_TABLE);
		db.execSQL(InboundPartEntry.SQL_CREATE_TABLE);
		db.execSQL(OutboundMmsEntry.SQL_CREATE_TABLE);
	}
	
	public void resetDb(SQLiteDatabase db) {
		db.execSQL(AttachmentEntry.SQL_DROP_TABLE);
		db.execSQL(EmailEntry.SQL_DROP_TABLE);
		db.execSQL(InboundPartEntry.SQL_DROP_TABLE);
		db.execSQL(InboundPacketEntry.SQL_DROP_TABLE);
		db.execSQL(OutboundMmsEntry.SQL_DROP_TABLE);
		
		createTables(db);
	}
	

}
