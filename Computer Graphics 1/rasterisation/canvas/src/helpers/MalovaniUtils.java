package helpers;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.JColorChooser;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.colorchooser.AbstractColorChooserPanel;
/**
 * Pomocna class prinasejici nektere funkce pro MainFrame
 * @author Jakub Josef
 *
 */
public class MalovaniUtils {
	private static Logger log = Logger.getLogger("MalovaniUtils");

	private static HashMap<String,Object> memory= new HashMap<>();
	/**
	 * Metoda zavola metodu zmeny barvy (bud popredi nebo pozadi)
	 * @param object Objekt na kterem budeme volat metodu
	 * @param methodName Nazev metody
	 * @param barva Barva
	 */
	public static void invokeChangeColorMethod(Object object,String methodName, Color barva) throws NoSuchMethodException {
		
				Method m = object.getClass().getMethod(methodName,Color.class);
				try {
					m.invoke(object, barva);
				} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
					log.log(Level.SEVERE,"Nepodarilo se zavolat metodu zmeny barvy");
					e.printStackTrace();
				}
	}
	/**
	 * Metoda zobrazi Color Chooser a rovnou vrati vybranou barvu
	 * @param defaultColor vstupni (defaultni) barva
	 * @uses HashMap memory
	 * @return color Vybrana barva
	 */
	public static Color showColorChooser(Color defaultColor) {
		final int[] hidedPanelsIDs={1,2,4};
		
		final JColorChooser chooserPane = new JColorChooser(defaultColor);
		final JLabel previewLabel = new JLabel("Vyberte barvu",JLabel.CENTER);
		previewLabel.setFont(new Font("Serif",Font.BOLD | Font.ITALIC,48));
		previewLabel.setSize(previewLabel.getPreferredSize());
		previewLabel.setBorder(BorderFactory.createEmptyBorder(0,0,1,0));
		chooserPane.setPreviewPanel(previewLabel);
		AbstractColorChooserPanel panels[] = chooserPane.getChooserPanels();
		for (int id : hidedPanelsIDs) {
			chooserPane.removeChooserPanel(panels[id]);	
		}
		ActionListener okListener = new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				addToMemory("color_choosed",new Boolean(true));
			}
		};
		ActionListener cancelListener = new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				addToMemory("color_choosed",new Boolean(false));
			}
		};
		final JDialog dialog = JColorChooser.createDialog(null, "Vyberte barvu", true, chooserPane, okListener, cancelListener);
		dialog.setVisible(true);
		if(getMemory().get("color_choosed").equals(new Boolean(true))){
			getMemory().clear();
			return chooserPane.getColor();
		}else{
			getMemory().clear();
			return null;
		}
	}
	/**
	 * Metoda vrati barvu (object Color) ze stringu s nazvem barvy
	 * @param colorString String s nazvem barvy
	 * @return Color
	 * @throws NoSuchFieldException pokud takova barva neexistuje
	 */
	public static Color getColorFromString(String colorString) throws NoSuchFieldException{
		Color color;
		try{
			Field field = Class.forName("java.awt.Color").getField(colorString.toLowerCase());
			color = (Color)field.get(null);
		}catch(ClassNotFoundException | IllegalAccessException e){
			log.log(Level.SEVERE,"Nepodarilo se prevest string na barvu");
			e.printStackTrace();
			color=null;
		}
		return color;
	}
	/**
	 * Metoda vrati stav menuItemu
	 * @param menuItem Samotny menuItem
	 * @param selected Vychozi vybrana barva
	 * @return boolean
	 */
	public static boolean getColorMenuItemState(JMenuItem menuItem,Color selected){
		try{
			return getColorFromString(menuItem.getActionCommand()).equals(selected);
		}catch(NoSuchFieldException e){
			log.log(Level.SEVERE,"Nepodaøilo se nastavit vychozi barvu, barva "+menuItem.getActionCommand()+" neexistuje!");
			return false;
		}
	}
	public static HashMap<String,Object> getMemory() {
		return memory;
	}

	public static void addToMemory(String key, Object object) {
		memory.put(key, object);
	}
}
