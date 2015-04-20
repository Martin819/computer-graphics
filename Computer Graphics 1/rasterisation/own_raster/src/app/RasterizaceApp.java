package app;

import javax.swing.SwingUtilities;

import gui.MainFrame;
/**
 * Rasterizace - Hlavn� spou�t�c� t��da
 * @author Jakub Josef
 *
 */
public class RasterizaceApp {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			
			@Override
			public void run() {
				new MainFrame().setVisible(true);
			}
		});
		

	}

}
