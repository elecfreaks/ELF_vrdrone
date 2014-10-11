package com.elf_vrdrone.view;

import java.util.ArrayList;

import android.content.Context;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.LinearLayout;

public class MyPagerAdapter  extends PagerAdapter{

	public ArrayList<View> views;
	private String[] titles = new String[0];
	Context context;
	
	public MyPagerAdapter(Context context) {
		this.context = context;
		views = new ArrayList<View>();
	}
	
	@Override
	public void finishUpdate(View arg0){
		
	}
	
	@Override
	public CharSequence getPageTitle(int position) {
		// TODO Auto-generated method stub
		return titles[position];
	}

	public void setTitles(String[] titles) {
		this.titles = titles;
	}

	public void addView(View v) {
		views.add(v);
	}

	@Override
	public void destroyItem(View view, int arg1, Object object) {
		((ViewPager) view).removeView((View)object);
	}

	//@Override
	//public void finishUpdate(View arg0) {
	//	System.out.println("finishUpdate");
	//}

	@Override
	public int getCount() {
		return views.size();
	}

	@Override
	public Object instantiateItem(View view, int position) {

		View myView = views.get(position);
		((ViewPager) view).addView(myView);
		
		return myView;
	}

	@Override
	public boolean isViewFromObject(View view, Object object) {
		return view == object;
	}

	@Override
	public void restoreState(Parcelable arg0, ClassLoader arg1) {

	}

	@Override
	public Parcelable saveState() {
		return null;
	}

	@Override
	public void startUpdate(View arg0) {

	}
}
