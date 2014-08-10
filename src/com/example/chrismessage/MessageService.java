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
		// TODO �Զ����ɵķ������
		return null;
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO �Զ����ɵķ������
		morningMsg = intent.getStringExtra("morningMsg");
//		noonMsg  = intent.getStringExtra("noonMsg");
//		nightMsg = intent.getStringExtra("nightMsg");
		
		sendMessageOnTime();
		
		return super.onStartCommand(intent, flags, startId);
	}

	private void sendMessageOnTime() {
		// TODO �Զ����ɵķ������
        final Calendar c = Calendar.getInstance(); 
//        mYear = c.get(Calendar.YEAR); //��ȡ��ǰ��� 
//        mMonth = c.get(Calendar.MONTH);//��ȡ��ǰ�·� 
//        mDay = c.get(Calendar.DAY_OF_MONTH);//��ȡ��ǰ�·ݵ����ں��� 
        int   mHour = c.get(Calendar.HOUR_OF_DAY);//��ȡ��ǰ��Сʱ�� 
        int  mMinute = c.get(Calendar.MINUTE);//��ȡ��ǰ�ķ����� 

        System.out.println(mHour + "");
	}
}
