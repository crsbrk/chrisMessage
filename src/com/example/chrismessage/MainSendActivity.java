package com.example.chrismessage;

import java.util.Calendar;

import com.example.wheel.NumericWheelAdapter;
import com.example.wheel.OnWheelChangedListener;
import com.example.wheel.OnWheelScrollListener;
import com.example.wheel.WheelView;


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
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.preference.Preference;
import android.provider.ContactsContract;
import android.provider.ContactsContract.Contacts;
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
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.TimePicker.OnTimeChangedListener;
import android.widget.Toast;
import android.widget.ToggleButton;

import net.youmi.android.AdManager;

import net.youmi.android.banner.AdSize;
import net.youmi.android.banner.AdView;
import net.youmi.android.smart.SmartBannerManager;
import net.youmi.android.spot.SpotManager;

public class MainSendActivity extends Activity {

	protected static String SHASHA = "";
	protected static String phoneNumber = "5554";
	private String morningMsg = " ";

	private EditText morningEditText = null;

	private Button startServiceButton = null;
	private Button stopServiceButton = null;
	private ImageButton imageBtn = null;

	private ToggleButton toggleButton = null;

	private String morningTime = "0:00";

	private String numPhone = "10010";// 从通讯录里获取的用户的电话号码

	Dialog dialog = null;
	Calendar calendar = Calendar.getInstance();
	private SharedPreferences sharedPreferences;
	private AlarmManager aManager = null;
	private PendingIntent pendingIntent;
	private TextView timeTextView;
	private TimePicker tp = null;
	private RadioGroup radioGroup = null;
	private RadioButton radioOnce = null;
	private RadioButton radioWeek = null;
	boolean toggleState = false;
	private TextView numberTextView = null;
	private ImageButton setRandomTimeButton = null;

	private MyHandler myHandler = null;
	private HandlerThread handlerThread = null;
	private ToggleReveiver tbr = null;
	private IntentFilter filter = null;

	private LinearLayout setNumberLayout;
	private EditText numberEdit;
	AlertDialog contactDialog;

	private Uri uriContact;
	private String contactID; // contacts unique ID

	// Time changed flag
	private boolean timeChanged = false;

	//
	private boolean timeScrolled = false;

