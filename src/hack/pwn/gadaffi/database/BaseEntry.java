package hack.pwn.gadaffi.database;

import android.provider.BaseColumns;

public interface BaseEntry extends BaseColumns{
	public static final String TEXT_TYPE = " TEXT";
	public static final String BLOB_TYPE = " BLOB";
	public static final String INTEGER_TYPE = " INTEGER";
	public static final String COMMA_SEP = ",";
	public static final String FOREIGN_KEY = "FOREIGN KEY(";
	public static final String REFERENCES  = ") REFERENCES ";
	public static final String NOT_NULL = " NOT NULL";
}
