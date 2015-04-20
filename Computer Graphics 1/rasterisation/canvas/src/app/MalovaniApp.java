package app;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import gui.MainFrame;
/**
 * Rasterizace - Hlavní spouštìcí tøída
 * @author Jakub Josef
 *
 */
public class MalovaniApp {
private static final String LOOK_AND_FEEL = "javax.swing.plaf.nimbus.NimbusLookAndFeel";
public final static String NAZEV_OKNA="Malování 2.0";
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			
			@Override
			public void run() {

				try {
					UIManager.setLookAndFeel(LOOK_AND_FEEL);
				} catch (ClassNotFoundException | InstantiationException
						| IllegalAccessException
						| UnsupportedLookAndFeelException e) {
						System.out.println("Nepodarilo se nastavit L&F!\n" +
								"L&F "+LOOK_AND_FEEL+" asi neexistuje.");
						e.printStackTrace();
				}
				JFrame malovaniFrame =new MainFrame();
				malovaniFrame.setTitle(NAZEV_OKNA);
				malovaniFrame.setVisible(true);
			}
		});
		

	}

}
