package com.abam.letsshare;

import android.app.*;
import android.os.*;
import android.support.v7.app.AppCompatActivity;
import android.widget.*;
import android.view.*;
import android.content.*;
public class DialogMessage extends Dialog
{
	private AppCompatActivity act;
	private int layout = -1;
	private SetAction action;
	// Start activity without layout defined 2ant to give message, text 
	public DialogMessage(AppCompatActivity a){
		super(a);
		layout = -1;
		act = a;
	}
	// Start activity with layout defined
	public DialogMessage(AppCompatActivity a,int lay){
		super(a);
		layout = lay;
	}
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(layout);
		setOnDismissListener(new DialogInterface.OnDismissListener(){
			@Override public void onDismiss(DialogInterface a){
				if(action != null) action.setDismiss();
			}
		});
		getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
		getWindow().setGravity(Gravity.START);
		if(action != null) action.setFunction(this);
	}
	// set interface
	public void setAction(SetAction a){
		action = a;
	}
	// Interface
	public interface SetAction{
		void setFunction(DialogMessage k);
		void setDismiss();
	}
}
