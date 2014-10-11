package com.g_zhang.p2pComm;

public class P2PDataWifiApItem {
	public String ssid; 				// WiFi ssid
	public int Auth;   				// refer to ENUM_AP_MODE
	public int Enc;				// refer to ENUM_AP_ENCTYPE
	public int Signal;   				// signal intensity 0--100%
	public int status;   				// 0 : invalid ssid or disconnected
								// 1 : connected with default gateway
								// 2 : unmatched password
								// 3 : weak signal and connected
								// 4 : selected:
								//		- password matched and
								//		- disconnected or connected but not default gateway
	
	public P2PDataWifiApItem()
	{
		ssid = "";
		Auth = 0;
		Enc = 0;
		Signal = 0;
		status = 0;
	}
	
	public boolean isConnectAp()
	{
		return this.status == 1;
	}
	
}
