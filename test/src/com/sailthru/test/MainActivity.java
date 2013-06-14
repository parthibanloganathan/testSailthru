package com.sailthru.test;

import java.util.concurrent.ConcurrentLinkedQueue;

import sailthru.Event.STEvent;
import sailthru.Event.SailthruEvent;
import sailthru.Exceptions.InvalidSailthruEventException;
import sailthru.Queuer.STQueuer;
import sailthru.Utilities.Logger;

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
    
    // Queue Unit Test
    public void queue(View v) throws InvalidSailthruEventException
    {
    	// Get elements before queue creation
    	ConcurrentLinkedQueue<STEvent> queue = STQueuer.getEvents(10);
    	
    	if(queue.size() == 0)
    	{
    		Logger.i("Before insertion, queue is empty.");
    	}
    	
    	for(int i = 0; i < queue.size(); i++)
    	{
    		if(queue.peek() != null)
    		{
    			Logger.i("Before insertion, queue contains: " + queue.poll());
    		}
    		else
    		{
    			Logger.e("Invalid element in queue");
    		}
    	}
    	
    	// Add elements
    	ConcurrentLinkedQueue<STEvent> list = new ConcurrentLinkedQueue<STEvent>();
    	
    	for(int i = 0; i < 10; i++)
    	{
    		SailthruEvent stevent = new SailthruEvent.Builder().tag("t"+i).event("e"+i).build();
    		list.add(stevent.getSTEvent());
    	}
    	
    	STQueuer.addEvents(list);
    }
}