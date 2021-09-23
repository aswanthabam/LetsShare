/*
MADE BY ASWANTH VC
******************
Copy Righyed content dont edit or republish.
© ABAM
*/
package com.abam.letsshare;

import android.support.v7.app.*;
import android.os.*;
import android.support.v7.widget.*;
import android.widget.*;
import android.support.v7.widget.Toolbar;
import android.support.v4.app.*;
import android.support.v4.widget.*;
import android.support.design.widget.*;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.*;
import android.view.View.*;
import android.app.Dialog;
import android.support.v4.content.*;
import android.*;
import android.content.pm.*;
import android.content.*;
import android.net.wifi.p2p.*;

public class ActivityMain extends AppCompatActivity
{
	private DrawerLayout mDrawer;
	private NavigationView mNavView;
	private Toolbar mToolbar;
	private ActionBarDrawerToggle mToogle;
	private ActionListener listener;
	private BottomNavigationView bottomNav;
	private Button connectButton;
	private TextView connectTextBottom;
	private FileFragmet frag2;
	// OnCreate
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_activity);
		initDrawer();
		initBottomNavigation();
		setConnectButton();
		getPermission();
	}
	// Asks for needed permissions
	public boolean getPermission(){
		DialogMessage m = new DialogMessage(this,R.layout.ask_permission);
		m.setAction(new DialogMessage.SetAction(){
			@Override public void setFunction(final DialogMessage m){
				TextView head = m.findViewById(R.id.dialogMessageHeading);
				TextView content = m.findViewById(R.id.dialogMessageContent);
				Button but1 = m.findViewById(R.id.dialogMessageButton1);
				Button but2 = m.findViewById(R.id.dialogMessageButton2);
				head.setText("Allow Permissions");
				content.setText(R.string.need_permission);
				but1.setOnClickListener(new OnClickListener(){
					@Override public void onClick(View v){
						m.dismiss();
						finish();
					}
				});
				but2.setOnClickListener(new OnClickListener(){
					@Override public void onClick(View v){
						m.dismiss();
						requestPermissions(new String[] { Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.ACCESS_FINE_LOCATION },9836);
						getPermission();
					}
				});
			}
			@Override public void setDismiss(){
				
			}
		});
		if(ContextCompat.checkSelfPermission(getApplicationContext(),Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(getApplicationContext(),Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
			m.show();
		else setFileFrame();
		return false;
	}
	/* Check whether the connection is formed
	public boolean checkWifiStatus(){
		WifiP2pManager mm = (WifiP2pManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
		WifiP2pManager.Channel ch = mm.initialize(getApplicationContext(),getMainLooper(),null);
		mm.requestConnectionInfo(ch, new WifiP2pManager.ConnectionInfoListener(){
				@Override public void onConnectionInfoAvailable(WifiP2pInfo in){
					if(in.groupFormed && in != null) wifiStatus = true;
					else wifiStatus = false;
				}
			});
		return wifiStatus;
	}*/
	// Initialize bottom connect dialog
	public void openBottomDialog(){
		BottomDialog mDialog = new BottomDialog(this,R.layout.main);
		mDialog.setListener(new BottomDialog.SetActions(){
			@Override
			public void setDeviceList(MainActivity ku){
				ku.setFiles(frag2.SelectedFiles);
			}
		});
		mDialog.setOnDismissListener(new DialogInterface.OnDismissListener(){
			@Override
			public void onDismiss(DialogInterface m){
				/*if(checkWifiStatus()){
					connectButton.setBackgroundResource(R.drawable.connected_button);
					connectTextBottom.setText("Connected");
				}*/
			}
		});
		mDialog.show();
	}
	// Initialize connect button
	private void setConnectButton(){
		connectTextBottom = (TextView) findViewById(R.id.main_activityConnectedText);
		connectTextBottom.setText("Connect");
		connectButton = (Button) findViewById(R.id.main_activityConnectButton);
		connectButton.setOnClickListener(new OnClickListener(){
			@Override public void onClick(View v){
				openBottomDialog();
			}
		});
	}
	// Set file frame Layout
	private void setFileFrame(){
		frag2 = new FileFragmet(this);
		frag2.mainAc = this;
		Fragment frag = frag2;
		FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
		transaction.replace(R.id.mainFrame,frag,"FileFragment");
		transaction.commit();
	}
	// initialize bottom navigatio  view
	public void initBottomNavigation(){
		bottomNav = (BottomNavigationView) findViewById(R.id.bottom_navigation);
		bottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener(){
			@Override public boolean onNavigationItemSelected(MenuItem item){
				switch(item.getItemId()){
					case R.id.bottomHome:
						openDrawerAction();
						setFileFrame();
						break;
				}
				return true;
			}
		});
	}
	// Open drawer by handler
	public void openDrawerAction(){
		new Handler().postDelayed(new Runnable(){
			@Override public void run(){
				mDrawer.openDrawer(Gravity.START);
			}
		},20);
	}
	// Close drawer by handler
	public void closeDrawerAction(){
		new Handler().postDelayed(new Runnable(){
				@Override public void run(){
					mDrawer.closeDrawers();
				}
			},20);
	}
	// Init drawer
	public void initDrawer(){
		mDrawer = (DrawerLayout)findViewById(R.id.mainActivitydrawer_layout);
		
		mNavView = (NavigationView)findViewById(R.id.mainActivity_drawer);
		mToolbar = (Toolbar)findViewById(R.id.mainActivitytoolbar);
		setSupportActionBar(mToolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setDisplayShowTitleEnabled(true);
		mToogle = new ActionBarDrawerToggle(this,mDrawer,mToolbar,R.string.drawer_open,R.string.drawer_close);
		mDrawer.setDrawerListener(mToogle);
		// Drawer listener working with
		mDrawer.setDrawerListener(new DrawerLayout.DrawerListener(){
			@Override public void onDrawerStateChanged(int p1){
				// Drawer State changed
			}
			@Override public void onDrawerClosed(View v){
				// Drawer opened
				closeDrawerAction();
			}
			@Override public void onDrawerSlide(View v,float p1){
				// Drawer slide
			}
			@Override public void onDrawerOpened(View v){
				// Drawer Opened
				openDrawerAction();
			}
		});
		mToogle.syncState();
		mNavView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener(){
				@Override public boolean onNavigationItemSelected(MenuItem item){
					item.setCheckable(true);
					switch(item.getItemId()){

					}
					closeDrawerAction();
					return true;
				}
			});
	}
	// Set action lister
	public void setActionListener(ActionListener l){
		listener = l;
	}
	// Interface for onBackPresa etc. to use in fragments
	public interface ActionListener{
		boolean onBackPressed();
	}
	// On back pressed
	@Override
	public void onBackPressed()
	{
		if(!listener.onBackPressed()){
			super.onBackPressed();
		}
	}
}
