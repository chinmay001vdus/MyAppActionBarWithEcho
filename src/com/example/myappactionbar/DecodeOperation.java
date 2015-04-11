package com.example.myappactionbar;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.AudioTrack.OnPlaybackPositionUpdateListener;
import android.media.MediaCodec;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.media.audiofx.EnvironmentalReverb;
import android.media.audiofx.Equalizer;
import android.os.AsyncTask;
import android.util.Log;



/**
* AsyncTask that takes care of running the decode/playback loop
*
*/
public class DecodeOperation extends AsyncTask<String, Void, Void> {
	protected MediaExtractor extractor;
	protected MediaCodec codec;
	
	//private PlayerEvents events = null;
	PlayerStates state = PlayerStates.getInstance();
	long presentationTimeUs = 0, duration = 0;
	boolean stop = false;
	
	//EchoFilter filter = new EchoFilter(1000, .6f);
	protected int inputBufIndex;
	//protected Boolean doStop = false;
	
	protected String mypath;
	private byte[][] chunks = new byte[10][];
	protected byte[] chunk;
	int el=0;
	
	Globals g  = Globals.getInstance();

   
     @Override
     protected Void doInBackground(String... values) {	
        System.out.println(values[0]);
        this.mypath = values[0];
       decodeLoop(mypath);
       
          return null;
     }
 
     @Override
     protected void onPreExecute() {
     }
 
     @Override
     protected void onProgressUpdate(Void... values) {
    	 
     }
    
     
     
     
     
//     public void setEventsListener(PlayerEvents events) {
// 		this.events = events;
// 	}
//     
//     public DecodeOperation() {
//    	 
//     }
//     
//     public DecodeOperation(PlayerEvents events){
//    	 setEventsListener(events);
//     }
     
     public synchronized void syncNotify() {
     	notify();
     }
     
	 public boolean isLive() {
	 		return (duration == 0);
	 	}
     
	   public void stop() {
	 		stop = true;
	 	}
	     
	     public void pause() {
	 		state.set(PlayerStates.READY_TO_PLAY);
	 	}
	 
	 
     public void seek(long pos) {
	 		extractor.seekTo(pos, MediaExtractor.SEEK_TO_CLOSEST_SYNC);
	 	}
	 	
	 	public void seek(int percent) {
	 		long pos = percent * duration / 100;
	 		seek(pos);
	 	}
//     
     public synchronized void waitPlay(){
     	// if (duration == 0) return;
         while(state.get() == PlayerStates.READY_TO_PLAY) {
             try {
                 //wait();
            	 Thread.sleep(100);
             } catch (InterruptedException e) {
                 e.printStackTrace();
             }
         }
     } 
     
     
	private void decodeLoop(String path){
		     ByteBuffer[] codecInputBuffers;
		     ByteBuffer[] codecOutputBuffers;
		 
		     // extractor gets information about the stream
		     extractor = new MediaExtractor();
		     
		     //String path = PlayListActivity.songsList.get(MainActivity.currentSongIndex).get("songPath");
		     try {
		          extractor.setDataSource(path);
		     } catch (Exception e) {
		          return;
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
		     MainActivity.audioTrack = new AudioTrack(
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
		     //MainActivity.audioTrack.setStereoVolume(0.1f, 1.0f);
		     MainActivity.audioTrack.attachAuxEffect(MainActivity.mEqualizer.getId());
		     MainActivity.audioTrack.attachAuxEffect(MainActivity.mReverb.getId());
		     MainActivity.audioTrack.setAuxEffectSendLevel(1.0f);
		     // start playing, we will feed you later
		     MainActivity.audioTrack.play();
		     extractor.selectTrack(0);
		 
		     // start decoding
		     final long kTimeOutUs = 10000;
		     MediaCodec.BufferInfo info = new MediaCodec.BufferInfo();
		     boolean sawInputEOS = false;
		     boolean sawOutputEOS = false;
		     int noOutputCounter = 0;
		     int noOutputCounterLimit = 50;
		     int samplenum =0;
		     
		   state.set(PlayerStates.PLAYING);  
		   while (!sawOutputEOS && noOutputCounter < noOutputCounterLimit && !g.getData1() && !stop) {
	    	 //Log.i("LOG_TAG", "loop ");  
			  
			  waitPlay();
			  
			   noOutputCounter++;
		    	 if (!sawInputEOS) {  
		    		 inputBufIndex = codec.dequeueInputBuffer(kTimeOutUs);
		    		 //Log.d("LOG_TAG", " bufIndexCheck " );
		     
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
		
		        // Log.d("LOG_TAG", "got frame, size " + info.size + "/" + info.presentationTimeUs);
		         
		     if (info.size > 0) {
		         noOutputCounter = 0;
		     }
//		    
		     int outputBufIndex = res;
		     
		     el = g.getEchoLevel();
		     for( int y = 9; y > 0; y-- ){
		     
		    	 if(chunks[y-1] != null){
			    	 //chunkold2 = new byte[info.size];
				     chunks[y] = chunks[y-1];
//				     for(int t = 9; t > y; t--){
//				    	 //java.util.Arrays.fill(chunks[t], (byte) 0);
//				    	 chunks[t]=null;
				     //}
				     }
		    	 
		     }
		     chunks[0] = chunk;
//		      
		     ByteBuffer buf = codecOutputBuffers[outputBufIndex];
		    // byte[] filterBuffer = new byte[4*info.size];
		     chunk = new byte[info.size];
		     //System.out.println("index "+outputBufIndex) ;
//		     for(outputBufIndex=0;outputBufIndex<4;){
//		    	 buf.get(filterBuffer, info.size*outputBufIndex, info.size);
//		    	 
//		     }
		          
		     buf.get(chunk);
		     buf.clear();
		     
		     short newSample =0;
		     
		     
		     if(chunk.length > 0 && chunks[el]!= null && chunk.length == chunks[el].length  ){   
		    	 if( el>0){
		    	 chunk = filter(chunk, chunks[el], chunk.length);
		    	 }
		    	MainActivity.audioTrack.write(chunk,0,chunk.length);
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
		 MainActivity.audioTrack.stop();
		      relaxResources(true);
		      state.set(PlayerStates.STOPPED);
		        stop = true;
		
		//g.setData1(true);
		}



	
	public static short getSample(byte[] buffer, int position) {
	    return (short) (((buffer[position + 1] & 0xff) << 8) | (buffer[position] & 0xff));
	  }
	
	public static void setSample(byte[] buffer, int position, short sample) {
	    buffer[position] = (byte) (sample & 0xff);
	    buffer[position + 1] = (byte) ((sample >> 8) & 0xff);
	  }

	public byte[] filter(byte[] samples, byte[] offset, int length) {

	    for (int i = 0; i < length; i+=2) {
	      // update the sample
	      short oldSample = getSample(samples, i);
	      short offSet = getSample(offset,i);
	      
	      short newSample = (short) (oldSample+ (0.4*offSet*el/9));
	      setSample(samples, i, newSample);
	      
	    }
		return samples;
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


}
