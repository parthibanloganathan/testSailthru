package com.sailthru.test;

import sailthru.Manager.Sailthru;
import android.app.Application;

import com.urbanairship.AirshipConfigOptions;
import com.urbanairship.Logger;
import com.urbanairship.UAirship;
import com.urbanairship.push.PushManager;

public class MyApplication extends Application {

	@Override
	public void onCreate(){
		
		super.onCreate();
		
		
		AirshipConfigOptions options = AirshipConfigOptions.loadDefaultOptions(this);

		UAirship.takeOff(this, options);
		PushManager.shared().setIntentReceiver(IntentReceiver.class);
		PushManager.enablePush();
		
		//Sailthru.init(this, "testapp", "prod-mobile.dannyrosen.net"); // from David
		Sailthru.init(this, "testapp", "qa2-horizon.dannyrosen.net"); // from Danny
		
		String apid = PushManager.shared().getAPID();
		Logger.info("My Application onCreate - App APID: " + apid);
		
	}
}