	private WheelView hours;
	private WheelView mins;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.one_msg_send);

		Log.e("on create", "on create");
		// int[] backgroundpics = new int[] {R.drawable.start0
		// ,R.drawable.start1,R.drawable.start2,R.drawable.start3,R.drawable.start4,R.drawable.start5};
		// int whichPic = new Random().nextInt(6);
		// getWindow().setBackgroundDrawableResource(R.drawable.bg1);

		//SmartBannerManager.init(this);
		//SmartBannerManager.show(this);

		SpotManager.getInstance(this).showSpotAds(this);
		// 实例化广告条
		// AdView adView = new AdView(this, AdSize.FIT_SCREEN);
		// 获取要嵌入广告条的布局
		// LinearLayout adLayout = (LinearLayout) findViewById(R.id.adLayout);
		// 将广告条加入到布局中
		// adLayout.addView(adView);

		setMyTimePicker();

		registerToggleReceiver();

		sharedPreferences = getSharedPreferences("alarm_record",
				Activity.MODE_PRIVATE);

		morningEditText = (EditText) findViewById(R.id.morningEditText);
		numberTextView = (TextView) findViewById(R.id.numberTextView);
		setRandomTimeButton = (ImageButton) findViewById(R.id.setRandomTimeImageButton);
		toggleButton = (ToggleButton) findViewById(R.id.toggleButton1);

		numberTextView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				setTheNumber(ConstApp.NORMAL_GET_FROM_SHAREPREFERENCE);
			}
		});

		setRandomTimeButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO 自动生成的方法存根
				setRandomMode4msg();
			}
		});
		// radioGroup

		radioGroup = (RadioGroup) findViewById(R.id.radioGroup);
		radioOnce = (RadioButton) findViewById(R.id.radioButtonOnce);
		radioWeek = (RadioButton) findViewById(R.id.radioButtonEveryday);
		radioGroup
				.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(RadioGroup group, int checkedId) {
						// TODO 自动生成的方法存根
						String timeStringTextView = (0 == sharedPreferences
								.getInt("random", 0)) ? "" : getResources()
								.getString(R.string.almost);
						
						int id = group.getCheckedRadioButtonId();
						System.out.println("group.getcheckradionButtonid()"
								+ id);
						switch (group.getCheckedRadioButtonId()) {
						case R.id.radioButtonOnce:
							sharedPreferences.edit()
									.putInt("frequency", R.id.radioButtonOnce)
									.commit();
							
							
							

							String sendOnceString = getResources().getString(
									R.string.once);


							timeTextView.setText(timeStringTextView+ sendOnceString + morningTime);

							System.out.println("set once");
							break;
						case R.id.radioButtonEveryday:
							sharedPreferences
									.edit()
									.putInt("frequency",
											R.id.radioButtonEveryday).commit();
							


							String sendEverydayString = getResources().getString(
									R.string.everyday);

							timeTextView.setText(timeStringTextView + sendEverydayString + morningTime);

							System.out.println("set everyday");
							break;

						}

					}
				});

		toggleButton.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				System.out.println("ischecked" + isChecked);
				SHASHA = sharedPreferences.getString("haoma", "");
				if (SHASHA.isEmpty() || SHASHA == null) {
					// buttonView.setChecked(false);
					toggleButton.setChecked(false);
					isChecked = false;
					sharedPreferences.edit().putBoolean("toggleState", false)
							.commit();

					Toast toast = Toast.makeText(getApplicationContext(),
							"情人的号码是空的~", Toast.LENGTH_LONG);
					toast.setGravity(Gravity.CENTER, 0, 0);
					toast.show();
				}

				boolean serving = sharedPreferences.getBoolean("toggleState",
						false);
				// 打开开关，没运行
				if (isChecked && !serving) {
					//
					if (morningEditText.getText().toString() != "") {
						morningMsg = morningEditText.getText().toString();
					}
					if (sharedPreferences.getString("time", "0:00") == null
							|| sharedPreferences.getString("time", "0:00")
									.trim() == "") {
						morningTime = String.valueOf(hours.getCurrentItem())
								+ ":" + String.valueOf(mins.getCurrentItem());

						String wheeltest = String.valueOf(hours
								.getCurrentItem())
								+ ":"
								+ String.valueOf(mins.getCurrentItem());

						Log.i("morningTime", morningTime);
						Log.i("wheelTime", wheeltest);

					} else {
						morningTime = sharedPreferences.getString("time",
								"0:00");
					}

					sharedPreferences.edit().putString("time", morningTime)
							.commit();
					sharedPreferences.edit().putString("haoma", SHASHA)
							.commit();
					sharedPreferences.edit().putString("neirong", morningMsg)
							.commit();
					sharedPreferences
							.edit()
							.putInt("frequency",
									radioGroup.getCheckedRadioButtonId())
							.commit();
					sharedPreferences.edit().putBoolean("toggleState", true)
							.commit();
					System.out.println("morningTime--->" + morningTime);

					// System.out.print(radioGroup.getId()+"=="+R.id.radioButtonOnce);
					if (sharedPreferences.getInt("frequency",
							R.id.radioButtonOnce) == -1) {
						sharedPreferences.edit()
								.putInt("frequency", R.id.radioButtonOnce)
								.commit();
						radioGroup.check(R.id.radioButtonOnce);
					} else {
						String timeStringTextView = (0 == sharedPreferences
								.getInt("random", 0)) ? "" : getResources()
								.getString(R.string.almost);
						String sendOnceString = getResources().getString(
								R.string.once);
						String sendEverydayString = getResources().getString(
								R.string.everyday);

						timeTextView.setText((radioGroup
								.getCheckedRadioButtonId() == R.id.radioButtonOnce) ? (timeStringTextView
								+ sendOnceString + morningTime)
								: (timeStringTextView + sendEverydayString + morningTime));
					}
					System.out.println("设置时间" + morningTime);
					// sharedPreferences.edit().putString("time",
					// morningMsg).commit();

					Toast toast = Toast.makeText(getApplicationContext(),
							getResources().getString(R.string.toastSucess),
							Toast.LENGTH_SHORT);
					toast.setGravity(Gravity.CENTER, 0, 0);
					toast.show();

					startalarm();// 开始alarm

				} else if (isChecked && serving
						&& sharedPreferences.getBoolean("reboot", false)) {// 没有关闭服务，而且还在记录还是运行状态
					// 如果重启，则开启闹钟
					System.out.println("reboot!!!start alarm");
					// if(sharedPreferences.getBoolean("reboot", false)){
					startalarm();
					// 启动后设置回false
					sharedPreferences.edit().putBoolean("reboot", false)
							.commit();

				}

				else {// 关闭服务，选择关闭
						// 运行
					if (!isChecked
							&& sharedPreferences.getBoolean("toggleState",
									false)) { // 按钮选择关闭，但是配置文件现实还在运行，则需要关闭

						System.out.println("cancel aManager pending in else ");
						System.out.println("aManager" + aManager);
						if (aManager != null) {

							aManager.cancel(pendingIntent);
						}
						sharedPreferences.edit()
								.putBoolean("toggleState", false).commit();
						// toggleButton.setChecked(false);
						System.out.println("stop service");

					}

				}
			}

		});

		morningMsg = sharedPreferences.getString("neirong", "");
		morningEditText.setText(morningMsg);
		morningEditText.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
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
				sharedPreferences
						.edit()
						.putString("neirong",
								morningEditText.getText().toString()).commit();
			}
		});

		calendar.setTimeInMillis(System.currentTimeMillis());

		toggleButton.setChecked(sharedPreferences.getBoolean("toggleState",
				false));// set toggleButton state
		// tp.setCurrentMinute(Calendar.MINUTE);

		Log.e("now hour", "" + Calendar.HOUR_OF_DAY + "now minutes"
				+ Calendar.MINUTE);

		// 已经在preference中有时间了，就用存储的时间，没有的话再用当前时间
		if (sharedPreferences.getString("time", null) == null
				|| sharedPreferences.getString("time", null).trim() == "") {
			morningTime = String.valueOf(hours.getCurrentItem()) + ":"
					+ String.valueOf(mins.getCurrentItem());

			// String test = String.valueOf(hours.getCurrentItem()) + ":"
			// + String.valueOf(mins.getCurrentItem());
			// System.out.println("time from wheel"+test);
			System.out.println("time:" + morningTime);
			sharedPreferences.edit().putString("time", morningTime).commit();

		} else {
			morningTime = sharedPreferences.getString("time", "0:00");
		}

		if (sharedPreferences.getInt("frequency", R.id.radioButtonOnce) == R.id.radioButtonOnce) {

			String timeStringTextView = (0 == sharedPreferences.getInt(
					"random", 0)) ? "" : getResources().getString(
					R.string.almost);
			String sendOnceString = getResources().getString(R.string.once);

			timeTextView.setText(timeStringTextView + sendOnceString
					+ morningTime);
			radioGroup.check(R.id.radioButtonOnce);

		} else {

			String timeStringTextView = (0 == sharedPreferences.getInt(
					"random", 0)) ? "" : getResources().getString(
					R.string.almost);
			String sendEverydayString = getResources().getString(
					R.string.everyday);

			timeTextView.setText(timeStringTextView + sendEverydayString
					+ morningTime);
			radioGroup.check(R.id.radioButtonEveryday);

		}

		SHASHA = sharedPreferences.getString("haoma", null);

		System.out.println(" what is number:" + SHASHA + "---");

		String setNumStr = getResources().getString(R.string.clickMeSetNumber);
		if (SHASHA == null) {
			sharedPreferences.edit().putBoolean("firstStart", true).commit();
			// System.out.println("");

			numberTextView.setText(setNumStr);

		} else if (SHASHA.isEmpty() || SHASHA.equals("null")
				|| SHASHA.equals("") || SHASHA == "") {
			numberTextView.setText(setNumStr);
		} else {
			String otherNumber = getResources().getString(R.string.otherNumber);
			numberTextView.setText(otherNumber + SHASHA);
		}

		// handlerThread = new HandlerThread("handler thread"); //新线程
		// handlerThread.start();
		// myHandler = new MyHandler(handlerThread.getLooper());
		myHandler = new MyHandler();

	}

	/**
	 * 注册广播接收
	 * */
	private void registerToggleReceiver() {
		// TODO Auto-generated method stub
		tbr = new ToggleReveiver();
		filter = new IntentFilter();
		filter.addAction(ConstApp.CHANGETOOGLE_ACTION);
		registerReceiver(tbr, filter);
	}

	/**
	 * 开启闹钟
	 * */
	private void startalarm() {
		// TODO 自动生成的方法存根
		aManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

		System.out.println("start alarm!");
		Intent intent = new Intent(MainSendActivity.this, AlarmReceiver.class);
		intent.setAction("AlarmReceiver");
		if (!sharedPreferences.getBoolean("toggleState", true)) {
			sharedPreferences.edit().putBoolean("toggleState", true).commit();
		}
		System.out.println();
		pendingIntent = PendingIntent.getBroadcast(this, 0, intent, 0);
		aManager.setRepeating(AlarmManager.RTC, 0, 60 * 1000, pendingIntent);
	}

	/**
	 * 接收更新toogleButton的广播
	 * **/
	class ToggleReveiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {

			System.out.println("toggleReceiver");
			Message msg = myHandler.obtainMessage();
			msg.arg1 = R.id.toggleButton1;
			msg.sendToTarget();
		}

	}

	/**
	 * 更新界面
	 * */
	class MyHandler extends Handler {
		public MyHandler() {
		}

		public MyHandler(Looper looper) {
			super(looper);
		}

		@Override
		public void handleMessage(Message msg) {
			System.out.println("handling message and put toggleButton false");
			int progress = msg.arg1;
			toggleButton.setChecked(false);// 一般情况下只会是第一次发送短信结束后，发送的更新toogle广播

			super.handleMessage(msg);
		}

	}

	@Override
	protected void onStart() {
		// TODO 自动生成的方法存根
		boolean firstStart = sharedPreferences.getBoolean("firstStart", true);// 是否第一次启用程序
		Log.e("on start", "on start");
		System.out.println("firststart:" + firstStart);

		if (firstStart) {
			sharedPreferences.edit().putBoolean("firstStart", false).commit();
			setTheNumber(ConstApp.NORMAL_GET_FROM_SHAREPREFERENCE);

			if (SHASHA != null || SHASHA != "") {
				numberTextView.setText(getResources().getString(
						R.string.otherNumber)
						+ SHASHA);
			} else {
				numberTextView.setText(getResources().getString(
						R.string.clickMeSetNumber));
			}

		} else {

			int rand = sharedPreferences.getInt("random", 0);
			if (0 == rand) {
				setRandomTimeButton.setImageDrawable(getResources()
						.getDrawable(R.drawable.unsafe));
			} else {
				setRandomTimeButton.setImageDrawable(getResources()
						.getDrawable(R.drawable.safe));
			}

			toggleButton.setChecked(sharedPreferences.getBoolean("toggleState",
					false));// set toggleButton state
			morningEditText.setText(sharedPreferences
					.getString("neirong", null));

			timeTextView.setText(sharedPreferences.getString("time", "0:00"));
			int checkId = sharedPreferences.getInt("frequency",
					R.id.radioButtonOnce);
			radioGroup.check(checkId);
			String time = sharedPreferences.getString("time", "0:00");
			String[] str = time.split(":");

			System.out.println(str[0]);
			System.out.println(str[1]);

			// tp.setCurrentHour(Integer.parseInt(str[0]));
			// tp.setCurrentMinute(Integer.parseInt(str[1]));
			hours.setCurrentItem(Integer.parseInt(str[0]));
			mins.setCurrentItem(Integer.parseInt(str[1]));

			String randStr = (0 == sharedPreferences.getInt("random", 0)) ? ""
					: getResources().getString(R.string.almost);

			System.out.println("checkId" + checkId);
			if (checkId == R.id.radioButtonOnce) {

				String sendOnceString = getResources().getString(R.string.once);
				timeTextView.setText(randStr + sendOnceString + time);
				radioGroup.check(R.id.radioButtonOnce);
			} else {

				String sendEveryDay = getResources().getString(
						R.string.everyday);
				timeTextView.setText(randStr + sendEveryDay + time);
				radioGroup.check(R.id.radioButtonEveryday);
			}

			if (SHASHA != null || SHASHA != "") {
				numberTextView.setText(getResources().getString(
						R.string.otherNumber)
						+ SHASHA);
			} else {
				numberTextView.setText(getResources().getString(
						R.string.clickMeSetNumber));
			}
			// numberTextView.setText("手机号码："+SHASHA);

		}
		super.onStart();
	}

	@Override
	protected void onDestroy() {
		// TODO 自动生成的方法存根
		// aManager.cancel(pendingIntent);
		// sharedPreferences.edit().putBoolean("toggleState", false);

		unregisterReceiver(tbr);
		myHandler.removeCallbacks(handlerThread);
		super.onDestroy();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		Log.e("onResume", "onResume");

		// Toast.makeText(getApplicationContext(), numPhone,
		// Toast.LENGTH_SHORT).show();

		// refreshEditText(numPhone);
		super.onResume();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		Log.e("onPause", "onPause");

		super.onPause();
	}

	/**
	 * 设置时间
	 * */
	public TimePicker dialog() {
		View view = getLayoutInflater().inflate(R.layout.shijian, null);//

		final TimePicker timePicker = (TimePicker) view
				.findViewById(R.id.timePicker);
		// final EditText
		// oneeditext=(EditText)view.findViewById(R.id.oneeditext);
		// final EditText
		// twoeditext=(EditText)view.findViewById(R.id.twoeditext);
		timePicker.setIs24HourView(true);
		String setTimeStr = getResources().getString(R.string.setTimeStr);
		String sureStr = getResources().getString(R.string.sureStr);
		String cancelStr = getResources().getString(R.string.cancel);
		new AlertDialog.Builder(this)
				.setTitle(setTimeStr)
				.setView(view)
				.setPositiveButton(sureStr,
						new DialogInterface.OnClickListener() {

							public void onClick(DialogInterface dialog,
									int which) {

							}

						}).setNegativeButton(cancelStr, null).show();

		return timePicker;

	}

	// UPDATE ABOUT 常量
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		String aboutStr = getResources().getString(R.string.about);
		String numberStr = getResources().getString(R.string.setNumberStr);
		menu.add(0, 111, 1, numberStr);
		menu.add(0, 222, 2, aboutStr);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO 自动生成的方法存根
		if (111 == item.getItemId()) {
			// 用户点击了更新列表按钮
			setTheNumber(ConstApp.NORMAL_GET_FROM_SHAREPREFERENCE);

		} else if (222 == item.getItemId()) {
			// 用户点击了关于按钮
			Intent intent = new Intent();
			intent.setClass(MainSendActivity.this, About.class);
			MainSendActivity.this.startActivity(intent);
		}
		return super.onOptionsItemSelected(item);
	}

	protected void refreshEditText(String str) {
		setNumberLayout = (LinearLayout) getLayoutInflater().inflate(
				R.layout.setnumber, null);
		numberEdit = (EditText) setNumberLayout
				.findViewById(R.id.setNumberEdit);

		numberEdit.setText(str);

		numberEdit.invalidate();
		Toast.makeText(this, str, DEFAULT_KEYS_SHORTCUT).show();

		imageBtn = (ImageButton) setNumberLayout
				.findViewById(R.id.moreContactBtn);
		imageBtn.setOnClickListener(new imageBtnLsner());
	}

	/**
	 * set 号码设置的layout
	 * */
	protected void getSetNumberlayout() {
		setNumberLayout = (LinearLayout) getLayoutInflater().inflate(
				R.layout.setnumber, null);
		numberEdit = (EditText) setNumberLayout
				.findViewById(R.id.setNumberEdit);

		numberEdit.setText(sharedPreferences.getString("haoma", ""));

		imageBtn = (ImageButton) setNumberLayout
				.findViewById(R.id.moreContactBtn);
		imageBtn.setOnClickListener(new imageBtnLsner());

	}

	private void setTheNumber(int flag) {
		// TODO 自动生成的方法存根
		// final LinearLayout setNumberLayout =
		// (LinearLayout)getLayoutInflater().inflate(R.layout.setnumber, null);
		// final EditText numberEdit = (EditText)
		// setNumberLayout.findViewById(R.id.setNumberEdit);
		//
		// imageBtn =
		// (ImageButton)setNumberLayout.findViewById(R.id.moreContactBtn);
		// imageBtn.setOnClickListener(new imageBtnLsner() );

		if (flag == ConstApp.FRESH_FROM_CONTENT_NUMBER) { // 如果是从通讯录里更新的号码，就用numphone更新，否则用shareprefernce更新Edittext
			refreshEditText(numPhone);
		} else {
			getSetNumberlayout();
		}
		// showDialog(ConstApp.DIALOG_CONTACT_NUMBER);

		String otherNumber = getResources().getString(R.string.setOtherNumber);
		String sureStr = getResources().getString(R.string.sureStr);
		String cancelStr = getResources().getString(R.string.cancel);
		final String phoneNumberStr = getResources().getString(
				R.string.phoneNumber);
		final String clickMeSetNum = getResources().getString(
				R.string.click_me_to_setnumber);

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.otherNumber);
		builder.setView(setNumberLayout);

		builder.setNegativeButton(cancelStr,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO 自动生成的方法存根
						// dialog.cancel();
					}
				});

		builder.setPositiveButton(sureStr,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO 自动生成的方法存根
						System.out.println("in okay");
						// numberEdit.setText(numPhone);
						Log.e("pick phone num", numPhone);

						String num = "";
						if (numberEdit != null) {
							num = numberEdit.getText().toString();
							if (num.trim() == null || num.trim() == "") {
								// num="未设置号码";
							}
						}
						if (num != "" && num != null) // 判断是否是电话号码，以后用正则表达式判断

							System.out.println(num + "");

						SHASHA = numberEdit.getText().toString();
						sharedPreferences.edit().putString("haoma", SHASHA)
								.commit(); // 更新号码
						System.out.println(SHASHA);

						if (SHASHA != null || SHASHA != "") {
							numberTextView.setText(phoneNumberStr + SHASHA);
						} else {
							numberTextView.setText(clickMeSetNum);
						}
						// numberTextView.setText("手机号码："+SHASHA);
					}
				});

		contactDialog = builder.create();

		contactDialog.show();
		// builder.show();
		// numberEdit.setText(str);

	}

	class imageBtnLsner implements OnClickListener {
		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			System.out.println("onclick imageButton");
			contactDialog.cancel();
			readcontact();

		}
	}

	public void readcontact() {
		try {
			// Intent intent = new Intent(Intent.ACTION_PICK,
			// Uri.parse("content://contacts/people"));

			Intent intent = new Intent(Intent.ACTION_PICK,
					ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
			startActivityForResult(intent, ConstApp.PICK_CONTACT);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		switch (requestCode) {
		case ConstApp.PICK_CONTACT:
			if (resultCode == RESULT_OK) {

				Log.d("onActivityContact", "Response: " + data.toString());
				uriContact = data.getData();
				retrieveContactNumber();
				/*
				 * final Uri uriRet = data.getData(); if (uriRet != null) { //
				 * android.permission.read_contacts 权限 Cursor c =
				 * managedQuery(uriRet, null, null, null, null); //
				 * cursor移动到数据最前端 c.moveToFirst(); // 取得联系人姓名 String strName = c
				 * .getString(c
				 * .getColumnIndexOrThrow(ContactsContract.Contacts.
				 * DISPLAY_NAME)); // 取得联系人电话 int contactId =
				 * c.getInt(c.getColumnIndex(ContactsContract.Contacts._ID));
				 * 
				 * Cursor phones = getContentResolver().query(
				 * ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
				 * ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " +
				 * contactId, null, null);
				 * 
				 * StringBuffer sb = new StringBuffer(); int typePhone, resType;
				 * // String numPhone;
				 * 
				 * if (phones.getCount() > 0) { phones.moveToFirst();
				 * 
				 * typePhone = phones .getInt(phones
				 * .getColumnIndex(ContactsContract
				 * .CommonDataKinds.Phone.TYPE));
				 * 
				 * numPhone = phones .getString(phones
				 * .getColumnIndex(ContactsContract
				 * .CommonDataKinds.Phone.NUMBER));
				 * 
				 * resType = ContactsContract.CommonDataKinds.Phone
				 * .getTypeLabelResource(typePhone);
				 * sb.append(getString(resType) + ":" + numPhone + "\n");
				 * System.out.println(sb); System.out.println(numPhone);
				 * setTheNumber(ConstApp.FRESH_FROM_CONTENT_NUMBER); //
				 * sharedPreferences.edit().putString("haoma", //
				 * numPhone).commit(); //refreshEditText(numPhone); }
				 * 
				 * }
				 */
			}
			break;

		default:
			break;
		}
		super.onActivityResult(requestCode, resultCode, data);

	}

	private void retrieveContactNumber() {

		String contactNumber = null;

		// getting contacts ID
		Cursor cursorID = getContentResolver().query(uriContact,
				new String[] { ContactsContract.Contacts._ID }, null, null,
				null);

		if (cursorID.moveToFirst()) {

			contactID = cursorID.getString(cursorID
					.getColumnIndex(ContactsContract.Contacts._ID));
		}

		cursorID.close();

		Log.d("onActivityContact", "Contact ID: " + contactID);

		// Using the contact ID now we will get contact phone number
		// Cursor cursorPhone =
		// getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
		// new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER},
		//
		// ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ? AND " +
		// ContactsContract.CommonDataKinds.Phone.TYPE + " = " +
		// ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE,
		//
		// new String[]{contactID},
		// null);

		Cursor cursorPhone = getContentResolver().query(
				ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
				ContactsContract.CommonDataKinds.Phone._ID + " = " + contactID,
				null, null);

		// Cursor cursorPhone =
		// getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_FILTER_URI,
		// null,
		//
		// ContactsContract.CommonDataKinds.Phone._ID + "= ?",
		//
		// new String[]{contactID},
		// null);

		Log.e("cursorPhone", "cursorPhone: " + cursorPhone.moveToFirst());

		if (cursorPhone.moveToFirst()) {

			contactNumber = cursorPhone
					.getString(cursorPhone
							.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
		}

		cursorPhone.close();
		numPhone = contactNumber;
		setTheNumber(ConstApp.FRESH_FROM_CONTENT_NUMBER);
		Log.d("onActivityContact", "Contact Phone Number: " + contactNumber);
	}

	private void setMyTimePicker() {
		timeTextView = (TextView) findViewById(R.id.timeTextView);

		// tp = (TimePicker) findViewById(R.id.timePicker1);
		// tp.setIs24HourView(true);
		// tp.setCurrentHour(calendar.get(Calendar.HOUR_OF_DAY));

		// tp.setOnTimeChangedListener(new OnTimeChangedListener() {
		//
		// @Override
		// public void onTimeChanged(TimePicker view, int hourOfDay, int minute)
		// {
		// // TODO 自动生成的方法存根
		// timeTextView.setText("设置时间：" + hourOfDay + ":" + minute);
		// sharedPreferences.edit()
		// .putString("time", hourOfDay + ":" + minute).commit();
		// }
		// });

		hours = (WheelView) findViewById(R.id.hour);
		hours.setAdapter(new NumericWheelAdapter(0, 23));
		hours.setLabel("hours");

		mins = (WheelView) findViewById(R.id.mins);
		mins.setAdapter(new NumericWheelAdapter(0, 59, "%02d"));
		mins.setLabel("mins");
		mins.setCyclic(true);

		// final TimePicker picker = (TimePicker) findViewById(R.id.time);
		// picker.setIs24HourView(true);

		// set current time
		Calendar c = Calendar.getInstance();
		int curHours = c.get(Calendar.HOUR_OF_DAY);
		int curMinutes = c.get(Calendar.MINUTE);

		hours.setCurrentItem(curHours);
		mins.setCurrentItem(curMinutes);

		// add listeners
		addChangingListener(mins, "min");
		addChangingListener(hours, "hour");

		OnWheelChangedListener wheelListener = new OnWheelChangedListener() {
			public void onChanged(WheelView wheel, int oldValue, int newValue) {
				if (!timeScrolled) {
					timeChanged = true;
					// tp.setCurrentHour(hours.getCurrentItem());
					// tp.setCurrentMinute(mins.getCurrentItem());

					morningTime = hours.getCurrentItem() + ":"
							+ mins.getCurrentItem();
					sharedPreferences.edit().putString("time", morningTime)
							.commit();

					String timeStringTextView = (0 == sharedPreferences.getInt(
							"random", 0)) ? "" : getResources().getString(
							R.string.almost);
					String sendOnceString = getResources().getString(
							R.string.once);
					String sendEverydayString = getResources().getString(
							R.string.everyday);

					String timeTextViewStr = (sharedPreferences.getInt(
							"frequency", R.id.radioButtonOnce) == R.id.radioButtonOnce) ? sendOnceString
							: sendEverydayString;

					timeTextView.setText(timeStringTextView + timeTextViewStr
							+ morningTime);

					if (0 != sharedPreferences.getInt("random", 0)) {
						sharedPreferences.edit()
								.putBoolean("randomFirstFlag", true).commit();// 更改时间时，如果设置为随机模式，则改变为true，否则不改变
					}
					timeChanged = false;

					// Log.e("in wheel changed time",tp.getCurrentHour()+"h"+tp.getCurrentMinute()+"m");
					// System.out.println(tp.getCurrentHour()+"h"+tp.getCurrentMinute()+"m");
				}
			}
		};

		hours.addChangingListener(wheelListener);
		mins.addChangingListener(wheelListener);

		OnWheelScrollListener scrollListener = new OnWheelScrollListener() {
			public void onScrollingStarted(WheelView wheel) {
				timeScrolled = true;
			}

			public void onScrollingFinished(WheelView wheel) {
				timeScrolled = false;
				timeChanged = true;
				// tp.setCurrentHour(hours.getCurrentItem());
				// tp.setCurrentMinute(mins.getCurrentItem());
				morningTime = hours.getCurrentItem() + ":"
						+ mins.getCurrentItem();
				sharedPreferences.edit().putString("time", morningTime)
						.commit();

				String timeStringTextView = (0 == sharedPreferences.getInt(
						"random", 0)) ? "" : getResources().getString(
						R.string.almost);
				String sendOnceString = getResources().getString(R.string.once);
				String sendEverydayString = getResources().getString(
						R.string.everyday);

				String timeTextViewStr = (sharedPreferences.getInt("frequency",
						R.id.radioButtonOnce) == R.id.radioButtonOnce) ? sendOnceString
						: sendEverydayString;

				timeTextView.setText(timeStringTextView + timeTextViewStr
						+ morningTime);

				if (0 != sharedPreferences.getInt("random", 0)) {
					sharedPreferences.edit()
							.putBoolean("randomFirstFlag", true).commit();// 更改时间时，如果设置为随机模式，则改变为true，否则不改变
				}

				timeChanged = false;

				// Log.e(" in srcoll time",tp.getCurrentHour()+"h"+tp.getCurrentMinute()+"m");
				// System.out.println(tp.getCurrentHour()+"h"+tp.getCurrentMinute()+"m");

			}
		};

		hours.addScrollingListener(scrollListener);
		mins.addScrollingListener(scrollListener);

	}

	/**
	 * Adds changing listener for wheel that updates the wheel label
	 * 
	 * @param wheel
	 *            the wheel
	 * @param label
	 *            the wheel label
	 */
	private void addChangingListener(final WheelView wheel, final String label) {
		wheel.addChangingListener(new OnWheelChangedListener() {
			public void onChanged(WheelView wheel, int oldValue, int newValue) {
				wheel.setLabel(newValue != 1 ? label + "s" : label);
			}
		});
	}

	private void setRandomMode4msg() {
		AlertDialog.Builder builder = new AlertDialog.Builder(
				MainSendActivity.this);
		builder.setMessage(R.string.setRandomMode)
				.setPositiveButton(R.string.ok,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								setconfig(10);// 短信大概随机5分钟
								timeTextView.setText(getResources().getString(
										R.string.almost)
										+ timeTextView.getText());
							}
						})
				.setNegativeButton(R.string.cancel,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								// cancel 什么也不做

							}
						})
				.setNeutralButton(R.string.not_set,
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface arg0, int arg1) {
								// TODO Auto-generated method stub
								setconfig(0);// 短信精确定时发送
							}
						});
		builder.show();

	}

	private void setconfig(int rand) {

		// 在alarm_record 中设置随机条件为5，只要不是0，就可以随机n分钟
		sharedPreferences.edit().putInt("random", rand).commit();

		if (rand != 0) {
			// 更改设置随机的按钮的图片
			setRandomTimeButton.setImageDrawable(getResources().getDrawable(
					R.drawable.safe));
			sharedPreferences.edit().putBoolean("randomFirstFlag", true)
					.commit(); // 设置第一次产生随机时间点，随机时间产生后就停止再调用随机数

		} else {
			setRandomTimeButton.setImageDrawable(getResources().getDrawable(
					R.drawable.unsafe));
		}
	}

}
