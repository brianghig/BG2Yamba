package com.bg2.bg2yamba;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class BootReceiver extends BroadcastReceiver {

	private static final String TAG = BootReceiver.class.getSimpleName();
	
	@Override
	public void onReceive(Context context, Intent intent) {
		context.startService(new Intent(context, UpdaterService.class));
		Log.d(TAG, "Boot receiver started UpdaterService");
	}

}
