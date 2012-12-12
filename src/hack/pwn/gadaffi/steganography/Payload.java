package hack.pwn.gadaffi.steganography;

import java.nio.ByteBuffer;

import android.text.format.Time;


abstract public class Payload {
	Integer payloadId;
	Time timeReceived;
	String from;
	
	/**
	 * @return the payloadId
	 */
	public Integer getPayloadId() {
		return payloadId;
	}


	/**
	 * @param payloadId the payloadId to set
	 */
	public void setPayloadId(int payloadId) {
		this.payloadId = payloadId;
	}


	/**
	 * @return the timeReceived
	 */
	public Time getTimeReceived() {
		return timeReceived;
	}


	/**
	 * @param timeReceived the timeReceived to set
	 */
	public void setTimeReceived(Time timeReceived) {
		this.timeReceived = timeReceived;
	}


	/**
	 * @return the from
	 */
	public String getFrom() {
		return from;
	}


	/**
	 * @param from the from to set
	 */
	public void setFrom(String from) {
		this.from = from;
	}
	
	abstract public byte[] toBytes();
	abstract public void toByteBuffer(ByteBuffer buffer);
	abstract public int toBytesLength();

	@Override
	public String toString() {
		return String.format("Payload[Payload Id %d, TimeReceived %s, From %s]", getPayloadId(), getTimeReceived(), getFrom());
	}

	public void setTimeReceived(long ms) {
		Time time = new Time();
		time.set(ms);
		setTimeReceived(time);
	}
}
