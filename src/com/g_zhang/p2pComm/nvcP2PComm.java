package com.g_zhang.p2pComm;


import com.elf_vrdrone.view.P2PVideoDisplayer;

import android.util.Log;

public class nvcP2PComm {

	static 
	{
        System.loadLibrary("wels");
		System.loadLibrary("ZGP2PComm");
	}
	public static boolean m_bInitOK = false;
	public static native String getVersion();
	
	//初始化 P2P库文件 
	public static native int InitP2PServer(String sev1, String sev2, String sev3, String sev4, short sevPort, int myip);
	// 销毁通讯库
	public static native void DestoryP2PComm();
	//注意所有设备的操作和识别通过设备句柄实现
	
	//添加新的设备到通讯库
	public static native int AddNewP2PDevice(String uid, String accpwd);
	//修改设备信息
	public static native int UpdateP2PDevice(int handle, String uid, String accpwd);
	//从通讯库删除设备
	public static native int DeleteP2PDevice(int handle);
	//返回设备状态
	public static native int getP2PDeviceStatus(int handle);
	//获取最后一次错误信息
	public static native int getP2PDeviceLastError(int handle);
	//重置最后一次错误信息
	public static native int resetP2PDeviceLastError(int handle);
	//获取连接模式
	public static native int getP2PDeviceLinkMode(int handle);
	//请求服务器中继数据，如果设备的连接模式在Relay下，开启音视频数据流需要请求中继，tmsec是中继时间长度，目前不限制>0
	public static native int reqP2PDeviceRelayData(int handle, int tmsec);
	//请求开启音视频数据流
	public static native int reqP2PDeviceAVMedia(int handle, int avmode);
	//控制对讲数据流 istalk = 0 关闭， istalk-1 开启
	public static native int reqP2PDeviceTalk(int handle, int istalk);
	//关闭设备音视频数据流
	public static native int StopP2PDeviceAVMedia(int handle);
	//获取媒体流参数
	public static native P2PDevMediaType readP2PDeviceMediaParams(int handle, P2PDevMediaType md);
	//设置发送帧率
	public static native int setP2PDevicePlayFps(int handle, int fps);
	//获取设备在线用户数
	public static native int getP2PDeviceOnlineUserCnt(int handle);
	//获取当前开启的码流类型，通过bit 位来区分， 参照 P2PCommDef 文件定义 P2PDATA_BITMK_VIDEO ...
	public static native int getP2PDeviceCurrLiveMode(int handle);
	//发送 PCM 8K 单声道 对讲数据，库文件内部压缩处理...
	public static native int sendP2PDeviceTalkAudioData(int handle, byte[] pFrame, int len);
	//发送 媒体控制指令  参照 P2PCommDef 文件定义 IPCP_PPPCTLCMD_VIDEO_RESLU ... ... 
	public static native int sendP2PDeviceMediaCmd(int handle, int cmd, int vlu);
	//开启录像，提供录制文件名 ... ... 
	public static native int recordP2PDeviceStart(int handle, String fl);
	//停止录像
	public static native int recordP2PDeviceStop(int handle);
	//获取录像状态
	public static native int getRecordStatus(int handle);
	//开启lan搜索设备
	public static native int StartSehP2PDeviceStatus();
	//开启 报警信息通知 almmsg = P2PCommDef 文件定义 IPCP_PUSHMSG_ALM, 为接收， 0 不接收
	public static native int setupP2PDevicePushMsg(int handle, int almmsg);
	//请求一个快照截图
	public static native int requP2PDeviceSnapshotImg(int handle);
	
	public static native int reConnectDevice(int handle);
	public static native int closeDeviceConnection(int handle);
	
	public static native int ASFPlayFile(int playerIndex, String file);
	public static native int ASFisPlaying(int playerIndex);
	public static native int ASFisRuning(int playerIndex);
	public static native int ASFGetPlayFileTimeLong(int playerIndex);
	public static native int ASFGotoTimeV(int playerIndex, int tmv);
	public static native int ASFPausePlay(int playerIndex, int bPlay);
	public static native int ASFStopPlay(int playerIndex);
	
	public static native int StartWIFIConfig(String ssid, String pwd, String uid, int nauth, int nenc, int ntype);
	public static native int GetWIFIConfigStatus();
	public static native int StopWIFIConfig();
	
