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
	public static final int MAGIC_VALUE = 0x1337;
}	
