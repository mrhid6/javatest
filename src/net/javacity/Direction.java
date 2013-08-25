package net.javacity;

public enum Direction {

	DOWN(0,1),
	UP(0,-1),
	LEFT(-1,0),
	RIGHT(1,0),
	UNKNOWN(0,0);

	public int offsetX;
	public int offsetY;
	
	public static final Direction[] VALID_DIRECTIONS = {UP, DOWN, LEFT, RIGHT};
	public static final int[] OPPOSITES = {1, 0, 3, 2};

	private Direction(int x, int y)
	{
		offsetX = x;
		offsetY = y;
	}

	public static Direction getOrientation(int id)
	{
		if (id >= 0 && id < VALID_DIRECTIONS.length)
		{
			return VALID_DIRECTIONS[id];
		}
		return UNKNOWN;
	}

	public Direction getOpposite()
	{
		return getOrientation(OPPOSITES[ordinal()]);
	}

}
