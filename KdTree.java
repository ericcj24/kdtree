package com.algorithms.week5;

import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.Queue;
import edu.princeton.cs.algs4.RectHV;
import edu.princeton.cs.algs4.StdDraw;
import edu.princeton.cs.algs4.StdOut;

public class KdTree {

	// vertical, compare x
	private static final boolean RED = true;
	// horizontal, compare y
	private static final boolean BLUE = false;

	private KdTreeNode root;
	private int size;

	public KdTree() {
		root = null;
		size = 0;
	}

	// is the set empty?
	public boolean isEmpty() {
		return root == null;
	}

	// number of points in the set
	public int size() {
		return this.size;
	}

	// add the point to the set (if it is not already in the set)
	public void insert(Point2D p) {
		if (p == null) {
			throw new java.lang.IllegalArgumentException();
		}

		if (contains(p) ) {
			return;
		}

		// first node
		if (root == null) {
			root = new KdTreeNode(p, RED, null, null);
			size++;
			return;
		}

		insert(root, p);
	}

	// assume, there is no p exist in tree
	private void insert(KdTreeNode node, Point2D p) {
		// compare horizontal
		if (node.color==RED) {
			if ( Double.compare(p.x(), node.point.x()) <0 ) {
				//smaller, go left
				if (node.left == null) {
					KdTreeNode newNode = new KdTreeNode(p, BLUE, null, null);
					node.left = newNode;
					size++;
					return;
				} else {
					insert(node.left, p);
				}

			} else {
				// larger, or equal go right
				if (node.right == null) {
					KdTreeNode newNode = new KdTreeNode(p, BLUE, null, null);
					node.right = newNode;
					size++;
					return;
				} else {
					insert(node.right, p);
				}
			}
		} else {
			// compare vertical
			if (Double.compare(p.y(), node.point.y()) < 0) {
				//smaller, go left
				if (node.left == null) {
					KdTreeNode newNode = new KdTreeNode(p, RED, null, null);
					node.left = newNode;
					size++;
					return;
				} else {
					insert(node.left, p);
				}
			} else {
				// larger, or equal, go right
				if (node.right == null) {
					KdTreeNode newNode = new KdTreeNode(p, RED, null, null);
					node.right = newNode;
					size++;
					return;
				} else {
					insert(node.right, p);
				}
			}
		}
	}

	// all points that are inside the rectangle (or on the boundary)
	public Iterable<Point2D> range(RectHV rect) {
		if (rect == null) {
			throw new java.lang.IllegalArgumentException();
		}


		return new RangeIterable(rect);
	}

	private class RangeIterable implements Iterable<Point2D> {
		private Set<Point2D> rangeSet;
		public RangeIterable(RectHV rect) {
			this.rangeSet = new TreeSet<>();

			if (root != null && rect.height() >= 0 && rect.width() >= 0) {
				RectHV wholeRect = new RectHV(0, 0, 1, 1);
				rangeSearch(wholeRect, root, rect);
			}
		}

		/**
		 *
		 * @param currentRect current rect before current node divide it
		 * @param node NotNull
		 * */
		private void rangeSearch(RectHV currentRect, KdTreeNode node, RectHV rect) {
			if (rect.contains(node.point)) {
				rangeSet.add(node.point);
			}

			if (node.color == RED) {
				// compare horizontal, x
				RectHV leftRect = new RectHV(currentRect.xmin(), currentRect.ymin(), node.point.x(), currentRect.ymax());
				RectHV rightRect = new RectHV(node.point.x(), currentRect.ymin(), currentRect.xmax(), currentRect.ymax());

				if (leftRect.intersects(rect)) {
					if (node.left != null) {
						rangeSearch(leftRect, node.left, rect);
					}
				}

				if (rightRect.intersects(rect)){
					if (node.right != null) {
						rangeSearch(rightRect, node.right, rect);
					}
				}
			} else {
				// compare vertical, y
				RectHV leftRect = new RectHV(currentRect.xmin(), currentRect.ymin(), currentRect.xmax(), node.point.y());
				RectHV rightRect = new RectHV(currentRect.xmin(), node.point.y(), currentRect.xmax(), currentRect.ymax());
				if (leftRect.intersects(rect)) {
					if (node.left != null) {
						rangeSearch(leftRect, node.left, rect);
					}
				}

				if (rightRect.intersects(rect)){
					if (node.right != null) {
						rangeSearch(rightRect, node.right, rect);
					}
				}
			}
		}

		@Override
		public Iterator<Point2D> iterator() {
			return rangeSet.iterator();
		}

	}



	// a nearest neighbor in the set to point p; null if the set is empty
	public Point2D nearest(Point2D query) {
		if (query == null) {
			throw new java.lang.IllegalArgumentException();
		}

		if (root == null) {
			return null;
		}

		this.minDistSq = Double.POSITIVE_INFINITY;
		this.nearestPoint = null;

		recursiveFindNearest(query, root);

		return nearestPoint;
	}

	private Point2D nearestPoint;
	private double minDistSq;

