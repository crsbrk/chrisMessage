package com.example.chrismessage;

import java.util.Calendar;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.preference.Preference;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.TimePicker.OnTimeChangedListener;
import android.widget.Toast;
import android.widget.ToggleButton;

public class MainSendActivity extends Activity {

	protected static  String SHASHA = "";
	protected static String phoneNumber = "5554";
	private String morningMsg = " ";

	private EditText morningEditText = null;
	
	private Button startServiceButton = null;
	private Button stopServiceButton =null;

	private ToggleButton toggleButton = null;
    
	private String	morningTime = "0:00";
    
    
	Dialog dialog = null;  
	 Calendar calendar = Calendar.getInstance();  
	 private SharedPreferences sharedPreferences;  
	 private AlarmManager aManager;
	 private PendingIntent pendingIntent;
	 private TextView timeTextView;
	 private TimePicker tp=null; 
	 private RadioGroup radioGroup =null;
	 private RadioButton radioOnce = null;
	 private RadioButton radioWeek = null;
	boolean toggleState =false;
	 private TextView numberTextView =null;
	private  MyHandler myHandler =null;
	private HandlerThread handlerThread = null;
	private ToggleReveiver tbr = null; 
	 private IntentFilter filter = null;
	
	 @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);	
		setContentView(R.layout.one_msg_send);
        
		 tbr = new ToggleReveiver();
		 filter = new IntentFilter();
		 filter.addAction(ConstApp.CHANGETOOGLE_ACTION);
		 registerReceiver(tbr, filter);
		 
        sharedPreferences=getSharedPreferences("alarm_record", Activity.MODE_PRIVATE);   
        
		 morningEditText = (EditText)findViewById(R.id.morningEditText);
		 numberTextView = (TextView)findViewById(R.id.numberTextView);
