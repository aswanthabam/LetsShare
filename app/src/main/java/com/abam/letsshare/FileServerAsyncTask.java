package com.abam.letsshare;
import android.os.*;
import java.net.*;
import android.util.*;
import java.io.*;
import android.content.*;
import android.widget.*;
import java.util.*;
import android.net.*;

public class FileServerAsyncTask extends AsyncTask<Void,Void,String>
{
	public String TAG = "FILE_SERVER_ASYNC_TASK";
	public OnFileSaved listener;
	public Context context;
	private long timeTaken;
	private ServerSocket serverSocket;
	private Socket socket;
	private File f;
	public FileServerAsyncTask(Context c){
		context = c;
	}
	// Do In Background
	@Override
	protected String doInBackground(Void[] p1)
	{
		try{
			serverSocket = new ServerSocket(8988);
			socket = serverSocket.accept();
			f = new File(Environment.getExternalStorageDirectory() + "/Lets Share/" + System.currentTimeMillis() + ".jpg");
			File dirs = new File(f.getParent());
			if(!dirs.exists()) dirs.mkdirs();
			f.createNewFile();
			return f.getAbsolutePath();
		}catch(Exception e){
			Log.e(TAG,"Error in creating server socket. cause by( "+e.toString()+" )");
			listener.toast("Socket creation failed");
		}
		return null;
	}
	// Get inpitstream and save file
	public void reciveStream(){
		try{
			//Toast.makeText(context,"file saving",12).show();
			InputStream inputStream = socket.getInputStream();
			timeTaken = MainActivity.copyFile(inputStream,new FileOutputStream(f));
			if(listener != null) listener.onSaved(timeTaken);
			listener.toast("Recive complete");
		}catch(Exception e){
			listener.toast("Recive stream "+e.toString()); 
			Log.e(TAG,"Cant save file by input stream. Caused by("+e.toString()+")");
			//Toast.makeText(context,e.toString(),12).show();
		}
	}
	public void shareStream(List<File> files){
		try{
			OutputStream stream = socket.getOutputStream();
			ContentResolver cv = context.getContentResolver();
			InputStream is = null;
			try{
				is = cv.openInputStream(Uri.parse(files.get(0).getAbsolutePath()));
			}catch(FileNotFoundException e){
				listener.toast("File not found");
				Log.e(TAG,"File open input stream error. caused by("+e.toString()+")");
			}
			long time = MainActivity.copyFile(is,stream);
			if(listener != null) listener.onSend(time);
			listener.toast("Share complete "+time);
		}catch(Exception e){
			listener.toast("File share "+e.toString());
			Log.e(TAG,"File sharing failed. caused by("+e.toString()+")");
		}
	}
	// Get the time
	public long getTime(){
		return timeTaken;
	}
	// Set File Saced Listener
	public void setOnFileSavedListener(OnFileSaved s){
		listener = s;
	}
	// Interface 
	public interface OnFileSaved{
		void onSaved(long time);
		void onSend(long time);
		void toast(String text);
	}
}
