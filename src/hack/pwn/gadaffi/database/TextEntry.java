package hack.pwn.gadaffi.database;

public class TextEntry implements BaseEntry {
	public static final String TABLE_NAME = "texts";
	public static final String COLUMN_NAME_PAYLOAD_ID = "payload_id";
	public static final String COLUMN_NAME_TEXT = "text";
	
	public static final String SQL_CREATE_TABLE = 
			CREATE_TABLE + TABLE_NAME + "(" +
			_ID + INTEGER_TYPE + " PRIMARY KEY" + COMMA_SEP +
			COLUMN_NAME_PAYLOAD_ID + INTEGER_TYPE + COMMA_SEP +
			COLUMN_NAME_TEXT + TEXT_TYPE + 
			")";
	

	public static final String SQL_DROP_TABLE =
			"DROP TABLE IF EXISTS " + TABLE_NAME;
}
