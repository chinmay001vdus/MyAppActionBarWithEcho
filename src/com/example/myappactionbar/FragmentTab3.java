package com.example.myappactionbar;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class FragmentTab3 extends Fragment {
	long time = 0;
	  long time1 = 0;
	communicate cm;
	ImageView iView;
	View v;
	int x = 1;
    int y = 0;
	int initialX =0;
	int initialY =0;
	int m = 0;
	int r = 30;
	int t = 0;
	int rd = 0;
	Globals g  = Globals.getInstance();
	
  public View onCreateView(LayoutInflater inflater, ViewGroup container, 
                           Bundle savedInstanceState){
	  View android = inflater.inflate(R.layout.tab_frag3, container, false);
      ((TextView)android.findViewById(R.id.textView)).setText("Android");
      ((ImageView)android.findViewById(R.id.imageView1)).setImageDrawable(new myDrawable(0,0));
      return android;
  }

//  class myRect extends Drawable{
//	  Paint paint = new Paint();
//	  
//	  myRect() {
//		  
//	  }
//	@Override
//	public void draw(Canvas canvas) {
//		// TODO Auto-generated method stub
//		
//	}
//
//	@Override
//	public void setAlpha(int alpha) {
//		// TODO Auto-generated method stub
//		
//	}
//
//	@Override
//	public void setColorFilter(ColorFilter cf) {
//		// TODO Auto-generated method stub
//		
//	}
//
//	@Override
//	public int getOpacity() {
//		// TODO Auto-generated method stub
//		return 0;
//	}
//	  
//  }
  
  class myDrawable extends Drawable{
	  Paint paint = new Paint();
	  int X ;
	  int Y ;
	  myDrawable(int _x, int _y){
		   X = _x;
		  Y = _y;
	  }
	@Override
	public void draw(Canvas canvas) {
		// TODO Auto-generated method stub
		
		RectF mRect = new RectF(25,25,canvas.getWidth()-25,canvas.getHeight()-150);
		paint.setColor(Color.parseColor("#FFC897"));
		paint.setStrokeWidth(10);
	    paint.setStyle(Paint.Style.FILL);
	    canvas.drawRoundRect( mRect,25f,25f, paint);
		
		if((X-(canvas.getWidth()/2)!=0)){
			 t= (int) ((Math.atan2((Y-(canvas.getHeight()/2)),(X-(canvas.getWidth()/2)))) *360/(2*Math.PI));
			 
				if(t <0){
					t= -t;
				}else{
					t = 360-t;
				}
			}
		rd = 255*(int) Math.sqrt((Math.pow((Y-(canvas.getHeight()/2)), 2))+(Math.pow((X-(canvas.getWidth()/2)), 2)))/(int)Math.sqrt((Math.pow(((canvas.getHeight()/2)), 2))+(Math.pow(((canvas.getWidth()/2)), 2)));
		
		paint.setStrokeWidth(10);
	      paint.setStyle(Paint.Style.FILL);
	      paint.setColor(Color.parseColor("#eeee12"));
	      
	      canvas.drawCircle(x, y, r, paint);
	      
	      paint.setColor(Color.BLACK);
	      canvas.drawCircle(x, y, 15, paint);
	}

	@Override
	public void setAlpha(int alpha) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setColorFilter(ColorFilter cf) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int getOpacity() {
		// TODO Auto-generated method stub
		return 0;
	} 
      
	 
             
      
  }
  
     @Override
     public void onViewCreated(View view, Bundle savedInstanceState) {
          super.onViewCreated(view, savedInstanceState);
          
    	  //final ImageView iView = (ImageView) view.findViewById(R.id.imageView1);
           
          
    	   //m = (int) ((Math.atan(y/x))*180f/(Math.PI));
        view.setOnTouchListener(new View.OnTouchListener() {
        	
        	
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				//((ImageView)v.findViewById(R.id.imageView1)).setImageDrawable(new myDrawable(x,y));		
				((TextView)v.findViewById(R.id.textView1)).setText("rd :"+rd);
			    ((TextView)v.findViewById(R.id.textView2)).setText("t :"+t); 
			    ((ImageView)v.findViewById(R.id.imageView1)).setImageDrawable(new myDrawable(x,y)); 
			    //Log.d("time", Long.toString(time));
			    //Log.d("time1", Long.toString(time1));
			    //if(time - time1 > 30 ){
			     cm.sliderM(rd, t);
			     //time1 = time;
			    //}
      			//time = event.getEventTime();
			    
				//circularImageBar(iView,m);
				
			      switch (event.getAction()) {
			          case MotionEvent.ACTION_DOWN:
			        	  x = (int)event.getX();
			        	  initialX = x;
			              y = (int)event.getY();
			              initialY = y;
			              g.setdatasendflag(true);
			    	       return true;
			          case MotionEvent.ACTION_MOVE:
			        	  x = (int)event.getX();
					      y = (int)event.getY();
					      //String message = "position "+String.valueOf(t)+"%"+ String.valueOf(rd);
					      //g.setBDataString(message);
					     
			        	  return true;
			          case MotionEvent.ACTION_UP:
			      	      r = 30;
			        	 g.setdatasendflag(false);
			        	  return true;
			          
			              }
			         			      
			        return false;
			        
                   }         
                   });
        
        
  } 
 
  
  public void onActivityCreated(Bundle savedInstanceState){
	  super.onActivityCreated(savedInstanceState);
      cm = (communicate) getActivity();
	    }
}