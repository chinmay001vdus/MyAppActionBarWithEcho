package com.example.myappactionbar;

import android.app.Activity;
import android.content.Context;
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
import android.widget.Toast;

public class FragmentTab4 extends Fragment {
	Globals g  = Globals.getInstance();
	communicate cm;
	ImageView iView;
	View v;
	long time1 = 0;
    long time =0;
	float mdist = 0;
	int otherX = 0;
	int otherY = 0;
	int x = 1;
    int y = 0;
	int initialX =0;
	int initialY =0;
	int m = 0;
	int r = 30;
	float xx =0;
	float yy =0;
	double angle = 0;
	private static Context globalContext = null;
	int ang1;
  public View onCreateView(LayoutInflater inflater, ViewGroup container, 
                           Bundle savedInstanceState){
	  View android = inflater.inflate(R.layout.tab_frag4, container, false);
	  
      ((ImageView)android.findViewById(R.id.imageView001)).setImageDrawable(new myDrawable(0,0,2));
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
//			}
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
	  float X ;
	  float Y ;
	  float Q;
	  myDrawable(int _x, int _y,int q){
		   X = (float)_x;
		  Y = (float)_y;
		  Q = q;
	  }
	  
	@Override
	public void draw(Canvas canvas) {
		// TODO Auto-generated method stub
		
RectF mRect = new RectF(25,25,canvas.getWidth()-25,canvas.getHeight()-25);
		
		paint.setColor(Color.parseColor("#dcdcdc"));
		paint.setStrokeWidth(10);
	    paint.setStyle(Paint.Style.FILL);
	    canvas.drawRoundRect( mRect,25f,25f, paint);
	    
	    paint.setColor(Color.parseColor("#555555"));
		paint.setStrokeWidth(20);
	    paint.setStyle(Paint.Style.STROKE);
	    canvas.drawCircle(canvas.getWidth()/2, canvas.getHeight()/2, 300, paint);
        
	    //if(Q == 1) {
	    
	    paint.setColor(Color.argb(100, (int)(200*(1-mdist)),(int) (200*mdist), 0));
		paint.setStyle(Paint.Style.FILL);
	    canvas.drawCircle(canvas.getWidth()/2, canvas.getHeight()/2, mdist*380, paint);
	    
	    mdist = (float)Math.sqrt((Math.pow((otherX - X), 2) + Math.pow((otherY - Y),2)));
	    if(mdist < canvas.getWidth()) {
	    	mdist = mdist/canvas.getWidth();
	    }else{
	    	mdist = 1;
	    }
	    
	   

	    //}else{
		if((X-(canvas.getWidth()/2)!=0)){
		 angle= Math.atan2((Y-(canvas.getHeight()/2)),(X-(canvas.getWidth()/2)));
		}
				
		xx = (float)(Math.cos(angle)*300+(canvas.getWidth()/2));
		yy = (float)(Math.sin(angle)*300 +(canvas.getHeight()/2));
		ang1 = (int)(angle*360/(2*Math.PI));
		if(ang1 <0){
			ang1= -ang1;
		}else{
			ang1 = 360-ang1;
		}
		
		
		//Toast.makeText(globalContext, "ang"+ang1, Toast.LENGTH_SHORT).show();
		
		  paint.setStrokeWidth(10);
	      paint.setStyle(Paint.Style.FILL);
	      paint.setColor(Color.parseColor("#ff9912"));
	      
	      canvas.drawCircle(xx, yy, r, paint);
	      
	      paint.setColor(Color.BLACK);
	      canvas.drawCircle(xx, yy, 15, paint);
	   // }
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
  public void onAttach(Activity activity) {
	  super.onAttach(activity);
	  globalContext = getActivity();
	 
  }
  
     @Override
     public void onViewCreated(View view, Bundle savedInstanceState) {
          super.onViewCreated(view, savedInstanceState);
          
    	  //final ImageView iView = (ImageView) view.findViewById(R.id.imageView1);
           
//          while(g.getdatasendflag())
//          {
//			    String message = "rotator "+String.valueOf(ang1);
//			    g.setBDataString(message);
//          }
    	   //m = (int) ((Math.atan(y/x))*180f/(Math.PI));
        view.setOnTouchListener(new View.OnTouchListener() {
        	
        	
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				//((ImageView)v.findViewById(R.id.imageView1)).setImageDrawable(new myDrawable(x,y));		
				((TextView)v.findViewById(R.id.textView001)).setText("angle :"+mdist);
			    //((TextView)v.findViewById(R.id.textView201)).setText("y :"+y); 
			    //((ImageView)v.findViewById(R.id.imageView001)).setImageDrawable(new myDrawable(x,y)); 
			    
//			    int pointerIndex  = event.getActionIndex();
//			    int pointerId = event.getPointerId(pointerIndex);
			    int maskedAction = event.getActionMasked();
			    //Log.d("time", Long.toString(time));
			    //Log.d("time1", Long.toString(time1));
			    //if(time - time1 > 30 ){
			    	
			    if(event.getPointerCount() > 1){
					    cm.overAllVolume(mdist);	
					
					    }else{
					    cm.circleSliderData(ang1);
					
					    }
			    //time1 = time;
			    //}
			    //time =  event.getEventTime();
			    
			    
			    
			    ((ImageView)v.findViewById(R.id.imageView001)).setImageDrawable(new myDrawable(x,y,1));
			    
		
			    //((ImageView)v.findViewById(R.id.imageView001)).setImageDrawable(new myDrawable(x,y,2));
			    
			    
			    
			      switch (maskedAction) {
			          case MotionEvent.ACTION_DOWN:
			        	  x = (int)event.getX(0);
			        	  initialX = x;
			              y = (int)event.getY(0);
			              initialY = y;
			              g.setdatasendflag(true);
			              
			    	       return true;
			          case MotionEvent.ACTION_MOVE:
			        	  
			        	  x = (int)event.getX(0);
					      y = (int)event.getY(0);
					      otherX = (int) event.getX(event.getPointerCount()-1);
			        	  otherY = (int) event.getY(event.getPointerCount()-1);
					      //message = "rotator "+String.valueOf(ang1);
						  //g.setBDataString(message);
					      
			        	  return true;
			          case MotionEvent.ACTION_UP:
			      	      r = 30;
			      	    x = (int)event.getX(0);
					    y = (int)event.getY(0);
			      	     
			      	      //g.setdatasendflag(false);			        	 
			        	  return true;
			          
			          case MotionEvent.ACTION_POINTER_DOWN: 
			        	  otherX = (int) event.getX(event.getPointerCount()-1);
			        	  otherY = (int) event.getY(event.getPointerCount()-1);
			        	  return true;
			        	  
			          case MotionEvent.ACTION_POINTER_UP:
			        	  otherX = (int) event.getX(event.getPointerCount()-1);
			        	  otherY = (int) event.getY(event.getPointerCount()-1);
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