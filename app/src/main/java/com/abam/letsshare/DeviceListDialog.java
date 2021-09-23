package com.abam.letsshare;
import android.app.DialogFragment;
import android.app.*;
import android.os.*;
import android.content.*;
import android.widget.*;

public class DeviceListDialog extends DialogFragment
{
	public Context context;
	public ArrayAdapter<String> mAdapter;
	public OnDeviceSelectedListener listener;
	public DeviceListDialog(Context c,ArrayAdapter<String> m){
		context = c;
		mAdapter = m;
	}
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState)
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle("Select Device").setSingleChoiceItems(mAdapter,0,new DialogInterface.OnClickListener(){
			@Override public void onClick(DialogInterface p1,int p2){
				listener.onDeviceSelected(p2);
				p1.dismiss();
			}
		}).setNegativeButton("Cancel",new DialogInterface.OnClickListener(){
			@Override public void onClick(DialogInterface dialog,int which){
				dialog.dismiss();
			}
		});
		return builder.create();
	}
	// Set Device selected listener
	public void setOnDeviceSelectedListener(OnDeviceSelectedListener d){
		listener = d;
	}
	// Ojdevice selected listener
	public interface OnDeviceSelectedListener{
		void onDeviceSelected(int which);
	}
}
