package com.elf_vrdrone.view;

import java.util.ArrayList;
import java.util.List;

import com.ElecFreaks.ELF_vrdrone.R;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class BLEDeviceListAdapter extends BaseAdapter{

	class ViewHolder {
		TextView tv_devName, tv_devAddress;
	}

	private List<BluetoothDevice> mBleArray;
	private ViewHolder viewHolder;
	Context context;

	public BLEDeviceListAdapter(Context context) {
		mBleArray = new ArrayList<BluetoothDevice>();
		this.context = context;
	}

	public void addDevice(BluetoothDevice device) {
		if (!mBleArray.contains(device)) {
			mBleArray.add(device);
		}
	}
	
	public void clearDevice(){
		mBleArray.clear();
	}
	
	public void removeDevice(BluetoothDevice dev){
		mBleArray.remove(dev);
	}

	@Override
	public int getCount() {
		return mBleArray.size();
	}

	@Override
	public BluetoothDevice getItem(int position) {
		return mBleArray.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(
					R.layout.item_list, null);
			viewHolder = new ViewHolder();
			viewHolder.tv_devName = (TextView) convertView
					.findViewById(R.id.textViewDevName);
			viewHolder.tv_devAddress = (TextView) convertView
					.findViewById(R.id.textViewDevAddress);
			convertView.setTag(viewHolder);
		} else {
			convertView.getTag();
		}

		// add-Parameters
		BluetoothDevice device = mBleArray.get(position);
		String devName = device.getName();
		if (devName != null && devName.length() > 0) {
			viewHolder.tv_devName.setText(devName);
		} else {
			viewHolder.tv_devName.setText("unknow-device");
		}
		viewHolder.tv_devAddress.setText(device.getAddress());
		
		return convertView;
	}
}
