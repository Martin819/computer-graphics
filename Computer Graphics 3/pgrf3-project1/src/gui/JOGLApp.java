package gui;



import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLProfile;
import javax.media.opengl.awt.GLCanvas;
import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import com.jogamp.opengl.util.FPSAnimator;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.ObjectInputStream.GetField;

public class JOGLApp {
	private static final String LOOK_AND_FEEL = "javax.swing.plaf.nimbus.NimbusLookAndFeel";
	private static final String APP_TITLE ="PGRF3 Projekt 1 - Jakub Josef";
	private static final int FPS = 60; // animator's target frames per second
	private static final Dimension windowSize= new Dimension(640, 480);

	private static Renderer renderer;
	private static JFrame frame;
	private static JMenu jmMappingMenu;
	private static JCheckBoxMenuItem jchkUseTexture;
	
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
			frame.setJMenuBar(getMenus());
			// setup OpenGL Version 2
	    	GLProfile profile = GLProfile.get(GLProfile.GL2);
	    	GLCapabilities capabilities = new GLCapabilities(profile);
	    	
	    	// The canvas is the widget that's drawn in the JFrame
	    	GLCanvas canvas = new GLCanvas(capabilities);
	    	renderer = new Renderer();
			canvas.addGLEventListener(renderer);
			canvas.addMouseListener(renderer);
			canvas.addMouseMotionListener(renderer);
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
		JMenuBar menuBar = new JMenuBar();
		JMenu jmObjectsMenu = new JMenu("Funkce");
		ButtonGroup btnFunctions = new ButtonGroup();
		
		/* kartÈzskÈ */
		JMenu jmCartessianObjects = new JMenu("KartÈzskÈ");
		
