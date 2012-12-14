package hack.pwn.gadaffi.database;

import android.provider.MediaStore;

public class OutboundMmsEntry implements BaseEntry{
	public static final String TABLE_NAME = "outbound_mms";
	public static final String COLUMN_NAME_TO  = "to_number";
	public static final String COLUMN_TIME_SENT = "time_sent";
	public static final String COLUMN_NAME_DATE_ADDED = MediaStore.MediaColumns.DATE_ADDED;
	public static final String COLUMN_IMAGE_TYPE  = MediaStore.MediaColumns.MIME_TYPE;
	public static final String COLUMN_IMAGE_FILE_NAME  = MediaStore.MediaColumns.DISPLAY_NAME;
	public static final String COLUMN_NAME_SEQUENCE_NUMBER = "sequence_number";
	public static final String COLUMN_NAME_PART_NUMBER = "part_number";
	public static final String COLUMN_NAME_IS_SENT = "is_sent";
	public static final String COLUMN_NAME_DATA_STREAM = MediaStore.MediaColumns.DATA;
	public static final String COLUMN_NAME_SIZE = MediaStore.MediaColumns.SIZE;
	public static final String COLUMN_NAME_HEIGHT = "height";
	public static final String COLUMN_NAME_WIDTH  = "width";
	
	public static final String SQL_CREATE_TABLE = 
			CREATE_TABLE + TABLE_NAME + "(" +
			_ID + INTEGER_TYPE + " PRIMARY KEY" + COMMA_SEP +
			COLUMN_TIME_SENT + TIME_TYPE + COMMA_SEP +
			COLUMN_NAME_DATE_ADDED + TIME_TYPE + COMMA_SEP +
			COLUMN_IMAGE_TYPE + TEXT_TYPE + COMMA_SEP  +
			COLUMN_IMAGE_FILE_NAME + TEXT_TYPE + COMMA_SEP +
			COLUMN_NAME_SEQUENCE_NUMBER + INTEGER_TYPE + COMMA_SEP +
			COLUMN_NAME_TO + TEXT_TYPE + COMMA_SEP +
			COLUMN_NAME_PART_NUMBER + INTEGER_TYPE + COMMA_SEP +
			COLUMN_NAME_IS_SENT + INTEGER_TYPE + " DEFAULT 0" + COMMA_SEP +
			COLUMN_NAME_WIDTH + INTEGER_TYPE + COMMA_SEP +
			COLUMN_NAME_HEIGHT + INTEGER_TYPE + COMMA_SEP +
			COLUMN_NAME_DATA_STREAM + TEXT_TYPE + COMMA_SEP +
			COLUMN_NAME_SIZE + INTEGER_TYPE +
			")";
	

	public static final String SQL_DROP_TABLE =
			"DROP TABLE IF EXISTS " + TABLE_NAME;
					
	public static final String SQL_UPDATE_IS_SENT =
			"UPDATE " + OutboundMmsEntry.TABLE_NAME + 
			" SET " + OutboundMmsEntry.COLUMN_NAME_IS_SENT + "=1 " + 
			" WHERE " + OutboundMmsEntry._ID + " IN (%s)";
}
