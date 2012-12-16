package hack.pwn.gadaffi.activities;

import hack.pwn.gadaffi.Constants;
import hack.pwn.gadaffi.R;
import hack.pwn.gadaffi.Utils;
import hack.pwn.gadaffi.database.BasePeer;
import hack.pwn.gadaffi.database.EmailEntry;
import hack.pwn.gadaffi.database.EmailPeer;
import hack.pwn.gadaffi.steganography.Attachment;
import hack.pwn.gadaffi.steganography.Email;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.app.NavUtils;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class ShowEmailActivity extends Activity {

    final static String TAG = "activites.ShowEmailActivity";
    
    
    State mState;
    TextView mTextViewAttachments;
    TextView mTextViewFrom;
    TextView mTextViewSubject;
    TextView mTextViewMessage;
    TextView mTextViewDate;
    Email mEmail = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_email);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        Utils._assert(getIntent() != null);
        
        mTextViewMessage     = (TextView) findViewById(R.id.textViewMessage);
        mTextViewSubject     = (TextView) findViewById(R.id.textViewSubject);
        mTextViewFrom        = (TextView) findViewById(R.id.textViewPhoneNumber);
        mTextViewAttachments = (TextView) findViewById(R.id.textViewAttachments);
        mTextViewAttachments.setOnClickListener(new AttachmentOnClick());
        mTextViewDate        = (TextView) findViewById(R.id.textViewDate);
        
        BasePeer.init(getApplicationContext());
        initFromState(savedInstanceState);
        redraw();
        
    }

    @Override
    protected void onSaveInstanceState(Bundle outState)
    {
        // TODO Auto-generated method stub
        super.onSaveInstanceState(outState);
        outState.putParcelable(Constants.KEY_STATE, mState);
    }

    private void initFromState(Bundle savedInstanceState)
    {
        if(savedInstanceState != null) {
            Log.v(TAG, "Loading from savedInstanceState");
            mState = savedInstanceState.getParcelable(Constants.KEY_STATE);
        }
        else {
            Log.v(TAG, "Loading from intent: " + getIntent().toString());
            int id = getIntent().getIntExtra(EmailEntry._ID, -1);
            Utils._assert(id != -1);
            
            mEmail = EmailPeer.getEmailById(id);
            Utils._assert(mEmail != null);
            mState = new State(mEmail);
        }
        
    }
    
    synchronized Email getEmail() {
        if (mEmail == null) {
            mEmail = EmailPeer.getEmailById(mState.emailId);
        }
        
        return mEmail;
    }

    private void redraw()
    {
        mTextViewFrom.setText(mState.from);
        mTextViewDate.setText(Utils.getFormattedDate(mState.time));
        mTextViewMessage.setText(mState.message);
        mTextViewSubject.setText(mState.subject);
        mTextViewAttachments.setText(mState.getAttachmentsString());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_show_email, menu);
        return true;
    }

    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
            case R.id.discard_email:
                Log.v(TAG, "Got a discard email click for id: " + mState.emailId);
                Intent intent = new Intent();
                intent.putExtra(EmailEntry._ID, mState.emailId);
                setResult(Constants.RESULT_DELETE, intent);
                finish();
        }
        return super.onOptionsItemSelected(item);
    }
    
    static class State implements Parcelable{
        long   time;
        String from;
        String message;
        String subject;
        int emailId;
        List<String> attachments = new ArrayList<String>();
        
        public State(Email email)
        {
            emailId = email.getEmailId();
            from = email.getFrom();
            message = email.getMessage();
            subject = email.getSubject();
            for(Attachment att : email.getAttachments()) {
                Log.v(TAG, "Adding : " + att);
                attachments.add(att.getFilename());
            }
            time = email.getTimeReceived().toMillis(false);
            
        }
        
        public State()
        {
            // TODO Auto-generated constructor stub
        }

        public String getAttachmentsString() {
            Log.v(TAG, "Attachment size: " + attachments.size());
            if(attachments.isEmpty()) {
                return "No attachments.";
            }
            else {
                return TextUtils.join("\n", attachments);
            }
        }

        
        public static final Parcelable.Creator<State> CREATOR = new Creator<ShowEmailActivity.State>()
        {
            
            @Override
            public State[] newArray(int size)
            {
                return new State[size];
            }
            
            @Override
            public State createFromParcel(Parcel source)
            {
                State state = new State();
                state.from    = source.readString();
                state.message = source.readString();
                state.subject = source.readString();
                int total = source.readInt();
                for(int i = 0; i < total; i++) {
                    state.attachments.add(source.readString());
                }
                state.time = source.readLong();
                state.emailId = source.readInt();
                return state;
            }
        };
        @Override
        public int describeContents()
        {
            // TODO Auto-generated method stub
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags)
        {
            dest.writeString(from);
            dest.writeString(message);
            dest.writeString(subject);
            dest.writeInt(attachments.size());
            for(String att : attachments) {
                dest.writeString(att);
            }
            dest.writeLong(time);
            dest.writeInt(emailId);
            
        }
    }
    
    class AttachmentOnClick implements View.OnClickListener {


        @Override
        public void onClick(View v)
        {
            Log.v(TAG, "Entered onClick for attachments.");
            if(mState.attachments.size() > 0) {
                final EditText input = new EditText(ShowEmailActivity.this);
                AlertDialog.Builder alert = new AlertDialog.Builder(ShowEmailActivity.this);
                alert.setTitle("Save Attachments.");
                alert.setView(input);
                alert.setPositiveButton("Ok", new DialogInterface.OnClickListener()
                {
                    
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        String value = input.getText().toString();
                        File folder = new File(ShowEmailActivity.this.getExternalFilesDir(null), "/" + value);
                        Log.v(TAG, "User wants to save attachments to: " + folder.getAbsolutePath());
                        
                        AttachmentSaveTask task = new AttachmentSaveTask();
                        task.execute(folder);
                        
                        
                    }
                });
                alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener()
                {
                    
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        // Nothing.
                        
                    }
                });
                alert.show();
            }
            
        }
        
        class AttachmentSaveTask extends AsyncTask<File, Integer, Boolean> {
            @Override
            protected void onProgressUpdate(Integer... values)
            {
                // TODO Auto-generated method stub
                super.onProgressUpdate(values);
                mDialog.setProgress(values[0]);
            }



            static final String TAG = ShowEmailActivity.TAG + ".AttachmentSaveTask";
            
            ProgressDialog mDialog;
            
            
            @Override
            protected void onPostExecute(Boolean result)
            {
                Log.v(TAG, "Entered onPostExecute(): " + result);
                super.onPostExecute(result);
                mDialog.dismiss();
                
                if(result) {
                    AlertDialog.Builder alert = new AlertDialog.Builder(ShowEmailActivity.this);
                    alert.setTitle("Success");
                    alert.setMessage("Successfully saved the attachments!");
                    alert.setPositiveButton("Ok", new DialogInterface.OnClickListener()
                    {
                        
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            // TODO Auto-generated method stub
                            
                        }
                    });
                    alert.show();
                }
                else {

                    AlertDialog.Builder alert = new AlertDialog.Builder(ShowEmailActivity.this);
                    alert.setTitle("Failure");
                    alert.setMessage("Failed to write to the external storage directory.");
                    alert.setPositiveButton("Ok", new DialogInterface.OnClickListener()
                    {
                        
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            // TODO Auto-generated method stub
                            
                        }
                    });
                    alert.show();
                }
            }



            @Override
            protected void onPreExecute()
            {
                super.onPreExecute();
                Log.v(TAG, "Entered onPreExecute()");
                mDialog = new ProgressDialog(ShowEmailActivity.this);
                mDialog.setTitle("Saving.");
                mDialog.setMessage("Saving attachments.");
                mDialog.setMax(getEmail().getAttachments().size());
            }



            @Override
            protected Boolean doInBackground(File... params)
            {
                Utils._assert(params.length == 1);

                Log.v(TAG, "Entered doInBackground() for file: " + params[0].toString());
                File folder = params[0];
                int file = 1;
                if(folder.mkdirs() || folder.isDirectory()) {
                    if(folder.canWrite()) {
                        for(Attachment att : getEmail().getAttachments()) {
                            File attachment = new File(folder, att.getFilename());
                            try {
                                Log.v(TAG, String.format("Writing file: %s ", attachment));  
                                BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(attachment));
                                out.write(att.getData());
                                out.flush();
                                out.close();
                                Log.v(TAG, String.format("Wrote %d bytes to file %s", att.getData().length, attachment));
                                publishProgress(file++);
                            }
                            catch(Exception ex) {
                                Log.e(TAG, String.format("Error writing file: %s...", attachment),ex);
                                return false;
                            }
                        }
                        return true;
                    }
                    else {
                        Log.v(TAG, "Can't write folder: " + folder);
                    }
                }
                else {
                    Log.v(TAG, "Couldn't make folder: " + folder.toString());
                }
                return false;
            }
            
        }
        
    }

}
