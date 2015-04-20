package gui;

import java.awt.Color;
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
import model.GPU.DrawingMethod;
import model.objects.Krychle;
import model.objects.Pyramida;
import model.objects.RasterObject;
import transforms3D.Mat4;
import transforms3D.Mat4RotXYZ;
import transforms3D.Mat4Scale;
import transforms3D.Mat4Transl;
import transforms3D.Point3D;
import utils.Projections;


public class MainFrame extends JFrame {
	
	/**
	 * default serialUID
	 */
	private static final long serialVersionUID = 1L;
	private final Dimension VELIKOST = new Dimension(500, 500);
	private Raster rastr=null;
	private Mat4 projection = null;
	private Renderer renderer = null;
	private Scene scene = null;
	
	//mysi body
	private Point mousePoint0,mousePoint1;
	
	//menuitemy
	private JMenuItem jmiKrychle,jmiPyramida,jmiControlKrychly,jmiControlPyramidu,jmiControlOboje;
	//buttongroupy
	private ButtonGroup bgShow,bgControl,bgProjection;
	private MouseMotionListener mmListener = new MouseMotionAdapter() {
		@Override
		public void mouseDragged(MouseEvent e) {
		    rastr.reset();
			doOperation(mousePoint0,e.getPoint(),e);
			mousePoint0=e.getPoint();
		}
						
	
	};
	
	
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
	projection = Projections.getPerspective(rastr.getVelikostOkna());
	renderer = new Renderer(rastr);
	scene = new Scene(renderer);
	scene.setProjection(projection);
	renderer.initGPU(scene);
	
