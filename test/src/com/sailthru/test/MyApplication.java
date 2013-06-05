package com.sailthru.test;

import android.app.Application;
import android.content.Intent;

import com.urbanairship.AirshipConfigOptions;
import com.urbanairship.Logger;
import com.urbanairship.UAirship;
import com.urbanairship.push.PushManager;
import com.urbanairship.push.PushPreferences;

public class MyApplication extends Application {

	@Override
	public void onCreate(){
		
		super.onCreate();
		
		AirshipConfigOptions options = AirshipConfigOptions.loadDefaultOptions(this);

		UAirship.takeOff(this, options);
		PushManager.shared().setIntentReceiver(IntentReceiver.class);
		PushManager.enablePush();
		
		String apid = PushManager.shared().getAPID();
		Logger.info("My Application onCreate - App APID: " + apid);
		
		
		/*Intent intent = new Intent(this, MainActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(intent);*/
	}
}