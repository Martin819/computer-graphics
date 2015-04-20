package lupa.app;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FileDialog;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.media.opengl.awt.GLCanvas;
import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import lupa.helpers.Enums.*;
import lupa.helpers.ColorChooser;
import lupa.helpers.ImageHelpers;
import lupa.model.JOGLListener;

public class LupaApp {
	private static final String TITLE = "Lupa 1.0a";
	private static final String LOOK_AND_FEEL = "javax.swing.plaf.nimbus.NimbusLookAndFeel";
	private static final Dimension WINDOW_SIZE=new Dimension(500,500);
	
	private JFrame frame=null;
	private JOGLListener joglListener=null;
	private GLCanvas canvas=null;
	
	private JMenu jmCircleRadius,jmScaleSizes,jmMagnifierColor;
	private JMenuItem jmiCircleOwn,jmiColorsOwn;
	

	
		public LupaApp() {
			frame = new JFrame(TITLE);
			frame.setSize(WINDOW_SIZE);
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.setJMenuBar(getMenus());
			frame.addComponentListener(new ComponentAdapter() {
				public void componentResized(ComponentEvent evt){
					canvas.repaint();
				}
			});
			frame.getRootPane().grabFocus();
			canvas = new GLCanvas();
			canvas.setFocusable(true);
			canvas.requestFocusInWindow();
			canvas.addKeyListener(
					new KeyAdapter() {
						@Override
						public void keyPressed(KeyEvent e) {
							if(joglListener.isImageDrawed()){
								//operace na sipkach
								switch(e.getKeyCode()){
									case KeyEvent.VK_LEFT: moveMagnifier(new Point(-10,0)); break;
									case KeyEvent.VK_RIGHT: moveMagnifier(new Point(10,0)); break;
									case KeyEvent.VK_UP: moveMagnifier(new Point(0,-10)); break;
									case KeyEvent.VK_DOWN: moveMagnifier(new Point(0,10)); break;
									case KeyEvent.VK_ADD: extendMagnifier(10);break;
									case KeyEvent.VK_SUBTRACT: extendMagnifier(-10);break;
								}
							}
						}
					});
			joglListener=new JOGLListener(frame);
			joglListener.setMagnifierRadius(MagnifierSizes.SMALL.size);
			canvas.addGLEventListener(joglListener);
			canvas.addMouseMotionListener(new MouseMotionAdapter() {
				@Override
				public void mouseMoved(MouseEvent arg0) {
					if(joglListener.isImageDrawed()){
						
						joglListener.setMousePosition(new Point(arg0.getX()+5,arg0.getY()+35));
						canvas.repaint();
					}
				}
			});
			frame.add(canvas);
			frame.setVisible(true);
		}
		public static void main(String args[]){
			try {
					UIManager.setLookAndFeel(LOOK_AND_FEEL);
				} catch (ClassNotFoundException | InstantiationException
						| IllegalAccessException
						| UnsupportedLookAndFeelException e) {
							System.out.println("Nepodarilo se nastavit L&F!\n" +
									"L&F "+LOOK_AND_FEEL+" asi neexistuje.");
							e.printStackTrace();
					}
			new LupaApp();
		}
		/**
		 * Metoda vykresli obrazek do GLCanvasu
		 * @param file soubor s obrazkem
		 * @param fitToFrame zvetsit frame přesne na velikost okna
		 */
		public void drawImage(File file,boolean fitToFrame){
			BufferedImage image=null;
			try {
				image = ImageIO.read(file);
			} catch (IOException e) {
				e.printStackTrace();
			}
			if(fitToFrame){
				frame.setSize(new Dimension(image.getWidth(), image.getHeight()));
			}
			joglListener.setImage(image);
			canvas.repaint();
		}
		/**
		 * Metoda posune lupu o dany posun Point(x,y)
		 * @param movement bod o ktery se ma posunout
		 */
		private void moveMagnifier(Point movement){
			Point newPosition=null;
			Point actualPosition = joglListener.getMousePosition();
			if(actualPosition!=null){
				newPosition=actualPosition;
			}else{
				newPosition=new Point();
			}
			newPosition.x=newPosition.x+movement.x;
			newPosition.y=newPosition.y+movement.y;
			joglListener.setMousePosition(newPosition);
			canvas.repaint();
		}
		/**
		 * Metoda zvetsi (nebo zmensi) lupu o zadany integer
		 * @param extendAbout o kolik zvetsit nebo zmensit
		 */
		private void extendMagnifier(int extendAbout) {
			joglListener.setMagnifierRadius(joglListener.getMagnifierRadius()+extendAbout);
			canvas.repaint();
		}
		/**
		 * Metoda vycisti platno, odebere z joglu obrazek, vycisti canvas a nastavi vychozi velikost okna
		 */
		public void reset(){
			joglListener.clear();
			canvas.repaint();
			frame.setSize(WINDOW_SIZE);
		}
		
