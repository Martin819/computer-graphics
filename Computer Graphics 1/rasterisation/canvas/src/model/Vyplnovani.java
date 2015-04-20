package model;

import gui.MainFrame.Algorithm;
import gui.MainFrame;
import gui.Raster;

import java.awt.Color;
import java.awt.Point;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JOptionPane;
/**
 * Trida obsluhujici vlakno s vyplnovanim
 * @author Jakub Josef
 *
 */
public class Vyplnovani extends Thread{
	private static Logger log = Logger.getLogger(Vyplnovani.class.getClass().getName());
	private MainFrame runningOn;
	private Algorithm algoritmus;
	private Point mousePoint;
	private Raster rastr;
	private Color barvaVyplneni;
	private Color barvaCary;
	private List<Bod> vyplneneBody=new LinkedList<>();
	private List<Usecka> objekt = null;

	public Vyplnovani(MainFrame runningOn, Point mousePoint,Algorithm algoritmus,Raster rastr,Color barvaVyplneni,Color barvaCary){
		this.runningOn=runningOn;
		this.mousePoint=mousePoint;
		this.algoritmus=algoritmus;
		this.rastr=rastr;
		this.barvaVyplneni=barvaVyplneni;
		this.barvaCary=barvaCary;
	}
	public void setObjekt(List<Usecka> objekt){
		this.objekt=objekt;
	}
	
	@Override
	public void run(){
		super.run();
		synchronized (MainFrame.class){
			try{
				runningOn.changeVyplnovaniState(true,false);
				
				switch(algoritmus){
					case SEED: vyplneneBody= this.seminkovyAlgorytmus(mousePoint);break;
					case LINESEED: vyplneneBody= this.radkoveSeminko(mousePoint);break;
					case SCANLINE: vyplneneBody= this.scanLine(objekt);break;
					default:
						vyplneneBody=null;break;
				}
				rastr.addDrawedElement(vyplneneBody);
				
				runningOn.changeVyplnovaniState(false,true);
			}catch(StackOverflowError e){
				JOptionPane.showMessageDialog(runningOn, "Zásobník pøetekl pøi vyplòování!","Chyba vyplòování",JOptionPane.ERROR_MESSAGE);
			}
		}
		
	}
	
	/**
	 * Metoda zjisti jestli je bod uvnitr nejakeho objektu (tedy nema stejnou barvu jako barva cary nebo barva vyplneni)
	 * @param bod Bod pro ktery to budeme testovat
	 * @return boolean
	 */
	public boolean jeUvnitr(Point bod){
		try {
			if (!rastr.vratBarvuBodu(bod).equals(barvaCary) && !rastr.vratBarvuBodu(bod).equals(barvaVyplneni)) {
				return true;
			}
		} catch (ArrayIndexOutOfBoundsException e) {
			log.log(Level.SEVERE,"Neznamy bod pro zjisteni barvy uvnitr metody jeUvnitr()");
		}
		return false;
	}
	//vykreslovaci metody
	private List<Bod> seminkovyAlgorytmus(Point seminko) throws StackOverflowError{	
	if(rastr.isOnScreen(new Bod(seminko,barvaCary))){
		if (jeUvnitr(seminko)) {
		
		rastr.vykresliBunku(seminko,barvaVyplneni);
		vyplneneBody.add(new Bod(seminko,barvaVyplneni));
		
		seminkovyAlgorytmus(new Point(seminko.x+1,seminko.y));
		seminkovyAlgorytmus(new Point(seminko.x-1,seminko.y));
		seminkovyAlgorytmus(new Point(seminko.x,seminko.y+1));
		seminkovyAlgorytmus(new Point(seminko.x,seminko.y-1));
		}
	}
	return vyplneneBody;
	}
private List<Bod> radkoveSeminko(Point seminko){
		
		LinkedList<Point> radek = new LinkedList<Point>();
		Point pravyBod;
		Point levyBod;
		radek.addFirst(seminko);
		
		while (!(radek.isEmpty())) {
			pravyBod = radek.pop();
			levyBod = pravyBod;
			
			//cyklus ktery jede doprava na y souradnici
			while ((jeUvnitr(pravyBod))) {	
				//ptam se jestli na stejne souradnici ale o 1 výš
				if (jeUvnitr(new Point(pravyBod.x,pravyBod.y+1))) {
					radek.add(new Point(pravyBod.x,pravyBod.y+1));
				}
				//ptame se jestli o 1 niz neni volny pokud ano vlozim
				if (jeUvnitr(new Point(pravyBod.x,pravyBod.y-1))) {
					radek.add(new Point(pravyBod.x,pravyBod.y-1));
				}//prepis stavaji bod novym bodem o 1 dal doprava
				pravyBod = new Point(pravyBod.x+1,pravyBod.y);
			}
			//cyklus ktery jede do leva dokud nenarazi na hranu
			while ((jeUvnitr(new Point(levyBod.x-1,levyBod.y)))) {
				if (jeUvnitr(new Point(levyBod.x,levyBod.y+1))) {
					radek.add(new Point(levyBod.x,levyBod.y+1));
				}
				if (jeUvnitr(new Point(levyBod.x,levyBod.y-1))) {
					radek.add(new Point(levyBod.x,levyBod.y-1));
				}
				levyBod = new Point(levyBod.x-1,levyBod.y);
			}	

			Usecka usecka = new Usecka(levyBod, pravyBod);
			List<Bod> body = usecka.spocitejSpojeni(Algorithm.DDA, barvaVyplneni, false);
			for (Point bod : body) {
				rastr.nastavBunku(bod, barvaVyplneni);
			}
			vyplneneBody.addAll(body);
		
		}
		rastr.vykresli();
		return vyplneneBody;
	}

	private List<Bod> scanLine(List<Usecka> usecky) {
	List<Usecka> zpracovane = new LinkedList<>();
	int ymin = runningOn.getHeight(), ymax = 0;
	
	for (Usecka usecka : usecky) {
		if (!usecka.jeVodorovna()) {
			//zpracujeme usecku
			usecka.spocitejHodnoty();
			usecka.srovnejOrientaci();
			zpracovane.add(usecka);
			//spocitame hranice
		if(ymin>usecka.getStart().y){ymin = usecka.getStart().y;}
		if(ymax<usecka.getStop().y){ymax = usecka.getStop().y;}
		}
	}
	//spocitame pruseciky
	for (int y = ymin; y <= ymax; y++) {
		List<Integer> prus = new LinkedList<>();
		for (Usecka u : zpracovane) {
			if (u.jePrusecik(y)) {
				prus.add(new Integer(Math.abs(u.prusecik(y))));
			}
		}
		Collections.sort(prus);
		//vykreslujeme
		for (int i = 1; i < prus.size(); i = i+2) {
			Usecka usecka = new Usecka(new Point(prus.get(i-1),y),new Point(prus.get(i), y));
			List<Bod> body = usecka.spocitejSpojeni(Algorithm.DDA, barvaVyplneni, false);
			for (Point bod : body) {
				rastr.nastavBunku(bod, barvaVyplneni);
			}
			
			vyplneneBody.addAll(body);
		}
	}
	//vykreslime hranici
	rastr.vykresliObjekt(usecky);
	//vykreslime vysledek
	rastr.vykresli();
	return vyplneneBody;

	
}
}
