package com.shadow.kilroggeyes;


import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnKeyListener;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.view.KeyEvent;
import android.widget.Toast;

public class EyesinShadowService extends Service implements OnKeyListener {

	 WakeLock mWakeLock = null;
	 
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		acquireWakeLock();
		super.onStartCommand(intent, flags, startId);
		return START_REDELIVER_INTENT;
	}

	@Override
	public void onDestroy() {
		releaseWakeLock();
	}
	
	private void acquireWakeLock() {
		
		if (null == mWakeLock) {
			PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
			mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK
					| PowerManager.ON_AFTER_RELEASE, "LOCK");
			if (null != mWakeLock) {
				mWakeLock.acquire();
			}
		}
	}

	// 释放设备电源锁
	private void releaseWakeLock() {
		if (null != mWakeLock) {
			mWakeLock.release();
			mWakeLock = null;
		}
	}

	@Override
	public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
		Toast.makeText (getApplicationContext(), "Key ", Toast.LENGTH_SHORT).show ();
		// TODO Auto-generated method stub
		return false;
	}
	
	

}
