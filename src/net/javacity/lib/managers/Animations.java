package net.javacity.lib.managers;

import java.util.ArrayList;
import java.util.HashMap;

import net.javacity.animation.AnimationTime;

public class Animations {
	
	
	private static HashMap<String, AnimationTime> animations = new HashMap<String, AnimationTime>();
	
	public static void addAnimation(String key, AnimationTime animation){
		animations.put(key, animation);
	}
	
	public static AnimationTime getAnimation(String key){
		if(animations.containsKey(key)){
			return animations.get(key).copy();
		}
		return null;
	}
}