	initKeyListeners();
	initMouseListeners();
	setJMenuBar(getMenus());
	getContentPane().add(rastr);
	pack();
	}

	/**
	 * Ridi ovladani operaci nad tlacitky
	 * @param mousePoint0
	 * @param mousePoint1
	 * @param e 
	 */
	protected void doOperation(Point mousePoint0, Point mousePoint1, MouseEvent e) {
		if(SwingUtilities.isLeftMouseButton(e)){
			int rotX = (mousePoint1.x-mousePoint0.x)/2;
			if(scene.getActualObject()!=null){
				renderer.rotateX(scene.getActualObject(),new Point(rotX,0));
			}else{
				for (RasterObject object : scene.getObjects()) {
					renderer.rotateX(object,new Point(rotX,0));
				}
			}
		}else if(SwingUtilities.isRightMouseButton(e)){
			int rotY = (mousePoint1.y-mousePoint0.y)/2;
			if(scene.getActualObject()!=null){
				renderer.rotateY(scene.getActualObject(),new Point(0,rotY));
			}else{
				for (RasterObject object : scene.getObjects()) {
					renderer.rotateY(object,new Point(0,rotY));
				}
			}
		}else if(SwingUtilities.isMiddleMouseButton(e)){
			int rotX = (mousePoint1.x-mousePoint0.x)/2;
			if(scene.getActualObject()!=null){
				renderer.rotateZ(scene.getActualObject(), new Point3D(0,0,rotX));
			}else{
				for (RasterObject object : scene.getObjects()) {
					renderer.rotateZ(object,new Point3D(0,0,rotX));
				}
			}
		}
		
	}


	private void initMouseListeners() {
		rastr.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
					mousePoint0 = e.getPoint();
					rastr.addMouseMotionListener(mmListener);
				
			}
			@Override
			public void mouseReleased(MouseEvent e) {
				if(scene.getActualObject()!=null){
					mousePoint1 = e.getPoint();
					rastr.removeMouseMotionListener(mmListener);
					doOperation(mousePoint0,mousePoint1,e);
				}
			}
		});
		rastr.addMouseWheelListener(new MouseWheelListener() {
			
			@Override
			public void mouseWheelMoved(MouseWheelEvent e) {
				float zoom = Math.abs((float)e.getWheelRotation()-0.1f);
					renderer.scale(scene.getActualObject(),zoom);
				}
		});
		
	}


	private JMenuBar getMenus() {
		JMenuBar jmBar = new JMenuBar();
		JMenu jmObjects = new JMenu("Nakreslit");
		jmiKrychle = new JCheckBoxMenuItem("Krychli");
		jmiKrychle.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if(!scene.isObjectDrawed("Krychle")){
					RasterObject object = new Krychle(Color.YELLOW,"Krychle");
					object.setPosition(new Mat4Transl(0.1, 0.1, 0));
					object.setRotate(new Mat4RotXYZ(Math.toRadians(15), Math.toRadians(15), Math.toRadians(15)));
					object.setScale(new Mat4Scale(0.2, 0.2, 0.2));
					object.computeMat();
					object.setActive(true);
					scene.addObject(object);
					scene.setActualObject("Krychle");
				}else{
				  scene.removeObject("Krychle");
				}
				refreshControlsMenu();
				scene.redrawScene();
			}
		});
		jmObjects.add(jmiKrychle);
		jmiPyramida = new JCheckBoxMenuItem("Pyramidu");
		jmiPyramida.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if(!scene.isObjectDrawed("Pyramida")){
					RasterObject object = new Pyramida(Color.YELLOW,"Pyramida");
					object.setPosition(new Mat4Transl(0.15, 0.15, 0));
					object.setRotate(new Mat4RotXYZ(Math.toRadians(15), Math.toRadians(15), Math.toRadians(15)));
					object.setScale(new Mat4Scale(0.2, 0.2, 0.2));
					object.computeMat();
					object.setActive(true);
					scene.addObject(object);
					scene.setActualObject("Pyramida");
				}else{
					scene.removeObject("Pyramida");
				}
				refreshControlsMenu();
				scene.redrawScene();
			}
		});
		jmObjects.add(jmiPyramida);
		jmObjects.addSeparator();
		JMenuItem jmiVycistit = new JMenuItem("Smazat plátno");
		jmiVycistit.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				scene.clear();
				rastr.reset();
				jmiKrychle.setSelected(false);
				jmiPyramida.setSelected(false);
				bgControl.clearSelection();
				
				
			}
		});
		jmObjects.add(jmiVycistit);
		jmBar.add(jmObjects);
		
		bgControl = new ButtonGroup();
		JMenu jmControl = new JMenu("Ovládáte");
		jmiControlKrychly = new JRadioButtonMenuItem("Krychly");
		jmiControlKrychly.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				scene.setActualObject("Krychle");
			}
		});
		bgControl.add(jmiControlKrychly);
		jmControl.add(jmiControlKrychly);
		jmiControlPyramidu = new JRadioButtonMenuItem("Pyramidu");
		jmiControlPyramidu.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				scene.setActualObject("Pyramida");
			}
		});
		bgControl.add(jmiControlPyramidu);
		jmControl.add(jmiControlPyramidu);
		jmiControlOboje = new JRadioButtonMenuItem("Oboje");
		jmiControlOboje.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				scene.setActualObject(null);
			}
		});
		bgControl.add(jmiControlOboje);
		jmControl.add(jmiControlOboje);
		
		jmBar.add(jmControl);
		bgShow = new ButtonGroup();
		JMenu jmShow = new JMenu("Zobrazit");
		JMenuItem jmiDrat = new JRadioButtonMenuItem("Drátový model");
		jmiDrat.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				rastr.reset();
				scene.changeDrawingType(DrawingMethod.WIREFRAME);
				
			}
		});
		JMenuItem jmiInterpolace = new JRadioButtonMenuItem("Interpolace",true);
		jmiInterpolace.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				rastr.reset();
				scene.changeDrawingType(DrawingMethod.INTERPOLATIONS);
				
			}
		});		
		JMenuItem jmiOboje = new JRadioButtonMenuItem("Oboje");
		jmiOboje.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				rastr.reset();
				scene.changeDrawingType(DrawingMethod.BOTH);
				
			}
		});		
		jmShow.add(jmiDrat);
		bgShow.add(jmiDrat);
		jmShow.add(jmiInterpolace);
		bgShow.add(jmiInterpolace);
		jmShow.add(jmiOboje);
		bgShow.add(jmiOboje);
		jmBar.add(jmShow);
		
		bgProjection = new ButtonGroup();
		JMenu jmProjection = new JMenu("Projekce");
		JMenuItem jmiParralel = new JRadioButtonMenuItem("Paralelní");
		jmiParralel.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				rastr.reset();
				scene.setProjection(Projections.getParallel(rastr.getVelikostOkna()));
				scene.redrawScene();
			}
		});
		JMenuItem jmiPerspective = new JRadioButtonMenuItem("Perspektivní",true);
		jmiPerspective.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				rastr.reset();
				scene.setProjection(Projections.getPerspective(rastr.getVelikostOkna()));
				scene.redrawScene();
			}
		});
		jmProjection.add(jmiParralel);
		bgProjection.add(jmiParralel);
		jmProjection.add(jmiPerspective);
		bgProjection.add(jmiPerspective);
		jmBar.add(jmProjection);
		jmBar.add(Box.createHorizontalGlue());
		JButton jbHelp = new JButton("Nápovìda");
		jbHelp.setFocusable(false);
		jbHelp.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				JOptionPane.showMessageDialog(null, "Levé tlaèítko myši  =>  Otáèení scény podle X\nPravé tlaèítko myši => Otáèení scény podle Y\nProstøední tlaèítko myši => Otáèení scény podle Z\nŠipky na klávesnici => Pohyb kamery\n","Nápovìda",JOptionPane.PLAIN_MESSAGE);
			}
		});
		jmBar.add(jbHelp);
		JButton jbAbout = new JButton("O programu");
		jbAbout.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				JOptionPane.showMessageDialog(getFrame(),"Napsáno pro úèely projektu PGRF2 na Univerzitì Hradec Králové.\n © 2013 Jakub Josef","O programu",JOptionPane.PLAIN_MESSAGE);
			}
		});
		jmBar.add(jbAbout);
		
		return jmBar;
	}
	/**
	 * Metoda hlida provazanost objektu s prepinacem ovladani objektu
	 */
	protected void refreshControlsMenu() {	
		if(scene.getObjects().size()!=0){
			String lastObjectName = scene.getObjects().get(scene.getObjects().size()-1).getName();
			if(lastObjectName.equals("Krychle")){
				bgControl.clearSelection();
				jmiControlKrychly.setSelected(true);
			}else if(lastObjectName.equals("Pyramida")){
				bgControl.clearSelection();
				jmiControlPyramidu.setSelected(true);
			}
		}else{
			bgControl.clearSelection();
		}
	}

	private void initKeyListeners() {
		rastr.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
					//operace na sipkach
					switch(e.getKeyCode()){
						case KeyEvent.VK_LEFT: scene.cameraMove(Scene.CAMERA_RIGHT); break;
						case KeyEvent.VK_RIGHT: scene.cameraMove(Scene.CAMERA_LEFT); break;
						case KeyEvent.VK_UP: scene.cameraMove(Scene.CAMERA_DOWN); break;
						case KeyEvent.VK_DOWN: scene.cameraMove(Scene.CAMERA_UP); break;
					}
				}
		});
		
	}
	private MainFrame getFrame(){
		return this;
	}
	
}
