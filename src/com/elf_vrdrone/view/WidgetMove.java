package com.elf_vrdrone.view;

import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnLayoutChangeListener;
import android.widget.ImageView;

public class WidgetMove {
	private View moveObject;
	
	private int originalLeft;
	private int originalRight;
	private int originalTop;
	private int originalBottom;
	
	private int currentLeft;
	private int currentRight;
	private int currentTop;
	private int currentBottom;
	
	private int leftMoveRange;
	private int rightMoveRange;
	private int upMoveRange;
	private int downMoveRange;
	
	private int screenWidth;
	private int screenHeight;
	
	private int originalX;
	private int originalY;
	private int currentX;
	private int currentY;
	
	private int objHeight;
	private int objWidth;
	
	public static int adjustOffset = 2;
	public static int movePixel = 10;
	
	public boolean needRecoverPosition = false;
	
	public WidgetMove(View v, Context context){
		moveObject = v;
		moveObject.addOnLayoutChangeListener(new OnLayoutChangeListener() {
			
			@Override
			public void onLayoutChange(View v, int left, int top, int right,
					int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
				// TODO Auto-generated method stub
				//System.out.printf("old: l=%d, t=%d, r=%d, b=%d\n", oldLeft, oldTop, oldRight, oldBottom);
				//System.out.printf("new: l=%d, t=%d, r=%d, b=%d\n", left, top, right, bottom);
				if(needRecoverPosition){
					recoverPosition();
					needRecoverPosition = false;
				}
			}
		});
		getScreenMetrics(context);
		waitFinishLayout();
	}
	
	private void getOriginalXy(){
		originalX = originalLeft + (originalRight - originalLeft)/2;
		originalY = originalTop + (originalBottom - originalTop)/2;
	}
	
	public int getDX(){
		return currentLeft-originalLeft;
 	}
	
	public int getDY(){
		return originalTop-currentTop;
 	}
	
	private void getWidthHeight(){
		objWidth = originalRight - originalLeft;
		objHeight = originalBottom - originalTop;
	}
	
	private void updateCurrentXy(){
		currentX = currentLeft + objWidth/2;
		currentY = currentTop + objHeight/2;
	}
	
	public void setCurrentX(int x){
		currentX = x;
		layout(currentX, currentY);
		updateCurrentPosition();
		updateCurrentXy();
	}
	
	public void setCurrentY(int y){
		currentY = y;
		layout(currentX, currentY);
		updateCurrentPosition();
		updateCurrentXy();
	}
	
	public void setCurrentXy(int x, int y){
		currentX = x;
		currentY = y;
		layout(currentX, currentY);
	}
	
	public int getCurrentX(){
		return currentX;
	}
	
	public int getCurrentY(){
		return currentY;
	}
	
	public int getCurrentTop(){
		return currentTop;
	}
	
	public int getCurrentBottom(){
		return currentBottom;
	}
	
	public int getCurrentLeft(){
		return currentLeft;
	}
	
	public int getCurrentRight(){
		return currentRight;
	}
	
	public void layout(int x, int y){
		int l, r, t, b;
		l = x - objWidth/2;
		r = x + objWidth/2;
		t = y - objHeight/2;
		b = y + objHeight/2;
		
		layout(l, r, t, b);
	}
	
	public void layoutOriginal(){
		layout(originalLeft, originalRight, originalTop, originalBottom);
	}
	
	public void layoutWithoutRange(int l, int r, int t, int b){
		moveObject.layout(l, t, r, b);
		updateCurrentPosition();
		updateCurrentXy();
	}
	
	public void layout(int l, int r, int t, int b){
		if(l < 0) {l = 0; r = l + objWidth;}
		if(r > screenWidth) {r = screenWidth; l = r - objWidth;}
		if(t < 0){t = 0; b = t + objHeight;}
		if(b > screenHeight){b = screenHeight; t = b - objHeight;}
		
		if(l < leftMoveRange){l = leftMoveRange; r = l + objWidth;}
		if(r > rightMoveRange){r = rightMoveRange; l = r - objWidth;}
		if(t < upMoveRange){t = upMoveRange; b = t + objHeight;}
		if(b > downMoveRange){b = downMoveRange; t = b - objHeight;}
		
		moveObject.layout(l, t, r, b);
		updateCurrentPosition();
		updateCurrentXy();
	}
	
	private void getScreenMetrics(Context context){
		DisplayMetrics dm = context.getResources().getDisplayMetrics();
		screenWidth = dm.widthPixels;
		screenHeight = dm.heightPixels;
	}
	
