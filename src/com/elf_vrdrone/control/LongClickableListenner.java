package com.elf_vrdrone.control;

import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

public abstract class LongClickableListenner implements OnTouchListener, OnGestureListener{
	
	private int repeatCount;
	private GestureDetector detector;
	private long interval;
	private long delay;
	private View currentView;
	HashMap<View, Timer> map = new HashMap<View, Timer>();
	
	public abstract void onRepeat(View v, int repeatCnt); 
	
	public LongClickableListenner(Context context, long delay,long interval) {
		// TODO Auto-generated constructor stub
		this.interval = interval;
		this.delay = delay;
		detector = new GestureDetector(context, this);
		repeatCount = 0;
	}

	public void resetRepeatCount(){
		repeatCount = 0;
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		// TODO Auto-generated method stub
		switch(event.getAction()){
		case MotionEvent.ACTION_UP:
			stop(v);
			map.remove(v);
			break;
		case MotionEvent.ACTION_DOWN:
			map.put(v, new Timer());
			start(v);
			break;
		case MotionEvent.ACTION_MOVE:
			
			break;
		}
		currentView = v;
		detector.onTouchEvent(event);
		return false;
	}
	
	private void start(final View v){
		Timer timer = map.get(v);
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				Message msg = new Message();
				msg.arg1 = 1;
				msg.arg2 = 1;
				msg.obj = v;
				handler.sendMessage(msg);
			}
		}, delay, interval);
	}
	
	private void stop(View v){
		Timer timer = map.get(v);
		if(timer != null)
			timer.cancel();
	}
	
	Handler handler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			onRepeat((View)msg.obj, repeatCount++);
		}
	};

	@Override
	public boolean onDown(MotionEvent e) {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public void onShowPress(MotionEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		// TODO Auto-generated method stub
		//System.out.println("onSingleTapUp");
		return true;
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		// TODO Auto-generated method stub
		//System.out.println("onScroll");
		stop(currentView);
		map.remove(currentView);
		return true;
	}

	@Override
	public void onLongPress(MotionEvent e) {
		// TODO Auto-generated method stub
		//System.out.println("onLongPress");
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		// TODO Auto-generated method stub
		//System.out.println("onFling");
		stop(currentView);
		map.remove(currentView);
		return true;
	}
}
