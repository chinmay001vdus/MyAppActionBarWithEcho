package com.example.myappactionbar;


import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.UUID;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.media.MediaCodec;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.audiofx.EnvironmentalReverb;
import android.media.audiofx.Equalizer;
import android.media.audiofx.PresetReverb;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends ActionBarActivity implements ActionBar.TabListener, communicate, OnCompletionListener {
	   Activity globalContext;
	   boolean reverbEnabler;
	   boolean isPaused;
	   public static Equalizer mEqualizer;
	   static MediaPlayer mPlayer;
	   protected String BAddress1 = "98:D3:31:B2:EE:0C";
	   protected String BAddress2= "00:06:66:52:71:B6";
	   private OutputStream outStream = null;
	   private static final UUID MY_UUID1 = UUID.fromString("a60f35f0-b93a-11de-8a39-08002009c666");
	   private static final UUID MY_UUID2 = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
	   private BluetoothSocket btSocket1 = null;
	   private BluetoothSocket btSocket2 = null;
	   private AudioManager amanager = null;
	   int connInd = 0;
	   private BluetoothAdapter BA;
	   Fragment frag; 
	   FragmentTab1 simpleListFragment;FragmentTab2 androidlidt;FragmentTab3 androidversionlist; FragmentTab4 azimuthControl;
	   private android.support.v7.app.ActionBar actionBar;
	   private static final String STATE_SELECTED_NAVIGATION_ITEM = "selected_navigation_item";
	   static int currentSongIndex = 0;
	   public String path;
	   PlayListActivity pl;
	   protected MediaExtractor extractor;
	   protected MediaCodec codec;
	   final long TIMEOUT_US = 5000;
	   boolean sawInputEOS;
	   boolean sawOutputEOS;
	   protected int inputBufIndex;
	   protected Boolean doStop = false;
	   public static AudioTrack audioTrack; 
	   public static PresetReverb mReverb;
	   public static EnvironmentalReverb eReverb;
	   String toastText;
	   int threadCount = 0;
	   public byte[] chunkPlay;
	   
	   DecodeOperation decodeop;
	   Globals g = Globals.getInstance();
	   
	@SuppressLint("NewApi") @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        isPaused = false;
        reverbEnabler = true;//true;
        
//        try {
//   		 playSong();
//   			} catch (IOException e) {
//   		// TODO Auto-generated catch block
//   		e.printStackTrace();
//   	    }
        
        BA = BluetoothAdapter.getDefaultAdapter();
               
        if (BA.isEnabled()) {
          String address = BA.getAddress();
          String name = BA.getName();
          toastText = name + " : " + address;
        }
        else {
        toastText = "Bluetooth is not enabled";
        Toast.makeText(this, toastText, Toast.LENGTH_LONG).show();
        }
        
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.drawable.logo);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        
        simpleListFragment = new FragmentTab1();
        androidlidt = new FragmentTab2();
        androidversionlist = new FragmentTab3();
        azimuthControl = new FragmentTab4();
        
        actionBar = getSupportActionBar();
        actionBar.setStackedBackgroundDrawable(new ColorDrawable(Color.parseColor("#505050")));
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        
        actionBar.addTab(actionBar.newTab().setText("One").setTabListener(this));
        actionBar.addTab(actionBar.newTab().setText("Two").setTabListener(this));
        actionBar.addTab(actionBar.newTab().setText("Three").setTabListener(this));
        actionBar.addTab(actionBar.newTab().setText("Four").setTabListener(this));
        
        SongsManager plm = new SongsManager();
        PlayListActivity.songsList = plm.getPlayList();
                
        mPlayer = new MediaPlayer();
        mPlayer.setOnCompletionListener(this);
        
       mReverb = new PresetReverb(1,0);
        
        //PresetReverb mReverb = new PresetReverb(0,mPlayer.getAudioSessionId());
        mReverb.setPreset(PresetReverb.PRESET_LARGEHALL);
        mReverb.setEnabled(true);
        ///mPlayer.attachAuxEffect(mReverb.getId());
        
        eReverb = new EnvironmentalReverb(0,0);
        
        
         mEqualizer = new Equalizer(0,0);
         mEqualizer.setEnabled(true);
        
