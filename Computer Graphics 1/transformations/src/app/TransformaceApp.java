package app;

import gui.MainFrame;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public class TransformaceApp {

	private static final String TITLE = "Transformace 1.1";
	private static final String LOOK_AND_FEEL = "javax.swing.plaf.nimbus.NimbusLookAndFeel";
	
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
				startApp();
				
			}
		});

	}

	protected static void startApp() {
		JFrame frame = new MainFrame();
		frame.setTitle(TITLE);
		frame.setVisible(true);
		
	}

}
