package hack.pwn.gadaffi.database;


public abstract class PayloadEntry implements BaseEntry{
	public static final String TABLE_NAME = "payload";
	public static final String COLUMN_NAME_TO = "to_number";
	public static final String COLUMN_NAME_FROM = "from_number";
	public static final String COLUMN_NAME_TIME_RECEIVED = "time_received";

	
	public static final String SQL_CREATE_TABLE =
			CREATE_TABLE + TABLE_NAME + " (" +
			_ID + INTEGER_TYPE + " PRIMARY KEY" + COMMA_SEP +
			COLUMN_NAME_TO + TEXT_TYPE + COMMA_SEP +
			COLUMN_NAME_FROM + TEXT_TYPE + COMMA_SEP +
			COLUMN_NAME_TIME_RECEIVED + TIME_TYPE +
			")";
	
	public static final String SQL_DROP_TABLE =
			"DROP TABLE IF EXISTS " + TABLE_NAME;
}
