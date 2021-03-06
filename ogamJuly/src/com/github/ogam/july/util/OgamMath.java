package com.github.ogam.july.util;

import java.util.Iterator;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

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
	
	
	/**
	 * Tests if the point is inside the polygon or not. This is an adaptation from math.Intersector.pointInPolygon
	 * FIXME: This is returning a wrong result if the point is in the NORTH or EAST line of the poligon (out, when it should be in)
	 * 
	 * @param point
	 * @param polygon
	 * @return
	 */
	public static boolean isPointInPolygon(Vector2 point, Vector2[] polygon)
	{
		int j = polygon.length - 1;
		boolean oddNodes = false;
		for (int i = 0; i < polygon.length; i++) 
		{
			if (polygon[i].y < point.y && polygon[j].y >= point.y || polygon[j].y < point.y
					&& polygon[i].y >= point.y) 
			{
				if (polygon[i].x + (point.y - polygon[i].y) / (polygon[j].y - polygon[i].y)
						* (polygon[j].x - polygon[i].x) < point.x) 
				{
					oddNodes = !oddNodes;
				}
			}
			j = i;
		}
		return oddNodes;
	}
	
	/**
	 * Tests if points a, b and c are collinear. Since this is a grid-based world, the three points 
	 * are collinear if their x coordinates or their y coordinates are the same.
	 * @param a
	 * @param b
	 * @param c
	 * @return
	 */
	public static boolean testCollinear(Vector2 a, Vector2 b, Vector2 c)
	{
		if ((a.x == b.x && a.x == c.x)||(a.y == b.y && a.y == c.y))
			return true;
		return false;
	}

	/**
	 * Calculates the area of a polygon defined by an Array of points.
	 * Adapted from gdx.math.polygon to work with GDX Arrays (silly GDX...)
	 * 
	 * @param p Array with 3 or more vector2
	 * @return Area, or -1 if Array has less than 3 vectors.
	 */
	public static float calcPolygonArea (Array<Vector2> p) {
		float area = 0;
		if (p.size < 3)
			return -1;

		Vector2 prev = p.peek();
		Vector2 cur;

		Iterator<Vector2> it = p.iterator();

		while (it.hasNext())
		{
			cur = it.next();
			area += prev.x * cur.y;
			area -= cur.x * prev.y;
			prev = cur;
		}
		
		area = Math.abs(area/2);
		
		return area;
	}

	/** 
	 * Adds the length of all the vectors in a polygon
	 * @param p
	 * @return
	 */
	public static float calcPolygonLength(Array<Vector2> p)
	{
		float ret = 0;
		Vector2 prev = p.peek();
		Vector2 curr;

		Iterator<Vector2> it = p.iterator();
		
		while (it.hasNext())
		{
			curr = it.next();
			ret += OgamMath.manhattanDistance(prev,curr);
			prev = curr;
		}
		return ret;
	}

}
