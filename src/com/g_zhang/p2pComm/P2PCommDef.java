package com.g_zhang.p2pComm;

import android.content.Context;

public class P2PCommDef {
	
	public final static short P2P_SEVPORT	 = 10200;
	public final static String P2PSev_Root1 	= "mycamdns.com";
	public final static String P2PSev_Root2 	= "54.200.199.150";
	
	public final static int DEVCLTSTU_UNCFG	= 0;
	public final static int DEVCLTSTU_LOGIN	= 1;
	public final static int DEVCLTSTU_REQUSEV = 2;
	public final static int DEVCLTSTU_CNNT	= 3;
	public final static int DEVCLTSTU_ONLINE = 4;

	public static Context m_Context = null; 
	
	public static String getP2PDevClientStatusMsg(int stu)
	{
		switch(stu)
		{
		case DEVCLTSTU_UNCFG:
		{
			if (m_Context == null) return "";
			
			return "";
		}
		//break;
		case DEVCLTSTU_LOGIN:
		{
			if (m_Context == null) return "";
			
			return "";
		}
		//break;
		case DEVCLTSTU_REQUSEV:
		{
			if (m_Context == null) return "";
			
			return "DEVCLTSTU_REQUSEV";
		}
		//break;
		case DEVCLTSTU_CNNT:
		{
			if (m_Context == null) return "";
			
			return "DEVCLTSTU_CNNT";
		}
		//break;
		case DEVCLTSTU_ONLINE:
		{
			if (m_Context == null) return "";
			
			return "DEVCLTSTU_ONLINE";
		}
		//break;
		default:
		{
			if (m_Context == null) return "";
			
			return "default";
			
		}
		//break;
		}
	}
	
	public final static int DEVCLTERR_OK	= 0;
	public final static int DEVCLTERR_UID_ERR	= 1;
	public final static int DEVCLTERR_SEVPWDERR	= 2;
	public final static int DEVCLTERR_APPSEVERR	= 3;
	public final static int DEVCLTERR_DEVOFFLINE= 4;
	public final static int DEVCLTERR_CLIENTFULL= 5;
	public final static int DEVCLTERR_TALKBUSY= 6;
	public final static int DEVCLTERR_TALKERR = 7;

	public static String getP2PDevClientErrorMsg(int err)
	{
		switch(err)
		{
		case DEVCLTERR_OK:
		{
			return "DEVCLTERR_OK";
		}
		//break;
		case DEVCLTERR_UID_ERR:
		{
			if (m_Context == null) return "";	
			return "DEVCLTERR_UID_ERR";
		}
		//break;
		case DEVCLTERR_SEVPWDERR:
		{
			if (m_Context == null) return "";			
			return "DEVCLTERR_SEVPWDERR";
		}
		//break;
		case DEVCLTERR_APPSEVERR:
		{
			if (m_Context == null) return "";			
			return "DEVCLTERR_APPSEVERR";
		}
		//break;
		case DEVCLTERR_DEVOFFLINE:
		{
			if (m_Context == null) return "";			
			return "DEVCLTERR_DEVOFFLINE";
		}
		//break;
		case DEVCLTERR_CLIENTFULL:
		{
			if (m_Context == null) return "";			
			return "DEVCLTERR_CLIENTFULL";
		}
		//break;
		case DEVCLTERR_TALKBUSY:
		{
			if (m_Context == null) return "";			
			return "DEVCLTERR_TALKBUSY";
		}
		//break;
		case DEVCLTERR_TALKERR:
		{
			if (m_Context == null) return "";			
			return "DEVCLTERR_TALKERR";
		}
		//break;
		default:
		{
			if (m_Context == null) return "Error " + err;		
			return "default" + err;
		}
		//break;
		}
	}

	public final static int DEVCLTLLNK_OFFLINE	= 0;
	public final static int DEVCLTLLNK_RELAY	= 1;
	public final static int DEVCLTLLNK_P2P	= 2;
	public final static int DEVCLTLLNK_LAN	= 3;
	public static String getP2PDevClientLinkModeStr(int lnk)
	{
		switch(lnk)
		{
		case DEVCLTLLNK_OFFLINE:
		{
			return "OffLine";
		}
		//break;
		case DEVCLTLLNK_RELAY:
		{
			return "Relay";
		}
		//break;
		case DEVCLTLLNK_P2P:
		{
			return "P2P";
		}
		//break;
		case DEVCLTLLNK_LAN:
		{
			return "Lan";
		}
		//break;
		default:
		{
			return "Unknow " + lnk;
		}
		//break;
		}
	}

	public final static int P2PDATA_BITMK_VIDEO = 1;
	public final static int P2PDATA_BITMK_AUDIO = 2;
	public final static int P2PDATA_BITMK_TALK = 4;
	
