package com.shadow.kilroggeyes;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.KeyEvent;
import android.view.Menu;
import android.widget.Toast;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		Intent intent = new Intent();
		intent.setClass(MainActivity.this, EyesinShadowService.class);
		startService(intent);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override 
	public boolean onKeyDown (int keyCode, KeyEvent event) { 
	    // 获取手机当前音量值 
	 
	    switch (keyCode) { 

	        case KeyEvent.KEYCODE_VOLUME_DOWN: 
	            Toast.makeText (getApplicationContext(), "Down ", Toast.LENGTH_SHORT).show (); 
	            return true; 
	 

	        case KeyEvent.KEYCODE_VOLUME_UP: 
	            Toast.makeText (getApplicationContext(), "Up", Toast.LENGTH_SHORT).show (); 
	            return true; 
	    } 
	    return super.onKeyDown (keyCode, event); 
	} 
}
