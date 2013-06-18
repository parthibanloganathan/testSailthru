package com.sailthru.test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.concurrent.ConcurrentLinkedQueue;

import sailthru.Event.STEvent;
import sailthru.Event.SailthruEvent;
import sailthru.Exceptions.InvalidLocationException;
import sailthru.Exceptions.InvalidSailthruEventException;
import sailthru.Queuer.STQueue;
import sailthru.Queuer.STQueuer;
import sailthru.Sender.STSender;
import sailthru.Utilities.Logger;
import sailthru.Utilities.NetworkManager;

import android.os.Bundle;
import android.app.Activity;
import android.content.res.AssetManager;
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
    
    // Async HTTP Request
    public void request(View v) throws IOException
    {
    	if(started == false)
    	{
    		Logger.setLogTag("testapp");
    		started = true;
    	}
    	
    	NetworkManager.init(this);
    	if(NetworkManager.isNetworkAvailable())
    	{
    		Logger.i("Sending requests");
    		
    		InputStream is = getAssets().open("url_test.txt");
    		BufferedReader br = new BufferedReader(new InputStreamReader(is));
    		String line;
    		int count = 1;
    		while((line = br.readLine()) != null && count < 10) 
    		{
    			Logger.i("count = " + (count++));
    			STSender.testSend(line);
    		}
    		br.close();
    	}
    	else
    	{
    		Logger.e("Network unavailable");
    	}
    }
    
    // Threaded Queue Unit Test
    public void threadedqueue(View v) throws InvalidSailthruEventException, IOException, InvalidLocationException
    {
    	if(started == false)
    	{
    		STQueuer.init(this, "testapp");
    		Logger.setLogTag("testapp");
    		started = true;
    	}
    	
    	//
		// Thread to enqueue events
		new Thread(new Runnable()
		{
			public void run()
			{
		    	Logger.i("Inserting elements.");
		    	
		    	for(int i = 1; i < 1000; i++)
		    	{
		    		SailthruEvent stevent;
					try
					{
						stevent = new SailthruEvent.Builder().event("e"+i).tag("facebook").tag("google").url("www.sailthru.com").location(34, 12).build();
						STQueuer.addEvent(stevent.getSTEvent());
						Logger.i("Added queue has " + STQueuer.getSize() + " elements.");
					}
					catch(Exception e)
					{
						Logger.e("Something went wrong when inserting element.");
					}
		    	}
		    	
		    	//STQueuer.addEvents(list);
			}
		}).start();
		
		
		// Thread to dequeue events
		new Thread(new Runnable()
		{
			public void run()
			{
		    	Logger.i("Removing elements.");
		    	
		    	for(int i = 0; i < 50; i++)
		    	{
		    		STQueuer.getEvents(20);
		    		Logger.i("Removed queue has " + STQueuer.getSize() + " elements.");
		    		
		    		try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
		    	}
			}
		}).start();
		
    }
    
    // Queue Unit Test
    public void queue(View v) throws InvalidSailthruEventException, IOException, InvalidLocationException
    {
    	if(started == false)
    	{
    		STQueuer.init(this, "testapp");
    		Logger.setLogTag("testapp");
    		started = true;
    	}
    	
    	//
    	Logger.i("Inserting elements.");
    	
    	ConcurrentLinkedQueue<STEvent> list = new ConcurrentLinkedQueue<STEvent>();
    	
    	for(int i = 1; i < 1000; i++)
    	{
    		SailthruEvent stevent = new SailthruEvent.Builder().event("e"+i).tag("facebook").tag("google").url("www.sailthru.com").location(34, 12).build();
    		list.add(stevent.getSTEvent());
    	}
    	
    	STQueuer.addEvents(list);
    	
    	Logger.i("Queue finally contains " + STQueue.getSize() + " elements.");
    	Logger.i("Emptying queue.");
    	
    	STQueuer.getEvents(10000);
    	Logger.i("Queue finally contains " + STQueue.getSize() + " elements.");
    	//
    	
    	/*
    	Logger.i("Queue initally contains " + STQueue.getSize() + " elements.");
    	
    	Logger.i("Adding random number of elements.");
    	for(int i = 0; i < 5 + Math.random()*5; i++)
    	{
    		SailthruEvent stevent = new SailthruEvent.Builder().event("e"+i).tag("facebook").tag("google").url("www.sailthru.com").location(34, 12).build();
    		STQueuer.addEvent(stevent.getSTEvent());
    	}
    	
    	Logger.i("Queue contains " + STQueue.getSize() + " elements.");
    	
    	Logger.i("Dequeueing some elements.");
    	
    	ConcurrentLinkedQueue<STEvent> result = STQueuer.getEvents((int) (Math.random()*5));
    	
    	while(result.size() != 0)
    	{
    		Logger.i("Dequeued: " + result.poll().getEvent());
    	}
    	
    	Logger.i("Queue finally contains " + STQueue.getSize() + " elements.");
    	*/
    	
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
    	
    	for(int p = 0; p < 50; p++)
    	{
    		
    	Logger.i("Initially, the queue contains " + STQueue.getSize() + " elements.");

    	// Add elements
    	
    	for(int i = 1; i < 20 + Math.random()*10; i++)
    	{
    		SailthruEvent stevent = new SailthruEvent.Builder().event("e"+i).tag("facebook").tag("google").url("www.sailthru.com").location(34, 12).build();
    		STQueue.queue.add(stevent.getSTEvent());
    	}
    	
    	// Get elements after queue creation
    	Logger.i("After insertion, the queue contains " + STQueue.getSize() + " elements.");
    	
    	// Remove some elements
    	
    	Logger.i("Dequeueing some elements.");
    	
    	final int SIZE = STQueue.getSize();
    	
    	while(STQueue.getSize() > SIZE - 19*Math.random())
    	{
    		if(STQueue.queue.peek() != null)
    		{
    			Logger.i("Dequeued: " + STQueue.queue.peek().getEvent());
    		}
    		else
    		{
    			Logger.e("Invalid element in queue!!!");
    		}
    		
    		STQueue.queue.remove();
    	}
    	
    	// Get elements after queue deletion
    	Logger.i("After removal, the queue contains " + STQueue.getSize() + " elements.");
    	
    	}
    	*/
    }
}