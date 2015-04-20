package lupa.helpers;

import java.awt.Image;

import javax.swing.ImageIcon;

public class ImageHelpers {
	/**
	 * Metoda overi jestli se jedna o obrazek
	 * @param image_path cesta k obrazku
	 * @return boolean
	 */
	public static boolean isImage(String image_path){
		  Image image = new ImageIcon(image_path).getImage();
		  if(image.getWidth(null) == -1){
		        return false;
		  }
		  else{
		        return true;
		  }
		}
	/**
	 * Workaround chyby v JOGLu, nutno volat na rozmerech obrazku
	 * @param n rozmer (sirka/vyska)
	 * @return spravna sirka/vyska
	 */
    public static int ceilingPow2(int n) {
    	int pow2 = 1;
    	while (n > pow2) {
    		pow2 = pow2<<1;
    	}
    	return pow2;
    }
}
