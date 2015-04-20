package gui;

import java.util.LinkedList;
import java.util.List;

import transforms3D.Camera;
import transforms3D.Mat4;
import transforms3D.Vec3D;

import model.GPU.DrawingMethod;
import model.Renderer;
import model.objects.RasterObject;

public class Scene {
	public RasterObject actualObject = null;
	private List<RasterObject> objects = new LinkedList<>();
	private Camera view = new Camera();
	private Renderer renderer = null;
	private Mat4 projection = null;
	private boolean firstPerson = true;
	//konstanty pro pohyb kamery
	public static final int CAMERA_LEFT = 0;
	public static final int CAMERA_RIGHT = 1;
	public static final int CAMERA_UP = 2;
	public static final int CAMERA_DOWN = 3;
	private final double CAMERA_SPEED = 0.05;
	public Scene() {
		view.setAzimuth(-2.3);
		view.setZenith(-0.3);
		view.setRadius(180);
		view.setFirstPerson(false);
		view.setPosition(new Vec3D(0,0,0));;
	}
	public Scene(Renderer renderer){
		this.renderer = renderer;
		view.setAzimuth(-2.3);
		view.setZenith(-0.3);
		view.setFirstPerson(false);
		view.setPosition(new Vec3D(0,0,0));
	}
	/**
	 * Prekresli celou scenu
	 */
	public void redrawScene(){
		renderer.getRaster().reset();
		for (RasterObject object : objects) {
			if(object.isActive()){
				renderer.getGPU().redraw(object);
			}
		}
	}
	 
	/**
	 * Prida object do sceny
	 * @param object
	 */
	public void addObject(RasterObject object){
		try{
			objects.add(object);
		}catch(Exception e){
			System.out.println("Nepodarilo se pridat objekt do sceny");
		}
	}
	/**
	 * Odebere object ze sceny podle jeho jmena
	 * @param object
	 */
	public void removeObject(String objectName){
		for (RasterObject object : objects) {
			if(object.getName().equals(objectName)){
				this.removeObject(object);
			}
		}
		if(objects.size()!=0){
			this.actualObject=objects.get(0);
		}
	}
	private void removeObject(RasterObject object){
		objects.remove(object);
	}
	/**
	 * Zjisti  jestli je objekt vykreslen podle jeho jmena
	 * @param objectName
	 * @return je objekt vykreslen
	 */
	public boolean isObjectDrawed(String objectName){
		for (RasterObject object : objects) {
			if(object.getName().equals(objectName)){
				return true;
			}
		}
		return false;
	}
	public void clear(){
		objects.clear();
		this.redrawScene();
	}
	/**
	 * Obsluha posunu kamery 
	 * @param direction Smer (Odpovida CAMERA konstantam)
	 */
	public void cameraMove(int direction){
		switch(direction){
		case CAMERA_LEFT: view.left(CAMERA_SPEED);break;
		case CAMERA_RIGHT: view.right(CAMERA_SPEED);break;
		case CAMERA_UP: view.up(CAMERA_SPEED);break;
		case CAMERA_DOWN: view.down(CAMERA_SPEED);break;
		}
		redrawScene();
	}
	public RasterObject getActualObject(){
		return this.actualObject;
	}
	
	public void setActualObject(String objectName){	
		if(objectName!=null){
			for (RasterObject object : objects) {
				if(object.getName().equals(objectName)){
					this.actualObject = object;
				}
			}
		}else{
			this.actualObject=null;
		}
	
	}
	public void setRenderer(Renderer renderer){
		this.renderer=renderer;
	}
	public void setProjection(Mat4 projection){
		this.projection = projection;
	}
	public Mat4 getProjection(){
		return this.projection;
	}
	public List<RasterObject> getObjects(){
		return objects;
	}
	public boolean isFirstPerson(){
		return this.firstPerson;
	}
	public void firstPerson(){
		this.firstPerson=true;
	}
	public void thirdPerson(){
		this.firstPerson=false;
	}
	public void changeDrawingType(DrawingMethod method){
		renderer.getGPU().setDrawingMethod(method);
		redrawScene();
	}
	
	public Camera getView(){
		return this.view;
	}
	
}
