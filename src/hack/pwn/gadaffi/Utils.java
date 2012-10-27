package hack.pwn.gadaffi;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap.CompressFormat;
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

}
