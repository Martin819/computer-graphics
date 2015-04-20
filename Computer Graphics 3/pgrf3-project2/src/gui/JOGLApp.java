package gui;



import javax.imageio.ImageIO;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLProfile;
import javax.media.opengl.awt.GLCanvas;
import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JSlider;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.FileChooserUI;

import com.jogamp.opengl.util.FPSAnimator;
import com.jogamp.opengl.util.GLReadBufferUtil;
import com.jogamp.opengl.util.awt.AWTGLReadBufferUtil;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream.GetField;
import java.lang.ProcessBuilder.Redirect;
import java.nio.file.Files;

public class JOGLApp {
	private static final String LOOK_AND_FEEL = "javax.swing.plaf.nimbus.NimbusLookAndFeel";
	private static final String APP_TITLE ="PGRF3 Projekt 2 - Zpracov�n� obrazu - Jakub Josef";
	private static final int FPS = 60; // animator's target frames per second
	private static final Dimension windowSize= new Dimension(800, 600);

	private static Renderer renderer;
	private static JFrame frame;
	private static JSlider jsIntensity;
	private static JLabel jlEffectIntensity;
	private static ButtonGroup effectsGroup;
	
	private static final JFileChooser fileChooser= new JFileChooser();
	private static GLCanvas canvas;
	private static GLProfile profile;
	private static GLCapabilities capabilities;
	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(LOOK_AND_FEEL);
		} catch (ClassNotFoundException | InstantiationException
				| IllegalAccessException
				| UnsupportedLookAndFeelException e) {
					System.out.println("Nepodarilo se nastavit L&F!\n" +
							"L&F "+LOOK_AND_FEEL+" asi neexistuje.");
					e.printStackTrace();
			}
		try {
			frame = new JFrame("TestFrame");
			frame.setSize(windowSize.width, windowSize.height);
			frame.setLocationRelativeTo(null);
			frame.setJMenuBar(getMenus());
			// setup OpenGL Version 2
	    	profile = GLProfile.get(GLProfile.GL2);
	    	capabilities = new GLCapabilities(profile);   	
	    	// The canvas is the widget that's drawn in the JFrame
	    	canvas = new GLCanvas(capabilities);
	    	renderer = new Renderer();
			canvas.addGLEventListener(renderer);
			canvas.addKeyListener(renderer);
	    	canvas.setSize(windowSize.width,windowSize.height);
	    	
	    	frame.add(canvas);
			
	        // shutdown the program on windows close event
	        			
	    	//final Animator animator = new Animator(canvas);
	    	final FPSAnimator animator = new FPSAnimator(canvas, FPS, true);
	    	 
	    	frame.addWindowListener(new WindowAdapter() {
				@Override
				public void windowClosing(WindowEvent e) {
					new Thread() {
	                     @Override
	                     public void run() {
	                        if (animator.isStarted()) animator.stop();
	                        System.exit(0);
	                     }
	                  }.start();
				}
			});
	    	frame.setTitle(APP_TITLE);
	    	frame.pack();
	    	frame.setVisible(true);
            animator.start(); // start the animation loop
            
            
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	private static JMenuBar getMenus(){
		JMenuBar jmenu = new JMenuBar();
		JMenu jMainMenu = new JMenu("Soubor");
		
		JMenuItem jmiOpenFile = new JMenuItem("Otev��t obr�zek");
		jmiOpenFile.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if(fileChooser.showOpenDialog(getFrame()) == JFileChooser.APPROVE_OPTION){
					String texturePath = fileChooser.getSelectedFile().getAbsolutePath();
					Dimension windowSize = getTextureImageSize(texturePath);
					renderer.setViewMethod(Renderer.VIEW_METHOD_NOTHING);
					renderer.loadTexture(texturePath);
					effectsGroup.clearSelection();
					getFrame().setSize(windowSize);
					getFrame().setPreferredSize(windowSize);
				}
			}
		});
		
		JMenuItem jmiSaveFile = new JMenuItem("Ulo�it do obr�zku");
		jmiSaveFile.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if(fileChooser.showSaveDialog(getFrame()) == JFileChooser.APPROVE_OPTION) {
					File file = fileChooser.getSelectedFile();
					String format = "";
					int i = file.getName().lastIndexOf(".");
					if(i >= 0){
						format = file.getName().substring(i+1);
					}
					
					saveCanvasToImage(file, format);
					JOptionPane.showMessageDialog(frame, "Obr�zek ulo�en do "+file.getAbsolutePath(),"Ulo�eno",JOptionPane.INFORMATION_MESSAGE);
				} 
			}
		});
		JMenuItem jmiCloseApp = new JMenuItem("Zav��t program");
		jmiCloseApp.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				getFrame().dispose();
			}
		});
		
		jMainMenu.add(jmiOpenFile);
		jMainMenu.add(jmiSaveFile);
		jMainMenu.addSeparator();
		jMainMenu.add(jmiCloseApp);
		
		
		JMenu jmEffectsMenu = new JMenu("Grafick� algoritmy");
		effectsGroup = new ButtonGroup();
		/* static effects */
		JCheckBoxMenuItem jmiNothing = new JCheckBoxMenuItem("��dn�",true);
		jmiNothing.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				renderer.setViewMethod(Renderer.VIEW_METHOD_NOTHING);
				jlEffectIntensity.setText("");
				jsIntensity.setVisible(false);
			}
		});
		jmEffectsMenu.add(jmiNothing);
		effectsGroup.add(jmiNothing);
		
		JCheckBoxMenuItem jmiEdgeDetection = new JCheckBoxMenuItem("Detekce hran");
		jmiEdgeDetection.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				renderer.setViewMethod(Renderer.VIEW_METHOD_EDGE_DETECTION);
				jlEffectIntensity.setText("Detekce hran");
				jsIntensity.setVisible(false);
			}
		});
		effectsGroup.add(jmiEdgeDetection);
		jmEffectsMenu.add(jmiEdgeDetection);
		
		JCheckBoxMenuItem jmiSobelEdge = new JCheckBoxMenuItem("Sobel");
		jmiSobelEdge.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				jlEffectIntensity.setText("Sobel");
				renderer.setViewMethod(Renderer.VIEW_METHOD_SOBEL);
				jsIntensity.setVisible(false);
			}
		});
		effectsGroup.add(jmiSobelEdge);
		jmEffectsMenu.add(jmiSobelEdge);
		
		JCheckBoxMenuItem jmiGrayscale = new JCheckBoxMenuItem("Odst�ny �edi");
		jmiGrayscale.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				jlEffectIntensity.setText("Odst�ny �edi");
				renderer.setViewMethod(Renderer.VIEW_METHOD_GRAYSCALE);
				jsIntensity.setVisible(false);
			}
		});
		effectsGroup.add(jmiGrayscale);
		jmEffectsMenu.add(jmiGrayscale);
		
		jmEffectsMenu.addSeparator();
		/* dynamic effects */
		JCheckBoxMenuItem jmiGaussianBlur = new JCheckBoxMenuItem("Gaussovsk� rozost�en�");
		jmiGaussianBlur.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				renderer.setViewMethod(Renderer.VIEW_METHOD_GAUSSIAN_BLUR);
				jlEffectIntensity.setText("Gaussovsk� rozost�en�");
				jsIntensity.setVisible(true);
			}
		});
		jmEffectsMenu.add(jmiGaussianBlur);
		effectsGroup.add(jmiGaussianBlur);
		
		JCheckBoxMenuItem jmiContrast = new JCheckBoxMenuItem("Zm�na kontrastu");
		jmiContrast.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				renderer.setViewMethod(Renderer.VIEW_METHOD_CONTRAST);
				jlEffectIntensity.setText("Zm�na kontrastu");
				jsIntensity.setVisible(true);
			}
		});
		jmEffectsMenu.add(jmiContrast);
		effectsGroup.add(jmiContrast);
		
		JCheckBoxMenuItem jmiBrightness = new JCheckBoxMenuItem("Zm�na jasu");
		jmiBrightness.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				renderer.setViewMethod(Renderer.VIEW_METHOD_BRIGHTNESS);
				jlEffectIntensity.setText("Zm�na jasu");
				jsIntensity.setVisible(true);
			}
		});
		jmEffectsMenu.add(jmiBrightness);
		effectsGroup.add(jmiBrightness);
		
		JCheckBoxMenuItem jmiHue = new JCheckBoxMenuItem("Zm�na barevnosti");
		jmiHue.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				renderer.setViewMethod(Renderer.VIEW_METHOD_HUE);
				jlEffectIntensity.setText("Zm�na barevnosti");
				jsIntensity.setVisible(true);
			}
		});
		jmEffectsMenu.add(jmiHue);
		effectsGroup.add(jmiHue);
		
		jmenu.add(jMainMenu);
		jmenu.add(jmEffectsMenu);
		jmenu.add(Box.createHorizontalGlue());
		
		jlEffectIntensity = new JLabel();
		jmenu.add(jlEffectIntensity);
		jsIntensity = new JSlider(SwingConstants.HORIZONTAL,-100,100,0);
		jsIntensity.setName("Intenzita efektu");
		jsIntensity.setMinorTickSpacing(8);
		jsIntensity.setMajorTickSpacing(25);
		jsIntensity.setPaintLabels(true);
		jsIntensity.setPaintTicks(true);
		jsIntensity.setVisible(false);
		jsIntensity.addChangeListener(new ChangeListener() {
			
			@Override
			public void stateChanged(ChangeEvent e) {
				renderer.setEffectIntensity((float) jsIntensity.getValue()/100);
			}
		});
		
		jmenu.add(jsIntensity);
		
		
		jmenu.add(Box.createHorizontalGlue());
		JButton jbAbout = new JButton("O programu");
		jbAbout.setFocusable(false);
		jbAbout.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(frame, "Uk�zka grafick�ch algoritm� pro zpracov�n� obrazu. \nNaps�no jako 2. projekt PGRF3 \n\n\n\n� 2014 Jakub Josef\nUniverzita Hradec Kr�lov�\nFakulta informatiky a managementu","O programu",JOptionPane.INFORMATION_MESSAGE);
			}
		});
		jmenu.add(jbAbout);
		return jmenu;
	}
	private static void saveCanvasToImage(File file, String format){
		
		try{
			ImageIO.write(renderer.getBufferedImage(profile), format, file);
		}catch(IOException e){e.printStackTrace();}
	}
		
	private static Dimension getTextureImageSize(String texturePath){
		BufferedImage readImage = null;
		int h=0,w=0;
		try {
		    readImage = ImageIO.read(new File(texturePath));
		    h= readImage.getHeight();
		    w = readImage.getWidth();
		} catch (Exception e) {
		    readImage = null;
		}
		return new Dimension(w, h);
	}
	private static JFrame getFrame(){
		return frame;
	}

}