package com.elf_vrdrone.view;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import com.elf_vrdrone.ble.BluetoothHandler;
import com.elf_vrdrone.modal.Channel;
import com.elf_vrdrone.modal.OSDCommon;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.Toast;

public class TouchHandler  implements OnTouchListener{
	private DisplayMetrics dm;
	private int screenWidth;
	private int screenHeight;		// 当Point的移动范围为0时默认设置移动范围为屏幕高的1/4
	
	public int imageViewDirLeft;
	public int imageViewDirRight;
	public int imageViewDirTop;
	public int imageViewDirBottom;
	public int imageViewAccLeft;
	public int imageViewAccRight;
	public int imageViewAccTop;
	public int imageViewAccBottom;	// 记录初始进入界面时控制轴的位置
	
	private int movePixelRange;
	private int accUpOffset = 0;	// 记录油门松开时的偏移量, 因为当手指按在屏幕的另外一个地方的时候油门位置保持之前松开的位置
	private int dirUpOffset = 0;
	
	private int dirLeftMin;
	private int dirRightMax;
	private int dirTopMin;
	private int dirBottomMax;			
	private int accLeftMin;
	private int accRightMax;
	private int accTopMin;
	private int accBottomMax;			// 记录移动范围
	
	public int imageViewAccCurLeft;
	public int imageViewAccCurRight;
	public int imageViewAccCurTop;
	public int imageViewAccCurBottom;	// 记录当前位置
	
	private float dirDeadMax = 40.0f;
	private float accDeadMax = 30.0f;	// 这是控制轴有效范围
	
	private boolean isLeftMode = false;	// 是否是左手模式
	
	public final static int CHANNEL_NAME_AILERON  = 0;
	public final static int CHANNEL_NAME_ELEVATOR = 1;
	public final static int CHANNEL_NAME_RUDDER   = 2;
	public final static int CHANNEL_NAME_THROTTLE = 3;
	public final static int CHANNEL_NAME_AUX1     = 4;
	public final static int CHANNEL_NAME_AUX2     = 5;
	public final static int CHANNEL_NAME_AUX3     = 6;
	public final static int CHANNEL_NAME_AUX4     = 7;
	
	Context context = null;
	public static float dirGain = 0.7f;		
	public static float accGain = 0.7f;		// 新手模式和非新手模式增益控制
	public static int rotateOffset = 0;
	private ArrayList<Channel> channelArrayList;
	
	public TouchHandler(Context context, boolean isLeftMode) {
		// TODO Auto-generated constructor stub
		this.context = context;
		this.isLeftMode = isLeftMode;
		dm = context.getResources().getDisplayMetrics();
		screenWidth = dm.widthPixels;
		screenHeight = dm.heightPixels;
		// movePixelRange默认设置为屏幕高度的1/4，但是若获取控制轴背景图标的高度
		// 成功的话会把movePixelRange设置为这个图标高度
		movePixelRange = (int) ((float)screenHeight/4f);	
	}
	
	// 初始化两个控制轴的位置
	// 这个函数会在进入MainActivity时调用，就是刚加载MainActivity时和从其他Activity返回时调用
	public void initImagePosition(){		
		Message msg = new Message();
		msg.what = 1;
		handler.sendMessage(msg);
	}
	
	public int getAccUpOffset(){			// 获取油门的偏差值
		return accUpOffset;
	}
	
	public void setAccUpOffset(int offset){	// 设置油门偏差值
		accUpOffset = offset;
	}
	
	public void setAccPosition(){
		Message msg = new Message();
		msg.what = 2;
		msg.arg1 = 0;
		handler.sendMessage(msg);
	}
	
	// 设置油门控制轴的位置，在刚连接上蓝牙和断开蓝牙以及解锁和加锁后都要复位油门控制轴的位置
	public void setAccPosition(int offset){	
		Message msg = new Message();
		msg.what = 2;
		msg.arg1 = offset;
		handler.sendMessage(msg);
	}
	
	// 获取方向控制轴的参数，在当方向控制轴按下时要获取参数，方便以后使用这些值
	// 其中可以根据dirTopMin或dirBottomMax的值来获得accUpOffset的值
	private void setDirImageViewParameter(){
		ImageView tempImageView = null;
		if(!isLeftMode){
			tempImageView = MainActivity.getInstance().imageViewLeft;
		}else{
			tempImageView = MainActivity.getInstance().imageViewRight;
		}
		imageViewDirLeft = tempImageView.getLeft();
		imageViewDirRight = tempImageView.getRight();
		imageViewDirTop = tempImageView.getTop();
		imageViewDirBottom = tempImageView.getBottom();
		
		dirLeftMin = imageViewDirLeft-movePixelRange;
		dirRightMax = imageViewDirRight+movePixelRange;
		dirTopMin = imageViewDirTop-movePixelRange;
		dirBottomMax = imageViewDirBottom+movePixelRange;
	}
	
