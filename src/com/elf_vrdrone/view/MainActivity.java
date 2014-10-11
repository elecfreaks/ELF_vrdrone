package com.elf_vrdrone.view;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import com.dd.plist.Base64.OutputStream;
import com.elf_vrdrone.ble.BluetoothHandler;
import com.elf_vrdrone.control.MediaButtonReceiver;
import com.elf_vrdrone.modal.ApplicationSettings;
import com.elf_vrdrone.modal.Channel;
import com.elf_vrdrone.modal.MyArray;
import com.elf_vrdrone.modal.OSDCommon;
import com.elf_vrdrone.modal.Transmitter;
import com.ElecFreaks.ELF_vrdrone.R;
import com.g_zhang.p2pComm.P2PCommDef;
import com.g_zhang.p2pComm.nvcP2PComm;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.media.AudioManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class MainActivity extends Activity{
	//////////Camera///////////
	public static boolean videoIsInMainActivity = true;
	P2PVideoDisplayer p2pVideoDisplayer = null;
	////////////////////////////
	
	public int screenWidth;
	public int screenHeight;
	// airplane control lock
	private boolean mIsLocked = false;
	private ImageView imageViewLock = null;
	
	public ImageView imageViewLeft = null;
	public ImageView imageViewRight = null;
	public ImageView imageViewJoyBackgroundLeft = null;
	public ImageView imageViewJoyBackgroundRight = null;
	// hold radio button
	private ImageView imageViewKeepAltitudeMode;	
	
	public FrameLayout mainLeftFrameLayout = null;
	public FrameLayout mainRightFrameLayout = null;
	public LinearLayout mainLinearLayout = null;
	public RelativeLayout mainRelativeLayout = null;
	public ImageView mainImageView = null;
	
	public AlertDialog alertDialog = null;
	
	public BluetoothHandler mBluetoothHandler = null;
	public ImageView imageViewScan = null;
	private boolean mConnected = false;
	public ApplicationSettings appSettings = null;
	
	public Channel aileronChannel;
	public Channel elevatorChannel;
	public Channel rudderChannel;
	public Channel throttleChannel;
	public Channel aux1Channel;
	public Channel aux2Channel;
	public Channel aux3Channel;
	public Channel aux4Channel;
    private ArrayList<Channel> channelArrayList;
    public TouchHandler touchHandler;
    
    private FrameLayout mainFramLayoutProgressBar;
    private boolean waitConnectProgressBarIsVisible = false;
    private Transmitter transmitter = null;
    
    private int receivedDataCount = 0;
    private int receivedDataLength = 0;
    private int receivedDataStartIndex = 0;
    private boolean receiveStart = false;
    private byte[] receivedDataBytes = null;
    
    private final static byte MSP_RC_TUNING = 0x6F;
    private final static byte MSP_PID = 0x70;
    //private final static byte MSP_SET_PID = (byte)202;
    private final static byte MSP_SET_RAW_RC_TINY = (byte)150;
    private final static byte MSP_ARM = (byte)151;
    private final static byte MSP_DISARM = (byte)152;
    private final static byte MSP_SET_P = (byte)157;
    private final static byte MSP_SET_I = (byte)158;
    private final static byte MSP_SET_D = (byte)159;
    private final static byte MSP_GET_SENSOR_DATA = (byte)161;
    private final static byte MSP_GET_BARO_P = (byte)162;
    private final static byte MSP_EEPROM_WRITE = (byte)250;
    private final static byte MSP_SET_RC_TUNING = (byte)204;
    private final static byte MSP_RESET_CONF = (byte)208;
    
    private final static int RESULT_RETURN_DATA = 100;
    
    private int settingsPointLeft;
    private int settingsPointRight;
    private int settingsPointBottom;
    private int settingsPointTop;
    private int settingsPointDX;
    private int settingsPointDY;
    
    private float planeHeight = 0;
    
    public int leftRightOffset = 0;	
    public int upDownOffset = 0;	// 记录AdjustController界面中Point的偏差值
    private MediaButtonReceiver myMediaButtonReceiver;
    
    private int p2p_result = 0;
    
    private static boolean isFirstPressSettings = true;
    
    private static MainActivity ins = null;
    
    public boolean needInitJoyImageViews = true;
	//private TextView textViewTest;
	private boolean MainThreadIsRunning = true;
	
	private PrintStream debugFilePrintStream;
	private FileOutputStream debugFileOutputStream;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.fragment_main);
		/*
		 * 刚进入Activity时会调用onCreate，当界面设置为水平时又会调用一次onCreate，因此
		 * 若把界面设置为水平，onCreate会调用两次，而我们有一些在onCreate中初始化的代码只要调用一次，
		 * 当界面变化时会调用onConfigurationChanged(Configuration)，这时我们可以在这里面初始化
		 * */
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		ins = this;	// 用于外部类对象使用MainActivity类对象的成员
	}
	
	public static MainActivity getInstance(){
		return ins;
	}
	
	/* 解锁与锁定，因为在连接蓝牙时不能立即对飞机进行控制，只有解锁成功以后才能对飞机进行控制
	 * */
	public void lock(View v){
		if(mBluetoothHandler != null && mConnected){
			transmitter = Transmitter.getSharedTransmitter();
			if(mIsLocked){
				transmitter.transmmitSimpleCommand(OSDCommon.MSPCommnand.MSP_DISARM);
			}else{
				transmitter.transmmitSimpleCommand(OSDCommon.MSPCommnand.MSP_ARM);
			}
		}else{
	    	ShowMessage(getResources().getString(R.string.please_connect_bluetooth));
		}
	}
	
	/* 设置按钮按下时会调用这个函数
	 * 切换到另一个界面(SettingsActivity)
	 * */
	public void onButtonSettings(View v){
		Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
		if(!isFirstPressSettings){	
			/* 由于第一次进入MainActivity时还没有得到下面这些参数，
			 * 因此不需要传递参数到SettingsActivity
			 */
			intent.putExtra("PointLeft", settingsPointLeft);
			intent.putExtra("PointRight", settingsPointRight);
			intent.putExtra("PointBottom", settingsPointBottom);
			intent.putExtra("PointTop", settingsPointTop);
		}else{
			isFirstPressSettings = false;
		}
		intent.putExtra("p2p_result", p2p_result);
		// 启动SettingsActivity
		startActivityForResult(intent, RESULT_RETURN_DATA);
		// 切换到下一个界面去显示视频
		if(videoIsInMainActivity)
			videoIsInMainActivity = false;
	}
	
	private void initChannels() {	// 初始化通道
	    aileronChannel  = appSettings.getChannel(Channel.CHANNEL_NAME_AILERON);
	    elevatorChannel = appSettings.getChannel(Channel.CHANNEL_NAME_ELEVATOR);
	    rudderChannel   = appSettings.getChannel(Channel.CHANNEL_NAME_RUDDER);
	    throttleChannel = appSettings.getChannel(Channel.CHANNEL_NAME_THROTTLE);
	    aux1Channel     = appSettings.getChannel(Channel.CHANNEL_NAME_AUX1);
	    aux2Channel     = appSettings.getChannel(Channel.CHANNEL_NAME_AUX2);
	    aux3Channel     = appSettings.getChannel(Channel.CHANNEL_NAME_AUX3);
	    aux4Channel     = appSettings.getChannel(Channel.CHANNEL_NAME_AUX4);
	    channelArrayList = new ArrayList<Channel>();
	    channelArrayList.add(aileronChannel);
	    channelArrayList.add(elevatorChannel);
	    channelArrayList.add(rudderChannel);
	    channelArrayList.add(throttleChannel);
	    channelArrayList.add(aux1Channel);
	    channelArrayList.add(aux2Channel);
	    channelArrayList.add(aux3Channel);
	    channelArrayList.add(aux4Channel);
	    
	    aileronChannel.setValue(125.0f);	//左右 
	    elevatorChannel.setValue(125.0f);	//上下
	    rudderChannel.setValue(125.0f+TouchHandler.rotateOffset);		//旋转
	    //throttleChannel.setValue(1.0f);		//油门
	}
	
	/* startActivityForResult()启动之后到下一个界面，
	 * 然后从那个界面返回的时候会调用这个函数
	 */
	@Override 
    protected void onActivityResult(int requestCode, int resultCode, Intent data) { 
        // TODO Auto-generated method stub  
        super.onActivityResult(requestCode, resultCode, data); 
        if (requestCode == 1) { 			// bluetooth open successful ?
            if (resultCode == RESULT_OK) { 
            	mBluetoothHandler.setEnabled(true);
            } else if (resultCode == RESULT_CANCELED) { 
            	mBluetoothHandler.setEnabled(false);
                finish(); 
            } 
        } 
        if(resultCode == RESULT_RETURN_DATA){	
        	// 从SettingsActivity返回的时候会传一些参数过来，因此在这里可以接收那些参数
        	settingsPointLeft = data.getIntExtra("PointLeft", 0);
    		settingsPointRight = data.getIntExtra("PointRight", 0);
    		settingsPointBottom = data.getIntExtra("PointBottom", 0);
    		settingsPointTop = data.getIntExtra("PointTop", 0);
    		settingsPointDX = data.getIntExtra("PointDX", 0);
    		settingsPointDY = data.getIntExtra("PointDY", 0);
    		// 得到AdjustController中Point的偏差值
    		leftRightOffset = settingsPointDX*WidgetMove.adjustOffset/(WidgetMove.movePixel);
    		upDownOffset = settingsPointDY*WidgetMove.adjustOffset/(WidgetMove.movePixel);
    		
    		// 把p2p视频显示在MainActivity    P2PVideoDisplayer这个类是一个单例模式
    		p2pVideoDisplayer = P2PVideoDisplayer.getInstance();
    		p2pVideoDisplayer.setParameter(this, getWindow().getDecorView(), p2p_result);
    		
    		// 得到保存参数的对象, 从这个对象中可以获取左手模式，新手模式等参数
    		String filesDir = getFilesDir().toString();	
    		appSettings = new ApplicationSettings(filesDir+"/Settings.plist"); 
            ApplicationSettings.copyDefaultSettingsFileIfNeeded(filesDir, this);
            
            // 根据是否是左手模式来设置图标和触摸控制器(touchHandler)
    		setHandMode();
        }
    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	/* 搜索蓝牙设备 */
	public void scan(View v){
		if(!mConnected){	// 若蓝牙未连接，则搜索蓝牙
			mBluetoothHandler.getDeviceListAdapter().clearDevice();
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			LayoutInflater inflater = LayoutInflater.from(this);
			View view = inflater.inflate(R.layout.device_list, null);
			
			ListView deviceListView = (ListView) view.findViewById(R.id.listViewDevice);
			deviceListView.setAdapter(mBluetoothHandler.getDeviceListAdapter());
			
			deviceListView.setOnItemClickListener(new OnItemClickListener() {
	
					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						BluetoothDevice device = mBluetoothHandler.getDeviceListAdapter().getItem(position);
						alertDialog.cancel();
						showConnectWaitProgressBar();
						// connect
						mBluetoothHandler.connect(device.getAddress());
					}
			});
			
			builder.setView(view);

			alertDialog = builder.create();
			alertDialog.show();
			
			mBluetoothHandler.scanLeDevice(true);	
		}else{			// 若蓝牙连接了则断开蓝牙
			transmitter = Transmitter.getSharedTransmitter();
			transmitter.transmmitSimpleCommand(OSDCommon.MSPCommnand.MSP_DISARM);
			//mBluetoothHandler.onPause();
			//mBluetoothHandler.onDestroy();
			showConnectWaitProgressBar();
			connectStatusCallback(false);
		}
	}
	
	
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}

	@Override
    protected void onDestroy() {
        super.onDestroy(); 
        
		try {
			debugFilePrintStream.close();
			debugFileOutputStream.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        isFirstPressSettings = true;
        MainThreadIsRunning = false;
        
        if(mBluetoothHandler != null){
        	Transmitter.getSharedTransmitter().stop();
        	mBluetoothHandler.onPause();
        	mBluetoothHandler.onDestroy();
        }
        if(videoIsInMainActivity)
        	p2pVideoDisplayer.onDestroy();
        if(p2p_result >= 0){
        	nvcP2PComm.DestoryP2PComm();
        }
        if(myMediaButtonReceiver != null)
        	myMediaButtonReceiver.unRegisterMediaButtonEventReceiver(this);
        MediaButtonReceiver.deleteReceiver();	
    }  
    
    public void connectStatusCallback(boolean isConnected){
    	cancelConnectWatiProgressBar();
    	if(isConnected){
    		transmitter = Transmitter.getSharedTransmitter();
    		transmitter.start();
    		imageViewScan.setImageDrawable(getResources().getDrawable(R.drawable.ble_connected));
    	}else{
    		transmitter = Transmitter.getSharedTransmitter();
    		transmitter.stop();
    		imageViewScan.setImageDrawable(getResources().getDrawable(R.drawable.ble_scan));
    		mIsLocked = false;
    		imageViewLock.setImageDrawable(getResources().getDrawable(R.drawable.btn_lock_image));
    		mBluetoothHandler.onPause();
    		mBluetoothHandler.onDestroy();
    	}
    	aileronChannel.setValue(125.0f);
	    elevatorChannel.setValue(125.0f);
	    rudderChannel.setValue(125.0f);		
	    throttleChannel.setValue(1.0f);		//油门

    	mConnected = isConnected;
    	mBluetoothHandler.mConnected = isConnected;
    	if(touchHandler != null){
    		touchHandler.setAccPosition(0);
    	}else{
    		System.out.println("touchHandler = null");
    	}
    	if(appSettings != null){
    		imageViewKeepAltitudeMode.setImageDrawable(getResources().getDrawable(R.drawable.switch_off));
			appSettings.setIsHoldAltitudeMode(false);
			aux2Channel.setValue(-1);
    	}
    }
    
    public void hasRecievedDataCallback(byte[] bytes){
    	//System.out.print("REC:");
    	//for(byte b:bytes)
    	//	System.out.printf("%02X ", b);
    	//System.out.println("");
    	
    	receivedDataBytes = MyArray.arrayCat(receivedDataBytes, bytes);
    	if(receivedDataBytes.length > 2){
	    	for(int i=0; i<receivedDataBytes.length; i++){
	    		if(receiveStart)break;
	    		if(receivedDataBytes[i] == (byte)0x24 && (receivedDataBytes.length-i>3) && receivedDataBytes[i+1] == (byte)0x4D && receivedDataBytes[i+2] == (byte)0x3E){
	    			receivedDataLength = (short) (receivedDataBytes[i+3]&0xff);
	    			receiveStart = true;
	    			receivedDataStartIndex = i;
	    			//System.out.printf("found head, start index=%d\n", i);
	    			break;
	    		}
	    	}
    	}
    	
    	if(receiveStart){
    		receivedDataCount = receivedDataBytes.length-receivedDataStartIndex;
    		if(receivedDataCount == (receivedDataLength+6)){
    			receiveStart = false;
    			receivedDataCount = 0;
    			//System.out.println("receive ok");
    			byte[] dstBytes = new byte[receivedDataLength+6];
        		System.arraycopy(receivedDataBytes, receivedDataStartIndex, dstBytes, 0, receivedDataLength+6);
    			// process data:receivedDataBytes
    			if(processReceivedData(dstBytes) == 0){// process ok
    				receivedDataBytes = null;
    				receivedDataStartIndex = 0;
    			}
    		}
    	}
    	
    }
    
    public int processReceivedData(byte[] bytes){
    	switch(bytes[4]){
    	case (byte)180:
    		System.out.printf("alt=%d\n", bytes[5]);
    		break;
    	case MSP_SET_P:
    	case MSP_SET_I:
    	case MSP_SET_D:
    	case MSP_SET_RC_TUNING:
    	case MSP_PID:
    	case MSP_EEPROM_WRITE:
    	case MSP_RC_TUNING:
    		Intent intent = new Intent("RECEIVED_DATA");
    		intent.putExtra("recievedData", bytes);
    		sendBroadcast(intent);
    		break;
    	case MSP_GET_BARO_P:
    		short p = (short) ((bytes[5]&0xff)+(bytes[6]&0xff)*256);
    		short estAlt = (short) ((bytes[7]&0xff) + (bytes[8]&0xff)*256);
    		short dError = (short) ((bytes[9]&0xff) + (bytes[10]&0xff)*256);
    		short throttle = (short) ((bytes[11]&0xff) + (bytes[12]&0xff)*256);
    		short i = (short) ((bytes[13]&0xff) + (bytes[14]&0xff)*256);
    		short d = (short) ((bytes[15]&0xff) + (bytes[16]&0xff)*256);
    		int pid = p + i + d;
    		if(pid > 123) pid = 123;
    		if(pid < -40) pid = -40;
    		//textViewTest.setText(String.format("p=%d \ti=%d \td=%d \tpid=%d \tcurrent=%d \tdError=%d \tthrottle=%d\n", p, i, d, pid, estAlt, dError, throttle));
    		debugFilePrintStream.printf("p=%d \ti=%d \td=%d \tpid=%d \tcurrent=%d \tdError=%d \tthrottle=%d\n", p, i, d, pid, estAlt, dError, throttle);
    		//System.out.printf("p=%d \ti=%d \td=%d \tpid=%d \tcurrent=%d \tdError=%d \tthrottle=%d\n", p, i, d, pid, estAlt, dError, throttle);
    		break;
    	case MSP_SET_RAW_RC_TINY:

    		break;
    	case MSP_GET_SENSOR_DATA:

    		break;
    	case MSP_RESET_CONF:
    		Transmitter.getSharedTransmitter().transmmitSimpleCommand(OSDCommon.MSPCommnand.MSP_PID);
    		break;
    	case MSP_ARM:
    		if(bytes[2] != '>'){
    			Toast.makeText(MainActivity.this, "failed", Toast.LENGTH_SHORT).show();
    			break;
    		}
    		mIsLocked = true;
    		aileronChannel.setValue(125.0f);
    	    elevatorChannel.setValue(125.0f);
    	    rudderChannel.setValue(125.0f);		
    	    throttleChannel.setValue(1.0f);		//油门
    		transmitter.start();
    		imageViewLock.setImageDrawable(getResources().getDrawable(R.drawable.btn_unlock_image));
    		// 初始化控制轴
    		if(touchHandler != null){
        		touchHandler.setAccPosition(0);
        	}
    		if(appSettings != null){
        		imageViewKeepAltitudeMode.setImageDrawable(getResources().getDrawable(R.drawable.switch_off));
    			appSettings.setIsHoldAltitudeMode(false);
    			aux2Channel.setValue(-1);
        	}
    		break;
    	case MSP_DISARM:
    		if(bytes[2] != '>'){
    			Toast.makeText(MainActivity.this, "failed", Toast.LENGTH_SHORT).show();
    			break;
    		}
    		aileronChannel.setValue(125.0f);
    	    elevatorChannel.setValue(125.0f);
    	    rudderChannel.setValue(125.0f);		
    	    throttleChannel.setValue(1.0f);		//油门
    		transmitter.stop();
    		mIsLocked = false;
    		imageViewLock.setImageDrawable(getResources().getDrawable(R.drawable.btn_lock_image));
    		// 初始化控制轴
    		if(touchHandler != null){
        		touchHandler.setAccPosition(0);
        	}
    		if(appSettings != null){
        		imageViewKeepAltitudeMode.setImageDrawable(getResources().getDrawable(R.drawable.switch_off));
    			appSettings.setIsHoldAltitudeMode(false);
    			aux2Channel.setValue(-1);
        	}
    		break;
    	}
    	
    	return 0;
    }


	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		// TODO Auto-generated method stub
		//Toast.makeText(this, "onConfigurationChanged", Toast.LENGTH_SHORT).show();
		mBluetoothHandler = new BluetoothHandler(this);
		if(!mBluetoothHandler.isSupportBle()){
			Toast.makeText(this, "your device not support BLE!", Toast.LENGTH_SHORT).show();
			this.finish();
			return ;
		}
		// open bluetooth
        if (!mBluetoothHandler.getBluetoothAdapter().isEnabled()) { 
            Intent mIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE); 
            startActivityForResult(mIntent, 1);   
        }else{
        	mBluetoothHandler.setEnabled(true);
        }    
        
        // you must setBluetoothHandler before new ApplicationSettings()
        // because it will call Transmitter.transmitteData() when you call
        // ApplicationSettings.resetToDefault() or Channel.setValue()
        // mBluetoothHandler is a static member of Transmitter, and this class can only
        // construct one instance, and it depend on BluetoothHandler
        Transmitter.setBluetoothHandler(mBluetoothHandler);
        
        myMediaButtonReceiver = MediaButtonReceiver.getMediaButtonReceiver(this);
        
        imageViewLeft = (ImageView) findViewById(R.id.imageViewDir);
		imageViewRight = (ImageView) findViewById(R.id.imageViewAcc);
		imageViewJoyBackgroundLeft = (ImageView) findViewById(R.id.imageViewJoyBackgroundLeft);
		imageViewJoyBackgroundRight = (ImageView) findViewById(R.id.imageViewJoyBackgroundRight);
		imageViewKeepAltitudeMode = (ImageView) findViewById(R.id.imageViewHoldAltitude);
		
		imageViewLock = (ImageView) findViewById(R.id.imageViewLock);
		imageViewScan = (ImageView) findViewById(R.id.imageViewScan);
		
		mainLeftFrameLayout = (FrameLayout) findViewById(R.id.mainLeftFrameLayout);
		mainRightFrameLayout = (FrameLayout) findViewById(R.id.mainRightFrameLayout);
		mainFramLayoutProgressBar = (FrameLayout) findViewById(R.id.mainFramLayoutProgressBar);
		
		//textViewTest = (TextView) findViewById(R.id.textViewTest);
		
		File debugFileDir = new File("/sdcard/elf_vrdron/debug/");
		if(debugFileDir.exists() == false)
			debugFileDir.mkdirs();
		
		File debugFile = new File("/sdcard/elf_vrdron/debug/debug.txt");
		
		try {
			if(debugFile.exists() == false)
				debugFile.createNewFile();
			debugFileOutputStream = new FileOutputStream(debugFile);
			debugFilePrintStream = new PrintStream(debugFileOutputStream);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		String filesDir = getFilesDir().toString();
        ApplicationSettings.copyDefaultSettingsFileIfNeeded(filesDir, this);
    	appSettings = new ApplicationSettings(filesDir+"/Settings.plist");
    	
    	if(appSettings.isBeginnerMode()){
    		TouchHandler.dirGain = 0.70f;
			TouchHandler.accGain = 0.80f;
    	}else{
    		TouchHandler.dirGain = 0.80f;
			TouchHandler.accGain = 1.00f;
    	}
    	initChannels();

    	imageViewKeepAltitudeMode.setImageDrawable(getResources().getDrawable(R.drawable.switch_off));
		appSettings.setIsHoldAltitudeMode(false);
		aux2Channel.setValue(-1);
    	
        // 这个函数用于确定左右控制轴是油门还是方向功能
        setHandMode(); // 这个函数里面会生成一个TouchHandler对象touchHandler
        
		DisplayMetrics dm = this.getResources().getDisplayMetrics();
		screenWidth = dm.widthPixels;
		screenHeight = dm.heightPixels;
		
		
		new Thread(new Runnable() {	// 更新控制轴的位置
			@Override
			public void run() {
				// TODO Auto-generated method stub
				while(MainThreadIsRunning){
					if(joyImageViewsLayoutFinished() && needInitJoyImageViews){
						//System.out.println("joyImageViewsLayoutFinished");
						touchHandler.initImageRect();			// 得到控制轴及控制轴背景的位置参数
						try {
							Thread.sleep(20);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						touchHandler.initImagePosition();		// 对控制轴及控制轴背景进行布局
						needInitJoyImageViews = false;
					}
				}
			}
		}).start();
		
		Intent intent = getIntent();
		settingsPointLeft = intent.getIntExtra("PointLeft", 0);
		settingsPointRight = intent.getIntExtra("PointRight", 0);
		settingsPointBottom = intent.getIntExtra("PointBottom", 0);
		settingsPointTop = intent.getIntExtra("PointTop", 0);
		
		mainLinearLayout = (LinearLayout) findViewById(R.id.mainLinearLayout);
		mainRelativeLayout = (RelativeLayout) findViewById(R.id.mainRelativeLayout);
		mainImageView = (ImageView) findViewById(R.id.mainImageView); // 用于显示视频的ImageView
		p2pVideoDisplayer = P2PVideoDisplayer.getInstance();
		
		new Thread(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				p2p_result = initP2PComm();
				///////////////////在主界面测试摄像头//////////////////////
				if(videoIsInMainActivity){
					p2pVideoDisplayer.setParameter(MainActivity.getInstance(), getWindow().getDecorView(), p2p_result);
				}
				///////////////////////////////////////////////////////
			}
		}).start();
		
		super.onConfigurationChanged(newConfig);
	}
    
    public void setHandMode(){
    	// 每次调用setHandMode()都会重新生成touchHandler对象
    	int offset = 0;
    	if(touchHandler != null){		// 保存之前对象的accUpOffset
    		offset = touchHandler.getAccUpOffset();
    	}
    	if(appSettings.isLeftHanded()){	//根据是否是左手模式来重新获得touchHandler
    		touchHandler = new TouchHandler(this, true);	
        }else{
        	touchHandler = new TouchHandler(this, false);	
        }
    	// 在第一次进入MainActivity时会启动一个线程一直在检测是否要更新两个控制轴图标的位置
    	// 由于界面刚开始加载或从别的界面进入到这个界面的时候就要重新设置控制轴图标的位置，
    	// 比如油门设置到上一次的位置，方向图标设置到中间位置(刚进入时它们都在最低的位置)
    	needInitJoyImageViews = true;	
    	touchHandler.setAccUpOffset(offset);
    	touchHandler.setChannelArrayList(channelArrayList);
    	if(touchHandler != null){	// 按下时其实不是按下控制轴图标，而是按下控制轴的布局来控制控制轴的位置的
    		mainLeftFrameLayout.setOnTouchListener(touchHandler);
    		mainRightFrameLayout.setOnTouchListener(touchHandler);
    	}
        
        if(appSettings.isHeadFreeMode())
    		aux1Channel.setValue(1.0f);
    	else
    		aux1Channel.setValue(-1.0f);
    	if(appSettings.isHoldAltitudeMode())
    		aux2Channel.setValue(1.0f);
    	else
    		aux2Channel.setValue(-1.0f);
    }
    
    public int initP2PComm() {
		WifiManager wm=(WifiManager)getSystemService(Context.WIFI_SERVICE);  
		//检查Wifi状态    
		if(!wm.isWifiEnabled()){  
			//wm.setWifiEnabled(true);  
			return -1;
		}
		WifiInfo wi=wm.getConnectionInfo();
		if(wi == null)
			return -1;
		//获取32位整型IP地址    
		int ipAdd=wi.getIpAddress(); 
		return nvcP2PComm.InitP2PServer(P2PCommDef.P2PSev_Root1,P2PCommDef.P2PSev_Root2,"","",P2PCommDef.P2P_SEVPORT, ipAdd);
	}  
    
    public void holdAltitudeModeOnClick(View v){
    	if(!mConnected){
    		ShowMessage(getResources().getString(R.string.please_connect_bluetooth));
    		imageViewKeepAltitudeMode.setImageDrawable(getResources().getDrawable(R.drawable.switch_off));
			appSettings.setIsHoldAltitudeMode(false);
			aux2Channel.setValue(-1);
    		return ;
    	}
		if(appSettings.isHoldAltitudeMode()){
			imageViewKeepAltitudeMode.setImageDrawable(getResources().getDrawable(R.drawable.switch_off));
			appSettings.setIsHoldAltitudeMode(false);
			aux2Channel.setValue(-1);
		}else{
			imageViewKeepAltitudeMode.setImageDrawable(getResources().getDrawable(R.drawable.switch_on));
			appSettings.setIsHoldAltitudeMode(true);
			aux2Channel.setValue(1);
		}
		appSettings.save();
	}
    
    public void scanCameraOnClick(View v){
    	if(videoIsInMainActivity && p2pVideoDisplayer != null){
    		p2pVideoDisplayer.scanOnClick(v);
    	}else{
    		ShowMessage(getResources().getString(R.string.offline));
    	}
    }
    
    public void ShowMessage(String str){
    	Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
    }
    
    public void showConnectWaitProgressBar(){ // 在界面布局的最顶端放了一个FrameLayout，在它的中间放了一个ProgressBar
		waitConnectProgressBarIsVisible = true;
		mainFramLayoutProgressBar.setVisibility(FrameLayout.VISIBLE);
	}
	
	public void cancelConnectWatiProgressBar(){
		waitConnectProgressBarIsVisible = false;
		mainFramLayoutProgressBar.setVisibility(FrameLayout.INVISIBLE);
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		if(waitConnectProgressBarIsVisible || (p2pVideoDisplayer != null && p2pVideoDisplayer.getWaitConnectProgressBarStatus())){
			cancelConnectWatiProgressBar();
			if(p2pVideoDisplayer != null)
				p2pVideoDisplayer.cancelConnectWatiProgressBar();
		}else{
			super.onBackPressed();
		}
	}
	// 设置图标的透明度
	public void setJoyImageViewsAlpha(float alpha){
		imageViewJoyBackgroundLeft.setAlpha(alpha);
		imageViewJoyBackgroundRight.setAlpha(alpha);
		imageViewLeft.setAlpha(alpha);
		imageViewRight.setAlpha(alpha);
	}
	// 判断布局是否完成
	public boolean joyImageViewsLayoutFinished(){
		if(imageViewJoyBackgroundLeft.getLeft()!=0 && imageViewJoyBackgroundLeft.getRight()!=0 
				&& imageViewJoyBackgroundLeft.getTop()!=0 && imageViewJoyBackgroundLeft.getBottom()!=0 && imageViewJoyBackgroundLeft.getHeight()!=0 && imageViewJoyBackgroundLeft.getWidth()!=0){
			if(imageViewJoyBackgroundRight.getLeft()!=0 && imageViewJoyBackgroundRight.getRight()!=0 
					&& imageViewJoyBackgroundRight.getTop()!=0 && imageViewJoyBackgroundRight.getBottom()!=0 && imageViewJoyBackgroundRight.getHeight()!=0 && imageViewJoyBackgroundRight.getWidth()!=0){
				if(imageViewLeft.getLeft()!=0 && imageViewLeft.getRight()!=0 
						&& imageViewLeft.getTop()!=0 && imageViewLeft.getBottom()!=0 && imageViewLeft.getHeight()!=0 && imageViewLeft.getWidth()!=0){
					if(imageViewRight.getLeft()!=0 && imageViewRight.getRight()!=0 
							&& imageViewRight.getTop()!=0 && imageViewRight.getBottom()!=0 && imageViewRight.getHeight()!=0 && imageViewRight.getWidth()!=0){
						return true;
					}else return false;
				}else return false;
			}else return false;
		}else return false;
	}
	
	public void imageViewCameraOnClick(View v){
		if(p2pVideoDisplayer == null || !videoIsInMainActivity)
			return ;
		p2pVideoDisplayer.startOnClick(v);
	}
}
