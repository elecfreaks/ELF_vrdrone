package com.g_zhang.p2pComm;

public class P2PDataIRLedConfig {
	public int Ver;
	public int IR_DetLow;
	public int IR_DetHigh;
	public int IR_DetCurr;
	public int IR_Disabled;
	public int IRLED_Opened;
	public int IRCUT_Opened;
	public int LED_SuppA;
	public int LED_Resv;
	
	public P2PDataIRLedConfig()
	{
		Ver = 0;
		this.IR_DetLow = 0;
		IR_DetHigh = 0;
		IR_DetCurr = 0;
		IR_Disabled = 0;
		IRLED_Opened = 0;
		IRCUT_Opened = 0;
		LED_SuppA = 0;
		LED_Resv = 0;
	}
	
	public boolean SupportLedCtl()
	{
		return (LED_SuppA & P2PCommDef.IPCP_LEDSUPP_LEDCTL) > 0;
	}
	
}