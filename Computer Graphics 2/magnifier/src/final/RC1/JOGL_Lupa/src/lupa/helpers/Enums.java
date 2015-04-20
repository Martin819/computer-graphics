package lupa.helpers;
/**
 * Pomocná třída nesoucí enumerace
 * @author Jakub Josef
 *
 */
public class Enums{
	
/**
 * Enumerace pro velikosti lupy	
 */
public enum MagnifierSizes{
	SMALL(30),MIDDLE(60),BIG(100);
	public int size;

	private MagnifierSizes(int size){
		this.size=size;
	}
}
/**
 * Enumarace pro meritka lupy
 */
public enum MagnifierScales{
	SMALL_UP(1.5),MIDDLE_UP(1),BIG_UP(0.5),SMALL_DOWN(2.7),MIDDLE_DOWN(3.4),BIG_DOWN(4.2);
	public double size;
	private MagnifierScales(double size){
		this.size=size;
	}
}
}