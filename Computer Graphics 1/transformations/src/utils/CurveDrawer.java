package utils;

import java.util.ArrayList;
import model.Renderer;
import transforms3D.Kubika;
import transforms3D.Point3D;

public class CurveDrawer {
	public static final int BEZIER = 0;
	public static final int FERGUSON = 1;
	public static final int COONS = 2;
	/* ridici body */
	private static boolean showPoints = false;
	private static Point3D 	v1 = new Point3D(100, 200, 0),
							v2 = new Point3D(200, 300, 100),
							v3 = new Point3D(300, 315, 0),
							v4 = new Point3D(400, 0, 100);
	
	public static void createCurve(Renderer renderer,int kubika){
		ArrayList<Point3D> body = new ArrayList<>();
		ArrayList<Point3D> verticesCurve = new ArrayList<Point3D>();
		ArrayList<Integer> indicesCurve = new ArrayList<Integer>();
		
		Kubika k = new Kubika(kubika);
		body.add(v1);
		body.add(v2);
		body.add(v3);
		body.add(v4);
		k.init(new Point3D(v1.x,v1.y,v1.z), new Point3D(v2.x,v2.y,v2.z), new Point3D(v3.x,v3.y,v3.z), new Point3D(v4.x,v4.y,v4.z));
		
		for (int i=0; i<body.size();i++) {		
			Point3D start = k.compute(0);
			int iterator=0;
			 for (float t = 0.002f; t <= 1; t += 0.001f)  {
				 // Vypocet aktualniho bodu
				 Point3D end = k.compute(t);
				 verticesCurve.add(start);
				 verticesCurve.add(end);
				 indicesCurve.add(iterator);
				 indicesCurve.add(iterator+1);
				 
				 start = end; // V dalsim pruchodu kreslime tam, kde jsme skoncili
				 iterator++;
			 }

		}
		 renderer.setData(verticesCurve, indicesCurve);
		 renderer.view();
		 if(showPoints){
			 showPoints(renderer,false);
		 }
	}
	public static void showPoints(Renderer renderer,boolean switcher){
		if(!showPoints || !switcher){
			renderer.drawPoint(v1);
			renderer.drawPoint(v2);
			renderer.drawPoint(v3);
			renderer.drawPoint(v4);
		}else{
			renderer.removePoint(v1);
			renderer.removePoint(v2);
			renderer.removePoint(v3);
			renderer.removePoint(v4);
			
		}
		if(switcher){
			showPoints=!showPoints;
		}
	}
}