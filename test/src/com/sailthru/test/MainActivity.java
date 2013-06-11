package com.sailthru.test;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.widget.Toast;


public class MainActivity extends Activity {
	
	public static String apid = "nil";
	public static String mid = "nil";
	
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
       
        return true;
    }
    
    public void logo(View v)
    {
    	Toast.makeText(getApplicationContext(), "Logo pressed", Toast.LENGTH_SHORT).show();
    }
    
    public void whiteButton(View v)
    {
    	Toast.makeText(getApplicationContext(), "White button presssed", Toast.LENGTH_SHORT).show();
    	
        Toast.makeText(getApplicationContext(), "APID: " + apid, Toast.LENGTH_SHORT).show();
        Toast.makeText(getApplicationContext(), "MID: " + mid, Toast.LENGTH_SHORT).show();
    }
}
