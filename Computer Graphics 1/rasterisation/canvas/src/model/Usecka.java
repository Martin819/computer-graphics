package model;

import gui.MainFrame.Algorithm;

import java.awt.Color;
import java.awt.Point;
import java.util.LinkedList;
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
	private double k,q;
	private List<Bod> body = new LinkedList<>();

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
	public Usecka(Point start, Point stop,List<Bod> body) {
		this.start = start;
		this.stop = stop;
		this.body=body;
	}
	/**
	 * Funkce spocita pro usecku hodnoty K a Q (smernice a konstanta)
	 */
	public void spocitejHodnoty(){
		this.k = ((double) (stop.x - start.x) / (stop.y - start.y)); 
		this.q = (double) (start.x - k * start.y); 
	}
	/**
	 * Funkce srovna orientaci usecky
	 */
	public void srovnejOrientaci(){
		if (start.y > stop.y){
			Point odloz = start;
			start = stop;
			stop = odloz;
		}
	}
	/**
	 * Vrati zda je usecka vodorovna
	 * @return boolean
	 */
	public boolean jeVodorovna(){
		return (start.y == stop.y);
	}
	/**
	 * Vrati zda je usecka svisla
	 * @return boolean
	 */
	public boolean jeSvisla(){
		return (start.x==stop.x);
	}
	/**
	 * Metoda overi zdali se jedna o prusecik s bodem Y
	 * @param y Bod Y
	 * @return boolean;
	 */
	public boolean jePrusecik(int y){
		return ((y>= this.start.y) && (y< this.stop.y));
	}
	/**
	 * Metoda spocita prusecik bodu Y
	 * @param y Bod Y
	 * @return int
	 */
	public int prusecik(int y){
		return (int)(this.k*y+this.q);
	}
	/**
	 * Metoda spoèítá spojení mezi dvìma koncovými body
	 * 
	 * @param algorithm
	 *            Algorytmus (hodnota enumerace)
	 * @return List<Point> Kolekce bodu spojujici dane koncove body
	 */
	public List<Bod> spocitejSpojeni(Algorithm algorithm,Color barva, boolean cleanRaster) {
		if (cleanRaster) {
			body.clear();
		}

		if (this.start != null && this.stop != null) {
			switch (algorithm) {
			case TRIVIAL:
				return trivialAlgorythm(this.start, this.stop,barva);
			case DDA:
				return ddaAlgorythm(this.start, this.stop,barva);
			case BRESENHAM:
				return bresenhamAlgorithm(this.start, this.stop,barva);
			default:
				return null;
			}
		} else {
			return null;
		}
	}

	public List<Bod> getBody() {
		return body;
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

	public double getK() {
		return k;
	}

	public double getQ() {
		return q;
	}

	public void setK(double k) {
		this.k = k;
	}

	public void setQ(double q) {
		this.q = q;
	}

	/**
	 * Metoda overi jestli je usecka rovna, pripadne vykresli v vrati true,
	 * jinak vrati false
	 * 
	 * @param start
	 *            Poèáteèní bod úseèky
	 * @param stop
	 *            Koncový bod úseèky
	 * @param Barva usecky
	 * @return Existuje rovná úseèka?
	 */
	private boolean checkLines(Point start, Point stop,Color barva) {
		if (start.x == stop.x) {
			// v pripade ze jsou body obracene, srovname je
			if (start.y > stop.y) {
				Point odloz = start;
				start = stop;
				stop = odloz;
			}
			for (int i = start.y; i <= stop.y; i++) {
				body.add(new Bod(start.x, i,barva));
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
				body.add(new Bod(i, start.y,barva));
			}
			return true;
		} else {
			return false;
		}

	}

	/* <graficke algorytmy> */

	private List<Bod> trivialAlgorythm(Point start, Point stop,Color barva) {

		if ((start != null) && (stop != null)) {
			// zavolame kontrolni metodu, ktera overi "rovnost" usecky, jinak
			// vrati false
			if (checkLines(start, stop,barva)) {
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
						body.add(new Bod(x, Math.round(y),barva));
					}
				} else {
					if (start.y > stop.y) {
						Point odloz = start;
						start = stop;
						stop = odloz;
					}
					for (int y = start.y; y <= stop.y; y++) {
						float i = (float) ((y - q) / k);
						body.add(new Bod(Math.round(i), y,barva));
					}
				}
			}
			return body;
		} else {
			return null;
		}
	}

	private List<Bod> ddaAlgorythm(Point start, Point stop,Color barva) {
		if ((start != null) && (stop != null)) {
			// zjisteni svisle nebo vodorovne usecky
			if (checkLines(start, stop,barva)) {
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
						body.add(new Bod(i, Math.round(yi),barva));
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
						body.add(new Bod(Math.round(xi), i,barva));
						xi = ((float) (xi + 1 / k));
					}
				}
			}
			return body;
		} else {
			return null;
		}
	}

	private List<Bod> bresenhamAlgorithm(Point start, Point stop,Color barva) {
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
			body.add(new Bod(x1, y1,barva));
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
