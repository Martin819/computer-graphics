package model;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;

/**
 * Zakladni entita vykreslovani, abstrakni trida popisujici utvar
 * 
 * @author Jakub Josef
 * 
 */
public abstract class Utvar {

	private Color barva;
	private Point pozice;

	public Utvar() {
		this.pozice = new Point();

	}

	public Utvar(Point p) {
		this.pozice = new Point(p);

	}

	public Utvar(int x, int y) {
		this.pozice = new Point(x, y);
	}

	public Color getBarva() {
		return barva;
	}

	public void setBarva(Color barva) {
		this.barva = barva;
	}

	public Point getPozice() {
		return pozice;
	}

	public void setPozice(Point pozice) {
		this.pozice = pozice;
	}

	public void setPozice(int x, int y) {
		pozice.x = x;
		pozice.y = y;
	}

	public abstract void kresli(Graphics2D gr, int x, int y, int sirka,
			int vyska);

}
