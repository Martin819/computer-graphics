package model;

import gui.Raster;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import transforms3D.Camera;
import transforms3D.Mat4;
import transforms3D.Mat4Identity;
import transforms3D.Mat4RotX;
import transforms3D.Mat4RotY;
import transforms3D.Mat4RotZ;
import transforms3D.Mat4Scale;
import transforms3D.Mat4Transl;
import transforms3D.Point3D;
import transforms3D.Vec3D;
/**
 * Hlavni vykreslovaci trida (renderer)
 * @author Jakub Josef
 *
 */
public class Renderer {
	private static Logger log = Logger.getLogger(Renderer.class.getName());
	private Dimension windowSize = null;
	private Point3D p1,p2,p3,p4;
	private final double SPEED = 1;
	
	private ArrayList<Point3D> vertices = null;
	private ArrayList<Integer> indices = null;
	private Graphics2D gr = null;
	private Raster rastr = null;
	private Mat4 dataMatrix = new Mat4Identity();
	private Mat4 viewMatrix = null;
	private Mat4 firstProjection = null;
	private boolean showAxes = false;
	
	private Color barvaKresleni = Color.GREEN;
	private Color barvaOs = Color.WHITE;
	//kamera a jeji obsluha
	private Camera viewCamera=new Camera();
	public static final int CAMERA_LEFT = 0;
	public static final int CAMERA_RIGHT = 1;
	public static final int CAMERA_UP = 2;
	public static final int CAMERA_DOWN = 3;
	
	public Renderer() {

	}
	
	public Renderer(Graphics2D gr,Dimension windowSize){
		this.gr=gr;
		this.setWindowSize(windowSize);
		switchCameraView(false);
	}
	
	public Renderer(Raster r,Dimension windowSize){
		this.rastr=r;
		this.setWindowSize(windowSize);
		switchCameraView(false);
	}
	/**
	 * Metoda pripravy pocatecni hodnoty pointu pro kresleni os
	 */
	private void fillPoints(){
	    p1 = new Point3D(60, 60, 0);
	    p2 = new Point3D(150, 60, 10);
	    p3 = new Point3D(60, 150, 10);
	    p4 = new Point3D(60, 60, 100);
	}
	/**
	 * Setter pro data rendereru
	 * @param vertices Pole Point3D s body
	 * @param indices Pole Integeru s indexy
	 */
	public void setData(ArrayList<Point3D> vertices, ArrayList<Integer> indices){
		this.vertices=vertices;
		this.indices=indices;
	}
	/**
	 * Zobrazovaci metoda, ktera neprovadi transformace objektu
	 */
	public void view(){
		if(rastr!=null){
			rastr.reset();
			if(showAxes){
				drawAxes();
			}
		}
		if(indices != null && !indices.isEmpty()){
			for (int i = 0; i < indices.size(); i=i+2) {	
				try {
					Point3D firstPoint = vertices.get(indices.get(i));
					Point3D secondPoint = vertices.get(indices.get(i+1));
					if(rastr!=null){
						rastr.kresliUsecku(new Point((int)firstPoint.x,(int) firstPoint.y),new Point((int) secondPoint.x,(int) secondPoint.y), getBarvaKresleni());
					}else if(gr!=null){
						gr.drawLine((int)firstPoint.x,(int)firstPoint.y,(int)secondPoint.x,(int)secondPoint.y);
					}else{
						throw new Exception("Neni na co kreslit!!!");
					}
					}catch(Exception e){
						e.printStackTrace();
					}
			}
		}
	}
	
