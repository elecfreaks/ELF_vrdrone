package com.elf_vrdrone.view;

import com.ElecFreaks.ELF_vrdrone.R;
import com.elf_vrdrone.modal.OSDCommon;
import com.elf_vrdrone.modal.Transmitter;

import android.content.Context;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class PIDSettings {
	EditText[] editTextP;
	EditText[] editTextI;
	EditText[] editTextD;

	EditText RollPitchRate;
	EditText RollPitchRate2;
	EditText YawRate;

	EditText ThrottleMid;
	EditText ThrottleExpo;

	EditText RcRate;
	EditText RcExpo;

	EditText ThrottleRate;
	
	float confRC_RATE = 0, 
			confRC_EXPO = 0, 
			rollPitchRate = 0, 
			yawRate = 0, 
			dynamic_THR_PID = 0, 
			throttle_MID = 0, 
			throttle_EXPO = 0;
	
	float[] P;
	float[] I;
	float[] D;
	
	private final static byte MSP_RC_TUNING = 0x6F;
    private final static byte MSP_PID = 0x70;
    private final static byte MSP_SET_PID = (byte)202;
    private final static byte MSP_EEPROM_WRITE = (byte)250;
    private final static byte MSP_SET_RC_TUNING = (byte)204;
    private final static byte MSP_SET_P = (byte)157;
    private final static byte MSP_SET_I = (byte)158;
    private final static byte MSP_SET_D = (byte)159;
	
	private Context context;
	private View view;
	
	public PIDSettings(Context context, View view) {
		// TODO Auto-generated constructor stub
		this.context = context;
		this.view = view;
		
		editTextP = new EditText[9];
		editTextI = new EditText[9];
		editTextD = new EditText[9];
		
		editTextP[0] = (EditText) view.findViewById(R.id.P1);
		editTextP[1] = (EditText) view.findViewById(R.id.P2);
		editTextP[2] = (EditText) view.findViewById(R.id.P3);
		editTextP[3] = (EditText) view.findViewById(R.id.P4);
		editTextP[4] = (EditText) view.findViewById(R.id.P5);
		editTextP[5] = (EditText) view.findViewById(R.id.P6);
		editTextP[6] = (EditText) view.findViewById(R.id.P7);
		editTextP[7] = (EditText) view.findViewById(R.id.P8);
		editTextP[8] = (EditText) view.findViewById(R.id.P9);

		editTextD[0] = (EditText) view.findViewById(R.id.D1);
		editTextD[1] = (EditText) view.findViewById(R.id.D2);
		editTextD[2] = (EditText) view.findViewById(R.id.D3);
		editTextD[3] = (EditText) view.findViewById(R.id.D4);
		editTextD[4] = (EditText) view.findViewById(R.id.D5);
		editTextD[5] = (EditText) view.findViewById(R.id.D6);
		editTextD[6] = (EditText) view.findViewById(R.id.D7);
		editTextD[7] = (EditText) view.findViewById(R.id.D8);
		editTextD[8] = (EditText) view.findViewById(R.id.D9);

		editTextI[0] = (EditText) view.findViewById(R.id.I1);
		editTextI[1] = (EditText) view.findViewById(R.id.I2);
		editTextI[2] = (EditText) view.findViewById(R.id.I3);
		editTextI[3] = (EditText) view.findViewById(R.id.I4);
		editTextI[4] = (EditText) view.findViewById(R.id.I5);
		editTextI[5] = (EditText) view.findViewById(R.id.I6);
		editTextI[6] = (EditText) view.findViewById(R.id.I7);
		editTextI[7] = (EditText) view.findViewById(R.id.I8);
		editTextI[8] = (EditText) view.findViewById(R.id.I9);
		
		P = new float[10];
		I = new float[10];
		D = new float[10];
		
		RollPitchRate = (EditText) view.findViewById(R.id.editTextRatePitchRoll1);
		RollPitchRate2 = (EditText) view.findViewById(R.id.editTextRatePitchRoll2);
		YawRate = (EditText) view.findViewById(R.id.editTextRateYaw);

		ThrottleMid = (EditText) view.findViewById(R.id.editTextMIDThrottle);
		ThrottleExpo = (EditText) view.findViewById(R.id.editTextEXPOThrottle);

		RcRate = (EditText) view.findViewById(R.id.editTextRate2PitchRoll);
		RcExpo = (EditText) view.findViewById(R.id.editTextEXPOPitchRoll);

		ThrottleRate = (EditText) view.findViewById(R.id.editTextTPA);
	}
	
	public void updateData(){
		confRC_RATE = Float.parseFloat(RcRate.getText().toString().equals("")?"0.0":RcRate.getText().toString());
		
		confRC_EXPO = Float.parseFloat(RcExpo.getText().toString().equals("")?"0.0":RcExpo.getText().toString());
		rollPitchRate = Float.parseFloat(RollPitchRate.getText().toString().equals("")?"0.0":RollPitchRate.getText().toString());
		yawRate = Float.parseFloat(YawRate.getText().toString().equals("")?"0.0":YawRate.getText().toString());
		dynamic_THR_PID = Float.parseFloat(ThrottleRate.getText().toString().equals("")?"0.0":ThrottleRate.getText().toString());
		throttle_MID = Float.parseFloat(ThrottleMid.getText().toString().equals("")?"0.0":ThrottleMid.getText().toString());
		throttle_EXPO = Float.parseFloat(ThrottleExpo.getText().toString().equals("")?"0.0":ThrottleExpo.getText().toString());

		int i=0;
		for(i=0; i<9; i++){
			P[i] = Float.parseFloat(editTextP[i].getText().toString().equals("")?"0.0":editTextP[i].getText().toString());
			I[i] = Float.parseFloat(editTextI[i].getText().toString().equals("")?"0.0":editTextI[i].getText().toString());
			D[i] = Float.parseFloat(editTextD[i].getText().toString().equals("")?"0.0":editTextD[i].getText().toString());
		}
	}
	
	public byte[] getExtraData(){
		byte[] data = new byte[7];
		data[0] = (byte)(confRC_RATE*100);
		data[1] = (byte)(confRC_EXPO*100);
		data[2] = (byte)(rollPitchRate*100);
		data[3] = (byte)(yawRate*100);
		data[4] = (byte)(dynamic_THR_PID*100);
		data[5] = (byte)(throttle_MID*100);
		data[6] = (byte)(throttle_EXPO*100);
		
		return data;
	}
	
	public int processReceivedData(byte[] bytes){
		int i;
		byte[] dataP = new byte[10];
		byte[] dataI = new byte[10];
		byte[] dataD = new byte[10];
		byte[] dstData = null;
    	switch(bytes[4]){
    	// get
    	case MSP_PID:
    		//System.out.println("MSP_PID finished! start MSP_RC_TUNING");
    		for(i=0; i<30; i+=3){
    			dataP[i/3] = bytes[i+5];
    			dataI[i/3] = bytes[i+6];
    			dataD[i/3] = bytes[i+7];
    		}
    		for(i=0; i<9; i++){
    			if(i != 4)
    				editTextP[i].setText(String.format("%.1f", (float) (dataP[i]&0xff) / 10.0));
    			else
    				editTextP[i].setText(String.format("%.2f", (float) (dataP[i]&0xff) / 100.0));
    		}
    		for(i=0; i<9; i++){
    			if(i >= 4 && i <= 6){
    				if(i == 4)
    					editTextI[i].setText(String.format("%.1f", (float) (dataI[i]&0xff) / 100.0));
    				else
    					editTextI[i].setText(String.format("%.2f", (float) (dataI[i]&0xff) / 100.0));
    			}
    			else
    				editTextI[i].setText(String.format("%.3f", (float) (dataI[i]&0xff) / 1000.0));
    		}
    		for(i=0; i<9; i++){
    			if(i >= 5 && i <= 6)
    				editTextD[i].setText(String.format("%.3f", (float) (dataD[i]&0xff) / 1000.0));
    			else if(i == 8)
    				editTextD[i].setText(String.format("%.3f", (float) (dataD[i]&0xff)));
    			else
    				editTextD[i].setText(String.format("%.0f", (float) (dataD[i]&0xff)));
    		}
    		Transmitter.getSharedTransmitter().transmmitSimpleCommand(OSDCommon.MSPCommnand.MSP_RC_TUNING);
    		break;
    	case MSP_RC_TUNING:
    		Toast.makeText(context, "load ok", Toast.LENGTH_SHORT).show();
    		//System.out.println("MSP_RC_TUNING finished!");
    		RollPitchRate.setText(String.format("%.2f", (float) bytes[7] / 100.0));
    		YawRate.setText(String.format("%.2f", (float) bytes[8] / 100.0));
    		ThrottleMid.setText(String.format("%.2f", (float) bytes[10] / 100.0));
    		ThrottleExpo.setText(String.format("%.2f", (float) bytes[11] / 100.0));
    		RcRate.setText(String.format("%.2f", (float) bytes[5] / 100.0));
    		RcExpo.setText(String.format("%.2f", (float) bytes[6] / 100.0));
    		ThrottleRate.setText(String.format("%.2f", (float) bytes[9] / 100.0));
    		break;
    	// set
    	case MSP_SET_P:
    		//System.out.println("MSP_SET_P finished! start MSP_SET_I");
    		for(i=0; i<10; i++)
				dataI[i] = (byte)Math.round(I[i] * 1000);
			
			dstData = new byte[16];
			dstData[0] = '$';dstData[1] = 'M';dstData[2] = '<';
			dstData[3] = 10;			// data length
			dstData[15] ^= dstData[3];	// checksum
			dstData[4] = MSP_SET_I;
			dstData[15] ^= dstData[4];
			
			dataI[4] = (byte) (Math.round(I[4] * 100.0));
			dataI[5] = (byte) (Math.round(I[5] * 100.0));
			dataI[6] = (byte) (Math.round(I[6] * 100.0));
			
			for(i=0; i<10; i++){
				dstData[5+i] = dataI[i];
				dstData[15] ^= dataI[i];
			}
			Transmitter.getSharedTransmitter().transmmitData(dstData);
    		break;
    	case MSP_SET_I:
    		//System.out.println("MSP_SET_I finished! start MSP_SET_D");
    		for(i=0; i<10; i++)
				dataD[i] = (byte)D[i];
			
			dstData = new byte[16];
			dstData[0] = '$';dstData[1] = 'M';dstData[2] = '<';
			dstData[3] = 10;			// data length
			dstData[15] ^= dstData[3];	// checksum
			dstData[4] = MSP_SET_D;
			dstData[15] ^= dstData[4];
			
			dataD[5] = (byte) ((Math.round(D[5] * 10000.0)) / 10);
			dataD[6] = (byte) ((Math.round(D[6] * 10000.0)) / 10);
			
			for(i=0; i<10; i++){
				dstData[5+i] = dataD[i];
				dstData[15] ^= dataD[i];
			}
			Transmitter.getSharedTransmitter().transmmitData(dstData);
    		break;
    	case MSP_SET_D:
    		//System.out.println("MSP_SET_D finished! start MSP_EEPROM_WRITE");
    		Transmitter.getSharedTransmitter().transmmitSimpleCommand(OSDCommon.MSPCommnand.MSP_EEPROM_WRITE);
    		break;
    	case MSP_SET_RC_TUNING:
    		//System.out.println("MSP_SET_RC_TUNING finished! start MSP_SET_P");
			for(i=0; i<10; i++)
				dataP[i] = (byte)Math.round(P[i] * 10);
			
			dstData = new byte[16];
			dstData[0] = '$';dstData[1] = 'M';dstData[2] = '<';
			dstData[3] = 10;			// data length
			dstData[15] ^= dstData[3];	// checksum
			dstData[4] = MSP_SET_P;
			dstData[15] ^= dstData[4];
			
			dataP[4] = (byte) (Math.round(P[4] * 100.0));
			dataP[5] = (byte) (Math.round(P[5] * 10.0));
			dataP[6] = (byte) (Math.round(P[6] * 10.0));
			
			for(i=0; i<10; i++){
				dstData[5+i] = dataP[i];
				dstData[15] ^= dataP[i];
			}
			Transmitter.getSharedTransmitter().transmmitData(dstData);
    		break;
    	case MSP_EEPROM_WRITE:
    		//System.out.println("MSP_EEPROM_WRITE finished!");
    		if(bytes[2] == '>')
    			Toast.makeText(context, "set succeed", Toast.LENGTH_SHORT).show();
    		else
    			Toast.makeText(context, "set failed", Toast.LENGTH_SHORT).show();
    		break;
    	}
    	
    	return 0;
    }
	
}
