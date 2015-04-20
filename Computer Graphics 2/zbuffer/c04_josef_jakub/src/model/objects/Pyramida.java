package model.objects;

import java.awt.Color;
import java.util.LinkedList;
import java.util.List;

import model.Triangle;
import transforms3D.Point3D;

public class Pyramida extends RasterObject {
public Pyramida(Color color,String name) {
	List<Point3D> vertices = new LinkedList<>();
	List<Integer> indices = new LinkedList<>();
	List<Triangle> triangles = new LinkedList<>();
	
	vertices.add(new Point3D(0.5,0.5,1));
	vertices.add(new Point3D(1,0,0));
	vertices.add(new Point3D(1,1,0));
	vertices.add(new Point3D(0,1,0));
	vertices.add(new Point3D(0,0,0));
	indices.add(0);
	indices.add(1);
	
	indices.add(1);
	indices.add(2);
	indices.add(2);
	indices.add(3);
	indices.add(3);
	indices.add(4);
	indices.add(4);
	indices.add(1);
	indices.add(0);
	indices.add(2);
	indices.add(0);
	indices.add(3);
	indices.add(0);
	indices.add(4);
	//ABC + ACD
	triangles.add(new Triangle(new Point3D(vertices.get(0)), vertices.get(1), vertices.get(2), color));
	triangles.add(new Triangle(new Point3D(vertices.get(0)), vertices.get(2), vertices.get(3), color));
	//ABE
	triangles.add(new Triangle(new Point3D(vertices.get(0)), vertices.get(1), vertices.get(4), color));
	//triangles.add(new Triangle(new Point3D(vertices.get(1)), vertices.get(5), vertices.get(6), color));
	//BCE
	//triangles.add(new Triangle(new Point3D(vertices.get(0)), vertices.get(1), vertices.get(6), color));
	triangles.add(new Triangle(new Point3D(vertices.get(1)), vertices.get(2), vertices.get(4), color));
	//CDE
	triangles.add(new Triangle(new Point3D(vertices.get(2)), vertices.get(3), vertices.get(4), color));
	
	//ADE
	triangles.add(new Triangle(new Point3D(vertices.get(0)), vertices.get(3), vertices.get(4), color));
	
	setName(name);
	setVertices(vertices);
	setIndices(indices);
	setTriangles(triangles);
	
}
}