	public static native int setVideoEncodeInfor(int handle,  int encMode, int encVlu, int EncModeIndex, int reslu, int stu);
	public static native int setVideoCodecLock(int handle,  int frmw, int frmh, int bytepersec);

	public static native int reqDevAlarmConfig(int handle);
	public static native P2PDataAlarmConfig readP2PDevAlarmConfigData(int handle, P2PDataAlarmConfig md);
	public static native int setP2PDevAlarmConfig(int handle, P2PDataAlarmConfig md);
	
	public static native int reqDevWifiInfor(int handle);
	public static native P2PDataWifiInfor readP2PDevWifiInforData(int handle, P2PDataWifiInfor md);
	public static native P2PDataWifiApItem readP2PDevWifiApItem(int handle, int apidx, P2PDataWifiApItem md);
	public static native int startP2PDevWifiScan(int handle);
	public static native int setP2PDevWifiDisconnect(int handle);
	public static native int setP2PDevWifiConnect(int handle, String ssid, String pwd, int nauth, int nenc, int ntype);
	
	public static native int reqP2PDevSDCardRecConfig(int handle);
	public static native P2PDataSDCardRecCfg readP2PDevSDCardRecCfg(int handle, P2PDataSDCardRecCfg md);
	public static native int setP2PDevSDCardRecCfg(int handle, P2PDataSDCardRecCfg md);
	public static native int reqP2PSDCardAllFiles(int handle);
	public static native P2PDataRecFileInfor readP2PDevRecFileData(int handle, P2PDataRecFileInfor md);
	public static native P2PDataRecFileItem readP2PDevRecFileItem(int handle, int flidx, P2PDataRecFileItem md);
	public static native int delDevSDCardFile(int handle, String fl);
	public static native int isDevSDCardPlayerFree(int handle);
	public static native int playDevSDCardRecFile(int handle, String fl, int nodelay);
	public static native P2PDataRecPlayMediaInfor readP2PDevRecPlayMediaInfor(int handle,P2PDataRecPlayMediaInfor md);
	public static native int playDevSDCardRecFileGoto(int handle, int tmv);
	public static native int stopDevSDCardRecFilePlay(int handle);
	
	public static native int setDevAccessPwd(int handle, String newpwd, String oldpwd);
	public static native int doDevReboot(int handle);
	public static native int getDevP2PVersion(int handle);
	
	public static native int reqDevIRLedConfig(int handle);
	public static native P2PDataIRLedConfig readP2PDevIRLedConfigData(int handle, P2PDataIRLedConfig md);
	public static native int setP2PDevIRLedConfig(int handle, P2PDataIRLedConfig md);
	public static native int setDevIRLedOn(int handle, int ison);
	
    public static void Callback_OnP2PDeviceStatusChanged(int handle, int status)
    {
    	//Log.d("nvcP2PComm", String.format("Callback_OnP2PDeviceStatusChanged %d - %d", handle, status));
    	//通知设备状态 参照 P2PCommDef DEVCLTSTU_LOGIN 等等定义
    	if (P2PVideoDisplayer.getInstance() != null )
    		P2PVideoDisplayer.getInstance().UpdateP2PDevLinkStatus(handle, status);
    }
    
    public static void Callback_OnP2PDevMediaParamsChanged(int handle)
    {
    	//Log.d("nvcP2PComm", String.format("Callback_OnP2PDevMediaParamsChanged %d", handle));
    	//通知设备媒体参数变化 readP2PDeviceMediaParams 读取新的数据，格式参照文件 P2PDevMediaType 定义
    	if (P2PVideoDisplayer.getInstance() != null )
    		P2PVideoDisplayer.getInstance().OnRecvMediaInfor(handle);
    }
    
    public static void Callback_OnP2PDevRecvVideoData(int handle, int frmid, int tmv, int wd, int hi, int pkLen, byte[] pFrame)
    {
    	//Log.d("nvcP2PComm", String.format("Callback_VideoFrame Dev:%d Frmid:%d (%dx%d), Tmv %d, Len: %d", handle, frmid, wd, hi, tmv, pFrame.length));
    	//接收到视频帧数据 tmv 时间戳， wd , hi 分辨率， pFrame 数据。
    	if (P2PVideoDisplayer.getInstance() != null )
    		P2PVideoDisplayer.getInstance().OnRecvVideoData(handle, frmid, tmv, wd, hi, pFrame);	
    }
    
