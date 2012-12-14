package hack.pwn.gadaffi.activities;

import hack.pwn.gadaffi.Constants;
import hack.pwn.gadaffi.R;
import hack.pwn.gadaffi.Utils;
import hack.pwn.gadaffi.database.OutboundMmsPeer;
import hack.pwn.gadaffi.images.BitmapScaler;
import hack.pwn.gadaffi.providers.MmsProvider;
import hack.pwn.gadaffi.steganography.Attachment;
import hack.pwn.gadaffi.steganography.Email;
import hack.pwn.gadaffi.steganography.OutboundMms;
import hack.pwn.gadaffi.steganography.Packet;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.TextView;

public class CreateEmail extends Activity {

    static final String TAG = "activities.CreateEmail";
	EditText mEditTextMessage;
    EditText mEditTextSubject;
	EditText mEditTextPhoneNumber;
	TextView mTextViewAttachments;
	State mState;
	static class AttachmentState {
		String fullpath;
		int size;
		String filename;
	}
	
	static class State implements Parcelable{
		String message;
		String subject;
		String phoneNumber;
		List<PhotoPicker.StatePhoto> photos = null;
		List<OutboundMms> outboundMms = new ArrayList<OutboundMms>();
		List<AttachmentState> attachments = new ArrayList<AttachmentState>();
		public int currentOutboundMms = 0;
		
		@Override
		public String toString() {
			StringBuilder b = new StringBuilder();
			
			b.append("CreateEmailState[")
			.append(String.format("Message: %s,", message))
			.append(String.format("Subject: %s,", subject))
			.append(String.format("phoneNumber: %s,", phoneNumber))
			.append(String.format("photos: %s,", photos)).append("]");
			
			return b.toString();
		}
		
		@Override
		public int describeContents() {
			// TODO Auto-generated method stub
			return 0;
		}
		@Override
		public void writeToParcel(Parcel dest, int flags) {
			dest.writeString(phoneNumber);
			dest.writeString(subject);
			dest.writeString(message);
			dest.writeInt(attachments.size());
			for(AttachmentState attachmentState : attachments) {
				dest.writeString(attachmentState.filename);
				dest.writeString(attachmentState.fullpath);
				dest.writeInt(attachmentState.size);
			}
		}
		
