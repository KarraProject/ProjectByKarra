package com.example.thinknote;

import java.util.ArrayList;
import android.os.Bundle;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

public class MainActivity extends Activity {
ListView notesList;
String[] menuItems = {"Edit","Delete"};
public ArrayList<String> notesarray=new ArrayList<String>();

EditText search;
DBAdapter db;
Cursor c;
NotesAdapter notesadp;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		search=(EditText)findViewById(R.id.search);
		
		notesList=(ListView)findViewById(R.id.noteslist);
		populateList("nosort");
		
		search.addTextChangedListener(new TextWatcher() {
			@Override
			public void afterTextChanged(Editable arg0) {
				//notesadp=new NotesAdapter(MainActivity.this, notesarray);
				// TODO Auto-generated method stub
				String text = search.getText().toString();
				notesadp.filter(text);
				
			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,
					int arg2, int arg3) {
				// TODO Auto-generated method stub
			}

			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {
				// TODO Auto-generated method stub
			}
		});
		getActionBar().setDisplayHomeAsUpEnabled(true);
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(false);
		actionBar.setDisplayShowHomeEnabled(true);
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setTitle("  Think Note+");
		actionBar.setDisplayUseLogoEnabled(true);
		
	}

	private void populateList(String sortspec) {
		// TODO Auto-generated method stub
		
		db=new DBAdapter(this);
		db.open();
		
		c=db.fetchNotesList(sortspec);
		if(c.getCount()!=0)
		{
			c.moveToFirst();
			do
			{
				notesarray.add(c.getString(0)+";"+c.getString(1)+";"+c.getString(2)+";"+c.getString(3)+";"+c.getString(4)+";"+
								c.getString(5)+";"+c.getString(6));
			}
			while(c.moveToNext());
		}		
		c.close();
		db.close();
		notesadp=new NotesAdapter(this, notesarray);
		notesList.setAdapter(notesadp);		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {

		case R.id.createnote:
			Intent create=new Intent(this,CreatNote.class);
			create.putExtra("mode", "create");
			startActivity(create);
			break;
		case R.id.sortsubject:
			notesarray.clear();
			populateList("sub");
			break;
		case R.id.sortdate:
			notesarray.clear();
			populateList("date");
			break;
		}
		return super.onOptionsItemSelected(item);
	}
	@Override
	public void onContentChanged() {
		super.onContentChanged();

		View empty = findViewById(R.id.empty);
		ListView list = (ListView) findViewById(R.id.noteslist);
		list.setEmptyView(empty);
	}
	
}
