package gui;

import helpers.MalovaniUtils;
import helpers.SpringUtilities;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseMotionListener;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.SpringLayout;
import javax.swing.SwingUtilities;
import javax.swing.border.BevelBorder;

import model.Bod;
import model.Kruznice;
import model.Usecka;
import model.Vyplnovani;

/**
 * Hlavní aplikaèní tøída celého okna Obsahuje metody initDialogs,initMenu a
 * initToolBar, ktere pripravy dodatecne casti okna.
 * 
 * @author Jakub Josef
 * 
 */
public class MainFrame extends JFrame {
	/**
	 * serializeUID
	 */
	private static final long serialVersionUID = 1L;
	// logger
	private static Logger log = Logger.getLogger(MainFrame.class.getClass().getName());

	// enumerace
	public enum Algorithm {
		TRIVIAL, DDA, BRESENHAM,SEED,LINESEED,SCANLINE;
	}

	public enum Methods {
		CLICKING, DRAGGING,
	}

	public enum Tools {
		LINE, CIRCLE, OBJECT;;
	}
	// nastaveni vychozich algorytmu
	private Algorithm lineAlgorithm = Algorithm.TRIVIAL;
	private Algorithm fillAlgorithm = Algorithm.SCANLINE;
	// nastaveni vychozi metody
	private Methods method = Methods.CLICKING;
	// nastaveni vychoziho nastroje
	private Tools tool = Tools.LINE;
	// nastaveni vychozi velikosti okna
	private final Dimension VELIKOST = new Dimension(540, 450);
	//nastaveni vychozi barvy kresleni
	private Color barva=Color.BLACK;
	//nastaveni vychozi barvy vyplnovani
	private Color barvaVyplneni=Color.RED;
	// privatni atributy
	private Raster rastr;
	private Usecka usecka;
	private Kruznice kruznice;
	// mysi prepinac
	private boolean secondClick = false;
	// dialogy
	private JDialog dialogUsecka;
	private JDialog dialogKruznice;
	// labely
	private JLabel jlErrorUsecka;
	private JLabel jlErrorKruznice;
	// textova pole
	private JTextField jtPocatecniBodUsecky;
	private JTextField jtKoncovyBodUsecky;
	private JTextField jtPocatecniBodKruznice;
	private JTextField jtPolomerKruznice;
	//menu pro barvy a metody(kvuli zamykani)
	private JMenu jmMainColorsMenu;
	private JMenu jmMethodMenu;
	// mouseMotionListener pro metodu Click&Move
	private MouseMotionListener mmListener = new MouseMotionAdapter() {
		@Override
		public void mouseMoved(MouseEvent e) {
			super.mouseMoved(e);
			Point mousePoint = e.getPoint();
			try {
				nakresliObjekt(mousePoint,false);
			} catch (Exception e2) {
				log.log(Level.SEVERE, e2.getMessage());
			}
		}
	};
	//keystroke pro ukonèení akce
	private KeyStroke escapeKeyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE,0, true);
	//akce pro keystroke
	private AbstractAction escapeKeyAction=new AbstractAction() {
		private static final long serialVersionUID = 1L;
		@Override
		public void actionPerformed(ActionEvent arg0) {
			clean();
			clearMemory();
			rastr.removeMouseMotionListener(mmListener);
			if(getTool().equals(Tools.OBJECT)){
				rastr.reset();
			}else{
				rastr.redraw();
			}
		}
	};
	//prepinac vyplnovani
	private boolean vyplnovani = false;
	private JButton buttonVyplnovani;
	//vlakno vyplnovani
	private Vyplnovani vyplnovaniThread;
	//vyplnovany objekt (kolekce usecek)
	private List<Usecka> usecky = new LinkedList<>();
	//programove konstanty
	private static final String CUSTOM_COLOR_ITEM_TEXT="Vlastní";
	private static final int COLORS_MENU_DRAWING = 0;
	private static final int COLORS_MENU_BACKGROUND = 1;
	private static final int COLORS_MENU_FILL = 2;
	//odkladaci prostor
	private HashMap<String,Object> memory = new HashMap<>();
	//konstruktor
	public MainFrame() {
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setSize(VELIKOST);
		setLocationRelativeTo(null);
		rastr = new Raster(getWidth(),getHeight());
		rastr.setSize(VELIKOST);
		rastr.setPreferredSize(new Dimension(getWidth(), getHeight()));
		rastr.grabFocus();
		getContentPane().add(initToolBar(), BorderLayout.SOUTH);
		getContentPane().add(rastr);
		initDialogs();
		setJMenuBar(initMenu());
		pack();
		//navazani handleru pro ESC 
		getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(escapeKeyStroke, "ESCAPE");
		getRootPane().getActionMap().put("ESCAPE", escapeKeyAction);
		//hlavni mysi listener
		rastr.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
					Point mousePoint = e.getPoint();
					if(SwingUtilities.isRightMouseButton(e) && getTool().equals(Tools.OBJECT)){
						if(getMemory().containsKey("GEOMETRICAL_OBJECT_START") && getMemory().containsKey("GEOMETRICAL_OBJECT_END"))
						{
							drawLine((Point) getMemory().get("GEOMETRICAL_OBJECT_START"),(Point) getMemory().get("GEOMETRICAL_OBJECT_END"));
							clearMemory();
							clean();
							rastr.removeMouseMotionListener(mmListener);
							rastr.smazPlatno();
							
						}		
					}else if(vyplnovani){
							fillArea(mousePoint,usecky);
									
					}else if (secondClick) {
						/* <druhe kliknuti> */
						/* <nastroje> */
						try {
							nakresliObjekt(mousePoint,true);
						} catch (Exception e3) {
							log.log(Level.SEVERE, e3.getMessage());
						}
						/* </nastroje> */
						/* <rezimy> */
						if(getTool().equals(Tools.OBJECT)){
							usecka = new Usecka(mousePoint);
							if(!getMemory().containsKey("GEOMETRICAL_OBJECT_END")){
								addToMemory("GEOMETRICAL_OBJECT_END", mousePoint);
							}else{
								getMemory().remove("GEOMETRICAL_OBJECT_END");
								addToMemory("GEOMETRICAL_OBJECT_END", mousePoint);
							}
			
						}else if (getMethod().equals(Methods.DRAGGING)) {
							rastr.removeMouseMotionListener(mmListener);
							secondClick = false;
						}else{
							secondClick = false;
						}
						/* </rezimy> */
						/* </druhe kliknuti> */
						
					} else {
						/* <prvni kliknuti> */
						/* <prvni obsluzne metody rezimu> */
						firstCallMethods();
						/* </prvni obsluzne metody rezimu> */
						/* <obecne volani> */
						rastr.vykresliBunku(mousePoint, getBarva());
						/* </obecne volani> */
						/* <nastroje> */
						switch(getTool()){
							//vykreslujeme usecku
							case LINE:  
								usecka = new Usecka(mousePoint);break;
							case CIRCLE:
								kruznice = new Kruznice(mousePoint);break;
							case OBJECT: 
								usecky.clear();
								usecka = new Usecka(mousePoint);
								if(!getMemory().containsKey("GEOMETRICAL_OBJECT_START")){
									addToMemory("GEOMETRICAL_OBJECT_START", mousePoint);
								}
								break;
						}
						/* </nastroje> */
						/* <rezimy> */
						if (method == Methods.DRAGGING) {
							rastr.addMouseMotionListener(mmListener);
						}
						/* </rezimy> */
						/* </prvni kliknuti> */
						secondClick = true;
					}
			}
		});

	}
	private void initDialogs() {
		dialogUsecka = new JDialog(this, "Nakreslit úseèku", true);
		dialogUsecka.setLayout(new SpringLayout());
		dialogUsecka.setLocationRelativeTo(this);
		dialogUsecka.setResizable(false);
		dialogUsecka.setSize(new Dimension(340, 135));
		JPanel plDialogUsecka = new JPanel(new SpringLayout());
		JLabel jlPocatecniBodUsecky = new JLabel(
				"Zadejte poèáteèní bod (tvar x,y): ", JLabel.LEFT);
		plDialogUsecka.add(jlPocatecniBodUsecky);
		jtPocatecniBodUsecky = new JTextField(10);
		jlPocatecniBodUsecky.setLabelFor(jtPocatecniBodUsecky);
		plDialogUsecka.add(jtPocatecniBodUsecky);
		JLabel jlKoncovyBodUsecky = new JLabel(
				"Zadejte koncový bod (tvar x,y): ", JLabel.LEFT);
		plDialogUsecka.add(jlKoncovyBodUsecky);
		jtKoncovyBodUsecky = new JTextField(10);
		jlKoncovyBodUsecky.setLabelFor(jtKoncovyBodUsecky);
		plDialogUsecka.add(jtKoncovyBodUsecky);
		jlErrorUsecka = new JLabel("");
		plDialogUsecka.add(jlErrorUsecka);
		JButton btnDrawLine = new JButton("Nakreslit úseèku");
		btnDrawLine.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				try {
					String[] startPoint = jtPocatecniBodUsecky.getText().split(
							",");
					String[] stopPoint = jtKoncovyBodUsecky.getText()
							.split(",");
					try {
						drawLine(new Point(Integer.parseInt(startPoint[0]),
								Integer.parseInt(startPoint[1])),
								new Point(Integer.parseInt(stopPoint[0]),
										Integer.parseInt(stopPoint[1])));
						dialogUsecka.setVisible(false);
					} catch (ArrayIndexOutOfBoundsException e) {
						jlErrorUsecka
								.setText("<html><font size=\"2\" color=\"red\">Zadané body jsou mimo hranice rastru</html>");
					}
				} catch (NumberFormatException e) {
					jlErrorUsecka
							.setText("<html><font size=\"2\" color=\"red\">Špatnì zadané vstupní parametry</html>");
				}

			}
		});
		plDialogUsecka.add(btnDrawLine);
		SpringUtilities.makeCompactGrid(plDialogUsecka, 3, 2, 6, 6, 6, 6);
		dialogUsecka.add(plDialogUsecka);

		dialogKruznice = new JDialog(this, "Nakreslit kružnici", true);
		dialogKruznice.setLayout(new SpringLayout());
		dialogKruznice.setLocationRelativeTo(this);
		dialogKruznice.setResizable(false);
		dialogKruznice.setSize(new Dimension(350, 135));
		JPanel plDialogKruznice = new JPanel(new SpringLayout());
		JLabel jlPocatecniBodKruznice = new JLabel(
				"Zadejte poèáteèní bod (tvar x,y): ");
		jtPocatecniBodKruznice = new JTextField(10);
		jlPocatecniBodKruznice.setLabelFor(jtPocatecniBodKruznice);
		plDialogKruznice.add(jlPocatecniBodKruznice);
		plDialogKruznice.add(jtPocatecniBodKruznice);
		JLabel jlPolomerKruznice = new JLabel(
				"Zadejte polomìr kružnice (pixely):");
		jtPolomerKruznice = new JTextField(10);
		jlPolomerKruznice.setLabelFor(jtPolomerKruznice);
		plDialogKruznice.add(jlPolomerKruznice);
		plDialogKruznice.add(jtPolomerKruznice);
		jlErrorKruznice = new JLabel("");
		plDialogKruznice.add(jlErrorKruznice);
		JButton btnDrawCircle = new JButton("Nakreslit kružnici");
		btnDrawCircle.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				try {
					String[] startPoint = jtPocatecniBodKruznice.getText()
							.split(",");
					int polomer = Integer.parseInt(jtPolomerKruznice.getText());
					try {
						drawCircle(new Point(Integer.parseInt(startPoint[0]),
								Integer.parseInt(startPoint[1])), polomer,true);
						dialogKruznice.setVisible(false);
					} catch (ArrayIndexOutOfBoundsException e) {
						jlErrorKruznice
								.setText("<html><font size=\"2\" color=\"red\">Zadané body jsou mimo hranice rastru</html>");
					}
				} catch (NumberFormatException e) {
					jlErrorKruznice
							.setText("<html><font size=\"2\" color=\"red\">Špatnì zadané vstupní parametry</html>");
				}

			}
		});
		plDialogKruznice.add(btnDrawCircle);
		SpringUtilities.makeCompactGrid(plDialogKruznice, 3, 2, 6, 6, 6, 6);
		dialogKruznice.add(plDialogKruznice);
	}

	private JMenuBar initMenu() {
		// zakladni menubar
		JMenuBar menubar = new JMenuBar();
		// menu akce
		JMenu actionMenu = new JMenu("Akce");
		JMenuItem actionMenuItem1 = new JMenuItem("Nakreslit úseèku");
		JMenuItem actionMenuItem2 = new JMenuItem("Nakreslit kružnici");
		JMenuItem actionMenuItem3 = new JMenuItem("Vymazat plátno");
		JMenuItem actionMenuItem4 = new JMenuItem("Ukonèit aplikaci");
		actionMenuItem1.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				dialogUsecka.setVisible(true);
				jtPocatecniBodUsecky.setText(null);
				jtKoncovyBodUsecky.setText(null);
				jlErrorUsecka.setText(null);

			}
		});
		actionMenuItem2.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				dialogKruznice.setVisible(true);
				jtPocatecniBodKruznice.setText(null);
				jtPolomerKruznice.setText(null);
				jlErrorKruznice.setText(null);

			}
		});
		actionMenuItem3.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				rastr.reset();
				clean();
			}
		});
		actionMenuItem4.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				dispose();

			}
		});
		actionMenu.add(actionMenuItem1);
		actionMenu.add(actionMenuItem2);
		actionMenu.addSeparator();
		actionMenu.add(actionMenuItem3);
		actionMenu.addSeparator();
		actionMenu.add(actionMenuItem4);

		menubar.add(actionMenu);
		// menu useckoveho algoritmu
		JMenu jmAlgorithm = new JMenu("Vykreslovací");
		ButtonGroup group = new ButtonGroup();
		JMenuItem jmiAlgorithm1 = new JRadioButtonMenuItem("Triviální",
				getLineAlgorithm().equals(Algorithm.TRIVIAL));
		JMenuItem jmiAlgorithm2 = new JRadioButtonMenuItem("DDA",
				getLineAlgorithm().equals(Algorithm.DDA));
		JMenuItem jmiAlgorithm3 = new JRadioButtonMenuItem("Bresenhamùv",
				getLineAlgorithm().equals(Algorithm.BRESENHAM));

		jmiAlgorithm1.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				setLineAlgorithm(Algorithm.TRIVIAL);
			}
		});
		jmiAlgorithm2.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				setLineAlgorithm(Algorithm.DDA);
			}
		});
		jmiAlgorithm3.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				setLineAlgorithm(Algorithm.BRESENHAM);

			}
		});
		jmAlgorithm.add(jmiAlgorithm1);
		jmAlgorithm.add(jmiAlgorithm2);
		jmAlgorithm.add(jmiAlgorithm3);

		group.add(jmiAlgorithm1);
		group.add(jmiAlgorithm2);
		group.add(jmiAlgorithm3);
		
		JMenu jmFillAlgorithm = new JMenu("Vyplòovací");
		ButtonGroup fillGroup = new ButtonGroup();
		JMenuItem jmiFillAlgorithm1 = new JRadioButtonMenuItem("Semínko",
				getFillAlgorithm().equals(Algorithm.SEED));
		JMenuItem jmiFillAlgorithm2 = new JRadioButtonMenuItem("Øádkové semínko",
				getFillAlgorithm().equals(Algorithm.LINESEED));
		JMenuItem jmiFillAlgorithm3 = new JRadioButtonMenuItem("Scan-line",
				getFillAlgorithm().equals(Algorithm.SCANLINE));

		jmiFillAlgorithm1.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				setFillAlgorithm(Algorithm.SEED);
			}
		});
		jmiFillAlgorithm2.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				setFillAlgorithm(Algorithm.LINESEED);
			}
		});
		jmiFillAlgorithm3.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				setFillAlgorithm(Algorithm.SCANLINE);

			}
		});
		jmFillAlgorithm.add(jmiFillAlgorithm1);
		jmFillAlgorithm.add(jmiFillAlgorithm2);
		jmFillAlgorithm.add(jmiFillAlgorithm3);
		
		fillGroup.add(jmiFillAlgorithm1);
		fillGroup.add(jmiFillAlgorithm2);
		fillGroup.add(jmiFillAlgorithm3);
		
		JMenu jmAlgorithmMainMenu = new JMenu("Algoritmus");
		jmAlgorithmMainMenu.add(jmAlgorithm);
		jmAlgorithmMainMenu.add(jmFillAlgorithm);
		menubar.add(jmAlgorithmMainMenu);

		ButtonGroup btnGroupMethod = new ButtonGroup();
		jmMethodMenu = new JMenu("Režim");
		JMenuItem jmiClickingMethod = new JRadioButtonMenuItem("Click & Click",
				true);
		jmiClickingMethod.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				clean();
				rastr.redraw();
				rastr.removeMouseMotionListener(mmListener);
				setMethod(Methods.CLICKING);

			}
		});
		jmMethodMenu.add(jmiClickingMethod);
		btnGroupMethod.add(jmiClickingMethod);
		JMenuItem jmiDraggingMethod = new JRadioButtonMenuItem("Click & Move");
		jmiDraggingMethod.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				clean();
				rastr.redraw();
				setMethod(Methods.DRAGGING);

			}
		});
		jmMethodMenu.add(jmiDraggingMethod);
		btnGroupMethod.add(jmiDraggingMethod);

		menubar.add(jmMethodMenu);
		jmMainColorsMenu = new JMenu("Barva");
		
		jmMainColorsMenu.add(getColorsMenu("Barva kreslení",this,"setBarva",getBarva()));
		jmMainColorsMenu.add(getColorsMenu("Barva pozadí",this,"zmenBarvuPozadi",rastr.getBarvaPozadi()));
		jmMainColorsMenu.add(getColorsMenu("Barva vyplnìní",this,"setBarvaVyplneni",getBarvaVyplneni()));
		
		lockSameColors(rastr.getBarvaPozadi(), COLORS_MENU_DRAWING);
		lockSameColors(getBarva(), COLORS_MENU_BACKGROUND);
		//nebudeme hlidat barvu vyplneni stejnou jako barvu kresleni
		//lockSameColors(getBarva(), COLORS_MENU_FILL);
		menubar.add(jmMainColorsMenu);
		menubar.add(Box.createHorizontalGlue());
		JButton jlAbout = new JButton("O programu ");
		jlAbout.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				JOptionPane.showMessageDialog(getFrame(),"Napsáno pro úèely projektu PGRF1 na Univerzitì Hradec Králové.\n © 2012 Jakub Josef","O programu",JOptionPane.PLAIN_MESSAGE);
				
			}
			
		});
		menubar.add(jlAbout);
		return menubar;

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
					MalovaniUtils.invokeChangeColorMethod(object,listenerMethod,MalovaniUtils.getColorFromString(menuItem.getActionCommand()));
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
		jmiCerna.setSelected(MalovaniUtils.getColorMenuItemState(jmiCerna, selected));
		btnGroupColors.add(jmiCerna);
		jmColorsMenu.add(jmiCerna);
		
		JMenuItem jmiBila = new JRadioButtonMenuItem("Bílá");
		jmiBila.setActionCommand("WHITE");
		jmiBila.addActionListener(listener);
		jmiBila.setSelected(MalovaniUtils.getColorMenuItemState(jmiBila, selected));
		btnGroupColors.add(jmiBila);
		jmColorsMenu.add(jmiBila);
		
		JMenuItem jmiCervena = new JRadioButtonMenuItem("Èervená");
		jmiCervena.setActionCommand("RED");
		jmiCervena.addActionListener(listener);
		jmiCervena.setSelected(MalovaniUtils.getColorMenuItemState(jmiCervena, selected));
		btnGroupColors.add(jmiCervena);
		jmColorsMenu.add(jmiCervena);
		
		JMenuItem jmiZelena = new JRadioButtonMenuItem("Zelená");
		jmiZelena.setActionCommand("GREEN");
		jmiZelena.addActionListener(listener);
		jmiZelena.setSelected(MalovaniUtils.getColorMenuItemState(jmiZelena, selected));
		btnGroupColors.add(jmiZelena);
		jmColorsMenu.add(jmiZelena);
		
		JMenuItem jmiModra = new JRadioButtonMenuItem("Modrá");
		jmiModra.setActionCommand("BLUE");
		jmiModra.addActionListener(listener);
		jmiModra.setSelected(MalovaniUtils.getColorMenuItemState(jmiModra, selected));
		btnGroupColors.add(jmiModra);
		jmColorsMenu.add(jmiModra);
		jmColorsMenu.addSeparator();
		
		JMenuItem jmiVlastni=new JRadioButtonMenuItem("Vlastní");
		jmiVlastni.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				Color barva=MalovaniUtils.showColorChooser(selected);
				JRadioButtonMenuItem jrbmenuItem = (JRadioButtonMenuItem) arg0.getSource();
				if(barva!=null){
					String barvaHex=Integer.toHexString(barva.getRGB());
					barvaHex = barvaHex.substring(2,barvaHex.length());
					try{
						MalovaniUtils.invokeChangeColorMethod(object,listenerMethod,barva);
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
	private JToolBar initToolBar() {
		ButtonGroup btnGroupToolBar = new ButtonGroup();
		JToolBar toolbar = new JToolBar("Toolbar", JToolBar.HORIZONTAL);
		JButton buttonUsecka = new JButton(new ImageIcon(getClass()
				.getClassLoader().getResource("img/line-icon.gif")));
		buttonUsecka.setToolTipText("Úseèka");
		buttonUsecka.setSelected(getTool().equals(Tools.LINE));
		buttonUsecka.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				jmMethodMenu.setEnabled(true);
				if(getMemory().containsKey("LAST_METHOD")){
					setMethod((Methods) getMemory().get("LAST_METHOD"));
				}else{
					setMethod(Methods.CLICKING);
				}
				setTool(Tools.LINE);
			}
		});
		btnGroupToolBar.add(buttonUsecka);
		toolbar.add(buttonUsecka);
		toolbar.addSeparator();
		JButton buttonKruznice = new JButton(new ImageIcon(getClass()
				.getClassLoader().getResource("img/Circle.png")));
		buttonKruznice.setToolTipText("Kružnice");
		buttonKruznice.setSelected(getTool().equals(Tools.CIRCLE));
		buttonKruznice.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				jmMethodMenu.setEnabled(true);
				if(getMemory().containsKey("LAST_METHOD")){
					setMethod((Methods) getMemory().get("LAST_METHOD"));
				}else{
					setMethod(Methods.CLICKING);
				}
				setTool(Tools.CIRCLE);
			}
		});
		btnGroupToolBar.add(buttonKruznice);
		toolbar.add(buttonKruznice);
		toolbar.addSeparator();
		JButton buttonNakreslitObjekt = new JButton(new ImageIcon(getClass().getClassLoader().getResource("img/objekt.jpg")));
		buttonNakreslitObjekt.setToolTipText("Objekt");
		buttonNakreslitObjekt.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				jmMethodMenu.setEnabled(false);
				addToMemory("LAST_METHOD", getMethod());
				setMethod(Methods.DRAGGING);
				setTool(Tools.OBJECT);
			}
		});
		btnGroupToolBar.add(buttonNakreslitObjekt);
		toolbar.add(buttonNakreslitObjekt);
		
		buttonVyplnovani = new JButton(new ImageIcon(getClass().getClassLoader().getResource("img/fill.png")));
		buttonVyplnovani.setToolTipText("Vyplòování");
		buttonVyplnovani.setFocusable(false);
		buttonVyplnovani.setBorder(null);
		buttonVyplnovani.addActionListener(new ActionListener() {
			
			@SuppressWarnings("deprecation")
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if(vyplnovaniThread instanceof Runnable && vyplnovani){
					changeVyplnovaniState(false, true);
					vyplnovaniThread.stop();
					vyplnovaniThread=null;
				}else{
					changeVyplnovaniState(false,true);
				}
			}
		});
		toolbar.addSeparator();
		toolbar.add(buttonVyplnovani);
		
		JButton buttonVymazatPlatno = new JButton(new ImageIcon(getClass().getClassLoader().getResource("img/Blank.png")));
		buttonVymazatPlatno.setToolTipText("Vymazat plátno");
		buttonVymazatPlatno.setFocusable(false);
		buttonVymazatPlatno.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				rastr.reset();
			}
		});
		toolbar.add(Box.createHorizontalGlue());
		toolbar.add(buttonVymazatPlatno);
		toolbar.setFloatable(false);
		return toolbar;
	}

	/* vykreslovaci metody */
	/**
	 * Hlavni vykreslovaci metoda, rozhoduje o tom co se bude kreslit
	 * 
	 * @param mousePoint
	 *            Druha souradnice bodu
	 * @return void
	 */
	private void nakresliObjekt(Point mousePoint,boolean finalize) throws Exception {
		if ((getTool().equals(Tools.LINE) || getTool().equals(Tools.OBJECT)) && (usecka instanceof Usecka)
				&& (usecka.getStop() == null)) {
			try {
				drawLine(mousePoint, finalize);
			} catch (Exception e) {
				log.log(Level.SEVERE, "Nepodarilo se vykreslit ucecku");
			}
		} else if ((getTool().equals(Tools.CIRCLE))
				&& (kruznice instanceof Kruznice)
				&& (kruznice.getPocatecniBod() != null)) {
			drawCircle(kruznice.getPocatecniBod(), Kruznice.spocitejPolomer(
					kruznice.getPocatecniBod(), mousePoint),finalize);
		} else {
			throw new Exception(
					"Nelze rozhodnout o tom ktery objekt se vykresli, zrejme nejsou splnena nektera vykreslovaci kriteria");
		}

	}

	/**
	 * Vykreslovací metoda pro usecku Ocekava ze uz usecka má nastaven pocatecni
	 * bod
	 * 
	 * @param stop
	 *            Koncovy bod usecky
	 * @param finalize
	 *            Finalizovat úseèku (nastavení koncového bude do úseèky)
	 * @return void
	 */
	private void drawLine(Point stop, boolean finalize) throws Exception {
		firstCallMethods();
		if (usecka instanceof Usecka && usecka.getStart() != null) {
			usecka.setStop(stop);
			
			rastr.smazBunku(usecka.getStart());
			List<Bod> body = usecka.spocitejSpojeni(getLineAlgorithm(),getBarva(),
					getMethod().equals(Methods.DRAGGING));
			for (int i=0;i<body.size();i++) {
				rastr.nastavBunku(body.get(i),getBarva());
			}
			if(finalize){
				usecky.add(new Usecka(usecka.getStart(),usecka.getStop(),usecka.getBody()));
				rastr.addDrawedElement(body);
			}
			rastr.vykresli();
			usecka.setStop(null);
			
		} else {
			throw new Exception("Nepodarilo se vykreslit usecku");
		}
	}

	/**
	 * Vykreslovaci metoda pro usecku Potrebuje predat pocatecni i koncovy bod
	 * 
	 * @param start
	 *            Pocatecni bod
	 * @param stop
	 *            Koncovy bod
	 * @return void
	 */
	private void drawLine(Point start, Point stop) {
		Usecka usecka = new Usecka(start);
		usecky.add(usecka);
		usecka.setStop(stop);
		rastr.vykresliBunku(start, getBarva());
		List<Bod> body=usecka.spocitejSpojeni(getLineAlgorithm(),getBarva(), getMethod()
				.equals(Methods.DRAGGING));
		for (Point bod : body) {
			rastr.nastavBunku(bod, getBarva());
		}
		rastr.addDrawedElement(body);
		rastr.vykresli();
		usecka = null;
	}

	/**
	 * Vykreslovaci metoda pro kruznici
	 * 
	 * @param start
	 *            pocatecni bod
	 * @param polomer
	 *            Polomer kruznice
	 * @return void
	 */
	private void drawCircle(Point start, int polomer,boolean finalize) {
		firstCallMethods();
		rastr.smazBunku(start);
		try {
			List<Bod> body = Kruznice.kresliKruznici(start, polomer,getBarva());
			for (Bod bod : body) {
				try {
					rastr.nastavBunku(bod, getBarva());
				} catch (ArrayIndexOutOfBoundsException e) {
					log.log(Level.WARNING, "Nelze kreslit na bod[" + bod.x
							+ "," + bod.y + "]");
				}
				if(finalize){
					rastr.addDrawedElement(body);
				}
			}
		} catch (Exception e) {
			log.log(Level.WARNING, e.getMessage());
		}
		
		rastr.vykresli();

	}
	private synchronized void fillArea(Point mousePoint,List<Usecka> usecky) {
		vyplnovaniThread=new Vyplnovani(this,mousePoint,getFillAlgorithm(),rastr,getBarvaVyplneni(),getBarva());
		vyplnovaniThread.setObjekt(usecky);
		try{
			vyplnovaniThread.start();
		}catch(StackOverflowError e){
			JOptionPane.showMessageDialog(getFrame(), "Zásobník pøetekl pøi vyplòování!","Chyba vyplòování",JOptionPane.ERROR_MESSAGE);
		}
	}
	
	/* pomocne metody */
	/**
	 * Pomocna metoda provede prvni operace podle metody kresleni.
	 */
	private void firstCallMethods() {
		if (getMethod().equals(Methods.DRAGGING)) {
			rastr.smazPlatno();
		}
	}
	public void zmenBarvuPozadi(Color barva){
		lockSameColors(barva,COLORS_MENU_DRAWING);
		clearVlastniItem(COLORS_MENU_BACKGROUND);
		rastr.setBarvaPozadi(barva);
	}
	/**
	 * Metoda osetri prepis menuItemu vlastni zpet na puvodni formu po zmene barvy
	 * @param componentID ID menu ve kterem se ma zmena osetrit
	 */
	private void clearVlastniItem(int componentID) {
		JMenu drawColorsMenu = (JMenu) jmMainColorsMenu.getMenuComponent(componentID);
		Component[] colorsMenuComponents =  drawColorsMenu.getMenuComponents();
		if(colorsMenuComponents[colorsMenuComponents.length-1] instanceof JRadioButtonMenuItem){
			JRadioButtonMenuItem jrMenuItem = (JRadioButtonMenuItem) colorsMenuComponents[colorsMenuComponents.length-1];
			if(!jrMenuItem.getText().equals(CUSTOM_COLOR_ITEM_TEXT)){
				jrMenuItem.setText(CUSTOM_COLOR_ITEM_TEXT);
			}
			if(jrMenuItem.isSelected()){
				jrMenuItem.setSelected(false);
			}
		}
		
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
						barvaItemu= MalovaniUtils.getColorFromString(jrMenuItem.getActionCommand());
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

	private void clean() {
		usecka = null;
		kruznice = null;
		secondClick = false;
		vyplnovani=false;
	}
	/* gettery a settery */
	public Algorithm getLineAlgorithm() {
		return lineAlgorithm;
	}

	public void setLineAlgorithm(Algorithm algorithm) {
		this.lineAlgorithm = algorithm;
	}

	public Algorithm getFillAlgorithm() {
		return fillAlgorithm;
	}
	public void setFillAlgorithm(Algorithm fillAlgorithm) {
		this.fillAlgorithm = fillAlgorithm;
	}
	public Methods getMethod() {
		return method;
	}

	public void setMethod(Methods method) {
		this.method = method;
	}

	public Tools getTool() {
		return tool;
	}

	public void setTool(Tools tool) {
		this.tool = tool;
	}

	public Color getBarva() {
		return barva;
	}

	public void setBarva(Color barva) {
		lockSameColors(barva, COLORS_MENU_BACKGROUND);
		lockSameColors(barva, COLORS_MENU_FILL);
		clearVlastniItem(COLORS_MENU_DRAWING);
		this.barva = barva;
	}
	public Color getBarvaVyplneni() {
		return barvaVyplneni;
	}
	public List<Usecka> getUsecky() {
		return usecky;
	}
	public void setBarvaVyplneni(Color barvaVyplneni) {
		//nebudeme hlidat stejnou barvu vyplneni jako kresleni
		//lockSameColors(barvaVyplneni, COLORS_MENU_DRAWING);
		this.barvaVyplneni = barvaVyplneni;
	}
	public JFrame getFrame(){
		return this;
	}
	private HashMap<String,Object> getMemory() {
		return memory;
	}

	private void addToMemory(String key, Object object) {
		memory.put(key, object);
	}

	private void clearMemory() {
		memory.clear();
		
	}
	/**
	 * Metoda zmeni stav vyplnovaciho prepinace a obslouzi zmenu zvyraznovace
	 * @param runningState Bezici stav, rozsvitit misto cerneho ohraniceni cervene.
	 * @param setState Prepnout stav promenne vyplnovani
	 */
	public void changeVyplnovaniState(boolean runningState,boolean setState) {
		if(!runningState){
			if(!(buttonVyplnovani.getBorder() instanceof BevelBorder) || (buttonVyplnovani.getBorder() == null)){
				buttonVyplnovani.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED,Color.BLACK,Color.BLACK));
			}else{
				buttonVyplnovani.setBorder(null);
			}
		}else{
			buttonVyplnovani.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED,Color.RED,Color.RED));
		}
		if(setState){
			vyplnovani=!vyplnovani;
		}
	}
}


