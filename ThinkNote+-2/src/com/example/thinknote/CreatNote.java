package com.example.thinknote;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class CreatNote extends Activity implements OnClickListener {
	TextView date,time,title,subcount;
	EditText sub,notes;
	String notedate,mode;
	Bundle intent;
	DBAdapter db;
	ImageView camera;
	Context context;
	static int pic_no=1;
	String picturePath="no_pic";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.createlayout);

		intent=getIntent().getExtras();
		mode=intent.getString("mode");
		context=this;
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		date=(TextView)findViewById(R.id.createdate);
		time=(TextView)findViewById(R.id.createtime);
		sub=(EditText)findViewById(R.id.createsubjecttxt);
		notes=(EditText)findViewById(R.id.createnotes);
		subcount=(TextView)findViewById(R.id.subcount);
		sub.addTextChangedListener(subLengthWatcher);
		camera=(ImageView)findViewById(R.id.startcamera);		 
		camera.setOnClickListener(this);

		getActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
		getActionBar().setCustomView(R.layout.actionbar);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		title=(TextView)findViewById(R.id.actionmodeLabel);
		if(mode.equalsIgnoreCase("create"))
		{
			title.setText("Create Note");
			notedate = new SimpleDateFormat("dd-MMM-yyyy HH:mm").format(new Date());
			String[] dateparts=notedate.split(" ");
			date.setText(dateparts[0]);
			time.setText(dateparts[1]);
		}
		else
		{
			title.setText("Edit Note");
			date.setText(intent.getString("date"));
			time.setText(intent.getString("time"));
			sub.setText(intent.getString("sub"));
			notes.setText(intent.getString("notes"));
			String imgpath=intent.getString("img");
			if(imgpath.length()==0)
			{
				camera.setImageResource(R.drawable.memo);
			}
			else
			{
				File imgFile = new  File(imgpath);
				if(imgFile.exists()){

					// Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
					// camera.setImageBitmap(myBitmap);
					Bitmap thumbnail = (BitmapFactory.decodeFile(imgFile.getAbsolutePath()));
					int nh = (int) ( thumbnail.getHeight() * (512.0 / thumbnail.getWidth()) );
					Bitmap scaled = Bitmap.createScaledBitmap(thumbnail, 512, nh, true);
					//iv.setImageBitmap(scaled);
					camera.setImageBitmap(scaled);
				}
				else
				{
					Toast.makeText(this, "Image no longer exists", Toast.LENGTH_LONG).show();
					camera.setImageResource(R.drawable.memo);
				}
			}
		}		
		Button cancelButton = (Button) findViewById(R.id.cancel);
		cancelButton.setOnClickListener(this); 
		Button doneButton = (Button) findViewById(R.id.done);
		doneButton.setOnClickListener(this);
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.deletemenu, menu);
		return true;
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {

		case R.id.delete:
			DBAdapter db=new DBAdapter(this);
			db.open();
			db.deleteRecord(intent.getInt("rowid"));
			Intent noteslistact=new Intent(this,MainActivity.class);
			noteslistact.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
			noteslistact.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(noteslistact);
			break;
		}
		return super.onOptionsItemSelected(item);
	}
	private final TextWatcher subLengthWatcher = new TextWatcher() {
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {
		}

		public void onTextChanged(CharSequence s, int start, int before, int count) {
			//This sets a textview to the current length

			subcount.setText(String.valueOf(25-s.length()));
		}

		public void afterTextChanged(Editable s) {
		}
	};
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if(v==findViewById(R.id.cancel))
		{
			Intent noteslistact=new Intent(this,MainActivity.class);
			noteslistact.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
			noteslistact.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(noteslistact);
		}
		else if(v==findViewById(R.id.done))
		{

			if(sub.getText().toString().length()==0 )
			{
				sub.setError("Please Enter Subject..");
			}
			else if(notes.getText().toString().length()==0)
			{
				notes.setError("Please Enter Notes..");
			}
			else
			{
				if(mode.equalsIgnoreCase("create"))
				{
					/*Bitmap bitmap=((BitmapDrawable)camera.getDrawable()).getBitmap();
				ByteArrayOutputStream stream=new ByteArrayOutputStream();
				bitmap.compress(Bitmap.CompressFormat.PNG, 90, stream);
				int nh = (int) ( bitmap.getHeight() * (512.0 / bitmap.getWidth()) );
				Bitmap scaled = Bitmap.createScaledBitmap(bitmap, 512, nh, true);
				byte[] image=scaled.toString().getBytes();*/
					db =new DBAdapter(this);
					db.open();
					Long id=db.insertNotes(sub.getText().toString(), notes.getText().toString(), 
							date.getText().toString(),time.getText().toString(),
							picturePath,null);
					System.out.println(" rows inserted "+id+" id value");
					db.close();
					Intent noteslistact=new Intent(this,MainActivity.class);
					noteslistact.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
					noteslistact.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(noteslistact);
				}
				else
				{
					/*Bitmap bitmap=((BitmapDrawable)camera.getDrawable()).getBitmap();
				ByteArrayOutputStream stream=new ByteArrayOutputStream();
				bitmap.compress(Bitmap.CompressFormat.PNG, 90, stream);
				byte[] image=stream.toByteArray();*/
					db=new DBAdapter(this);
					db.open();
					System.out.println("picture path goin for edit "+picturePath);
					db.editRecord(intent.getInt("rowid"), sub.getText().toString(), notes.getText().toString(),picturePath,null);
					db.close();
					Intent noteslistact=new Intent(this,MainActivity.class);
					noteslistact.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
					noteslistact.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(noteslistact);
				}
			}

		}
		else
		{
			/*Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
			startActivityForResult(intent, 0);*/
			final CharSequence[] options = { "Take Photo", "Choose from Gallery","Cancel" };

			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("Add Photo!");
			builder.setItems(options, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int item) {
					if (options[item].equals("Take Photo"))
					{
						Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
						File f = new File(android.os.Environment.getExternalStorageDirectory(), "temp.jpg");
						intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
						startActivityForResult(intent, 1);
					}
					else if (options[item].equals("Choose from Gallery"))
					{
						Intent intent = new   Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
						startActivityForResult(intent, 2);

					}
					else if (options[item].equals("Cancel")) {
						dialog.dismiss();
					}
				}
			});
			builder.show();
		}
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			if (requestCode == 1) {
				File f = new File(Environment.getExternalStorageDirectory().toString());
				for (File temp : f.listFiles()) {
					if (temp.getName().equals("temp.jpg")) {
						f = temp;
						break;
					}
				}
				try {
					Bitmap bitmap;
					BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
					bitmapOptions.inPreferredConfig = Bitmap.Config.RGB_565;
					bitmap = BitmapFactory.decodeFile(f.getAbsolutePath(),bitmapOptions); 

					int nh = (int) ( bitmap.getHeight() * (512.0 / bitmap.getWidth()) );
					Bitmap scaled = Bitmap.createScaledBitmap(bitmap, 512, nh, true);
					camera.setImageBitmap(scaled);

					//String path = android.os.Environment.getExternalStorageDirectory().getAbsolutePath()+ File.separator+ "Phoenix" + File.separator + "default";
					///storage/emulated/0/DCIM/Camera/
					String path = android.os.Environment.getExternalStorageDirectory().getAbsolutePath()+ File.separator+ "DCIM" + File.separator + "Camera";
					f.delete();
					System.out.println("path in cam before "+path);
					OutputStream outFile = null;
					File file1 = new File(path);
					if (!file1.exists()) {
						file1.mkdir();
					}
					String filename=path+File.separator+String.valueOf(System.currentTimeMillis()) + ".jpg";
					try {
						outFile = new FileOutputStream(filename);
						picturePath=path+File.separator+filename;
						System.out.println("pic path in cam "+picturePath);
						bitmap.compress(Bitmap.CompressFormat.JPEG, 85, outFile);
						System.out.println("im here  ");
						outFile.flush();
						outFile.close();
					}  catch (Exception e) {
						e.printStackTrace();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else if (requestCode == 2) {

				Uri selectedImage = data.getData();
				String[] filePath = { MediaStore.Images.Media.DATA };
				Cursor c = getContentResolver().query(selectedImage,filePath, null, null, null);
				c.moveToFirst();
				int columnIndex = c.getColumnIndex(filePath[0]);
				picturePath = c.getString(columnIndex);
				System.out.println(" path of pic is in gallery "+picturePath);
				c.close();
				Bitmap thumbnail = (BitmapFactory.decodeFile(picturePath));
				int nh = (int) ( thumbnail.getHeight() * (512.0 / thumbnail.getWidth()) );
				Bitmap scaled = Bitmap.createScaledBitmap(thumbnail, 512, nh, true);
				//iv.setImageBitmap(scaled);
				camera.setImageBitmap(scaled);
			}
		}
	}   
}
