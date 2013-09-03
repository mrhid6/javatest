package net.javacity.lib.managers;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import net.javacity.animation.AnimationTime;

public class AssetManager {

	public int assets_loaded = 0;
	public static boolean assetsLoaded = false;
	
	private static ZipFile zipFile;
	private static ZipEntry zipEntry;

	public static void loadAssets() throws Exception{
		readMainZip();
	}

	private static void readMainZip() throws Exception{
		File assetsFile = new File("res/core/assets.zip");

		if(assetsFile.exists()){
			zipFile = new ZipFile(assetsFile);
			Enumeration<? extends ZipEntry> entries = zipFile.entries();

			while (entries.hasMoreElements()) {
				zipEntry = entries.nextElement();
				
				if (!zipEntry.isDirectory()) {
					
					final String fileName = zipEntry.getName();
					System.out.println(fileName);
					
					if (fileName.endsWith(".info")) {
						if(fileName.startsWith("animations")){
							readAnimations();
						}
					}
				}
			}
			zipFile.close();
			assetsLoaded = true;
		}else{
			redownloadAsssets();
			assetsLoaded = false;
		}
	}

	public static void redownloadAsssets(){
		System.out.println("asset.zip - not found redownloadin!");
	}

	public void numAssets(){
		System.out.println(assets_loaded);
	}
	
	public static void readAnimations() throws IOException{
		InputStream in = zipFile.getInputStream(zipEntry);
		
		BufferedReader br = new BufferedReader(new InputStreamReader(in, "UTF-8"));
		String line;
		
		while((line=br.readLine())!=null){
			if(!line.isEmpty()){
				String[] data = line.split(",");
				
				String key = data[0];
				float start = Float.parseFloat(data[1]);
				float end = Float.parseFloat(data[2]);
				float time = Float.parseFloat(data[3]);
				
				AnimationTime antime = new AnimationTime(start, end, time);
				Animations.addAnimation(key, antime);
				
				System.out.println(key+" "+antime.toString());
			}
		}
	}
	
	public static float calcStartTime(float s, float t){
		return t*s;
	}

}
