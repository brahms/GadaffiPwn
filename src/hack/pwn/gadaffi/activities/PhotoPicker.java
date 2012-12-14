package hack.pwn.gadaffi.activities;

import hack.pwn.gadaffi.Constants;
import hack.pwn.gadaffi.R;
import hack.pwn.gadaffi.Utils;
import hack.pwn.gadaffi.images.BitmapScaler;
import hack.pwn.gadaffi.steganography.PngStegoImage;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class PhotoPicker extends Activity {
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
		public int  height;
		public int  width;
		public int  maxBytes;
		
		@Override
		public String toString() {
			return String.format("Photo[FilePath: %s, Height: %s, Width %s, MaxBytes %s]", filePath, height, width, maxBytes);
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
				photo.height = source.readInt();
				photo.width = source.readInt();
				photo.maxBytes = source.readInt();
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
				dest.writeInt(photo.height);
				dest.writeInt(photo.width);
				dest.writeInt(photo.maxBytes);
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
        if(getActionBar() != null) {
        	getActionBar().setDisplayHomeAsUpEnabled(true);
        }
        
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
        getMenuInflater().inflate(R.menu.activity_photo_picker, menu);
        return true;
    }

    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
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
					String[] filePathColumn = {MediaStore.Images.Media.DATA};

					Cursor cursor = getContentResolver().query(
					                   selectedImage, filePathColumn, null, null, null);
					cursor.moveToFirst();

					int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
					String filePath = cursor.getString(columnIndex);
					cursor.close();
					
					File imageFile = new File(filePath);
					Log.v(TAG, String.format("Got file %s", imageFile));
					BitmapScaler scaler = new BitmapScaler(imageFile, mImageView.getWidth());
					
					mImageView.setImageBitmap(scaler.getScaled());
					
					mSelectedPhotoState = new StatePhoto();
					mSelectedPhotoState.filePath = filePath;
					mSelectedPhotoState.height = scaler.getOriginalHeight();
					mSelectedPhotoState.width  = scaler.getOriginalWidth();
					mSelectedPhotoState.maxBytes = PngStegoImage.getMaxBytesEncodable(mSelectedPhotoState.height, mSelectedPhotoState.width);
					
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
		mNeededBytes = mState.TotalBytesNeeded - maxBytes;
		mNeededBytes = Math.max(0, mNeededBytes);
		mAbleBytesText.setText(labelAbleBytes + maxBytes);
		mNeededBytesText.setText(labelNeededBytes + mNeededBytes);
	}
    
    

}
