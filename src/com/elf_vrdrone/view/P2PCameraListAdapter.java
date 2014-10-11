package com.elf_vrdrone.view;

import java.util.ArrayList;

import com.ElecFreaks.ELF_vrdrone.R;

import android.R.bool;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class P2PCameraListAdapter extends BaseAdapter{
	public class P2PCameraInfo{
		String uid;
		String ipAddr;
	};
	
	private class ViewHolder{
		TextView uid;
		TextView ipAddr;
	};
	
	private Context context;
	private ViewHolder viewHolder;
	private ArrayList<P2PCameraInfo> p2pCameraInfos;
	
	public P2PCameraListAdapter(Context context) {
		// TODO Auto-generated constructor stub
		p2pCameraInfos = new ArrayList<P2PCameraInfo>();
		this.context = context;
	}
	
	public boolean isContainP2PCamera(String uid){
		boolean isContain = false;
		for(P2PCameraInfo p2pcam: p2pCameraInfos){
			if(p2pcam.uid.endsWith(uid)){
				isContain = true;
				break;
			}
		}
		return isContain;
	}
	
	public boolean isEmpty(){
		return p2pCameraInfos.isEmpty();
	}
	
	public void addP2PCamera(P2PCameraInfo cam){
		p2pCameraInfos.add(cam);
	}
	
	public void clearP2PCamera(){
		p2pCameraInfos.clear();
	}
	
	public void removeP2PCamera(P2PCameraInfo cam){
		p2pCameraInfos.remove(cam);
	}
	
	public void removeP2PCamera(int index){
		p2pCameraInfos.remove(index);
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return p2pCameraInfos.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return p2pCameraInfos.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		if(convertView == null){
			convertView = LayoutInflater.from(context).inflate(
					R.layout.item_list, null);
			viewHolder = new ViewHolder();
			viewHolder.uid = (TextView) convertView
					.findViewById(R.id.textViewDevName);
			viewHolder.ipAddr = (TextView) convertView
					.findViewById(R.id.textViewDevAddress);
			convertView.setTag(viewHolder);
		}else{
			convertView.getTag();
		}
		
		P2PCameraInfo p2pCameraInfo = p2pCameraInfos.get(position);
		if(viewHolder != null){
			viewHolder.uid.setText(p2pCameraInfo.uid);
			viewHolder.ipAddr.setText(p2pCameraInfo.ipAddr);
		}
		
		return convertView;
	}

}
