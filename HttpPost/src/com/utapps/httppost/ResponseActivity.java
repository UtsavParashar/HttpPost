package com.utapps.httppost;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.app.ListActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;

/*
 * ListActivity to get the data from MainActivity(HttpPostActivity)
 * and display the data in a listView rendering in a row.
 */
public class ResponseActivity extends ListActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		savedInstanceState = getIntent().getExtras();
		if (savedInstanceState != null) {
			String response = savedInstanceState.getString("KEY");
			List<String> myList = new ArrayList<String>(
					Arrays.asList(response.split(",")));
			setListAdapter(new ArrayAdapter<String>(ResponseActivity.this,
					android.R.layout.simple_list_item_1, myList));
		}
	}
}
