/*
MADE BY ASWANTH V C
********************
Copy Righted content dont edit or republish.
© ABAM
*/
package com.abam.letsshare;

import android.app.*;
import android.os.*;
import java.io.*;
import android.util.*;
import android.support.v7.app.*;
import android.net.wifi.p2p.*;
import android.net.wifi.p2p.WifiP2pManager.*;
import android.content.*;
import android.widget.*;
import android.support.v4.widget.*;
import android.support.design.widget.*;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.*;
import java.util.*;
import android.graphics.drawable.*;
import android.support.v4.content.*;
import android.content.pm.*;
import android.*;
import android.net.*;
import android.view.View.*;
import android.net.wifi.*;
import android.location.*;

public class MainActivity implements ConnectionInfoListener
{
	private String TAG = "MAIN_ACTIVITY";
	private WifiP2pManager manager;
	private Channel channel;
	private Button mainTurnOn;
	private Button ButtonMakeConnection;
	private Button ButtonConnect;
	private Button ButtonDisconnect;
	private ImageView mainImage;
	private TextView mainText;
	private ArrayList<WifiP2pDevice> mDeviceList = new ArrayList<WifiP2pDevice>();
	//private ArrayList<String> deviceNames = new ArrayList<String>();
	private ArrayAdapter<String> mAdapter;
	private WifiP2pDevice currentDevice = null;
	private boolean WifiDirectConnected = false;
	private AnimationDrawable wifiAnimation;
	private MainReciver mReciver;
	private IntentFilter mIntentFilter;
	private WifiP2pInfo info;
	private WifiManager wifiManager;
	public AppCompatActivity act;
	public BottomDialog vv;
	private int flag = 0;
	private boolean LocationOn = false;
	private Button connectRecive;
	private TextView bottomFilesSelected;
	private FileServerAsyncTask task = null;
	private List<File> filesSelected = null;
	private FileTransferService fileShareService = null;
	private boolean reciver = true;
	public MainActivity(AppCompatActivity a,BottomDialog v){
		act = a;
		vv = v;
	}
    public boolean onCreate()
    {
		// Asks for permissions
		askPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);	// Storage
		askPermission(Manifest.permission.ACCESS_FINE_LOCATION);	// Location
		askPermission(Manifest.permission.ACCESS_WIFI_STATE);		// Access WifiState
		askPermission(Manifest.permission.CHANGE_WIFI_STATE);		// Chanege Wifi state
		if(Build.VERSION.SDK_INT >= 24){
			showLocationMessages();
		}
		// Wifi p2p manager
		manager = (WifiP2pManager) act.getSystemService(Context.WIFI_P2P_SERVICE);
		channel = (WifiP2pManager.Channel) manager.initialize(act.getApplicationContext(),act.getMainLooper(),null);
		mReciver = new MainReciver(manager,channel,act);
		// Buttons
		mainTurnOn = (Button)vv.findViewById(R.id.mainButtonTurnOn);
		ButtonMakeConnection = (Button)vv.findViewById(R.id.mainButtonMakeConnection);
		ButtonConnect = (Button)vv.findViewById(R.id.mainButtonConnect);
		ButtonDisconnect = (Button)vv.findViewById(R.id.mainButtonDisconnect);
		connectRecive = (Button) vv.findViewById(R.id.mainButtonRecive);
		// Image and text
		mainImage = (ImageView)vv.findViewById(R.id.mainImage);
		mainText = (TextView)vv.findViewById(R.id.mainText);
		bottomFilesSelected = (TextView) vv.findViewById(R.id.mainBottomFiles);
		bottomFilesSelected.setText("Total "+filesSelected.size()+" files selected");
		// Wifi animation
		mainImage.setBackgroundResource(R.drawable.wifi_success_animation);
		wifiAnimation = (AnimationDrawable) mainImage.getBackground();
		wifiAnimation.start();
		// Wifi Manager
		wifiManager = (WifiManager) act.getSystemService(act.WIFI_SERVICE);
		// Seting wifi reciver actions of all intents
		setWifiListener();
		// Intent Filters
		mIntentFilter = new IntentFilter();
		mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
		mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
		mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
		mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
		// Register reciver
		act.registerReceiver(mReciver,mIntentFilter);
		// setclicklistener
		setClickListeners();
		return true;
    }
	public void setFiles(List<File> fi){
		filesSelected = fi;
	}
	// Show location permission messages
	public void showLocationMessages(){
		if(askPermission(Manifest.permission.ACCESS_COARSE_LOCATION) && askPermission(Manifest.permission.ACCESS_COARSE_LOCATION)){
		LocationManager mm = (LocationManager) act.getSystemService(Context.LOCATION_SERVICE);
		if(!mm.isProviderEnabled(LocationManager.GPS_PROVIDER)){
		DialogMessage mess = new DialogMessage(act,R.layout.ask_permission);
		mess.setAction(new DialogMessage.SetAction(){
			@Override public void setFunction(final DialogMessage k){
				TextView v1 = k.findViewById(R.id.dialogMessageHeading);
				TextView v2 = k.findViewById(R.id.dialogMessageContent);
				Button but1 = k.findViewById(R.id.dialogMessageButton1);
				Button but2 = k.findViewById(R.id.dialogMessageButton2);
				v1.setText("Please turn on location");
				v2.setText("Lets Share need loaction turned on. for working");
				but1.setOnClickListener(new OnClickListener(){
					@Override public void onClick(View v){
						k.dismiss();
					}
				});
				but2.setOnClickListener(new OnClickListener(){
					@Override public void onClick(View v){
						askPermission(Manifest.permission.ACCESS_COARSE_LOCATION);
						act.startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
						LocationOn = true;
						k.dismiss();
					}
				});
			}
			@Override public void setDismiss(){
				if(!LocationOn){
					act.finish();
				}
			}
		});
		mess.show();
		}
		}else askPermission(Manifest.permission.ACCESS_COARSE_LOCATION);
	}
	// Set button click listeners
	public void setClickListeners(){
		// Turn on wifi button
		mainTurnOn.setOnClickListener(new OnClickListener(){
			@Override public void onClick(View v){
				wifiManager.setWifiEnabled(true);
			}
		});
		// Make connection button
		ButtonMakeConnection.setOnClickListener(new OnClickListener(){
			@Override public void onClick(View v){
				// Discover peers
				// This will be captured by the wifi_on_peers_changed
				DiscoverPeers();
				// Show scanning animation
				wifiScan();
			}
		});
		// Share button
		ButtonConnect.setOnClickListener(new OnClickListener(){
			@Override public void onClick(View v){
				if(filesSelected == null || filesSelected.size() < 1){
					toast("No files selected");
					vv.dismiss();
				}else{
					toast("File sharing");
					// start share service
					Runnable r = new Runnable(){
						@Override
						public void run(){
							if(reciver){
								fileShareService.shareSocket(filesSelected);
							}else{
								task.shareStream(filesSelected);
							}
						}
					};
					new Thread(r).start();
				}
			}
		});
		// Disconnect button
		ButtonDisconnect.setOnClickListener(new OnClickListener(){
			@Override public void onClick(View v){
				disconnect();
			}
		});
		// Recive button
		connectRecive.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v){
				// Start file recive service
				Runnable r = new Runnable(){
					@Override
					public void run(){
						if(reciver){
							fileShareService.reciveSocket();
						}else{
							task.reciveStream();
						}
					}
				};
				new Thread(r).start();
			}
		});
	}
	// Asks for permission
	public boolean askPermission(String permission){
		try{
			if (ContextCompat.checkSelfPermission(act.getApplicationContext(),permission) == PackageManager.PERMISSION_GRANTED) {
				return true;
			} else {
				act.requestPermissions(new String[] { permission },6557);
				return false;
			}
		}catch(Exception e){
			Log.e(TAG,"Error in requesting permission. caused by( "+ e.toString()+ " )");
			return false;
		}
	}
	// Set wifi manager action listeners
	public void setWifiListener(){
		if(manager != null && mReciver != null){
			mReciver.setActionListener(new MainReciver.ActionListener(){
				@Override public void onWifiEnabled(){
					// Show wifi success
					wifiSccess();
				}
				@Override public void onWifiDisabled(){
					// Show wifi disabled
					wifiDisabled();
				}
				@Override public void onPeersChanged(Intent p2){
					// Check for new peers
					checkPeer();
				}
				@Override public void onDeviceChanged(){
					
				}
				@Override public void onConnectionChanged(Intent p2){
					if(manager != null){
						NetworkInfo net = p2.getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);
						if(net.isConnected()){
							manager.requestConnectionInfo(channel,MainActivity.this);
						}
					}
				}
			});
		}
	}
	
	//Show connect button
	public void showConnect(){
		wifiConnected();
		ButtonMakeConnection.setVisibility(View.GONE);
		ButtonConnect.setVisibility(View.VISIBLE);
		ButtonDisconnect.setVisibility(View.VISIBLE);
		connectRecive.setVisibility(View.VISIBLE);
		// ButtonConnect.setText("Share to "+currentDevice.deviceName);
	}
	// Wifi disable animation
	public void wifiDisabled(){
		wifiAnimation.stop();
		mainImage.setBackgroundResource(R.drawable.wifi_failed);
		mainText.setText("Wifi Disabled");
		mainTurnOn.setVisibility(View.VISIBLE);
		ButtonMakeConnection.setVisibility(View.GONE);
		ButtonConnect.setVisibility(View.GONE);
		ButtonDisconnect.setVisibility(View.GONE);
		connectRecive.setVisibility(View.GONE);
	}
	// Wifi success animation
	public void wifiSccess(){
		wifiAnimation.stop();
		mainTurnOn.setVisibility(View.GONE);
		mainImage.setBackgroundResource(R.drawable.wifi_success_animation);
		wifiAnimation = (AnimationDrawable) mainImage.getBackground();
		wifiAnimation.start();
		mainText.setText("Ready to go on");
		ButtonMakeConnection.setVisibility(View.VISIBLE);
		ButtonConnect.setVisibility(View.GONE);
		ButtonDisconnect.setVisibility(View.GONE);
		connectRecive.setVisibility(View.GONE);
	}
	// Wifi connected animation
	public void wifiConnected(){
		wifiAnimation.stop();
		mainTurnOn.setVisibility(View.GONE);
		mainImage.setBackgroundResource(R.drawable.connected_animation);
		wifiAnimation = (AnimationDrawable) mainImage.getBackground();
		wifiAnimation.start();
		mainText.setText("Connected");
		ButtonMakeConnection.setVisibility(View.VISIBLE);
		ButtonConnect.setVisibility(View.GONE);
		ButtonDisconnect.setVisibility(View.GONE);
		connectRecive.setVisibility(View.GONE);
	}
	// wifi scanning animation
	public void wifiScan(){
		wifiAnimation.stop();
		mainTurnOn.setVisibility(View.GONE);
		mainImage.setBackgroundResource(R.drawable.wifi_animation);
		wifiAnimation = (AnimationDrawable) mainImage.getBackground();
		wifiAnimation.start();
		mainText.setText("Scanning..");
		ButtonMakeConnection.setVisibility(View.VISIBLE);
		ButtonConnect.setVisibility(View.GONE);
		ButtonDisconnect.setVisibility(View.GONE);
		connectRecive.setVisibility(View.GONE);
	}
	// Disconnect from peer
	public void disconnect(){
		if(manager != null){
			manager.removeGroup(channel,new ActionListener(){
				@Override public void onSuccess(){
					wifiSccess();
					mainText.setText("Disconnected");
					Log.i(TAG,"Disconnected From Group");
				}
				@Override public void onFailure(int reason){
					mainText.setText("Couldnt disconnect");
					Log.e(TAG,"Cant disconnect");
				}
			});
		}
	}
	// Discover to peers
	public void DiscoverPeers(){
		if(manager != null){
			manager.discoverPeers(channel,new ActionListener(){
				@Override public void onSuccess(){
					Log.i(TAG,"Discovered peers successful");
				}
				@Override public void onFailure(int reason){
					Log.e(TAG+"_ERROR","Cant discover peers. caused by reason coede("+reason+" )");
				}
			});
		}
	}
	// Check For peers
	public void checkPeer(){
		if(manager != null){
			manager.requestPeers(channel,new PeerListListener(){
				@Override public void onPeersAvailable(WifiP2pDeviceList peers){
					if(peers != null){
						//ArrayList<WifiP2pDevice> dd = new ArrayList<WifiP2pDevice>();
						mDeviceList.clear();
						mDeviceList.addAll(peers.getDeviceList());
						ArrayList<String> devices = new ArrayList<String>();
						for(WifiP2pDevice device : mDeviceList){
							devices.add(device.deviceName);
						}
						if(devices.size() > 0){
							mAdapter = new ArrayAdapter<String>(act.getApplicationContext(),android.R.layout.simple_list_item_1,devices);
							// Shows device selection dialog only for once
							if(flag == 0){
								flag = 1;
								showDeviceListDialog();
							}
						}else{
							mainText.setText("No Nearby Devices found. Searching...");
						}
					}
				}
				
			});
		}
	}
	// Show device list fragment
	public void showDeviceListDialog(){
		DeviceListDialog dialog = new DeviceListDialog(act.getApplicationContext(),mAdapter);
		dialog.setOnDeviceSelectedListener(new DeviceListDialog.OnDeviceSelectedListener(){
			@Override public void onDeviceSelected(int which){
				final WifiP2pDevice device = mDeviceList.get(which);
				if(device != null){
					WifiP2pConfig config = new WifiP2pConfig();
					config.deviceAddress = device.deviceAddress;
					toast("Devices available "+device.deviceName);
					// Connect to device
					manager.connect(channel,config,new ActionListener(){
						@Override public void onSuccess(){
							// Sets current device
							currentDevice = device;
							// Sets wifi direct is connected
							WifiDirectConnected = true;
						}
						@Override public void onFailure(int reason){
							// Connection failed
							Log.e(TAG+"_MANAGER_CONNECT","Wifi direct connection failiure. caused by( "+reason+ " )");
						}
					});
				}
				
			}
		});
		// Show fragment
		dialog.show(act.getFragmentManager(),"devices");
	}
	// File saving 
	public static long copyFile(InputStream in,OutputStream out){
		byte buf[] = new byte[1024];
		int len;
		long starttime = System.currentTimeMillis();
		try{
			while((len = in.read(buf)) != -1){
				out.write(buf,0,len);
			}
			out.close();
			in.close();
			long endtime = System.currentTimeMillis() - starttime;
			return endtime;
		}catch(Exception e){
			Log.e("SAVE_FILE_ERROR",e.toString());
		}
		return 0;
	}
	// Toast
	public void toast(final String text){
		try{
			Runnable r = new Runnable(){
				@Override public void run(){
					Toast.makeText(act.getApplicationContext(),text,12).show();
				}
			};
			act.runOnUiThread(r);
		}catch(Exception e){
			Log.e(TAG,"Error in toasting. Caused by( "+e.toString()+" )");
		}
	}
	// On Connection info available
	@Override
	public void onConnectionInfoAvailable(WifiP2pInfo p1)
	{
		info = p1;
		if(info.isGroupOwner && info.groupFormed){
			try{
				Log.i("WIFI_P2_CONNECTION","Connection group owner");
				showConnect();
				Runnable r = new Runnable(){
					@Override
					public void run(){
						task = new FileServerAsyncTask(act.getApplicationContext());
						task.setOnFileSavedListener(new FileServerAsyncTask.OnFileSaved(){
							@Override public void onSaved(long time){
								mainText.setText("File Saved. Time Taken "+time);
							}
							@Override public void onSend(long time){
								mainText.setText("File shared. time taken "+time);
							}
							@Override public void toast(String text){
								toast(text);
							}
						});
						task.execute();
						reciver = false;
					}
				};
				new Thread(r).start();
			}catch(Exception e){
				Log.e("WIFI_P2P_CONNECTION","Couldnt connect caused by( "+e.toString()+" )");
				mainText.setText("Connection Error");
			}
		}else if(info.groupFormed){
			Log.i("WIFI_P2P_CONNECTION","connection group member");
			showConnect();
			Runnable r = new Runnable(){
				@Override
				public void run(){
					fileShareService = new FileTransferService(act,info.groupOwnerAddress.getHostAddress(),8988);
					fileShareService.startSocket();
					fileShareService.setOnFileSaved(new FileTransferService.OnFileSaved(){
						@Override
						public void onSaved(long time){
							mainText.setText("File saved. Time taken "+time);
						}
						@Override public void onSend(long time){
							mainText.setText("File shared. Time taken "+time);
						}
						@Override public void toast(String text){
							toast(text);
						}
					});
					reciver = true;
				}
			};
			new Thread(r).start();
		}
		else{
			Log.v("WIFI_P2P_CONNECTION","Wifi connection is not finished!");
			mainText.setText("Connection Error Please Reconnect");
		}
	}
}
