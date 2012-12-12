package hack.pwn.gadaffi.activities;

import hack.pwn.gadaffi.R;
import hack.pwn.gadaffi.images.BitmapScaler;

import java.io.File;
import java.io.IOException;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

public class PhotoPicker extends Activity {
	
	private static final String TAG = "PhotoPicker";
	
	ImageView mImageView;
	TextView mAbleBytesText;
	TextView mNeededBytesText;
	SeekBar mDistortionBar;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_picker);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        
        mImageView = (ImageView) findViewById(R.id.imageView);
        
        mImageView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
				photoPickerIntent.setType("image/*");
				startActivityForResult(photoPickerIntent, R.id.imageView);
			}
		});
        
        mAbleBytesText = (TextView) findViewById(R.id.valueAbleBytes);
        mNeededBytesText = (TextView) findViewById(R.id.valueNeededBytes);
        
        
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
         
					BitmapScaler scaler = new BitmapScaler(new File(filePath), mImageView.getWidth());
					
					mImageView.setImageBitmap(scaler.getScaled());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					Log.e(TAG, "Oops in onActivityResult().", e);
				}
	        }
		}
	}
    
    

}
