package gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;

import javax.swing.JPanel;

import model.Bod;
import model.Utvar;

/**
 * Graficke platno do ktereho vykreslujeme.
 * 
 * @author Jakub Josef
 * 
 */
public class Raster extends JPanel {
	/**
	 * serializeUID
	 */
	private static final long serialVersionUID = 1L;
	private Color barvaPozadi;
	private Color barvaSite;

	private int pocetSloupcu;
	private int pocetRadku;

	private int sirkaBunky;
	private int vyskaBunky;

	private Dimension velikostOkna = new Dimension();
	private Utvar[][] pole;

	// pretizene konstruktory
	public Raster() {
	}

	public Raster(int pocetSloupcu, int pocetRadku) {
		this.pocetSloupcu = pocetSloupcu;
		this.pocetRadku = pocetRadku;

		this.sirkaBunky = 0;
		this.vyskaBunky = 0;

		this.barvaPozadi = Color.BLACK;
		this.barvaSite = Color.WHITE;

		this.pole = new Utvar[pocetSloupcu][pocetRadku];

		this.velikostOkna.setSize(getWidth(), getHeight());
	}

	/**
	 * Metoda upravi rozmery rastru pri zmene velikosti okna
	 */
	private void nastavOkno() {
		if (velikostOkna.height != getHeight()
				|| velikostOkna.width != getWidth()) {
			sirkaBunky = (getWidth()) / pocetSloupcu;
			vyskaBunky = getHeight() / pocetRadku;
		}

	}

	/**
	 * Hlavni kreslici metoda, ktera ma na starost vykreslit jednu bunku
	 * 
	 * @param p
	 *            Souradnice bodu k vykresleni
	 * @param gr
	 *            Graficke platno
	 */
	private void kresliBunku(Point p, Graphics2D gr) {
		if (gr != null) {

			Utvar u = pole[p.x][p.y];

			nastavOkno();
			Rectangle rect = new Rectangle(p.x * sirkaBunky, p.y * vyskaBunky,
					sirkaBunky, vyskaBunky);
			gr.setColor(barvaSite);
			gr.drawRect(rect.x, rect.y, rect.width, rect.height);

			rect.x++;
			rect.y++;
			rect.height--;
			rect.width--;

			if (u != null) {
				u.kresli(gr, rect.x, rect.y, rect.width, rect.height);
			} else {
				gr.setColor(barvaPozadi);
				gr.fillRect(rect.x, rect.y, rect.width, rect.height);
			}
		}
	}

	/**
	 * Vykreslovací metoda pro bunku Staèí pøedat souøadnice bodu, bude použita
	 * výchozí barva
	 * 
	 * @param p
	 *            Souøadnice bodu
	 */
	public void nastavBunkuAVykresliBunku(Point p) {
		pole[p.x][p.y] = new Bod(p.x, p.y);
		kresliBunku(p, (Graphics2D) getGraphics());
	}

	/**
	 * Rozšíøená vykreslovací metoda pro bunku Staèí pøedat souøadnice bodu a
	 * barvu
	 * 
	 * @param p
	 *            Souøadnice bodu
	 * @param c
	 *            Barva bodu
	 */
	public void nastavBunkuAVykresliBunku(Point p, Color c) {
		Utvar u = new Bod(p.x, p.y);
		u.setBarva(c);
		pole[p.x][p.y] = u;
		kresliBunku(p, (Graphics2D) getGraphics());
	
	}

	/**
	 * Rozšíøená vykreslovací metoda pro buòku Staèí pøedat souøadnice bodu a
	 * bod sám
	 * 
	 * @param p
	 *            Souøadnice bodu
	 * @param u
	 *            Utvar ktery chceme vykreslit
	 */
	public void nastavBunkuAVykresliBunku(Point p, Utvar u) {
		pole[p.x][p.y] = u;
		kresliBunku(p, (Graphics2D) getGraphics());
	}

	public void paint(Graphics g) {
		super.paint(g);

		for (int i = 0; i < pocetSloupcu; i++) {
			for (int j = 0; j < pocetRadku; j++) {
				kresliBunku(new Point(i, j), (Graphics2D) g);
			}

		}
	}

	private void smazPlatno() {
		for (int i = 0; i < pocetSloupcu; i++) {
			for (int j = 0; j < pocetRadku; j++) {
				nastavBunkuAVykresliBunku(new Point(i, j), (Utvar) null);
			}

		}
	}

	/**
	 * Metoda vycisti platno
	 */
	public void reset() {
		pole = new Utvar[pocetSloupcu][pocetRadku];
		nastavOkno();
		smazPlatno();

	}

	public Utvar[][] getPole() {
		return pole;
	}

	public int getPocetSloupcu() {
		return pocetSloupcu;
	}

	public void setPocetSloupcu(int pocetSloupcu) {
		this.pocetSloupcu = pocetSloupcu;
	}

	public int getPocetRadku() {
		return pocetRadku;
	}

	public void setPocetRadku(int pocetRadku) {
		this.pocetRadku = pocetRadku;
	}

	public Color getBarvaPozadi() {
		return barvaPozadi;
	}

	public Dimension getVelikostBunky() {
		return new Dimension(sirkaBunky, vyskaBunky);
	}

	public Color getBarvaSite() {
		return barvaSite;
	}

	public void setBarvaPozadi(Color barvaPozadi) {
		this.barvaPozadi = barvaPozadi;
	}

	public void setBarvaSite(Color barvaSite) {
		this.barvaSite = barvaSite;
	}

	public Point getRozmerHraciPlochy() {
		return new Point(this.pocetSloupcu, this.pocetRadku);
	}
}