//        Thread thread = new Thread(new Runnable() {
//        	public void run() {
//        		
//        		finish();
//        	}
//        });
//        thread.start();
         
         
   }
	
	private byte[] decodeLoop(String path){
	     ByteBuffer[] codecInputBuffers;
	     ByteBuffer[] codecOutputBuffers;
	     byte[] chunkT = null;
	     // extractor gets information about the stream
	     extractor = new MediaExtractor();
	     
	     
	     try {
	          extractor.setDataSource(path);
	     } catch (Exception e) {
	          Log.e("tag", "source not uploaded");
	     }
	 
	     MediaFormat format = extractor.getTrackFormat(0);
	     String mime = format.getString(MediaFormat.KEY_MIME);
	 
	     // the actual decoder
	     
			try {
				codec = MediaCodec.createDecoderByType(mime);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				Log.e("tag", "*****decoder nit created by mime");
				e.printStackTrace();
			}
		
	     codec.configure(format, null /* surface */, null /* crypto */, 0 /* flags */);
	     codec.start();
	     codecInputBuffers = codec.getInputBuffers();
	     codecOutputBuffers = codec.getOutputBuffers();
	 
	     // get the sample rate to configure AudioTrack
	     int sampleRate = format.getInteger(MediaFormat.KEY_SAMPLE_RATE);
	 
	     // create our AudioTrack instance
	     audioTrack = new AudioTrack(
	          AudioManager.STREAM_MUSIC, 
		  sampleRate, 
		  AudioFormat.CHANNEL_OUT_STEREO, 
		  AudioFormat.ENCODING_PCM_16BIT, 
		  AudioTrack.getMinBufferSize (
		       sampleRate, 
		       AudioFormat.CHANNEL_OUT_STEREO, 
		       AudioFormat.ENCODING_PCM_16BIT
		  ), 
		  AudioTrack.MODE_STREAM
	     );
	     
	     // start playing, we will feed you later
	     //audioTrack.play();
	     extractor.selectTrack(0);
	 
	     // start decoding
	     final long kTimeOutUs = 10000;
	     MediaCodec.BufferInfo info = new MediaCodec.BufferInfo();
	     boolean sawInputEOS = false;
	     boolean sawOutputEOS = false;
	     int noOutputCounter = 0;
	     int noOutputCounterLimit = 100;
	     int samplenum =0;
	     
	   while (!sawOutputEOS && noOutputCounter < noOutputCounterLimit && !doStop) {
   	    Log.i("LOG_TAG", "loop ");  
   	 
   	 
	    	 if (!sawInputEOS) {  
	    		 inputBufIndex = codec.dequeueInputBuffer(kTimeOutUs);
	    		 Log.d("LOG_TAG", " bufIndexCheck " );
	     
	    		 if (inputBufIndex >= 0) {
	                     ByteBuffer dstBuf = codecInputBuffers[inputBufIndex];
	
	                      int sampleSize = extractor.readSampleData(dstBuf, 0 /* offset */);
	                      
	                     long presentationTimeUs = 0;
	                      
	             // can throw illegal state exception (???) 
	         
	         if (sampleSize < 0  ) { Log.d("LOG_TAG", "saw input EOS.");  
	         sawInputEOS = true;
	         sampleSize = 0;
	         }else{
	        	 presentationTimeUs = extractor.getSampleTime();
	         }
	         
	         
	         codec.queueInputBuffer(inputBufIndex,0 /* offset */,sampleSize,presentationTimeUs,sawInputEOS ? MediaCodec.BUFFER_FLAG_END_OF_STREAM : 0);
	         
	         if (!sawInputEOS) { 
	        	 extractor.advance();
	        	 }
	         }
	         else { 
	        	 Log.e("LOG_TAG", "inputBufIndex " +inputBufIndex); 
	        	 }
	    	 }
	    	 
	    	 
	       int res = codec.dequeueOutputBuffer(info, kTimeOutUs);
	       
	       noOutputCounter++;
	       
	       if (res >= 0) {		         
	
	         Log.d("LOG_TAG", "got frame, size " + info.size + "/" + info.presentationTimeUs);
	         
	     if (info.size > 0) {
	         noOutputCounter = 0;
	     }
	
	     int outputBufIndex = res;
	     ByteBuffer buf = codecOutputBuffers[outputBufIndex];
	
	     final byte[] chunk = new byte[info.size];
	     buf.get(chunk);
	     buf.clear();
	     if(chunk.length > 0){
	    	 chunkT = chunk;
	    	 //audioTrack.write(chunk,0,chunk.length);
	     }
	     codec.releaseOutputBuffer(outputBufIndex, false /* render */);
	     
	     if ((info.flags & MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0) {
	         Log.d("LOG_TAG", "saw output EOS.");
	         sawOutputEOS = true;		     }
	     } else if (res == MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED) {
	     codecOutputBuffers = codec.getOutputBuffers();
	     Log.d("LOG_TAG", "output buffers have changed.");
	 } else if (res == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
	     MediaFormat oformat = codec.getOutputFormat();
	     //audioTrack.setPlaybackRate(oformat.getInteger(MediaFormat.KEY_SAMPLE_RATE));
	     Log.d("LOG_TAG", "output format has changed to " + oformat);
	 } else {
	     Log.d("LOG_TAG", "dequeueOutputBuffer returned " + res);
	 }
}
	Log.d("LOG_TAG", "stopping...");
	
	      relaxResources(true);
	
	doStop = true;
	return chunkT;
	
	}

void relaxResources(Boolean release)
{
if(codec != null){
     if(release){
	codec.stop();
	codec.release();
	codec = null;
 }	    
}
if(MainActivity.audioTrack != null){
	 MainActivity.audioTrack.flush();
	 MainActivity.audioTrack.release();
	 MainActivity.audioTrack = null;	
}
}
	
	
	public void playSong() throws IOException
	{
	   g.setData1(false);
//	   if(decodeop !=null){
//			decodeop.cancel(true);
//	    }
	   path = PlayListActivity.songsList.get(MainActivity.currentSongIndex).get("songPath");
	   decodeop = (DecodeOperation) new DecodeOperation().execute( path);
//	     threadCount++;
	     
	     
	}
	
	
	public void on(View view){
	         if (!BA.isEnabled()) {
        BA.enable();
	    //String enableBT = BluetoothAdapter.ACTION_REQUEST_ENABLE;
	    //Intent turnOnIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        //startActivityForResult(new Intent(enableBT), 0);
        Toast.makeText(globalContext.getApplicationContext(),"Bluetooth turned on",Toast.LENGTH_LONG).show();
     }else{
       Toast.makeText(globalContext.getApplicationContext(),"Bluetooth is already on",Toast.LENGTH_LONG).show();
     }
	}
       
	@Override
	public void connectBlueData() {
		
	BluetoothDevice device = BA.getRemoteDevice(BAddress1);
		
		try {
		      btSocket1 = device.createRfcommSocketToServiceRecord(MY_UUID2);
		    } catch (IOException e) {
		    	Toast.makeText(globalContext.getApplicationContext(),"failed to create socket ",Toast.LENGTH_LONG).show();
		    }

		    BA.cancelDiscovery();

		    try {
		      btSocket1.connect();
		    } catch (IOException e) {
		      try {
		        btSocket1.close();
		      } catch (IOException e2) {
		    	  Toast.makeText(globalContext.getApplicationContext(),"unable to close socket ",Toast.LENGTH_LONG).show();
		      }
		    }
		
		  Toast.makeText(globalContext.getApplicationContext(),"got into function ",Toast.LENGTH_LONG).show();  
		  
		  if(btSocket1.isConnected()){
		    	connInd = 1;
		    }else{connInd = 0;}
  
	} 
	
	
	@Override
	public void connectBlueAudio() {
		// TODO Auto-generated method stub
			
//	BluetoothDevice device = BA.getRemoteDevice(BAddress2);
//		
//		try {
//		      btSocket1 = device.createRfcommSocketToServiceRecord(MY_UUID2);
//		      
//		      
//		    } catch (IOException e) {
//		    	Toast.makeText(globalContext.getApplicationContext(),"failed to create socket ",Toast.LENGTH_LONG).show();
//		    }
//		
//		    BA.cancelDiscovery();
//
//		    try {
//		      btSocket1.connect();
//		      
//		      
//		    } catch (IOException e) {
//		      try {
//		        btSocket1.close();
//		      } catch (IOException e2) {
//		    	  Toast.makeText(globalContext.getApplicationContext(),"unable to close socket ",Toast.LENGTH_LONG).show();
//		      }
//		    }
//		    
//		    if(btSocket1.isConnected()){
//		    	connInd = 1;
//		    }else{connInd = 0;}
		    		        
	}
	
	  @Override
	    public void onRestoreInstanceState(Bundle savedInstanceState) {
	        if (savedInstanceState.containsKey(STATE_SELECTED_NAVIGATION_ITEM)) {
	            getActionBar().setSelectedNavigationItem(savedInstanceState.getInt(STATE_SELECTED_NAVIGATION_ITEM));
	        }
	    }
	 
	    @Override
	    public void onSaveInstanceState(Bundle outState) {
	        //outState.putInt(STATE_SELECTED_NAVIGATION_ITEM, getActionBar().getSelectedNavigationIndex());
	    }
	 
	   
	    @Override
	    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
	    }
	 
	    @Override
   public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
	         
	     
	     if (tab.getPosition() == 0) {
	      
	      getSupportFragmentManager().beginTransaction().replace(R.id.container, simpleListFragment).commit();
	     } 
	     else if (tab.getPosition() == 1) {
	    	 
	    	 getSupportFragmentManager().beginTransaction().replace(R.id.container, androidlidt).commit();
	  }
	            
	     else if(tab.getPosition() == 2){
	       	    	 
	    	 getSupportFragmentManager().beginTransaction().replace(R.id.container, androidversionlist).commit();
	     } else {
	    	 getSupportFragmentManager().beginTransaction().replace(R.id.container, azimuthControl).commit();
	     }
	    }
	 
	    @Override
