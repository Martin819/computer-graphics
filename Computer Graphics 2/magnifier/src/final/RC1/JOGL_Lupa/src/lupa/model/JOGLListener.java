package lupa.model;
 
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ComponentColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.nio.ByteBuffer;
 
import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;
import javax.swing.JFrame;

import lupa.helpers.ImageHelpers;
 
	public class JOGLListener implements GLEventListener {
		private JFrame frame=null;
		private BufferedImage drawedImage = null;
		private double w,h,radius,scale=1;
		private Point mousePosition=null;
		private double increment = 2*Math.PI/70;
		private Color magnifierColor=Color.BLACK;
		/** konstruktory **/
		public JOGLListener(BufferedImage image) {
			drawedImage=image;
		w = ImageHelpers.ceilingPow2(image.getWidth());
		h = ImageHelpers.ceilingPow2(image.getHeight());
		}
		public JOGLListener(JFrame frame){
			this.frame=frame;
		}
		public JOGLListener(JFrame frame,BufferedImage image){
			this.frame=frame;
			drawedImage=image;
			w = ImageHelpers.ceilingPow2(image.getWidth());
			h = ImageHelpers.ceilingPow2(image.getHeight());
		}
		public JOGLListener(){}
 
		/** settery **/
		public void setImage(BufferedImage image){
			drawedImage=image;
			w = ImageHelpers.ceilingPow2(image.getWidth());
			h = ImageHelpers.ceilingPow2(image.getHeight());
		}
		public void setFrame(JFrame frame){
			this.frame=frame;
		}
		public void setMousePosition(Point position){
			if(this.drawedImage==null){
				System.out.println("Nelze nastavit pozici mysi pokud neni nacteny obrazek");
			}else{
				Point calculatedPosition=new Point();
				float ratioX = computeRatio(frame.getWidth(),drawedImage.getWidth());
				float ratioY = computeRatio(frame.getHeight(),drawedImage.getHeight());
				
				calculatedPosition.x=(int)(position.x/ratioX);
				calculatedPosition.y=(int)(position.y/ratioY);
				mousePosition=calculatedPosition;
			}
		}
		private float computeRatio(float width, float width2) {
			
			return width/width2;
		}
		public void setMagnifierRadius(double radius){
			this.radius=radius;
		}
		public void setMagnifierScale(double scale){
			this.scale=scale;
		}
		public void setMagnifierColor(Color color){
			this.magnifierColor=color;
		}
		/** gettery **/
		public boolean isImageDrawed(){
			return this.drawedImage!=null;
		}
		public double getMagnifierRadius(){
			return this.radius;
		}
		public double getMagnifierScale(){
			return this.scale;
		}
		public Color getMagnifierColor(){
			return this.magnifierColor;
		}
		public Point getMousePosition(){
			return this.mousePosition;
		}
		@Override
		public void display(GLAutoDrawable drawable) {
			if(this.mousePosition!=null && this.drawedImage!=null){
				System.out.println("DISPLAY CALLED [mouse: "+this.mousePosition.x+"x"+this.mousePosition.y+", to frame: "+this.frame.getWidth()+"x"+this.frame.getHeight()+
						", with picture: "+this.drawedImage.getWidth()+"x"+this.drawedImage.getHeight()+"]");
			}else if(this.drawedImage!=null){
				System.out.println("DISPLAY CALLED [to frame: "+this.frame.getWidth()+"x"+this.frame.getHeight()+
						", with picture: "+this.drawedImage.getWidth()+"x"+this.drawedImage.getHeight()+"]");
			}else{
				System.out.println("DISPLAY CALLED [to frame: "+this.frame.getWidth()+"x"+this.frame.getHeight()+"]");
			}
			GL2 gl = drawable.getGL().getGL2();
			gl.glMatrixMode(GL2.GL_PROJECTION);
			gl.glLoadIdentity();

			//nastavime projekci bud podle velikosti okna nebo podle velikosti obrazku
			if(drawedImage==null){
				gl.glOrtho(0, frame.getWidth(), frame.getHeight(), 0, 0, 1);
			}else{
				gl.glOrtho(0, drawedImage.getWidth(), drawedImage.getHeight(), 0, 0, 1);
			}
			gl.glMatrixMode(GL2.GL_MODELVIEW);
			gl.glDisable(GL2.GL_DEPTH_TEST);
			gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);  
			gl.glClear(GL2.GL_COLOR_BUFFER_BIT);
			gl.glBlendFunc (GL2.GL_SRC_ALPHA, GL2.GL_ONE_MINUS_SRC_ALPHA); 
			gl.glEnable (GL2.GL_BLEND);
			
			if(drawedImage!=null){
				WritableRaster raster = 
					Raster.createInterleavedRaster (DataBuffer.TYPE_BYTE,
							(int)w,
							(int)h,
							4,
							null);
				ComponentColorModel colorModel=
					new ComponentColorModel (ColorSpace.getInstance(ColorSpace.CS_sRGB),
							new int[] {8,8,8,8},
							true,
							false,
							ComponentColorModel.TRANSLUCENT,
							DataBuffer.TYPE_BYTE);
				BufferedImage dukeImg = 
					new BufferedImage (colorModel,
							raster,
							false,
							null);
				Graphics2D g = dukeImg.createGraphics();
				g.drawImage(this.drawedImage, null, null);
				DataBufferByte dukeBuf =
					(DataBufferByte)raster.getDataBuffer();
				byte[] dukeRGBA = dukeBuf.getData();
				ByteBuffer bb = ByteBuffer.wrap(dukeRGBA);
				bb.position(0);
				bb.mark();
				gl.glBindTexture(GL2.GL_TEXTURE_2D, 13);
				gl.glPixelStorei(GL2.GL_UNPACK_ALIGNMENT, 1);
				gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_WRAP_S, GL2.GL_CLAMP);
				gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_WRAP_T, GL2.GL_CLAMP);
				gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MAG_FILTER, GL2.GL_LINEAR);
				gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MIN_FILTER, GL2.GL_LINEAR);
				gl.glTexEnvf(GL2.GL_TEXTURE_ENV, GL2.GL_TEXTURE_ENV_MODE, GL2.GL_REPLACE);
				gl.glTexImage2D (GL2.GL_TEXTURE_2D, 0, GL2.GL_RGBA, (int)w, (int)h, 0, GL2.GL_RGBA, 
						GL2.GL_UNSIGNED_BYTE, bb);
	 
				int left = 0;
				int top = 0;
				gl.glEnable(GL2.GL_TEXTURE_2D);
				gl.glBindTexture (GL2.GL_TEXTURE_2D, 13);
				gl.glBegin (GL2.GL_QUADS);
				gl.glTexCoord2d (0, 0);
				gl.glVertex2d (left,top);
				gl.glTexCoord2d(1,0);
				gl.glVertex2d (left + w, top);
				gl.glTexCoord2d(1,1);
				gl.glVertex2d (left + w, top + h);
				gl.glTexCoord2d(0,1);
				gl.glVertex2d (left, top + h);
				gl.glEnd();
				gl.glFlush();
			}
			//kreslime kruh
			if(mousePosition!=null && drawedImage!=null){
				this.drawCircle(gl,mousePosition,this.radius,this.magnifierColor);
				this.drawMagnifier(gl, mousePosition, this.radius);
			}

		}
		/**
		 * Metoda vykresli kruh do GLCanvasu
		 * @param gl GL2 do ktereho se bude kreslit
		 * @param center pozice stredu
		 * @param radius polomer
		 * @param color barva
		 */
		private void drawCircle(GL2 gl,Point center,double dradius,Color color){
			double radius = dradius*1.05;
			boolean textureEnabled = gl.glIsEnabled(GL2.GL_TEXTURE_2D); 
			if(textureEnabled){
				//vypneme textury pokud je třeba
				gl.glDisable(GL2.GL_TEXTURE_2D);
			}
			//nastavime barvy
			gl.glColor4f(color.getRed()/255, color.getGreen()/255, color.getBlue()/255,color.getAlpha()/255);
			for(double angle = 0; angle < 2*Math.PI; angle+=increment) 
			{ 
				gl.glBegin(GL2.GL_TRIANGLE_FAN);
				gl.glVertex2d(center.x, center.y);
				gl.glVertex2d(center.x + Math.cos(angle)*radius, center.y + Math.sin(angle)*radius);
				gl.glVertex2d(center.x + Math.cos(angle+increment)*radius, center.y + Math.sin(angle+increment)*radius);
				gl.glEnd();
			}
			if(textureEnabled){
				//zase je zapneme
				gl.glEnable(GL2.GL_TEXTURE_2D);
			}
		}
		
		private void drawMagnifier(GL2 gl,Point center,double dradius){
		      //kruhova lupa
		        double radius = dradius;
		        double radius2= dradius/2*scale;
		       
		     for(double angle = 0; angle < 2*Math.PI; angle+=increment){
		    	
		    	gl.glBegin(GL.GL_TRIANGLE_FAN);
		    	gl.glLoadIdentity();
		    	gl.glTexCoord2d (center.x/w,center.y/h);
		    	gl.glVertex2d(center.x, center.y);
		    	gl.glTexCoord2d((center.x + Math.cos(angle)* radius2)/w, (center.y + Math.sin(angle)*radius2)/h);
		    	gl.glVertex2d(center.x + Math.cos(angle)* radius, center.y + Math.sin(angle)*radius);
		    	gl.glTexCoord2d((center.x + Math.cos(angle + increment)*radius2)/w, (center.y + Math.sin(angle + increment)*radius2)/h);
		    	gl.glVertex2d(center.x + Math.cos(angle + increment)*radius, center.y + Math.sin(angle + increment)*radius);
		  		gl.glEnd();
		  		
		        }
	  	   }
		public void clear(){
			this.drawedImage=null;
		}
	    /** pretizene metody, debug info **/
		@Override
		public void init(GLAutoDrawable drawable) {
			System.out.println("INIT CALLED [to frame: "+this.frame.getWidth()+"x"+this.frame.getHeight()+"]");
		}
 
		@Override
		public void reshape(GLAutoDrawable arg0, int arg1, int arg2, int arg3,
				int arg4) {
			System.out.println("RESHAPE CALLED [to frame: "+this.frame.getWidth()+"x"+this.frame.getHeight()+"]");
 
		}

		@Override
		public void dispose(GLAutoDrawable drawable) {
			System.out.println("DISPOSE CALLED");
			
		}
	}