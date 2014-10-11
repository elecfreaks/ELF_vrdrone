package com.g_zhang.p2pComm;

public class P2PDataSDCardRecCfg {
	public final static int IPCP_REC_MODE_CLOSE	=0x00;
	public final static int IPCP_REC_MODE_ALARM =0x01;
	public final static int IPCP_REC_MODE_SCH = 0x02;

	public final static int IPCP_REC_CFG_AUDIO = 0x01;
	public final static int IPCP_REC_CFG_LOOP = 0x02;
	
	public final static int IPCP_RECFILE_TYPE_ASF_ESN = 0;

	public final static int  IPCP_RECPLAYER_ISBUSY = 1;
	
	public int nRecFileFormat;
	public int nRecStatus;	// 0 , 1  bRecing
	public int nRecMode;	// 
	public int nRecFrameW;
	public int nRecFrameH;
	public int nRecLong; // Min;
	public int bRecVoice;
	public int bRecLoop;
	public int bRecTag1;
	public int bRecTag2;
	public int nPlayerStatus;
	//UINT32 Rec_sch[7][3];
	public int SDCardSize;	// KByte  -1 No SDCArd, -2 NeedFmt, -3 formating
	public int SDCardFree;	// KByte
	
	public P2PDataSDCardRecCfg()
	{
		this.nRecFileFormat = 0;
		this.nRecStatus = 0;
		this.nRecMode = 0;
		this.nRecFrameW = 0;
		this.nRecFrameH = 0;
		this.nRecLong = 0;
		this.bRecVoice = 0;
		this.bRecLoop = 0;
		this.bRecTag1 = 0;
		this.bRecTag2 = 0;
		this.nPlayerStatus = 0;
		this.SDCardFree = 0;
		this.SDCardSize = 0;
	}
	
}