	private void setAccImageViewParameter(){
		ImageView tempImageView = null;
		if(isLeftMode){
			tempImageView = MainActivity.getInstance().imageViewLeft;
		}
		else {
			tempImageView = MainActivity.getInstance().imageViewRight;
		}
		imageViewAccLeft = tempImageView.getLeft();
		imageViewAccRight = tempImageView.getRight();
		imageViewAccTop = tempImageView.getTop();
		imageViewAccBottom = tempImageView.getBottom();
		
		accLeftMin = imageViewAccLeft-movePixelRange;
		accRightMax = imageViewAccRight+movePixelRange;
		accTopMin = imageViewAccTop-movePixelRange*2+accUpOffset;
		accBottomMax = imageViewAccBottom+accUpOffset;
	}
	
	public void initImageRect(){
		//System.out.println(imageViewDirLeft+"+"+imageViewDirRight+"+"+imageViewDirTop+"+"+imageViewDirBottom);
		movePixelRange = (int) ((MainActivity.getInstance().imageViewJoyBackgroundLeft.getHeight()-
				MainActivity.getInstance().imageViewLeft.getHeight())/2f);
		if(movePixelRange == 0)
			movePixelRange = (int) ((float)screenHeight/4f);
		setDirImageViewParameter();
		setAccImageViewParameter();
	}
	
	Handler handler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			int top, bottom;
			
