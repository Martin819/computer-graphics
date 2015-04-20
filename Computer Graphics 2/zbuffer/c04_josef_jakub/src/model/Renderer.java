package model;

import gui.Raster;
import gui.Scene;
import java.awt.Point;
import model.objects.RasterObject;
import transforms3D.Mat4RotX;
import transforms3D.Mat4RotXYZ;
import transforms3D.Mat4RotY;
import transforms3D.Mat4RotZ;
import transforms3D.Mat4Scale;
import transforms3D.Mat4Transl;
import transforms3D.Point3D;

public class Renderer {
	private GPU gpu;
	private Raster rastr;
	private Scene scene;
	public Renderer(Raster rastr) {
		this.rastr=rastr;
	}
	
	public void initGPU(Scene scene) {
		gpu = new GPU(this.rastr,scene);
		this.scene = scene;
	}
	
	public void move(RasterObject object, double x, double y) {
		rastr.reset();
		object.setPosition(object.getPosition().mul(new Mat4Transl(x  ,  y  , 0)));
		scene.redrawScene();
	}
	public void rotate(RasterObject object, Point3D degree) {
	    rastr.reset();
		object.setRotate(object.getRotate().mul(new Mat4RotXYZ(Math.toRadians(degree.x), Math.toRadians(degree.y), Math.toRadians(degree.z))));//nastaveni telesu samotnemu otaceci matici
		scene.redrawScene();
	}

	public void rotateZ(RasterObject object, Point3D degree) {
		rastr.reset();
		object.setRotate(object.getRotate().mul(new Mat4RotZ(degree.z)));//nastaveni telesu samotnemu otaceci matici
		scene.redrawScene();
	}
	public void rotateX(RasterObject object, Point degree) {
		rastr.reset();
		object.setRotate(object.getRotate().mul(new Mat4RotX(Math.toRadians(degree.x))));//nastaveni telesu samotnemu otaceci matici
		scene.redrawScene();
	}
	public void rotateY(RasterObject object, Point degree) {
		rastr.reset();
		object.setRotate(object.getRotate().mul(new Mat4RotY(Math.toRadians(degree.y))));
		scene.redrawScene();
	}

	public void scale(RasterObject object, double zoom) {
		rastr.reset();
		object.setScale(object.getScale().mul(new Mat4Scale(zoom, zoom, zoom)));
		scene.redrawScene();
	}

	public GPU getGPU(){
		return gpu;
	}
	public Raster getRaster(){
		return rastr;
	}
}
