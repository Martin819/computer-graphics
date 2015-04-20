package model;

import gui.Raster;
import gui.Scene;

import java.awt.Color;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import model.objects.RasterObject;
import transforms3D.Mat4;
import transforms3D.Mat4Identity;
import transforms3D.Point3D;
import transforms3D.Vec3D;


public class GPU {
	
	private float[][] zb;
	private Raster rastr;
	private Scene scene;
	public enum DrawingMethod {
		WIREFRAME, INTERPOLATIONS, BOTH
	}
	private DrawingMethod drawingMethod = DrawingMethod.INTERPOLATIONS;
	private List<Point3D> points = new ArrayList<>();
	
	private Mat4 help = new Mat4();
	
	
	public GPU(Raster rastr,Scene scene){
		this.rastr = rastr;
		this.scene = scene;
		
		zb = new float[(int)rastr.getVelikostOkna().getHeight()][(int)rastr.getVelikostOkna().getWidth()];
		initZBuffer();
	}
	
	public void draw(RasterObject object){
		
		Vec3D v1,v2;
		for (int i = 0; i < object.getIndices().size(); i = i+2) {
			if ((points.get(object.getIndices().get(i)).w>=0) && (points.get(object.getIndices().get(i+1)).w>=0)) {
				v1 = points.get(object.getIndices().get(i)).dehomog();//dehomogenizace bodu
				v2 = points.get(object.getIndices().get(i+1)).dehomog();
			//prvni vektor prehozeni osy Y a nasledne vektor posunu
				v1.x = (rastr.getVelikostOkna().getWidth())*(v1.x+1)/2;
				v1.y = (rastr.getVelikostOkna().getHeight())*(1-v1.y)/2;

				v2.x = (rastr.getVelikostOkna().getWidth())*(v2.x+1)/2;
				v2.y = (rastr.getVelikostOkna().getHeight())*(1-v2.y)/2;

				
			}else {//pokud je w zaporne tak ho ignoruj
				v1 = points.get(object.getIndices().get(i)).ignoreW();
				v2 = points.get(object.getIndices().get(i+1)).ignoreW();
			}
			drawLine(v1, v2,Color.GREEN);
	}
		
}
	public void redraw(RasterObject object){
		points.clear();
		
		for (Point3D point : object.getVertices()) {
		    help = new Mat4Identity();
			help = help.mul(object.getMat()); 
			if (scene.getView()!=null) { //pokud pohled aplikuj ho
				if (scene.isFirstPerson()) { //prvni Osoba ?	
					help = help.mul(scene.getView().getViewMatrix());					
				} else { //dalsi osoba patricne nastavit a zmenit pozici
					scene.getView().setPosition(new Vec3D(200,200,100));
					help = help.mul(scene.getView().getViewMatrix());					
				}
			} 
			help=help.mul(scene.getProjection());

			points.add(point.mul(help));//pridej bod TELESA pridej MATICI		
			
		}
		if(drawingMethod.equals(DrawingMethod.INTERPOLATIONS) || drawingMethod.equals(DrawingMethod.BOTH)){
			//vypln objekty
			for (Triangle triangle : object.getTriangles()) {
				drawTriangle(triangle, help);
			}
		}
		if(drawingMethod.equals(DrawingMethod.WIREFRAME) || drawingMethod.equals(DrawingMethod.BOTH)){
			//vykresli hrany
			draw(object); 
		}
		rastr.vykresli();
		initZBuffer();
		
	}
	public DrawingMethod getDrawingMethod() {
		return drawingMethod;
	}
	public void setDrawingMethod(DrawingMethod drawingMethod) {
		this.drawingMethod = drawingMethod;
	}
	public void drawTriangle(Triangle triangle, Mat4 mat4) {

		Point3D a,b,c = new Point3D();
		
		a = triangle.getA().mul(mat4);
		b = triangle.getB().mul(mat4);
		c = triangle.getC().mul(mat4);
		
		Vec3D A = a.dehomog();
		Vec3D B = b.dehomog();
		Vec3D C = c.dehomog();
		
		// zpìtná transformace dehomogenizovaných souøadníc (0 až 1) 
		A.x = (rastr.getVelikostOkna().getWidth())*(A.x+1)/2;
		A.y = (rastr.getVelikostOkna().getHeight())*(1-A.y)/2;

		B.x = (rastr.getVelikostOkna().getWidth())*(B.x+1)/2;
		B.y = (rastr.getVelikostOkna().getHeight())*(1-B.y)/2;

		C.x = (rastr.getVelikostOkna().getWidth())*(C.x+1)/2;
		C.y = (rastr.getVelikostOkna().getHeight())*(1-C.y)/2;

		if(triangle.isInterpolated) {
			A.w = a.w;
			B.w = b.w;
			C.w = c.w;
			A.interpolate(Color.YELLOW);
			B.interpolate(Color.RED);
			C.interpolate(Color.BLUE);
		}
		
		// seøadíme ABC, tak, aby A<B<C
		if(A.y > B.y) {
			Vec3D temp = B;
			B = A;
			A = temp;
		}
		if(B.y > C.y) {
			Vec3D temp = C;
			C = B;
			B = temp;
		}
		if(A.y > B.y) {
			Vec3D temp = B;
			B = A;
			A = temp;
		}

		// interpolace z A do B
		interpolate(triangle,A, B, C, A, B);
		
		// interpolace z B do C
		interpolate(triangle,A, B, C, B, C);
	}
	