    public static void Callback_OnP2PDevRecvVideoData(int a, int b, int c, int d, int e, byte[] f){
    	
    }
    
    public static void Callback_OnP2PDevRecvAudioData(int handle, int frmid, int tmv, byte[] pFrame)
    {
    	//接收到音频帧数据 tmv 时间戳，pFrame 已解码的PCM音频 数据。 
    	//可使用 AudioTrack 播放PCM 数据.
    	//Log.d("nvcP2PComm", String.format("Callback_AudioFrame Dev:%d Frmid:%d, Tmv %d, Len: %d", handle, frmid, tmv, pFrame.length));
    	if (P2PVideoDisplayer.getInstance() != null )
    		P2PVideoDisplayer.getInstance().OnRecvAudioData(handle, frmid, tmv, pFrame);	
    }

    public static void Callback_OnP2PDevRecvOtherData(int handle, int frmid, byte[] pFrame)
    {
    	//保留
    	//Log.d("nvcP2PComm", String.format("Callback_OtherData Dev:%d Frmid:%d, Len: %d", handle, frmid, pFrame.length));
    }
    
    public static void Callback_OnP2PDevRecordStatusChanged(int handle, String flnm, int isRec)
    {
    	//录像状态变化； Handle 设备句柄， flnm 文件名， isRec 是否在录像， 
    	if (P2PVideoDisplayer.getInstance() != null )
    		P2PVideoDisplayer.getInstance().OnRecordStatusChanged(handle, flnm, isRec);
    }
    
    public static void Callback_OnRecvP2PDevSehResult(String uid, int DevType, int Wanip, int Wanport, int Lanip, int Lanport, int LanAppPort, int isLanSeh)
    {
    	//返回搜索结果，注意 IP地址 和 Port端口都是网络Bit顺序， 
    	if (P2PVideoDisplayer.getInstance() != null )
    		P2PVideoDisplayer.getInstance().OnRecvNewSehItem(uid, DevType, Wanip, Wanport, Lanip, Lanport, LanAppPort, isLanSeh);   	
    	//Log.d("nvcP2PComm", String.format("Callback_OnRecvP2PDevSehResult %s, DevType:%d, %x : %d, %x : %d AppPort %d, ISLan %d \n", uid, DevType, Wanip, Wanport, Lanip, Lanport, LanAppPort, isLanSeh));
    }

    public static void Callback_OnRecvP2PDevImageFrame(int handle, int almtmv, int almtype, int imgtype, int imgcnt, int imgindex, byte[] pImage)
    {
    	Log.d("nvcP2PComm", String.format("Callback_OnRecvP2PDevImageFrame AlmType:%d, Time %d, ImgCnt %d, ImgIndex %d \n", almtype, almtmv, imgcnt, imgindex));
    	if ( almtype == P2PCommDef.IPCP_ALMTYPE_SNAPSHOT ){
    		//截图快照数据
    		if (P2PVideoDisplayer.getInstance() != null )
    			P2PVideoDisplayer.getInstance().onRecvImageFrame(pImage);
    	}
    	else{
    		//报警截图， 每次3张，依次回传。
    	}
    }
    
    public static void CallBack_OnRecvP2PPlayerStatueChanged(int idx, int stu)
    {
    	Log.d("nvcP2PComm", String.format("CallBack_OnRecvP2PPlayerStatueChanged :%d, Status: %d \n", idx, stu));
    }
    
    public static void CallBack_OnRecvP2PPlayerFrameData(int idx, int nStream, byte[] pFrame, int len, int tmv)
    {
    	Log.d("nvcP2PComm", String.format("CallBack_OnRecvP2PPlayerFrameData :%d, Stream: %d, Len %d, Tmv %d \n", idx, nStream, len, tmv));
    }
    
    public static void CallBack_OnRecvP2PDevConfigData(int handle, int dtype)
    {
    	Log.d("nvcP2PComm", String.format("CallBack_OnRecvP2PDevConfigData :%d, Type: %d\n", handle, dtype));
    }
    
    public static void CallBack_OnRecvP2PSDCardPlayFrame(int handle, int plyChn, int nStream, byte[] pFrame, int len, int tmv)
    {
    	Log.d("nvcP2PComm", String.format("CallBack_OnRecvP2PSDCardPlayFrame :%d, Stream: %d, Len %d, Tmv %d \n", plyChn, nStream, len, tmv));
    }   
}
