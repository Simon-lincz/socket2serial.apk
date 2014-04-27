package com.fornut.wificartest;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import com.fornut.wificartest.IConnection;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;

public class Connection extends Service {

	static final String TAG = "WCT-Connection";

	Context mContext;
	ConnectionHandler mHandler;

	Socket mSocket;
	boolean isConnected = false;
	String mAddress = "192.168.1.1";
	int mPort = 2001;
	OutputStream mOutputStream;
	InputStream mInputStream;

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		mContext = Connection.this;
		mHandler = new ConnectionHandler();
		super.onCreate();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return mbinder;
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		if (isConnected) {
			try {
				mSocket.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		super.onDestroy();
	}

	void newSocket() {
		new newSocketThread().start();
		// try {
		// mSocket = new Socket(mAddress,mPort);
		// mOutputStream = mSocket.getOutputStream();
		// mInputStream = mSocket.getInputStream();
		// } catch (UnknownHostException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// } catch (IOException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
	}

	class newSocketThread extends Thread {
		@Override
		public void run() {
			// TODO Auto-generated method stub
			try {
				mSocket = new Socket(mAddress, mPort);
				mOutputStream = mSocket.getOutputStream();
				mInputStream = mSocket.getInputStream();
				isConnected = true;
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	void newSocket(String address, int port) {
		mAddress = address;
		mPort = port;
		newSocket();
	}

	public void reconnected(){
		try {
			mSocket.close();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		isConnected = false;
		newSocket();
	}
	
	public void changeConnectionTo(String address, int port) {
		try {
			mSocket.close();
			isConnected = false;
			mAddress = address;
			mPort = port;
			newSocket();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public OutputStream getOutputStream() {
		if (mSocket.isConnected())
			return mOutputStream;
		else
			return null;
	}

	public InputStream getInputStream() {
		if (mSocket.isConnected())
			return mInputStream;
		else
			return null;
	}

	
	static final int MOTO_DATALEN = 7;
	
	/*
	 * formate:
	 * 	moto-> 55 aa 02 02 01 02  aa (hex)
	 *         head[0:1] com[2] datelen[3] date[4:5] tail[6]
	 *  eg.byte[] b =  {(byte)0x55,(byte)0x31,(byte)0x02,(byte)0x02,(byte)0x01,(byte)0x02,(byte)0xaa};
	 *  
	 */
	byte[] changeStringToHEX(String str) {
		switch (str.length()) {
		case MOTO_DATALEN * 2:
			byte[] motodata = new byte[7];
			for(int i = 0;i<7;i++){
				motodata[i]=(byte) Integer.parseInt(str.substring(i*2, (i+1)*2), 16);
//				Log.d(TAG, str.substring(i*2, (i+1)*2));
			}
			return motodata;

		default:
			break;
		}
		return null;
	}

	void sendCMD(String cmd) {
		// Log.d(TAG, "msg: " + cmd);
		if (mSocket != null) {
			if (isConnected) {
				// byte[] b = cmd.getBytes();
				try {
					mOutputStream.write(changeStringToHEX(cmd));
					mOutputStream.flush();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					reconnected();
				}
				Log.d(TAG, "msg send done.");
			}
		}
	}

	class ConnectionHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
		}
	}

	// ---------------------------aidl------------------------
	IConnection.Stub mbinder = new IConnection.Stub() {

		@Override
		public void newSocket(String address, int port) throws RemoteException {
			// TODO Auto-generated method stub
			Connection.this.newSocket(address, port);
		}

		@Override
		public void changeConnectionTo(String address, int port)
				throws RemoteException {
			// TODO Auto-generated method stub
			Connection.this.changeConnectionTo(address, port);
		}

		@Override
		public void sendCMD(String cmd) throws RemoteException {
			// TODO Auto-generated method stub
			Connection.this.sendCMD(cmd);
		}

	};

}
