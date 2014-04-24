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
						// TODO �Զ����ɵķ������
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
						     "���˵ĺ����ǿյ�~", Toast.LENGTH_LONG);
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
				        
				   	 timeTextView.setText((radioGroup.getCheckedRadioButtonId()==R.id.radioButtonOnce) ? ("����һ��ʱ�䣺"+morningTime) : ("ÿ�췢��ʱ�䣺"+morningTime));
				   	 
				        System.out.println("����ʱ��"+morningTime);
				        //sharedPreferences.edit().putString("time", morningMsg).commit();

				        Toast.makeText(getApplicationContext(), "�Ѿ����óɹ�", Toast.LENGTH_SHORT).show();
				         aManager=(AlarmManager)getSystemService(Context.ALARM_SERVICE);   
					        
				         startalarm();//��ʼalarm
					        

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
				// TODO �Զ����ɵķ������
				
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO �Զ����ɵķ������
				
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				// TODO �Զ����ɵķ������
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
				// TODO �Զ����ɵķ������
			timeTextView.setText(hourOfDay+":"+minute);
			sharedPreferences.edit().putString("time", hourOfDay+":"+minute).commit();
			}
		});
		  
	 	 toggleButton.setChecked(sharedPreferences.getBoolean("toggleState", false));//set toggleButton state
	 //	 tp.setCurrentMinute(Calendar.MINUTE);
	 	 
		 Log.e("now hour",""+Calendar.HOUR_OF_DAY+"now minutes"+Calendar.MINUTE);
		 

		morningTime=String.valueOf(tp.getCurrentHour())+":"+String.valueOf(tp.getCurrentMinute());
		 timeTextView = (TextView) findViewById(R.id.timeTextView);
		 timeTextView.setText("����һ��ʱ�䣺"+morningTime);
		 radioGroup.check(R.id.radioButtonOnce);
		 
		SHASHA = sharedPreferences.getString("haoma", null);
		
		
		if (SHASHA == null){
			sharedPreferences.edit().putBoolean("firstStart", true).commit();
		}
		System.out.println(" what is number:"+SHASHA);
		numberTextView.setText("�ֻ����룺"+SHASHA);
		
		 
		
		   handlerThread = new HandlerThread("handler thread");  //���߳�
		  handlerThread.start();
		   myHandler = new MyHandler(handlerThread.getLooper());
		  


	}
	
	private void startalarm() {
		// TODO �Զ����ɵķ������
         Intent intent=new Intent(MainSendActivity.this,AlarmReceiver.class);   
	        intent.setAction("AlarmReceiver");  
	        
	        sharedPreferences.edit().putBoolean("toggleState", true);
	        
	        pendingIntent=PendingIntent.getBroadcast(this, 0, intent, 0);     
	        aManager.setRepeating(AlarmManager.RTC, 0, 60*1000, pendingIntent);   
	}
	
	
	/*���ո���toogleButton�Ĺ㲥
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
		  toggleButton.setChecked(false);//һ�������ֻ���ǵ�һ�η��Ͷ��Ž����󣬷��͵ĸ���toogle�㲥
		    super.handleMessage(msg);
		  }
		  
		 }	  
	
	
	
@Override
protected void onStart() {
	// TODO �Զ����ɵķ������
	  boolean firstStart = sharedPreferences.getBoolean("firstStart", true);//�Ƿ��һ�����ó���
	  System.out.println(firstStart);
	  if (firstStart){  
		sharedPreferences.edit().putBoolean("firstStart", false).commit();
		  setTheNumber();
			numberTextView.setText("�ֻ����룺"+SHASHA);
		
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
			 timeTextView.setText("����һ��ʱ�䣺"+time); 
			 radioGroup.check(R.id.radioButtonOnce);
		 }else{
			 timeTextView.setText("ÿ�췢��ʱ�䣺"+time); 
					 radioGroup.check(R.id.radioButtonEveryday);
		 }
		  
			numberTextView.setText("�ֻ����룺"+SHASHA);
			
		  
	  }
	super.onStart();
}



@Override
protected void onDestroy() {
	// TODO �Զ����ɵķ������
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
	        .setTitle("����ʱ��")  
	        .setView(view)  
	        .setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {   
	  
	        public void onClick(DialogInterface dialog, int which) {   
	  

	  
	        }   
	  
	        }).setNegativeButton("ȡ��", null).show();
			   
	  return timePicker;
	        
	    }  
	   

		//UPDATE ABOUT  ����
		@Override
		public boolean onCreateOptionsMenu(Menu menu) {
			// TODO Auto-generated method stub
			menu.add(0, 111, 1, "���ú���");
			menu.add(0, 222, 2, "����");
			return super.onCreateOptionsMenu(menu);
		}

		@Override
		public boolean onOptionsItemSelected(MenuItem item) {
			// TODO �Զ����ɵķ������
			if (111 == item.getItemId()) {
				// �û�����˸����б�ť
				setTheNumber();

			} else if (222 == item.getItemId()) {
				// �û�����˹��ڰ�ť
				Intent intent  = new Intent();
				intent.setClass(MainSendActivity.this,About.class);
				MainSendActivity.this.startActivity(intent);
			}
			return super.onOptionsItemSelected(item);
		}

		private void setTheNumber() {
			// TODO �Զ����ɵķ������
			final LinearLayout setNumberLayout = (LinearLayout)getLayoutInflater().inflate(R.layout.setnumber, null);
			new AlertDialog.Builder(this).setTitle("���ú���").setView(setNumberLayout).setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO �Զ����ɵķ������
					System.out.println("in okay");
					EditText numberEdit = (EditText) setNumberLayout.findViewById(R.id.setNumberEdit);
					String num="";
					if(numberEdit !=null)
						 num =numberEdit.getText().toString();
					if (num != "" && num != null)  //�ж��Ƿ��ǵ绰���룬�Ժ���������ʽ�ж�
					
						System.out.println(num+"");
					
						SHASHA = numberEdit.getText().toString();
					sharedPreferences.edit().putString("haoma", SHASHA).commit();   //���º���
					System.out.println(SHASHA);
				}
			}).setNegativeButton("ȡ��", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO �Զ����ɵķ������
					
				}
			}).show();
			
		}





}
