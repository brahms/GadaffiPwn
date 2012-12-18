package hack.pwn.gadaffi.activities;

import hack.pwn.gadaffi.Constants;
import hack.pwn.gadaffi.R;
import hack.pwn.gadaffi.Utils;
import hack.pwn.gadaffi.images.BitmapScaler;
import hack.pwn.gadaffi.steganography.PngStegoImage;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap.CompressFormat;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;


public class PhotoPicker extends SherlockActivity {
	private static final String TAG = "PhotoPicker";
	
	ImageView mImageView;
	TextView mAbleBytesText;
	TextView mNeededBytesText;
	State mState;
	Button mButtonDone;
	StatePhoto mSelectedPhotoState;
	int mNeededBytes;
	String labelNeededBytes;
	String labelAbleBytes;
	
	public static class StatePhoto {
		public String filePath;
		public int  originalHeight;
		public int  originalWidth;
		public int  originalSize;
		public int  scaledWidth;
		public int  scaledHeight;
		public int  maxBytes;
		
		@Override
		public String toString() {
			return String.format("Photo[FilePath: %s,Original Height: %d, Original Width %d, Original Size %d, MaxBytes %d, Scaled Height %d, Scaled Width %d]", filePath, originalHeight, originalWidth, originalSize, maxBytes, scaledHeight, scaledWidth);
		}
	}
	public static class State implements Parcelable{
		public int TotalBytesNeeded;
		public int TotalBytesGotten;
		public List<StatePhoto> Photos = new ArrayList<StatePhoto>();
		public State() {
			
		}
		public State(Parcel source) {
			TotalBytesNeeded = source.readInt();
			TotalBytesGotten = source.readInt();
			int size = source.readInt();
			
			for(int i = 0; i < size; i++) {
				StatePhoto photo = new StatePhoto();
				
				photo.filePath = source.readString();
				photo.originalHeight = source.readInt();
				photo.originalWidth = source.readInt();
				photo.originalSize = source.readInt();
				photo.maxBytes = source.readInt();
				photo.scaledHeight = source.readInt();
				photo.scaledWidth = source.readInt();
				Photos.add(photo);
			}
		}
		@Override
		public int describeContents() {
			// TODO Auto-generated method stub
			return 0;
		}
		@Override
		public void writeToParcel(Parcel dest, int flags) {
			Log.v(TAG, "Writing to parcel. " + toString());
			dest.writeInt(TotalBytesNeeded);
			dest.writeInt(TotalBytesGotten);
			dest.writeInt(Photos.size());
			for(StatePhoto photo : Photos) {
				dest.writeString(photo.filePath);
				dest.writeInt(photo.originalHeight);
				dest.writeInt(photo.originalWidth);
				dest.writeInt(photo.originalSize);
				dest.writeInt(photo.maxBytes);
				dest.writeInt(photo.scaledHeight);
				dest.writeInt(photo.scaledWidth);
			}
			
		}
		
		@Override
		public String toString() {
			StringBuilder b = new StringBuilder();
			
			b.append("State[");
			b.append(String.format("TotalNeeded: %d, TotalGotten: %d, Difference: %d ", TotalBytesNeeded, TotalBytesGotten, TotalBytesNeeded - TotalBytesGotten));
			b.append("Current Photos: <");
			for ( StatePhoto photo : Photos) {
				b.append(" " + photo);
			}
			b.append("> ]");
			
			return b.toString();
		}
		
