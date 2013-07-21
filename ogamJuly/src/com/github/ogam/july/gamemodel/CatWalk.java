package com.github.ogam.july.gamemodel;

import java.util.Iterator;

import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.github.ogam.july.util.Constants;
import com.github.ogam.july.util.OgamMath;

/**
 * Catwalk contains the information about the "safe" area that can be walkable by the ship
 * The internal values of this class are ugly and I should be ashamed of them.
 * 
 * @author caranha
 *
 */
public class CatWalk {

	Array<Vector2> p1; // replacement candidates
	Array<Vector2> p2;

	Array<Vector2> originalPath; // used for drawing
	float originalLength;
	float originalArea;
	
	Array<Vector2> path;
	float pathLength;
	float pathArea;
	
	public CatWalk()
	{
		path = new Array<Vector2>(Vector2.class);
		p1 = new Array<Vector2>(Vector2.class); // replacement candidates
		p2 = new Array<Vector2>(Vector2.class);
		
		
		originalPath = new Array<Vector2>(Vector2.class);		
		originalPath.add(new Vector2(0,0));
		originalPath.add(new Vector2(300,0));
		originalPath.add(new Vector2(300,300));
		originalPath.add(new Vector2(0,300));
		
		originalLength = OgamMath.calcPolygonLength(originalPath);
		originalArea = OgamMath.calcPolygonArea(originalPath);
		
		setPath(originalPath); // sets the current path as the original, and initializes lenght and area as well;		
	}
	
