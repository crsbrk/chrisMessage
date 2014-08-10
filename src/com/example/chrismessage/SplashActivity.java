package com.example.chrismessage;

import java.util.Random;



import net.youmi.android.AdManager;
import net.youmi.android.spot.SpotManager;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View.OnCreateContextMenuListener;

public class SplashActivity extends Activity{
	
	 private final int SPLASH_DISPLAY_LENGHT = 2000; //�ӳ�����

	 @Override
	 protected void onCreate(Bundle savedInstanceState) {
		 super.onCreate(savedInstanceState);
		 setContentView(R.layout.splash);
		 AdManager.getInstance(this).init("77d541676ffdbc6a", "914b103e8896e83a", false);
		// �������½ӿڹر����׹�� SDK ��ص� log
		 AdManager.getInstance(this).setEnableDebugLog(false);
		 
		 SpotManager.getInstance(this).loadSpotAds();
		 SpotManager.getInstance(this).setSpotTimeout(4000); // 5��
		 
		 
		 
//		 int[] backgroundpics = new int[] {R.drawable.start0 ,R.drawable.start1,R.drawable.start2,R.drawable.start3,R.drawable.start4,R.drawable.start5,
//				 R.drawable.start6,R.drawable.start7,R.drawable.start8,R.drawable.start9};
//         int whichPic = new Random().nextInt(10);
		 getWindow().setBackgroundDrawableResource(R.drawable.start);
		         new Handler().postDelayed(new Runnable(){
		 
		  
		 
		          @Override
		 
		          public void run() {
		 
		              Intent mainIntent = new Intent(SplashActivity.this,MainSendActivity.class);
		              SplashActivity.this.startActivity(mainIntent);
		              SplashActivity.this.finish();
		 
		          }
		 
		              
	
		         }, SPLASH_DISPLAY_LENGHT);

}
	 
}



