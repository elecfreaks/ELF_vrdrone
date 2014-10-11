package com.elf_vrdrone.modal;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.FileHandler;

import android.content.Context;
import android.content.res.AssetManager;
import android.provider.MediaStore.Files;
import android.util.Log;

import com.dd.plist.NSArray;
import com.dd.plist.NSDictionary;
import com.dd.plist.PropertyListParser;
import com.dd.plist.NSNumber;
import com.elf_vrdrone.view.MainActivity;


public class ApplicationSettings {
	private final static String INTERFACE_OPACITY  = "InterfaceOpacity";
	private final static String IS_LEFT_HANDED     = "IsLeftHanded";
	public final  static String IS_FIRST_RUN       = "IsFirstRun";
	private final static String IS_ACC_MODE        = "IsAccMode";
	private final static String IS_HEAD_FREE_MODE  = "IsHeadFreeMode";
	private final static String IS_ALT_HOLD_MODE   = "IsAltHoldMode";
	private final static String IS_BEGINNER_MODE   = "IsBeginnerMode";
	private final static String AILERON_DEAD_BAND  = "AileronDeadBand";
	private final static String ELEVATOR_DEAD_BAND = "ElevatorDeadBand";
	private final static String RUDDER_DEAD_BAND   = "RudderDeadBand";
	private final static String TAKE_OFF_THROTTLE  = "TakeOffThrottle";
	public final  static String CHANNELS           = "Channels";
	public final  static String IS_HOLD_ALTITUDE   = "IsHoldAltitudeMode";
	
	private String path;
	private static float thisVersion = 1.1f;

	private NSDictionary data;
	
	private float interfaceOpacity;
	private boolean isLeftHanded;
	private boolean isAccMode;
	private boolean isFirstRun;
	private boolean isHeadFreeMode;
	private boolean isAltHoldMode;
	private boolean isBeginnerMode;
	private boolean isHoldAltitudeMode;
	private float aileronDeadBand;
	private float elevatorDeadBand;
	private float rudderDeadBand;
	private float takeOffThrottle;
	
	private List<Channel> channels;
	
