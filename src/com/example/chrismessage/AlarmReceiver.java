package com.example.chrismessage;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Random;



import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

public class AlarmReceiver extends BroadcastReceiver {

	/**
	 * 
	 * 通过广播进行扫描，是否到达时间后再响起闹铃
	 * 
	 * */

	
	@Override
	public void onReceive(Context context, Intent intent) {

		SharedPreferences sharedPreferences = context.getSharedPreferences(
				"alarm_record", Activity.MODE_PRIVATE);
		
		if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
			// 开机调用，发现需要发送广播
			if (sharedPreferences.getBoolean("toggleState", false)){
				sharedPreferences.edit().putBoolean("reboot", true).commit();//设置为启动
				   
				Intent newIntent = new Intent(context, MainSendActivity.class); 
				newIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);  //注意，必须添加这个标记，否则启动会失败 
				context.startActivity(newIntent);    
				
			}
		} else {

		

			String hour = String.valueOf(Calendar.getInstance().get(
					Calendar.HOUR_OF_DAY));
			String minute = String.valueOf(Calendar.getInstance().get(
					Calendar.MINUTE));

			String timeNow = hour + ":" + minute;
			// String time = sharedPreferences.getString(hour + ":" + minute,
			// null);// 小时与分，
			
			String time = sharedPreferences.getString("time", null);
			String time2 = time;
			int randInt = 0;
			Boolean randomFirstFlag = sharedPreferences.getBoolean("randomFirstFlag",false);
			if (0 != (randInt =sharedPreferences.getInt("random", 0))){
				Log.i("before random time ",time);
				
				if (randomFirstFlag){
					time = getRandomTime(time, randInt);
					sharedPreferences.edit().putString("randomTime", time).commit();
					sharedPreferences.edit().putBoolean("randomFirstFlag", false).commit(); 
					Log.i("after random time ",time);
				}else{
					Log.i("randomFirstFlag false no random time ",randomFirstFlag+"");
				}
				
				time =sharedPreferences.getString("randomTime","0:0");
			}
			
			
			
			String haoma = sharedPreferences.getString("haoma", null);
			String neirong = sharedPreferences.getString("neirong", " ");
			 int frequency = sharedPreferences.getInt("frequency",
					R.id.radioButtonOnce);

			System.out.println("time" + time + "time now" + timeNow);

			if (time != null && time.equals(timeNow) && neirong.trim() != ""
					&& neirong != null) {// 判断是否为空，而且从文件中获取的时间等于现在时间 ，然后通过创建，

				Toast toast = Toast.makeText(context, "短信已经发送成功", Toast.LENGTH_LONG);
				toast.setGravity(Gravity.CENTER, 0, 0);
				toast.show();

				
				System.out.println("messageSendSuccessfully" + neirong);
				sendMsg(context, haoma, neirong);
				recordAfterSendSms(context, haoma, neirong);// 记录短信内容
				
				sharedPreferences.edit().putBoolean("needChangeRandomTimeOnce", true).commit(); //设置标志位，需要改变一次随机时间

				// System.out.println("frequency"+frequency+"  == FrequencyOnce"+ConstApp.FrequencyOnce);

				if (frequency == R.id.radioButtonOnce) {

					// cancel the alarm.
					System.out.println("alarmReceiver to stop it");
					// Intent intentCancel=new
					// Intent(context,AlarmReceiver.class);
					// intentCancel.setAction("AlarmReceiver");
					PendingIntent pendingIntent = PendingIntent.getBroadcast(
							context, 0, intent, 0);
					AlarmManager aManager = (AlarmManager) context
							.getSystemService(Context.ALARM_SERVICE);
					aManager.cancel(pendingIntent);

					System.out.println("stop the alarm_service");

					sharedPreferences.edit().putBoolean("toggleState", false)
							.commit();// 发送一次状态后toggleButton状态变为false

					Intent intentToogle = new Intent();
					intentToogle.setAction(ConstApp.CHANGETOOGLE_ACTION);
					context.sendBroadcast(intentToogle);

					System.out
							.println("shendBroadcast to change state of tooglebutton");

				}
			}//end if( time
			
			if (sharedPreferences.getBoolean("needChangeRandomTimeOnce", false)){
				
				//短信发送结束后，需要继续设置随机时间
				//如果刚发送短信后，立刻重新生成随机时间，可能会重复发送短信，例如9:10，随机范围是10分钟，
				//也就是9:5~9:15都可能发送短信，如果第一次随机发送时间时9:5，发送过后，立刻重新定义随机时间，将9:10随机到9:7又会发送一次
				//所以重新定义随机时间必须至少在一个随机范围（定义10分钟）之后，比如9:20之后定义随机时间，（9:15-9:25）
				if (0!=sharedPreferences.getInt("random", 0)){
					if(isOutOfRange(timeNow, time2, randInt )) {
						sharedPreferences.edit().putBoolean("randomFirstFlag", true).commit();
						sharedPreferences.edit().putBoolean("needChangeRandomTimeOnce", false).commit(); //设置标志位，已经改变了一次随机时间，不需要再次改变
					}
				}
				

			}
			
		}

	}

	private void sendMsg(Context context, String number, String message) {

		SmsManager smsManager = SmsManager.getDefault();
		// 如果短信内容超过70个字符 将这条短信拆成多条短信发送出去
		if (message.length() > 70) {
			ArrayList<String> msgs = smsManager.divideMessage(message);
			for (String msg : msgs) {
				smsManager.sendTextMessage(number, null, msg, null, null);
			}
		} else {
			System.out.println("not longer than 70");
			smsManager.sendTextMessage(number, null, message, null, null);
		}

	}

	/**
	 * 记录已发送的短信(不调用此方法的话,只发送短信但手机的"已发送"中无法查询到记录)
	 * 
	 * @param phoneNumber
	 *            目标手机号
	 * @param message
	 *            发送短信内容
	 * 
	 *            <p>
	 *            及时发送短信失败此处代码也可以调用使用,</br>
	 * 
	 */
	private void recordAfterSendSms(Context context, String phoneNumber,
			String message) {
		/** 将发送的短信插入数据库 **/
		ContentValues values = new ContentValues();
		// 发送时间
		values.put("date", System.currentTimeMillis());
		// 阅读状态
		values.put("read", 0);
		// 1为收 2为发
		values.put("type", 2);
		// 送达号码
		values.put("address", phoneNumber);
		// 送达内容
		values.put("body", message);
		// 插入短信库
		context.getContentResolver().insert(Uri.parse("content://sms"), values);

	}
	
	private String getRandomTime(String time , int randInt){
		String strSplit[] = time.split(":");
		int  hour =Integer.parseInt(strSplit[0]) ;
		int  mins =Integer.parseInt(strSplit[1]) ;
	    
		System.out.println("str[0]=>"+strSplit[0]+"str[1]=>"+strSplit[1]);
		
		int rand = (int) (Math.random()*randInt +1); //随机数
		
		int range = randInt/2; //range ==5  范围，-5 +随机数
		if (mins-range <0){
			hour = (hour>1)?(hour-=1):23;
			mins = 60-range;
			
		}else {
			mins -=range;
		}
		mins +=rand;
		if (mins >=60){
			mins = mins - 60;
			
			hour +=1;
			if (hour >=24){
				hour -= 24;
			}
			
		}
		
		
		return hour +":"+mins;
	}

	/**
	 * 判断当前时间timeNow是否已经超过了 time2 +randInt的这个范围, 不在这个范围内 ( timeNow - time2 > randInt )
	 * */
	private boolean isOutOfRange(String timeNow, String time2, int randInt ){
		String strSplit[] = timeNow.split(":");
		int  hourNow =Integer.parseInt(strSplit[0]) ;
		int  minsNow =Integer.parseInt(strSplit[1]) ;
		
		String strSplit2[] = time2.split(":");
		int  hour =Integer.parseInt(strSplit2[0]) ;
		int  mins =Integer.parseInt(strSplit2[1]) ;
		
		int minsMax ;
		int minsMin;
		int hourMax;
		int hourMin;
		
		int range = randInt/2;
		if ( mins - range >= 0){
			minsMin = mins - range;
			hourMin = hour;
		}else {
			minsMin = 60 -(range-mins);
			if (hour >0) {
				hourMin = hour - 1;
			}else {
				hourMin = 23;
			}
		}
		System.out.println("rangMin"+ hourMin+":"+minsMin);
		
		if (mins + range <60){
			minsMax = mins+range;
			hourMax = hour;
		}else {
			minsMax =  mins +range - 60;
			if ( 23 == hour ){
				hourMax = 0;
			}else {
				hourMax = hour +1;
			}
		}
		
		System.out.println("rangeMax"+hourMax+":"+minsMax);
		
		if (isInRange(hourMin, minsMin , hourNow, minsNow ,randInt)){
			System.out.println("now in range");
			return false;
		}else {
			System.out.println("now time out of range");
			return true;
		}
		

		
		
	}
	
	 private boolean isInRange(int hourMin, int minsMin ,int hour, int mins , int randInt){
	//判断时间差
		 
		 
		 if (hour == hourMin){
			 if (mins -minsMin >randInt){
				 return false; //out of range
			 }else {
				 return true; // in range
			 }
		 } else if (hour > hourMin){
			 if (hour - hourMin >1){
				 return false;  //out of range
			 }else { //hour - hourMin ==1
				 if (60 - minsMin + mins > randInt){
					 return false; //out of range
				 }else {
					 return true; //in range
				 }
			 }
		 }else {  //hour < hourMin
			 
			 return false; // out of range
		 }
		 
	 }
	 
	 

}




