package net.javacity.animation;

public class AnimationTime {
	
	private float startTime;
	private float endTime;
	
	private float currentTime;
	private float timeToNextFrame;
	private int startFrame;
	private int endFrame;
	
	private float currentFrame = 0;
	private int lastAnimationTick;
	
	public AnimationTime(float startTime, float endTime, float timeToNextFrame, int startFrame, int endFrame) {
		super();
		
		this.startTime = startTime;
		this.endTime = endTime;
		this.startFrame = startFrame;
		this.endFrame = endFrame;
		
		currentTime = startTime;
		currentFrame = startFrame;
		
		this.timeToNextFrame = timeToNextFrame;
	}
	
	public void update(){
		lastAnimationTick++;
	}
	
	public boolean shouldAnimate(){
		update();
		return (lastAnimationTick%4==0);
	}

	public float getCurrentTime() {
		return currentTime;
	}
	
	public float interval(){
		return timeToNextFrame;
	}
	
	public void subFrame(){
		currentTime -= timeToNextFrame;
		if(currentTime <= startTime) currentTime =  startTime;
	}
	
	public void addFrame(){
		currentTime += timeToNextFrame;
		if(currentTime >= endTime) currentTime = endTime;; 
	}
	
	public void loopAnimation(){
		currentFrame++;
		if(currentFrame>endFrame){
			//System.out.println(currentTime);
			currentFrame=startFrame;
			currentTime = startTime;
		}
		
		currentTime = startTime+(timeToNextFrame*currentFrame);
		
	}
}
