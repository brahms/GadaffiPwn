package hack.pwn.gadaffi.steganography;

import hack.pwn.gadaffi.Utils;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import android.content.Context;
import android.text.format.Time;
import android.util.Log;

public class OutboundMms {
	Integer id = null;
	Integer partNumber = null;
	Byte    sequenceNumber = null;
	String  mimeType = null;
	String  to = null;
	Time    timeQueued = null;
	Time    timeSent = null;
    String  imageFilename;
	private static final String TAG = "steganography.OutboundMms";
	
	/**
	 * @return the id
	 */
	public Integer getId() {
		return id;
	}
	/**
	 * @param id the id to set
	 */
	public OutboundMms setId(Integer id) {
		this.id = id;
		return this;
	}
	/**
	 * @return the partNumber
	 */
	public Integer getPartNumber() {
		return partNumber;
	}
	/**
	 * @param partNumber the partNumber to set
	 * @return 
	 */
	public OutboundMms setPartNumber(Integer partNumber) {
		this.partNumber = partNumber;
		return this;
	}
	/**
	 * @return the sequenceNumber
	 */
	public Byte getSequenceNumber() {
		return sequenceNumber;
	}
	/**
	 * @param byte1 
	 * @param byte the sequenceNumber to set
	 * @return 
	 */
	public OutboundMms setSequenceNumber(Byte bite) {
		this.sequenceNumber = bite;
		return this;
	}
	
	public File getFile(Context context) {
		File filesDir = Utils.getFilesDir(context);
		Log.v(TAG, String.format("Entered getFile(). Files dir is %s, filename is %s", filesDir, getImageFilename()));
		return new File(filesDir, getImageFilename());
	}
	
	/**
	 * @return the mimeType
	 */
	public String getMimeType() {
		return mimeType;
	}
	/**
	 * @param mimeType the mimeType to set
	 * @return 
	 */
	public OutboundMms setMimeType(String mimeType) {
		this.mimeType = mimeType;
		return this;
	}
	/**
	 * @return the to
	 */
	public String getTo() {
		return to;
	}
	/**
	 * @param to the to to set
	 * @return 
	 */
	public OutboundMms setTo(String to) {
		this.to = to;
		return this;
	}
	/**
	 * @return the timeQueued
	 */
	public Time getTimeQueued() {
		return timeQueued;
	}
	/**
	 * @param timeQueued the timeQueued to set
	 * @return 
	 */
	public OutboundMms setTimeQueued(Time timeQueued) {
		this.timeQueued = timeQueued;
		return this;
	}
	/**
	 * @return the timeSent
	 */
	public Time getTimeSent() {
		return timeSent;
	}
	/**
	 * @param timeSent the timeSent to set
	 * @return 
	 */
	public OutboundMms setTimeSent(Time timeSent) {
		this.timeSent = timeSent;
		return this;
	}
	
	@Override
	public String toString() {
		return String.format("OutboundMms[Id %d, SeqNum %d, PartNum %d, MimeType %s, To %s, TimeQueued %s, TimeSent %s, ImageFilename %s]",
				getId(), getSequenceNumber(), getPartNumber(), getMimeType(), getTo(), getTimeQueued(), getTimeSent(), getImageFilename());
	}
	
	public String getImageFilename() {
		return imageFilename;
	}
	public void setImageFilename(String filename) {
		this.imageFilename = filename;
	}
	
	public static String generateImageFile(int id, String extension) {
		return Integer.toString(id) + '.' + extension;
	}
	public void setTimeQueued(long ms) {
		Time time = new Time();
		time.set(ms);
		setTimeQueued(time);
		
	}
	
	public void setTimeSent(long ms) {
		Time time = new Time();
		time.set(ms);
		setTimeSent(time);
	}
	public byte[] getImageBytes(Context context) throws IOException {
		InputStream is = context.openFileInput(getImageFilename());
		BufferedInputStream bis = new BufferedInputStream(is);
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		
		int bite = bis.read();
		while(bite != -1) {
			bos.write(bite & 0xFF);
			bite = bis.read();
		}
		
		return bos.toByteArray();
	}
	
	

}