	public final static int IPCP_PPPCTLCMD_VIDEO_RESLU =0x0004;
	public final static int IPCP_PPPCTLCMD_VIDEO_FPS = 0x0005;
	public final static int IPCP_PPPCTLCMD_VIDEO_BIRGHTNESS	= 0x0006;
	public final static int IPCP_PPPCTLCMD_VIDEO_CONSTRAST = 0x0007;
	public final static int IPCP_PPPCTLCMD_VIDEO_FLIP = 0x0008;
	public final static int IPCP_PPPCTLCMD_PTZCMD = 0x0009;
	/*
	public final static int IPCP_PPPCTLCMD_PUSHMSG = 0x000A;
	public final static int IPCP_PPPCTLCMD_REQUIMG = 0x000B;
	public final static int IPCP_PPPCTLCMD_REQUIDR = 0x000C;	
	public final static int IPCP_PPPCTLCMD_ALARM = 0x000D;
	public final static int IPCP_PPPCTLCMD_WIFI = 0x000E;
	public final static int IPCP_PPPCTLCMD_SDCARD = 0x000F;
	public final static int IPCP_PPPCTLCMD_CODECINFO = 0x0010;	
	public final static int IPCP_PPPCTLCMD_LOCKENCMAX = 0x0011;  
	public final static int IPCP_PPPCTLCMD_SETACCPWD = 0x0012;
*/
	
	public final static int IPCP_PTZCODE_STOPV = 0;
	public final static int IPCP_PTZCODE_STOPH = 1;
	public final static int IPCP_PTZCODE_MTV_UP = 2;
	public final static int IPCP_PTZCODE_MTV_DN = 3;
	public final static int IPCP_PTZCODE_MTH_LEFT = 4;
	public final static int IPCP_PTZCODE_MTH_RIGHT= 5;
	public final static int IPCP_PTZCODE_MT_LU = 6;
	public final static int IPCP_PTZCODE_MT_LD = 7;
	public final static int IPCP_PTZCODE_MT_RU = 8;
	public final static int IPCP_PTZCODE_MT_RD = 9;
	public final static int IPCP_PTZCODE_MTV_LOOP = 10;
	public final static int IPCP_PTZCODE_MTH_LOOP = 11;
	public final static int IPCP_PTZCODE_MTALL_LOOP = 12;
	
	public final static int IPCP_IMGPRM_FLIP_V = 0x01;
	public final static int IPCP_IMGPRM_FLIP_H = 0x02;
	
	public final static int IPCP_PUSHMSG_ALM =	0x01;
	
	public final static int IPCP_ALMTYPE_SNAPSHOT = 0;
	public final static int IPCP_ALMTYPE_MOVEDET = 1;
	public final static int IPCP_ALMTYPE_IO = 2;
	public final static int IPCP_ALMTYPE_VOICE = 3;
	public final static int IPCP_ALMTYPE_BUTTON = 4;
	
	public final static int IPCPIMG_TYPE_JPEG = 0;
	
	public final static int IPCP_ENCLEVEL_MAX = 0;
	public final static int IPCP_ENCLEVEL_HIGH = 1;
	public final static int IPCP_ENCLEVEL_NORMAL = 2;
	public final static int IPCP_ENCLEVEL_LOW = 3;
	public final static int IPCP_ENCLEVEL_LOWEST = 4;
	
	// --Player Status 
	public final static int PLY_STU_STOP = 0;
	public final static int PLY_STU_PLAY = 1;
	public final static int PLY_STU_PAUSE = 2;
	//---------------------------------
	
	public final static int DEVCFG_DATATYPEA_ALARM = 0x00;
	public final static int DEVCFG_DATATYPEA_WIFI = 0x01;
	public final static int DEVCFG_DATATYPEA_SDCARDCFG = 0x02;
	public final static int DEVCFG_DATATYPEA_SDCARDFILE =0x03;
	public final static int DEVCFG_DATATYPEA_PLAYMTTYPE =0x04;
	public final static int DEVCFG_DATATYPEA_IRLEDCFG =0x05;

	public final static int  IPCP_ALMSUPP_MOVDET =0x01;
	public final static int IPCP_ALMSUPP_IOP = 0x02;
	public final static int IPCP_ALMSUPP_VOICE = 0x04;
	public final static int IPCP_ALMSUPP_PIR = 0x08;
	public final static int IPCP_ALMSUPP_ENV = 0x10;

	public final static int IPCP_ALMIOPORT_NONE = 0x00;
	public final static int IPCP_ALMIOPORT_NO = 0x01;
	public final static int IPCP_ALMIOPORT_NC= 0x02;
	
	public final static int IPCP_LEDSUPP_LEDCTL = 0x01;
	
}
