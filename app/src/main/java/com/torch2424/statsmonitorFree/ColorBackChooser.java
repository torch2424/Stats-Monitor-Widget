package com.torch2424.statsmonitorFree;

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

public class ColorBackChooser extends Activity 
{

	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.color_back_chooser);
		//get listview
				final ListView listView = (ListView) findViewById(R.id.listViewBack);
				//create string array with which colors I want, can add more colors by finding their hex values
				String [] colorArray = {"100% Transparent", "75% Transparent", "50% Transparent",
						"25% Transparent", "Black", "Blue", "Dark Gray", "Gray", 
						"Green", "Light Gray", "Red", "White", "Yellow", "Magenta", "Cyan", 
						"Pink", "Purple", "Orange", "Maroon", "Gold", 
						"Forest Green", "Turquoise", "Sky Blue", "Indigo", "Dark Violet", 
						"Chocolate", "Slate Gray"};
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
				    	 int backColor = Color.argb(128, 00, 00, 00);
				    	 //creating preferences
				    	 SharedPreferences prefs = getSharedPreferences("MyPrefs", 0);
				 		Editor editor = prefs.edit();
				 		//series of ifs to check for which color is selected
				 		//semi transparent first to stop transparent from going
				 		if(selectedColor.contains("50%") == true)
				 		{
				 			backColor = Color.argb(128, 00, 00, 00);
				 		}
				 		else if(selectedColor.contains("25%") == true)
				 		{
				 			backColor = Color.argb(64, 00, 00, 00);
				 		}
				 		else if(selectedColor.contains("75%") == true)
				 		{
				 			backColor = Color.argb(192, 00, 00, 00);
				 		}
				 		else if(selectedColor.contains("100%") == true)
				 		{
				 			backColor = Color.TRANSPARENT;
				 		}
				 		else if(selectedColor.contains("Black") == true)
				 		{
				 			backColor = Color.BLACK;
				 		}
				 		else if (selectedColor.contains("Blue") == true )
				 		{
				 			backColor = Color.BLUE;
				 		}
				 		else if (selectedColor.contains("Dark Gray") == true )
				 		{
				 			backColor = Color.DKGRAY;
				 		}
				 		else if (selectedColor.contains("Gray") == true )
				 		{
				 			backColor = Color.GRAY;
				 		}
				 		else if (selectedColor.contains("Light Gray") == true )
				 		{
				 			backColor = Color.LTGRAY;
				 		}
				 		else if (selectedColor.contains("Red") == true )
				 		{
				 			backColor = Color.RED;
				 		}
				 		else if (selectedColor.contains("White") == true )
				 		{
				 			backColor = Color.WHITE;
				 		}
				 		else if (selectedColor.contains("Yellow") == true )
				 		{
				 			backColor = Color.YELLOW;
				 		}
				 		else if (selectedColor.contains("Green") == true )
				 		{
				 			backColor = Color.GREEN;
				 		}
				 		else if (selectedColor.contains("Magenta") == true )
				 		{
				 			backColor = Color.MAGENTA;
				 		}
				 		else if (selectedColor.contains("Cyan") == true )
				 		{
				 			backColor = Color.CYAN;
				 		}
				 		else if (selectedColor.contains("Pink") == true )
				 		{
				 			backColor = Color.rgb(255,0 , 255);
				 		}
				 		else if (selectedColor.contains("Purple") == true )
				 		{
				 			backColor = Color.rgb(128,57 , 123);
				 		}
				 		else if (selectedColor.contains("Orange") == true )
				 		{
				 			backColor = Color.rgb(255,128 , 0);
				 		}
				 		else if (selectedColor.contains("Maroon") == true )
				 		{
				 			backColor = Color.rgb(128,0 , 0);
				 		}
				 		else if (selectedColor.contains("Gold") == true )
				 		{
				 			backColor = Color.rgb(255,215,0);
				 		}
				 		else if (selectedColor.contains("Forest Green") == true )
				 		{
				 			backColor = Color.rgb(34,139,34);
				 		}
				 		else if (selectedColor.contains("Turquoise") == true )
				 		{
				 			backColor = Color.rgb(64,224,208);
				 		}
				 		else if (selectedColor.contains("Sky Blue") == true )
				 		{
				 			backColor = Color.rgb(135,206,235);
				 		}
				 		else if (selectedColor.contains("Indigo") == true )
				 		{
				 			backColor = Color.rgb(75,0,130);
				 		}
				 		else if (selectedColor.contains("Dark Violet") == true )
				 		{
				 			backColor = Color.rgb(148,0,211);
				 		}
				 		else if (selectedColor.contains("Chocolate") == true )
				 		{
				 			backColor = Color.rgb(210,105,30);
				 		}
				 		else if (selectedColor.contains("Slate Gray") == true )
				 		{
				 			backColor = Color.rgb(112,128,144);
				 		}
				 		editor.putInt("BACKCOLOR", backColor);
				 		editor.commit();
				 		finish();
				    }
				};

				listView.setOnItemClickListener(listclick); 
			}
}
