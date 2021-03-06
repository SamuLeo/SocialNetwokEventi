package it.unibs.dii.isw.socialNetworkEventi.view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

class AnelloNumerico extends JPanel {
	private static final long serialVersionUID = 1L;
	private static final Color sfondoAnello = new Color(190,190,190);
	Color sfondoCard;
	private int totale, n, lato, altezzaStringhe;
	private JLabel capienza;
	private Font testo;
	
	AnelloNumerico (int w, int totale, int n, Font testo, int altezzaStringhe, Color sfondo) {
		lato=w;
		this.totale=totale;
		this.n=n;
		this.testo=testo;
		this.altezzaStringhe = altezzaStringhe;
		this.sfondoCard=sfondo;
	}
	
	protected void paintComponent(Graphics g) {
		g.setColor(sfondoCard);
		g.fillRect(0, 0, lato, lato);
		g.setColor(sfondoAnello);
		g.fillOval(0, 0, lato, lato);
		g.setColor(new Color((int)(255*Math.pow((double)n/(double)totale,0.5)),(int)(255-255*Math.pow((double)n/(double)totale, 2)),30));
		g.fillArc(0, 0, lato, lato, 90, -360/totale*n);
		g.setColor(sfondoCard);
		g.fillOval(lato/12, lato/12, lato-lato/6, lato-lato/6);
		g.setColor(Color.darkGray);
		if (capienza==null) {capienza = new JLabel(""+n +'/'+totale); this.add(capienza);}
		capienza.setFont(testo);
		capienza.setBounds(lato/10, lato/2-altezzaStringhe/2, lato/5*4, altezzaStringhe);
		capienza.setHorizontalAlignment(SwingConstants.CENTER);
		this.setPreferredSize(new Dimension(lato,lato));
	}
	
	void ridimensiona (int lato) {this.lato=lato;}
}
