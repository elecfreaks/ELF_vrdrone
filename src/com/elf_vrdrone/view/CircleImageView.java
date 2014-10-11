package com.elf_vrdrone.view;

import android.content.Context;  
import android.graphics.Bitmap;  
import android.graphics.Bitmap.Config;  
import android.graphics.Canvas;  
import android.graphics.Matrix;
import android.graphics.Paint;  
import android.graphics.PorterDuff.Mode;  
import android.graphics.PorterDuffXfermode;  
import android.graphics.Rect;  
import android.graphics.drawable.BitmapDrawable;  
import android.graphics.drawable.Drawable;  
import android.util.AttributeSet;  
import android.util.DisplayMetrics;
import android.widget.ImageView;  

public class CircleImageView extends ImageView {
	private Paint paint = new Paint();
	private Matrix matrix = new Matrix();
	private Context context;
	public int screenWidth;
	public int screenHeight;
	
	private void getScreenMetrics(){
		DisplayMetrics dm = context.getResources().getDisplayMetrics();
		screenWidth = dm.widthPixels;
		screenHeight = dm.heightPixels;
	}
	
	private void setupImageView(){
		this.setScaleType(ScaleType.MATRIX);
		this.setImageMatrix(matrix);
		getScreenMetrics();
	}
	
	public CircleImageView(Context context) {
		super(context);
		this.context = context;
		setupImageView();
	}
	
	public CircleImageView(Context context, AttributeSet attrs) {  
        super(context, attrs); 
        this.context = context;
        setupImageView();
    }  
  
    public CircleImageView(Context context, AttributeSet attrs, int defStyle) {  
        super(context, attrs, defStyle); 
        this.context = context;
        setupImageView();
    }
    
    public boolean drawBitmap(Canvas canvas){
    	Drawable drawable = getDrawable();  
        if (null != drawable) {  
            Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();  
            Bitmap b = toRoundCorner(bitmap, 14);  
            final Rect rect = new Rect(0, 0, b.getWidth(), b.getHeight());  
            paint.reset();  
            canvas.drawBitmap(b, rect, rect, paint);  
            return true;
        } 
        return false;
    }

	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		if(!drawBitmap(canvas))
			super.onDraw(canvas);
	} 
    
	private Bitmap toRoundCorner(Bitmap bitmap, int pixels) {  
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),  
                bitmap.getHeight(), Config.ARGB_8888);  
        Canvas canvas = new Canvas(output);  
          
        final int color = 0xff424242;  
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());  
        paint.setAntiAlias(true);  
        canvas.drawARGB(0, 0, 0, 0);  
        paint.setColor(color);  
        int x = bitmap.getWidth();  
        canvas.drawCircle(x / 2, x / 2, x / 2, paint);  
        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));  
        canvas.drawBitmap(bitmap, rect, rect, paint);  
        return output;  
    }  
}