	/**
	 * Hlavni zobrazovaci metoda,provadi vsechny potrebne transformace objektu
	 */
	public void render(){
		if(rastr!=null){
			rastr.reset();
			if(showAxes){
				drawAxes();
			}
		}
		if(indices != null && !indices.isEmpty()){
		for (int i = 0; i < indices.size(); i=i+2) {	
			try {
				Mat4 finalMat = new Mat4Identity();
				finalMat=finalMat.mul(dataMatrix);
				finalMat=finalMat.mul(viewMatrix);
				finalMat=finalMat.mul(firstProjection);
				Point3D firstPoint = vertices.get(indices.get(i)).mul(finalMat);
				Point3D secondPoint = vertices.get(indices.get(i+1)).mul(finalMat);
				if(firstProjection!=null){
					draw(firstPoint,secondPoint);
				}else{
					draw(firstPoint,secondPoint);
				}
			} catch (Exception e) {
				log.log(Level.SEVERE,"Nepodarilo se vykreslit objekt!");
				e.printStackTrace();
			}
		}
		}
	}
	/**
	 * Metoda zapne zobrazeni os
	 */
	public void showAxes(){
		fillPoints();
		showAxes=true;
		drawAxes();
	}
	/**
	 * Metoda prichysta kameru k prvnimu zobrazeni
	 */
	private void firstCamera() {
			viewCamera.setAzimuth(-2.3f);
			viewCamera.setZenith(-0.3f);
			viewCamera.setRadius(180);
			viewCamera.setPosition(new Vec3D(0,0,0));
			viewCamera.setFirstPerson(false);
			viewMatrix = viewCamera.getViewMatrix();
			
	}
	/**
	 * Prepinac pohledu kamery
	 * @param firstPerson Prvni osoba true/false
	 */
	public void switchCameraView(boolean firstPerson) {
		if (firstPerson) {
			viewCamera.setPosition(new Vec3D(0,0,0));
			viewCamera.setFirstPerson(false);	
			firstCamera();
			render();
		} else {
			viewCamera.setPosition(new Vec3D(200,200,100));	
			viewCamera.setFirstPerson(true);
			firstCamera();
			render();
		}
	}
	/** 
	 * Metoda fakticky vykreslujici osy 
	 */
	private void drawAxes() {
	    rastr.kresliUsecku(new Point((int)p1.x, (int)p1.y), new Point((int)p2.x, (int)p2.y), getBarvaOs());
	    rastr.kresliText(new Point((int)p2.x+5, (int)p2.y+5),"x",getBarvaOs());
		rastr.kresliUsecku(new Point((int)p1.x, (int)p1.y), new Point((int)p3.x, (int)p3.y), getBarvaOs());
		rastr.kresliText(new Point((int) p3.x+5, (int)p3.y+5),"y",getBarvaOs());
		rastr.kresliUsecku(new Point((int)p1.x, (int)p1.y), new Point((int)p4.x, (int)p4.y), getBarvaOs());
		rastr.kresliText(new Point((int) p4.x+5, (int)p4.y+10),"z",getBarvaOs());		
	}
	/**
	 * Hlavni vykreslovaci metoda pro usecku, zpracuje body, z transformuje je a zobrazi
	 * @param x Prvni Point3D
	 * @param y Druhy Point3D
	 * @throws Exception
	 */
	private void draw(Point3D x, Point3D y) throws Exception {
		Vec3D vecX;
		Vec3D vecY;
		if((x.w>0) || (y.w>0)){
			vecX = x.dehomog();
			vecY = y.dehomog();
			//transformuji objekty pro zobrazeni na platne
			vecX=vecX.mul(new Vec3D(1,-1,1)).add(new Vec3D(1,1,0)).mul(getWindowSize().getHeight()/2);
			vecY=vecY.mul(new Vec3D(1,-1,1)).add(new Vec3D(1,1,0)).mul(getWindowSize().getHeight()/2);
		}else{
			vecX = x.ignoreW();
			vecY = y.ignoreW();
		}
		if(rastr!=null){
			rastr.kresliUsecku(new Point((int)vecX.x,(int) vecX.y),new Point((int) vecY.x,(int) vecY.y), getBarvaKresleni());
		}else if(gr!=null){
			gr.drawLine((int)vecX.x,(int)vecX.y,(int)vecY.x,(int)vecY.y);
		}else{
			throw new Exception("Neni na co kreslit!!!");
		}		
	}
	/**
	 * Metoda vykresli jeden bod 
	 * @param point Bod
	 */
	public void drawPoint(Point3D point){
		rastr.vykresliBunku(new Point((int)point.x,(int)point.y), getBarvaKresleni());
	}
	/**
	 * Metoda odebere jeden bod
	 * @param point Bod
	 */
	public void removePoint(Point3D point){
		rastr.smazBunku(new Point((int)point.x,(int)point.y));
	}
	/**
	 * Posune teleso o zadany Point3D
	 * @param kam Point3D posunu
	 */
	public void move(Point3D kam){
		dataMatrix = dataMatrix.mul(new Mat4Transl(kam.x,kam.y,kam.z));
	}
	/**
	 * Orotuje teleso o zadany uhel na ose X
	 * @param degree Uhel
	 */
	public void rotateX(double degree){
		Mat4 rotMatX = new Mat4RotX(Math.toRadians(degree));
		refreshPoints(rotMatX);
		dataMatrix=rotMatX.mul(dataMatrix);
		
	}
	/**
	 * Orotuje teleso o zadany uhel na ose Y
	 * @param degree Uhel
	 */	
	public void rotateY(double degree){
		Mat4 rotMatY = new Mat4RotY(Math.toRadians(degree));
		refreshPoints(rotMatY);
		dataMatrix=rotMatY.mul(dataMatrix);

	}
	/**
	 * Orotuje teleso o zadany uhel na ose Z
	 * @param degree Uhel
	 */	
	public void rotateZ(double degree) {
		Mat4 rotMatZ = new Mat4RotZ(Math.toRadians(degree));
		refreshPoints(rotMatZ);
		dataMatrix=rotMatZ.mul(dataMatrix);
		
	}
	/**
	 * Meni meritko pohledu
	 * @param jak Point3D zoomu
	 */
	public void zoom(Point3D jak){
		Mat4 zoomMat=new Mat4Scale(jak.x, jak.y, jak.z);
		refreshPoints(zoomMat);
		dataMatrix=dataMatrix.mul(zoomMat);
	}
	/**
	 * Obsluha posunu kamery 
	 * @param direction Smer (Odpovida CAMERA konstantam)
	 */
	public void cameraMove(int direction){
		switch(direction){
		case CAMERA_LEFT: viewCamera.left(SPEED); viewMatrix = viewCamera.getViewMatrix();render();break;
		case CAMERA_RIGHT: viewCamera.right(SPEED);viewMatrix = viewCamera.getViewMatrix();render();break;
		case CAMERA_UP: viewCamera.up(SPEED); viewMatrix = viewCamera.getViewMatrix();render();break;
		case CAMERA_DOWN: viewCamera.down(SPEED); viewMatrix = viewCamera.getViewMatrix();render();break;
		
		}
	}
	/**
	 * Metoda prepocita body pro zobrazeni os podle poslane matice
	 * @param matice 
	 */
	private void refreshPoints(Mat4 matice){
		
		p1=p1.mul(matice);
		p2=p2.mul(matice);
		p3=p3.mul(matice);
		p4=p4.mul(matice);
	}

	public void setFirstProjection(Mat4 firstProjection) {
		this.firstProjection = new Mat4Identity().mul(firstProjection);
	}

	public Color getBarvaKresleni() {
		return barvaKresleni;
	}

	public Color getBarvaOs() {
		return barvaOs;
	}

	public void setBarvaKresleni(Color barvaKresleni) {
		this.barvaKresleni = barvaKresleni;
		render();
	}
	
	public void setBarvaOs(Color barvaOs) {
		this.barvaOs = barvaOs;
		render();
	}

	public Dimension getWindowSize() {
		return windowSize;
	}

	public void setWindowSize(Dimension windowSize) {
		this.windowSize = windowSize;
	}
		

}
