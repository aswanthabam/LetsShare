package com.abam.letsshare;
import android.content.*;
import android.net.wifi.p2p.*;
import android.app.*;
import android.util.*;

public class MainReciver extends BroadcastReceiver
{
	private Activity activity;
	private Context context;
	public String TAG = "MAIN_RECIVER";
	private ActionListener actionListener;
	private WifiP2pManager manager;
	private WifiP2pManager.Channel channel;
	// Initializing
	public MainReciver(WifiP2pManager m,WifiP2pManager.Channel ch,Activity a){
		context = a.getApplicationContext();
		activity = a;
		manager = m;
		channel = ch;
	}
	// On receive
	@Override
	public void onReceive(Context p1, Intent p2)
	{
		String action = p2.getAction();
		// Wifi state change action
		if(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)){
			int state = p2.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE,-1);
			if(state == WifiP2pManager.WIFI_P2P_STATE_ENABLED){
				Log.d(TAG,"Wifi state enabled");
				actionListener.onWifiEnabled();
			}else{
				Log.d(TAG,"Wifi state disabled");
				actionListener.onWifiDisabled();
			}
		}else if(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)){
			Log.d(TAG,"Peers changed");
			actionListener.onPeersChanged(p2);
		}else if(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)){
			Log.d(TAG,"Connection changed");
			actionListener.onConnectionChanged(p2);
		}else if(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)){
			Log.d(TAG,"This device changed");
			actionListener.onDeviceChanged();
		}
	}
	// Set action listener
	public void setActionListener(ActionListener ac){
		actionListener = ac;
	}
	public interface ActionListener{
		void onWifiEnabled();	// Wifi enabled
		void onWifiDisabled();	// Wifi disabled
		void onPeersChanged(Intent p2);	//Wifi PeersChanged
		void onConnectionChanged(Intent p2);	//Wifi ConnectionChanged
		void onDeviceChanged();	//On device changed
	}
}
