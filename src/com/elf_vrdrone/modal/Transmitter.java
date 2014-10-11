package com.elf_vrdrone.modal;

import java.util.Timer;
import java.util.TimerTask;

import com.elf_vrdrone.ble.BluetoothHandler;

import android.R.integer;
import android.os.Handler;
import android.text.StaticLayout;
import android.util.Log;


public class Transmitter {
	private static final int  CHANNEL_COUNT = 8;
	private static final int  FPS = 16;
	
	private static BluetoothHandler mBluetoothHandler = null;
	private static Transmitter transmitter = null;
	private static Object lock;
	private Timer timer;
	private byte[] dataPackage;
	private float[] channelList;
	
	public BluetoothHandler getBluetoothHandler() {
		return mBluetoothHandler;
	}

	public void setBleConnectionManager(BluetoothHandler bluetoothHandler) {
		this.mBluetoothHandler = bluetoothHandler;
	}

	public float getChannelListValue(int n){
		return channelList[n];
	}
	
	public Transmitter(){
		
	}
	
	private Transmitter(BluetoothHandler bluetoothHandler){
		this.mBluetoothHandler = bluetoothHandler;
	}
	
	Handler handler = new Handler();
	
	public void start(){
		//stop();
		
		initDataPackage();
		
		timer = new Timer();
		timer.schedule(new TimerTask() {
			
			@Override
			public void run() {
				transmmit();
			}
		}, 0, 1000 / FPS);
	}
	
	public void alloc(){
		dataPackage = new byte[11];
		channelList = new float[CHANNEL_COUNT];
		lock = new Object();
	}
	
	public void stop(){
		if (timer != null) {
			timer.cancel();
			timer = null;
		}
	}
	public static Transmitter getSharedTransmitter(){
		if(transmitter == null){
			transmitter = new Transmitter();
			transmitter.alloc();
		}
		return transmitter;
	}
	
	public static void setBluetoothHandler(BluetoothHandler bluetoothHandler){
		mBluetoothHandler = bluetoothHandler;
	}
	
	public static void transmmitData(byte[] data){
		if (mBluetoothHandler != null && data != null){
			//for(byte b:data){
			//	System.out.printf("%02X ", b);
			//}
			System.out.println("");
			mBluetoothHandler.sendData(data);
		}
	}
	
	public boolean transmmitSimpleCommand(OSDCommon.MSPCommnand commnand){
		transmmitData(OSDCommon.getSimpleCommand(commnand));
		return true;
	}
	
	public void transmmit(){
		updateDataPackage();
	    if (mBluetoothHandler != null && dataPackage != null) {
			mBluetoothHandler.sendData(dataPackage);
		}
	}
	
	private void initDataPackage(){
		
		dataPackage[0] = '$';
		dataPackage[1] = 'M';
		dataPackage[2] = '<';
		dataPackage[3] = 4;
		dataPackage[4] = (byte)(OSDCommon.MSPCommnand.MSP_SET_RAW_RC_TINY.value());
		
		updateDataPackage();
	}
	
	public void setChannel(int channeIdx, float value){
		channelList[channeIdx] = value;
	}
	
	public float getChannel(int channeIdx){
		return channelList[channeIdx];
	}
	
	int check = 0;

	//传输八个通道的数据，通道数据用5个字节来表示
    private void updateDataPackage(){
		byte checkSum = 0;
	    
	    int dataSizeIdx = 3;
	    int checkSumIdx = 10;
	    
	    dataPackage[dataSizeIdx] = 5;
	    
	    checkSum ^= (dataPackage[dataSizeIdx] & 0xFF);
	    checkSum ^= (dataPackage[dataSizeIdx + 1] & 0xFF);
	    
	    for(int channelIdx = 0; channelIdx < CHANNEL_COUNT - 4; channelIdx++){
	        dataPackage[5 + channelIdx] = (byte) channelList[channelIdx];
	        checkSum ^= (dataPackage[5 + channelIdx] & 0xFF);
	    }
	    
	    byte auxChannels = 0x00;
	    
	    float aux1Scale = channelList[4];
	    
	    if (aux1Scale < -0.666) {
	        auxChannels |= 0x00;
	    }
	    else if(aux1Scale < 0.3333){
	        auxChannels |= 0x40;
	    }
	    else{
	        auxChannels |= 0x80;
	    }
	    
	    float aux2Scale = channelList[5];
	    
	    if (aux2Scale < -0.666) {
	        auxChannels |= 0x00;
	    }
	    else if(aux2Scale < 0.3333){
	        auxChannels |= 0x10;
	    }
	    else{
	        auxChannels |= 0x20;
	    }
	    
	    float aux3Scale = channelList[6];
	    
	    if (aux3Scale < -0.666) {
	        auxChannels |= 0x00;
	    }
	    else if(aux3Scale < 0.3333){
	        auxChannels |= 0x04;
	    }
	    else{
	        auxChannels |= 0x08;
	    }
	    
	    float aux4Scale = channelList[7];
	    
	    if (aux4Scale < -0.666) {
	        auxChannels |= 0x00;
	    }
	    else if(aux4Scale < 0.3333){
	        auxChannels |= 0x01;
	    }
	    else{
	        auxChannels |= 0x02;
	    }
	    
	    dataPackage[5 + 4] = (byte) auxChannels;
	    checkSum ^= (dataPackage[5 + 4] & 0xFF);
	       
	    dataPackage[checkSumIdx] = (byte) checkSum;
	}
}
