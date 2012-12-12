package hack.pwn.gadaffi.database;

import android.provider.BaseColumns;

public interface BaseEntry extends BaseColumns{
	public static final String CREATE_TABLE = "CREATE TABLE ";
	public static final String TEXT_TYPE = " TEXT";
	public static final String BLOB_TYPE = " BLOB";
	public static final String INTEGER_TYPE = " INTEGER";
	public static final String TIME_TYPE    = INTEGER_TYPE;
	public static final String COMMA_SEP = ", ";
	public static final String FOREIGN_KEY = "FOREIGN KEY(";
	public static final String REFERENCES  = ") REFERENCES ";
	public static final String NOT_NULL = " NOT NULL";
	public static final String SQL_GET_NEXT_ID = "SELECT MAX(_ID) FROM ";
	
}
