package com.example.myappactionbar;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
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
class DecodeOperation extends AsyncTask<String, Void, Void> {
	protected MediaExtractor extractor;
	protected MediaCodec codec;
	EchoFilter filter = new EchoFilter(1000, .6f);
	protected int inputBufIndex;
	//protected Boolean doStop = false;
	
	protected String mypath;
	
	protected byte[] chunk;
	
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
		     MainActivity.audioTrack.setStereoVolume(0.1f, 1.0f);
		     MainActivity.audioTrack.attachAuxEffect(MainActivity.mEqualizer.getId());
		     //MainActivity.audioTrack.attachAuxEffect(MainActivity.mReverb.getId());
		     //MainActivity.eReverb = new EnvironmentalReverb(0,MainActivity.audioTrack.getAudioSessionId());
		     
		     MainActivity.audioTrack.attachAuxEffect(MainActivity.eReverb.getId());

		     
		        
		     MainActivity.eReverb.setDecayTime(20000);
		     MainActivity.eReverb.setReflectionsDelay(300);
		     //MainActivity.eReverb.setDensity((short) 500);
		     //MainActivity.eReverb.setReverbDelay(100);
		     //MainActivity.eReverb.setDiffusion((short) 500);
		     MainActivity.eReverb.setReverbLevel((short)-9000);
		     MainActivity.eReverb.setEnabled(true);
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
		     int noOutputCounterLimit = 100;
		     int samplenum =0;
		     
