package com.github.ogam.july.util;

import com.badlogic.gdx.math.Vector2;

/**
 * Static class with maths particular to this game
 * @author caranha
 *
 */
public class OgamMath {
	
	/**
	 * Returns the projection of point in the grid segment delimited by start and end.
	 * This function uses the fact that a grid segment is aligned to one of the axis 
	 * to make calculations simpler.
	 * 
	 * @param start
	 * @param end
	 * @param point
	 * @return
	 */
	public static Vector2 projectPointInGridSeg(Vector2 start, Vector2 end, Vector2 point)
	{
		Vector2 ret = new Vector2();
		Vector2 small, big;
		
		if (start.x < end.x || start.y < end.y)
		{
			small = start;
			big = end;
		}
		else
		{ 
			small = end;
			big = start;
		}
		
		if (small.x == big.x) // vertical case
		{
			ret.x = small.x;
			ret.y = point.y;
			if (ret.y < small.y)
				ret.y = small.y;
			if (ret.y > big.y)
				ret.y = big.y;
		}
		else // horizontal case
		{
			ret.y = small.y;
			ret.x = point.x;
			if (ret.x < small.x)
				ret.x = small.x;
			if (ret.x > big.x)
				ret.x = big.x;			
		}
		
		return ret;
	}
	
	/**
	 * Calculates if the distance between each coordinate of point is equal or less the the distance between 
	 * the end points: Since the line is vertical or horizontal, this should determine that the point is located 
	 * in the segment.
	 * 
	 * @param start
	 * @param end
	 * @param point
	 * @return
	 */
	public static boolean isPointInSegment(Vector2 start, Vector2 end, Vector2 point)
	{
		if ((Math.abs(point.x - start.x) + Math.abs(point.x - end.x) <= Math.abs(start.x - end.x) + Constants.EPSILON)&&
				(Math.abs(point.y - start.y) + Math.abs(point.y - end.y) <= Math.abs(start.y - end.y) + Constants.EPSILON))
			return true;
		else
			return false;
	}
	
	/**
	 * Calculates the manhattan points between two points. If the points are colinear (they often are in a grid) this 
	 * calculates the actual distance without the need for multiplications.
	 * 
	 * @param start
	 * @param end
	 * @return
	 */
	public static float manhattanDistance(Vector2 start, Vector2 end)
	{
		return((Math.abs(start.x - end.x) + Math.abs(start.y - end.y)));
	}
	
}
