package utils;

import java.awt.Dimension;

import transforms3D.Mat4;
import transforms3D.Mat4OrthoRH;
import transforms3D.Mat4PerspRH;
/**
 * Pomocna trida obsahujici typy projekci
 * @author Jakub Josef
 *
 */
public class Projections {
	
	public static Mat4 getParallel(){
		return new Mat4OrthoRH(100, 100, 0.001f, 200);
	}
	public static Mat4 getPerspective(Dimension windowSize){
		return new Mat4PerspRH((float)Math.PI/4, (float)windowSize.getWidth()/windowSize.getHeight(), 0.001f, 200);
	}
	
}
