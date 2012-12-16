package hack.pwn.gadaffi;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Calendar;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.text.format.DateFormat;
import android.text.format.Time;
import android.util.Log;

public class Utils {
	private static String TAG = "Utils";
	
	public static long getThreadId() {
		Thread t = Thread.currentThread();
		return t.getId();
	}

	public static String getThreadSignature() {
		Thread t = Thread.currentThread();
		long l = t.getId();
		String name = t.getName();
		long p = t.getPriority();
		String gname = t.getThreadGroup().getName();
		return (name + ":(id)" + l + ":(priority)" + p + ":(group)" + gname);
	}

	public static void logThreadSignature(String tag) {
		Log.d(tag, getThreadSignature());
	}

	/**
	 * Turns a number of bytes into a human readable string
	 * 
	 * @param bytes
	 * 
	 *            Defaults precision to 2
	 */
	public static String formatBytes(double bytes) {
		return formatBytes(bytes, 1);
	}

	/**
	 * Turns a number of bytes into a human readable string
	 * 
	 * @param bytes
	 *            The total number of bytes
	 * @param precision
	 *            The precision to format to
	 */
	public static String formatBytes(double bytes, int precision) {
		final String[] units = { "B", "KB", "MB", "GB", "TB" };
		final String zero = "0";
		precision = Math.max(0, precision);

		DecimalFormat format = new DecimalFormat("#"
				+ (precision == 0 ? "" : "." + Utils.repeat(zero, precision)));

		double pow = Math.floor(Math.log(bytes) / Math.log(1024));
		pow = Math.min(pow, units.length - 1);

		bytes /= Math.pow(1024, pow);

		return format.format(bytes) + units[(int) pow];

	}

	/**
	 * Repeats a string <i>times</i> times
	 * 
	 * @param string
	 *            The string to repeat
	 * @param times
	 *            The amount of times to repeat.
	 * @return
	 */
	private static String repeat(String string, int times) {
		times = Math.max(0, times);

		StringBuilder b = new StringBuilder();

		for (int i = 0; i < times; i++) {
			b.append(string);
		}

		return b.toString();
	}

	public static byte[] imageToPngByteArray(byte[] imageBytes) {
		
		Bitmap originalImage = BitmapFactory.decodeByteArray(imageBytes, 0,
				imageBytes.length);
		ByteArrayOutputStream bos = new ByteArrayOutputStream(
				imageBytes.length);
		originalImage.compress(CompressFormat.PNG, 100, bos);
		
		byte[] ret = bos.toByteArray();
		
		try {
			bos.close();
		} 
		catch (IOException e) {
			Log.e(TAG, "Error.", e);
		}
		
		return ret;

	}
	
	public static void _assert(boolean b) throws AssertionError {
		if(!b) {
			throw new AssertionError();
		}
		
	}

	public static Time getNow() {
		Time t = new Time();
		
		t.setToNow();
		
		return t;
	}
	
	public static File getFilesDir(Context context) {
		if(context.getFilesDir().getAbsolutePath().equals("/dev/null")) {
			return new File("/data/data/hack.pwn.gadaffi/files");
		}
		else {
			return context.getFilesDir();
		}
		
		
	}

    public static String getFormattedDate(Time time) {
        return getFormattedDate(time.toMillis(false));
    }
	public static String getFormattedDate(long time) {
	    
	    long smsTimeInMilis = time;
	    Calendar smsTime = Calendar.getInstance();
	    smsTime.setTimeInMillis(smsTimeInMilis);

	    Calendar now = Calendar.getInstance();

	    final String timeFormatString = "h:mm aa";
	    final String dateTimeFormatString = "EEEE, MMMM d, h:mm aa";
	    if(now.get(Calendar.DATE) == smsTime.get(Calendar.DATE) ){
	        return "Today " + DateFormat.format(timeFormatString, smsTime);
	    }else if(now.get(Calendar.DATE) - smsTime.get(Calendar.DATE) == 1 ){
	        return "Yesterday " + DateFormat.format(timeFormatString, smsTime);
	    }else if(now.get(Calendar.YEAR) == smsTime.get(Calendar.YEAR)){
	        return DateFormat.format(dateTimeFormatString, smsTime).toString();
	    }else
	        return DateFormat.format("MMMM dd yyyy, h:mm aa", smsTime).toString();
	}

}
