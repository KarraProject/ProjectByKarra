package com.example.thinknote;

import java.util.ArrayList;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class NotesAdapter extends BaseAdapter {

	private Context context;
	 private LayoutInflater mInflater;
	private ArrayList<String> values=new ArrayList<String>();
	public  ArrayList<String> notescopy;
	DBAdapter db;

	public NotesAdapter(Context context, ArrayList<String> values) {
		super();
		this.context = context;
		this.values = values;
		notescopy=new ArrayList<String>();
		this.notescopy.addAll(values);
		mInflater = LayoutInflater.from(context);
	}
	public class Viewholder
	{
		ImageView img;
		TextView date ;
		TextView time;
		TextView notessub;
	}
	@Override
	public View getView(int position, View view, ViewGroup parent) {
		final Viewholder holder;
		
		if (view == null) {
			holder=new Viewholder();
			 view = mInflater.inflate(R.layout.listitem, null);
			
			 holder.img = (ImageView) view.findViewById(R.id.imageView1);
			 holder.date = (TextView) view.findViewById(R.id.date);
			 holder.time = (TextView) view.findViewById(R.id.time);
			 holder.notessub = (TextView) view.findViewById(R.id.notestxt);
			view.setTag(holder);
			} 
		else 
		{
			holder = (Viewholder) view.getTag();
		 }
		final String[] data = values.get(position).split(";");
		 holder.notessub.setText(data[1]);
		 holder.date.setText(data[3]);
			 holder.time.setText(data[4]);
			 /*Bitmap bitmapOrg = BitmapFactory.decodeByteArray(data[5].getBytes(), 0, data[5].getBytes().length);
			 int width = bitmapOrg.getWidth();
			 int height = bitmapOrg.getHeight();
		        int newWidth = holder.img.getLayoutParams().width; //this should be parent's whdth later
		        int newHeight = holder.img.getLayoutParams().height;

		        // calculate the scale
		        float scaleWidth = ((float) newWidth) / width;
		        float scaleHeight = ((float) newHeight) / height;

		        // create a matrix for the manipulation
		        Matrix matrix = new Matrix();
		        // resize the bit map
		        matrix.postScale(scaleWidth, scaleHeight);

		        // recreate the new Bitmap
		        Bitmap resizedBitmap = Bitmap.createBitmap(bitmapOrg, 0, 0, width, height, matrix, true); 
			 
			 holder.img.setImageBitmap(resizedBitmap);*/
		holder.img.setImageResource(R.drawable.memo);
			view.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Intent editintent=new Intent(context,CreatNote.class);
					editintent.putExtra("mode", "edit");
					
					db=new DBAdapter(context);
					db.open();
					Cursor c=db.fetchRecord(Integer.parseInt(data[0]));
					if(c.getCount()!=0)
					{
						c.moveToFirst();
						editintent.putExtra("rowid",c.getInt(0));
						editintent.putExtra("sub",c.getString(1));
						editintent.putExtra("notes",c.getString(2));
						editintent.putExtra("date",c.getString(3));
						editintent.putExtra("time",c.getString(4));
						editintent.putExtra("img", c.getString(5));
						editintent.putExtra("loc", c.getString(6));
						c.close();
					}
					context.startActivity(editintent);
				}
			});
		return view;
	}
	public void filter(String charText) 
	{
		values.clear();
		if (charText.length() == 0) 
		{
			values.addAll(notescopy);
		} 
		else
		{
			for (int i = 0; i <notescopy.size(); i++)
			{
				String record[]=notescopy.get(i).split(";");
				if (record[1].contains(charText)||record[2].contains(charText)) 
				{
					values.add(notescopy.get(i));
				}
			}
		}
		
		if( notescopy.size() == 0) {
			Toast.makeText(context, "No records matching the filter",Toast.LENGTH_SHORT).show();
		}
		if(values.size()==0)
		{
			
		}
		notifyDataSetChanged();
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return values.size();
	}
	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return values.get(position);
	}
	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}
}

