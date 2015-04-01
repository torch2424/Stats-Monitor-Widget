package com.torch2424.simplemonitor;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.torch2424.statsmonitorwidget.R;

public class ColorChooser extends Activity 
{
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.color_chooser);
		//get listview
		final ListView listView = (ListView) findViewById(R.id.listView);
		//create string array with which colors I want, can add more colors by finding their hex values
		String [] colorArray = {"Black", "Blue", "Dark Gray", "Gray", "Green", "Light Gray", "Red", 
				"White", "Yellow", "Magenta", "Cyan", "Pink", "Purple", "Orange", "Maroon", "Gold", 
				"Forest Green", "Turquoise", "Sky Blue", "Indigo", "Dark Violet", "Chocolate", "Slate Gray"};
		//creating array adapter for listview
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, colorArray);		
		listView.setAdapter(adapter);
		
		OnItemClickListener listclick = new OnItemClickListener() 
		{
		    public void onItemClick(AdapterView<?> parent, View v, int position, long id) 
		    {
		    	//getting the selected color
		    	 String  selectedColor  = (String) listView.getItemAtPosition(position);
		    	 //create color
		    	 int textColor = Color.LTGRAY;
		    	 //creating preferences
		    	 SharedPreferences prefs = getSharedPreferences("MyPrefs", 0);
		 		Editor editor = prefs.edit();
		 		//series of ifs to check for which color is selected
		 		if(selectedColor.contains("Black") == true)
		 		{
		 			textColor = Color.BLACK;
		 		}
		 		else if (selectedColor.contains("Blue") == true )
		 		{
		 			textColor = Color.BLUE;
		 		}
		 		else if (selectedColor.contains("Dark Gray") == true )
		 		{
		 			textColor = Color.DKGRAY;
		 		}
		 		else if (selectedColor.contains("Gray") == true )
		 		{
		 			textColor = Color.GRAY;
		 		}
		 		else if (selectedColor.contains("Light Gray") == true )
		 		{
		 			textColor = Color.LTGRAY;
		 		}
		 		else if (selectedColor.contains("Red") == true )
		 		{
		 			textColor = Color.RED;
		 		}
		 		else if (selectedColor.contains("White") == true )
		 		{
		 			textColor = Color.WHITE;
		 		}
		 		else if (selectedColor.contains("Yellow") == true )
		 		{
		 			textColor = Color.YELLOW;
		 		}
		 		else if (selectedColor.contains("Green") == true )
		 		{
		 			textColor = Color.GREEN;
		 		}
		 		else if (selectedColor.contains("Magenta") == true )
		 		{
		 			textColor = Color.MAGENTA;
		 		}
		 		else if (selectedColor.contains("Cyan") == true )
		 		{
		 			textColor = Color.CYAN;
		 		}
		 		else if (selectedColor.contains("Pink") == true )
		 		{
		 			textColor = Color.rgb(255,0 , 255);
		 		}
		 		else if (selectedColor.contains("Purple") == true )
		 		{
		 			textColor = Color.rgb(128,57 , 123);
		 		}
		 		else if (selectedColor.contains("Orange") == true )
		 		{
		 			textColor = Color.rgb(255,128 , 0);
		 		}
		 		else if (selectedColor.contains("Maroon") == true )
		 		{
		 			textColor = Color.rgb(128,0 , 0);
		 		}
		 		else if (selectedColor.contains("Gold") == true )
		 		{
		 			textColor = Color.rgb(255,215,0);
		 		}
		 		else if (selectedColor.contains("Forest Green") == true )
		 		{
		 			textColor = Color.rgb(34,139,34);
		 		}
		 		else if (selectedColor.contains("Turquoise") == true )
		 		{
		 			textColor = Color.rgb(64,224,208);
		 		}
		 		else if (selectedColor.contains("Sky Blue") == true )
		 		{
		 			textColor = Color.rgb(135,206,235);
		 		}
		 		else if (selectedColor.contains("Indigo") == true )
		 		{
		 			textColor = Color.rgb(75,0,130);
		 		}
		 		else if (selectedColor.contains("Dark Violet") == true )
		 		{
		 			textColor = Color.rgb(148,0,211);
		 		}
		 		else if (selectedColor.contains("Chocolate") == true )
		 		{
		 			textColor = Color.rgb(210,105,30);
		 		}
		 		else if (selectedColor.contains("Slate Gray") == true )
		 		{
		 			textColor = Color.rgb(112,128,144);
		 		}
		 		editor.putInt("TEXTCOLOR", textColor);
		 		editor.commit();
		 		finish();
		    }
		};

		listView.setOnItemClickListener(listclick); 
	}
	

}
