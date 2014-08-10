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
	 * ͨ���㲥����ɨ�裬�Ƿ񵽴�ʱ�������������
	 * 
	 * */

	@Override
	public void onReceive(Context context, Intent intent) {

		SharedPreferences sharedPreferences = context.getSharedPreferences(
				"alarm_record", Activity.MODE_PRIVATE);
		
		if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
			// �������ã�������Ҫ���͹㲥
			if (sharedPreferences.getBoolean("toggleState", false)){
				sharedPreferences.edit().putBoolean("reboot", true).commit();//����Ϊ����
				   
				Intent newIntent = new Intent(context, MainSendActivity.class); 
				newIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);  //ע�⣬������������ǣ�����������ʧ�� 
				context.startActivity(newIntent);    
				
			}
		} else {

		

			String hour = String.valueOf(Calendar.getInstance().get(
					Calendar.HOUR_OF_DAY));
			String minute = String.valueOf(Calendar.getInstance().get(
					Calendar.MINUTE));

			String timeNow = hour + ":" + minute;
			// String time = sharedPreferences.getString(hour + ":" + minute,
			// null);// Сʱ��֣�
			
			String time = sharedPreferences.getString("time", null);
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
					&& neirong != null) {// �ж��Ƿ�Ϊ�գ����Ҵ��ļ��л�ȡ��ʱ���������ʱ�� ��Ȼ��ͨ��������

				Toast toast = Toast.makeText(context, "�����Ѿ����ͳɹ�", Toast.LENGTH_LONG);
				toast.setGravity(Gravity.CENTER, 0, 0);
				toast.show();

				
				System.out.println("messageSendSuccessfully" + neirong);
				sendMsg(context, haoma, neirong);
				recordAfterSendSms(context, haoma, neirong);// ��¼��������
				
				//���ŷ��ͽ�������Ҫ�����������ʱ��
				if (0!=sharedPreferences.getInt("random", 0)){
					sharedPreferences.edit().putBoolean("randomFirstFlag", true).commit();
				} 
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
							.commit();// ����һ��״̬��toggleButton״̬��Ϊfalse

					Intent intentToogle = new Intent();
					intentToogle.setAction(ConstApp.CHANGETOOGLE_ACTION);
					context.sendBroadcast(intentToogle);

					System.out
							.println("shendBroadcast to change state of tooglebutton");

				}
			}
		}

	}

	private void sendMsg(Context context, String number, String message) {

		SmsManager smsManager = SmsManager.getDefault();
		// ����������ݳ���70���ַ� ���������Ų�ɶ������ŷ��ͳ�ȥ
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
	 * ��¼�ѷ��͵Ķ���(�����ô˷����Ļ�,ֻ���Ͷ��ŵ��ֻ���"�ѷ���"���޷���ѯ����¼)
	 * 
	 * @param phoneNumber
	 *            Ŀ���ֻ���
	 * @param message
	 *            ���Ͷ�������
	 * 
	 *            <p>
	 *            ��ʱ���Ͷ���ʧ�ܴ˴�����Ҳ���Ե���ʹ��,</br>
	 * 
	 */
	private void recordAfterSendSms(Context context, String phoneNumber,
			String message) {
		/** �����͵Ķ��Ų������ݿ� **/
		ContentValues values = new ContentValues();
		// ����ʱ��
		values.put("date", System.currentTimeMillis());
		// �Ķ�״̬
		values.put("read", 0);
		// 1Ϊ�� 2Ϊ��
		values.put("type", 2);
		// �ʹ����
		values.put("address", phoneNumber);
		// �ʹ�����
		values.put("body", message);
		// ������ſ�
		context.getContentResolver().insert(Uri.parse("content://sms"), values);

	}
	
	private String getRandomTime(String time , int randInt){
		String strSplit[] = time.split(":");
		int  hour =Integer.parseInt(strSplit[0]) ;
		int  mins =Integer.parseInt(strSplit[1]) ;
	    
		System.out.println("str[0]=>"+strSplit[0]+"str[1]=>"+strSplit[1]);
		
		int rand = (int) (Math.random()*randInt +1); //�����
		
		int range = randInt/2; //range ==5  ��Χ��-5 +�����
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

}




