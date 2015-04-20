package model;

import java.awt.Graphics2D;

/**
 * Zakladni vykreslovaci jednotka, jeden bod (jeden pixel).
 * 
 * @author Jakub Josef
 * 
 */
public class Bod extends Utvar {

	public Bod(int x, int y) {
		setPozice(x, y);
	}

	@Override
	public void kresli(Graphics2D gr, int x, int y, int sirka, int vyska) {
		gr.setColor(getBarva());
		gr.fillRect(x, y, sirka, vyska);
	}

}
