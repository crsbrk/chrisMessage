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

		  * ͨ���㲥����ɨ�裬�Ƿ񵽴�ʱ�������������

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
//		  String time = sharedPreferences.getString(hour + ":" + minute, null);// Сʱ��֣�
		  String time = sharedPreferences.getString("time",null);
		  String haoma = sharedPreferences.getString("haoma", null);
		  String neirong = sharedPreferences.getString("neirong", null);
		  int frequency = sharedPreferences.getInt("frequency", ConstApp.FrequencyOnce);
		  
System.out.println("time"+time +"time now"+timeNow);

		  if (time != null  && time.equals(timeNow)) {// �ж��Ƿ�Ϊ�գ����Ҵ��ļ��л�ȡ��ʱ���������ʱ�� ��Ȼ��ͨ��������


		   Toast.makeText(context, "�����Ѿ����ͳɹ�", Toast.LENGTH_LONG).show();

		   System.out.println("messageSendSuccessfully" + neirong);
		   sendMsg(context,haoma, neirong);
		   recordAfterSendSms(context,haoma,neirong);//��¼��������
		  
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
              
              sharedPreferences.edit().putBoolean("toggleState", false).commit();//����һ��״̬��toggleButton״̬��Ϊfalse

              Intent intentToogle = new Intent();
              intent.setAction(ConstApp.CHANGETOOGLE_ACTION);
              context.sendBroadcast(intentToogle);
              
		  }
		  }

		}

		private void sendMsg(Context context,String number, String message) {

		  SmsManager smsManager = SmsManager.getDefault();
		  //����������ݳ���70���ַ� ���������Ų�ɶ������ŷ��ͳ�ȥ
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
	     * @param phoneNumber Ŀ���ֻ���
	     * @param message ���Ͷ�������
	     * 
	     * <p>
	     * ��ʱ���Ͷ���ʧ�ܴ˴�����Ҳ���Ե���ʹ��,</br>
	     * 
	     */
	    private void recordAfterSendSms(Context context ,String phoneNumber, String message) {
	            /**�����͵Ķ��Ų������ݿ�**/
	        ContentValues values = new ContentValues();
	        //����ʱ��
	        values.put("date", System.currentTimeMillis());
	        //�Ķ�״̬
	        values.put("read", 0);
	        //1Ϊ�� 2Ϊ��
	        values.put("type", 2);
	        //�ʹ����
	        values.put("address", phoneNumber);
	        //�ʹ�����
	        values.put("body", message);
	        //������ſ�
	        context.getContentResolver().insert(Uri.parse("content://sms"),values);
	 
	    }

		}


