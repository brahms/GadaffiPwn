package hack.pwn.gadaffi.database;

public class FileEntry implements BaseEntry {
	public static final String TABLE_NAME = "files";
	public static final String COLUMN_NAME_PAYLOAD_ID = "payload_id";
	public static final String COLUMN_NAME_MIME_TYPE = "mime_type";
	public static final String COLUMN_NAME_FILE_NAME = "file_name";
	public static final String COLUMN_NAME_BINARY_DATA = "binary_data";
	
	public static final String SQL_CREATE_TABLE = 
			CREATE_TABLE + TABLE_NAME + "(" +
			_ID + INTEGER_TYPE + " PRIMARY KEY" + COMMA_SEP +
			COLUMN_NAME_PAYLOAD_ID + INTEGER_TYPE + COMMA_SEP +
			COLUMN_NAME_MIME_TYPE + TEXT_TYPE + COMMA_SEP +
			COLUMN_NAME_FILE_NAME + TEXT_TYPE + COMMA_SEP +
			COLUMN_NAME_BINARY_DATA + BLOB_TYPE + 
			")";
	

	public static final String SQL_DROP_TABLE =
			"DROP TABLE IF EXISTS " + TABLE_NAME;
}
