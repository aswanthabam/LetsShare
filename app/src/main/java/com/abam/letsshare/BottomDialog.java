package com.abam.letsshare;

import android.app.*;
import android.os.*;
import android.widget.*;
import android.view.*;
import android.support.v7.app.AppCompatActivity;
public class BottomDialog extends Dialog
{
	private int layoutIs;
	public AppCompatActivity activity;
	private SetActions listener;
	// Normal Constructor
	public BottomDialog(AppCompatActivity a){
		super(a,R.style.MaterialDialogSheet);
		activity = a;
		layoutIs = R.layout.bottom_popup;
	}
	// Constructor with layout defined
	public BottomDialog(AppCompatActivity a,int layout){
		super(a,R.style.MaterialDialogSheet);
		activity = a;
		layoutIs = layout;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		// TODO: Implement this method
		super.onCreate(savedInstanceState);
		//View v = findViewById(R.id.drawer_layout);
		setContentView(R.layout.main);
		getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
		getWindow().setGravity(Gravity.BOTTOM);
		MainActivity ku = new MainActivity(activity,this);
		if(listener != null){
			listener.setDeviceList(ku);
		}
		ku.onCreate();
	}
	public void setListener(SetActions m){
		listener = m;
	}
	// Set interface
	public interface SetActions{
		void setDeviceList(MainActivity activitys);
	}
}
