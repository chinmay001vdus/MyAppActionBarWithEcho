package com.example.myappactionbar;

public class Globals {
    private static Globals instance;
    private boolean isPlaying = false;
    
    private boolean doStop;
    private boolean sawOutputEOS = false;
    private int noOutputCounter = 0;
    

    private Globals() {}

    public void setData1(boolean a){
        this.doStop=a;
    }
    
    public void setData2(boolean a){
    	 this.sawOutputEOS = a;
    }
    
   public void setData3( int a){
	     this.noOutputCounter = a;
    }

    public boolean getData1(){
    	 return this.doStop;
         
    }
    
    public boolean getData2(){
    	return this.sawOutputEOS;
    }
    
    public int getData3(){

        return this.noOutputCounter;
    }
    
    
    
    
    
    
    
    
    
    
    public void setData(boolean d){
        this.isPlaying=d;
    }
    public boolean getData(){
        return this.isPlaying;
    }

    public static synchronized Globals getInstance(){
        if(instance==null){
            instance=new Globals();
        }
        return instance;
    }
}
