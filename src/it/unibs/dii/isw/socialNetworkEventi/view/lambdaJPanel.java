package it.unibs.dii.isw.socialNetworkEventi.view;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JPanel;

class lambdaJPanel implements MouseListener {
	static final Color coloreNormale = new Color(220,220,220), coloreSopra = new Color(210,210,210), colorePremuto = new Color(200,200,200);
	JPanel jp;
	Class<? extends JPanel> classe;
	lambdaJPanel aggiuntivo;
	ActionListener eventoClick;
	
	lambdaJPanel (JPanel jp, Class<? extends JPanel> classe, lambdaJPanel aggiuntivo, ActionListener eventoClick){
		this.jp=jp;
		this.classe=classe;
		this.aggiuntivo = aggiuntivo;
		this.eventoClick = eventoClick;
	}
	
	public void mouseClicked(MouseEvent e) {
		if (eventoClick != null) eventoClick.actionPerformed(new ActionEvent(jp, 0, null));
	}
	public void mouseEntered(MouseEvent e) {
		try {classe.getDeclaredField("sfondoCard").set(jp, coloreSopra);
			if (aggiuntivo != null) aggiuntivo.mouseEntered(e);
			jp.repaint();
		} catch (Exception e1) {}
	}
	public void mouseExited(MouseEvent e) {
		try {classe.getDeclaredField("sfondoCard").set(jp, coloreNormale);
			if (aggiuntivo != null) aggiuntivo.mouseExited(e);
			jp.repaint();
		} catch (Exception e1) {}
	}
	public void mousePressed(MouseEvent e) {
		try {classe.getDeclaredField("sfondoCard").set(jp, colorePremuto);
			if (aggiuntivo != null) aggiuntivo.mousePressed(e);
			jp.repaint();
		} catch (Exception e1) {}
	}
	public void mouseReleased(MouseEvent e) {
		try {
			if (((Color)classe.getDeclaredField("sfondoCard").get(jp)).getRed() == colorePremuto.getRed()) {
				classe.getDeclaredField("sfondoCard").set(jp, coloreSopra);
				if (aggiuntivo != null) aggiuntivo.mouseReleased(e);
			}
			jp.repaint();
		} catch (Exception e1) {}
	}	
}