public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
	    }
	    
	    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
    	MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_main_actions, menu);
        
        
        
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	
    	 
    	
    	// Take appropriate action for each action item click
        switch (item.getItemId()) {
        case R.id.action_search:
            // search action
            return true;
        case R.id.action_refresh:
        	
            return true;
        case R.id.action_help:
            // help action
            return true;
        case R.id.action_settings:
            // help action
            return true;
        case R.id.action_bluetooth:
              	        	 
       	 Drawable newIcon = (Drawable)item.getIcon();
       	if(!BA.isEnabled()) {
       	    on(frag.getView());
       	    
       	 newIcon.mutate().setColorFilter(Color.rgb(0, 100, 200), PorterDuff.Mode.SRC_IN);
       	    }else{
            off(frag.getView());
            newIcon.mutate().setColorFilter(Color.rgb(255, 255, 255), PorterDuff.Mode.SRC_IN);
         	}
       	item.setIcon(newIcon);
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }

    public Fragment getVisibleFragment(){
        FragmentManager fragmentManager = MainActivity.this.getSupportFragmentManager();
        List<Fragment> fragments = fragmentManager.getFragments();
        for(Fragment fragment : fragments){
            if(fragment != null && fragment.getUserVisibleHint())
                return (Fragment)fragment;
        }
        return null;
    }
     
    public void off(View view){
  	    BA.disable();
        Toast.makeText(globalContext.getApplicationContext(),"Bluetooth turned off",Toast.LENGTH_LONG).show();
       }
    
    @Override
    public void onStart(){
    	super.onStart();
    	
    	
//    findViewById(R.id.button1).setOnClickListener(new OnClickListener() {                       
//            @Override
//            public void onClick(View v) {
//          	  Toast.makeText(globalContext, 
//          	          "PLay", Toast.LENGTH_LONG).show();
//          	
//            }
//    });
    }
    
    @Override
    public void onResume() {
    	
    	super.onResume();
    	
    	frag = getVisibleFragment();
   	    globalContext = frag.getActivity();
    }
    
    @Override
    public void onPause() {
    	
    	super.onPause();
    }
    
    @Override
   public void onStop() {
    	super.onStop();
    	//mPlayer.stop();
    	BA.disable();
    	
    }
    
    @Override
    protected void onDestroy(){
    	super.onDestroy();
    	audioTrack.release();
    	mPlayer.release();
    	//amanager.stopBluetoothSco();
    	///amanager.setBluetoothScoOn(false);
        mPlayer = null;
    }

	@Override
	public void playM() {
		 //path = PlayListActivity.songsList.get(MainActivity.currentSongIndex).get("songPath");
//       Thread thread = new Thread(new Runnable() {
//     	public void run() {
//     			
//     		finish();
//     	}
//     });
//     thread.start();
		
    
	
		
	 try {
			playSong();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	// audioTrack.flush();
	
//	if(audioTrack != null){
//		if(audioTrack.getPlayState() != AudioTrack.PLAYSTATE_PLAYING ) {
//	
//					audioTrack.play();
//					isPaused = false;
//	
//		}
//	}
		
//        try {
//        	mPlayer.reset();
//            path = PlayListActivity.songsList.get(currentSongIndex).get("songPath");
//			mPlayer.setDataSource(path);
//		} catch (IllegalArgumentException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (SecurityException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IllegalStateException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//       TextView songName =  (TextView)findViewById(R.id.textView1);
//		songName.setText(PlayListActivity.songsList.get(currentSongIndex).get("songTitle"));
//		
//		if(mPlayer!=null){
//		if(!mPlayer.isPlaying()){
//		if(!isPaused){
//	    mPlayer.prepareAsync();
//	    mPlayer.setOnPreparedListener(new OnPreparedListener() {
//	    @Override
//	    	public void onPrepared(MediaPlayer mp) {
//	    	    
//	            mp.start();
//	            
//	    }
//	        
//	    });	 
//		}else{
//	    try {
//			mPlayer.prepare();
//		} catch (IllegalStateException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		 
//		mPlayer.start();
//		isPaused = false;
//		
//		}
//		}	
//		}
		
}

	@Override
	public void pauseM() {
//		if(mPlayer.isPlaying()) {
//		mPlayer.pause();
//		if (audioTrack.getPlayState() == AudioTrack.PLAYSTATE_PLAYING){
			audioTrack.pause();
		isPaused = true;
		//
//		}
	}

	@Override
	public void stopM() {
		   
		g.setData1(true);
	    audioTrack.stop();
			//decodeop.cancel(true);

	}

	@Override
	public void previousM() {
		// TODO Auto-generated method stub
		
		
		if(currentSongIndex > 0){
			currentSongIndex = currentSongIndex-1;
		}else{
			currentSongIndex = PlayListActivity.songsList.size() - 1;
		}
				
      playM();
		}

	@Override
	public void nextM() {
		// TODO Auto-generated method stub
		
       if(currentSongIndex < (PlayListActivity.songsList.size() - 1)){
      
      currentSongIndex = currentSongIndex+1;
		}else{
			currentSongIndex = 0;	
		}
		
	      playM();
		
	}

	
	 @Override
	    public void onActivityResult(int requestCode,
	                                       int resultCode, Intent data) {
	          super.onActivityResult(requestCode, resultCode, data);
	          if(resultCode == 100){
	              currentSongIndex = data.getExtras().getInt("songIndex");
	              g.setData1(true);
	              path = PlayListActivity.songsList.get(MainActivity.currentSongIndex).get("songPath");
	              decodeop = (DecodeOperation) new DecodeOperation().execute(path);
	               //Toast.makeText(getApplicationContext(),"itemSelected"+currentSongIndex, Toast.LENGTH_SHORT).show();
	       		playM();
	          }
	      }

	

	public void getAudioFile(){
        
        Uri myUri = Uri.parse("file:///sdcard/Music/afterthefall.mp3");
        
       
        //mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
       
        try {
			//mPlayer.setDataSource(songPath);
			mPlayer.setDataSource(getApplicationContext(),myUri);
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

        
	}

	
	
	


	@Override
	public void sendSliderData(int sliderv,String sliderno) {
		// TODO Auto-generated method stub
		    String message = "dimmer"+sliderno+" "+ String.valueOf(sliderv);
		    sendData(message);
		
	}



	@Override
	public void circleSliderData(int a) {
		// TODO Auto-generated method stub
			    String message = "rotator "+String.valueOf(a);
			    sendData(message);
		
	}

	 @Override
		public void sliderM(int r, int t) {
			// TODO Auto-generated method stub
		   String message = "position "+String.valueOf(t)+"%"+ String.valueOf(r);
		   sendData(message);
			    
		}
	 
	 public void sendData(String s) {
		 while(connInd==1){
				try {
				      outStream = btSocket1.getOutputStream();
				    } catch (IOException e) {
				    	
				      Log.e("BlDataSend","failed");
				    }

				    
				    byte[] msgBuffer = s.getBytes();

				    try {
				          outStream.write(msgBuffer);
				          Toast.makeText(getApplicationContext(), "Done! Message is successfully transferred!", Toast.LENGTH_SHORT).show();
				    } catch (IOException e) {
				      String msg = "Please ensure the Server is up and listening for incoming connection\n\n";
				      //AlertBox("Server Error", msg);       
				    }
				}
		 
	 }



	@Override
	public void onCompletion(MediaPlayer mp) {
		// TODO Auto-generated method stub
		//Toast.makeText(getApplicationContext(),"songFinished", Toast.LENGTH_SHORT).show();
		
		mp.reset();
		
		if(currentSongIndex < (PlayListActivity.songsList.size() - 1)){
      
      currentSongIndex = currentSongIndex+1;
		}else{
			currentSongIndex = 0;	
		}
		
				
      playM();
	}


}