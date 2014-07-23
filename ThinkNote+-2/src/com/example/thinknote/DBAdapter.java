package com.example.thinknote;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase.CursorFactory;

public class DBAdapter {
		private static final String DB_NAME="NotesDB";
		private static final String DB_TABLE="Notes_Table"; 
		private static final int DB_VERSION=15;
		private static final String[] COL_NAMES={"_id","Title","Notes","NotesDate","NotesTime","NotesImage","NoteLocation"};
		
		//private static final String KEY_ID="_id";*
		private static final String CREATE_DBTABLE="create table "+DB_TABLE+" ("
													+COL_NAMES[0]+" integer primary key autoincrement ,"
													+COL_NAMES[1]+" text ,"
													+COL_NAMES[2]+" text ,"
													+COL_NAMES[3]+" text ,"
													+COL_NAMES[4]+" text ," 
													+COL_NAMES[5]+" text ,"
													+COL_NAMES[6]+" text );"; 
		private SQLiteDatabase  database;
		private MyDBHelper helper;
		public  Context context;
		private int version=4;
		
		public DBAdapter(Context context)
		{
			this.context=context;
		}

		public DBAdapter open()
		{
			helper=new MyDBHelper(this.context, DB_NAME, null, DB_VERSION);
			database=helper.getWritableDatabase();
			return this;
		}

		public void close() {
			helper.close();
			database.close();
		}
		public Long insertNotes(String sub,String notes_txt,String notedate,String notetime,String img,String location)
		{
			ContentValues contentValues=new ContentValues();
			contentValues.put(COL_NAMES[1],sub);
			contentValues.put(COL_NAMES[2],notes_txt);
			contentValues.put(COL_NAMES[3],notedate);
			contentValues.put(COL_NAMES[4],notetime);
			contentValues.put(COL_NAMES[5],img);
			contentValues.put(COL_NAMES[6],location);
			return database.insert(DB_TABLE,null,contentValues);
		}
		public Cursor fetchNotesList(String sortspec)
		{
			Cursor c;
			if(sortspec.equalsIgnoreCase("nosort"))
			{
				String orderBySpec=COL_NAMES[3] +" DESC, "+COL_NAMES[4]+" DESC ";
				 c=database.query(DB_TABLE, null, null, null, null, null, orderBySpec);
			 //sc=database.query(DB_TABLE, null, null, null, null, null, null);
			}
			else if(sortspec.equalsIgnoreCase("sub"))
			{
				 c=database.query(DB_TABLE, null, null, null, null, null, COL_NAMES[1]);
			}
			else
			{ 
				String orderBySpec=COL_NAMES[3] +" ASC, "+COL_NAMES[4]+" ASC ";
				 c=database.query(DB_TABLE, null, null, null, null, null, orderBySpec);
			}
			return c;
		}
		public Cursor fetchRecord(int rowid)
		{
			Cursor c=database.query(DB_TABLE, null, COL_NAMES[0]+" = "+rowid, null, null, null, null);
			return c;
		}
		public int editRecord(int rowid,String sub,String notes,String img,String loc)
		{
			ContentValues contentValues=new ContentValues();
			contentValues.put(COL_NAMES[1],sub);
			contentValues.put(COL_NAMES[2],notes);
			contentValues.put(COL_NAMES[5],img);
			contentValues.put(COL_NAMES[6],loc);
			return database.update(DB_TABLE, contentValues, COL_NAMES[0]+" = "+rowid, null);
		}
		public int deleteRecord(int rowid)
		{
			return database.delete(DB_TABLE, COL_NAMES[0]+" = "+rowid, null);
		}
		private static class MyDBHelper extends SQLiteOpenHelper {
			public MyDBHelper(Context context,String name,CursorFactory cursorFactory, int version)
			{
				super(context, name, cursorFactory, version);
			}

			@Override
			public void onCreate(SQLiteDatabase db) {
				// TODO Auto-generated method stub
				db.execSQL(CREATE_DBTABLE);
			}

			@Override
			public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
				// TODO Auto-generated method stub
				db.execSQL("DROP TABLE IF EXISTS "+DB_TABLE);
				onCreate(db);
			}
		}
}
