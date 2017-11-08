package com.coolx.service;
import com.coolx.R;
import com.coolx.model.MyApplication;

import com.coolx.model.netSpeed;

import android.app.Activity;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.TextView;

public class FloatService extends Service {

	WindowManager wm = null;
	WindowManager.LayoutParams wmParams = null;
	View view;
	private float mTouchStartX;
	private float mTouchStartY;
	private float x;
	private float y;
	int state;
	TextView tx1;
	TextView tx;
	ImageView iv;
	private float StartX;
	private float StartY;
	int delaytime=1000;
	long receiveStart, receiveEnd;
	long transmitStart, transmitEnd;
	long[] tmp = new long[2];
	String dev = "eth0";
	String netChange = "android.net.conn.CONNECTIVITY_CHANGE";
	@Override
	public void onCreate() {
		Log.d("FloatService", "onCreate");
		super.onCreate();
		view = LayoutInflater.from(this).inflate(R.layout.floating, null);
		tx = (TextView) view.findViewById(R.id.memunused);
		tx1 = (TextView) view.findViewById(R.id.memtotal);
		tx.setText("" +  "KB");
		tx1.setText("" +  "KB");
		iv = (ImageView) view.findViewById(R.id.img2);
		iv.setVisibility(View.GONE);
		createView();
		IntentFilter mNetFilter = new IntentFilter();
		mNetFilter.addAction(netChange);
		mNetFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
		registerReceiver(mNetReceiver, mNetFilter);
		
		tmp = netSpeed.getSpeed(dev);
		handler.postDelayed(task, delaytime);
	}

	private BroadcastReceiver mNetReceiver = new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			dev = getCurrentNetDevice(getApplicationContext());
			if(dev == null)dev = "eth0";
		}
	};
	
	private void createView() {
		SharedPreferences shared = getSharedPreferences("float_flag",
				Activity.MODE_PRIVATE);
		SharedPreferences.Editor editor = shared.edit();
		editor.putInt("float", 1);
		editor.commit();
		
		wm = (WindowManager) getApplicationContext().getSystemService("window");
		
		wmParams = ((MyApplication) getApplication()).getMywmParams();
		wmParams.type = 2002;
		wmParams.flags |= 8;
		wmParams.gravity = Gravity.LEFT | Gravity.TOP;
		wmParams.x = 0;
		wmParams.y = 0;
		
		wmParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
		wmParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
		wmParams.format = 1;
		
		wm.addView(view, wmParams);

		view.setOnTouchListener(new OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				
				x = event.getRawX();
				y = event.getRawY() - 25; 
				Log.i("currP", "currX" + x + "====currY" + y);
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					state = MotionEvent.ACTION_DOWN;
					StartX = x;
					StartY = y;
					
					mTouchStartX = event.getX();
					mTouchStartY = event.getY();
					Log.i("startP", "startX" + mTouchStartX + "====startY"
							+ mTouchStartY);
					break;
				case MotionEvent.ACTION_MOVE:
					state = MotionEvent.ACTION_MOVE;
					updateViewPosition();
					break;

				case MotionEvent.ACTION_UP:
					state = MotionEvent.ACTION_UP;

					updateViewPosition();
					showImg();
					mTouchStartX = mTouchStartY = 0;
					break;
				}
				return true;
			}
		});

		iv.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent serviceStop = new Intent();
				serviceStop.setClass(FloatService.this, FloatService.class);
				stopService(serviceStop);
			}
		});

	}

	public void showImg() {
		if (Math.abs(x - StartX) < 1.5 && Math.abs(y - StartY) < 1.5
				&& !iv.isShown()) {
			iv.setVisibility(View.VISIBLE);
		} else if (iv.isShown()) {
			iv.setVisibility(View.GONE);
		}
	}

	private Handler handler = new Handler();
	private Runnable task = new Runnable() {
		public void run() {
			// TODO Auto-generated method stub
			dataRefresh();

			handler.postDelayed(this, delaytime);
			wm.updateViewLayout(view, wmParams);
		}
	};

	public void dataRefresh() {
		receiveStart = tmp[0];
		transmitStart = tmp[1];
		tmp = netSpeed.getSpeed(dev);
		receiveEnd = tmp[0];
		transmitEnd = tmp[1];
		long speed1 = receiveEnd-receiveStart;
		long speed2 = transmitEnd-transmitStart;
		if(speed1 < 1024){
			tx.setText("↓: " + String.valueOf(speed1) + "B/S");
		}else if(speed1 >= 1048576 ){
			tx.setText("↓: " + String.valueOf(speed1/1048576) + "MB/S");
		}else{
			tx.setText("↓: " + String.valueOf(speed1/1024) + "KB/S");
		}
		
		if(speed2 < 1024){
			tx1.setText("↑: " + String.valueOf(speed2) + "B/S");
		}else if(speed2 >= 1048576 ){
			tx1.setText("↑: " + String.valueOf(speed2/1048576) + "MB/S");
		}else{
			tx1.setText("↑: " + String.valueOf(speed2/1024) + "KB/S");
		}
	}

	private void updateViewPosition() {
		// 更新浮动窗口位置参数
		wmParams.x = (int) (x - mTouchStartX);
		wmParams.y = (int) (y - mTouchStartY);
		wm.updateViewLayout(view, wmParams);
	}

	@Override
	public void onStart(Intent intent, int startId) {
		Log.d("FloatService", "onStart");
//		setForeground(true);
		super.onStart(intent, startId);
	}

	@Override
	public void onDestroy() {
		unregisterReceiver(mNetReceiver);
		handler.removeCallbacks(task);
		Log.d("FloatService", "onDestroy");
		wm.removeView(view);
		super.onDestroy();
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}	
	
	public static String getCurrentNetDevice(Context icontext){
			String tmpdev = null;
	       Context context = icontext.getApplicationContext();
	       ConnectivityManager connectivity = (ConnectivityManager) context
	                  .getSystemService(Context.CONNECTIVITY_SERVICE);
	       NetworkInfo[] info;
	       if (connectivity != null) {
	           info = connectivity.getAllNetworkInfo();
	            if (info != null) {
	              for (int i = 0; i < info.length; i++) {
	            	  if(info[i].isConnected()){
	            		  if (info[i].getTypeName().equals("WIFI")){
	            			  tmpdev = "wlan0";
	            		  }else if (info[i].getTypeName().equals("MOBILE")){
	            			  tmpdev = "ippp0";
	            		  }else {
	            			  tmpdev = "eth0";
	            		  }
	            		  break;
	            	  }
	              }
	          }
	      }
	      return tmpdev;
	  }
}
