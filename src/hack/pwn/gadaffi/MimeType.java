package hack.pwn.gadaffi;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public enum MimeType {
	JPEG("image/jpeg", "image/jpg"),
	BITMAP("image/bmp"),
	PNG("image/png"),
	TEXT("text/plain"),
	GIF("image/gif"),
	UNKNOWN;
	private String[] strings;
	
	private static Map<String, MimeType> mMap = new HashMap<String, MimeType>();
	MimeType(String... params) {
		strings = params;
	}
	public String[] getStrings() {
		return strings;
	}
	
	public static MimeType get(String string) {
		MimeType type = mMap.get(string);
		
		if(type == null) {
			return  UNKNOWN;
		}
		
		return type;
	}
	
	//
	// Init our hash map
	// 
	static {
		EnumSet<MimeType> set = EnumSet.allOf(MimeType.class);
		
		for(MimeType mimeType : set) {
			for(String s : mimeType.getStrings()) {
				mMap.put(s, mimeType);
			}
		}
	}
	
	
}
