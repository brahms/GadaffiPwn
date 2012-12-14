package hack.pwn.gadaffi.database;

public class InboundPacketEntry implements BaseEntry{
	public static final String TABLE_NAME = "inbound_packets";
	public static final String COLUMN_NAME_SEQUENCE_NUMBER = "sequence_number";
	public static final String COLUMN_NAME_FLAGS = "flags";
	public static final String COLUMN_NAME_FROM = "from_number";
	
	public static final String SQL_CREATE_TABLE = 
			CREATE_TABLE + TABLE_NAME + "(" + 
			_ID + INTEGER_TYPE + " PRIMARY KEY" + COMMA_SEP + 
			COLUMN_NAME_SEQUENCE_NUMBER + INTEGER_TYPE +  NOT_NULL + COMMA_SEP +
			COLUMN_NAME_FLAGS + INTEGER_TYPE +  NOT_NULL + COMMA_SEP +
			COLUMN_NAME_FROM + TEXT_TYPE +  NOT_NULL + COMMA_SEP + 
			"UNIQUE (" + COLUMN_NAME_FROM + COMMA_SEP + COLUMN_NAME_SEQUENCE_NUMBER + ") " +
			")";
	

	public static final String SQL_DROP_TABLE =
			"DROP TABLE IF EXISTS " + TABLE_NAME;
}
