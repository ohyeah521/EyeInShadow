package com.shadow.kilroggeyes;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Calendar;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import android.media.AudioManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.os.Vibrator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PictureCallback;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

public class MainActivity extends Activity {

	WakeLock mWakeLock = null;
	String DirPath = "/save";
	int AutoExposureDelay = 1000;
	AudioManager mAudioManager;
	int AudioMode;
	
	PictureCallback pcb = new PictureCallback() {
		@Override
		public void onPictureTaken(byte[] data, Camera camera) {
			giveVibrator();
			savetoPic(data);
			camera.release();
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		// 去掉Activity上面的状态栏
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		mAudioManager = (AudioManager)this.getSystemService(Context.AUDIO_SERVICE);
		AudioMode = mAudioManager.getRingerMode();
		mAudioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);

		setContentView(R.layout.activity_main);
		setBritness(1);
		acquireWakeLock();

		// Intent intent = new Intent();
		// intent.setClass(MainActivity.this, EyesinShadowService.class);
		// startService(intent);
	}

	@Override
	protected void onDestroy() {
		releaseWakeLock();
		mAudioManager.setRingerMode(AudioMode);
		super.onDestroy();
	}

	// @Override
	// public boolean onCreateOptionsMenu(Menu menu) {
	// // Inflate the menu; this adds items to the action bar if it is present.
	// getMenuInflater().inflate(R.menu.main, menu);
	// return true;
	// }

	private void setBritness(float brightness)
	{

		WindowManager.LayoutParams params = getWindow().getAttributes();

		params.flags |= WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;

		params.screenBrightness = brightness / 255.0f;

		getWindow().setAttributes(params);

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// 获取手机当前音量值
		switch (keyCode) {

		case KeyEvent.KEYCODE_VOLUME_DOWN:
			catchCamera(false);
			mAudioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_LOWER, 0);
			return true;

		case KeyEvent.KEYCODE_VOLUME_UP:
			catchCamera(true);
			mAudioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_RAISE, 0);
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	
	public void giveVibrator()
	{
		Vibrator vibrator = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE); 
		vibrator.vibrate(100);
	}

	@SuppressLint("NewApi")
	private int FindCamera(boolean back) {
		int cameraCount = 0;
		Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
		cameraCount = Camera.getNumberOfCameras(); // get cameras number
		int value = Camera.CameraInfo.CAMERA_FACING_FRONT;
		if (back) {
			value = Camera.CameraInfo.CAMERA_FACING_BACK;
		}
		for (int camIdx = 0; camIdx < cameraCount; camIdx++) {
			Camera.getCameraInfo(camIdx, cameraInfo); // get camerainfo
			if (cameraInfo.facing == value) {
				return camIdx;
			}
		}
		return -1;
	}

	// Main Func of Catch Camera
	@SuppressLint("NewApi")
	public boolean catchCamera(boolean backCamera) {

		int cameraindex = FindCamera(backCamera);
		if (cameraindex == -1) {
			return false;
		}

		// Open Camera, Set Camera
		Camera camera = null;
		try {
			camera = Camera.open(cameraindex);
			camera.setDisplayOrientation(90);

			try {
				Parameters p = camera.getParameters();
				if (p != null) {
					if (backCamera) {
						p.setFlashMode(Parameters.FLASH_MODE_OFF);
						p.set("orientation", "portrait");
						p.setRotation(90);
					} else {
						p.set("orientation", "portrait");
						p.setRotation(270);
					}
					camera.setParameters(p);
				}
			} catch (Exception e) {
			}
			camera.setPreviewTexture(new SurfaceTexture(0));
		} catch (Exception e1) {
			return false;
		}

		// Start Camera
		camera.startPreview();
		try {
			Thread.sleep(AutoExposureDelay);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		camera.autoFocus(new AutoFocusCallback() {
			@Override
			public void onAutoFocus(boolean success, Camera camera) {
				if (success) {
					try {
						camera.takePicture(null, null, pcb);
					} catch (Exception e) {
						camera.release();
					}
				}
			}

		});


//		try {
//			Thread.sleep(AutoExposureDelay);
//			camera.takePicture(null, null, pcb);
//		} catch (Exception e) {
//			camera.release();
//			return false;
//		}
		return true;
	}

	public void savetoPic(byte[] data) {
		Calendar c = Calendar.getInstance();
		String datestring = "" + c.get(Calendar.YEAR)
				+ String.format("%02d",(c.get(Calendar.MONTH) + 1)) 
				+ String.format("%02d", c.get(Calendar.DAY_OF_MONTH))
				+ String.format("%02d", c.get(Calendar.HOUR_OF_DAY))
				+ String.format("%02d", c.get(Calendar.MINUTE)) 
				+ String.format("%02d", c.get(Calendar.SECOND));

		String Path = Environment.getExternalStorageDirectory() + DirPath;
		File dir = new File(Path);
		dir.mkdirs();
		Path += "/" + datestring + ".zip";

		File f = new File(Path);
		FileOutputStream fo = null;
		try {
			fo = new FileOutputStream(f);
			ZipOutputStream zo = new ZipOutputStream(fo);
			ZipEntry ze = new ZipEntry("x.jpg");
			zo.putNextEntry(ze);
			zo.write(data);
			zo.flush();
			zo.close();

		} catch (IOException e) {
			Log.v("PicSave", e.getMessage());
		} finally {
			try {
				if (fo != null)
					fo.close();
			} catch (IOException e) {
			}
		}
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

}
