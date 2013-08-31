package net.javacity.lib.managers;

import java.io.BufferedReader;
import java.io.IOException;

import net.javacity.lib.Mesh;
import net.javacity.lib.Object3D;

public class ObjectLoader {

	
	public static Mesh loadobject(BufferedReader br) {
		int linecounter = 0;
		Mesh mesh = new Mesh();
		try {
			
			String newline;
			boolean firstpass = true;
			Object3D currentObj = new Object3D();
			
			while (((newline = br.readLine()) != null)) {
				linecounter++;
				newline = newline.trim();
				if (newline.length() > 0) {
					
					if (newline.charAt(0) == 'v' && newline.charAt(1) == ' ') {
						float[] coords = new float[4];
						String[] coordstext = new String[4];
						coordstext = newline.split("\\s+");
						for (int i = 1;i < coordstext.length;i++) {
							coords[i-1] = Float.valueOf(coordstext[i]).floatValue();
						}
						//// check for farpoints ////
						if (firstpass) {
							currentObj.rightpoint = coords[0];
							currentObj.leftpoint = coords[0];
							currentObj.toppoint = coords[1];
							currentObj.bottompoint = coords[1];
							currentObj.nearpoint = coords[2];
							currentObj.farpoint = coords[2];
							firstpass = false;
						}
						if (coords[0] > currentObj.rightpoint) {
							currentObj.rightpoint = coords[0];
						}
						if (coords[0] < currentObj.leftpoint) {
							currentObj.leftpoint = coords[0];
						}
						if (coords[1] > currentObj.toppoint) {
							currentObj.toppoint = coords[1];
						}
						if (coords[1] < currentObj.bottompoint) {
							currentObj.bottompoint = coords[1];
						}
						if (coords[2] > currentObj.nearpoint) {
							currentObj.nearpoint = coords[2];
						}
						if (coords[2] < currentObj.farpoint) {
							currentObj.farpoint = coords[2];
						}
						/////////////////////////////
						currentObj.vertexsets.add(coords);
					}
					if (newline.charAt(0) == 'v' && newline.charAt(1) == 't') {
						float[] coords = new float[4];
						String[] coordstext = new String[4];
						coordstext = newline.split("\\s+");
						for (int i = 1;i < coordstext.length;i++) {
							coords[i-1] = Float.valueOf(coordstext[i]).floatValue();
						}
						currentObj.vertexsetstexs.add(coords);
					}
					if (newline.charAt(0) == 'v' && newline.charAt(1) == 'n') {
						float[] coords = new float[4];
						String[] coordstext = new String[4];
						coordstext = newline.split("\\s+");
						for (int i = 1;i < coordstext.length;i++) {
							coords[i-1] = Float.valueOf(coordstext[i]).floatValue();
						}
						currentObj.vertexsetsnorms.add(coords);
					}
					if (newline.charAt(0) == 'f' && newline.charAt(1) == ' ') {
						String[] coordstext = newline.split("\\s+");
						int[] v = new int[coordstext.length - 1];
						int[] vt = new int[coordstext.length - 1];
						int[] vn = new int[coordstext.length - 1];
						
						for (int i = 1;i < coordstext.length;i++) {
							String fixstring = coordstext[i].replaceAll("//","/0/");
							String[] tempstring = fixstring.split("/");
							v[i-1] = Integer.valueOf(tempstring[0]).intValue();
							if (tempstring.length > 1) {
								vt[i-1] = Integer.valueOf(tempstring[1]).intValue();
							} else {
								vt[i-1] = 0;
							}
							if (tempstring.length > 2) {
								vn[i-1] = Integer.valueOf(tempstring[2]).intValue();
							} else {
								vn[i-1] = 0;
							}
						}
						currentObj.faces.add(v);
						currentObj.facestexs.add(vt);
						currentObj.facesnorms.add(vn);
					}
				}
			}
			mesh.addObject(currentObj);
			mesh.build();
			
		} catch (IOException e) {
			System.out.println("Failed to read file: " + br.toString());
			//System.exit(0);			
		} catch (NumberFormatException e) {
			System.out.println("Malformed OBJ (on line " + linecounter + "): " + br.toString() + "\r \r" + e.getMessage());
			//System.exit(0);
		}
		
		return mesh;
		
	}
}	
