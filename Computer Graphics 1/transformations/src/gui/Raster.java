package gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JPanel;

import model.Bod;
import model.Usecka;
import model.Usecka.Algorithm;

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
	private static Logger log = Logger.getLogger(Raster.class.getClass().getName());
	
	private Color barvaPozadi=Color.BLACK;

	private Dimension velikostOkna = new Dimension();
	private List<Bod> body;
	private List<List<Bod>> usecky;
	private List<RasterText> stringy;
	private BufferedImage img;
	private Graphics2D gr = null;

	// pretizene konstruktory
	public Raster(int width,int height) {
		body=new LinkedList<Bod>();
		usecky=new LinkedList<List<Bod>>();
		stringy=new LinkedList<RasterText>();
		velikostOkna=new Dimension(width, height);
		getImage();
		
	}
	/**
	 * Hlavni kreslici metoda, ktera ma na starost vykreslit jednu bunku
	 * 
	 * @param p
	 *            Souradnice bodu k vykresleni
	 */
	private void kresliBod(Bod bod){
		//osetreni hranic
		if(isOnScreen(bod)){
			img.setRGB(bod.x, bod.y, bod.getColor().getRGB());
		}
	}
	public Color vratBarvuBodu(Point point){
		int barva = img.getRGB(point.x, point.y);
		if(barva!=0){
			return new Color(barva);
		}else{
			return getBarvaPozadi();
		}
	   
	}
	/**
	 * Hlavni metoda pro bunku Staèí pøedat souøadnice bodu, bude použita
	 * výchozí barva, pote je nutne vykreslit
	 * 
	 * @param p
	 *            Souøadnice bodu
	 */
	public void nastavBunku(Point p) {
		body.add(new Bod(p));
		kresliBod(new Bod(p));
	}
	/**
	 * Metoda rovnou vykresli bunku, bude pouzita
	 * výchozí barva, pote je nutne vykreslit
	 * 
	 * @param p
	 *            Souøadnice bodu
	 */
	public void vykresliBunku(Point p) {
		body.add(new Bod(p));
		kresliBod(new Bod(p));
		vykresli();
	}
	/**
	 * Metoda rovnou vykresli bunku, se zadanou barvou
	 * 
	 * @param p
	 *            Souøadnice bodu
	 * @param p Barva
	 */
	public void vykresliBunku(Point p,Color c) {
		Bod bod=new Bod(p,c);
		body.add(bod);
		kresliBod(bod);
		vykresli();
	}
	public void vykresliBunky(List<Bod> bunky){
		for (Bod bod : bunky) {
			nastavBunku(new Point(bod),bod.getColor());
		}
		vykresli();
	}
	public void vykresliObjekt(List<Usecka> usecky) {
		for(Usecka u:usecky){
			if(u.getBody()!=null){
				vykresliBunky(u.getBody());
			}else{
				log.log(Level.WARNING,"Nektera z usecek kterou chcete vykreslit nema spocitane body!");
			}
		}
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
	public void nastavBunku(Point p, Color c) {
		Bod bod=new Bod(p,c);
		body.add(bod);
		kresliBod(bod);
	
	}
	public void smazBunku(Point p) {
		body.remove(new Bod(p));
		kresliBod(new Bod(p,getBarvaPozadi()));
		vykresli();
	}
	public void kresliUsecku(Point start,Point stop,Color barva){
		if(start.equals(stop)){
			vykresliBunku(start, barva);
		}else if((stop.x-start.x == 1) && (stop.y-start.y==1)){
			vykresliBunku(stop,barva);
		}else{
			Usecka u = new Usecka(start,stop);
			List<Bod> spojeni = u.spocitejSpojeni(Algorithm.BRESENHAM, barva, false);
			addDrawedElement(spojeni);
			vykresliBunky(spojeni);
		}
	}
	public void kresliText(Point pozice,String text,Color barva){
		this.drawString(pozice, text,barva);
		addDrawedText(pozice,text,barva);
	}
	private void drawString(Point pozice,String text,Color barva){
		gr.setColor(barva);
		gr.drawString(text,pozice.x,pozice.y);
		vykresli();
		
	}
	public void paint(Graphics g) {
		super.paint(g);
		nastavOkno();
		g.drawImage(img, 0, 0, null);
	}
	public void prekresliPlatno() {
		getImage();
		for (Bod bod : body) {
			kresliBod(bod);
		}
		for(RasterText rasterText: stringy){
			drawString(rasterText.getPozice(),rasterText.getText(),rasterText.getBarva());
		}
	}
	private void getImage() {
		
		img= new BufferedImage(velikostOkna.width, velikostOkna.height, BufferedImage.TYPE_INT_ARGB);
		setBackground(getBarvaPozadi());
		gr=img.createGraphics();
	}
	private void nastavOkno() {
		if(velikostOkna.width!=getWidth() || velikostOkna.height!=getHeight()){
			velikostOkna=new Dimension(getWidth(), getHeight());
			prekresliPlatno();
		}
	}
	public void vykresli() {
		super.repaint();
		gr.drawImage(img, 0, 0, null);
	}
	public void vykresli(Graphics g) {
		super.repaint();
		g.drawImage(img, 0, 0, null);
	}

	public void smazPlatno() {
		prekresliPlatno();
		vykresli();
	}

	/**
	 * Metoda vycisti platno
	 */
	public void reset() {
		body=new LinkedList<Bod>();
		usecky=new LinkedList<List<Bod>>();
		stringy=new LinkedList<RasterText>();
		smazPlatno();

	}
	/**
	 * Metoda prekresli nepouzite pixely na platne, pouzivana pro metodu Click&Move
	 */
	public void redraw(){
		body=new ArrayList<Bod>();
		smazPlatno();
	}
	public boolean isOnScreen(Bod bod) {
		return (bod.x>=0) && (bod.y>=0) && (bod.x<getVelikostOkna().width) && (bod.y<getVelikostOkna().height);
	}
	public void addDrawedElement(List<Bod> usecka){
		usecky.add(usecka);
	}
	private void addDrawedText(Point pozice, String text,Color barva) {
		stringy.add(new RasterText(pozice,text,barva));
		
	}
	public List<Bod> getBody() {
		return body;
	}

	public Color getBarvaPozadi() {
		return barvaPozadi;
	}

	public void setBarvaPozadi(Color barvaPozadi) {
		this.barvaPozadi = barvaPozadi;
		prekresliPlatno();
	}
	public Dimension getVelikostOkna() {
		return velikostOkna;
	}
	public void setVelikostOkna(Dimension velikostOkna) {
		this.velikostOkna = velikostOkna;
	}
	public Graphics2D getRasterGraphics(){
		return gr;
	}

}
