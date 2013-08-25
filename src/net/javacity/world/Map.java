package net.javacity.world;

import static org.lwjgl.opengl.GL11.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;

public class Map {
	public int mapList;
	
	int mapHeight=-1;
	int mapWidth=-1;

	//int[][] data;

	public void loadMap() throws Exception{

		File mapFile = new File("res/map/1.dat");

		BufferedReader br = new BufferedReader(new FileReader(mapFile));

		String line = br.readLine();
		int yCount=0;
		
		ArrayList<ArrayList<Integer>> mapData = new ArrayList<ArrayList<Integer>>();
		ArrayList<ArrayList<Integer>> positionData = new ArrayList<ArrayList<Integer>>();
		
		while (line != null) {

			System.out.println(line);
			String[] lineData = line.split(";");
			
			
			String[] poisitonD = lineData[0].split("/");
			String[] heightData = lineData[1].split("/");
			
			ArrayList<Integer> poisitonArray = new ArrayList<Integer>();
			ArrayList<Integer> heightArray = new ArrayList<Integer>();
			
			for(int i=0;i<heightData.length;i++){
				heightArray.add(Integer.parseInt(heightData[i]));
			}
			
			for(int i=0;i<poisitonD.length;i++){
				poisitonArray.add(Integer.parseInt(poisitonD[i]));
			}
			
			positionData.add(poisitonArray);
			mapData.add(heightArray);
			
			line = br.readLine();
			yCount++;
		}
		
		float h = getMapHeight(positionData);
		float w = getMapWidth(positionData);
		
		mapList = glGenLists(1);
		// TODO: Add alternative VBO rendering for pseudo-compatibility with version 3 and higher.
		glNewList(mapList, GL_COMPILE);
		
		for(int y=0;y<mapData.size();y++){
			glBegin(GL_QUADS);
			
			float xOffset = positionData.get(y).get(0);
			float zOffset = positionData.get(y).get(1);
			
			float texNo = xOffset+(zOffset*16);
			
			float row = texNo / 16;
			float col = texNo % 16;

			float u0 = row / 32;
			float u1 = (row + 1) / 32;

			float v0 = col / 32;
			float v1 = (col + 1) / 32; 
			
			
			
			glTexCoord2f(u0,v0);
			glVertex3f(0+xOffset, mapData.get(y).get(0), 0+zOffset);
			
			glTexCoord2f(u0,v1);
			glVertex3f(0+xOffset, mapData.get(y).get(1), 1+zOffset);
			
			glTexCoord2f(u1,v1);
			System.out.println(u1+","+v1);
			glVertex3f(1+xOffset, mapData.get(y).get(2), 1+zOffset);
			
			glTexCoord2f(u1,v0);
			glVertex3f(1+xOffset, mapData.get(y).get(3), 0+zOffset);
			
			glEnd();
		}
		glEndList();
		
		System.out.println(h+","+w);
		
	}
	
	public int getMapHeight(ArrayList<ArrayList<Integer>> data){
		if(mapHeight!=-1)
			return mapHeight;
		
		int lastx = -1;
		for(int i=0;i<data.size();i++){
			int x = data.get(i).get(0);
			
			if(lastx<x){
				lastx=x;
				mapHeight = x+1;
			}
		}
		
		return mapHeight;
	}
	
	public int getMapWidth(ArrayList<ArrayList<Integer>> data){
		
		if(mapWidth!=-1)
			return mapWidth;
		
		int lastz = -1;
		for(int i=0;i<data.size();i++){
			int x = data.get(i).get(0);
			
			if(lastz<x){
				lastz=x;
				mapWidth = x+1;
			}
		}
		
		return mapWidth;
	}

}
