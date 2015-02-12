package com.example.myappactionbar;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;


public class FragmentTab2 extends Fragment {
  communicate cm;

  private Context globalContext = null;
  public int skp1;
  public int skp2;
  public int skp3;
  public int skp4;
  public int skp5;
  public int skp6;


  public View onCreateView(LayoutInflater inflater, ViewGroup container, 
                           Bundle savedInstanceState){
	  View ios = inflater.inflate(R.layout.tab_frag2, container, false);
	  
      
      return ios;
  }
  
  @Override
  public void onAttach(Activity activity) {
	  super.onAttach(activity);
	  globalContext = getActivity();
	 
  }
  
  @Override
  public void onViewCreated(View view, Bundle savedInstanceState) {
          super.onViewCreated(view, savedInstanceState);
   
          final SeekBar sk1 = (SeekBar)view.findViewById(R.id.seekBar1);
           
          sk1.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
        	  
        	  @Override
      		public void onProgressChanged(SeekBar seekBar, int progress,boolean fromUser) {
      			// TODO Auto-generated method stub
      			skp1=(int) (progress*2.55);
      			cm.sendSliderData(skp1,"1");
      			//t1.setTextSize(p);
      		}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
				
			}
      	 });     
  
          
          
          final SeekBar sk2 = (SeekBar)view.findViewById(R.id.seekBar2);
          
          sk2.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
        	  
        	  @Override
      		public void onProgressChanged(SeekBar seekBar, int progress,boolean fromUser) {
      			// TODO Auto-generated method stub
      			skp2=(int) (progress*2.55);
      			cm.sendSliderData(skp2,"2");
      			//t1.setTextSize(p);
      		}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
				
			}
      	 });
          
          
          
          final SeekBar sk3 = (SeekBar)view.findViewById(R.id.seekBar3);
          
          sk3.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
        	  
        	  @Override
      		public void onProgressChanged(SeekBar seekBar, int progress,boolean fromUser) {
      			// TODO Auto-generated method stub
      			skp3=(int) (progress*2.55);
      			cm.sendSliderData(skp3,"3");
      			//t1.setTextSize(p);
      		}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
				
			}
      	 });
          
          
          final SeekBar sk4 = (SeekBar)view.findViewById(R.id.seekBar4);
          
          sk4.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
        	  
        	  @Override
      		public void onProgressChanged(SeekBar seekBar, int progress,boolean fromUser) {
      			// TODO Auto-generated method stub
      			skp4=(int) (progress*2.55);
      			cm.sendSliderData(skp4,"4");
      			//t1.setTextSize(p);
      		}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
				
			}
      	 });
          
          
          final SeekBar sk5 = (SeekBar)view.findViewById(R.id.seekBar5);
          
          sk5.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
        	  
        	  @Override
      		public void onProgressChanged(SeekBar seekBar, int progress,boolean fromUser) {
      			// TODO Auto-generated method stub
      			skp5=(int) (progress*2.55);
      			cm.sendSliderData(skp5,"5");
      			//t1.setTextSize(p);
      		}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
				
			}
      	 });
          
          
          final SeekBar sk6 = (SeekBar)view.findViewById(R.id.seekBar6);
          
          sk6.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
        	  
        	  @Override
      		public void onProgressChanged(SeekBar seekBar, int progress,boolean fromUser) {
      			// TODO Auto-generated method stub
      			skp6=(int) (progress*2.55);
      			cm.sendSliderData(skp6,"6");
      			//t1.setTextSize(p);
      		}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
				
			}
      	 });
          
          
  }
  
  
  public void onActivityCreated(Bundle savedInstanceState){
	  super.onActivityCreated(savedInstanceState);
      cm = (communicate) getActivity();
	    }
}