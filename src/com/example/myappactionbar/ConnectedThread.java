package com.example.myappactionbar;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.bluetooth.BluetoothSocket;
import android.util.Log;

public class ConnectedThread extends Thread{
	public String s = null;
	Globals g  = Globals.getInstance();
	private final BluetoothSocket mmSocket;
	//private final InputStream mmInStream;
	private final OutputStream mmOutStream;
	
	public ConnectedThread(BluetoothSocket socket) {
	mmSocket = socket;
	InputStream tmpIn = null;
	OutputStream tmpOut = null;
	try {
	//tmpIn = socket.getInputStream();
	tmpOut = socket.getOutputStream();
	} catch (IOException e) { }
	//mmInStream = tmpIn;
	mmOutStream = tmpOut;
	}
	
	
//	
//	public void run(){
//		byte[] msgBuffer= "noop".getBytes();
//		
//		
//        while(true){
//        	s = g.getBDataString();
//    		
//    		if(s!=null){
//    		Log.d("bl_msg",s );
//    		msgBuffer = s.getBytes();
//    		}
//			    try {
//			      mmOutStream.write(msgBuffer);
//			      Log.d("tag_bluetooth", "data sent: "+s);
//			      //Toast.makeText(getApplicationContext(), "Done! Message is successfully transferred!", Toast.LENGTH_SHORT).show();
//			    } catch (IOException e) {
//			      //String msg = "Please ensure the Server is up and listening for incoming connection\n\n";
//			      //AlertBox("Server Error", msg);  
//			    	Log.d("error", "data not sent");
//			    }
//    }
//	}	
	
	public void write(String message ) {
		byte[] msgBuffer = message.getBytes();
		try {
			
		mmOutStream.write(msgBuffer);
		Log.d("sending", message);
		} catch (IOException e) { }
		}

	
	public void cancel() {
		try {
		mmSocket.close();
		} catch (IOException e) { }
		}
}
