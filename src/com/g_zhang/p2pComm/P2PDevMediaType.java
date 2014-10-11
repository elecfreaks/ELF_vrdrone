package com.g_zhang.p2pComm;

public class P2PDevMediaType {
	public final static int P2PDEV_TYPE_HD = 0x10;
	public final static int P2PDEV_TYPE_FHD = 0x20;
	
	public int DevType;
	public int DevSubType;
	public int DevSupport;
	public int TotalVideoChanel;
	public int TotalAudioChanel;
	public int CurVideoChanel;
	public int CurAudioChanel;
	
	public int vdFrame_Width;
	public int vdFrame_Height;
	public int vdFrame_Fps;
	public int vdFrame_Brightness;
	public int vdFrame_Contrast;
	public int vdFrame_LightMode;
	public int vdFrame_Flip;
	public int vdFrame_CodecMode;
	public int vdFrame_CodecValue;
	public int vdFrame_EncodeModeIdx;
	public int vdFrame_PTZSpeed;
	public int vdFrame_Format;
	
	public int adFrame_Chanel;
	public int adFrame_Rate;
	public int adFrame_SampleSize;
	public int adFrame_Format;

	public int LiveType;
	
	public P2PDevMediaType()
	{
		this.DevType = 0;
		this.DevSubType =0;
		this.DevSupport = 0;
		this.TotalVideoChanel = 0;
		this.TotalAudioChanel = 0;
		this.CurVideoChanel = 0;
		this.CurAudioChanel = 0;
		
		this.vdFrame_Width = 0;
		this.vdFrame_Height = 0;
		this.vdFrame_Fps = 0;
		this.vdFrame_Brightness = 0;
		this.vdFrame_Contrast = 0;
		this.vdFrame_LightMode = 0;
		this.vdFrame_Flip = 0;
		this.vdFrame_CodecMode = 0;
		this.vdFrame_CodecValue = 0;
		this.vdFrame_EncodeModeIdx = 0;
		this.vdFrame_PTZSpeed = 0;
		this.vdFrame_Format = 0;
		
		this.adFrame_Chanel = 0;
		this.adFrame_Rate = 0;
		this.adFrame_SampleSize = 0;
		this.adFrame_Format = 0;
		
		this.LiveType = 0;
	}
	
	public boolean ISHDDevice()
	{
		return (this.DevType & P2PDEV_TYPE_HD) > 0;
	}
}