	public void interpolate(Triangle troj,Vec3D a, Vec3D b, Vec3D c, Vec3D from, Vec3D to) {
		//bresh(a, b);
		for(int y =  Math.round((float)from.y); y <= to.y; y++) {
			float s1 = (y - Math.round(from.y)) / (float)(Math.round(to.y) - Math.round(from.y));
			float s2 = (y - Math.round(a.y)) / (float)(Math.round(c.y) - Math.round(a.y));
			
			Vec3D v1 = new Vec3D(0, y, 0);
			v1.x = Math.round((Math.round(from.x) * (1 - s1) + Math.round(to.x) * s1));
			v1.z = ((1 - s1) * from.z + s1 * to.z);
			v1.w = (1 / ((1 - s1) / from.w + s1 / to.w));
			
			Vec3D v2 = new Vec3D(0, y, 0);
			v2.x = Math.round((Math.round(a.x) * (1 - s2) + Math.round(c.x) * s2));
			v2.z = ((1 - s2) * a.z + s2 * c.z);
			v2.w = 1 / ((1 - s2) / a.w + s2 / c.w); 
			
			if(troj.isInterpolated) {
				v1.rs = ((1 - s1) * from.rs + s1 * to.rs);
				v2.rs = ((1 - s2) * a.rs + s2 * c.rs);
				v1.gs = ((1 - s1) * from.gs + s1 * to.gs);
				v2.gs = ((1 - s2) * a.gs + s2 * c.gs);
				v1.bs = ((1 - s1) * from.bs + s1 * to.bs);
				v2.bs = ((1 - s2) * a.bs + s2 * c.bs);
			}
			
			// prohodíme, kvùli procházení zprava doleva
			if (v1.x > v2.x) {
				Vec3D temp = v1;
				v1 = v2;
				v2 = temp;
			}
			
			// procházíme zprava doleva
			for (int x = (int)v1.x; x <= (int)v2.x; x++) {
				float t = (float) ((x - v1.x) / (float)(v2.x - v1.x));
				// jednoduchá interpolace z
				float z = (float)((1 - t) * v1.z + t * v2.z);
				if(x+1>=rastr.getVelikostOkna().getHeight() || x<=0) continue;
				if(y+1>=rastr.getVelikostOkna().getWidth() || y<=0) continue;
				if(z < zb[x][y]) {
					if(troj.isInterpolated) {
						// složitìjší interpolace podle w
						float wc = (float)(1 / ((1 - t) / v1.w + t / v2.w));
						float red = ((1 - t) * v1.rs + t * v2.rs) * wc;
						float gre = ((1 - t) * v1.gs + t * v2.gs) * wc;
						float blu = ((1 - t) * v1.bs + t * v2.bs) * wc;
						rastr.nastavBunku(new Point(x,y), new Color(intColor(red), intColor(gre), intColor(blu)));
					}
					else {
						rastr.nastavBunku(new Point(x,y),Color.RED);
					}
					zb[x][y] = z;
				}
			}
		}
		
	}
	/**
	 * 
	 * @param startBod
	 * @param endBod
	 * @param color
	 */
	public void drawLine(Vec3D startBod, Vec3D endBod,Color color) {
		int deltaX = Math.round((float)endBod.x) - Math.round((float)startBod.x);
		int dx1 = 0;
		int dy1 = 0;
		int dx2 = 0;
		int deltaY =Math.round((float)endBod.y) - Math.round((float)startBod.y);
		int dy2 = 0;

		if (deltaX < 0) {
			dx1 = -1;
			dx2 = -1;
		} else if (deltaX > 0) {
			dx1 = 1;
			dx2 = 1;
		}
		if (deltaY < 0)
			dy1 = -1;
		else if (deltaY > 0)
			dy1 = 1;
		int delsi = Math.abs(deltaX);
		int kratsi = Math.abs(deltaY);
		if (!(delsi > kratsi)) {
			delsi = Math.abs(deltaY);
			kratsi = Math.abs(deltaX);
			if (deltaY < 0)
				dy2 = -1;
			else if (deltaY > 0)
				dy2 = 1;
			dx2 = 0;
		}
		int scitac = delsi >> 1;
		for (int i = 0; i <= delsi; i++) {
			rastr.nastavBunku(new Point(Math.round((float)startBod.x),Math.round((float)startBod.y)),color); 
			scitac = scitac + kratsi;
			if (!(scitac < delsi)) {
				scitac = scitac - delsi;
				startBod.x = startBod.x + dx1;
				startBod.y = startBod.y + dy1;
			} else {
				startBod.x = startBod.x + dx2;
				startBod.y = startBod.y + dy2;
			}
		}
	}
	
	public int intColor(float floatColor) {
		if(floatColor>255) floatColor = 255;
		else if(floatColor<0) floatColor = 0;
		return (int)floatColor;
	}

	
public void initZBuffer(){
	zb = new float[(int)rastr.getVelikostOkna().getHeight()][(int)rastr.getVelikostOkna().getWidth()];
		for (int i = 0; i < rastr.getVelikostOkna().getHeight(); i++) {
			for (int j = 0; j < rastr.getVelikostOkna().getWidth(); j++) {
				if (zb[i][j] != Float.MAX_VALUE) {
					zb[i][j] = Float.MAX_VALUE;					
				}
			}
			
		}
		//vypisZbuffer();
	}
	
	
	
}
