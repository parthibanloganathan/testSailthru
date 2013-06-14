package com.sailthru.test;

import java.io.IOException;
import java.util.concurrent.ConcurrentLinkedQueue;

import sailthru.Event.STEvent;
import sailthru.Event.SailthruEvent;
import sailthru.Exceptions.InvalidSailthruEventException;
import sailthru.Queuer.STQueue;
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
	public static boolean started = false;
	
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
    public void queue(View v) throws InvalidSailthruEventException, IOException
    {
    	if(started == false)
    	{
    		STQueuer.init(this, "testapp");
    		Logger.setLogTag("testapp");
    		started = true;
    	}
    	
    	Logger.i("Queue iniitally contains " + STQueue.getSize() + " elements.");
    	
    	Logger.i("Adding random number of elements.");
    	for(int i = 0; i < 5 + Math.random()*5; i++)
    	{
    		SailthruEvent stevent = new SailthruEvent.Builder().event("e"+i).build();
    		STQueuer.addEvent(stevent.getSTEvent());
    	}
    	
    	Logger.i("Queue contains " + STQueue.getSize() + " elements.");
    	
    	Logger.i("Dequeueing 3 elements.");
    	
    	ConcurrentLinkedQueue<STEvent> result = STQueuer.getEvents(3);
    	
    	while(result.size() != 0)
    	{
    		Logger.i("Dequeued: " + result.poll().getEvent());
    	}
    	
    	Logger.i("Queue finally contains " + STQueue.getSize() + " elements.");
    	
    	/*
    	// Get elements before queue creation
    	Logger.i("Before insertion, the queue contains " + STQueue.getSize() + " elements.");
    	
    	while(STQueue.getSize() != 2)
    	{
    		Logger.i("Before insertion, queue contains: " + STQueuer.getEvent().getEvent() +". Removing it.");
    	}
    	
    	// Get elements after queue deletion
    	Logger.i("After removal, the queue contains " + STQueue.getSize() + " elements.");
    	
    	// Add elements
    	ConcurrentLinkedQueue<STEvent> list = new ConcurrentLinkedQueue<STEvent>();
    	
    	for(int i = 1; i < 9; i++)
    	{
    		SailthruEvent stevent = new SailthruEvent.Builder().event("e"+i).build();
    		list.add(stevent.getSTEvent());
    	}
    	
    	STQueuer.addEvents(list);
    	
    	// Get elements after queue creation
    	Logger.i("After insertion, the queue contains " + STQueue.getSize() + " elements.");
    	*/
    	
    	/*
    	// Get elements before queue creation
    	Logger.i("Before insertion, the queue contains " + STQueue.getSize() + " elements.");
    	
    	while(STQueue.getSize() != 0)
    	{
    		if(STQueue.queue.peek() != null)
    		{
    			Logger.i("Before insertion, queue contains: " + STQueue.queue.peek().getEvent() +". Removing it.");
    			STQueue.queue.remove();
    		}
    		else
    		{
    			Logger.e("Invalid element in queue");
    		}
    	}
    	
    	// Get elements after queue deletion
    	Logger.i("After removal, the queue contains " + STQueue.getSize() + " elements.");
    	
    	// Add elements
    	ConcurrentLinkedQueue<STEvent> list = new ConcurrentLinkedQueue<STEvent>();
    	
    	for(int i = 1; i < 4; i++)
    	{
    		SailthruEvent stevent = new SailthruEvent.Builder().event("e"+i).build();
    		list.add(stevent.getSTEvent());
    	}
    	
    	STQueuer.addEvents(list);
    	
    	// Get elements after queue creation
    	Logger.i("After insertion, the queue contains " + STQueue.getSize() + " elements.");
    	
    	*/
    }
}