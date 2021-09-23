package com.abam.letsshare;
import android.support.v4.app.*;
import android.view.*;
import android.os.*;
import android.support.v7.app.*;
import android.support.v7.widget.*;
import android.widget.*;
import java.io.*;
import android.util.*;
import android.support.v4.view.animation.*;
import android.support.design.widget.BottomNavigationView;
import java.util.*;
import android.graphics.drawable.*;
import android.net.wifi.p2p.*;
import android.content.*;
public class FileFragmet extends Fragment implements FilesRecyclerAdapter.ItemClickListener,FileManager.FileSearchListener
{
	public View v;
	private AppCompatActivity activity;
	private RecyclerView rcView;
	public String TAG = "FILE_FRAGMENT_ACTIVITY";
	public ActivityMain mainAc = null;
	private FileManager manager;
	private ProgressBar progress;
	private Button FileBackButton;
	private TextView FilePathText;
	private Switch ShowHideenButton;
	public List<File> SelectedFiles = new ArrayList<File>();
	public FileFragmet(AppCompatActivity a){
		activity = a;
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		super.onCreateView(inflater, container, savedInstanceState);
		// Initialize view
		v = inflater.inflate(R.layout.files_fragment,container,false);
		// Recycler view progress spinner
		progress = v.findViewById(R.id.files_fragmentLoading);
		progress.setVisibility(View.GONE);
		// Button
		FileBackButton = v.findViewById(R.id.files_fragmentBackButton);
		// path TextView
		FilePathText = v.findViewById(R.id.files_fragmentPath);
		// Recycler View
		rcView= v.findViewById(R.id.filesRecyclerView);
		// Show hidden files switch
		ShowHideenButton = v.findViewById(R.id.files_fragmentSwitch);
		ShowHideenButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
			@Override public void onCheckedChanged(CompoundButton p1,boolean p2){
				rcView.setAdapter(null);
				manager.restoreFiles();
				manager.setShowHiddenFiles(p2);
				setCureentPath(manager.getAbsolutePath());
				manager.setFileSearchListener(FileFragmet.this);
				FilesRecyclerAdapter adapter = new FilesRecyclerAdapter(activity.getApplicationContext(),manager.getFiles());
				adapter.setClickListener(FileFragmet.this);
				setAdapter(rcView,adapter);
			}
		});
		// Create File manager class
		manager = new FileManager(activity);
		manager.setFileSearchListener(this);
		manager.initialize();
		manager.setShowHiddenFiles(ShowHideenButton.isChecked());
		setCureentPath(manager.getAbsolutePath());
		manager.setFileSearchListener(this);
		// Set adapter and layout manager
		FilesRecyclerAdapter adapter = new FilesRecyclerAdapter(activity.getApplicationContext(),manager.getFiles());
		rcView.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.VERTICAL,false));
		adapter.setClickListener(this);
		rcView.setAdapter(adapter);
		// Set on back pressed
		mainAc.setActionListener(new ActivityMain.ActionListener(){
			// On Back pressed
			@Override public boolean onBackPressed(){
				if(manager.getParent() == null){
					Log.i(TAG,"back normally");
					return false;
				}
				else{
					Log.i(TAG,"back to parent directory");
					// Back to parent directory
					manager = manager.getParent();
					manager.setShowHiddenFiles(ShowHideenButton.isChecked());
					setCureentPath(manager.getAbsolutePath());
					manager.setFileSearchListener(FileFragmet.this);
					FilesRecyclerAdapter adapter = new FilesRecyclerAdapter(activity.getApplicationContext(),manager.getFiles());
					adapter.setClickListener(FileFragmet.this);
					setAdapter(rcView,adapter);
					return true;
				}
			}
		});
		return v;
	}
	
	// Function to select files
	public void selectFile(File f,FilesRecyclerAdapter.ViewHolder v){
		if(SelectedFiles.contains(f) || v.FileType.getText().toString().equals("Selected")){
			// Setting back the old state
			v.thumbnail.setBackgroundDrawable(null);
			v.mainLayout.setBackgroundResource(R.color.white);
			v.FileType.setText("");
			SelectedFiles.remove(f);
		}
		else{
			// Setting selection state
			v.mainLayout.setBackgroundResource(R.color.lightBlue);
			v.thumbnail.setBackgroundResource(R.drawable.selected_tic);
			v.FileType.setText("Selected");
			SelectedFiles.add(f);
		}
	}
	// Function to set adapter
	public static void setAdapter(final RecyclerView v,final FilesRecyclerAdapter m){
		new Handler().postDelayed(new Runnable(){
			@Override public void run(){
				v.setAdapter(m);
			}
		},20);
	}
	// On recycler item binding finished
	@Override
	public void onBindFinished(){
		progress.setVisibility(View.GONE);
		rcView.setVisibility(View.VISIBLE);
	}
	// On recycler item click
	@Override
	public void onClick(int position, File file,FilesRecyclerAdapter.ViewHolder v)
	{
		if(file.isDirectory()){
			Log.i(TAG,"Clicked file is a directory opening subFiles. ("+file.getAbsolutePath()+")");
			manager = manager.getChild(file,true);
			manager.setShowHiddenFiles(ShowHideenButton.isChecked());
			setCureentPath(manager.getAbsolutePath());
			manager.setFileSearchListener(this);
			FilesRecyclerAdapter adapter = new FilesRecyclerAdapter(activity.getApplicationContext(),manager.getFiles());
			adapter.setClickListener(this);
			setAdapter(rcView,adapter);
		}else if(file.isFile()){
			Log.i(TAG,"Clicked file is a file ("+file.getAbsolutePath()+")");
			selectFile(file,v);
		}
	}
	// Function to set the currect path
	public void setCureentPath(final String Path){
		FilePathText.setText(Path);
		FileBackButton.setOnClickListener(new View.OnClickListener(){
			@Override public void onClick(View v){
				try{
					manager = manager.getParent();
					manager.setShowHiddenFiles(ShowHideenButton.isChecked());
					setCureentPath(manager.getAbsolutePath());
					manager.setFileSearchListener(FileFragmet.this);
					FilesRecyclerAdapter adapter = new FilesRecyclerAdapter(activity.getApplicationContext(),manager.getFiles());
					adapter.setClickListener(FileFragmet.this);
					setAdapter(rcView,adapter);
				}catch(Exception e){
					Log.i(TAG,"Reached root of external storage directory");
				}
			}
		});
	}
	// Do when file is in selection list
	@Override
	public void selectionHighlight(File file,FilesRecyclerAdapter.ViewHolder v)
	{
		if(SelectedFiles.contains(file)){
			v.mainLayout.setBackgroundResource(R.color.lightBlue);
			v.thumbnail.setBackgroundResource(R.drawable.selected_tic);
			v.FileType.setText("Selected");
		}
	}
	// On init of FileSearchListener
	@Override
	public void onInit()
	{
		/*progress.setVisibility(View.VISIBLE);
		rcView.setVisibility(View.GONE);*/
	}
	// On finish listener of FileSearchListener
	@Override
	public void onFinish()
	{
		
	}

}
