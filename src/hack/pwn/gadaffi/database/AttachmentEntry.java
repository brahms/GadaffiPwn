package hack.pwn.gadaffi.database;

public interface AttachmentEntry extends BaseEntry {
	public static final String TABLE_NAME           = "attachments";
	public static final String COLUMN_NAME_EMAIL_ID = "email_id";
	public static final String COLUMN_NAME_MIME_TYPE= "mime_type";
	public static final String COLUMN_NAME_FILENAME = "filename";
	public static final String COLUMN_NAME_DATA     = "data";
	public static final String COLUMN_NAME_DATA_LENGTH = "data_length";
	
	public static final String SQL_CREATE_TABLE = 
			"CREATE TABLE " + TABLE_NAME + "(" + 
			_ID + INTEGER_TYPE + " PRIMARY KEY " + COMMA_SEP + 
			COLUMN_NAME_EMAIL_ID + INTEGER_TYPE + COMMA_SEP + 
			COLUMN_NAME_MIME_TYPE + TEXT_TYPE + COMMA_SEP + 
			COLUMN_NAME_FILENAME + TEXT_TYPE + COMMA_SEP + 
			COLUMN_NAME_DATA + BLOB_TYPE + COMMA_SEP + 
			COLUMN_NAME_DATA_LENGTH + INTEGER_TYPE + COMMA_SEP +
			FOREIGN_KEY + COLUMN_NAME_EMAIL_ID + REFERENCES + EmailEntry.TABLE_NAME + "(" + EmailEntry._ID + ")" +
			")";

	public static final String SQL_DROP_TABLE =
			"DROP TABLE IF EXISTS " + TABLE_NAME;
}
