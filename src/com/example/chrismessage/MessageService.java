package com.example.chrismessage;

import java.util.Calendar;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class MessageService extends Service {

	private String morningMsg = null;
	private String noonMsg = null;
	private String nightMsg = null;
	
	
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO 自动生成的方法存根
		return null;
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO 自动生成的方法存根
		morningMsg = intent.getStringExtra("morningMsg");
//		noonMsg  = intent.getStringExtra("noonMsg");
//		nightMsg = intent.getStringExtra("nightMsg");
		
		sendMessageOnTime();
		
		return super.onStartCommand(intent, flags, startId);
	}

	private void sendMessageOnTime() {
		// TODO 自动生成的方法存根
        final Calendar c = Calendar.getInstance(); 
//        mYear = c.get(Calendar.YEAR); //获取当前年份 
//        mMonth = c.get(Calendar.MONTH);//获取当前月份 
//        mDay = c.get(Calendar.DAY_OF_MONTH);//获取当前月份的日期号码 
        int   mHour = c.get(Calendar.HOUR_OF_DAY);//获取当前的小时数 
        int  mMinute = c.get(Calendar.MINUTE);//获取当前的分钟数 

        System.out.println(mHour + "");
	}
}
