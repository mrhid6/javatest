package net.javacity.gui;

import java.awt.Font;

import org.newdawn.slick.Color;
import org.newdawn.slick.TrueTypeFont;

public class GuiText {
	
	private static TrueTypeFont[] fonts = new TrueTypeFont[10];
	
	public static int getStringWidth(String text, int size){
		
		if(!(size%2==0))
			return 0;

		int i = (size-10)/2;

		if(i<0 || i>fonts.length-1)
			return 0;
		
		return fonts[i].getWidth(text);
	}
	
	public static int getStringHeight(String text, int size){
		
		if(!(size%2==0))
			return 0;
		
		int i = (size-10);
		
		if(i<0 || i>fonts.length-1)
			return 0;
		
		return fonts[i].getHeight(text);
	}
	
	public static void drawText(String text, int x,int y, boolean shadow, int color, int size){

		if(!(size%2==0))
			return;

		int i = (size-10)/2;

		if(i<0 || i>fonts.length-1)
			return;

		if(shadow){
			fonts[i].drawString(x+1, y+1, text, Color.black);
		}

		fonts[i].drawString(x, y, text, new Color(color));
	}
	
	static{
		for(int i=0;i<fonts.length;i++){
			Font awtFont = new Font("Arial", Font.BOLD, 10+(i));
			fonts[i] = new TrueTypeFont(awtFont, true);
		}
	}
}
