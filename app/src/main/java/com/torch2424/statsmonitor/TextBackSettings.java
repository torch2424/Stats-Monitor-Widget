package com.torch2424.statsmonitor;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.torch2424.statsmonitorwidget.R;

public class TextBackSettings extends AppCompatActivity
{
	public void launchSize()
	{
		Intent size = new Intent(this, TextSizeChooser.class);
		 startActivity(size);
	}
	
	public void launchTextColor()
	{
		Intent size = new Intent(this, ColorChooser.class);
		 startActivity(size);
	}
	
	public void launchBackColor()
	{
		Intent size = new Intent(this, ColorBackChooser.class);
		 startActivity(size);
	}
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.text_back_settings);
		//get our action bar
		getSupportActionBar().show();

		//get listview
		final ListView listView = (ListView) findViewById(R.id.listView);
		//create string array with which colors I want, can add more colors by finding their hex values
		String [] settingsArray = {"Choose Text Size", "Choose Text Color", "Choose Background Color"};
		//creating array adapter for listview
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, settingsArray);		
		listView.setAdapter(adapter);
		
		OnItemClickListener listclick = new OnItemClickListener() 
		{
		    public void onItemClick(AdapterView<?> parent, View v, int position, long id) 
		    {
		    	//getting the selected setting
		    	 String  selectedSetting = (String) listView.getItemAtPosition(position);
		    	 if(selectedSetting.contains("Choose Text Size"))
		    	{
		    		 launchSize();
		    	}
		    	 else if(selectedSetting.contains("Choose Text Color"))
		    	{
		    		 launchTextColor();
		    	}
		    	 else if(selectedSetting.contains("Choose Background Color"))
			    {
		    		 launchBackColor();
			    }
		    }
		};
		
		listView.setOnItemClickListener(listclick); 
		
	}
}
