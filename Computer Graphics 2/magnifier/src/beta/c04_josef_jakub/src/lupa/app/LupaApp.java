package lupa.app;

import java.awt.Dimension;
import java.awt.FileDialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.media.opengl.awt.GLCanvas;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JRadioButtonMenuItem;

import lupa.helpers.ImageHelpers;
import lupa.model.JOGLListener;

public class LupaApp {
	private JFrame frame=null;
	private JOGLListener joglListener=null;
	private GLCanvas canvas=null;
	
	public enum MagnifierSizes{
		SMALL(30),MIDDLE(60),BIG(100);
		public int size;

		private MagnifierSizes(int size){
			this.size=size;
		}
	}
	
		public LupaApp() {
			frame = new JFrame("Lupa 0.1");
			frame.setSize(500,500);
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.setJMenuBar(getMenus());
			canvas = new GLCanvas();
			joglListener=new JOGLListener(frame);
			joglListener.setMagnifierRadius(MagnifierSizes.SMALL.size);
			canvas.addGLEventListener(joglListener);
			canvas.addMouseMotionListener(new MouseMotionAdapter() {
				@Override
				public void mouseMoved(MouseEvent arg0) {
					joglListener.setMousePosition(arg0.getPoint());
					canvas.repaint();
				}
			});
			frame.add(canvas);
			frame.setVisible(true);
		}
		public static void main(String args[]){
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
		
		
		private JFrame getFrame() {
			return frame;
		}
		/**
		 * Posklada JMenuBar pro pouziti v JFramu
		 * @return JMenuBar menu bar
		 */
		private JMenuBar getMenus() {
			JMenuBar jm = new JMenuBar();
			JMenu jmMain = new JMenu("Obrázek");
			JMenuItem jmiOpen = new JMenuItem("Otevřít");
			jmiOpen.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent arg0) {
					FileDialog fd = new FileDialog(getFrame(), "Vyberte obrázek",FileDialog.LOAD);
 					//fd.setFileFilter(new FileNameExtensionFilter("Obrázky","jpg","png","bmp","gif" ));
					fd.setVisible(true);
					String filepath=fd.getDirectory()+System.getProperty("file.separator")+fd.getFile();
					boolean isImage;
					if(!(isImage = ImageHelpers.isImage(filepath))){
						JOptionPane.showMessageDialog(getFrame(), "Vybraný soubor není obrázkem","Chyba načítání obrázku",JOptionPane.ERROR_MESSAGE);
					}
					if(fd.getFile()!=null && isImage){
						drawImage(new File(filepath),true);
					}
					
					
				}
			});
			jmMain.add(jmiOpen);
			jm.add(jmMain);
			
			JMenu jmCircleRadius = new JMenu("Velikost lupy");
			
			JMenuItem jmiCircleSmall = new JRadioButtonMenuItem("Malá",true);
			jmiCircleSmall.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent arg0) {
					joglListener.setMagnifierRadius(MagnifierSizes.SMALL.size);
					canvas.repaint();
				}
			});
			JMenuItem jmiCircleMiddle = new JRadioButtonMenuItem("Střední");
			jmiCircleMiddle.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent arg0) {
					joglListener.setMagnifierRadius(MagnifierSizes.MIDDLE.size);
					canvas.repaint();
				}
			});
			JMenuItem jmiCircleBig = new JRadioButtonMenuItem("Velká");
			jmiCircleBig.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent arg0) {
					joglListener.setMagnifierRadius(MagnifierSizes.BIG.size);
					canvas.repaint();
				}
			});
			JMenuItem jmiCircleOwn = new JRadioButtonMenuItem("Vlastní");
			jmiCircleOwn.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					joglListener.setMagnifierRadius(Integer.parseInt(JOptionPane.showInputDialog(getFrame(), "Zadejte poloměr lupy (px):")));
					canvas.repaint();
				}
			});
			
			jmCircleRadius.add(jmiCircleSmall);
			jmCircleRadius.add(jmiCircleMiddle);
			jmCircleRadius.add(jmiCircleBig);
			jmCircleRadius.addSeparator();
			jmCircleRadius.add(jmiCircleOwn);
			
			
			jm.add(jmCircleRadius);
			return jm;
		}
		
}
