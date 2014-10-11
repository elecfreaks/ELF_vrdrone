package com.elf_vrdrone.control;

import com.ElecFreaks.ELF_vrdrone.R;

import android.content.Context;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.widget.ImageView;

public class MyOnPageChangeListenner implements OnPageChangeListener{

	private ImageView[] imageViews;
	private Context context;
	private int currentPageIndex;
	
	public MyOnPageChangeListenner(Context context, ImageView[] imageViews, int initPage) {
		// TODO Auto-generated constructor stub
		this.imageViews = imageViews;
		this.context = context;
		currentPageIndex = initPage;
		onPageSelected(initPage);
	}
	
	@Override
	public void onPageScrollStateChanged(int arg0) {
		// arg0=0:什么都没做, arg0=1:正在滑动, arg0=2:滑动完毕
		// TODO Auto-generated method stub
	}

	/*
	 * arg0 :当前页面，及你点击滑动的页面
	 * arg1:当前页面偏移的百分比
	 * arg2:当前页面偏移的像素位置 */
	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
		// TODO Auto-generated method stub
		
	}
	
	public int getCurrentPageIndex(){
		return currentPageIndex;
	}

	@Override
	public void onPageSelected(int arg0) {
		// TODO Auto-generated method stub
		currentPageIndex = arg0;
		if(imageViews != null){
			for(int i=0; i<imageViews.length; i++){
				if(i == arg0)
					imageViews[i].setImageDrawable(context.getResources().getDrawable(R.drawable.btn_radio_on));
				else
					imageViews[i].setImageDrawable(context.getResources().getDrawable(R.drawable.btn_radio_off));
			}
		}
	}
}
