package com.sailthru.test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import sailthru.Event.STEvent;
import sailthru.Event.SailthruEvent;
import sailthru.Exceptions.InvalidLocationException;
import sailthru.Exceptions.InvalidSailthruEventException;
import sailthru.Queuer.STQueue;
import sailthru.Queuer.STQueuer;
import sailthru.Sender.STSender;
import sailthru.Utilities.Logger;
import sailthru.Utilities.NetworkManager;
import sailthru.Utilities.RequestBuilder;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends Activity
{

	public static String apid = "nil";
	public static String mid = "nil";
	public static boolean started = false;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
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

	public void register(View v) throws IOException, NoSuchAlgorithmException
	{
		if(started == false)
		{
			Logger.setLogTag("testapp");
			started = true;

			// Test 1 : Register app to obtain HID

			if(NetworkManager.isNetworkAvailable())
			{
				STSender.registerApp();
			}
		}
	}

	public void sender(View v) throws IOException
	{
		if(started == false)
		{
			NetworkManager.init(this);
			STSender.setBaseUrl("prod-mobile.dannyrosen.net");
			Logger.setLogTag("testapp");
			RequestBuilder.setHID("6ca98d3b1f82eb204c6e506d5afac640515c717a21e070dffacfdd3ebffd899faa912160aa9024e8fb38d685");
			started = true;
		}

		// Test 1 : Queues events on sender

		// Queue from Thread 1
		new Thread(new Runnable()
		{
			public void run()
			{
				ConcurrentLinkedQueue<STEvent> queue = new ConcurrentLinkedQueue<STEvent>();
				for(int j = 0; j < 10; j++)
				{
					for(int i = 0; i < 10; i++)
					{
						SailthruEvent stevent;
						try
						{
							stevent = new SailthruEvent.Builder().event("e"+j+"."+i).build();
							Logger.i("Thread 1 added event to sender: " + stevent.getSTEvent().getEvent());
							queue.add(stevent.getSTEvent());
						}
						catch(Exception e)
						{
							Logger.e("Something went wrong when inserting element.");
						}
					}

					STSender.addEvents(queue);

					try
					{
						Thread.sleep((long) (Math.random()*1000));
					}
					catch(InterruptedException e)
					{
						e.printStackTrace();
					}
				}
			}
		}).start();    	

		// Queue from Thread 2
		new Thread(new Runnable()
		{
			public void run()
			{
				ConcurrentLinkedQueue<STEvent> queue = new ConcurrentLinkedQueue<STEvent>();
				for(int j = 0; j < 10; j++)
				{
					for(int i = 0; i < 10; i++)
					{
						SailthruEvent stevent;
						try
						{
							stevent = new SailthruEvent.Builder().event("e"+j+"."+i).build();
							Logger.i("Thread 2 added event to sender: " + stevent.getSTEvent().getEvent());
							queue.add(stevent.getSTEvent());
						}
						catch(Exception e)
						{
							Logger.e("Something went wrong when inserting element.");
						}
					}

					STSender.addEvents(queue);

					try
					{
						Thread.sleep((long) (Math.random()*1000));
					}
					catch(InterruptedException e)
					{
						e.printStackTrace();
					}
				}
			}
		}).start();    	

		// Send events from scheduler thread
		Runnable runnable = new Runnable()
		{
			public void run()
			{
				Logger.i("Scheduler activated.");
				
				new Thread(new Runnable()
				{
					public void run()
					{
						if(NetworkManager.isNetworkAvailable())
						{
							Logger.i("Network is available");
							if(STSender.hasEvents())
							{
								Logger.i("Sending");
								STSender.sendEvents();
							}
						}
					}
				}).start();
			}
		};
		ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
		scheduler.scheduleAtFixedRate(runnable, 1, 1, TimeUnit.SECONDS);
	}

	// Async HTTP Request
	public void request(View v) throws IOException
	{
		// Test 1 : Send multiple Asynchronous HTTP requests with URLs from url_test.txt

		// Max number of async requests
		final int LIMIT = 50;
		Toast.makeText(getApplicationContext(), "Request test initiated.", Toast.LENGTH_SHORT).show();

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
			while((line = br.readLine()) != null && count < LIMIT) 
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

		// Test 1 : Enqueues and dequeues elements from queue on two separate threads simultaneously.

		Toast.makeText(getApplicationContext(), "Threaded queue test initiated.", Toast.LENGTH_SHORT).show();

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

					try
					{
						Thread.sleep(100);
					}
					catch(InterruptedException e)
					{
						e.printStackTrace();
					}
				}
			}
		}).start();
	}

	// Queue Unit Test
	public void queue(View v) throws InvalidSailthruEventException, IOException, InvalidLocationException
	{
		Toast.makeText(getApplicationContext(), "Queue test initiated.", Toast.LENGTH_SHORT).show();

		if(started == false)
		{
			STQueuer.init(this, "testapp");
			Logger.setLogTag("testapp");
			started = true;
		}

		// Test 1 : Enqueue and dequeue 5000 elements
		Logger.i("Inserting elements.");

		for(int i = 1; i < 1000; i++)
		{
			SailthruEvent stevent = new SailthruEvent.Builder().event("e"+i).tag("facebook").tag("google").url("www.sailthru.com").location(34, 12).build();
			STQueuer.addEvent(stevent.getSTEvent());
		}

		Logger.i("Queue finally contains " + STQueue.getSize() + " elements.");
		Logger.i("Emptying queue.");

		STQueuer.getEvents(10000);
		Logger.i("Queue finally contains " + STQueue.getSize() + " elements.");

		// Test 2 : Enqueue elements and dequeue some elements randomly
		for(int n = 1; n <= 10; n++)
		{
			Logger.i("Queue initally contains " + STQueue.getSize() + " elements.");

			Logger.i("Adding random number of elements.");
			for(int i = 0; i < 50 + Math.random()*50; i++)
			{
				SailthruEvent stevent = new SailthruEvent.Builder().event("e"+i).tag("facebook").tag("google").url("www.sailthru.com").location(34, 12).build();
				STQueuer.addEvent(stevent.getSTEvent());
			}

			Logger.i("Queue contains " + STQueue.getSize() + " elements.");

			Logger.i("Dequeueing some elements.");

			ConcurrentLinkedQueue<STEvent> result = STQueuer.getEvents((int) (Math.random()*50));

			while(result.size() != 0)
			{
				Logger.i("Dequeued: " + result.poll().getEvent());
			}

			Logger.i("Queue finally contains " + STQueue.getSize() + " elements.");
		}
	}
}