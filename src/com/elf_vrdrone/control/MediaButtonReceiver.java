package com.elf_vrdrone.control;

import android.content.BroadcastReceiver; 
import android.content.ComponentName;
import android.content.Context; 
import android.content.Intent; 
import android.media.AudioManager;
import android.os.IBinder;
import android.util.Log; 
import android.view.KeyEvent; 
 
public class MediaButtonReceiver extends BroadcastReceiver { 
    private static String TAG = "MediaButtonReceiver:"; 
    
    private static MediaButtonReceiver receiver = null;
    private static ComponentName componentName = null;
    
    
    public static MediaButtonReceiver getMediaButtonReceiver(Context context){
    	if(receiver == null){
    		receiver = new MediaButtonReceiver();
    		componentName = new ComponentName(context.getPackageName(), MediaButtonReceiver.class.getName());
    		((AudioManager)context.getSystemService(Context.AUDIO_SERVICE)).registerMediaButtonEventReceiver(componentName);
    	}
    	return receiver;
    }
    
    public void unRegisterMediaButtonEventReceiver(Context context){
    	((AudioManager)context.getSystemService(Context.AUDIO_SERVICE)).unregisterMediaButtonEventReceiver(componentName);
    }
    
    public static void deleteReceiver(){
    	receiver = null;
    }

	@Override 
    public void onReceive(Context context, Intent intent) { 
		// 获得Action 
        String intentAction = intent.getAction(); 
        // 获得KeyEvent对象 
        KeyEvent keyEvent = (KeyEvent) intent.getParcelableExtra(Intent.EXTRA_KEY_EVENT); 
 
        if (Intent.ACTION_MEDIA_BUTTON.equals(intentAction)) { 
            // 获得按键字节码 
            int keyCode = keyEvent.getKeyCode(); 
            // 按下 / 松开 按钮 
            int keyAction = keyEvent.getAction(); 
            // 获得事件的时间 
            long downtime = keyEvent.getEventTime(); 
 
            // 获取按键码 keyCode 
            StringBuilder sb = new StringBuilder(); 
            // 这些都是可能的按键码 ， 打印出来用户按下的键 
            if (KeyEvent.KEYCODE_MEDIA_NEXT == keyCode) { 
            	System.out.println("KEYCODE_MEDIA_NEXT"); 
            } 
            // 说明：当我们按下MEDIA_BUTTON中间按钮时，实际出发的是 KEYCODE_HEADSETHOOK 而不是 
            // KEYCODE_MEDIA_PLAY_PAUSE 
            if (KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE == keyCode) { 
            	System.out.println("KEYCODE_MEDIA_PLAY_PAUSE"); 
            } 
            if (KeyEvent.KEYCODE_HEADSETHOOK == keyCode) { 
            	System.out.println("KEYCODE_HEADSETHOOK"); 
            } 
            if (KeyEvent.KEYCODE_MEDIA_PREVIOUS == keyCode) { 
            	System.out.println("KEYCODE_MEDIA_PREVIOUS"); 
            } 
            if (KeyEvent.KEYCODE_MEDIA_STOP == keyCode) { 
            	System.out.println("KEYCODE_MEDIA_STOP"); 
            }
        } 
    } 
}
