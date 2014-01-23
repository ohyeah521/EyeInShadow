package com.shadow.kilroggeyes;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class KrBoardcastReceiver extends BroadcastReceiver {
    static final String SYSTEM_REASON = "reason";
    static final String SYSTEM_HOME_KEY = "homekey";//home key
    static final String SYSTEM_RECENT_APPS = "recentapps";//long home key
  

	@Override
	public void onReceive(Context context, Intent intent){
        String action = intent.getAction();
        Log.v("BroadCast",action);
    }
}