//		 startServiceButton = (Button)findViewById(R.id.startButton);
//		 stopServiceButton = (Button)findViewById(R.id.stopButton);
		 toggleButton =(ToggleButton)findViewById(R.id.toggleButton1);

		 //radioGroup
		 
		         radioGroup=(RadioGroup)findViewById(R.id.radioGroup);		 
		         radioOnce=(RadioButton)findViewById(R.id.radioButtonOnce);	 
		         radioWeek=(RadioButton)findViewById(R.id.radioButtonEveryday);		 
		         radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
					
		        	
		        	 
					@Override
					public void onCheckedChanged(RadioGroup group, int checkedId) {
						// TODO 自动生成的方法存根
						 int id= group.getCheckedRadioButtonId();
						 System.out.println("group.getcheckradionButtonid()"+id);
						             switch (group.getCheckedRadioButtonId()) {
						             case R.id.radioButtonOnce:
						                 sharedPreferences.edit().putInt("frequency", ConstApp.FrequencyOnce).commit();
						                 System.out.println("set once");
						            	 break;
						             case R.id.radioButtonEveryday:
						            	 sharedPreferences.edit().putInt("frequency", ConstApp.FrequencyWeek).commit();
						            	 System.out.println("set everyday");
						            	 break;
						 
						             
						             }

					}
				});
		         
		
		         
		         toggleButton.setOnCheckedChangeListener(new OnCheckedChangeListener(){

		        	 
			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				SHASHA = sharedPreferences.getString("haoma", "");
				if(SHASHA.isEmpty() || SHASHA == null){
					//buttonView.setChecked(false);
					toggleButton.setChecked(false);
					isChecked=false;
					sharedPreferences.edit().putBoolean("toggleState", false).commit();
					
					Toast toast = Toast.makeText(getApplicationContext(),
						     "情人的号码是空的~", Toast.LENGTH_LONG);
						   toast.setGravity(Gravity.CENTER, 0, 0);
						   toast.show();
				}
				boolean serving = sharedPreferences.getBoolean("toogleState", false);
				 if (isChecked  && !serving) {  
					 //
				        if (morningEditText.getText().toString() != ""){
				        	morningMsg = morningEditText.getText().toString();	
				        }
				        morningTime =String.valueOf(tp.getCurrentHour())+":"+String.valueOf(tp.getCurrentMinute());
				        
				       
				        sharedPreferences.edit().putString("time", morningTime).commit();   
				        sharedPreferences.edit().putString("haoma", SHASHA).commit();   
				        sharedPreferences.edit().putString("neirong", morningMsg).commit();
				       sharedPreferences.edit().putInt("frequency", radioGroup.getCheckedRadioButtonId()).commit();
				       sharedPreferences.edit().putBoolean("toggleState", true).commit();
				        System.out.println("morningTime--->" + morningTime);
				     

				       // System.out.print(radioGroup.getId()+"=="+R.id.radioButtonOnce);
				        
				   	 timeTextView.setText((radioGroup.getCheckedRadioButtonId()==R.id.radioButtonOnce) ? ("发送一次时间："+morningTime) : ("每天发送时间："+morningTime));
				   	 
				        System.out.println("设置时间"+morningTime);
				        //sharedPreferences.edit().putString("time", morningMsg).commit();

				        Toast.makeText(getApplicationContext(), "已经设置成功", Toast.LENGTH_SHORT).show();
				         aManager=(AlarmManager)getSystemService(Context.ALARM_SERVICE);   
					        
				         startalarm();//开始alarm
					        

	                } else {
	                	if (sharedPreferences.getBoolean("toggleState", false)) {
	    				
	                		aManager.cancel(pendingIntent);
	                		sharedPreferences.edit().putBoolean("toggleState", false).commit();
	                		toggleButton.setChecked(false);
	    				}
	                	  
	                }                 
			}


			 
		 });
		 

		 

		 morningMsg = sharedPreferences.getString("neirong", "");
		morningEditText.setText(morningMsg);
		morningEditText.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO 自动生成的方法存根
				
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO 自动生成的方法存根
				
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				// TODO 自动生成的方法存根
			sharedPreferences.edit().putString("neirong", morningEditText.getText().toString()).commit();	
			}
		});
		
		 calendar.setTimeInMillis(System.currentTimeMillis());
		  
		  tp = (TimePicker)findViewById(R.id.timePicker1);
		  tp.setIs24HourView(true);
		  tp.setCurrentHour(calendar.get(Calendar.HOUR_OF_DAY));
		  tp.setOnTimeChangedListener(new OnTimeChangedListener() {
			
			@Override
			public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
				// TODO 自动生成的方法存根
			timeTextView.setText(hourOfDay+":"+minute);
			sharedPreferences.edit().putString("time", hourOfDay+":"+minute).commit();
			}
		});
		  
	 	 toggleButton.setChecked(sharedPreferences.getBoolean("toggleState", false));//set toggleButton state
	 //	 tp.setCurrentMinute(Calendar.MINUTE);
	 	 
		 Log.e("now hour",""+Calendar.HOUR_OF_DAY+"now minutes"+Calendar.MINUTE);
		 

		morningTime=String.valueOf(tp.getCurrentHour())+":"+String.valueOf(tp.getCurrentMinute());
		 timeTextView = (TextView) findViewById(R.id.timeTextView);
		 timeTextView.setText("发送一次时间："+morningTime);
		 radioGroup.check(R.id.radioButtonOnce);
		 
		SHASHA = sharedPreferences.getString("haoma", null);
		
		
		if (SHASHA == null){
			sharedPreferences.edit().putBoolean("firstStart", true).commit();
		}
		System.out.println(" what is number:"+SHASHA);
		numberTextView.setText("手机号码："+SHASHA);
		
		 
		
		   handlerThread = new HandlerThread("handler thread");  //新线程
		  handlerThread.start();
		   myHandler = new MyHandler(handlerThread.getLooper());
		  


	}
	
	private void startalarm() {
		// TODO 自动生成的方法存根
         Intent intent=new Intent(MainSendActivity.this,AlarmReceiver.class);   
	        intent.setAction("AlarmReceiver");  
	        
	        sharedPreferences.edit().putBoolean("toggleState", true);
	        
	        pendingIntent=PendingIntent.getBroadcast(this, 0, intent, 0);     
	        aManager.setRepeating(AlarmManager.RTC, 0, 60*1000, pendingIntent);   
	}
	
	
	/*接收更新toogleButton的广播
	 * **/
	class ToggleReveiver extends BroadcastReceiver {

		  @Override
		  public void onReceive(Context context, Intent intent) {
		   
		   Message msg = myHandler.obtainMessage();
		   msg.arg1 = R.id.toggleButton1;
		   msg.sendToTarget();
		  }

		  

			 
	}
		  
	
	  class MyHandler extends Handler {
		   public MyHandler(){}
		   public MyHandler(Looper looper) {
		    super(looper);
		   }
		  
		   @Override
		   public void handleMessage(Message msg) {
		   
		    int progress = msg.arg1;
		  toggleButton.setChecked(false);//一般情况下只会是第一次发送短信结束后，发送的更新toogle广播
		    super.handleMessage(msg);
		  }
		  
		 }	  
	
	
	
