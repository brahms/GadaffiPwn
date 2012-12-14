package hack.pwn.gadaffi.database;

public class OutboundMmsEntry implements BaseEntry{
	public static final String TABLE_NAME = "outbound_mms";
	public static final String COLUMN_NAME_TO  = "to_number";
	public static final String COLUMN_TIME_SENT = "time_sent";
	public static final String COLUMN_TIME_QUEUED = "time_queued";
	public static final String COLUMN_IMAGE_TYPE  = "image_type";
	public static final String COLUMN_IMAGE_FILE_NAME  = "image_filename";
	public static final String COLUMN_NAME_SEQUENCE_NUMBER = "sequence_number";
	public static final String COLUMN_NAME_PART_NUMBER = "part_number";
	public static final String COLUMN_NAME_IS_SENT = "is_sent";
	
	public static final String SQL_CREATE_TABLE = 
			CREATE_TABLE + TABLE_NAME + "(" +
			_ID + INTEGER_TYPE + " PRIMARY KEY" + COMMA_SEP +
			COLUMN_TIME_SENT + TIME_TYPE + COMMA_SEP +
			COLUMN_TIME_QUEUED + TIME_TYPE + COMMA_SEP +
			COLUMN_IMAGE_TYPE + TEXT_TYPE + COMMA_SEP  +
			COLUMN_IMAGE_FILE_NAME + TEXT_TYPE + COMMA_SEP +
			COLUMN_NAME_SEQUENCE_NUMBER + INTEGER_TYPE + COMMA_SEP +
			COLUMN_NAME_TO + TEXT_TYPE + COMMA_SEP +
			COLUMN_NAME_PART_NUMBER + INTEGER_TYPE + COMMA_SEP +
			COLUMN_NAME_IS_SENT + INTEGER_TYPE + " DEFAULT 0" +
			")";
	

	public static final String SQL_DROP_TABLE =
			"DROP TABLE IF EXISTS " + TABLE_NAME;
					
	public static final String SQL_UPDATE_IS_SENT =
			"UPDATE " + OutboundMmsEntry.TABLE_NAME + 
			" SET " + OutboundMmsEntry.COLUMN_NAME_IS_SENT + "=1 " + 
			" WHERE " + OutboundMmsEntry._ID + " IN (%s)";
}
