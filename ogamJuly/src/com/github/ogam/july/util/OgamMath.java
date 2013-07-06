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
	
	
}