			switch(msg.what){
			case 1:
				if(!isLeftMode){
					MainActivity.getInstance().imageViewLeft.layout(imageViewDirLeft, imageViewDirTop-movePixelRange, imageViewDirRight, imageViewDirBottom-movePixelRange);
					MainActivity.getInstance().imageViewRight.layout(imageViewAccLeft, imageViewAccTop-accUpOffset, imageViewAccRight, imageViewAccBottom-accUpOffset);
				}else {
					MainActivity.getInstance().imageViewRight.layout(imageViewDirLeft, imageViewDirTop-movePixelRange, imageViewDirRight, imageViewDirBottom-movePixelRange);
					MainActivity.getInstance().imageViewLeft.layout(imageViewAccLeft, imageViewAccTop-accUpOffset, imageViewAccRight, imageViewAccBottom-accUpOffset);
				}break;
			case 2: 	// 相对点为当前位置移动accUpOffset个单位到0点，而不是把按下的那个位置作为相对点
				if(!isLeftMode){
					top = MainActivity.getInstance().imageViewRight.getTop();
					bottom = MainActivity.getInstance().imageViewRight.getBottom();
					MainActivity.getInstance().imageViewRight.layout(imageViewAccLeft, top+accUpOffset-msg.arg1, imageViewAccRight, bottom+accUpOffset-msg.arg1);
				}else {
					top = MainActivity.getInstance().imageViewLeft.getTop();
					bottom = MainActivity.getInstance().imageViewLeft.getBottom();
					MainActivity.getInstance().imageViewLeft.layout(imageViewAccLeft, top+accUpOffset-msg.arg1, imageViewAccRight, bottom+accUpOffset-msg.arg1);
				}
				accUpOffset = msg.arg1;
				break;
			default:break;
			}
			super.handleMessage(msg);
		}
		
	};

	public void setChannelArrayList(ArrayList list){
		channelArrayList = list;
	}
	
	public void doDirMove(View v, MotionEvent event){
		float x = event.getRawX();
		float y = event.getRawY();
		float left = x-v.getWidth()/2;
		float right = x+v.getWidth()/2;
		float top = y-v.getHeight()/2;
		float bottom = y+v.getHeight()/2;
		
		if(left < dirLeftMin){
			left = dirLeftMin;
			right = left + v.getWidth();
		}
		if(right > dirRightMax){
			right = dirRightMax;
			left = right - v.getWidth();
		}
		if(top < dirTopMin){
			top = dirTopMin;
			bottom = top + v.getHeight();
		}
		if(bottom > dirBottomMax){
			bottom = dirBottomMax;
			top = bottom - v.getHeight();
		}
		v.layout((int)left, (int)top, (int)right, (int)bottom);
		
		int aileron = 125;
		float leftTemp = movePixelRange+dirLeftMin-left;
		float rightTemp = right-dirRightMax+movePixelRange;
		if(leftTemp > 0.0f){
			leftTemp = 125.0f * (leftTemp/(movePixelRange*1.0f));
			if(leftTemp > dirDeadMax)
				aileron = (int) (125.0f - (leftTemp-dirDeadMax)*dirGain);
		}
		if(rightTemp > 0.0f){
			rightTemp = 125.0f * (rightTemp/(movePixelRange*1.0f));
			if(rightTemp > dirDeadMax)
				aileron = (int) (125.0f + (rightTemp-dirDeadMax)*dirGain);
		}
		//System.out.printf("leftOffset=%f rightOffset=%f\n", leftTemp, rightTemp);
		
		int elevator = 125;
		float upTemp = dirBottomMax-bottom-movePixelRange;
		float downTemp = top-dirTopMin-movePixelRange;
		if(upTemp > 0){
			upTemp = 125.0f * (upTemp/(movePixelRange*1.0f));
			if(upTemp > dirDeadMax)
				elevator = (int) (125.0f + (upTemp-dirDeadMax)*dirGain);
		}
		if(downTemp > 0){
			downTemp = 125.0f * (downTemp/(movePixelRange*1.0f));
			if(downTemp > dirDeadMax)
				elevator = (int) (125.0f - (downTemp-dirDeadMax)*dirGain);
		}
		//System.out.printf("upOffset=%d downOffset=%d\n", upOffset, downOffset);
		elevator += MainActivity.getInstance().upDownOffset;
		if(elevator > 250) elevator = 250;
		if(elevator < 0) elevator = 0;
		aileron += MainActivity.getInstance().leftRightOffset;
		if(aileron > 250) aileron = 250;
		if(aileron < 0) aileron = 0;
		//System.out.printf("elevator=%d aileron=%d\n", elevator, aileron);
		channelArrayList.get(CHANNEL_NAME_ELEVATOR).setValue(elevator);
		channelArrayList.get(CHANNEL_NAME_AILERON).setValue(aileron);
	}
	
	public void doAccMove(View v, MotionEvent event){
		float x = event.getRawX();
		float y = event.getRawY();
		float left = x-v.getWidth()/2;
		float right = x+v.getWidth()/2;
		float top = y-v.getHeight()/2;
		float bottom = y+v.getHeight()/2;
		
		if(left < accLeftMin){
			left = accLeftMin;
			right = left + v.getWidth();
		}
		if(right > accRightMax){
			right = accRightMax;
			left = right - v.getWidth();
		}
		if(top < accTopMin){
			top = accTopMin;
			bottom = top + v.getHeight();
		}
		if(bottom > accBottomMax){
			bottom = accBottomMax;
			top = bottom - v.getHeight();
		}
		v.layout((int)left, (int)top, (int)right, (int)bottom);
		int rudder = 125;

		float leftTemp = movePixelRange+accLeftMin-left;
		float rightTemp = right-accRightMax+movePixelRange;
		
		if(leftTemp > 0.0f){
			leftTemp = 125.0f * (leftTemp/(movePixelRange*1.0f));
			if(leftTemp > accDeadMax)
				rudder = (int) (125.0f - (leftTemp-accDeadMax)*accGain);
		}
		if(rightTemp > 0.0f){
			rightTemp = 125.0f * (rightTemp/(movePixelRange*1.0f));
			if(rightTemp > accDeadMax)
				rudder = (int) (125.0f + (rightTemp-accDeadMax)*accGain);
		}
		rudder = rudder+rotateOffset;
		if(rudder > 250) rudder = 250;
		if(rudder < 1)	 rudder = 0;
		float throttle = (accBottomMax-bottom)*accGain*125f/movePixelRange;
		throttle = (float) (16*Math.sqrt(throttle));
		//throttle = (float) (throttle/15.8f)*(throttle/15.8f);
		//System.out.printf("left=%f right=%f rudder=%d\n",leftTemp, rightTemp, rudder);
		channelArrayList.get(CHANNEL_NAME_RUDDER).setValue(rudder);
		channelArrayList.get(CHANNEL_NAME_THROTTLE).setValue(throttle);
	}
	
	public void doDirUp(View v, MotionEvent event){
		v.layout(imageViewDirLeft, imageViewDirTop, imageViewDirRight, imageViewDirBottom);
		int aileron = 125;
		int elevator = 125;
		elevator += MainActivity.getInstance().upDownOffset;
		if(elevator > 250) elevator = 250;
		if(elevator < 0) elevator = 0;
		aileron += MainActivity.getInstance().leftRightOffset;
		if(aileron > 250) aileron = 250;
		if(aileron < 0) aileron = 0;
		//System.out.printf("elevator=%d aileron=%d\n", elevator, aileron);
		channelArrayList.get(CHANNEL_NAME_ELEVATOR).setValue(elevator);
		channelArrayList.get(CHANNEL_NAME_AILERON).setValue(aileron);
	}

	public void doAccUp(View v, MotionEvent event){
		float x = event.getRawX();
		float y = event.getRawY();
		float top = y-v.getHeight()/2;
		float bottom = y+v.getHeight()/2;
		
		if(top < accTopMin){
			top = accTopMin;
			bottom = top + v.getHeight();
		}
		if(bottom > accBottomMax){
			bottom = accBottomMax;
			top = bottom - v.getHeight();
		}
		accUpOffset = (int)(accBottomMax - bottom);
		//System.out.printf("accUpOffset = %d\n", accUpOffset);
		//System.out.println("up down:"+(int)((accBottomMax-bottom)*0.764f*125f/movePixelRange+9.0f));
		int rudder = 125+rotateOffset;
		if(rudder > 250) rudder = 250;
		if(rudder < 1)	 rudder = 0;
		float throttle = (accBottomMax-bottom)*accGain*125f/movePixelRange;
		throttle = (float) (16*Math.sqrt(throttle));
		//throttle = (float) (throttle/15.8f)*(throttle/15.8f);
		channelArrayList.get(CHANNEL_NAME_RUDDER).setValue(rudder);
		channelArrayList.get(CHANNEL_NAME_THROTTLE).setValue(throttle);
		v.layout(imageViewAccLeft, (int)top, imageViewAccRight, (int)bottom);
	}
	
	/**/
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		// TODO Auto-generated method stub
		if(v == MainActivity.getInstance().mainLeftFrameLayout || v == MainActivity.getInstance().mainRightFrameLayout){
			int action = event.getAction();
			switch(action){
			case MotionEvent.ACTION_DOWN:
				int width, height;
				int x, y;
				x = (int) event.getRawX();
				y = (int) event.getRawY();
				//System.out.printf("x=%f y=%f\n", event.getRawX(), event.getRawY());
				if(v == MainActivity.getInstance().mainLeftFrameLayout){
					width = MainActivity.getInstance().imageViewLeft.getHeight();
					height = MainActivity.getInstance().imageViewLeft.getWidth();
					//System.out.printf("width=%d height=%d\n", width, height);
					MainActivity.getInstance().imageViewLeft.layout(x-width/2, y-height/2, x+width/2, y+height/2);
					width = MainActivity.getInstance().imageViewJoyBackgroundLeft.getHeight();
					height = MainActivity.getInstance().imageViewJoyBackgroundLeft.getWidth();
					if(!isLeftMode){
						MainActivity.getInstance().imageViewJoyBackgroundLeft.layout(x-width/2, y-height/2, x+width/2, y+height/2);
						setDirImageViewParameter();
					}else{
						MainActivity.getInstance().imageViewJoyBackgroundLeft.layout(x-width/2, y-height/2-movePixelRange+accUpOffset, x+width/2, y+height/2-movePixelRange+accUpOffset);
						setAccImageViewParameter();
					}
				}
				if(v == MainActivity.getInstance().mainRightFrameLayout){
					width = MainActivity.getInstance().imageViewRight.getHeight();
					height = MainActivity.getInstance().imageViewRight.getWidth();
					MainActivity.getInstance().imageViewRight.layout(x-width/2, y-height/2, x+width/2, y+height/2);
					width = MainActivity.getInstance().imageViewJoyBackgroundRight.getHeight();
					height = MainActivity.getInstance().imageViewJoyBackgroundRight.getWidth();
					if(!isLeftMode){
						MainActivity.getInstance().imageViewJoyBackgroundRight.layout(x-width/2, y-height/2-movePixelRange+accUpOffset, x+width/2, y+height/2-movePixelRange+accUpOffset);
						setAccImageViewParameter();
					}else{
						MainActivity.getInstance().imageViewJoyBackgroundRight.layout(x-width/2, y-height/2, x+width/2, y+height/2);
						setDirImageViewParameter();
					}
				}
			break;
			case MotionEvent.ACTION_MOVE:
				if(v == MainActivity.getInstance().mainLeftFrameLayout){
					if(!isLeftMode){
						doDirMove(MainActivity.getInstance().imageViewLeft, event);
					}else{
						doAccMove(MainActivity.getInstance().imageViewLeft, event);
					}
				}
				if(v == MainActivity.getInstance().mainRightFrameLayout){
					if(!isLeftMode){
						doAccMove(MainActivity.getInstance().imageViewRight, event);
					}else{
						doDirMove(MainActivity.getInstance().imageViewRight, event);
					}
				}
			break;
			case MotionEvent.ACTION_UP:
				if(v == MainActivity.getInstance().mainLeftFrameLayout){
					if(!isLeftMode){
						doDirUp(MainActivity.getInstance().imageViewLeft, event);
					}else{
						doAccUp(MainActivity.getInstance().imageViewLeft, event);
					}
				}
				if(v == MainActivity.getInstance().mainRightFrameLayout){
					if(!isLeftMode){
						doAccUp(MainActivity.getInstance().imageViewRight, event);
					}else{
						doDirUp(MainActivity.getInstance().imageViewRight, event);
					}
				}
			break;
			}
		}
		return true;
	}
}
