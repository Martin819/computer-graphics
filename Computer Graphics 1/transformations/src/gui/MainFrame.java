package gui;

import utils.TransformaceUtils;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.SwingUtilities;

import model.Renderer;
import transforms3D.Mat4;
import transforms3D.Point3D;
import utils.CurveDrawer;
import utils.ObjectDrawer;
import utils.Projections;

public class MainFrame extends JFrame{
	private static Logger log = Logger.getLogger("MainFrame");
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final Dimension VELIKOST = new Dimension(540, 450);
	private Raster rastr=null;
	// menu a menuItemy (kvuli zamykani)
	private JMenu jmMainColorsMenu;
	private JMenu jmProjectionMenu;
	private JMenu jmViewMenu;
	private JMenuItem jmiShowCurvePoints;
	//buttongroupy
	private ButtonGroup bgObjects;
	private ButtonGroup bgCurves;
	private static final int COLORS_MENU_DRAWING = 0;
	private static final int COLORS_MENU_BACKGROUND = 2;
	
	private boolean objectDrawed=false;
	
	//nastaveni vychozi projekce
	private Mat4 projection = null;
	private Renderer renderer = null;
	private Point mousePoint0 = null;
	private Point mousePoint1 = null;
	private MouseMotionListener mmListener = new MouseMotionAdapter() {
		@Override
		public void mouseDragged(MouseEvent e) {
		    rastr.reset();
			doOperation(mousePoint0,e.getPoint(),e);
			mousePoint0=e.getPoint();
			renderer.render();
			
		}
						
	
	};
	/**
	 * @param args
	 */
	public MainFrame() {	
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setSize(VELIKOST);
		setLocationRelativeTo(null);
		rastr = new Raster(getWidth(),getHeight());
		rastr.setSize(VELIKOST);
		rastr.setPreferredSize(new Dimension(getWidth(), getHeight()));
		rastr.setFocusable(true);
		rastr.grabFocus();	
		rastr.requestFocusInWindow();
		projection=Projections.getPerspective(rastr.getVelikostOkna());
		renderer = new Renderer(rastr,rastr.getVelikostOkna());
		renderer.setFirstProjection(projection);
		renderer.showAxes();
		getContentPane().add(getMenu(),BorderLayout.NORTH);
		lockSameColors(rastr.getBarvaPozadi(), COLORS_MENU_DRAWING);
		lockSameColors(renderer.getBarvaKresleni(), COLORS_MENU_BACKGROUND);
		getContentPane().add(rastr);
		pack();
		initKeyListeners();
		initMouseListeners();

	}
	private void initKeyListeners() {
		rastr.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if(objectDrawed){
					//operace na sipkach
					switch(e.getKeyCode()){
						case KeyEvent.VK_LEFT: renderer.cameraMove(Renderer.CAMERA_RIGHT); break;
						case KeyEvent.VK_RIGHT: renderer.cameraMove(Renderer.CAMERA_LEFT); break;
						case KeyEvent.VK_UP: renderer.cameraMove(Renderer.CAMERA_DOWN); break;
						case KeyEvent.VK_DOWN: renderer.cameraMove(Renderer.CAMERA_UP); break;
					}
				}
			}
		});
		
	}
	private void initMouseListeners() {
		rastr.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if(objectDrawed){
					mousePoint0 = e.getPoint();
					rastr.addMouseMotionListener(mmListener);
				}
				
			}
			@Override
			public void mouseReleased(MouseEvent e) {
				if(objectDrawed){
					mousePoint1 = e.getPoint();
					rastr.removeMouseMotionListener(mmListener);
					doOperation(mousePoint0,mousePoint1,e);
					renderer.render();
				}
			}
		});
		rastr.addMouseWheelListener(new MouseWheelListener() {
			
			@Override
			public void mouseWheelMoved(MouseWheelEvent e) {
				if(objectDrawed){	
				float zoom = Math.abs((float)e.getWheelRotation()-0.1f);
					renderer.zoom(new Point3D(zoom,zoom,zoom));
					renderer.render();
				}
				
			}
		});
		
	}
	protected void doOperation(Point mousePoint0, Point mousePoint1,
			MouseEvent e) {
		if(SwingUtilities.isLeftMouseButton(e)){
			int rotX = (mousePoint1.y-mousePoint0.y)/4;
			int rotY = (mousePoint1.x-mousePoint0.x)/4;
			renderer.rotateX(rotX);
	        renderer.rotateY(rotY);
	        if(rotX>0 && rotY>0){
	        	renderer.rotateZ(rotX/rotY);
	        }
	        
		}else if(SwingUtilities.isRightMouseButton(e)){
		  renderer.move(new Point3D(mousePoint1.y-mousePoint0.y,mousePoint1.x - mousePoint0.x,0));
		}
		
	}
	private JMenuBar getMenu() {
		JMenuBar jmBar = new JMenuBar();
		JMenu jmObjectMenu = new JMenu("Objekt");
		JMenuItem jmiPyramid = new JRadioButtonMenuItem("Pyramidu");
		jmiPyramid.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				ObjectDrawer.createPyramid(getRenderer());
				afterObject();
				
			}
		});
		JMenuItem jmiCube = new JRadioButtonMenuItem("Krychli");
		jmiCube.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				ObjectDrawer.createCube(getRenderer());
				afterObject();
			}
		});
		JMenuItem jmiBlock = new JRadioButtonMenuItem("Kvádr");
		jmiBlock.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				ObjectDrawer.createBlock(getRenderer());
				afterObject();
			}
		});
		bgObjects = new ButtonGroup();
		bgObjects.add(jmiCube);
		bgObjects.add(jmiBlock);
		bgObjects.add(jmiPyramid);
		jmObjectMenu.add(jmiCube);
		jmObjectMenu.add(jmiBlock);
		jmObjectMenu.add(jmiPyramid);
		jmBar.add(jmObjectMenu);
		JMenu jmCurveMenu = new JMenu("Køivka");
		JMenuItem jmiBezier = new JRadioButtonMenuItem("Beziér");
		jmiBezier.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				SwingUtilities.invokeLater(new Runnable() {
					
					@Override
					public void run() {
						CurveDrawer.createCurve(getRenderer(), CurveDrawer.BEZIER);
						afterCurve();
					}
				});
				
			}
		});
		JMenuItem jmiFerguson = new JRadioButtonMenuItem("Ferguson");
		jmiFerguson.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				SwingUtilities.invokeLater(new Runnable() {
					
					@Override
					public void run() {
						CurveDrawer.createCurve(getRenderer(), CurveDrawer.FERGUSON);
						afterCurve();
					} 
				});	
			}
		});
		JMenuItem jmiCoons = new JRadioButtonMenuItem("Coons");
		jmiCoons.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				SwingUtilities.invokeLater(new Runnable(){
					public void run(){
						CurveDrawer.createCurve(getRenderer(), CurveDrawer.COONS);
						afterCurve();	
					}
				});	
			}
		});
		bgCurves = new ButtonGroup();
		bgCurves.add(jmiBezier);
		bgCurves.add(jmiFerguson);
		bgCurves.add(jmiCoons);
		jmCurveMenu.add(jmiBezier);
		jmCurveMenu.add(jmiFerguson);
		jmCurveMenu.add(jmiCoons);
		jmiShowCurvePoints = new JCheckBoxMenuItem("Zobrazit body");
		jmiShowCurvePoints.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				CurveDrawer.showPoints(getRenderer(),true);
				
			}
		});
		jmCurveMenu.addSeparator();
		jmCurveMenu.add(jmiShowCurvePoints);
		jmBar.add(jmCurveMenu);
		jmProjectionMenu = new JMenu("Projekce");
		JMenuItem jmiProjectionsParralel= new JRadioButtonMenuItem("Paralelní");
		jmiProjectionsParralel.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if(objectDrawed){
					renderer.setFirstProjection(Projections.getParallel());
					renderer.render();
				}
			}
		});
		JMenuItem jmiProjectionsPerspective = new JRadioButtonMenuItem("Perspektivní",true);
		jmiProjectionsPerspective.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if(objectDrawed){
					renderer.setFirstProjection(Projections.getPerspective(rastr.getVelikostOkna()));
					renderer.render();
				}
			}
		});
		jmProjectionMenu.add(jmiProjectionsParralel);
		jmProjectionMenu.add(jmiProjectionsPerspective);
		
		ButtonGroup bgProjections = new ButtonGroup();
		bgProjections.add(jmiProjectionsParralel);
		bgProjections.add(jmiProjectionsPerspective);
		jmBar.add(jmProjectionMenu);
		jmViewMenu = new JMenu("Pohled");
		JMenuItem jmfirstPerson = new JRadioButtonMenuItem("První osoba");
		jmfirstPerson.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if(objectDrawed){
					renderer.switchCameraView(true);
				}
				
			}
		});
		JMenuItem jmthirdPerson = new JRadioButtonMenuItem("Tøetí osoba",true);
		jmthirdPerson.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if(objectDrawed){
					renderer.switchCameraView(false);
				}
			}
		});
		ButtonGroup bgViews = new ButtonGroup();
		bgViews.add(jmfirstPerson);
		bgViews.add(jmthirdPerson);
		jmViewMenu.add(jmfirstPerson);
		jmViewMenu.add(jmthirdPerson);
		jmBar.add(jmViewMenu);
		
		jmMainColorsMenu = new JMenu("Barva");
		jmMainColorsMenu.add(getColorsMenu("Barva kreslení", this, "setBarvaKresleni", getRenderer().getBarvaKresleni()));
		jmMainColorsMenu.add(getColorsMenu("Barva os", getRenderer(), "setBarvaOs", getRenderer().getBarvaOs()));
		jmMainColorsMenu.add(getColorsMenu("Barva pozadí", this, "setBarvaPozadi",rastr.getBarvaPozadi()));
		jmBar.add(jmMainColorsMenu);
		jmBar.add(Box.createHorizontalGlue());
		
		JButton jbHelp = new JButton("Nápovìda");
		jbHelp.setFocusable(false);
		jbHelp.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				JOptionPane.showMessageDialog(getFrame(), "Levé tlaèítko myši  =>  Otáèení scény\nPravé tlaèítko myši => Pohyb v prostoru\nŠipky na klávesnici => Pohyb kamery\n\n\n\nPoznámka:\nTransformace nepracují s køivkami, protože vykreslování køivek na vlastním rastru je velmi pomalé (jedná se vlastnì o tisíce úseèek)\nTaké z nìjakého dùvodu nefunguje zoom v jar souboru, v eclipsu vše funguje.","Nápovìda",JOptionPane.PLAIN_MESSAGE);
			}
		});
		JButton jbAbout = new JButton("O programu");
		jbAbout.setFocusable(false);
		jbAbout.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(getFrame(),"Napsáno pro úèely projektu PGRF1 na Univerzitì Hradec Králové.\n © 2012 Jakub Josef","O programu",JOptionPane.PLAIN_MESSAGE);
				
			}
		});
		jmBar.add(jbHelp);
		jmBar.add(jbAbout);
		return jmBar;
}
	/**
	 * Metoda vytvori menu barev
	 * @return jmColorsMenu
	 */
	private JMenu getColorsMenu(String caption,final Object object,final String listenerMethod,final Color selected) {
		//listener pro barvy
		ActionListener listener=new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				JMenuItem menuItem = (JMenuItem) arg0.getSource();
				try{
					TransformaceUtils.invokeChangeColorMethod(object,listenerMethod,TransformaceUtils.getColorFromString(menuItem.getActionCommand()));
				}catch(NoSuchFieldException e){
					log.log(Level.SEVERE,"Nepodaøilo se provést zmenu barvy, barva "+menuItem.getActionCommand()+" neexistuje!");
				}catch(NoSuchMethodException e){
					log.log(Level.SEVERE,"Nepodaøilo se provest zmenu barvy, neexistuje metoda zmeny barvy "+listenerMethod+ "!");
				}
				

			}
		};
		final JMenu jmColorsMenu = new JMenu(caption);
		final ButtonGroup btnGroupColors = new ButtonGroup();
		JMenuItem jmiCerna = new JRadioButtonMenuItem("Èerná");
		jmiCerna.setActionCommand("BLACK");
		jmiCerna.addActionListener(listener);
		jmiCerna.setSelected(TransformaceUtils.getColorMenuItemState(jmiCerna, selected));
		btnGroupColors.add(jmiCerna);
		jmColorsMenu.add(jmiCerna);
		
		JMenuItem jmiBila = new JRadioButtonMenuItem("Bílá");
		jmiBila.setActionCommand("WHITE");
		jmiBila.addActionListener(listener);
		jmiBila.setSelected(TransformaceUtils.getColorMenuItemState(jmiBila, selected));
		btnGroupColors.add(jmiBila);
		jmColorsMenu.add(jmiBila);
		
		JMenuItem jmiCervena = new JRadioButtonMenuItem("Èervená");
		jmiCervena.setActionCommand("RED");
		jmiCervena.addActionListener(listener);
		jmiCervena.setSelected(TransformaceUtils.getColorMenuItemState(jmiCervena, selected));
		btnGroupColors.add(jmiCervena);
		jmColorsMenu.add(jmiCervena);
		
		JMenuItem jmiZelena = new JRadioButtonMenuItem("Zelená");
		jmiZelena.setActionCommand("GREEN");
		jmiZelena.addActionListener(listener);
		jmiZelena.setSelected(TransformaceUtils.getColorMenuItemState(jmiZelena, selected));
		btnGroupColors.add(jmiZelena);
		jmColorsMenu.add(jmiZelena);
		
		JMenuItem jmiModra = new JRadioButtonMenuItem("Modrá");
		jmiModra.setActionCommand("BLUE");
		jmiModra.addActionListener(listener);
		jmiModra.setSelected(TransformaceUtils.getColorMenuItemState(jmiModra, selected));
		btnGroupColors.add(jmiModra);
		jmColorsMenu.add(jmiModra);
		jmColorsMenu.addSeparator();
		
		JMenuItem jmiVlastni=new JRadioButtonMenuItem("Vlastní");
		jmiVlastni.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				Color barva=TransformaceUtils.showColorChooser(selected);
				JRadioButtonMenuItem jrbmenuItem = (JRadioButtonMenuItem) arg0.getSource();
				if(barva!=null){
					String barvaHex=Integer.toHexString(barva.getRGB());
					barvaHex = barvaHex.substring(2,barvaHex.length());
					try{
						TransformaceUtils.invokeChangeColorMethod(object,listenerMethod,barva);
					}catch(NoSuchMethodException e){
						log.log(Level.SEVERE,"Nepodaøilo se provest zmenu barvy, neexistuje metoda zmeny barvy "+listenerMethod+ "!");
					}
					
					jrbmenuItem.setText(jrbmenuItem.getText()+" (#"+barvaHex+")");
					btnGroupColors.clearSelection();
					jrbmenuItem.setSelected(true);
				}else{
					//bylo stisknuto Cancel
					jrbmenuItem.setSelected(false);
				}
			}
		});
		jmColorsMenu.add(jmiVlastni);
		return jmColorsMenu;
	}
	/**
	 * Metoda osetruje zamykani stejnych barev mezi ruznymi menu
	 * @param barva Barva ktera se ma osetrovat
	 * @param componentID ID menu ve kterem se ma zamceni provest
	 */
	private void lockSameColors(Color barva,int componentID) {
		JMenu drawColorsMenu = (JMenu) jmMainColorsMenu.getMenuComponent(componentID);
		Component[] colorsMenuComponents =  drawColorsMenu.getMenuComponents();
		//posledni prvek je vlastni barva, ten nas nezajima
		for (int i = 0; i < colorsMenuComponents.length-1; i++) {
			if(colorsMenuComponents[i] instanceof JRadioButtonMenuItem){
				//nejedna se o policko vlastni barvy, osetrime vybirani barev
				JRadioButtonMenuItem jrMenuItem = (JRadioButtonMenuItem) colorsMenuComponents[i];
					if(!jrMenuItem.isEnabled()){
						jrMenuItem.setEnabled(true);
					}
					Color barvaItemu;
					try{
						barvaItemu= TransformaceUtils.getColorFromString(jrMenuItem.getActionCommand());
					}catch(NoSuchFieldException e){
						log.log(Level.SEVERE,"Nepodaøilo se provést zmenu barvy, barva "+jrMenuItem.getActionCommand()+" neexistuje!");
						barvaItemu=null;
					}
					if(barvaItemu.equals(barva)){
						jrMenuItem.setEnabled(false);
					}
			}
		}
		
	}
	private void afterObject(){
		jmiShowCurvePoints.setEnabled(false);
		jmProjectionMenu.setEnabled(true);
		jmViewMenu.setEnabled(true);
		bgCurves.clearSelection();
		objectDrawed=true;
		
	}
	private void afterCurve() {
		jmiShowCurvePoints.setEnabled(true);
		jmProjectionMenu.setEnabled(false);
		jmViewMenu.setEnabled(false);
		bgObjects.clearSelection();
		objectDrawed=false;
	}
	public void setBarvaKresleni(Color barva){
		getRenderer().setBarvaKresleni(barva);
		lockSameColors(barva, COLORS_MENU_DRAWING);
	}
	public void setBarvaPozadi(Color barva){
		rastr.setBarvaPozadi(barva);
		lockSameColors(barva, COLORS_MENU_DRAWING);
	}
	public Renderer getRenderer() {
		return renderer;
	}
	public MainFrame getFrame(){
		return this;
	}

}
