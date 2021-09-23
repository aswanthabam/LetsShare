package com.abam.letsshare;
import android.content.*;
import android.support.v4.content.*;
import android.content.pm.*;
import android.util.*;
import android.support.v7.app.*;
import android.*;
import java.util.*;
import android.os.*;
import java.io.*;

public class FileManager
{
	public FileManager parentDirectory = null;
	public boolean isFile = false;
	private AppCompatActivity activity;
	private Context context;
	public String TAG = "FILE_MANAGER";
	private List<String> fileNameList = new ArrayList<String>();
	private List<File> fileList = new ArrayList<File>();
	private boolean showHiddenFiles;
	private FileSearchListener listener = null;
	private String CurrentPath = null;
	private File[] files;
	// File Manager second constructor
	public FileManager(AppCompatActivity c){
		activity = c;
		context = c.getApplicationContext();
		askPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
		askPermission(Manifest.permission.READ_EXTERNAL_STORAGE);
	}
	// Initialize witjout path defined path =""
	public void initialize(){
		try{
			listener.onInit();
		}catch(NullPointerException e){
			Log.e(TAG,"No FileSearchListener Attached");
		}catch(Exception e){
			Log.e(TAG,"Cant set init listener. Caused by("+e.toString()+")");
		}
		String path = Environment.getExternalStorageDirectory().toString() +"/";
		CurrentPath = path;
		File  f = new File(path);
		files = f.listFiles();
		orderFileList("name");
		try{
			listener.onFinish();
		}catch(NullPointerException e){
			Log.e(TAG,"No listener attached.");
		}catch(Exception e){
			Log.e(TAG,"Cant set onFinish. Caused by("+e.toString()+")");
		}
	}
	// Initialize with path
	public void initialize(String pat,FileManager parent){
		try{
			listener.onInit();
		}catch(NullPointerException e){
			Log.e(TAG,"No FileSearchListener Attached");
		}catch(Exception e){
			Log.e(TAG,"Cant set init listener. Caused by("+e.toString()+")");
		}
		String path = pat + "/";
		CurrentPath = path;
		File  f = new File(path);
		files = f.listFiles();
		orderFileList("name");
		parentDirectory = parent;
		try{
			listener.onFinish();
		}catch(NullPointerException e){
			Log.e(TAG,"No listener attached.");
		}catch(Exception e){
			Log.e(TAG,"Cant set onFinish. Caused by("+e.toString()+")");
		}
	}
	// Get file ordered
	public void orderFileList(String orderBy){
		switch(orderBy){
			case "name":
				if(files != null && files.length > 1) {
					Arrays.sort(files, new Comparator<File>() {
						@Override
						public int compare(File object1, File object2) {
							return object1.getName().toLowerCase(Locale.getDefault()).compareTo(object2.getName().toLowerCase(Locale.getDefault()));
						}
					});
				}
				break;
			case "date":
				if(files != null && files.length > 1) {
					Arrays.sort(files, new Comparator<File>() {
						@Override
						public int compare(File object1, File object2) {
							return (int) (object1.lastModified() > object2.lastModified() ? object1.lastModified() : object2.lastModified());
						}
					});
				}
				break;
		}
		// Clear previous data
		fileNameList.clear();
		fileList.clear();
		for(File i : files){
			if(i.getName().startsWith(".") && !showHiddenFiles) continue;
			fileNameList.add(i.getName());
			fileList.add(i);
		}
	}
	// Restore files
	public void restoreFiles(){
		fileNameList.clear();
		fileList.clear();
		File  f = new File(getAbsolutePath());
		files = f.listFiles();
		orderFileList("name");
	}
	// Get directory currently working
	public String getAbsolutePath(){
		return CurrentPath;
	}
	// get Files
	public List<File> getFiles(){
		return fileList;
	}
	// get file list names
	public List<String> getFileList(){
		return fileNameList;
	}
	// Get parent directory
	public FileManager getParent(){
		return parentDirectory;
	}
	// Ask for permission
	private void askPermission(String permission){
		try{
			if (ContextCompat.checkSelfPermission(context,permission) == PackageManager.PERMISSION_GRANTED) {
			} else {
				activity.requestPermissions(new String[] { permission },6557);
			}
		}catch(Exception e){
			Log.e(TAG,"Error in requesting permission. caused by( "+ e.toString()+ " )");
		}
	}
	// Get child
	public FileManager getChild(File file,boolean useFileSearchListener){
		FileManager k = new FileManager(activity);
		if(useFileSearchListener){
			k.setFileSearchListener(listener);
		}
		// Set the show hidden file to child
		k.setShowHiddenFiles(this.showHiddenFiles);
		k.initialize(file.getAbsolutePath(),this);
		return k;
	}
	// Set show hidden files enabled
	public void setShowHiddenFiles(boolean enable){
		showHiddenFiles = !enable;
	}
	// Set FileSerchListener
	public void setFileSearchListener(FileSearchListener l){
		listener = l;
	}
	// Interface for starting and finishing of file search
	public interface FileSearchListener{
		void onInit();	//On start
		void onFinish();	// On finish
	}
}
