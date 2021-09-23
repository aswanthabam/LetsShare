package com.abam.letsshare;

import android.support.v7.app.*;
import android.os.*;
import android.content.*;
public class SplashActivity extends AppCompatActivity
{
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		new Handler().postDelayed(new Runnable(){
			@Override
			public void run(){
				startActivity(new Intent(SplashActivity.this,ActivityMain.class));
				finish();
			}
		},2000);
	}
}
