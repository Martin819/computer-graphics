package gui;

import helpers.SpringUtilities;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseMotionListener;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.SpringLayout;

import model.Kruznice;
import model.Usecka;
import model.Utvar;

/**
 * HlavnÌ aplikaËnÌ t¯Ìda celÈho okna Obsahuje metody initDialogs,initMenu a
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
	private static Logger log = Logger.getLogger("log");

	// enumerace
	public enum Algorithm {
		TRIVIAL, DDA, BRESENHAM;
	}

	public enum Methods {
		CLICKING, DRAGGING;
	}

	public enum Tools {
		LINE, CIRCLE;
	}
	// nastaveni vychoziho algorytmu
	private Algorithm algorithm = Algorithm.TRIVIAL;
	// nastaveni vychozi metody
	private Methods method = Methods.CLICKING;
	// nastaveni vychoziho nastroje
	private Tools tool = Tools.LINE;
	// nastaveni vychozi velikosti okna
	private final Dimension VELIKOST = new Dimension(540, 450);
	// nastaveni vychoziho rastru
	private final int pocetRadku = 30;
	private final int pocetSloupcu = 30;
	// nastaveni vychozi barvy
	private Color barva = Color.WHITE;
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
	// mouseMotionListener
	private MouseMotionListener mmListener = new MouseMotionAdapter() {
		@Override
		public void mouseMoved(MouseEvent e) {
			super.mouseMoved(e);
			Dimension velikostBunky = rastr.getVelikostBunky();
			Point mousePoint = e.getPoint();
			mousePoint.x = mousePoint.x / velikostBunky.width;
			mousePoint.y = mousePoint.y / velikostBunky.height;
			try {
				nakresliObjekt(mousePoint);
			} catch (Exception e2) {
				log.log(Level.SEVERE, e2.getMessage());
			}
		}
	};

	public MainFrame() {
		setTitle("Rasterizace");
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setSize(VELIKOST);
		setLocationRelativeTo(null);
		rastr = new Raster(pocetSloupcu, pocetRadku);
		rastr.setSize(VELIKOST);
		rastr.setPreferredSize(new Dimension(getWidth(), getHeight()));
		rastr.grabFocus();
		getContentPane().add(initToolBar(), BorderLayout.SOUTH);
		getContentPane().add(rastr);
		initDialogs();
		setJMenuBar(initMenu());
		pack();
		rastr.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				Dimension velikostBunky = rastr.getVelikostBunky();
				Point mousePoint = e.getPoint();
				mousePoint.x = mousePoint.x / velikostBunky.width;
				mousePoint.y = mousePoint.y / velikostBunky.height;
				if (secondClick) {
					/* <druhe kliknuti> */
					/* <nastroje> */
					try {
						nakresliObjekt(mousePoint);
					} catch (Exception e3) {
						log.log(Level.SEVERE, e3.getMessage());
					}
					/* </nastroje> */
					/* <rezimy> */
					if (getMethod().equals(Methods.DRAGGING)) {
						rastr.removeMouseMotionListener(mmListener);
					}
					/* </rezimy> */
					/* </druhe kliknuti> */
					secondClick = false;
				} else {
					/* <prvni kliknuti> */
					/* <prvni obsluzne metody rezimu> */
					firstCallMethods();
					/* </prvni obsluzne metody rezimu> */
					/* <obecne volani> */
					rastr.nastavBunkuAVykresliBunku(mousePoint, getBarva());
					/* </obecne volani> */
					/* <nastroje> */
					switch(getTool()){
						//vykreslujeme usecku
						case LINE:  
							usecka = new Usecka(mousePoint);break;
						case CIRCLE:
							kruznice = new Kruznice(mousePoint);
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
		dialogUsecka = new JDialog(this, "Nakreslit ˙seËku", true);
		dialogUsecka.setLayout(new SpringLayout());
		dialogUsecka.setLocationRelativeTo(this);
		dialogUsecka.setResizable(false);
		dialogUsecka.setSize(new Dimension(340, 120));
		JPanel plDialogUsecka = new JPanel(new SpringLayout());
		JLabel jlPocatecniBodUsecky = new JLabel(
				"Zadejte poË·teËnÌ bod (tvar x,y): ", JLabel.LEFT);
		plDialogUsecka.add(jlPocatecniBodUsecky);
		jtPocatecniBodUsecky = new JTextField(10);
		jlPocatecniBodUsecky.setLabelFor(jtPocatecniBodUsecky);
		plDialogUsecka.add(jtPocatecniBodUsecky);
		JLabel jlKoncovyBodUsecky = new JLabel(
				"Zadejte koncov˝ bod (tvar x,y): ", JLabel.LEFT);
		plDialogUsecka.add(jlKoncovyBodUsecky);
		jtKoncovyBodUsecky = new JTextField(10);
		jlKoncovyBodUsecky.setLabelFor(jtKoncovyBodUsecky);
		plDialogUsecka.add(jtKoncovyBodUsecky);
		jlErrorUsecka = new JLabel("");
		plDialogUsecka.add(jlErrorUsecka);
		JButton btnDrawLine = new JButton("Nakreslit ˙seËku");
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
								.setText("<html><font size=\"2\" color=\"red\">ZadanÈ body jsou mimo hranice rastru</html>");
					}
				} catch (NumberFormatException e) {
					jlErrorUsecka
							.setText("<html><font size=\"2\" color=\"red\">äpatnÏ zadanÈ vstupnÌ parametry</html>");
				}

			}
		});
		plDialogUsecka.add(btnDrawLine);
		SpringUtilities.makeCompactGrid(plDialogUsecka, 3, 2, 6, 6, 6, 6);
		dialogUsecka.add(plDialogUsecka);

		dialogKruznice = new JDialog(this, "Nakreslit kruûnici", true);
		dialogKruznice.setLayout(new SpringLayout());
		dialogKruznice.setLocationRelativeTo(this);
		dialogKruznice.setResizable(false);
		dialogKruznice.setSize(new Dimension(350, 120));
		JPanel plDialogKruznice = new JPanel(new SpringLayout());
		JLabel jlPocatecniBodKruznice = new JLabel(
				"Zadejte poË·teËnÌ bod (tvar x,y): ");
		jtPocatecniBodKruznice = new JTextField(10);
		jlPocatecniBodKruznice.setLabelFor(jtPocatecniBodKruznice);
		plDialogKruznice.add(jlPocatecniBodKruznice);
		plDialogKruznice.add(jtPocatecniBodKruznice);
		JLabel jlPolomerKruznice = new JLabel(
				"Zadejte polomÏr kruûnice (pixely):");
		jtPolomerKruznice = new JTextField(10);
		jlPolomerKruznice.setLabelFor(jtPolomerKruznice);
		plDialogKruznice.add(jlPolomerKruznice);
		plDialogKruznice.add(jtPolomerKruznice);
		jlErrorKruznice = new JLabel("");
		plDialogKruznice.add(jlErrorKruznice);
		JButton btnDrawCircle = new JButton("Nakreslit kruûnici");
		btnDrawCircle.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				try {
					String[] startPoint = jtPocatecniBodKruznice.getText()
							.split(",");
					int polomer = Integer.parseInt(jtPolomerKruznice.getText());
					try {
						drawCircle(new Point(Integer.parseInt(startPoint[0]),
								Integer.parseInt(startPoint[1])), polomer);
						dialogKruznice.setVisible(false);
					} catch (ArrayIndexOutOfBoundsException e) {
						jlErrorKruznice
								.setText("<html><font size=\"2\" color=\"red\">ZadanÈ body jsou mimo hranice rastru</html>");
					}
				} catch (NumberFormatException e) {
					jlErrorKruznice
							.setText("<html><font size=\"2\" color=\"red\">äpatnÏ zadanÈ vstupnÌ parametry</html>");
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
		JMenuItem actionMenuItem1 = new JMenuItem("Nakreslit ˙seËku");
		JMenuItem actionMenuItem2 = new JMenuItem("Nakreslit kruûnici");
		JMenuItem actionMenuItem3 = new JMenuItem("Vymazat pl·tno");
		JMenuItem actionMenuItem4 = new JMenuItem("UkonËit aplikaci");
		JMenuItem actionMenuItem5= new JCheckBoxMenuItem("Zobrazovat sÌù",true);
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
				usecka = null;
				kruznice = null;
				secondClick = false;
			}
		});
		actionMenuItem4.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				dispose();

			}
		});
		actionMenuItem5.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if(rastr.getBarvaPozadi().equals(rastr.getBarvaSite())){
					rastr.setBarvaSite(Color.WHITE);
					rastr.repaint();
				}else{
				rastr.setBarvaSite(rastr.getBarvaPozadi());
				rastr.repaint();
				}
				
			}
		});
		actionMenu.add(actionMenuItem1);
		actionMenu.add(actionMenuItem2);
		actionMenu.addSeparator();
		actionMenu.add(actionMenuItem3);
		actionMenu.addSeparator();
		actionMenu.add(actionMenuItem5);
		actionMenu.addSeparator();
		actionMenu.add(actionMenuItem4);

		menubar.add(actionMenu);
		// menu algoritmus
		JMenu jmAlgorithm = new JMenu("Algoritmus");
		ButtonGroup group = new ButtonGroup();
		JMenuItem jmiAlgorithm1 = new JRadioButtonMenuItem("Trivi·lnÌ",
				getAlgorithm().equals(Algorithm.TRIVIAL));
		JMenuItem jmiAlgorithm2 = new JRadioButtonMenuItem("DDA",
				getAlgorithm().equals(Algorithm.DDA));
		JMenuItem jmiAlgorithm3 = new JRadioButtonMenuItem("Bresenham˘v",
				getAlgorithm().equals(Algorithm.BRESENHAM));

		jmiAlgorithm1.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				setAlgorithm(Algorithm.TRIVIAL);
			}
		});
		jmiAlgorithm2.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				setAlgorithm(Algorithm.DDA);
			}
		});
		jmiAlgorithm3.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				setAlgorithm(Algorithm.BRESENHAM);

			}
		});
		jmAlgorithm.add(jmiAlgorithm1);
		jmAlgorithm.add(jmiAlgorithm2);
		jmAlgorithm.add(jmiAlgorithm3);

		group.add(jmiAlgorithm1);
		group.add(jmiAlgorithm2);
		group.add(jmiAlgorithm3);
		menubar.add(jmAlgorithm);
		/* Velikost rastru */
		ButtonGroup group2 = new ButtonGroup();
		JMenu jmRasterSize = new JMenu("Velikost rastru");
		JMenuItem jmic30x30 = new JRadioButtonMenuItem("30x30", true);
		jmic30x30.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				rastr.setPocetRadku(30);
				rastr.setPocetSloupcu(30);
				rastr.repaint();
			}
		});
		jmRasterSize.add(jmic30x30);
		JMenuItem jmic60x60 = new JRadioButtonMenuItem("60x60", false);
		jmic60x60.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				rastr.setPocetRadku(60);
				rastr.setPocetSloupcu(60);
				rastr.reset();
				rastr.repaint();
				pack();
			}
		});
		jmRasterSize.add(jmic60x60);
		JMenuItem jmic90x90 = new JRadioButtonMenuItem("90x90");
		jmic90x90.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				rastr.setPocetRadku(90);
				rastr.setPocetSloupcu(90);
				rastr.reset();
				rastr.repaint();
				pack();
			}
		});
		jmRasterSize.add(jmic90x90);
		group2.add(jmic30x30);
		group2.add(jmic60x60);
		group2.add(jmic90x90);
		menubar.add(jmRasterSize);

		ButtonGroup btnGroupMethod = new ButtonGroup();
		JMenu jmMethodMenu = new JMenu("Reûim");
		JMenuItem jmiClickingMethod = new JRadioButtonMenuItem("Click&Click",
				true);
		jmiClickingMethod.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				rastr.reset();
				setMethod(Methods.CLICKING);

			}
		});
		jmMethodMenu.add(jmiClickingMethod);
		btnGroupMethod.add(jmiClickingMethod);
		JMenuItem jmiDraggingMethod = new JRadioButtonMenuItem("Click&Move");
		jmiDraggingMethod.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				rastr.reset();
				setMethod(Methods.DRAGGING);

			}
		});
		jmMethodMenu.add(jmiDraggingMethod);
		btnGroupMethod.add(jmiDraggingMethod);

		menubar.add(jmMethodMenu);

		JMenu jmColorsMenu = new JMenu("Barva");
		ButtonGroup btnGroupColors = new ButtonGroup();
		JMenuItem jmiBila = new JRadioButtonMenuItem("BÌl·", true);
		jmiBila.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				setBarva(Color.WHITE);

			}
		});
		btnGroupColors.add(jmiBila);
		jmColorsMenu.add(jmiBila);
		JMenuItem jmiCervena = new JRadioButtonMenuItem("»erven·");
		jmiCervena.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				setBarva(Color.RED);

			}
		});
		btnGroupColors.add(jmiCervena);
		jmColorsMenu.add(jmiCervena);
		JMenuItem jmiZelena = new JRadioButtonMenuItem("Zelen·");
		jmiZelena.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				setBarva(Color.GREEN);

			}
		});
		btnGroupColors.add(jmiZelena);
		jmColorsMenu.add(jmiZelena);
		JMenuItem jmiModra = new JRadioButtonMenuItem("Modr·");
		jmiModra.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				setBarva(Color.BLUE);

			}
		});
		btnGroupColors.add(jmiModra);
		jmColorsMenu.add(jmiModra);
		menubar.add(jmColorsMenu);

		return menubar;

	}

	private JToolBar initToolBar() {
		ButtonGroup btnGroupToolBar = new ButtonGroup();
		JToolBar toolbar = new JToolBar("Toolbar", JToolBar.HORIZONTAL);
		JButton buttonUsecka = new JButton(new ImageIcon(getClass()
				.getClassLoader().getResource("img/line-icon.gif")));
		buttonUsecka.setToolTipText("⁄seËka");
		buttonUsecka.setSelected(getTool().equals(Tools.LINE));
		buttonUsecka.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				rastr.reset();
				setTool(Tools.LINE);
			}
		});
		btnGroupToolBar.add(buttonUsecka);
		toolbar.add(buttonUsecka);
		toolbar.addSeparator();
		JButton buttonKruznice = new JButton(new ImageIcon(getClass()
				.getClassLoader().getResource("img/Circle.png")));
		buttonKruznice.setToolTipText("Kruûnice");
		buttonKruznice.setSelected(getTool().equals(Tools.CIRCLE));
		buttonKruznice.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				rastr.reset();
				setTool(Tools.CIRCLE);
			}
		});
		toolbar.add(buttonKruznice);
		btnGroupToolBar.add(buttonKruznice);
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
	private void nakresliObjekt(Point mousePoint) throws Exception {
		if ((getTool().equals(Tools.LINE)) && (usecka instanceof Usecka)
				&& (usecka.getStop() == null)) {
			try {
				drawLine(mousePoint, false);
			} catch (Exception e) {
				log.log(Level.WARNING, e.getMessage());
			}
		} else if ((getTool().equals(Tools.CIRCLE))
				&& (kruznice instanceof Kruznice)
				&& (kruznice.getPocatecniBod() != null)) {
			drawCircle(kruznice.getPocatecniBod(), Kruznice.spocitejPolomer(
					kruznice.getPocatecniBod(), mousePoint));
		} else {
			throw new Exception(
					"Nelze rozhodnout o tom ktery objekt se vykresli, zrejme nejsou splnena nektera vykreslovaci kriteria");
		}

	}

	/**
	 * VykreslovacÌ metoda pro usecku Ocekava ze uz usecka m· nastaven pocatecni
	 * bod
	 * 
	 * @param stop
	 *            Koncovy bod usecky
	 * @param finalize
	 *            Finalizovat ˙seËku (nastavenÌ koncovÈho bude do ˙seËky)
	 * @return void
	 */
	private void drawLine(Point stop, boolean finalize) throws Exception {
		firstCallMethods();
		if (usecka instanceof Usecka && usecka.getStart() != null) {
			usecka.setStop(stop);
			rastr.nastavBunkuAVykresliBunku(usecka.getStart(), getBarva());
			List<Point> body = usecka.spocitejSpojeni(getAlgorithm(),
					getMethod().equals(Methods.DRAGGING));
			for (int i=0;i<body.size();i++) {
				rastr.nastavBunkuAVykresliBunku(body.get(i), getBarva());
			} 
			if (!finalize) {
				usecka.setStop(null);
			}
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
		usecka.setStop(stop);
		rastr.nastavBunkuAVykresliBunku(start, getBarva());
		for (Point bod : usecka.spocitejSpojeni(getAlgorithm(), getMethod()
				.equals(Methods.DRAGGING))) {
			rastr.nastavBunkuAVykresliBunku(bod, getBarva());
		}
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
	private void drawCircle(Point start, int polomer) {
		firstCallMethods();
		rastr.nastavBunkuAVykresliBunku(start, (Utvar) null);
		try {
			for (Point bod : Kruznice.kresliKruznici(start, polomer)) {
				try {
					rastr.nastavBunkuAVykresliBunku(bod, getBarva());
				} catch (ArrayIndexOutOfBoundsException e) {
					log.log(Level.WARNING, "Nelze kreslit na bod[" + bod.x
							+ "," + bod.y + "]");
				}
			}
		} catch (Exception e) {
			log.log(Level.WARNING, e.getMessage());
		}

	}

	/* pomocne metody */
	/**
	 * Pomocna metoda provede prvni operace podle metody kresleni.
	 */
	private void firstCallMethods() {
		if (getMethod().equals(Methods.DRAGGING)) {
			rastr.reset();
		}
	}

	/* gettery a settery */
	public Algorithm getAlgorithm() {
		return algorithm;
	}

	public void setAlgorithm(Algorithm algorithm) {
		this.algorithm = algorithm;
	}

	public int getPocetRadku() {
		return pocetRadku;
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
		this.barva = barva;
	}

}
