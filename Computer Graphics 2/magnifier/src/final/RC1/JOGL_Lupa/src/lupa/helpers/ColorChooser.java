package lupa.helpers;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;

import javax.swing.BorderFactory;
import javax.swing.JColorChooser;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.colorchooser.AbstractColorChooserPanel;

public class ColorChooser {
	private static HashMap<String,Object> memory= new HashMap<>();
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
    public static HashMap<String,Object> getMemory() {
		return memory;
	}

	public static void addToMemory(String key, Object object) {
		memory.put(key, object);
	}
}
