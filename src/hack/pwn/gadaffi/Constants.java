package hack.pwn.gadaffi;

import hack.pwn.gadaffi.database.BaseEntry;

import java.nio.ByteOrder;
import java.nio.charset.Charset;

public interface Constants {

	public static final String KEY_RECEIVER = "receiver";
	public static final String KEY_USE_ENCRYPTION = "encrypted";
	public static final String KEY_DATA = "data";
	public static final String PREFS_FILE = "MyPrefsFile";
	public static final String KEY_MMS_COUNT = "mms_count";
	public static final String KEY_MMS_ID = "mms_id";
	public static final String KEY_PART_ID = "part_id";
	public static final String KEY_TYPE = "type";
	public static final String KEY_ID = "_id";
	public static final ByteOrder BYTE_ORDER = ByteOrder.BIG_ENDIAN;
	public static final int MAGIC_VALUE = 0xDEADBEEF;
	public static final int MAGIC_VALUE_LENGTH = 4; // bytes
	public static final int LENGTH_VALUE_LENGTH = 2; //bytes
	public static final int STEGO_HEADER_LENGTH = MAGIC_VALUE_LENGTH + LENGTH_VALUE_LENGTH;
	
	public static final int FIRST_BIT =  0x1 << 0;
	public static final int SECOND_BIT = 0x1 << 1;
	public static final int THIRD_BIT = 0x1 << 2;
	public static final int FOURTH_BIT = 0x1 << 3;
	public static final int FIFTH_BIT = 0x01 << 4;
	public static final int SIXTH_BIT = 0x01 << 5;
	public static final int SEVENTH_BIT = 0x01 << 6;
	public static final int EIGTH_BIT = 0x01 << 7;
	public static final int PIXEL_BLACK_WITH_ALPHA = 0xFF000000;
	public static final String ACTION_NEW_PACKET = "gadaffi.pwn.intent.action.new_packet";
	public static final int PART_HEADER_LENGTH = 3;
	public static final int PART_CHECKSUM_LENGTH = 4;
	public static final int PART_HEADER_PLUS_CHECKSUM_LENGTH = PART_HEADER_LENGTH + PART_CHECKSUM_LENGTH;
	public static final Charset CHARSET = Charset.forName("UTF-8");
	public static final String MIME_TYPE_OCTET_STREAM = "application/octet-stream";
	public static final int PACKET_HEADER_LENGTH = 2;
	public static final boolean LOG_SQL_QUERIES = true;
	public static final String DB_FILE_NAME = null;
	public static final int DATABASE_VERSION = 2;
	public static final String DATABASE_NAME = "gadaffipwn.db";
	public static final String EXTENSION_PNG = "png";
	public static final String ORDER_BY_ID_DESC = BaseEntry._ID + " DESC";
	public static final String ORDER_BY_ID_ASC  = BaseEntry._ID + " ASC";
	public static final String ACTION_NEW_EMAIL = "hack.pwn.gadaffi.actions.new_email";
	public static final String KEY_STATE = "state";
	
}	