	public void update(float delta, LevelContext lc)
	{
		if (p1.size > 0 || p2.size > 0)
			setPath(selectReplacement(lc));
		p1.clear();
		p2.clear();
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
	 * Finds an intersection between the start and end and the path. Assumes that this segment crosses the path.
	 * 
	 * @param start assumed to be inside the path
	 * @param end assumed to be outside the path
	 * @return
	 */
	public Vector2 intersectionInPath(Vector2 start, Vector2 end)
	{
		Vector2 ret = new Vector2();
		int j = path.size - 1;
		for (int i = 0; i < path.size; i++)
		{
			// FIXME: North/East collisions are not being correctly tested
			if (Intersector.intersectSegments(start, end, path.get(j), path.get(i), ret))
				return ret;
			j = i;
		}
		return null;
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
	 * This vector stores a series of cuts for eventual cutting of the catwalk at the next update cycle
	 * 
	 * FIXME: hard to reproduce bug where one node from the path won't be added exists.
	 * 
	 * @param cutline
	 */
	public void pushCut(Array<Vector2> cutline)
	{
		// FIXME: if the cutline has size 2, do an extra test to see if the line is not within the catwalk. 
		// This is not necessary if the bug at "OgamMath.isPointInPolygon" is fixed.		
		if (cutline.size == 2)
			for (int i = 1; i < path.size; i++)
			{
				if ((OgamMath.isPointInSegment(path.get(i-1), path.get(i), cutline.first()))
						&&(OgamMath.isPointInSegment(path.get(i-1), path.get(i), cutline.peek())))
				{
					System.out.println("fail");
					return;						
				}
			}
		
		
		int startidx = -1, endidx = -1; // these are the start index for the path segment where the startpoint and end points are located;
		for (int i = 0; i < path.size; i++)
		{
			if (startidx != -1 && endidx != -1)
				break;
			if (startidx == -1 && OgamMath.isPointInSegment(path.get(i), path.get((i+1)%path.size),cutline.first()))
				startidx = i;
			if (endidx == -1 && OgamMath.isPointInSegment(path.get(i), path.get((i+1)%path.size),cutline.peek()))
				endidx = i;
		}
		
		
		// creating paths:
		p1.clear();
		p2.clear();
		
		// Adding nodes from main path
		int tmpidx;
		if (endidx == startidx) // special case, if both endpoints are in the same segment - only one gets the entire path
		{
			float enddist = OgamMath.manhattanDistance(path.get(endidx), cutline.peek());
			float startdist = OgamMath.manhattanDistance(path.get(startidx),cutline.first());
			
			Array<Vector2> tp;
			if (startdist < enddist) // start points come first
				tp = p1;
			else 
				tp = p2;

			for (int i = 0; i < path.size; i++)
			{
				tp.add(new Vector2(path.get((i + endidx + 1)%path.size)));
			}			
			
			
			// Adding nodes from cut:
			// TODO: I don't quite understand why the order must be reversed if start and end are in the same segment.
			// Study this black magic!
			for (int i = 0; i < cutline.size; i++)
				p1.add(new Vector2(cutline.get(i)));
			
			while (cutline.size > 0) // p2 nodes are added in reverse -- from end to start
				p2.add(cutline.pop());
		}
		else // both endpoints are in different segments: regular case
		{
			tmpidx = (endidx + 1)%path.size;
			while (tmpidx != (startidx + 1)%path.size)
			{
				p2.add(new Vector2(path.get(tmpidx)));
				tmpidx = (tmpidx + 1)%path.size;
			}
			
			tmpidx = (startidx + 1)%path.size;
			while (tmpidx != (endidx + 1)%path.size)
			{
				p1.add(new Vector2(path.get(tmpidx)));
				tmpidx = (tmpidx + 1)%path.size;
			}
			
			
			// Adding nodes from cut:
			for (int i = 0; i < cutline.size; i++)
				p2.add(new Vector2(cutline.get(i)));
			
			while (cutline.size > 0) // p1 nodes are added in reverse -- from end to start
				p1.add(cutline.pop());
		}
		
	}
	
	/**
	 * Selects a replacement path from p1 and p2 for this catwalk.
	 * Selection is made by the path with the highest enemy weight. If both sides have the same enemy weight, 
	 * smallest area wins.
	 * @param lc
	 * @return
	 */
	private Array<Vector2> selectReplacement(LevelContext lc)
	{
		Array<Vector2> winner = null;
		Array<Enemy> e1 = new Array<Enemy>(); // enemies in p1
		Array<Enemy> e2 = new Array<Enemy>(); // enemies in p2
		
		int p1weight = 0;
		int p2weight = 0;
		boolean p1won;
		
		// TODO: this is inneficient if we have too many enemies
		Iterator<Enemy> it = lc.enemylist.iterator();
		while (it.hasNext()) // testing each enemy to see if he is inside p1 or p2
		{
			Enemy tmp = it.next();
			if (tmp.getWeight() != 0) // enemies with weight 0 don't matter, no point in calcing them.
			{
				if (OgamMath.isPointInPolygon(tmp.getPos(), p1.toArray()))
				{
					e1.add(tmp);
					p1weight+= tmp.getWeight();
				}
				else // if he is not inside p1, he must be inside p2 (not really, but it should work)
				{
					e2.add(tmp);
					p2weight+= tmp.getWeight();
				}			
			}
		}
		
		if (p1weight == p2weight) // if the weights are the same, the largest area is zoned out (loses)
			p1won = (OgamMath.calcPolygonArea(p1) < OgamMath.calcPolygonArea(p2));
		else // weights are different, smallest weight is zoned out.
			p1won = (p1weight > p2weight);
		
		// sending signals to enemies in the areas.
		it = e1.iterator();
		while (it.hasNext())
			it.next().doZoningSignal(!p1won);
		it = e2.iterator();
		while (it.hasNext())
			it.next().doZoningSignal(p1won);
		
		if (p1won) // define which area does not get zoned out.
			return p1;
		else
			return p2;
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
	
	/**
	 * Replace the current path in the Catwalk with the vector passed here.
	 * @param p
	 */
	private void setPath(Array<Vector2> p)
	{
		path.clear();
		// copy all the points that are not collinear
		for (int i = 0; i < p.size; i++)
		{
			if (!OgamMath.testCollinear(p.get((i-1+p.size)%p.size),p.get(i),p.get((i+1)%p.size)))
				path.add(new Vector2(p.get(i)));
		}
		
		pathLength = OgamMath.calcPolygonLength(path);
		pathArea = OgamMath.calcPolygonArea(path);
	}
	
	
	
}
