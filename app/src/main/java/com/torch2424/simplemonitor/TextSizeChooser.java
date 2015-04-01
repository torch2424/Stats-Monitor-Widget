package com.torch2424.simplemonitor;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.torch2424.statsmonitorwidget.R;

public class TextSizeChooser extends Activity
{
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.text_size_chooser);
		//get listview
				final ListView listView = (ListView) findViewById(R.id.listView);
				//create string array with which colors I want, can add more colors by finding their hex values
				String [] colorArray = {"(Default) Title: 18 Text: 12", "Title: 12 Text: 6", "Title: 14 Text: 8", "Title: 16 Text: 10", "Title: 20 Text: 14", "Title: 22 Text: 16", "Title: 24 Text: 18", "Title: 26 Text: 20", "Title: 28 Text: 22"};
				//creating array adapter for listview
				ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, colorArray);		
				listView.setAdapter(adapter);
				
				OnItemClickListener listclick = new OnItemClickListener() 
				{
				    public void onItemClick(AdapterView<?> parent, View v, int position, long id) 
				    {
				    	//getting the selected color
				    	 String  selectedSize = (String) listView.getItemAtPosition(position);
				    	 //create color
				    	 int textTitleSize = 18;
				    	 int textSize = 12;
				    	 //creating preferences
				    	 SharedPreferences prefs = getSharedPreferences("MyPrefs", 0);
				 		Editor editor = prefs.edit();
				 		//series of ifs to check for which size is selected
				 		if(selectedSize.contains("Title: 12 Text: 6") == true)
				 		{
				 			textTitleSize = 12;
				 			textSize = 6;
				 		}
				 		else if(selectedSize.contains("Title: 14 Text: 8") == true)
				 		{
				 			textTitleSize = 14;
				 			textSize = 8;
				 		}
				 		else if(selectedSize.contains("Title: 16 Text: 10") == true)
				 		{
				 			textTitleSize = 16;
				 			textSize = 10;
				 		}
				 		else if(selectedSize.contains("(Default) Title: 18 Text: 12") == true)
				 		{
				 			textTitleSize = 18;
				 			textSize = 12;
				 		}
				 		else if(selectedSize.contains("Title: 20 Text: 14") == true)
				 		{
				 			textTitleSize = 20;
				 			textSize = 14;
				 		}
				 		else if(selectedSize.contains("Title: 22 Text: 16") == true)
				 		{
				 			textTitleSize = 22;
				 			textSize = 16;
				 		}
				 		else if(selectedSize.contains("Title: 24 Text: 18") == true)
				 		{
				 			textTitleSize = 16;
				 			textSize = 10;
				 		}
				 		else if(selectedSize.contains("Title: 26 Text: 20") == true)
				 		{
				 			textTitleSize = 26;
				 			textSize = 20;
				 		}
				 		else if(selectedSize.contains("Title: 28 Text: 22") == true)
				 		{
				 			textTitleSize = 28;
				 			textSize = 22;
				 		}
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
