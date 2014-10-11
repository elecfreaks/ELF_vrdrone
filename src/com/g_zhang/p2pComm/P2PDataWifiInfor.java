package com.g_zhang.p2pComm;


public class P2PDataWifiInfor {
	public final static int IPCP_WIFI_STU_CNNT = 0x01;
	public final static int IPCP_WIFI_STU_ACTIVE =0x02;
	public final static int IPCP_WIFI_STU_APENB =0x04;
	
	public int WifiStatus;
	public int WIFI_IPAddr;
	public int number;	// MAX number: 1024(IOCtrl packet size) / 36(bytes) = 28

	public P2PDataWifiInfor()
	{
		this.WifiStatus = 0;
		this.WIFI_IPAddr = 0;
		this.number = 0;
	}
	
	public String GetWifiIpAddr()
	{
		if ( this.WIFI_IPAddr == 0 )
			return "";
		return "GetWifiIpAddr";
	}
	
	public boolean isWifiConnect()
	{
		return (this.WifiStatus & IPCP_WIFI_STU_CNNT ) > 0;
	}
	public boolean isWifiApEnaled()
	{
		return (this.WifiStatus & IPCP_WIFI_STU_APENB ) > 0;
	}
}
