package com.torch2424.statsmonitor;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.torch2424.statsmonitorwidget.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextSizeChooser extends AppCompatActivity
{
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.text_size_chooser);
		//get our action bar
		getSupportActionBar().show();

		//get listview
				final ListView listView = (ListView) findViewById(R.id.listView);
				//create string array with which colors I want, can add more colors by finding their hex values
				String [] colorArray = {"Title: 12 Text: 6", "Title: 13 Text: 7",
                        "Title: 14 Text: 8", "Title: 15 Text: 9",
                        "Title: 16 Text: 10", "Title: 17 Text: 11",
                        "(Default) Title: 18 Text: 12", "Title: 19 Text: 13",
                        "Title: 20 Text: 14", "Title: 21 Text: 15",
                        "Title: 22 Text: 16", "Title: 23 Text: 17",
                        "Title: 24 Text: 18", "Title: 25 Text: 19",
                        "Title: 26 Text: 20", "Title: 27 Text: 21",
                        "Title: 28 Text: 22"};
				//creating array adapter for listview
				ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, colorArray);		
				listView.setAdapter(adapter);
				
				OnItemClickListener listclick = new OnItemClickListener() 
				{
				    public void onItemClick(AdapterView<?> parent, View v, int position, long id) 
				    {

                        //getting the selected color
				    	 String  selectedSize = (String) listView.getItemAtPosition(position);

				    	 //Create default
				    	 int textTitleSize = 18;
				    	 int textSize = 12;

				    	 //creating preferences
				    	 SharedPreferences prefs = getSharedPreferences("MyPrefs", 0);
				 		Editor editor = prefs.edit();

                        //Parse the integers from the strings
                        Pattern p = Pattern.compile("\\d+");
                        Matcher m = p.matcher(selectedSize);
                        List<String> matches = new ArrayList<String>();
                        while(m.find()) {
                            matches.add(m.group());
                        }

                        textTitleSize = Integer.parseInt(matches.get(0));
                        textSize = Integer.parseInt(matches.get(1));

				 		//put into prefs
				 		editor.putInt("TEXTTITLESIZE", textTitleSize);
				 		editor.putInt("TEXTSIZE", textSize);
				 		editor.commit();
				 		finish();
				 		
				    }
				};
				
				listView.setOnItemClickListener(listclick); 
		
	}
}
