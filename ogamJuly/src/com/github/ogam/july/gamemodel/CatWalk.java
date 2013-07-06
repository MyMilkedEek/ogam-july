package com.github.ogam.july.gamemodel;

import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.github.ogam.july.util.Constants;
import com.github.ogam.july.util.OgamMath;

/**
 * Catwalk contains the information about the "safe" area that can be walkable by the ship
 * @author caranha
 *
 */
public class CatWalk {
	
	Array<Vector2> originalPath; // used for drawing
	
	Array<Vector2> path;
	float pathLength;
	
	public CatWalk()
	{
		
		path = new Array<Vector2>(Vector2.class);
		originalPath = new Array<Vector2>(Vector2.class);
		
		/* Initial Catwalk (on world coordinates) */
		path.add(new Vector2(0,0));
		path.add(new Vector2(300,0));
		path.add(new Vector2(300,300));
		path.add(new Vector2(0,300));
		pathLength = 1200; // TODO: make a function to calculate this
		
		originalPath.add(new Vector2(0,0));
		originalPath.add(new Vector2(300,0));
		originalPath.add(new Vector2(300,300));
		originalPath.add(new Vector2(0,300));
		
		
	}
	
	public Vector2[] getPath()
	{
		return path.toArray();
	}
	
	public Vector2[] getOriginalPath()
	{
		return originalPath.toArray();
	}
	
	/**
	 * This is useful to snap the player ship back to the grid whenever needed.
	 * @param point: the point that we want to test
	 * @return a point in the path that is closest to the parameter point.
	 */
	public Vector2 closestPointInPath(Vector2 point)
	{
		Vector2 ret = null;
		float retdist = 0;
		int retidx = -1;
		
		// Calculating the closest path Line
		for (int i = 0; i < path.size; i++)
		{
			if (retidx == -1)
			{
				retidx = i;
				// Note: Intersector.distanceLinePoint(Vector2, Vector2, Vector2) calculates distance between segment and point, not line and point
				retdist = Intersector.distanceLinePoint(path.get(i), path.get((i+1)%path.size), point);
			}
			else
			{
				float tmpdist = Intersector.distanceLinePoint(path.get(i), path.get((i+1)%path.size), point);
				if (tmpdist < retdist)
				{
					retidx = i;
					retdist = tmpdist;
				}
			}
		}
		
		// Now that I know the closest path line, project point into that line (if necessary)
		if (retdist < Constants.EPSILON)
			return point;
		
		ret = OgamMath.projectPointInGridSeg(path.get(retidx), path.get((retidx+1)%path.size), point);
		return ret;
	}
	
	
	/** 
	 * Given a starting point and a "touch" target, calculates the list of vertices that takes 
	 * from the starting point to the target in the shortest path.
	 * 
	 * TODO: This function checks all the segments in the path 4 times. We may want to optimize this.
	 * 
	 * @param origin
	 * @param target
	 * @return
	 */
	public Array<Vector2> calculateSubGoals(Vector2 origin, Vector2 target)
	{
		Array<Vector2> calc = new Array<Vector2>(Vector2.class);
		Vector2 endPoint = closestPointInPath(target); // Possibly goes around all segments #1

		int idx_o = -1;
		int idx_e = -1;

		// 1- Calculate the segments in which point 1 and 2 are located.
		for (int i = 0; i < path.size; i++) // possibly goes around all segments #2
		{
			if (idx_o == -1)
				if (testPointInSegment(i,origin))
					idx_o = i;
			
			if (idx_e == -1)
				if (testPointInSegment(i,endPoint))
					idx_e = i;
				
			if (idx_o != -1 && idx_e != -1) // these are the droids we are looking for
				break;
		}
				
		// 1a- If both points are in the same segment, set the end point as destination and return
		if (idx_o == idx_e)
		{
			calc.add(endPoint);
			return calc;
		}
		
		// 2- Calculate the in-path distance from 1 to 2
		float distance = OgamMath.manhattanDistance(origin, path.get((idx_o+1)%path.size));
		
		int i = ((idx_o+1)%path.size);
		while (i != idx_e) // possibly goes around all segments #3
		{
			distance += OgamMath.manhattanDistance(path.get(i), path.get((i+1)%path.size));
			i = (i+1)%path.size;
		}
		distance += OgamMath.manhattanDistance(path.get(i), endPoint);

		// 3- Put all vectors in the path - order depends on the shorter distance

		i = idx_o;
		if (distance < pathLength/2) // possibly goes around all segments #4
		{
			while (i != idx_e)
			{
				i = (i+1)%path.size;
				calc.add(path.get(i));
			}
		}
		else
		{
			while (i != idx_e)
			{
				calc.add(path.get(i));
				i = (i - 1 + path.size)%path.size;
			}			
		}
		
		calc.add(endPoint);
		
		return calc;
	}
	
	
	
	/**
	 * Returns true if the point "point" is in the segment between the path points idx and idx+1.
	 * Returns false if idx is out of range;
	 * 
	 * @param idx
	 * @param point
	 * @return
	 */
	public boolean testPointInSegment(int idx, Vector2 point)
	{
		if (idx >= path.size)
			return false;
		
		Vector2 p0 = path.get(idx);
		Vector2 p1 = path.get((idx+1)%path.size);
		
		return OgamMath.isPointInSegment(p0, p1, point);
	}

}