		public static final Parcelable.Creator<State> CREATOR = new Creator<PhotoPicker.State>() {
			
			@Override
			public State[] newArray(int size) {
				return new State[size];
			}
			
			@Override
			public State createFromParcel(Parcel source) {
					return new State(source);
			}
		};
	}
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_picker);
        
        mImageView = (ImageView) findViewById(R.id.imageView);
        
        mImageView.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
				photoPickerIntent.setType("image/*");
				startActivityForResult(photoPickerIntent, R.id.imageView);
			}
		});
        
        mButtonDone = (Button) findViewById(R.id.addButton);
        mButtonDone.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Log.v(TAG, "Entered onClick for button.");
				if(mSelectedPhotoState != null) {
					
					mState.Photos.add(mSelectedPhotoState);
					mState.TotalBytesGotten += mSelectedPhotoState.maxBytes;
					Intent intent = new Intent();
					Log.v(TAG, "Returning state: "+  mState);
					intent.putExtra(Constants.KEY_STATE, mState);
					setResult(RESULT_OK, intent);
					finish();
				}
				else {
					setResult(RESULT_CANCELED);
					finish();
				}
				
			}
		});
        
        mAbleBytesText = (TextView) findViewById(R.id.valueAbleBytes);
        mNeededBytesText = (TextView) findViewById(R.id.valueNeededBytes);
        
        labelNeededBytes = (String) getString(R.string.labelNeededBytes);
        labelAbleBytes  = (String) getString(R.string.labelAbleBytes);
        mState = getIntent().getParcelableExtra(Constants.KEY_STATE);
        
        Utils._assert(mState != null);
        
        updateByteReadouts(0);
        
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getSupportMenuInflater().inflate(R.menu.activity_photo_picker, menu);
        return true;
    }

    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.cancel:
            case android.R.id.home:
                setResult(RESULT_CANCELED);
                finish();
        }
        return super.onOptionsItemSelected(item);
    }

	/* (non-Javadoc)
	 * @see android.app.Activity#onActivityResult(int, int, android.content.Intent)
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);

		Log.v(TAG, "Entered onActivityResult()");
		if(requestCode == R.id.imageView) {
			Log.d(TAG, "Retrieved a new image.");
			
			if(resultCode == RESULT_OK){  
	            try {
					Uri selectedImage = data.getData();
					String[] filePathColumn = {MediaStore.Images.Media.DATA, MediaStore.Images.Media.SIZE};

					Cursor cursor = getContentResolver().query(
					                   selectedImage, filePathColumn, null, null, null);
					cursor.moveToFirst();

					int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
					String filePath = cursor.getString(columnIndex);
					columnIndex     = cursor.getColumnIndex(filePathColumn[1]);
					int size        = cursor.getInt(columnIndex);
					
					cursor.close();
					
					File imageFile = new File(filePath);
					Log.v(TAG, String.format("Got file %s with a total size of %d", imageFile, size));
					BitmapScaler scaler = new BitmapScaler(imageFile, mImageView.getWidth());
					
					mImageView.setImageBitmap(scaler.getScaled());
					
					mSelectedPhotoState = new StatePhoto();
					mSelectedPhotoState.filePath = filePath;
					mSelectedPhotoState.scaledHeight  = mSelectedPhotoState.originalHeight = scaler.getOriginalHeight();
					mSelectedPhotoState.scaledWidth   = mSelectedPhotoState.originalWidth  = scaler.getOriginalWidth();
					mSelectedPhotoState.originalSize   = size;
					
					
					if(size > Constants.MAX_SIZE_FOR_PHOTO_PICK) {
					    Log.v(TAG, String.format("Picked image is bigger than max size of : %d bytes, it's height is %d, and width is %d", 
					                                                         Constants.MAX_SIZE_FOR_PHOTO_PICK, scaler.getOriginalHeight(), scaler.getOriginalWidth()));
					    Log.v(TAG, String.format("We'll use the prescaled image, that has a height of %d and a width of %d", scaler.getScaled().getHeight(), scaler.getScaled().getWidth()));
					    mSelectedPhotoState.scaledHeight = scaler.getScaled().getHeight();
					    mSelectedPhotoState.scaledWidth  = scaler.getScaled().getWidth();
					    Log.v(TAG, String.format("We'll test how big this file is compressed."));
					    ByteArrayOutputStream bos = new ByteArrayOutputStream();
					    scaler.getScaled().compress(CompressFormat.PNG, 100,bos);
					    int testSize = bos.size();
					    Log.v(TAG, String.format("Compressed size is: %d", testSize));
                        bos.close();
                        
                        if(testSize <= Constants.MAX_SIZE_FOR_PHOTO_PICK) {
                            Log.v(TAG, "The current scaled image's size is small enough for our photo pick, using it instead.");
                        }
                        else {
                            Log.v(TAG, "The current scaled image is still to big, trying to scale the image even smaller. (Using .75)");
                    
                            int nextWidth = (int) (Math.floor(Constants.COMPRESS_CONSTANT *(double)scaler.getScaled().getWidth()));
                            while(true) {
                                Log.v(TAG, "Next scaled width to test with will be: " + nextWidth);
                                scaler = new BitmapScaler(imageFile, nextWidth);
                                Log.v(TAG, String.format("We'll test how big this file is compressed."));
                                bos = new ByteArrayOutputStream();   
                                Log.v(TAG, String.format("The scaled image has a height of %d and a width of %d", scaler.getScaled().getHeight(), scaler.getScaled().getWidth()));
                                scaler.getScaled().compress(CompressFormat.PNG, 100,bos);
                                testSize = bos.size();
                                Log.v(TAG, String.format("Compressed size is: %d", testSize));
                                bos.close();               
                                mSelectedPhotoState.scaledHeight = scaler.getScaled().getHeight();
                                mSelectedPhotoState.scaledWidth  = scaler.getScaled().getWidth();
                                if(testSize <= Constants.MAX_SIZE_FOR_PHOTO_PICK) {
                                    Log.v(TAG, "The scaled image's size is small enough for our photo pick, using it instead. Its size is: " + testSize);
                                    break;
                                }
                                nextWidth = (int) (Math.floor(Constants.COMPRESS_CONSTANT*(double)nextWidth));
                            }
                        }
					    
					}
					mSelectedPhotoState.maxBytes = PngStegoImage.getMaxBytesEncodable(mSelectedPhotoState.scaledHeight, mSelectedPhotoState.scaledWidth);
					
					updateByteReadouts(mSelectedPhotoState.maxBytes);
					Log.v(TAG, String.format("Selected photo. %s", mSelectedPhotoState));
					
				} catch (IOException e) {
					// TODO Auto-generated catch block
					Log.e(TAG, "Oops in onActivityResult().", e);
				}
	        }
		}
	}

	private void updateByteReadouts(int maxBytes) {
		Log.v(TAG, "Entered updateBytReadouts("+ maxBytes +")");
		mNeededBytes = mState.TotalBytesNeeded - mState.TotalBytesGotten - maxBytes;
		mNeededBytes = Math.max(0, mNeededBytes);
		mAbleBytesText.setText(labelAbleBytes + maxBytes);
		mNeededBytesText.setText(labelNeededBytes + mNeededBytes);
	}
    
    

}
