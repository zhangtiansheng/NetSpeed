package com.coolx.main;

import com.coolx.R;
import com.coolx.service.FloatService;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class NetSpeed extends Activity {
	Button btnstart;
	 Button btnstop;
	 TextView tv;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        btnstart = (Button) findViewById(R.id.btnstart);
        btnstart.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
            	Intent service = new Intent();
        		service.setClass(NetSpeed.this, FloatService.class);		
        		startService(service);
            }
        });
        
        btnstop = (Button) findViewById(R.id.btnstop);
        btnstop.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
            	Intent serviceStop = new Intent();
        		serviceStop.setClass(NetSpeed.this, FloatService.class);
        		stopService(serviceStop);
            }
        });       
        tv= (TextView) findViewById(R.id.tv);
        
        
        
        String str=new StringBuilder()
        .append("\n")
        .append("说明：").append("\n")
        .append("1.悬浮窗可随意移动").append("\n")
        .append("2.实时显示当前网络数据").append("\n")     
        .append("3.上层数据表示下载值").append("\n")
        .append("4.下层数据表示上传值").append("\n")
        .append("5.点击悬浮窗出现关闭小图标可直接关闭").append("\n").append("\n")
		
		.toString();
        tv.setText(str);        
    }
    @Override
    protected void onStop(){
     super.onStop();
     Log.v("stop","stop");
     //createView();
    }
    @Override
    protected void onRestart(){
     super.onRestart();
     Log.v("restart","restart");
     
    }
  
}