package utils;

import java.util.ArrayList;

import javax.swing.JOptionPane;

import model.Renderer;
import transforms3D.Point3D;
/**
 * Pomocna trida zajistujici vykreslovani objektu
 * @author Jakub Josef
 *
 */
public class ObjectDrawer {

public static void createCube(Renderer renderer) {
		int strana = Integer.parseInt(JOptionPane.showInputDialog(null,"Zadejte stranu krychle:"));
		ArrayList<Point3D> verticesCube = new ArrayList<Point3D>();
		ArrayList<Integer> indicesCube = new ArrayList<Integer>();
		//spodek
		verticesCube.add(new Point3D(0,0,0));
		verticesCube.add(new Point3D(0,strana,0));
		verticesCube.add(new Point3D(strana,strana,0));
		verticesCube.add(new Point3D(strana,0,0));
		//vršek krychle
		verticesCube.add(new Point3D(0,0,strana));
		verticesCube.add(new Point3D(0,strana,strana));
		verticesCube.add(new Point3D(strana,strana,strana));
		verticesCube.add(new Point3D(strana,0,strana));
		
		indicesCube.add(0);indicesCube.add(1);
		indicesCube.add(1);indicesCube.add(2);
		indicesCube.add(2);indicesCube.add(3);
		indicesCube.add(3);indicesCube.add(0);
		
		indicesCube.add(4);indicesCube.add(5);
		indicesCube.add(5);indicesCube.add(6);
		indicesCube.add(6);indicesCube.add(7);
		indicesCube.add(7);indicesCube.add(4);
		
		indicesCube.add(0);indicesCube.add(4);
		indicesCube.add(1);indicesCube.add(5);
		indicesCube.add(2);indicesCube.add(6);
		indicesCube.add(3);indicesCube.add(7);
		
		renderer.setData(verticesCube, indicesCube);
		renderer.render();
	}
public static void createBlock(Renderer renderer){
	int strana1 = Integer.parseInt(JOptionPane.showInputDialog(null,"Zadejte stranu kvádru A:"));
	int strana2 = Integer.parseInt(JOptionPane.showInputDialog(null,"Zadejte stranu kvádru B:"));
	ArrayList<Point3D> verticesBlock = new ArrayList<Point3D>();
	ArrayList<Integer> indicesBlock = new ArrayList<Integer>();
	//spodek
	verticesBlock.add(new Point3D(0,0,0));
	verticesBlock.add(new Point3D(0,strana1,0));
	verticesBlock.add(new Point3D(strana1,strana1,0));
	verticesBlock.add(new Point3D(strana1,0,0));
	//vršek krychle
	verticesBlock.add(new Point3D(0,0,strana2));
	verticesBlock.add(new Point3D(0,strana1,strana2));
	verticesBlock.add(new Point3D(strana1,strana1,strana2));
	verticesBlock.add(new Point3D(strana1,0,strana2));
	
	indicesBlock.add(0);indicesBlock.add(1);
	indicesBlock.add(1);indicesBlock.add(2);
	indicesBlock.add(2);indicesBlock.add(3);
	indicesBlock.add(3);indicesBlock.add(0);
	
	indicesBlock.add(4);indicesBlock.add(5);
	indicesBlock.add(5);indicesBlock.add(6);
	indicesBlock.add(6);indicesBlock.add(7);
	indicesBlock.add(7);indicesBlock.add(4);
	
	indicesBlock.add(0);indicesBlock.add(4);
	indicesBlock.add(1);indicesBlock.add(5);
	indicesBlock.add(2);indicesBlock.add(6);
	indicesBlock.add(3);indicesBlock.add(7);
	
	renderer.setData(verticesBlock, indicesBlock);
	renderer.render();
}
public static void createPyramid(Renderer renderer){
	int strana = Integer.parseInt(JOptionPane.showInputDialog(null,"Zadejte stranu pyramidy:"));
	ArrayList<Point3D> verticesPyramid = new ArrayList<Point3D>();
	ArrayList<Integer> indicesPyramid = new ArrayList<Integer>();
	verticesPyramid.add(new Point3D(strana/2,strana/2,strana));
	verticesPyramid.add(new Point3D(0,0,0));
	verticesPyramid.add(new Point3D(strana,0,0));
	verticesPyramid.add(new Point3D(strana,strana,0));
	verticesPyramid.add(new Point3D(0,strana,0));
	
	for(int i=1;i<5;i++){
		indicesPyramid.add(0);
		indicesPyramid.add(i);
		indicesPyramid.add(i);
		indicesPyramid.add(i % 4 +1 );
	}
	renderer.setData(verticesPyramid,indicesPyramid);
	renderer.render();
	
}
}
