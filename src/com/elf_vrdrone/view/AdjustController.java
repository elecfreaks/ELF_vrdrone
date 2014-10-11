package com.elf_vrdrone.view;

import com.ElecFreaks.ELF_vrdrone.R;
import com.elf_vrdrone.control.LongClickableListenner;

import android.content.Context;
import android.text.InputType;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

public class AdjustController {
	private ImageView imageViewPoint;
	private ImageView imageViewUp;
	private ImageView imageViewDown;
	private ImageView imageViewLeft;
	private ImageView imageViewRight;
	
	private ImageView imageViewLeftMode;
	private ImageView imageViewHeadFreeMode;
	private ImageView imageViewBeginnerMode;
	private SeekBar seekBarRotateOffset;
	public TextView textViewRotateSeekBarValue;
	
	public static ImageView imageViewPointBackground = null;
	
	private Context context;
	private View view;
	
	private int screenWidth;
	private int screenHeight;
	private float scaleX;
	private float scaleY;
	private int lastX;
	private int lastY;
	
	public AdjustController(Context context, View view) {
		// TODO Auto-generated constructor stub
		this.context = context;
		this.view = view;
		
		imageViewPoint = (ImageView) view.findViewById(R.id.imageViewPoint);
		imageViewUp = (ImageView) view.findViewById(R.id.imageViewUp);
		imageViewDown = (ImageView) view.findViewById(R.id.imageViewDown);
		imageViewLeft = (ImageView) view.findViewById(R.id.imageViewLeft);
		imageViewRight = (ImageView) view.findViewById(R.id.imageViewRight);
		
		imageViewLeftMode = (ImageView) view.findViewById(R.id.imageViewLeftMode);
		imageViewBeginnerMode = (ImageView) view.findViewById(R.id.imageViewBeginnerMode);
		imageViewHeadFreeMode = (ImageView) view.findViewById(R.id.imageViewHeadFreeMode);
		seekBarRotateOffset = (SeekBar) view.findViewById(R.id.seekBarRotateOffset);
		textViewRotateSeekBarValue = (TextView) view.findViewById(R.id.textViewRotateSeekBarValue);
		
		seekBarRotateOffset.setProgress(TouchHandler.rotateOffset+50);
		textViewRotateSeekBarValue.setText(String.valueOf(TouchHandler.rotateOffset));
		seekBarRotateOffset.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
				//System.out.println("onStopTrackingTouch");
				((SettingsActivity)AdjustController.this.context).pointWidgetMove.recoverPosition();
				((SettingsActivity)AdjustController.this.context).pointWidgetMove.needRecoverPosition = true;
			}
			
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
				//System.out.println("onStartTrackingTouch");
			}
			
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				// TODO Auto-generated method stub
				TouchHandler.rotateOffset = (progress-50);
				MainActivity.getInstance().rudderChannel.setValue(125.0f+TouchHandler.rotateOffset);		//Ðý×ª
				textViewRotateSeekBarValue.setText(String.valueOf(TouchHandler.rotateOffset));
			}
		});
		
		DisplayMetrics dm = context.getResources().getDisplayMetrics();
		screenWidth = dm.widthPixels;
		screenHeight = dm.heightPixels;
		
		imageViewPointBackground = (ImageView) view.findViewById(R.id.imageViewPointBackground);
	}
	
	public ImageView getPointImageView(){
		return imageViewPoint;
	}
	
	public ImageView getUpImageView(){
		return imageViewUp;
	}
	
	public ImageView getDownImageView(){
		return imageViewDown;
	}
	
	public ImageView getLeftImageView(){
		return imageViewLeft;
	}
	
	public ImageView getRightImageView(){
		return imageViewRight;
	}
	
	public ImageView getLeftModeImageView(){
		return imageViewLeftMode;
	}
	
	public ImageView getBeginnerImageView(){
		return imageViewBeginnerMode;
	}
	
	public ImageView getHeadFreemageView(){
		return imageViewHeadFreeMode;
	}
	
	public void setLongTouchListenner(){
		imageViewUp.setOnTouchListener(longClickableListenner);
		imageViewDown.setOnTouchListener(longClickableListenner);
		imageViewLeft.setOnTouchListener(longClickableListenner);
		imageViewRight.setOnTouchListener(longClickableListenner);
	}
	
	private LongClickableListenner longClickableListenner = new LongClickableListenner(context, 500, 100) {
		
		@Override
		public void onRepeat(View v, int repeatCnt) {
			// TODO Auto-generated method stub
			if(v == imageViewUp){
				((SettingsActivity)context).upTrimOnClick(v);
			}
			if(v == imageViewDown){
				((SettingsActivity)context).downTrimOnClick(v);
			}
			if(v == imageViewLeft){
				((SettingsActivity)context).leftTrimOnClick(v);
			}
			if(v == imageViewRight){
				((SettingsActivity)context).rightTrimOnClick(v);
			}
		}
	};
	
	public void onDestroy(){
		imageViewPointBackground = null;
	}
}
