package com.example.myappactionbar;


import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.UUID;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.media.MediaCodec;
import android.media.MediaExtractor;
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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends ActionBarActivity implements ActionBar.TabListener, communicate {
	   Activity globalContext;
	   boolean reverbEnabler;
	   static boolean isPaused;
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
	   BlueToothSendData mConnectThread;
	   DecodeOperation decodeop;
	   Globals g = Globals.getInstance();
	   ConnectedThread mConnectedThread;
	   int count =0;
	   PlayerStates state = PlayerStates.getInstance();
	   
	 @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        isPaused = false;
        reverbEnabler = true;//true;
        
        
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
      
        mReverb = new PresetReverb(1,0);
        mReverb.setPreset(PresetReverb.PRESET_SMALLROOM);
        mReverb.setEnabled(false);
            
        mEqualizer = new Equalizer(0,0);
        mEqualizer.setEnabled(true);
         
   }
	

	     
	     public void play(String path) {
	 		if (state.get() == PlayerStates.STOPPED || state.get() == PlayerStates.NOT_SET) {
	 			
	 			decodeop = (DecodeOperation) new DecodeOperation();
	 			decodeop.execute( path);
	 			decodeop.stop = false;
	 			//decodeLoop(path);
	 		}
	 		if (state.get() == PlayerStates.READY_TO_PLAY) {
	 			state.set(PlayerStates.PLAYING);
	 			decodeop.syncNotify();
	 		}
	 	}
	     
	  
	     
	      
	     
	 	
	 	
	     
	    
	    
	 
	 
	public void playSong() throws IOException
	{
	   g.setData1(false);
//	   if(decodeop !=null){
//			decodeop.cancel(true);
//	    }
	   path = PlayListActivity.songsList.get(MainActivity.currentSongIndex).get("songPath");
	   play(path);
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
		
		mConnectThread = new BlueToothSendData(BA,BAddress1);
		mConnectThread.start();
		BluetoothSocket mmSocket = null;

	    mmSocket = BlueToothSendData.getMmSocket();
	    if(mmSocket!=null){		 
			mConnectedThread = new ConnectedThread(mmSocket);
	        }
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
    	Drawable newIcon = null;
    	 
    	
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
              	        	 
       	 newIcon = (Drawable)item.getIcon();
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
        	if(!BA.isEnabled()){
        		newIcon.mutate().setColorFilter(Color.rgb(255, 255, 255), PorterDuff.Mode.SRC_IN);
        	}
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
    	
    		
    }
    
    @Override
    protected void onDestroy(){
    	super.onDestroy();
    	BA.disable();
    	if(mConnectedThread != null){mConnectedThread.cancel();}   
    	if(audioTrack != null){
    	audioTrack.release();}
    	
    }

	@Override
	public void playM() {
		   
	
		if(isPaused || !state.isPlaying()){
			
	 try {  
			playSong();
			g.setData1(false);
			
			if(isPaused)
				state.set(PlayerStates.PLAYING);
			isPaused = false;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} }
		
		
	
       TextView songName =  (TextView)findViewById(R.id.textView1);
		songName.setText(PlayListActivity.songsList.get(currentSongIndex).get("songTitle"));
//		
//		
		
}

	@Override
	public void pauseM() {
//		if(mPlayer.isPlaying()) {
//		mPlayer.pause();
		if (state.isPlaying()){
			//audioTrack.pause();
		isPaused = true;
		decodeop.pause();
		//
		}
	}

	@Override
	public void stopM() {
		 //decodeop.cancel(true);  
		g.setData1(true);
	    isPaused = false;
	    decodeop.stop();
	}

	@Override
	public void previousM() {
		// TODO Auto-generated method stub
	stopM();	
		
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
		stopM();
	//if(audioTrack == null )	{
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
			    //g.setBDataString(s);
			    sendData(message);
		
	}

	 @Override
		public void sliderM(int r, int t) {
			// TODO Auto-generated method stub
		   String message = "position "+String.valueOf(t)+"%"+ String.valueOf(r);
		   sendData(message);
			    
		}
	 
	 public void sendData(String s) {
		
		 
		 if(mConnectedThread != null){        
		    if(count > 5){
			mConnectedThread.write(s);
		    count = 0;
		    }
			count++;
		}
		 //g.setBDataString(s);
	 }



	@Override
	public void overAllVolume(float mdist) {
		// TODO Auto-generated method stub
		   String message = "volume "+String.valueOf((int)(mdist*255));
		   sendData(message);

		
	}


}