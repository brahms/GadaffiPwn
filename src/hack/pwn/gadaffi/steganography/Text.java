package hack.pwn.gadaffi.steganography;

import hack.pwn.gadaffi.Constants;

import java.nio.ByteBuffer;

public class Text extends Payload {
	

	Integer textId = null;
	String text = null;

	/**
	 * @return the textId
	 */
	public Integer getTextId() {
		return textId;
	}

	/**
	 * @param textId the textId to set
	 */
	public void setTextId(Integer textId) {
		this.textId = textId;
	}

	/**
	 * @return the text
	 */
	public String getText() {
		return text;
	}

	/**
	 * @param text the text to set
	 */
	public void setText(String text) {
		this.text = text;
	}

	@Override
	public byte[] toBytes() {
		return getText().getBytes(Constants.CHARSET);
	}

	public static Payload fromByteBuffer(ByteBuffer byteBuffer) {
		byte[] textBytes = new byte[byteBuffer.remaining()];
		byteBuffer.get(textBytes);
		
		Text text = new Text();
		
		text.setText(new String(textBytes, Constants.CHARSET));
		
		return text;
		
	}

	@Override
	public void toByteBuffer(ByteBuffer buffer) {
		buffer.put(toBytes());
		
	}

	@Override
	public String toString() { 
		return String.format("Text[TextId %d, TextMessage \"%s\", Payload %s]", getTextId(), getText(), super.toString());
	}
	
	@Override
	public int toBytesLength() {
		return toBytes().length;
	}
}
