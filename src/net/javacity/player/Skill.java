package net.javacity.player;

public class Skill {
	
	private int playerLevel;
	private double exp;
	
	public Skill(int lvl) {
		playerLevel = lvl;
		exp = getExperence(lvl);
	}
	
	public Skill(int lvl, int exp) {
		playerLevel = lvl;
		this.exp = exp;
	}

	private double getExperence(int lvl) {
		 double a=0;
		  for(float i=1; i<lvl; i++) {
		    a += Math.floor(i+300*Math.pow(2, (i/7)));
		  }
		  return Math.floor(a/4);
	}
	
	public int getLevel(double exp){
	  int level = 1;
	  
      while (getExperence(level) < exp)
          level++;

      return level;
      
	}
	
	public int getLevel(){
		return playerLevel;
	}
	
	public double getExp() {
		return exp;
	}
	
}
