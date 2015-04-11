package com.example.myappactionbar;

public class Globals {
    private static Globals instance;
    
    private boolean isPlaying = false;
    private int echoLevel;
    private boolean doStop;
    private String bDataStr;
    private boolean sawOutputEOS = false;
    private int noOutputCounter = 0;
    private boolean sendataflag = false;

    

    private Globals() {}
    
    public void setdatasendflag(boolean b){
    	this.sendataflag = b;
    }

    public void setData1(boolean a){
        this.doStop=a;
    }
    
    public void setData2(boolean a){
    	 this.sawOutputEOS = a;
    }
    
   public void setData3( int a){
	     this.noOutputCounter = a;
    }
   
   public void setEchoLevel(int a){
       this.echoLevel=a;
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
    
    public int getEchoLevel(){

        return this.echoLevel;
    }
    
    public String getBDataString(){
    	return this.bDataStr;
    }
    
    
    public boolean getdatasendflag(){
    return this.sendataflag;
    }
    
    
    public void setBDataString(String s){
    	this.bDataStr = s;
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