		   while (!sawOutputEOS && noOutputCounter < noOutputCounterLimit && !g.getData1()) {
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
		     byte[] chunkold = new byte[info.size];
		     if(chunk != null){
		     chunkold = chunk;
		     }
		     int outputBufIndex = res;
		     ByteBuffer buf = codecOutputBuffers[outputBufIndex];
		     byte[] filterBuffer = new byte[4*info.size];
		     chunk = new byte[info.size];
		     System.out.println("index "+outputBufIndex) ;
//		     for(outputBufIndex=0;outputBufIndex<4;){
//		    	 buf.get(filterBuffer, info.size*outputBufIndex, info.size);
//		    	 
//		     }
		          
		     buf.get(chunk);
		     buf.clear();
		     short newSample =0;
		     
		     
		     if(chunk.length > 0 && chunkold.length == chunk.length){   
		    	 //for(int i = 300; i<chunk.length+300;i++){
//		    	 newSample =  (short) (getSample(chunk,i)+getSample(chunkold,i));
//		    	 setSample(chunk,i,newSample );
		    	 //chunk = filter(chunk, chunkold, chunk.length);
		    	 //}
		    
		    
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
		
		      relaxResources(true);
		
		g.setData1(true);
		}


//	byte[] returnchunk(){
//		
//		return this.chunk;
//	}
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
//	      short oldSample1 = getSample(samples, i);
//	      short oldSample2 = getSample(samples, i);
	      short offSet = getSample(offset,i);
	      
	      short newSample = (short) (oldSample+ 0.5*offSet);
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



class FilteredSoundStream extends FilterInputStream {

	  private static final int REMAINING_SIZE_UNKNOWN = -1;

	  private SoundFilter soundFilter;

	  private int remainingSize;

	  /**
	   * Creates a new FilteredSoundStream object with the specified InputStream
	   * and SoundFilter.
	   */
	  public FilteredSoundStream(InputStream in, SoundFilter soundFilter) {
	    super(in);
	    this.soundFilter = soundFilter;
	    remainingSize = REMAINING_SIZE_UNKNOWN;
	  }

	  /**
	   * Overrides the FilterInputStream method to apply this filter whenever
	   * bytes are read
	   */
	  public int read(byte[] samples, int offset, int length) throws IOException {
	    // read and filter the sound samples in the stream
	    int bytesRead = super.read(samples, offset, length);
	    if (bytesRead > 0) {
	      soundFilter.filter(samples, offset, bytesRead);
	      return bytesRead;
	    }

	    // if there are no remaining bytes in the sound stream,
	    // check if the filter has any remaining bytes ("echoes").
	    if (remainingSize == REMAINING_SIZE_UNKNOWN) {
	      remainingSize = soundFilter.getRemainingSize();
	      // round down to nearest multiple of 4
	      // (typical frame size)
	      remainingSize = remainingSize / 4 * 4;
	    }
	    if (remainingSize > 0) {
	      length = Math.min(length, remainingSize);

	      // clear the buffer
	      for (int i = offset; i < offset + length; i++) {
	        samples[i] = 0;
	      }

	      // filter the remaining bytes
	      soundFilter.filter(samples, offset, length);
	      remainingSize -= length;

	      // return
	      return length;
	    } else {
	      // end of stream
	      return -1;
	    }
	  }

	}

class EchoFilter extends SoundFilter {

	  private short[] delayBuffer;

	  private int delayBufferPos;

	  private float decay;

	  /**
	   * Creates an EchoFilter with the specified number of delay samples and the
	   * specified decay rate.
	   * <p>
	   * The number of delay samples specifies how long before the echo is
	   * initially heard. For a 1 second echo with mono, 44100Hz sound, use 44100
	   * delay samples.
	   * <p>
	   * The decay value is how much the echo has decayed from the source. A decay
	   * value of .5 means the echo heard is half as loud as the source.
	   */
	  public EchoFilter(int numDelaySamples, float decay) {
	    delayBuffer = new short[numDelaySamples];
	    this.decay = decay;
	  }

	  /**
	   * Gets the remaining size, in bytes, of samples that this filter can echo
	   * after the sound is done playing. Ensures that the sound will have decayed
	   * to below 1% of maximum volume (amplitude).
	   */
	  public int getRemainingSize() {
	    float finalDecay = 0.01f;
	    // derived from Math.pow(decay,x) <= finalDecay
	    int numRemainingBuffers = (int) Math.ceil(Math.log(finalDecay)
	        / Math.log(decay));
	    int bufferSize = delayBuffer.length * 2;

	    return bufferSize * numRemainingBuffers;
	  }

	  /**
	   * Clears this EchoFilter's internal delay buffer.
	   */
	  public void reset() {
	    for (int i = 0; i < delayBuffer.length; i++) {
	      delayBuffer[i] = 0;
	    }
	    delayBufferPos = 0;
	  }

	  /**
	   * Filters the sound samples to add an echo. The samples played are added to
	   * the sound in the delay buffer multipied by the decay rate. The result is
	   * then stored in the delay buffer, so multiple echoes are heard.
	   */
	  public void filter(byte[] samples, int offset, int length) {

	    for (int i = offset; i < offset + length; i += 2) {
	      // update the sample
	      short oldSample = getSample(samples, i);
	      short newSample = (short) (oldSample + decay
	          * delayBuffer[delayBufferPos]);
	      setSample(samples, i, newSample);

	      // update the delay buffer
	      delayBuffer[delayBufferPos] = newSample;
	      delayBufferPos++;
	      if (delayBufferPos == delayBuffer.length) {
	        delayBufferPos = 0;
	      }
	    }
	  }

	}

	/**
	 * The FilteredSoundStream class is a FilterInputStream that applies a
	 * SoundFilter to the underlying input stream.
	 * 
	 * @see SoundFilter
	 */

	

	abstract class SoundFilter {

	  /**
	   * Resets this SoundFilter. Does nothing by default.
	   */
	  public void reset() {
	    // do nothing
	  }

	  /**
	   * Gets the remaining size, in bytes, that this filter plays after the sound
	   * is finished. An example would be an echo that plays longer than it's
	   * original sound. This method returns 0 by default.
	   */
	  public int getRemainingSize() {
	    return 0;
	  }

	  /**
	   * Filters an array of samples. Samples should be in 16-bit, signed,
	   * little-endian format.
	   */
	  public void filter(byte[] samples) {
	    filter(samples, 0, samples.length);
	  }

	  /**
	   * Filters an array of samples. Samples should be in 16-bit, signed,
	   * little-endian format. This method should be implemented by subclasses.
	   */
	  public abstract void filter(byte[] samples, int offset, int length);

	  /**
	   * Convenience method for getting a 16-bit sample from a byte array. Samples
	   * should be in 16-bit, signed, little-endian format.
	   */
	  public short getSample(byte[] buffer, int position) {
	    return (short) (((buffer[position + 1] & 0xff) << 8) | (buffer[position] & 0xff));
	  }

	  /**
	   * Convenience method for setting a 16-bit sample in a byte array. Samples
	   * should be in 16-bit, signed, little-endian format.
	   */
	  public void setSample(byte[] buffer, int position, short sample) {
	    buffer[position] = (byte) (sample & 0xff);
	    buffer[position + 1] = (byte) ((sample >> 8) & 0xff);
	  }

	}  

}