	public ApplicationSettings(String path)
	{
		this.path = path;

		try {
			data = (NSDictionary)PropertyListParser.parse(path);
			
			interfaceOpacity = ((NSNumber)data.objectForKey(INTERFACE_OPACITY)).floatValue();
			isLeftHanded     = ((NSNumber)data.objectForKey(IS_LEFT_HANDED)).boolValue();
			isAccMode        = ((NSNumber)data.objectForKey(IS_ACC_MODE)).boolValue();
			isFirstRun       = ((NSNumber)data.objectForKey(IS_FIRST_RUN)).boolValue();
			isHeadFreeMode   = ((NSNumber)data.objectForKey(IS_HEAD_FREE_MODE)).boolValue();
			isAltHoldMode    = ((NSNumber)data.objectForKey(IS_ALT_HOLD_MODE)).boolValue();
			isBeginnerMode   = ((NSNumber)data.objectForKey(IS_BEGINNER_MODE)).boolValue();
			aileronDeadBand  = ((NSNumber)data.objectForKey(AILERON_DEAD_BAND)).floatValue();
			elevatorDeadBand = ((NSNumber)data.objectForKey(ELEVATOR_DEAD_BAND)).floatValue();
			rudderDeadBand   = ((NSNumber)data.objectForKey(RUDDER_DEAD_BAND)).floatValue();
			takeOffThrottle  = ((NSNumber)data.objectForKey(TAKE_OFF_THROTTLE)).floatValue();
			isHoldAltitudeMode = ((NSNumber)data.objectForKey(IS_HOLD_ALTITUDE)).boolValue();
			
			NSArray rawChannels = (NSArray)data.objectForKey(ApplicationSettings.CHANNELS);
			int channelCount = rawChannels.count();
		
			channels = new ArrayList<Channel>(channelCount);
			
			for(int channelIdx = 0; channelIdx < channelCount; channelIdx++){
				Channel oneChannel = new Channel(this, channelIdx);
				channels.add(oneChannel);
			}	
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public ApplicationSettings(InputStream inputStream, Transmitter mTransmitter)
	{
		try {
			data = (NSDictionary)PropertyListParser.parse(inputStream);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void copyDefaultSettingsFileIfNeeded(String path, Context context){
		String settingsFileName        = "Settings.plist";        //user
		String defaultSettingsFileName = "DefaultSettings.plist"; //default
		String filePathString = null;
		
		File file = null;
		
		for(int i=0; i<2; i++){
			if(i == 0){
				filePathString = path+"/Settings.plist";
			}else if(i == 1){
				filePathString = path+"/DefaultSettings.plist";
			}
			file = new File(filePathString);
			AssetManager assetManager = context.getAssets();
			
			try {
				if (file.exists() == false) {
					InputStream in = null;
					OutputStream out = null;
					
					in = assetManager.open(settingsFileName);
					out = new FileOutputStream(filePathString);
					
					byte[] buffer = new byte[1024];
					int read;
					while ((read = in.read(buffer)) != -1) {
						out.write(buffer, 0, read);
					}
	
					in.close();
					in = null;
					out.flush();
					out.close();
					out = null;
				} 
			}catch (IOException e) {
				Log.e("tag", "Failed to copy asset file: " + settingsFileName, e);
			}
		}
    }

	
	public boolean save(){
		File file = new File(path);
		try {
			//save as xml£¬be compatible with the plist of iOS
			PropertyListParser.saveAsXML(data, file);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	public void resetToDefault(String path){
		ApplicationSettings defaultSettings = new ApplicationSettings(path);
		
		this.data = defaultSettings.getData();
		if(data == null)
			System.out.println("appSettings.data is null");
		
		this.interfaceOpacity = defaultSettings.getInterfaceOpacity();
		this.isLeftHanded = defaultSettings.isLeftHanded();
		this.isAccMode = defaultSettings.isAccMode();
		this.isFirstRun = defaultSettings.isFirstRun();
		this.isHeadFreeMode = defaultSettings.isHeadFreeMode();
		this.isAltHoldMode = defaultSettings.isAltHoldMode();
		this.isBeginnerMode = defaultSettings.isBeginnerMode();
		this.aileronDeadBand = defaultSettings.getAileronDeadBand();
		this.elevatorDeadBand = defaultSettings.getElevatorDeadBand();
		this.rudderDeadBand = defaultSettings.getRudderDeadBand();
		this.takeOffThrottle = defaultSettings.getTakeOffThrottle();
		
		int channelCount = defaultSettings.getChannelCount();
		
		for(int defaultChannelIdx = 0; defaultChannelIdx < channelCount; defaultChannelIdx++){
			Channel defaultChannel = new Channel(defaultSettings, defaultChannelIdx);
			Channel channel =  this.getChannel(defaultChannel.getName());
			
	        if(channel.getIdx() != defaultChannelIdx){
	        	Channel needsReordedChannel =channels.get(defaultChannelIdx);
	            needsReordedChannel.setIdx(channel.getIdx()); 
	            
	            Channel tmp = channels.get(defaultChannelIdx);
	            
	            channels.set(defaultChannelIdx, channels.get(channel.getIdx()));
	            channels.set(channel.getIdx(), tmp);
	            
	            channel.setIdx(defaultChannelIdx);
	        }
	        
	        channel.setReversed(defaultChannel.isReversed());
	        channel.setTrimValue(defaultChannel.getTrimValue());
	        channel.setOutputAdjustabledRange(defaultChannel.getOutputAdjustabledRange());
	        channel.setDefaultOutputValue(defaultChannel.getDefaultOutputValue());
	        channel.setValue(channel.getDefaultOutputValue());
		}
	}
	
	public NSDictionary getData() {
		return data;
	}

	public void setData(NSDictionary data) {
		this.data = data;
	}

	public float getInterfaceOpacity() {
		return interfaceOpacity;
	}

	public void setInterfaceOpacity(float interfaceOpacity) {
		this.interfaceOpacity = interfaceOpacity;
		data.put(INTERFACE_OPACITY, interfaceOpacity);
	}
	
	public boolean isLeftHanded() {
		return isLeftHanded;
	}
	
	public boolean isHoldAltitudeMode() {
		return isHoldAltitudeMode;
	}
	
	public boolean isFirstRun(){
		return isFirstRun;
	}

	public void setIsLeftMode(boolean isLeftHanded) {
		this.isLeftHanded = isLeftHanded;
		data.put(IS_LEFT_HANDED, isLeftHanded);
	}
	
	public void setIsFirstRun(boolean isFirstRun) {
		this.isFirstRun = isFirstRun;
		data.put(IS_FIRST_RUN, isFirstRun);
	}
	
	public boolean isAccMode() {
		return isAccMode;
	}

	public void setIsAccMode(boolean isAccMode) {
		this.isAccMode = isAccMode;
		data.put(IS_ACC_MODE, isAccMode);
	}
	
	public void setIsHoldAltitudeMode(boolean isHoldAltitudeMode){
		this.isHoldAltitudeMode = isHoldAltitudeMode;
		data.put(IS_HOLD_ALTITUDE, isHoldAltitudeMode);
	}
	
	public boolean isHeadFreeMode() {
		return isHeadFreeMode;
	}

	public void setIsHeadFreeMode(boolean isHeadFreeMode) {
		this.isHeadFreeMode = isHeadFreeMode;
		data.put(IS_HEAD_FREE_MODE, isHeadFreeMode);
	}
	
	public boolean isAltHoldMode() {
		return isAltHoldMode;
	}
	
	public boolean isBeginnerMode() {
		return isBeginnerMode;
	}

	public void setIsBeginnerMode(boolean isBeginnerMode) {
		this.isBeginnerMode = isBeginnerMode;
		data.put(IS_BEGINNER_MODE, isBeginnerMode);
	}
	

	public void setIsAltHoldMode(boolean isAltHoldMode) {
		this.isAltHoldMode = isAltHoldMode;
		data.put(IS_LEFT_HANDED, isAltHoldMode);
	}
	
	public float getAileronDeadBand() {
		return aileronDeadBand;
	}

	public void setAileronDeadBand(float aileronDeadBand) {
		this.aileronDeadBand = aileronDeadBand;
		data.put(AILERON_DEAD_BAND, aileronDeadBand);
	}

	public float getElevatorDeadBand() {
		return elevatorDeadBand;
	}

	public void setElevatorDeadBand(float elevatorDeadBand) {
		this.elevatorDeadBand = elevatorDeadBand;
		data.put(ELEVATOR_DEAD_BAND, elevatorDeadBand);
	}

	public float getRudderDeadBand() {
		return rudderDeadBand;
	}

	public void setRudderDeadBand(float rudderDeadBand) {
		this.rudderDeadBand = rudderDeadBand;
		data.put(RUDDER_DEAD_BAND, rudderDeadBand);
	}
	
	public float getTakeOffThrottle() {
		return takeOffThrottle;
	}

	public void setTakeOffThrottle(float takeOffThrottle) {
		this.takeOffThrottle = takeOffThrottle;
		data.put(TAKE_OFF_THROTTLE, takeOffThrottle);
	}
	
	public int getChannelCount(){
	    return channels.size();
	}

	public Channel getChannel(int idx){
	    if(idx < channels.size()){
	    	return channels.get(idx);
	    }
	    else{
	    	return null;
	    }    
	}

	public Channel getChannel(String name){
	    for(Channel oneChannel : channels){
	    	if(name.equals(oneChannel.getName())){
	    		return oneChannel;
	    	}
	    }
	    
	    return null;
	}
}
