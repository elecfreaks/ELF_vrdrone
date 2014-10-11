package com.g_zhang.p2pComm;

public class P2PDataAlarmConfig {
	public int Ver;
	public int MoveDetLevel;
	public int VoiceAlmLevel;
	public int PIRAlmLevel;
	public int ENVAlmLevel;
	public int IOPortAlarm;
	public int AlarmPTZCall;
	public int AlarmInterval;
	public int EmailAlarm;
	public int FTPAlarm;
	public int RecordAlarm;
	public int AlarmSCH;
	public int AlarmSupport;
	//	UINT32 alm_sch[7][3];	
	
	public P2PDataAlarmConfig()
	{
		this.Ver = 0;
		this.MoveDetLevel = 0;
		this.VoiceAlmLevel = 0;
		this.PIRAlmLevel = 0;
		this.ENVAlmLevel = 0;
		this.IOPortAlarm = 0;
		this.AlarmPTZCall =  0;
		this.AlarmInterval = 0;
		this.EmailAlarm = 0;
		this.FTPAlarm = 0;
		this.RecordAlarm = 0;
		this.AlarmSCH = 0;
		this.AlarmSupport = 0;
	}
	
}
