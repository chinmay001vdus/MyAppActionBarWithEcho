package com.example.myappactionbar;

import java.io.IOException;
import java.util.UUID;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

public class BlueToothSendData extends Thread {
	boolean flagSocket = false;
	private static BluetoothSocket mmSocket = null;
	private final BluetoothDevice mmDevice;
	public final BluetoothAdapter BA;
	private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
	ConnectedThread mConnectedThread;
	public BlueToothSendData(BluetoothAdapter BAd, String BAddress) {
		BluetoothDevice device =BAd.getRemoteDevice(BAddress); 
		BA = BAd;
		BluetoothSocket tmp = null;
		mmDevice = device;
		
		try {
		tmp = device.createRfcommSocketToServiceRecord(MY_UUID);
		} catch (IOException e) { }
		mmSocket = tmp;
		}
     
	public void run() {
		BA.cancelDiscovery();
	while(!flagSocket && BA.isEnabled()){	
		try {
		getMmSocket().connect();
		
        Log.d("bluettoth", "connected");
        flagSocket = true;
        //mConnectedThread = new ConnectedThread(getMmSocket());
		//mConnectedThread.start();
				} catch (IOException connectException) {
		
					try {
		 getMmSocket().close();
		 Log.d("bluettoth", "connection failed");
		 flagSocket = false;
		 } catch (IOException closeException) { }
					
		
					
		return;
		}
	}
	Log.d("Bconnect", "thread end");	
	}
	
	
	
	public void cancel() {
		try {
		getMmSocket().close();
		} catch (IOException e) { }
	}

	public static BluetoothSocket getMmSocket() {
		return mmSocket;
	}
}
