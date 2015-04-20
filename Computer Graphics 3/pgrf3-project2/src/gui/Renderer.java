package gui;

import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.GLProfile;
import javax.media.opengl.glu.GLU;













import com.jogamp.common.nio.Buffers;
import com.jogamp.newt.Display;
import com.jogamp.opengl.util.GLReadBufferUtil;
import com.jogamp.opengl.util.awt.AWTGLReadBufferUtil;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseEvent;
import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;

import utils.BufferUtils;
import utils.OGLBuffers;
import utils.OGLTexture;
import utils.ShaderUtils;

public class Renderer implements GLEventListener, KeyListener {
	public static final float VIEW_METHOD_NOTHING=0f;
	public static final float VIEW_METHOD_EDGE_DETECTION=1f;
	public static final float VIEW_METHOD_GAUSSIAN_BLUR=2f;
	public static final float VIEW_METHOD_CONTRAST=3f;
	public static final float VIEW_METHOD_SOBEL=4f;
	public static final float VIEW_METHOD_GRAYSCALE=5f;
	public static final float VIEW_METHOD_BRIGHTNESS=6f;
	public static final float VIEW_METHOD_HUE=7f;
	
	GL2 gl;
	GLAutoDrawable glDrawable;
	int width, height;
	GLU glu;
	boolean textureChanged=false;

	OGLBuffers buffers;
    OGLTexture texture;
	
	int shaderProgram,locViewMethod,locImageHeight,locImageWidth,locEffectIntensity;
	float viewMethod=0f,imageHeight=512f,imageWidth=512f,effectIntesity=0f;
	String texturePath = "textures/bricks.jpg";
	public Renderer() {
	}
	public Renderer(String texturePath,float imageHeight,float imageWidth) {
		this.texturePath=texturePath.replace("\\", "/");
		this.imageHeight=imageHeight;
		this.imageWidth=imageWidth;
	}
	
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
		texture = new OGLTexture(gl, texturePath);
		
		shaderProgram = ShaderUtils.loadProgram(gl, "./shader/image");
		gl.glUseProgram(shaderProgram);
		locViewMethod=gl.glGetUniformLocation(shaderProgram, "viewMethod");
		locImageHeight=gl.glGetUniformLocation(shaderProgram, "imageHeight");
		locImageWidth=gl.glGetUniformLocation(shaderProgram, "imageWidth");
		locEffectIntensity=gl.glGetUniformLocation(shaderProgram, "effectIntensity");
		createBuffers();
	}
	
	void createBuffers() {
		float[] vertexBufferData = {
			-1, 1,
			1, 1,
			1, -1,
			-1 ,-1
		};
		int[] indexBufferData = { 0, 1, 2, 3 };
		OGLBuffers.Attrib[] attributes = {
				new OGLBuffers.Attrib("inPosition", 2),
		};
		buffers = new OGLBuffers(gl, vertexBufferData, attributes,
				indexBufferData);
	}

	public void display(GLAutoDrawable drawable) {
		gl.glClearColor(0.1f, 0.1f, 0.1f, 1.0f);
		gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);

		gl.glUniform1f(locViewMethod, viewMethod);
		gl.glUniform1f(locImageHeight, imageHeight);
		gl.glUniform1f(locImageWidth, imageWidth);
		gl.glUniform1f(locEffectIntensity, effectIntesity);
		if(this.textureChanged){
			texture=new OGLTexture(gl, texturePath);
			this.textureChanged=false;
		}
		texture.bind(shaderProgram, "texture", 0);
		// vykresleni
		buffers.draw(GL2.GL_QUADS, shaderProgram);
	}
	public void reshape(GLAutoDrawable drawable, int x, int y, int width,
			int height) {
		this.width = width;
		this.height = height;
	}
	public BufferedImage getBufferedImage(GLProfile profile){
		AWTGLReadBufferUtil bufferUtils = new AWTGLReadBufferUtil(profile, false); 
		gl.getContext().makeCurrent();
		return bufferUtils.readPixelsToBufferedImage(gl, true);
	}
	public void loadTexture(String path){
		this.texturePath=path;
		this.textureChanged=true;
	}
	public void setViewMethod(float viewMethod){
		this.viewMethod=viewMethod;
	}
	public void setEffectIntensity(int intensity){
		this.effectIntesity=(float) intensity;
	}
	public void setEffectIntensity(float intensity){
		this.effectIntesity=intensity;
	}
	public void displayChanged(GLAutoDrawable drawable, boolean modeChanged,
			boolean deviceChanged) {
	}
	
	public void dispose(GLAutoDrawable arg0) {
	}
	@Override
	public void keyPressed(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}
}