	private void waitFinishLayout(){
		// TODO Auto-generated method stub
		originalLeft = moveObject.getLeft();
		originalRight = moveObject.getRight();
		originalTop = moveObject.getTop();
		originalBottom = moveObject.getBottom();
		
		updateCurrentPosition();
		//System.out.printf("waitFinishLayout:%d %d %d %d\n", 
		//		currentLeft, currentRight, currentTop, currentBottom);
		getOriginalXy();
		getWidthHeight();
		updateCurrentXy();
		if(AdjustController.imageViewPointBackground != null){
			setMoveRange(AdjustController.imageViewPointBackground.getWidth()/2-moveObject.getWidth()/2, 
					AdjustController.imageViewPointBackground.getWidth()/2-moveObject.getWidth()/2, 
					AdjustController.imageViewPointBackground.getHeight()/2-moveObject.getHeight()/2, 
					AdjustController.imageViewPointBackground.getHeight()/2-moveObject.getHeight()/2);
		}
	}
	
	private void waitFinishLayout(final int leftRange, final int rightRange, final int upRange, final int downRange){
		Timer timer = new Timer();
		timer.schedule(new TimerTask() {
			// wait finish initialize layout
			@Override
			public void run() {
				// TODO Auto-generated method stub
				originalLeft = moveObject.getLeft();
				originalRight = moveObject.getRight();
				originalTop = moveObject.getTop();
				originalBottom = moveObject.getBottom();
				
				updateCurrentPosition();
				//System.out.printf("waitFinishLayout:%d %d %d %d\n", 
				//		currentLeft, currentRight, currentTop, currentBottom);
				getOriginalXy();
				getWidthHeight();
				updateCurrentXy();
				setMoveRange(leftRange, rightRange, upRange, downRange);
			}
		}, 300);
	}
	
	public WidgetMove(View v, Context context, int leftRange, int rightRange, int upRange, int downRange){
		moveObject = v;
		getScreenMetrics(context);
		waitFinishLayout(leftRange, rightRange, upRange, downRange);
	}
	
	public void setLeftMoveRange(int range){
		leftMoveRange = originalLeft - range;
		if(leftMoveRange < 0)
			leftMoveRange = 0;
	}
	
	public void setRightMoveRange(int range){
		rightMoveRange = originalRight + range;
		if(rightMoveRange > screenWidth)
			rightMoveRange = screenWidth;
	}
	
	public void setUpMoveRange(int range){
		upMoveRange = originalTop - range;
		if(upMoveRange < 0)
			upMoveRange = 0;
	}
	
	public void setDownMoveRange(int range){
		downMoveRange = originalBottom + range;
		if(downMoveRange > screenHeight)
			downMoveRange = screenHeight;
	}
	
	public void setMoveRange(int l, int r, int u, int d){
		setLeftMoveRange(l);
		setRightMoveRange(r);
		setUpMoveRange(u);
		setDownMoveRange(d);
		//System.out.printf("movRange:%d %d %d %d\n", 
		//		rightMoveRange, rightMoveRange, upMoveRange, downMoveRange);
	}
	
	
	public void updateCurrentPosition(){
		currentLeft = moveObject.getLeft();
		currentRight = moveObject.getRight();
		currentTop = moveObject.getTop();
		currentBottom = moveObject.getBottom();
	}
	
	public void recoverPosition(){
		moveObject.layout(currentLeft, currentTop, currentRight, currentBottom);
		updateCurrentPosition();
		updateCurrentXy();
	}
	
	public void moveLeft(int offset){
		updateCurrentPosition();
		if(currentLeft-offset < leftMoveRange)
			offset = 0;
		moveObject.layout(currentLeft-offset, currentTop, currentRight-offset, currentBottom);
		updateCurrentPosition();
		updateCurrentXy();
	}
	
	public void moveRight(int offset){
		updateCurrentPosition();
		if(currentRight+offset > rightMoveRange)
			offset = 0;
		moveObject.layout(currentLeft+offset, currentTop, currentRight+offset, currentBottom);
		updateCurrentPosition();
		updateCurrentXy();
	}
	
	public void moveUp(int offset){
		updateCurrentPosition();
		if(currentTop-offset < upMoveRange)
			offset = 0;
		moveObject.layout(currentLeft, currentTop-offset, currentRight, currentBottom-offset);
		updateCurrentPosition();
		updateCurrentXy();
	}
	
	public void moveDown(int offset){
		updateCurrentPosition();
		if(currentBottom+offset > downMoveRange)
			offset = 0;
		moveObject.layout(currentLeft, currentTop+offset, currentRight, currentBottom+offset);
		updateCurrentPosition();
		updateCurrentXy();
	}
	
	public void moveLeftUp(int leftOffset, int upOffset){
		updateCurrentPosition();
		if(currentLeft-leftOffset < leftMoveRange)
			leftOffset = 0;
		if(currentTop-upOffset < upMoveRange)
			upOffset = 0;
		moveObject.layout(currentLeft-leftOffset, currentTop-upOffset, currentRight-leftOffset, currentBottom-upOffset);
		updateCurrentPosition();
	}
	
	public void gobackOriginalPosition(){
		moveObject.layout(originalLeft, originalTop, originalRight, originalBottom);
	}
}
