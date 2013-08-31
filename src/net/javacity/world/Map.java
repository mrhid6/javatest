package net.javacity.world;

import static org.lwjgl.opengl.GL11.GL_COMPILE;
import static org.lwjgl.opengl.GL11.GL_QUADS;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glEnd;
import static org.lwjgl.opengl.GL11.glEndList;
import static org.lwjgl.opengl.GL11.glGenLists;
import static org.lwjgl.opengl.GL11.glNewList;
import static org.lwjgl.opengl.GL11.glTexCoord2f;
import static org.lwjgl.opengl.GL11.glVertex3f;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;

public class Map {
	public int mapList;

	int mapHeight=-1;
	int mapWidth=-1;

	public void reset(){
		mapHeight = -1;
		mapWidth = -1;

	}
	public void loadMap() throws Exception{

		File mapFile = new File("res/map/1.dat");

		BufferedReader br = new BufferedReader(new FileReader(mapFile));

		String line = br.readLine();

		ArrayList<ArrayList<Float>> heightData = new ArrayList<ArrayList<Float>>();
		ArrayList<ArrayList<Integer>> positionData = new ArrayList<ArrayList<Integer>>();
		
		while (line != null) {

			
			if(isValidLine(line)){
				//System.out.println(line);
				String[] lineData = line.split(";");


				String[] poisitonD = lineData[0].split("/");
				String[] heightD = lineData[1].split("/");

				ArrayList<Integer> poisitonArray = new ArrayList<Integer>();
				ArrayList<Float> heightArray = new ArrayList<Float>();

				for(int i=0;i<heightD.length;i++){
					heightArray.add(Float.parseFloat(heightD[i]));
				}

				for(int i=0;i<poisitonD.length;i++){
					poisitonArray.add(Integer.parseInt(poisitonD[i]));
				}

				positionData.add(poisitonArray);
				heightData.add(heightArray);
			}

			line = br.readLine();
		}
		
		br.close();

		mapList = glGenLists(1);

		glNewList(mapList, GL_COMPILE);

		for(int y=0;y<heightData.size();y++){
			glBegin(GL_QUADS);

			float xOffset = positionData.get(y).get(0);
			float zOffset = positionData.get(y).get(1);

			float texNo = xOffset+(zOffset*16);

			float col = texNo / 16;
			float row = texNo % 16;

			float u0 = row / 32;
			float u1 = (row + 1)/ 32;

			float v0 = col / 32 - (xOffset/512);
			float v1 = (col + 1) / 32 - (xOffset/512); 

			//System.out.println("#####################");
			//System.out.println(u0+","+v0+","+u1+","+v1);

			glTexCoord2f(u0,v0);
			glVertex3f(0+xOffset, heightData.get(y).get(0), 0+zOffset);

			glTexCoord2f(u0,v1);
			glVertex3f(0+xOffset, heightData.get(y).get(1), 1+zOffset);

			glTexCoord2f(u1,v1);
			glVertex3f(1+xOffset, heightData.get(y).get(2), 1+zOffset);

			glTexCoord2f(u1,v0);
			glVertex3f(1+xOffset, heightData.get(y).get(3), 0+zOffset);

			glEnd();
		}
		glEndList();

	}
	

	public boolean isValidLine(String line){

		if(line.startsWith(" "))
			return false;

		if(line.startsWith("##"))
			return false;

		return true;

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
