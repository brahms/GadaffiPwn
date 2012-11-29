package hack.pwn.gadaffi.database;


public abstract class DataEntry implements BaseEntry{
	public static final String TABLE_NAME = "data";
	public static final String COLUMN_NAME_DATA = "data";
	public static final String COLUMN_NAME_MIME_TYPE = "mime_type";

	
	public static final String SQL_CREATE_TABLE =
			"CREATE TABLE " + TABLE_NAME + " (" +
			_ID + " INTEGER PRIMARY KEY, " +
			COLUMN_NAME_DATA + BLOB_TYPE + NOT_NULL + COMMA_SEP +
			COLUMN_NAME_MIME_TYPE + TEXT_TYPE + NOT_NULL +
			")";
	
	public static final String SQL_DROP_TABLE =
			"DROP TABLE IF EXISTS " + TABLE_NAME;
}
