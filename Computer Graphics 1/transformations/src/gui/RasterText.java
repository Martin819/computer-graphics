package gui;

import java.awt.Color;
import java.awt.Point;
/**
 * Pomocna trida textu na rastru
 * @author Jakub Josef
 *
 */
public class RasterText {
		private Point pozice=null;
		private String text="";
		private Color barva=Color.WHITE;
		public RasterText(Point pozice,String text){
			this.pozice=pozice;
			this.text=text;
		}
		public RasterText(Point pozice,String text,Color barva){
			this.pozice=pozice;
			this.text=text;
			this.barva=barva;
		}
		public Point getPozice(){return pozice;}
		public String getText(){return text;}
		public Color getBarva(){return barva;}
}
