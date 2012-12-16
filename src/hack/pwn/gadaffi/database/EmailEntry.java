package hack.pwn.gadaffi.database;

public interface EmailEntry extends BaseEntry {
	public static final String COLUMN_NAME_SUBJECT  = "subject";
	public static final String COLUMN_NAME_MESSAGE  = "message";
	public static final String COLUMN_NAME_FROM = "from_number";
	public static final String COLUMN_NAME_TO = "to_number";
	public static final String COLUMN_NAME_TIME_RECEIVED = "time_received";
	public static final String TABLE_NAME = "emails";
	
	public static final String SQL_CREATE_TABLE = 
			"CREATE TABLE " + TABLE_NAME + "(" +
			_ID + INTEGER_TYPE + " PRIMARY KEY" + COMMA_SEP +
			COLUMN_NAME_SUBJECT  + TEXT_TYPE + NOT_NULL + COMMA_SEP +
			COLUMN_NAME_MESSAGE  + TEXT_TYPE + NOT_NULL + COMMA_SEP +
			COLUMN_NAME_FROM + TEXT_TYPE + COMMA_SEP +
			COLUMN_NAME_TO   + TEXT_TYPE + COMMA_SEP +
			COLUMN_NAME_TIME_RECEIVED + TIME_TYPE + NOT_NULL +
			")";

	public static final String SQL_DROP_TABLE =
			"DROP TABLE IF EXISTS " + TABLE_NAME;
    public static final String SQL_DELETE = 
            "DELETE FROM " + TABLE_NAME + " WHERE ";
}
