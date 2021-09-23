package com.abam.letsshare;
import android.app.*;
import android.content.*;
import java.net.*;
import java.io.*;
import android.net.*;
import android.support.v7.app.*;
import android.util.*;
import java.util.*;
import android.widget.*;
import org.apache.commons.codec.*;
import android.os.*;

public class FileTransferService
{
	public int SOCKET_TIMEOUT = 5000;
	public String TAG = "FILE_TRANSFER_SERVICE";
	private String host;
	private Socket socket;
	private AppCompatActivity act;
	private int port;
	private Context context;
	private OnFileSaved listener;
	public FileTransferService(AppCompatActivity a,String h,int p){
		act = a;
		host = h;
		port = p;
	}
	// create socket
	public boolean startSocket()
	{
		context = act.getApplicationContext();
		socket = new Socket();
		try{
			socket.bind(null);
			socket.connect((new InetSocketAddress(host,port)),SOCKET_TIMEOUT);
			return true;
		}catch(Exception e){
			//Toast.makeText(context,e.toString(),12).show();
			Log.e(TAG,"Socket connection failed. Caused by("+e.toString()+")");
			return false;
		}
	}
	public void shareSocket(List<File> files){
		try{
			OutputStream stream = socket.getOutputStream();
			ContentResolver cv = context.getContentResolver();
			InputStream is = null;
			try{
				is = cv.openInputStream(Uri.parse(files.get(0).getAbsolutePath()));
			}catch(FileNotFoundException e){
				Log.e(TAG,"File open input stream error. caused by("+e.toString()+")");
			}
			long time = MainActivity.copyFile(is,stream);
			if(listener != null) listener.onSend(time);
		}catch(Exception e){
			listener.toast("Sharing failed(FileTransfer) "+e.toString());
			Log.e(TAG,"File sharing failed. caused by("+e.toString()+")");
		}
	}
	public void reciveSocket(){
		try{
			InputStream stream = socket.getInputStream();
			File f = new File(Environment.getExternalStorageDirectory() + "/Lets Share/" + System.currentTimeMillis() + ".jpg");
			long time = MainActivity.copyFile(stream,new FileOutputStream(f));
			if(listener != null) listener.onSaved(time);
		}catch(Exception e){
			listener.toast("Recive failed "+e.toString());
			Log.e(TAG,"File reciving failed. caused by("+e.toString()+")");
		}
	}
	public void setOnFileSaved(OnFileSaved ff){
		listener = ff;
	}
	// set interface
	public interface OnFileSaved{
		void onSaved(long time);
		void onSend(long time);
		void toast(String text);
	}
}