		private JFrame getFrame() {
			return frame;
		}
		/**
		 * Posklada JMenuBar pro pouziti v JFramu
		 * @return JMenuBar menu bar
		 */
		private JMenuBar getMenus() {
			JMenuBar jm = new JMenuBar();
			/** menu obrazku **/
			JMenu jmMain = new JMenu("Obrázek");
			JMenuItem jmiOpen = new JMenuItem("Otevřít");
			jmiOpen.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent arg0) {
					FileDialog fd = new FileDialog(getFrame(), "Vyberte obrázek",FileDialog.LOAD);
 					//nefunguje nad Windows Native file nabidkou
					//fd.setFileFilter(new FileNameExtensionFilter("Obrázky","jpg","png","bmp","gif" ));
					fd.setVisible(true);
					String filepath=fd.getDirectory()+System.getProperty("file.separator")+fd.getFile();
					boolean isImage;
					if(!(isImage = ImageHelpers.isImage(filepath))){
						JOptionPane.showMessageDialog(getFrame(), "Vybraný soubor není obrázkem","Chyba načítání obrázku",JOptionPane.ERROR_MESSAGE);
					}
					if(fd.getFile()!=null && isImage){
						drawImage(new File(filepath),true);
						toggleMenu();
					}
					
					
				}
			});
			JMenuItem jmiClose = new JMenuItem("Zavřít");
			jmiClose.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent arg0) {
					reset();
					toggleMenu();
					
				}
			});
			jmMain.add(jmiOpen);
			jmMain.add(jmiClose);
			jm.add(jmMain);
			ButtonGroup bgCircleSizes = new ButtonGroup();
			jmCircleRadius = new JMenu("Velikost lupy");
			jmCircleRadius.setEnabled(false);
			
			JMenuItem jmiCircleSmall = new JRadioButtonMenuItem("Malá",true);
			jmiCircleSmall.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent arg0) {
					joglListener.setMagnifierRadius(MagnifierSizes.SMALL.size);
					canvas.repaint();
					jmiCircleOwn.setText("Vlastní");
				}
			});
			bgCircleSizes.add(jmiCircleSmall);
			JMenuItem jmiCircleMiddle = new JRadioButtonMenuItem("Střední");
			jmiCircleMiddle.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent arg0) {
					joglListener.setMagnifierRadius(MagnifierSizes.MIDDLE.size);
					canvas.repaint();
					jmiCircleOwn.setText("Vlastní");
				}
			});
			bgCircleSizes.add(jmiCircleMiddle);
			JMenuItem jmiCircleBig = new JRadioButtonMenuItem("Velká");
			jmiCircleBig.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent arg0) {
					joglListener.setMagnifierRadius(MagnifierSizes.BIG.size);
					canvas.repaint();
					jmiCircleOwn.setText("Vlastní");
				}
			});
			bgCircleSizes.add(jmiCircleBig);
			jmiCircleOwn = new JRadioButtonMenuItem("Vlastní");
			jmiCircleOwn.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					int radius = Integer.parseInt(JOptionPane.showInputDialog(getFrame(), "Zadejte poloměr lupy (px):"));
					joglListener.setMagnifierRadius(radius);
					canvas.repaint();
					jmiCircleOwn.setText("Vlastní ("+String.valueOf(radius)+")");
				}
			});
			bgCircleSizes.add(jmiCircleOwn);
			//poradi
			jmCircleRadius.add(jmiCircleSmall);
			jmCircleRadius.add(jmiCircleMiddle);
			jmCircleRadius.add(jmiCircleBig);
			jmCircleRadius.addSeparator();
			jmCircleRadius.add(jmiCircleOwn);
			jm.add(jmCircleRadius);
			
			/** menu meritka lupy **/
			ButtonGroup bgScaleSizes=new ButtonGroup();
			jmScaleSizes = new JMenu("Měřítko lupy");
			jmScaleSizes.setEnabled(false);
			JMenuItem jmiScaleSmallUp = new JRadioButtonMenuItem("Malé zvětšení",true);
			jmiScaleSmallUp.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent arg0) {
					joglListener.setMagnifierScale(MagnifierScales.SMALL_UP.size);
					canvas.repaint();
					
				}
			});
			bgScaleSizes.add(jmiScaleSmallUp);
			JMenuItem jmiScaleMiddleUp = new JRadioButtonMenuItem("Střední zvětšení");
			jmiScaleMiddleUp.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent arg0) {
					joglListener.setMagnifierScale(MagnifierScales.MIDDLE_UP.size);
					canvas.repaint();
					
				}
			});
			bgScaleSizes.add(jmiScaleMiddleUp);
			JMenuItem jmiScaleBigUp = new JRadioButtonMenuItem("Velké zvětšení");
			jmiScaleBigUp.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent arg0) {
					joglListener.setMagnifierScale(MagnifierScales.BIG_UP.size);
					canvas.repaint();
					
				}
			});
			bgScaleSizes.add(jmiScaleBigUp);
			
			JMenuItem jmiScaleSmallDown = new JRadioButtonMenuItem("Malé zmenšení");
			jmiScaleSmallDown.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent arg0) {
					joglListener.setMagnifierScale(MagnifierScales.SMALL_DOWN.size);
					canvas.repaint();
					
				}
			});
			bgScaleSizes.add(jmiScaleSmallDown);
			JMenuItem jmiScaleMiddleDown = new JRadioButtonMenuItem("Střední zmenšení");
			jmiScaleMiddleDown.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent arg0) {
					joglListener.setMagnifierScale(MagnifierScales.MIDDLE_DOWN.size);
					canvas.repaint();
					
				}
			});
			bgScaleSizes.add(jmiScaleMiddleDown);
			JMenuItem jmiScaleBigDown = new JRadioButtonMenuItem("Velké zmenšení");
			jmiScaleBigDown.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent arg0) {
					joglListener.setMagnifierScale(MagnifierScales.BIG_DOWN.size);
					canvas.repaint();
					
				}
			});
			bgScaleSizes.add(jmiScaleBigDown);
			
			//poradi
			jmScaleSizes.add(jmiScaleSmallUp);
			jmScaleSizes.add(jmiScaleMiddleUp);
			jmScaleSizes.add(jmiScaleBigUp);
			jmScaleSizes.addSeparator();
			jmScaleSizes.add(jmiScaleSmallDown);
			jmScaleSizes.add(jmiScaleMiddleDown);
			jmScaleSizes.add(jmiScaleBigDown);
						
			jm.add(jmScaleSizes);
			
			/** menu barev okraje **/
			ButtonGroup bgMagnifierColors= new ButtonGroup();
			jmMagnifierColor = new JMenu("Barva okraje");
			jmMagnifierColor.setEnabled(false);
			JMenuItem jmiColorsBlack = new JRadioButtonMenuItem("Černá",true);
			jmiColorsBlack.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent arg0) {
					joglListener.setMagnifierColor(Color.BLACK);
					canvas.repaint();
					jmiColorsOwn.setText("Vlastní");
				}
			});
			bgMagnifierColors.add(jmiColorsBlack);
			JMenuItem jmiColorsWhite = new JRadioButtonMenuItem("Bílá");
			jmiColorsWhite.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent arg0) {
					joglListener.setMagnifierColor(Color.WHITE);
					canvas.repaint();
					jmiColorsOwn.setText("Vlastní");
				}
			});
			bgMagnifierColors.add(jmiColorsWhite);
			JMenuItem jmiColorsGreen = new JRadioButtonMenuItem("Zelená");
			jmiColorsGreen.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent arg0) {
					joglListener.setMagnifierColor(Color.GREEN);
					canvas.repaint();
					jmiColorsOwn.setText("Vlastní");
				}
			});
			bgMagnifierColors.add(jmiColorsGreen);
			JMenuItem jmiColorsBlue = new JRadioButtonMenuItem("Modrá");
			jmiColorsBlue.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent arg0) {
					joglListener.setMagnifierColor(Color.BLUE);
					canvas.repaint();
					jmiColorsOwn.setText("Vlastní");
				}
			});
			bgMagnifierColors.add(jmiColorsBlue);
			JMenuItem jmiColorsRed = new JRadioButtonMenuItem("Červená");
			jmiColorsRed.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent arg0) {
					joglListener.setMagnifierColor(Color.RED);
					canvas.repaint();
					jmiColorsOwn.setText("Vlastní");
				}
			});
			bgMagnifierColors.add(jmiColorsRed);
			jmiColorsOwn = new JRadioButtonMenuItem("Vlastní");
			jmiColorsOwn.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					Color color = ColorChooser.showColorChooser(joglListener.getMagnifierColor());
					joglListener.setMagnifierColor(color);
					canvas.repaint();
					String barvaHex=Integer.toHexString(color.getRGB());
					barvaHex = barvaHex.substring(2,barvaHex.length());
					jmiColorsOwn.setText("Vlastní (#"+barvaHex+")");
				}
			});
			bgMagnifierColors.add(jmiColorsOwn);
			
			
			jmMagnifierColor.add(jmiColorsBlack);
			jmMagnifierColor.add(jmiColorsWhite);
			jmMagnifierColor.add(jmiColorsGreen);
			jmMagnifierColor.add(jmiColorsBlue);
			jmMagnifierColor.add(jmiColorsRed);
			jmMagnifierColor.addSeparator();
			jmMagnifierColor.add(jmiColorsOwn);
			jm.add(jmMagnifierColor);
			jm.add(Box.createHorizontalGlue());
			JButton jbAbout = new JButton("O programu");
			jbAbout.setFocusable(false);
			jbAbout.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					JOptionPane.showMessageDialog(getFrame(), "Napsáno jako seminární projekt PGRF2 Lupa \n\n\nOvládání:\nPohyb lupy: šipky na klávesnici nebo myš\nZvětšení lupy: +\nZmenšení lupy: -\n\n© 2013 Jakub Josef\nUniverzita Hradec Králové\nFakulta informatiky a managementu","O programu",JOptionPane.INFORMATION_MESSAGE);
				}
			});
			jm.add(jbAbout);
			return jm;
		}
		/**
		 * Metoda prepne zobrazeni menu, aktivuje ovladaci prvky pro nacteny obrazek¨
		 * Neguje aktualni stav nad polozkami menu 
		 */
		private void toggleMenu(){
			jmCircleRadius.setEnabled(!jmCircleRadius.isEnabled());
			jmScaleSizes.setEnabled(!jmScaleSizes.isEnabled());
			jmMagnifierColor.setEnabled(!jmMagnifierColor.isEnabled());
		}
		
}
