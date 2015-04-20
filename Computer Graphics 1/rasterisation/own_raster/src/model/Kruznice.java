package model;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

/**
 * Trida objektu Kruznice, obsahuje vykreslovaci algorytmy pro kruznici, metodu
 * pro spocitani polomeru a pomocne zobrazovaci metody.
 * 
 * @author Jakub Josef
 * 
 */
public class Kruznice {
	private Point pocatecniBod = null;
	private int polomer = 0;

	// prezizene konstruktory
	public Kruznice(Point pocatecniBod) {
		this.pocatecniBod = pocatecniBod;
	}

	public Kruznice() {
	}

	/* <vykreslovani kruznice> */
	public List<Point> kresliKruznici() throws Exception {
		if ((pocatecniBod != null) && (polomer != 0)) {
			List<Point> body = new ArrayList<Point>();
			int p = 1 - polomer;
			int x = 0;
			int y = polomer;
			int dveX = 1;
			int dveY = -2 * polomer;

			body.add(new Point(pocatecniBod.x, pocatecniBod.y + polomer));
			body.add(new Point(pocatecniBod.x, pocatecniBod.y - polomer));
			body.add(new Point(pocatecniBod.x + polomer, pocatecniBod.y));
			body.add(new Point(pocatecniBod.x - polomer, pocatecniBod.y));
			while (x < y) {
				if (p >= 0) {
					y = y - 1;
					dveY = dveY + 2;
					p = p + dveY;
				}
				x++;
				dveX = dveX + 2;
				p = p + dveX;
				body.add(new Point(pocatecniBod.x + x, pocatecniBod.y + y));
				body.add(new Point(pocatecniBod.x - x, pocatecniBod.y + y));
				body.add(new Point(pocatecniBod.x + x, pocatecniBod.y - y));
				body.add(new Point(pocatecniBod.x - x, pocatecniBod.y - y));

				body.add(new Point(pocatecniBod.x + y, pocatecniBod.y + x));
				body.add(new Point(pocatecniBod.x - y, pocatecniBod.y + x));
				body.add(new Point(pocatecniBod.x + y, pocatecniBod.y - x));
				body.add(new Point(pocatecniBod.x - y, pocatecniBod.y - x));
			}
			return body;
		} else {
			throw new Exception(
					"Nelze vypoèítat body kruznice, pocatecni bod nebo polomer je null");
		}
	}

	/**
	 * Staticka metoda ktera spocita body kruznice podle startovniho bodu a
	 * polomeru
	 * 
	 * @param startBod
	 *            Startovni bod
	 * @param polomer
	 *            Polomer kruznice
	 * @throws Exception
	 *             Vyjímka pokud pocatecni bod nebo polomer je null
	 * @return Kolekce bodu k vykresleni
	 */
	public static List<Point> kresliKruznici(Point start, int polomer)
			throws Exception {
		if ((start != null) && (polomer != 0)) {
			List<Point> body = new ArrayList<Point>();
			int p = 1 - polomer;
			int x = 0;
			int y = polomer;
			int dveX = 1;
			int dveY = -2 * polomer;

			body.add(new Point(start.x, start.y + polomer));
			body.add(new Point(start.x, start.y - polomer));
			body.add(new Point(start.x + polomer, start.y));
			body.add(new Point(start.x - polomer, start.y));
			while (x < y) {
				if (p >= 0) {
					y = y - 1;
					dveY = dveY + 2;
					p = p + dveY;
				}
				x++;
				dveX = dveX + 2;
				p = p + dveX;
				body.add(new Point(start.x + x, start.y + y));
				body.add(new Point(start.x - x, start.y + y));
				body.add(new Point(start.x + x, start.y - y));
				body.add(new Point(start.x - x, start.y - y));

				body.add(new Point(start.x + y, start.y + x));
				body.add(new Point(start.x - y, start.y + x));
				body.add(new Point(start.x + y, start.y - x));
				body.add(new Point(start.x - y, start.y - x));
			}
			return body;
		} else {
			throw new Exception(
					"Nelze vypoèítat body kruznice, pocatecni bod nebo polomer je null");
		}
	}

	/**
	 * Metoda spocita polomer kruznice (tedy vzdalenost mezi bodem start s bodem
	 * stop)
	 * 
	 * @param start
	 * @param stop
	 * @return polomer
	 */
	public static int spocitejPolomer(Point start, Point stop) {
		int polomer = 0;
		// rovne cary
		if (stop.x == start.x) {
			polomer = Math.abs(stop.y - start.y);
		} else if (stop.y == start.y) {
			polomer = Math.abs(stop.x - start.x);
		} else if (Math.abs(stop.x - start.x) - Math.abs(stop.y - start.y) == 0) {
			polomer = Math.abs(stop.x - start.x);
		} else {
			polomer = (int) Math.sqrt(Math.abs((stop.x - start.x)
					* (stop.x - start.x) - (stop.y - start.y)
					* (stop.y - start.y)));
		}
		return polomer;
	}

	/* gettery a settery */
	public Point getPocatecniBod() {
		return pocatecniBod;
	}

	public void setPocatecniBod(Point pocatecniBod) {
		this.pocatecniBod = pocatecniBod;
	}

	public int getPolomer() {
		return polomer;
	}

	public void setPolomer(int polomer) {
		this.polomer = polomer;
	}
}
