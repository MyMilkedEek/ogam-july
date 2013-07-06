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
	
	public CatWalk()
	{
		
		path = new Array<Vector2>(Vector2.class);
		originalPath = new Array<Vector2>(Vector2.class);
		
		/* Initial Catwalk (on world coordinates) */
		path.add(new Vector2(0,0));
		path.add(new Vector2(300,0));
		path.add(new Vector2(300,300));
		path.add(new Vector2(0,300));
		
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
	
	
	public boolean insidePath(Vector2 point)
	{
		// TODO: Transform the code below (ray algorithm) to this data structure
		
		// Code from LibGdx "math.Polygon"
//		final float[] vertices = getTransformedVertices();
//		final int numFloats = vertices.length;
//		int intersects = 0;
//
//		for (int i = 0; i < numFloats; i += 2) {
//		float x1 = vertices[i];
//		float y1 = vertices[i + 1];
//		float x2 = vertices[(i + 2) % numFloats];
//		float y2 = vertices[(i + 3) % numFloats];
//		if (((y1 <= y && y < y2) || (y2 <= y && y < y1)) && x < ((x2 - x1) / (y2 - y1) * (y - y1) + x1)) intersects++;
//		}
//		return (intersects & 1) == 1;	

		return false;
	}
	

}
