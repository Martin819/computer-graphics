package model.objects;

import java.awt.Color;
import java.util.LinkedList;
import java.util.List;
import model.Triangle;
import transforms3D.Point3D;

public class Krychle extends RasterObject {
	public Krychle(Color color,String name) {
		List<Point3D> vertices = new LinkedList<>();
		List<Integer> indices = new LinkedList<>();
		List<Triangle> triangles = new LinkedList<>();
		vertices.add(new Point3D(0,0,0)); //A - 0
		vertices.add(new Point3D(0,1,0)); //B - 1
		vertices.add(new Point3D(1,1,0)); //C - 2
		vertices.add(new Point3D(1,0,0)); //D - 3
			
		vertices.add(new Point3D(0,0,1)); //E - 4
		vertices.add(new Point3D(0,1,1)); //F - 5
		vertices.add(new Point3D(1,1,1)); //G - 6
		vertices.add(new Point3D(1,0,1)); //H - 7
		
		indices.add(0);indices.add(1);
		indices.add(1);indices.add(2);
		indices.add(2);indices.add(3);
		indices.add(3);indices.add(0);
		
		indices.add(4);indices.add(5);
		indices.add(5);indices.add(6);
		indices.add(6);indices.add(7);
		indices.add(7);indices.add(4);
		
		indices.add(0);indices.add(4);
		indices.add(1);indices.add(5);
		indices.add(2);indices.add(6);
		indices.add(3);indices.add(7);
		
		triangles.add(new Triangle(vertices.get(4), vertices.get(5), vertices.get(6), color));
		triangles.add(new Triangle(vertices.get(4), vertices.get(7), vertices.get(6), color));
		triangles.add(new Triangle(vertices.get(0), vertices.get(1), vertices.get(2), color));
		triangles.add(new Triangle(vertices.get(0), vertices.get(3), vertices.get(2), color));
		triangles.add(new Triangle(vertices.get(0), vertices.get(1), vertices.get(5), color));
		triangles.add(new Triangle(vertices.get(0), vertices.get(4), vertices.get(5), color));
		triangles.add(new Triangle(vertices.get(3), vertices.get(2), vertices.get(6), color));
		triangles.add(new Triangle(vertices.get(3), vertices.get(7), vertices.get(6), color));
		triangles.add(new Triangle(vertices.get(1), vertices.get(2), vertices.get(6), color));
		triangles.add(new Triangle(vertices.get(1), vertices.get(5), vertices.get(6), color));
		triangles.add(new Triangle(vertices.get(0), vertices.get(3), vertices.get(7), color));
		triangles.add(new Triangle(vertices.get(0), vertices.get(4), vertices.get(7), color));
		
		setName(name);
		setVertices(vertices);
		setIndices(indices);
		setTriangles(triangles);
	}
}