		JMenuItem jmiObjectSedlo = new JCheckBoxMenuItem("Sedlo",true);
		jmiObjectSedlo.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				renderer.setRenderedFunction(Renderer.FUNCTION_SEDLO,true);
			}
		});
		
		jmCartessianObjects.add(jmiObjectSedlo);
		btnFunctions.add(jmiObjectSedlo);
		
		JMenuItem jmiObjectSnake = new JCheckBoxMenuItem("Had");
		jmiObjectSnake.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				renderer.setRenderedFunction(Renderer.FUNCTION_HAD,true);		
			}
		});
		jmCartessianObjects.add(jmiObjectSnake);
		btnFunctions.add(jmiObjectSnake);
		
		JMenuItem jmiObjectCone = new JCheckBoxMenuItem("Kuûel");
		jmiObjectCone.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				renderer.setRenderedFunction(Renderer.FUNCTION_KUZEL,true);
			}
		});
		
		jmCartessianObjects.add(jmiObjectCone);
		btnFunctions.add(jmiObjectCone);
		
		jmObjectsMenu.add(jmCartessianObjects);
		
		/* cylidrickÈ */
		JMenu jmCylindricalObjects = new JMenu("CylindrickÈ");
		JMenuItem jmiObjectSombrero = new JCheckBoxMenuItem("SombrÈro");
		jmiObjectSombrero.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				renderer.setRenderedFunction(Renderer.FUNCTION_SOMBRERO,true);
			}
		});
		jmCylindricalObjects.add(jmiObjectSombrero);
		btnFunctions.add(jmiObjectSombrero);
		
		JMenuItem jmiObjectMushroom = new JCheckBoxMenuItem("Houba");
		jmiObjectMushroom.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				renderer.setRenderedFunction(Renderer.FUNCTION_HOUBA,true);
			}
		});
		jmCylindricalObjects.add(jmiObjectMushroom);
		btnFunctions.add(jmiObjectMushroom);
		
		JMenuItem jmiObjectGlass = new JCheckBoxMenuItem("SkleniËka");
		jmiObjectGlass.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				renderer.setRenderedFunction(Renderer.FUNCTION_SKLENICE,true);
			}
		});
		jmCylindricalObjects.add(jmiObjectGlass);
		btnFunctions.add(jmiObjectGlass);
		jmObjectsMenu.add(jmCylindricalObjects);
		
		/* sfÈrickÈ */
		JMenu jmSphericalObjects = new JMenu("SfÈrickÈ");
		JMenuItem jmiObjectSphere = new JCheckBoxMenuItem("Koule");
		jmiObjectSphere.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				renderer.setRenderedFunction(Renderer.FUNCTION_KOULE,true);
			}
		});
		jmSphericalObjects.add(jmiObjectSphere);
		btnFunctions.add(jmiObjectSphere);
		
		JMenuItem jmiObjectElHead = new JCheckBoxMenuItem("SlonÌ hlava");
		jmiObjectElHead.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				renderer.setRenderedFunction(Renderer.FUNCTION_SLONI_HLAVA,true);
			}
		});
		jmSphericalObjects.add(jmiObjectElHead);
		btnFunctions.add(jmiObjectElHead);
		
		JMenuItem jmiObjectAlien = new JCheckBoxMenuItem("Mimozemöùan");
		jmiObjectAlien.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				renderer.setRenderedFunction(Renderer.FUNCTION_MIMOZEMSTAN, true);
			}
		});
		jmSphericalObjects.add(jmiObjectAlien);
		btnFunctions.add(jmiObjectAlien);
		
		jmObjectsMenu.add(jmSphericalObjects);
		menuBar.add(jmObjectsMenu);
		
		JMenu jmLightsMenu = new JMenu("OsvÏtlenÌ");
		ButtonGroup btnLights = new ButtonGroup();
		JMenuItem jmiLightPerPixel = new JCheckBoxMenuItem("Per Pixel",true);
		jmiLightPerPixel.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				renderer.setLightPerVertex(false);
				toggleMappingMenu(true);
			}
		});
		jmLightsMenu.add(jmiLightPerPixel);
		btnLights.add(jmiLightPerPixel);
		
		JMenuItem jmiLightPerVertex = new JCheckBoxMenuItem("Per Vertex");
		jmiLightPerVertex.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				renderer.setLightPerVertex(true);
				toggleMappingMenu(false);
			}
		});
		jmLightsMenu.add(jmiLightPerVertex);
		btnLights.add(jmiLightPerVertex);
		
		menuBar.add(jmLightsMenu);
		
		jmMappingMenu = new JMenu("Mapping");
		jmMappingMenu.setEnabled(false);
		ButtonGroup btnMapping = new ButtonGroup();
		JMenuItem jmiNoMapping = new JCheckBoxMenuItem("é·dn˝",true);
		jmiNoMapping.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				renderer.setMappingType(Renderer.MAPPING_NO);
			}
		});
		jmMappingMenu.add(jmiNoMapping);
		btnMapping.add(jmiNoMapping);
		JMenuItem jmiNormalMapping = new JCheckBoxMenuItem("Normal");
		jmiNormalMapping.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				renderer.setMappingType(Renderer.MAPPING_NORMAL);
			}
		});
		jmMappingMenu.add(jmiNormalMapping);
		btnMapping.add(jmiNormalMapping);
		
		JMenuItem jmiParallaxMapping = new JCheckBoxMenuItem("Parallax");
		jmiParallaxMapping.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				renderer.setMappingType(Renderer.MAPPING_PARALLAX);
			}
		});
		jmMappingMenu.add(jmiParallaxMapping);
		btnMapping.add(jmiParallaxMapping);
		
		menuBar.add(jmMappingMenu);
		
		
		
		jchkUseTexture = new JCheckBoxMenuItem("PouûÌt texturu");
		jchkUseTexture.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				renderer.setUseTexture(jchkUseTexture.getState());
				toggleMappingMenu(jchkUseTexture.getState());
			}
		});
		menuBar.add(jchkUseTexture);
		menuBar.add(Box.createHorizontalGlue());
		JButton jbResetView = new JButton("Resetovat pohled");
		jbResetView.setFocusable(false);
		jbResetView.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				renderer.resetCamera();
			}
		});
		menuBar.add(jbResetView);
		JButton jbAbout = new JButton("O programu");
		jbAbout.setFocusable(false);
		jbAbout.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(frame, "Naps·no jako 1. projekt PGRF3 \n\n\nOvl·d·nÌ:\nPohyb: WASD, Ctrl, Shift \nOt·ËenÌ: myö \nPrvnÌ osoba: mezernÌk \n\n© 2014 Jakub Josef\nUniverzita Hradec Kr·lovÈ\nFakulta informatiky a managementu","O programu",JOptionPane.INFORMATION_MESSAGE);
			}
		});
		menuBar.add(jbAbout);
		return menuBar;
	}
	private static void toggleMappingMenu(boolean state){
		if(!state || jchkUseTexture.getState()){
			jmMappingMenu.setEnabled(state);
		}
	}

}