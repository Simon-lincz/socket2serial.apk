package com.lcz.wificartest;

import java.util.Random;

import com.lcz.wificartest.IConnection;

import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.util.Log;
import android.view.Menu;

public class MainControl extends Activity {

	static String TAG = "WCT-MainControl";
	
	MainHandler mHandler;
	Context mContext;
	
	MainServiceConnection mServiceConnection;
	IConnection mConnection;
	boolean isBind = false;
	
	MjpegView cameraView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_control);
		mContext = MainControl.this;
		mHandler = new MainHandler();
		mServiceConnection = new MainServiceConnection();
		Intent i = new Intent(mContext, Connection.class);
		bindService(i, mServiceConnection,BIND_AUTO_CREATE);
		mHandler.sendEmptyMessage(SEND_CMD);
		
		cameraView = (MjpegView) findViewById(R.id.mySurfaceView1);
		cameraView.setSource("http://192.168.1.114:8080/?action=stream");//初始化Camera
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		unbindService(mServiceConnection);
		mHandler.removeMessages(SEND_CMD);
		super.onDestroy();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main_control, menu);
		return true;
	}

	
	public static final int BASE = 0;
	public static final int NEW_SOCKET = BASE + 1;
	public static final int SEND_CMD = BASE + 2;
	
	class MainHandler extends Handler{
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			switch (msg.what) {
			case NEW_SOCKET:
				break;
				
			case SEND_CMD:
				if(isBind){
					try {
						if(new Random().nextBoolean()){
							mConnection.sendCMD("55aa02020102aa");
						}else{
							mConnection.sendCMD("55aa020201ffaa");
						}
					} catch (RemoteException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				mHandler.sendEmptyMessageDelayed(SEND_CMD, 2000);
				break;
				
			default:
				break;
			}
			super.handleMessage(msg);
		}
	}
	
	class MainServiceConnection implements ServiceConnection{

		@Override
		public void onServiceConnected(ComponentName arg0, IBinder arg1) {
			// TODO Auto-generated method stub
			mConnection = IConnection.Stub.asInterface(arg1);
			isBind = true;
			try {
				
//				mConnection.newSocket("192.168.1.1", 2001);
				mConnection.newSocket("192.168.1.114", 8000);
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Log.d(TAG, "connect to service.");
		}

		@Override
		public void onServiceDisconnected(ComponentName arg0) {
			// TODO Auto-generated method stub
			isBind = false;
		}
		
	}
}
