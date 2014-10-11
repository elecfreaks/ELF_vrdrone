package com.g_zhang.p2pComm;

public class P2PDataRecFileInfor {
	public final static int IPCP_RECFILE_TYPE_ASF_ESN = 0;
	
	public int  FileCnt;
	public int  Tag;
	public int  FileFormat;

	public P2PDataRecFileInfor()
	{
		this.FileCnt = 0;
		this.Tag = 0;
		this.FileFormat = IPCP_RECFILE_TYPE_ASF_ESN;
	}
}