	private void recursiveFindNearest(Point2D query, KdTreeNode node) {
		double tempDist = query.distanceSquaredTo(node.point);
		if (Double.compare(tempDist, 0) == 0) {
			// 0 dist, this is the one
			minDistSq = tempDist;
			nearestPoint = node.point;
			return;
		}

		if (Double.compare(tempDist, minDistSq) < 0) {
			minDistSq = tempDist;
			nearestPoint = node.point;
		}

		// compare x
		if (smaller(query, node.point, node.color) ) {
			if (node.left != null) {
				recursiveFindNearest(query, node.left);
			}

			// if i did not improve
			double distToLine = distToLine(node.point, query, node.color);
			if (Double.compare(minDistSq, distToLine*distToLine) > 0) {
				if (node.right != null) {
					recursiveFindNearest(query, node.right);
				}
			}


		} else {
			if (node.right != null) {
				recursiveFindNearest(query, node.right);
			}

			// if i did not improve
			double distToLine = distToLine(node.point, query, node.color);
			if (Double.compare(minDistSq, distToLine*distToLine) > 0) {
				if (node.left != null) {
					recursiveFindNearest(query, node.left);
				}
			}
		}

	}


	private double distToLine(Point2D point, Point2D query, boolean color) {
		if (color == RED) {
			return Math.abs(point.x() - query.x());
		} else {
			return Math.abs(point.y() - query.y());
		}

	}

	private boolean smaller(Point2D p1, Point2D p2, boolean color) {
		if (color == RED) {
			return Double.compare(p1.x(), p2.x()) < 0;
		} else {
			return Double.compare(p1.y(), p2.y()) < 0;
		}

	}

	// does the set contain point p?
	public boolean contains(Point2D p) {
		if (p == null) {
			throw new java.lang.IllegalArgumentException();
		}

		return seek(p) != null;
	}

	//if the point to be inserted has a smaller x-coordinate than the point at the root, go left; otherwise go right
	//if the point to be inserted has a smaller y-coordinate than the point in the node, go left; otherwise go right
	private KdTreeNode seek(Point2D p) {

		KdTreeNode runner = this.root;
		while (runner!=null) {

			if ( smaller(p, runner.point, runner.color)) {
				//smaller, go left
				runner = runner.left;
			} else if (Double.compare(p.x(), runner.point.x()) ==0 && Double.compare(p.y(), runner.point.y()) == 0) {
				return runner;
			} else {
				// larger, or equal go right
				runner = runner.right;
			}
		}

		return null;
	}

	// draw all points to standard draw
	public void draw() {
		for (KdTreeNode p : levelOrderTraverseIterable()) {
			/*if (p.color==RED) {
				StdDraw.setPenColor(StdDraw.BLACK);
				StdDraw.setPenRadius(0.01);
				p.point.draw();
				StdDraw.setPenColor(StdDraw.RED);
				StdDraw.setPenRadius(0.001);
				StdDraw.line(p.point.x(), 0, p.point.x(), 1);
			} else {
				StdDraw.setPenColor(StdDraw.BLACK);
				StdDraw.setPenRadius(0.01);
				p.point.draw();
				StdDraw.setPenColor(StdDraw.BLUE);
				StdDraw.setPenRadius(0.001);
				StdDraw.line(0, p.point.y(), 1, p.point.y());
			}*/

			p.point.draw();
		}

	}

	private Iterable<KdTreeNode> levelOrderTraverseIterable() {
		return new LevelOrderIterable();
	}

	private final class LevelOrderIterable implements Iterable<KdTreeNode> {
		@Override
		public Iterator<KdTreeNode> iterator() {
			return new LevelOrderIterator();
		}
	}
	private final class LevelOrderIterator implements Iterator<KdTreeNode> {

		private Queue<KdTreeNode> points;
		public LevelOrderIterator() {
			points = new Queue<>();

			levelOrderTraverse(root);
		}

		private void levelOrderTraverse(KdTreeNode node) {
			points.enqueue(node);
			if (node.left != null) {
				levelOrderTraverse(node.left);
			}
			if (node.right != null) {
				levelOrderTraverse(node.right);
			}
			return;
		}

		@Override
		public boolean hasNext() {
			return !points.isEmpty();
		}

		@Override
		public KdTreeNode next() {
			return points.dequeue();
		}
	}

	private class KdTreeNode {

		private final Point2D point;
		private final boolean color;
		private KdTreeNode left;
		private KdTreeNode right;

		public KdTreeNode(Point2D p, boolean color, KdTreeNode left, KdTreeNode right) {
			this.point = p;
			this.color = color;
			this.left = left;
			this.right= right;
		}
	}



    public static void main(String[] args) {

        // initialize the two data structures with point from file
        String filename = args[0];
        In in = new In(filename);
        PointSET brute = new PointSET();
        KdTree kdtree = new KdTree();
        while (!in.isEmpty()) {
            double x = in.readDouble();
            double y = in.readDouble();
            Point2D p = new Point2D(x, y);
            kdtree.insert(p);
            brute.insert(p);
        }


        for (KdTreeNode p : kdtree.levelOrderTraverseIterable()) {
        		//StdOut.println(p.point);
        		if (!kdtree.contains(p.point)) {
        			StdOut.println("error:" + p.point);
        		}
        }

        StdDraw.enableDoubleBuffering();

        RectHV rect = new RectHV(0, 0, 1, 1);

        StdDraw.setPenRadius(.02);
        StdDraw.setPenColor(StdDraw.BLUE);
        //for (Point2D p : kdtree.range(rect))
          //  p.draw();

        Point2D xp = new Point2D(0.9, 0.5);
        xp.draw();
        kdtree.nearest(xp).draw();

        StdDraw.setPenRadius(0.01);
        StdDraw.setPenColor(StdDraw.RED);
        kdtree.draw();
        StdDraw.show();
    }

}
