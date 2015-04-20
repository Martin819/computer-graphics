package model;

import gui.MainFrame.Algorithm;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

/**
 * Trida objektu Usecka, obsahuje jednotlive graficke algorytmy a pomocne metody
 * 
 * @author Jakub Josef
 * 
 */
public class Usecka {
	private Point start = null;
	private Point stop = null;
	private List<Point> body = new ArrayList<Point>();

	// pretizene konstuktory
	public Usecka() {
	}

	public Usecka(Point start) {
		this.start = start;
	}

	public Usecka(Point start, Point stop) {
		this.start = start;
		this.stop = stop;
	}

	/**
	 * Metoda spoèítá spojení mezi dvìma koncovými body
	 * 
	 * @param algorithm
	 *            Algorytmus (hodnota enumerace)
	 * @return List<Point> Kolekce bodu spojujici dane koncove body
	 */
	public List<Point> spocitejSpojeni(Algorithm algorithm, boolean cleanRaster) {
		if (cleanRaster) {
			body.clear();
		}

		if (this.start != null && this.stop != null) {
			switch (algorithm) {
			case TRIVIAL:
				return trivialAlgorythm(this.start, this.stop);
			case DDA:
				return ddaAlgorythm(this.start, this.stop);
			case BRESENHAM:
				return bresenhamAlgorithm(this.start, this.stop);
			default:
				return null;
			}
		} else {
			return null;
		}
	}

	public Point getStart() {
		return start;
	}

	public void setStart(Point start) {
		this.start = start;
	}

	public void setStop(Point stop) {
		this.stop = stop;
	}

	public Point getStop() {
		return stop;
	}

	/**
	 * Metoda overi jestli je usecka rovna, pripadne vykresli v vrati true,
	 * jinak vrati false
	 * 
	 * @param start
	 *            Poèáteèní bod úseèky
	 * @param stop
	 *            Koncový bod úseèky
	 * @return Existuje rovná úseèka?
	 */
	private boolean checkLines(Point start, Point stop) {
		if (start.x == stop.x) {
			// v pripade ze jsou body obracene, srovname je
			if (start.y > stop.y) {
				Point odloz = start;
				start = stop;
				stop = odloz;
			}
			for (int i = start.y; i <= stop.y; i++) {
				body.add(new Point(start.x, i));
			}
			return true;
		} else if (start.y == stop.y) {
			// v pripade ze jsou body obracene, srovname je
			if (start.x > stop.x) {
				Point odloz = start;
				start = stop;
				stop = odloz;
			}
			for (int i = start.x; i <= stop.x; i++) {
				body.add(new Point(i, start.y));
			}
			return true;
		} else {
			return false;
		}

	}

	/* <graficke algorytmy> */

	private List<Point> trivialAlgorythm(Point start, Point stop) {

		if ((start != null) && (stop != null)) {
			// zavolame kontrolni metodu, ktera overi "rovnost" usecky, jinak
			// vrati false
			if (checkLines(start, stop)) {
			} else {
				// pokracovani pokud je usecka "sikma"
				double k = (double) (stop.y - start.y) / (stop.x - start.x); // smernice
				double q = (start.y - k * start.x);

				if (Math.abs(k) <= 1) {
					if (start.x > stop.x) {
						Point odloz = start;
						start = stop;
						stop = odloz;
					}
					for (int x = start.x; x <= stop.x; x++) {
						float y = (float) ((k * x) + q);
						body.add(new Point(x, Math.round(y)));
					}
				} else {
					if (start.y > stop.y) {
						Point odloz = start;
						start = stop;
						stop = odloz;
					}
					for (int y = start.y; y <= stop.y; y++) {
						float i = (float) ((y - q) / k);
						body.add(new Point(Math.round(i), y));
					}
				}
			}
			return body;
		} else {
			return null;
		}
	}

	private List<Point> ddaAlgorythm(Point start, Point stop) {
		if ((start != null) && (stop != null)) {
			// zjisteni svisle nebo vodorovne usecky
			if (checkLines(start, stop)) {
			} else {
				// pokracovani pokud je usecka sikma
				double k = ((double) (stop.y - start.y) / (stop.x - start.x));
				if (Math.abs(k) < 1) {
					// pokud je start.x vetsi nez stop.x, prehodime body
					if (start.x > stop.x) {
						Point odloz = start;
						start = stop;
						stop = odloz;
					}
					float yi = start.y;
					for (int i = start.x; i <= stop.x; i++) {
						body.add(new Point(i, Math.round(yi)));
						yi = (float) (yi + k);
					}
				} else {
					// pokud je start.y vetsi nez stop.y tak prehodime body
					if (start.y > stop.y) {
						Point odloz = start;
						start = stop;
						stop = odloz;
					}
					float xi = start.x;
					for (int i = start.y; i <= stop.y; i++) {
						body.add(new Point(Math.round(xi), i));
						xi = ((float) (xi + 1 / k));
					}
				}
			}
			return body;
		} else {
			return null;
		}
	}

	private List<Point> bresenhamAlgorithm(Point start, Point stop) {
		int deltaX = stop.x - start.x;
		int deltaY = stop.y - start.y;
		int stavX1 = 0;
		int stavY1 = 0;
		int stavX2 = 0;
		int stavY2 = 0;

		if (deltaX < 0) {
			stavX1 = -1;
			stavX2 = -1;
		} else if (deltaX > 0) {
			stavX1 = 1;
			stavX2 = 1;
		}

		if (deltaY < 0) {
			stavY1 = -1;
		} else if (deltaY > 0) {
			stavY1 = 1;
		}
		int delsi = Math.abs(deltaX);
		int kratsi = Math.abs(deltaY);
		if (!(delsi > kratsi)) {
			delsi = Math.abs(deltaY);
			kratsi = Math.abs(deltaX);
			if (deltaY < 0) {
				stavY2 = -1;
			} else if (deltaY > 0) {
				stavY2 = 1;
			}
			stavX2 = 0;
		}
		int scitac = delsi >> 1;
		int x1 = start.x;
		int y1 = start.y;
		for (int i = 0; i <= delsi; i++) {
			body.add(new Point(x1, y1));
			scitac = scitac + kratsi;
			if (!(scitac < delsi)) {
				scitac = scitac - delsi;
				x1 = x1 + stavX1;
				y1 = y1 + stavY1;
			} else {
				x1 = x1 + stavX2;
				y1 = y1 + stavY2;
			}
		}
		return body;
	}
	/* </graficke algoritmy> */
}
