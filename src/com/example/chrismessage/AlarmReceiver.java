package com.example.chrismessage;

import java.util.ArrayList;
import java.util.Calendar;

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
import android.widget.Toast;

public class AlarmReceiver extends BroadcastReceiver{


		/**

		  * 通过广播进行扫描，是否到达时间后再响起闹铃

		  * */

		@Override

		public void onReceive(Context context, Intent intent) {

		  SharedPreferences sharedPreferences = context.getSharedPreferences(

		    "alarm_record", Activity.MODE_PRIVATE);

		  String hour = String.valueOf(Calendar.getInstance().get(

		    Calendar.HOUR_OF_DAY));

		  String minute = String.valueOf(Calendar.getInstance().get(

		    Calendar.MINUTE));
		  String timeNow =hour+":"+minute;
//		  String time = sharedPreferences.getString(hour + ":" + minute, null);// 小时与分，
		  String time = sharedPreferences.getString("time",null);
		  String haoma = sharedPreferences.getString("haoma", null);
		  String neirong = sharedPreferences.getString("neirong", null);
		  int frequency = sharedPreferences.getInt("frequency", ConstApp.FrequencyOnce);
		  
System.out.println("time"+time +"time now"+timeNow);

		  if (time != null  && time.equals(timeNow)) {// 判断是否为空，而且从文件中获取的时间等于现在时间 ，然后通过创建，


		   Toast.makeText(context, "短信已经发送成功", Toast.LENGTH_LONG).show();

		   System.out.println("messageSendSuccessfully" + neirong);
		   sendMsg(context,haoma, neirong);
		   recordAfterSendSms(context,haoma,neirong);//记录短信内容
		  
		//   System.out.println("frequency"+frequency+"  == FrequencyOnce"+ConstApp.FrequencyOnce);
		  
		   if (frequency == R.id.radioButtonOnce){
			  
			   //cancel the alarm.
			   System.out.println("alarmReceiver to stop it");
              Intent intentCancel=new Intent(context,AlarmReceiver.class);
              intentCancel.setAction("AlarmReceiver");
              PendingIntent pendingIntent=PendingIntent.getBroadcast(context, 0, intentCancel, 0);
              AlarmManager aManager=(AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
              aManager.cancel(pendingIntent);
              System.out.println("stop the alarm_service");
              
              sharedPreferences.edit().putBoolean("toggleState", false).commit();//发送一次状态后toggleButton状态变为false

              Intent intentToogle = new Intent();
              intent.setAction(ConstApp.CHANGETOOGLE_ACTION);
              context.sendBroadcast(intentToogle);
              
		  }
		  }

		}

		private void sendMsg(Context context,String number, String message) {

		  SmsManager smsManager = SmsManager.getDefault();
		  //如果短信内容超过70个字符 将这条短信拆成多条短信发送出去
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
	     * @param phoneNumber 目标手机号
	     * @param message 发送短信内容
	     * 
	     * <p>
	     * 及时发送短信失败此处代码也可以调用使用,</br>
	     * 
	     */
	    private void recordAfterSendSms(Context context ,String phoneNumber, String message) {
	            /**将发送的短信插入数据库**/
	        ContentValues values = new ContentValues();
	        //发送时间
	        values.put("date", System.currentTimeMillis());
	        //阅读状态
	        values.put("read", 0);
	        //1为收 2为发
	        values.put("type", 2);
	        //送达号码
	        values.put("address", phoneNumber);
	        //送达内容
	        values.put("body", message);
	        //插入短信库
	        context.getContentResolver().insert(Uri.parse("content://sms"),values);
	 
	    }

		}