@Override
protected void onStart() {
	// TODO 自动生成的方法存根
	  boolean firstStart = sharedPreferences.getBoolean("firstStart", true);//是否第一次启用程序
	  System.out.println(firstStart);
	  if (firstStart){  
		sharedPreferences.edit().putBoolean("firstStart", false).commit();
		  setTheNumber();
			numberTextView.setText("手机号码："+SHASHA);
		
	  }else{
		  
	 	 toggleButton.setChecked(sharedPreferences.getBoolean("toggleState", false));//set toggleButton state
		  morningEditText.setText(sharedPreferences.getString("neirong", null));
		  timeTextView.setText(sharedPreferences.getString("time", "0:00"));
		  int checkId = sharedPreferences.getInt("frequency", 1);
		  radioGroup.check(checkId);
		  String time = sharedPreferences.getString("time", "0:00");
		  String[] str = time.split(":");
		  
		  System.out.println(str[0]);
		  System.out.println(str[1]);
		  
		  tp.setCurrentHour(Integer.parseInt(str[0]));
		  tp.setCurrentMinute(Integer.parseInt(str[1]));
		  System.out.println( "checkId"+checkId);
		 if(checkId==R.id.radioButtonOnce){
			 timeTextView.setText("发送一次时间："+time); 
			 radioGroup.check(R.id.radioButtonOnce);
		 }else{
			 timeTextView.setText("每天发送时间："+time); 
					 radioGroup.check(R.id.radioButtonEveryday);
		 }
		  
			numberTextView.setText("手机号码："+SHASHA);
			
		  
	  }
	super.onStart();
}



@Override
protected void onDestroy() {
	// TODO 自动生成的方法存根
	aManager.cancel(pendingIntent);
	sharedPreferences.edit().putBoolean("toggleState", false);
	
	unregisterReceiver(tbr);
	myHandler.removeCallbacks(handlerThread);
	super.onDestroy();
}


	   public TimePicker dialog(){  
        View view=getLayoutInflater().inflate(R.layout.shijian, null);//    
	  
	        final TimePicker timePicker=(TimePicker)view.findViewById(R.id.timePicker);
//	        final  EditText  oneeditext=(EditText)view.findViewById(R.id.oneeditext);  
//	        final  EditText  twoeditext=(EditText)view.findViewById(R.id.twoeditext);  
	        timePicker.setIs24HourView(true);  
	        
	        
	        new AlertDialog.Builder(this)  
	        .setTitle("设置时间")  
	        .setView(view)  
	        .setPositiveButton("确定", new DialogInterface.OnClickListener() {   
	  
	        public void onClick(DialogInterface dialog, int which) {   
	  

	  
	        }   
	  
	        }).setNegativeButton("取消", null).show();
			   
	  return timePicker;
	        
	    }  
	   

		//UPDATE ABOUT  常量
		@Override
		public boolean onCreateOptionsMenu(Menu menu) {
			// TODO Auto-generated method stub
			menu.add(0, 111, 1, "设置号码");
			menu.add(0, 222, 2, "关于");
			return super.onCreateOptionsMenu(menu);
		}

		@Override
		public boolean onOptionsItemSelected(MenuItem item) {
			// TODO 自动生成的方法存根
			if (111 == item.getItemId()) {
				// 用户点击了更新列表按钮
				setTheNumber();

			} else if (222 == item.getItemId()) {
				// 用户点击了关于按钮
				Intent intent  = new Intent();
				intent.setClass(MainSendActivity.this,About.class);
				MainSendActivity.this.startActivity(intent);
			}
			return super.onOptionsItemSelected(item);
		}

		private void setTheNumber() {
			// TODO 自动生成的方法存根
			final LinearLayout setNumberLayout = (LinearLayout)getLayoutInflater().inflate(R.layout.setnumber, null);
			new AlertDialog.Builder(this).setTitle("设置号码").setView(setNumberLayout).setPositiveButton("确定", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO 自动生成的方法存根
					System.out.println("in okay");
					EditText numberEdit = (EditText) setNumberLayout.findViewById(R.id.setNumberEdit);
					String num="";
					if(numberEdit !=null)
						 num =numberEdit.getText().toString();
					if (num != "" && num != null)  //判断是否是电话号码，以后用正则表达式判断
					
						System.out.println(num+"");
					
						SHASHA = numberEdit.getText().toString();
					sharedPreferences.edit().putString("haoma", SHASHA).commit();   //更新号码
					System.out.println(SHASHA);
				}
			}).setNegativeButton("取消", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO 自动生成的方法存根
					
				}
			}).show();
			
		}





}
