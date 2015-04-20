package gui;

import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.glu.GLU;

import model.Grid;
import model.Mesh;

import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseEvent;
import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;

import transforms.Camera;
import transforms.Mat4;
import transforms.Mat4PerspRH;
import transforms.Vec3D;
import utils.OGLBuffers;
import utils.OGLTexture;
import utils.ShaderUtils;
import utils.ToFloatArray;

public class Renderer implements GLEventListener, MouseListener,
		MouseMotionListener, KeyListener {
	public static final int FUNCTION_SEDLO = 0;
	public static final int FUNCTION_HAD = 1;
	public static final int FUNCTION_KUZEL = 2;
	public static final int FUNCTION_SOMBRERO = 3;
	public static final int FUNCTION_HOUBA = 4;
	public static final int FUNCTION_SKLENICE = 5;
	public static final int FUNCTION_KOULE = 6;
	public static final int FUNCTION_SLONI_HLAVA = 7;
	public static final int FUNCTION_MIMOZEMSTAN = 8;	
	
	public static final int MAPPING_NO = 0;
	public static final int MAPPING_NORMAL = 1;
	public static final int MAPPING_PARALLAX = 2;
	
	GL2 gl;
	GLAutoDrawable glDrawable;
	GLU glu;
	int width, height, ox, oy, gWidth = 20, gHeight = 20;
 
	//ridici parametry
	int renderedFunction=0, mappingType=0;
	boolean lightPerVertex,useTexture;
	
	OGLBuffers buffers;
	Mesh grid;

	//OPENGL lokace prommennych
	int shaderProgram, locMatGrid,locLightPos, locEyePos, locRenderedFunction,locLightPerVertex,locUseTexture,locMappingType,locParallaxScale;

	Camera cam = new Camera();
	Mat4 proj; // vytvarena v reshape
	OGLTexture texture,normTex,heightTex;
	
	
	/**
	 * Nastaveni 
	 */
	Vec3D lightPos = new Vec3D(0,0,150);
	Vec3D cameraPos = new Vec3D(5, 5, 2.5);
	double azimuth = Math.PI * 1.25,
		  zenith = Math.PI * -0.125;
	
	
	public void init(GLAutoDrawable drawable) {
		glDrawable = drawable;
		gl = glDrawable.getGL().getGL2();

		System.out.println("Init GL is " + gl.getClass().getName());
		System.out.println("OpenGL version " + gl.glGetString(GL2.GL_VERSION));
		System.out.println("OpenGL vendor " + gl.glGetString(GL2.GL_VENDOR));
		System.out
				.println("OpenGL renderer " + gl.glGetString(GL2.GL_RENDERER));
		System.out.println("OpenGL extension "
				+ gl.glGetString(GL2.GL_EXTENSIONS));
		shaderProgram = ShaderUtils.loadProgram(gl, "./shader/grid");
		ShaderUtils.linkProgram(gl, shaderProgram);
		grid = new Grid(gl,gHeight,gWidth, "inPosition");
		
		texture = new OGLTexture(gl, "textures/bricks.jpg");
		normTex = new OGLTexture(gl, "textures/bricksn.png");
		heightTex = new OGLTexture(gl, "textures/bricksh.png");

		locMatGrid = gl.glGetUniformLocation(shaderProgram, "mat");
		locLightPos = gl.glGetUniformLocation(shaderProgram, "lightPos");
		locEyePos = gl.glGetUniformLocation(shaderProgram,"eyePos");
		locRenderedFunction = gl.glGetUniformLocation(shaderProgram, "renderFunction");
		locLightPerVertex = gl.glGetUniformLocation(shaderProgram, "lightPerVertex");
		locUseTexture = gl.glGetUniformLocation(shaderProgram, "useTexture");
		locMappingType = gl.glGetUniformLocation(shaderProgram, "mappingType");
		locParallaxScale = gl.glGetUniformLocation(shaderProgram, "parallaxScale");
		
		
		cam.setPosition(cameraPos);
		cam.setAzimuth(azimuth);
		cam.setZenith(zenith);
		
		gl.glEnable(GL2.GL_DEPTH_TEST);
	}
	public void display(GLAutoDrawable drawable) {
		gl.glClearColor(0.1f, 0.1f, 0.1f, 1.0f);
		gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);
		
		Mat4 mat = cam.getViewMatrix().mul(proj);
			
		gl.glUseProgram(shaderProgram);
		gl.glUniformMatrix4fv(locMatGrid,1,false,ToFloatArray.convert(mat),0);
		gl.glUniform3fv(locLightPos, 3, ToFloatArray.convert(lightPos), 0);
		gl.glUniform3fv(locEyePos, 3, ToFloatArray.convert(cam.getEye()), 0);
		
		gl.glUniform1f(locRenderedFunction, (float) renderedFunction);
		gl.glUniform1f(locLightPerVertex, (lightPerVertex)?1f:0f);
		gl.glUniform1f(locUseTexture, (useTexture)?1f:0f);
		gl.glUniform1f(locMappingType, (float) mappingType);

		//nastaveni parallax scale
		gl.glUniform1f(locParallaxScale, 0.1f);
		
		
		texture.bind(shaderProgram, "texture", 0);
		normTex.bind(shaderProgram, "normTex", 1);
		heightTex.bind(shaderProgram,"heightTex",2);
	
		grid.draw(shaderProgram);
	}

	public void reshape(GLAutoDrawable drawable, int x, int y, int width,
			int height) {
		this.width = width;
		this.height = height;
		proj = new Mat4PerspRH(Math.PI / 4, height / (double) width, 0.01, 1000.0);
	}

	public void setRenderedFunction(int function,boolean resetCamera){
		this.renderedFunction = function;
		if(resetCamera) this.resetCamera();
	}
	public void setLightPerVertex(boolean lightPerVertex){
		this.lightPerVertex=lightPerVertex;
	}
	public void setUseTexture(boolean useTexture){
		this.useTexture=useTexture;
	}
	public void setMappingType(int mappingType){
		this.mappingType=mappingType;
	}
	public void displayChanged(GLAutoDrawable drawable, boolean modeChanged,
			boolean deviceChanged) {
	}

	public void mouseClicked(MouseEvent e) {
	}

	public void mouseEntered(MouseEvent e) {
	}

	public void mouseExited(MouseEvent e) {
	}

	public void mousePressed(MouseEvent e) {
		ox = e.getX();
		oy = e.getY();
	}

	public void mouseReleased(MouseEvent e) {
	}

	public void mouseDragged(MouseEvent e) {
		cam.addAzimuth((double) Math.PI * (ox - e.getX())
				/ width);
		cam.addZenith((double) Math.PI * (e.getY() - oy)
				/ width);
		ox = e.getX();
		oy = e.getY();
	}

	public void mouseMoved(MouseEvent e) {
	}

	public void keyPressed(KeyEvent e) {
		switch (e.getKeyCode()) {
		case KeyEvent.VK_W:
			cam.forward(1);
			break;
		case KeyEvent.VK_D:
			cam.right(1);
			break;
		case KeyEvent.VK_S:
			cam.backward(1);
			break;
		case KeyEvent.VK_A:
			cam.left(1);
			break;
		case KeyEvent.VK_SHIFT:
			cam.down(1);
			break;
		case KeyEvent.VK_CONTROL:
			cam.up(1);
			break;
		case KeyEvent.VK_SPACE:
			cam.setFirstPerson(!cam.getFirstPerson());
			break;
		case KeyEvent.VK_R:
			cam.mulRadius(0.9f);
			break;
		case KeyEvent.VK_F:
			cam.mulRadius(1.1f);
			break;
		}
	}

	public void keyReleased(KeyEvent e) {
	}

	public void keyTyped(KeyEvent e) {
	}

	public void dispose(GLAutoDrawable arg0) {
	}
	public void resetCamera(){
		cam.setPosition(cameraPos);
		cam.setAzimuth(azimuth);
		cam.setZenith(zenith);
	}
}