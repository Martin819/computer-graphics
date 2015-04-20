package model.objects;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import model.Triangle;

import transforms3D.Mat4;
import transforms3D.Mat4Identity;
import transforms3D.Point3D;

public abstract class RasterObject {
	
	private List<Point3D> vertices=new LinkedList<>();
	private List<Integer> indices=new LinkedList<>();
	private List<Triangle> triangles = new ArrayList<>();
	private boolean active;
    private Mat4 mat = new Mat4Identity();
    private Mat4 rotate = new Mat4Identity();
    private Mat4 position = new Mat4Identity();
    private Mat4 scale =  new Mat4Identity();
    
    private String name;
    
    public String getName(){
    	if (name.equals("")){
    		pojmenujObjekt(); 
    	} 
    	return name;
    }
    private void pojmenujObjekt(){
    	//int pocet = HlavniScena.objektyNaScene.size();
    	//setName("Teleso" + pocet);
    }
	public void computeMat(){
		Mat4 pomocna = new Mat4Identity();
		pomocna = pomocna.mul(getScale());
		pomocna = pomocna.mul(getRotate());
		pomocna = pomocna.mul(getPosition());
		setMat(pomocna);
	}
    public void setName(String name){
    	this.name = name;
    }
	public List<Point3D> getVertices() {
		return vertices;
	}
	public void setVertices(List<Point3D> vertices) {
		this.vertices = vertices;
	}
	public List<Integer> getIndices() {
		return indices;
	}
	public void setIndices(List<Integer> indices) {
		this.indices = indices;
	}
	public List<Triangle> getTriangles() {
		return triangles;
	}
	public void setTriangles(List<Triangle> triangles) {
		this.triangles = triangles;
	}
	public boolean isActive() {
		return active;
	}
	public void setActive(boolean active) {
		this.active = active;
	}
	public Mat4 getMat() {
		return mat;
	}
	public void setMat(Mat4 mat) {
		this.mat = mat;
	}
	public Mat4 getRotate() {
		return rotate;
	}
	public void setRotate(Mat4 rotate) {
		this.rotate = rotate;
		this.computeMat();
	}
	public Mat4 getPosition() {
		return position;
	}
	public void setPosition(Mat4 position) {
		this.position = position;
		this.computeMat();
	}
	public Mat4 getScale() {
		return scale;
	}
	public void setScale(Mat4 scale) {
		this.scale = scale;
		this.computeMat();
	}
	

}
