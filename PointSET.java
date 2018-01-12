package com.algorithms.week5;

import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.RectHV;

public class PointSET {

	private Set<Point2D> pointSet;

	public PointSET() {
		// construct an empty set of points
		pointSet = new TreeSet<>();
	}

	public boolean isEmpty() {
		// is the set empty?
		return pointSet.isEmpty();
	}


	public int size() {
		// number of points in the set
		return pointSet.size();
	}

	public void insert(Point2D p) {
		// add the point to the set (if it is not already in the set)
		if (p == null) {
			throw new java.lang.IllegalArgumentException();
		}

		if (!contains(p)) {
			pointSet.add(p);
		}
	}

	public boolean contains(Point2D p) {
		// does the set contain point p?
		if (p == null) {
			throw new java.lang.IllegalArgumentException();
		}
		return pointSet.contains(p);
	}

	public void draw() {
		// draw all points to standard draw
		if (isEmpty()) {
			return;
		}

		for (Point2D q : pointSet) {
			q.draw();
		}
	}

	public Iterable<Point2D> range(RectHV rect) {
		// all points that are inside the rectangle (or on the boundary)
		if (rect == null) {
			throw new java.lang.IllegalArgumentException();
		}

		return new RangeIterable(rect);
	}

	private class RangeIterable implements Iterable<Point2D> {

		private Set<Point2D> containedPoints;

		public RangeIterable(RectHV rect) {
			containedPoints = new TreeSet<>();

			for (Point2D q : pointSet) {
				if (rect.contains(q)) {
					containedPoints.add(q);
				}
			}
		}

		@Override
		public Iterator<Point2D> iterator() {
			return containedPoints.iterator();
		}
	}



	public Point2D nearest(Point2D p) {
		// a nearest neighbor in the set to point p; null if the set is empty
		if (p == null) {
			throw new java.lang.IllegalArgumentException();
		}

		if (isEmpty()) {
			return null;
		}

		double tempMinDist = Double.POSITIVE_INFINITY;
		Point2D tempMinPoint = null;
		for (Point2D q : pointSet) {
			double distpq = q.distanceTo(p);
			if (Double.compare(distpq, tempMinDist) <= 0) {
				tempMinPoint = q;
				tempMinDist = distpq;
			}
		}
		return tempMinPoint;
	}


	public static void main(String[] args) {
		// unit testing of the methods (optional)
	}
}
