package hack.pwn.gadaffi;

import java.nio.ByteOrder;

public interface Constants {

	public static final String KEY_RECEIVER = "receiver";
	public static final String KEY_USE_ENCRYPTION = "encrypted";
	public static final String KEY_DATA = "data";
	public static final String PREFS_FILE = "MyPrefsFile";
	public static final String KEY_MMS_COUNT = "mms_count";
	public static final String KEY_MMS_ID = "mms_id";
	public static final String KEY_PART_ID = "part_id";
	public static final ByteOrder BYTE_ORDER = ByteOrder.BIG_ENDIAN;
	public static final int MAGIC_VALUE = 0xDEADB33F;
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
}	
