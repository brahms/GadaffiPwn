package hack.pwn.gadaffi.database;

public class InboundPartEntry implements BaseEntry {
	public static final String TABLE_NAME = "inbound_parts";
	public static final String COLUMN_NAME_PACKET_ID = "packet_id";
	public static final String COLUMN_NAME_PART_NUMBER = "part_number";
	public static final String COLUMN_NAME_FLAGS = "flags";
	public static final String COLUMN_NAME_PART_BLOB = "part_blob";
	public static final String COLUMN_NAME_TIME_RECEIVED  = "time_received";
	
	
	public static final String SQL_CREATE_TABLE = 
			CREATE_TABLE + TABLE_NAME + "(" +
			_ID + INTEGER_TYPE + " PRIMARY KEY" + COMMA_SEP +
			COLUMN_NAME_PACKET_ID + INTEGER_TYPE + NOT_NULL +  COMMA_SEP + 
			COLUMN_NAME_PART_NUMBER + INTEGER_TYPE +  NOT_NULL + COMMA_SEP +
			COLUMN_NAME_FLAGS + INTEGER_TYPE + NOT_NULL +  COMMA_SEP +
			COLUMN_NAME_PART_BLOB + BLOB_TYPE +  NOT_NULL + COMMA_SEP +
			COLUMN_NAME_TIME_RECEIVED + INTEGER_TYPE +  NOT_NULL + COMMA_SEP +
			"UNIQUE (" + COLUMN_NAME_PACKET_ID + COMMA_SEP + COLUMN_NAME_PART_NUMBER + ")" + COMMA_SEP +
			FOREIGN_KEY + COLUMN_NAME_PACKET_ID + REFERENCES + InboundPacketEntry.TABLE_NAME + "(" + InboundPartEntry._ID + ")" +
			")";

	public static final String SQL_DROP_TABLE =
			"DROP TABLE IF EXISTS " + TABLE_NAME;
}
