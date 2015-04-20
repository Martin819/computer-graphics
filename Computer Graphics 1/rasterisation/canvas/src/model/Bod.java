package model;

import java.awt.Color;
import java.awt.Point;
import java.util.LinkedList;
import java.util.List;

public class Bod extends Point {
private static final long serialVersionUID = 1L;
//nastaveni vychozi barvy
private Color color=Color.BLACK;
//konstruktory
public Bod(){
	
}
public Bod(int x,int y){
	this.x=x;
	this.y=y;
}
public Bod(Color c){
	color=c;
}
public Bod(Point p){
	this.x=p.x;
	this.y=p.y;
}
public Bod(int x,int y,Color c){
	this.x=x;
	this.y=y;
	this.color=c;
}
public Bod(Point p, Color c){
	this.x=p.x;
	this.y=p.y;
	this.color=c;
}
public Color getColor() {
	return color;
}

public void setColor(Color color) {
	this.color = color;
}

public static List<Bod> createBodListFromPointList(List<Point> pointList,Color c){
	List<Bod> bodList = new LinkedList<Bod>();
	for (Point point : pointList) {
		bodList.add(new Bod(point,c));
	}
	return bodList;
}

}
