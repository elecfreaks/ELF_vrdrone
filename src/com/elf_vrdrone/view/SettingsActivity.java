package com.elf_vrdrone.view;


import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.zip.Inflater;

import com.ElecFreaks.ELF_vrdrone.R;
import com.elf_vrdrone.control.LongClickableListenner;
import com.elf_vrdrone.control.MediaButtonReceiver;
import com.elf_vrdrone.control.MyOnPageChangeListenner;
import com.elf_vrdrone.modal.ApplicationSettings;
import com.elf_vrdrone.modal.MyArray;
import com.elf_vrdrone.modal.OSDCommon;
import com.elf_vrdrone.modal.Transmitter;
import com.g_zhang.p2pComm.nvcP2PComm;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Matrix;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

public class SettingsActivity extends Activity implements SensorEventListener{

	public WidgetMove pointWidgetMove;
	private int settingsPointLeft;
    private int settingsPointRight;
    private int settingsPointBottom;
    private int settingsPointTop;
	private ApplicationSettings appSettings;
	
	private SensorManager mSensorManager;
	private List<Sensor> mSensors;
	private MyOnPageChangeListenner onPageListenner;
	
	private int p2p_result;
    
    private final static int RESULT_RETURN_DATA = 100;
    
    private final static byte INIT_MOVE_POINT = 1;
    
    public AdjustController adjustController;
    private P2PVideoDisplayer p2pVideoDisplayer = null;
    private PIDSettings pidSettings;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		setContentView(R.layout.settings_main);
		
		mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		mSensors = mSensorManager.getSensorList(Sensor.TYPE_ALL);
		// 由于setRequestedOrientation()改变了屏幕方向，因此会重新调用onCreate()，
		// 因此初始化放到了onConfigurationChanged()里面，因为onConfigurationChanged()只调用一次
	}

	Handler handler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch(msg.arg1){
			case INIT_MOVE_POINT:
				//System.out.printf("from main: l:%d r:%d b:%d t:%d\n", settingsPointLeft, settingsPointRight,
	    		//		settingsPointBottom, settingsPointTop);
				if(settingsPointLeft != 0 && settingsPointRight != 0 && settingsPointBottom != 0 && settingsPointTop != 0){
					pointWidgetMove.layout(settingsPointLeft, settingsPointRight, settingsPointTop, settingsPointBottom);
				}
				break;
			default:
				break;
			}
		}
	};
	
	
	
	public void upTrimOnClick(View v){
		
		pointWidgetMove.moveUp(WidgetMove.movePixel);
		/*float value = MainActivity.getInstance().elevatorChannel.getValue();
		value += WidgetMove.adjustOffset;
		if(value > 250.0f) value = 250.0f;
		MainActivity.getInstance().elevatorChannel.setValue(value);
		*/
		Transmitter.getSharedTransmitter().transmmitSimpleCommand(OSDCommon.MSPCommnand.MSP_TRIM_UP);
	}

	public void downTrimOnClick(View v){
		
		pointWidgetMove.moveDown(WidgetMove.movePixel);
		/*float value = MainActivity.getInstance().elevatorChannel.getValue();
		value -= WidgetMove.adjustOffset;
		if(value < 0.0f) value = 0.0f;
		MainActivity.getInstance().elevatorChannel.setValue(value);
		*/
		Transmitter.getSharedTransmitter().transmmitSimpleCommand(OSDCommon.MSPCommnand.MSP_TRIM_DOWN);
	}
	
	public void leftTrimOnClick(View v){
		
		pointWidgetMove.moveLeft(WidgetMove.movePixel);
		/*float value = MainActivity.getInstance().aileronChannel.getValue();
		value -= WidgetMove.adjustOffset;
		if(value < 0.0f) value = 0.0f;
		MainActivity.getInstance().aileronChannel.setValue(value);
		*/
		Transmitter.getSharedTransmitter().transmmitSimpleCommand(OSDCommon.MSPCommnand.MSP_TRIM_LEFT);
	}

	public void rightTrimOnClick(View v){
		
		pointWidgetMove.moveRight(WidgetMove.movePixel);
		/*float value = MainActivity.getInstance().aileronChannel.getValue();
		value += WidgetMove.adjustOffset;
		if(value > 250.0f) value = 250.0f;
		MainActivity.getInstance().aileronChannel.setValue(value);
		*/
		Transmitter.getSharedTransmitter().transmmitSimpleCommand(OSDCommon.MSPCommnand.MSP_TRIM_RIGHT);
	}
	
	public void magCalibrateOnClick(View v){
		Transmitter.getSharedTransmitter().transmmitSimpleCommand(OSDCommon.MSPCommnand.MSP_MAG_CALIBRATION);
	}
	
	public void accCalibrateOnClick(View v){
		Transmitter.getSharedTransmitter().transmmitSimpleCommand(OSDCommon.MSPCommnand.MSP_ACC_CALIBRATION);
	}
	
	public void pointResetPositionOnClick(View v){
		pointWidgetMove.layoutOriginal();
		Transmitter.getSharedTransmitter().transmmitSimpleCommand(OSDCommon.MSPCommnand.MSP_TRIM_CLEAR);
	}
	
	public void setOnClick(View v){
		pidSettings.updateData();
		
		byte[] data = null;
		byte[] dstData = null;
		
		// 1st
		data = pidSettings.getExtraData();
		dstData = new byte[13];
		dstData[0] = '$';dstData[1] = 'M';dstData[2] = '<';
		dstData[3] = 7;			// data length
		dstData[12] ^= dstData[3];	// checksum
		dstData[4] = (byte)204;
		dstData[12] ^= dstData[4];
		int i=0;
		for(byte b:data){
			dstData[5+i] = b;
			dstData[12] ^= b;
			i++;
		}
		Transmitter.getSharedTransmitter().transmmitData(dstData);
	}
	
	public void saveProfileOnClick(View v){
		
	}
	
	public void loadOnClick(View v){	
		Transmitter.getSharedTransmitter().transmmitSimpleCommand(OSDCommon.MSPCommnand.MSP_PID);
	}
	
	public void resetOnClick(View v){
		Transmitter.getSharedTransmitter().transmmitSimpleCommand(OSDCommon.MSPCommnand.MSP_RESET_CONF);
	}
			
	private BroadcastReceiver myBroadcastReceiver = new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			String action = intent.getAction();
			if(action.equals("RECEIVED_DATA")){
				byte[] bytes = intent.getByteArrayExtra("recievedData");
				pidSettings.processReceivedData(bytes);
			}
		}
	};
	
	private void registerBoradcastReceiver(){
		IntentFilter myIntentFilter = new IntentFilter();
		myIntentFilter.addAction("RECEIVED_DATA");
		registerReceiver(myBroadcastReceiver, myIntentFilter);
	}
	
	public void backOnClick(View v){
		onBackPressed();
	}
	
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		if(p2pVideoDisplayer != null)
			if(p2pVideoDisplayer.getWaitConnectProgressBarStatus()){
				p2pVideoDisplayer.cancelConnectWatiProgressBar();
				return ;
			}
		
		Intent intent = new Intent();
		intent.putExtra("PointLeft", pointWidgetMove.getCurrentLeft());
		intent.putExtra("PointRight", pointWidgetMove.getCurrentRight());
		intent.putExtra("PointBottom", pointWidgetMove.getCurrentBottom());
		intent.putExtra("PointTop", pointWidgetMove.getCurrentTop());
		intent.putExtra("PointDX", pointWidgetMove.getDX());
		intent.putExtra("PointDY", pointWidgetMove.getDY());
		
		setResult(RESULT_RETURN_DATA, intent);
		// 视频数据到主界面
		MainActivity.videoIsInMainActivity = true;
		
		super.onBackPressed();
	}
	

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		for(Sensor sensor : mSensors){
			if(sensor.getType() == Sensor.TYPE_ACCELEROMETER)
				mSensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);
		}
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		unregisterReceiver(myBroadcastReceiver);
		
		// 由于视频需要在两个界面显示，因此不要销毁
		//if(p2pVideoDisplayer != null)
		//	p2pVideoDisplayer.onDestroy();
		
		super.onDestroy();
	}
	
	

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		for(Sensor sensor : mSensors){
			if(sensor.getType() == Sensor.TYPE_ACCELEROMETER)
				mSensorManager.unregisterListener(this);
		}
		super.onPause();
	}

	public void initSwitchImageView(){
		if(!appSettings.isBeginnerMode()){
			adjustController.getBeginnerImageView().setImageDrawable(getResources().getDrawable(R.drawable.switch_off));
		}else{
			adjustController.getBeginnerImageView().setImageDrawable(getResources().getDrawable(R.drawable.switch_on));
		}
		if(!appSettings.isLeftHanded()){
			adjustController.getLeftModeImageView().setImageDrawable(getResources().getDrawable(R.drawable.switch_off));
		}else{
			adjustController.getLeftModeImageView().setImageDrawable(getResources().getDrawable(R.drawable.switch_on));
		}
		if(!appSettings.isHeadFreeMode()){
			adjustController.getHeadFreemageView().setImageDrawable(getResources().getDrawable(R.drawable.switch_off));
			MainActivity.getInstance().aux1Channel.setValue(-1);
		}else{
			adjustController.getHeadFreemageView().setImageDrawable(getResources().getDrawable(R.drawable.switch_on));
			MainActivity.getInstance().aux1Channel.setValue(1);
		}
	}
	
	
	public void beginnerModeOnClick(View v){
		if(appSettings.isBeginnerMode()){
			adjustController.getBeginnerImageView().setImageDrawable(getResources().getDrawable(R.drawable.switch_off));
			appSettings.setIsBeginnerMode(false);
			TouchHandler.dirGain = 0.70f;
			TouchHandler.accGain = 0.80f;
		}else{
			adjustController.getBeginnerImageView().setImageDrawable(getResources().getDrawable(R.drawable.switch_on));
			appSettings.setIsBeginnerMode(true);
			TouchHandler.dirGain = 0.80f;
			TouchHandler.accGain = 1.00f;
		}
		appSettings.save();
	}
	
	public void imageViewFrameOnClick(View v){
		if(p2pVideoDisplayer.slidingDrawerIsOpened()){
			p2pVideoDisplayer.closeSlidingDrawer();
		}else{
			p2pVideoDisplayer.openSlidingDrawer();
		}
	}
	
	public void leftModeOnClick(View v){
		if(appSettings.isLeftHanded()){
			adjustController.getLeftModeImageView().setImageDrawable(getResources().getDrawable(R.drawable.switch_off));
			appSettings.setIsLeftMode(false);
		}else{
			adjustController.getLeftModeImageView().setImageDrawable(getResources().getDrawable(R.drawable.switch_on));
			appSettings.setIsLeftMode(true);
		}
		appSettings.save();
	}

	public void headFreeModeOnClick(View v){			
		if(appSettings.isHeadFreeMode()){
			adjustController.getHeadFreemageView().setImageDrawable(getResources().getDrawable(R.drawable.switch_off));
			appSettings.setIsHeadFreeMode(false);
			MainActivity.getInstance().aux1Channel.setValue(-1);
		}else{
			adjustController.getHeadFreemageView().setImageDrawable(getResources().getDrawable(R.drawable.switch_on));
			appSettings.setIsHeadFreeMode(true);
			MainActivity.getInstance().aux1Channel.setValue(1);
		}
		appSettings.save();
	}
	
	public void scanCameraOnClick(View v){
		if(p2pVideoDisplayer == null)
			return ;
		p2pVideoDisplayer.scanOnClick(v);
	}
	
	public void imageViewCameraOnClick(View v){
		if(p2pVideoDisplayer == null)
			return ;
		p2pVideoDisplayer.startOnClick(v);
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		// TODO Auto-generated method stub
		registerBoradcastReceiver();
		
		ViewPager settingsViewPager = (ViewPager) findViewById(R.id.settingsViewPager);
		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		final View layoutSettingsMain1 = inflater.inflate(R.layout.adjust_controller, null);
		View layoutSettingsMain2 = inflater.inflate(R.layout.p2p_video_displayer, null);
		View layoutSettingsMain3 = inflater.inflate(R.layout.pid_settings, null);
		
		MyPagerAdapter adapter = new MyPagerAdapter(this);
		
		// 添加layout
		String[] titlesString={"settings1", "settings2", "setting3"};
		adapter.setTitles(titlesString);
		
		adapter.addView(layoutSettingsMain1);
		adapter.addView(layoutSettingsMain2);
		adapter.addView(layoutSettingsMain3);
		
		// 添加布局标号，用点表示
		LinearLayout pageIndex = (LinearLayout) findViewById(R.id.pageIndex);
		pageIndex.removeAllViews();
		final ImageView[] imageViews = new ImageView[adapter.getCount()];
		for(int i=0; i<imageViews.length; i++){
			imageViews[i] = new ImageView(this);
			imageViews[i].setImageDrawable(getResources().getDrawable(R.drawable.btn_radio_off));
			imageViews[i].setPadding(2, 2, 2, 2);
			pageIndex.addView(imageViews[i]);
		}
		// 添加页面切换监听器
		onPageListenner = new MyOnPageChangeListenner(this, imageViews, 0);
		settingsViewPager.setAdapter(adapter);
		settingsViewPager.setOnPageChangeListener(onPageListenner);
		settingsViewPager.setCurrentItem(0);
		
		adjustController = new AdjustController(this, layoutSettingsMain1);
		// 由于这个页面中有几个按键需要长按，因此自定义了这个方法
		adjustController.setLongTouchListenner();
		
		// 获取设置参数
		String filesDir = getFilesDir().toString();
		appSettings = new ApplicationSettings(filesDir+"/Settings.plist"); 
		if(appSettings != null){
			initSwitchImageView();
		}else{
			Toast.makeText(SettingsActivity.this, "null", Toast.LENGTH_SHORT).show();
		}

		Intent intent = getIntent();		
		// 得到由MainActivity传过来的settingsPointLeft等初始值，用来设置这个点到上一次退出此页面的位置
		settingsPointLeft = intent.getIntExtra("PointLeft", 0);
		settingsPointRight = intent.getIntExtra("PointRight", 0);
		settingsPointBottom = intent.getIntExtra("PointBottom", 0);
		settingsPointTop = intent.getIntExtra("PointTop", 0);
		p2p_result = intent.getIntExtra("p2p_result", -1);
		
		// 这里使用了adjustController里面的adjustController.imageViewPointBackground对象,这个是用于移动的对象，因此要先构造adjustController
		// 因为Point的移动范围由它决定，为了适应不同的手机
		new Thread(new Runnable() {
			@Override
			public void run() { // 用于确定Point的初始位置
				// TODO Auto-generated method stub
				ImageView imageViewPoint = adjustController.getPointImageView();
				while(true){
					if(imageViewPoint.getLeft()!=0 && imageViewPoint.getRight()!=0 
							&& imageViewPoint.getTop()!=0 && imageViewPoint.getBottom()!=0){
						pointWidgetMove = new WidgetMove(imageViewPoint, SettingsActivity.this);
						Message msg = new Message();
						msg.arg1 = INIT_MOVE_POINT;
						handler.sendMessage(msg);
						break;
					}
				}
			}
		}).start();
			
		// 从另外一个界面切换过来的时候设置了视频到底显示在哪个界面
		if(!MainActivity.videoIsInMainActivity){	// 若在此界面显示
			p2pVideoDisplayer = P2PVideoDisplayer.getInstance();
			p2pVideoDisplayer.setParameter(this, layoutSettingsMain2, p2p_result);
		}
		
		pidSettings = new PIDSettings(this, layoutSettingsMain3);
		
		//System.out.println("onConfigurationChanged finished");
		super.onConfigurationChanged(newConfig);
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		// TODO Auto-generated method stub
		switch(event.sensor.getType()){
		case Sensor.TYPE_ACCELEROMETER:	//加速度
			if(onPageListenner != null && p2pVideoDisplayer != null && onPageListenner.getCurrentPageIndex() == 1){
				float rotation = 0.0f;
				float degrees = 0.0f;
				float value = 0.0f;
				
				value = event.values[SensorManager.DATA_Y]/10.0f;
				if(value > 1.0f || value < -1.0f)
					break;
				rotation = (float) Math.asin(value);
				degrees = (float) Math.toDegrees(rotation);
				p2pVideoDisplayer.setGraduationRotation(degrees*-1.0f);
				if((degrees > -16.0f && degrees < -8.0f)||(degrees > 8.0f && degrees < 16.0f)){
					System.out.printf("degrees = %f\n", degrees*4.25f*TouchHandler.dirGain+125.0f);
					MainActivity.getInstance().aileronChannel.setValue(degrees*4.25f*TouchHandler.dirGain+125.0f);
				}
				
				value = event.values[SensorManager.DATA_Z]/10.0f;
				if(value > 1.0f || value < -1.0f)
					break;
				rotation = (float) Math.asin(value);
				degrees = (float) Math.toDegrees(rotation);
				Matrix matrix = new Matrix();
				matrix.postTranslate(0, degrees*-3.1f);
				
				if(degrees > 15.0f && degrees < 60.0f){
					MainActivity.getInstance().throttleChannel.setValue((60.0f-degrees)*5.5f*TouchHandler.accGain);
					//System.out.printf("degrees = %f\n", (60.0f-degrees)*5.5f*TouchHandler.accGain);
				}	
				p2pVideoDisplayer.setGraduationMatrix(matrix);
			}
			break;
		default:
			break;
		}
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub
		
	}	
	
	public void catchPictureOnClick(View v){
		if(p2pVideoDisplayer == null)
			p2pVideoDisplayer.snapshotImage();
	}
	
	public void needAudioOnClick(View v){
		if(p2pVideoDisplayer == null)
			p2pVideoDisplayer.needAudio(v);
	}
	
	public void liveRecOnClick(View v){
		if(p2pVideoDisplayer == null)
			p2pVideoDisplayer.liveRecOnClick(v);
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		// TODO Auto-generated method stub
		super.onWindowFocusChanged(hasFocus);
	}
}
