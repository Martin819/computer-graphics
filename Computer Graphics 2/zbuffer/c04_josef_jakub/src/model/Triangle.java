package model;

import java.awt.Color;

import transforms3D.Point3D;
import transforms3D.Point3DColor;

public class Triangle {
	private Point3D a;
	private Point3D b;
	private Point3D c;
	public boolean isInterpolated = true;
	
	public boolean isInterpolated() {
		return isInterpolated;
	}
	public void setInterpolated(boolean isInterpolated) {
		this.isInterpolated = isInterpolated;
	}
	public Triangle(Point3DColor a, Point3DColor b, Point3DColor c){
		this.a = new Point3D(a.x,a.y,a.z,a.w);
		this.b = new Point3D(b.x,b.y,b.z,b.w);
		this.c = new Point3D(c.x,c.y,c.z,c.w);
		
		
	}
	public Triangle(Point3D a, Point3D b, Point3D c, Color barva){
		this.a = new Point3D(a.x,a.y,a.z,a.w);
		this.b = new Point3D(b.x,b.y,b.z,b.w);
		this.c = new Point3D(c.x,c.y,c.z,c.w);
	}

	public Point3D getA() {
		return new Point3D(a.x,a.y,a.z,a.w);
	}
	public Point3D getB() {
		return new Point3D(b.x,b.y,b.z,b.w);
	}
	public Point3D getC() {
		return new Point3D(c.x,c.y,c.z,c.w);
	}
}
