package it.unibs.dii.isw.socialNetworkEventi.utility;

import java.awt.Color;
import java.awt.Font;
import java.awt.Toolkit;
import javax.swing.JOptionPane;
import javax.swing.UIManager;

public class MsgBox {
	public static void configuraAspetto() {
		Font f = new Font("sans", Font.PLAIN, Toolkit.getDefaultToolkit().getScreenResolution()/5);
		UIManager.put("OptionPane.messageFont", f);
		UIManager.put("OptionPane.buttonFont", f);
		UIManager.put("OptionPane.background", new Color(238,238,238));
		UIManager.put("OptionPane.messageForeground", Color.black);
		UIManager.put("Button.background", Color.white);
		UIManager.put("Panel.background", new Color(238,238,238));
		UIManager.put("Button.select", new Color(240,255,245));
	}
	
	public void messaggioSemplice(String titolo, String contenuto) {
		mexBase(titolo, contenuto, JOptionPane.INFORMATION_MESSAGE);
	}
	
	public void messaggioErrore(String titolo, String contenuto) {
		UIManager.put("OptionPane.background", new Color(255,240,240));
		UIManager.put("Panel.background", new Color(255,240,240));
		UIManager.put("Button.select", new Color(200,0,0));
		mexBase(titolo, contenuto, JOptionPane.ERROR_MESSAGE);
		configuraAspetto();
	}
	
	public void messaggioAvviso(String titolo, String contenuto) {
		UIManager.put("OptionPane.background", new Color(255,250,240));
		UIManager.put("Panel.background", new Color(255,250,240));
		UIManager.put("Button.select", new Color(160,140,0));
		mexBase(titolo, contenuto, JOptionPane.WARNING_MESSAGE);
		configuraAspetto();
	}
	
	private void mexBase(String titolo, String contenuto, int tipo) {
		JOptionPane.showMessageDialog(null, contenuto, titolo, tipo);
	}
	
	public boolean sceltaSemplice(String titolo, String contenuto) {
		return JOptionPane.showOptionDialog(null, contenuto, titolo, JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, null) == 0;
	}
}