		public static final Parcelable.Creator<CreateEmail.State> CREATOR = new Creator<CreateEmail.State>() {
			
			@Override
			public State[] newArray(int size) {
				return new CreateEmail.State[size];
			}
			
			@Override
			public State createFromParcel(Parcel source) {
				CreateEmail.State state = new CreateEmail.State();
				state.phoneNumber = source.readString();
				state.subject = source.readString();
				state.message = source.readString();
				int attatchmentNum = source.readInt();
				for(int i = 0 ; i< attatchmentNum; i++) {
					AttachmentState att = new AttachmentState();
					att.filename = source.readString();
					att.fullpath = source.readString();
					att.size = source.readInt();
					state.attachments.add(att);
				}
				return state;
			}
		};
		public Email toEmail(boolean fakeAttachments) {
			Email email = new Email();
			email.setFrom(phoneNumber);
			email.setMessage(message);
			email.setSubject(subject);
			
			for (AttachmentState attachmentState : attachments) {
				Attachment att = new Attachment();
				att.setDataLength(attachmentState.size);
				att.setFilename(attachmentState.filename);
				email.addAttachment(att);
			}
			return email;
		}
		
	}


	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_email);
        if(getActionBar() != null) {
        	getActionBar().setDisplayHomeAsUpEnabled(true);
        }
        
        mEditTextMessage = (EditText) findViewById(R.id.editTextMessage);
        mEditTextSubject = (EditText) findViewById(R.id.editTextSubject);
        mEditTextPhoneNumber = (EditText) findViewById(R.id.editTextPhoneNumber);
        mTextViewAttachments = (TextView) findViewById(R.id.textViewAttachments);
        
        if(savedInstanceState != null) {
        	initFromState(savedInstanceState.getParcelable(Constants.KEY_STATE));
        }
        else {
        	Log.v(TAG, "No savedInstanceState was given.");
        	initFromState(null);
        }
    }

    private void initFromState(Parcelable parcelable) {
		Log.v(TAG, "Entered initFromState()");
		
		if (parcelable == null) {
			Log.v(TAG, "No state was given, not doing anything.");
			mState = new State();
		}
		else {
			mState = (State) parcelable;
			Log.v(TAG, "Got state: " + mState.toString());
			mEditTextMessage.setText(mState.message);
			mEditTextPhoneNumber.setText(mState.phoneNumber);
			mEditTextSubject.setText(mState.subject);
		}
	}

	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_create_email, menu);
        return true;
    }



	/* (non-Javadoc)
	 * @see android.app.Activity#onPause()
	 */
	@Override
	protected void onPause() {
		Log.v(TAG, "Entered onPause()");
		super.onPause();
	}


	/* (non-Javadoc)
	 * @see android.app.Activity#onSaveInstanceState(android.os.Bundle)
	 */
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		Log.v(TAG, "Entered onSaveInstanceState()");
		super.onSaveInstanceState(outState);
		outState.putParcelable(Constants.KEY_STATE, mState);
	}

	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
            	setResult(RESULT_CANCELED);
            	finish();
        }
        return super.onOptionsItemSelected(item);
    }
	
	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {	
		switch(item.getItemId()) {
		case R.id.send_email:
			Log.v(TAG, "Got a steganize and send email click.");
			saveState();
			
			PhotoPicker.State photoPickerState = new PhotoPicker.State();
			photoPickerState.TotalBytesGotten = 0;
			photoPickerState.TotalBytesNeeded = mState.toEmail(true).toBytesLength();
			Log.v(TAG, "Total bytes of our email: " + photoPickerState.TotalBytesNeeded);

			startActivityForResult(generatePhotoPickerIntent(photoPickerState), 0);
			break;
		case R.id.discard_email:
			Log.v(TAG, "Got a discard menu click.");
			setResult(RESULT_CANCELED);
			finish();
		}
		return super.onMenuItemSelected(featureId, item);
	}

	private Intent generatePhotoPickerIntent(
			PhotoPicker.State photoPickerState) {
		Intent intent = new Intent(this, PhotoPicker.class);
		intent.putExtra(Constants.KEY_STATE, photoPickerState);
		return intent;
	}

	private void saveState() {
		Log.v(TAG, "Entered saveState()");
		mState.message = mEditTextMessage.getText().toString();
		mState.phoneNumber = mEditTextPhoneNumber.getText().toString();
		mState.subject = mEditTextSubject.getText().toString();
		
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onActivityResult(int, int, android.content.Intent)
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.v(TAG, String.format("Entered onActivityResult() %d, %d, %s", requestCode, resultCode, data));
		super.onActivityResult(requestCode, resultCode, data);
		switch(requestCode) {
		case 0:
			switch(resultCode) {
			case RESULT_CANCELED:
				Log.v(TAG, "Got result canceled.");
				break;
			case RESULT_OK:
				Log.v(TAG, "Got result ok.");
				PhotoPicker.State photopickerState = (PhotoPicker.State) data.getParcelableExtra(Constants.KEY_STATE);
				Log.v(TAG, String.format("Got photopicker state back: %s", photopickerState));
				Utils._assert(photopickerState != null);
				Utils._assert(photopickerState.TotalBytesNeeded >= 0);
				if(photopickerState.TotalBytesNeeded <= photopickerState.TotalBytesGotten) {
					Log.v(TAG, "We have enough bytes in the returned photos, stego time!");
					mState.photos = photopickerState.Photos;
					StegoAsyncTask task = new StegoAsyncTask();
					task.execute(mState);
				}
				else {
					Log.v(TAG, "We do not have enough bytes in the returned photos, requesting another photo");
					startActivityForResult(generatePhotoPickerIntent(photopickerState), 0);
				}
				
				break;
			}
			break;
		case 1:
			switch(resultCode) {
			case RESULT_CANCELED:
				Log.v(TAG, "Got result cancelled. :(");
				break;
			case RESULT_OK:
				Log.v(TAG, "Got result ok :)");
				try {
					//if(mState.currentOutboundMms < mState.outboundMms.size());
					//sendMms(mState.currentOutboundMms);
				}
				catch(Exception ex) {
					Log.v(TAG, "Couldnt send new mms.");
					setResult(RESULT_CANCELED);
					finish();
				}
				break;
			}
			break;
		}
		
	}
	void sendMms(int index) throws Exception {
		if(mState.outboundMms.size() == 0) {
			Log.v(TAG, "Cant queue any mms, none in the queue.");
			throw new Exception("Failed");
		}
		if(index > mState.outboundMms.size() ) {
			Log.v(TAG, String.format("Cant queue index %d, the queue doesn't have that index, its size %d", index, mState.outboundMms));

			throw new Exception("Failed");
		}
		
		
	}
	private class StegoAsyncTask extends AsyncTask<State, Long, List<OutboundMms>> {
		private static final String TAG = CreateEmail.TAG + ".StegoAsyncTask";
		ProgressDialog progress;
		
		
		/* (non-Javadoc)
		 * @see android.os.AsyncTask#onCancelled()
		 */
		@Override
		protected void onCancelled() {
			// TODO Auto-generated method stub
			super.onCancelled();
			Log.v(TAG, "Entered onCancelled()");
		}
		/* (non-Javadoc)
		 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
		 */
		@Override
		protected void onPostExecute(List<OutboundMms> result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			
			Log.v(TAG, "Entered onPostExecute()");
			if(result == null) {
				Log.v(TAG, "Result is null.");
			}
			else {
				if(result.isEmpty()) {
					Log.v(TAG, "Result is empty.");
				}
				else {
					/*mState.outboundMms = result;
					mState.currentOutboundMms = 0;
					try {
						sendMms(0);
					} catch (Exception e) {
						Log.e(TAG, "Could not send mms, :(");
						setResult(RESULT_CANCELED);
						finish();
					}*/

					Intent sendIntent = new Intent(Intent.ACTION_SEND_MULTIPLE);
					sendIntent.putExtra("address", mState.phoneNumber);
					sendIntent.putExtra("sms_body", "Text"); 
					sendIntent.setType("image/png");
					sendIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
				
					//TODO: Figure out how to do this in a way not based on Uri.fromFile
					ArrayList<Uri> uris = new ArrayList<Uri>();
					for(OutboundMms mms : result) {
						uris.add(Uri.fromFile(mms.getFile(CreateEmail.this)));
					}
					sendIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris);
					startActivity(sendIntent);
				}
			}
			progress.dismiss();
		}

		/* (non-Javadoc)
		 * @see android.os.AsyncTask#onPreExecute()
		 */
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			progress = ProgressDialog.show(CreateEmail.this, "Stegonizing", "Working...", true);
		}
	
		@Override
		protected List<OutboundMms> doInBackground(State... params) {
			Log.v(TAG, "Entered doInBackground()");
			Utils._assert(params.length == 1);
			State state = params[0];
			Log.v(TAG, "Got state: " + state.toString());
			try {
				Email email = state.toEmail(false);
				Log.v(TAG, String.format("Have %d attachments.", state.attachments.size()));
				for(AttachmentState attachmentState : state.attachments) {
					File file = new File(attachmentState.fullpath);
					if(file.canRead()) {
						Attachment attachment = new Attachment();
						attachment.setFilename(file.getName());
						
						Log.v(TAG, "The file's name is: " + file.getName());
						
						String[] splitByDot = file.getName().split(".");
						
						String ext = splitByDot.length > 1 ? splitByDot[splitByDot.length-1] : null;
						Log.v(TAG, "The file's extension is: " + ext);
						if(ext == null) {
							attachment.setMimeType(Constants.MIME_TYPE_OCTET_STREAM);
						}
						else {
							attachment.setMimeType(MimeTypeMap.getSingleton().getMimeTypeFromExtension(ext));
						}
						BufferedInputStream bis = null;
						ByteArrayOutputStream bos = new ByteArrayOutputStream();
						byte[] fileBytes = null;
						Log.v(TAG, "Attempting to read file.");
						try {
							bis = new BufferedInputStream(new FileInputStream(file));
							
							int b =  bis.read();
							while(b != -1) {
								bos.write(b);
								b = bis.read();
							}
							fileBytes = bos.toByteArray();
							attachment.setData(fileBytes);
							Log.v(TAG, String.format("Success got attachment of %d bytes length!", fileBytes.length));
							email.addAttachment(attachment);
						}
						catch(Exception ex) {
							Log.e(TAG, "Got error trying to read file, will skip.", ex);
						}
						finally {
							if(bis != null) bis.close();
							bos.close();
						}
					}
					else {
						Log.e(TAG, "Cannot read file: " + file + ". Will skip.");
					}
				}
				
				Log.v(TAG, String.format("Encoding email into %d photos.", mState.photos.size()));
				
				List<Integer> maxLengths = new ArrayList<Integer>(mState.photos.size());
				for(PhotoPicker.StatePhoto photo : mState.photos){
					maxLengths.add(photo.maxBytes);
				}
				
				Packet packet = Packet.encode(email, maxLengths);
				
				List<Bitmap> bitmaps = new ArrayList<Bitmap>();
				
				for(PhotoPicker.StatePhoto photo : mState.photos) {
					Log.v(TAG, String.format("Loading photo: %s with a width of %d", photo.filePath, photo.width));
					BitmapScaler scaler = new BitmapScaler(new File(photo.filePath), photo.width);
					bitmaps.add(scaler.getScaled());
				}
				
				Log.v(TAG, "Calling outbound mms peer.");
				List<OutboundMms> mmses = OutboundMmsPeer.insertPngStegoImage(packet, mState.phoneNumber, bitmaps);
				Log.v(TAG, String.format("Got %d mms objects.", mmses.size()));
				return mmses;
			}
			catch(Exception ex) {
				Log.e(TAG, "Got uncaught exception", ex);
				return null;
			}
		}
	
		
		
	}


}