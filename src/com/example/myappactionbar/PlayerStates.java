package com.example.myappactionbar;

public class PlayerStates {
	
private static PlayerStates instance;
	
	static int	READY_TO_PLAY = 2;
	static int PLAYING = 3; 
	static int STOPPED = 4; 
	static int NOT_SET = -1; 
	static int playerState = NOT_SET;
    
	private PlayerStates() {}
	
    public int get() {
    	return this.playerState;
    }
    
    public void set(int state) { 
    	this.playerState = state;
    }
    
    
    /**
     * Checks whether the player is ready to play, this is the state used also for Pause (phase 2)
     *
     * @return <code>true</code> if ready, <code>false</code> otherwise
     */
    public synchronized boolean isReadyToPlay() {
        return this.playerState == READY_TO_PLAY;
    }
    
    
    /**
     * Checks whether the player is currently playing (phase 3)
     *
     * @return <code>true</code> if playing, <code>false</code> otherwise
     */
    public synchronized boolean isPlaying() {
        return this.playerState == PLAYING;
    }
    
    
    /**
     * Checks whether the player is currently stopped (not playing)
     *
     * @return <code>true</code> if playing, <code>false</code> otherwise
     */
    public synchronized boolean isStopped() {
        return this.playerState == STOPPED;
    }
    
    static synchronized PlayerStates getInstance(){
        if(instance==null){
            instance=new PlayerStates();
        }
        return instance;
    }

}
