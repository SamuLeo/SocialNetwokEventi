package it.unibs.dii.isw.socialNetworkEventi.view;


import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.text.DateFormat;
import java.util.LinkedList;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import it.unibs.dii.isw.socialNetworkEventi.model.Notifica;

public class PannelloNotifiche extends JPanel 
{
	private Grafica grafica;
	private static final long serialVersionUID = 1L;
	private int X=0, Y=20, altezzaStringhe;
	private static final Color sfondo = new Color(240,240,240);
	Font testo, testoBottoni;
	CardNotifica[] cards;
	Graphics g;

	public PannelloNotifiche(Grafica grafica, LinkedList<Notifica> notifiche, int larghezza, Font testo, Font testoBottoni, int altezzaStringhe) 
	{
		super();
		this.grafica = grafica;
		setLayout(null);
		this.altezzaStringhe=altezzaStringhe;
		this.testo=testo; this.testoBottoni=testoBottoni;
		cards=new CardNotifica[notifiche.size()];
		for (int i=0; i<notifiche.size();i++) {
			cards[i]=new CardNotifica(notifiche.get(i), larghezza-60);
			cards[i].setBounds(20, Y, larghezza-60, 80+altezzaStringhe/5*16);
			Y+=100+altezzaStringhe/5*16;
			add(cards[i]);
		}
		X=larghezza;
	}
	
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		this.g = g;
		int nuovaY=0, quota=20;
		for (CardNotifica c: cards) {
			nuovaY=c.calcolaY(X-60);
			c.setBounds(20, quota, X-60, nuovaY);
			c.ridimensiona(X-60);
			quota+=nuovaY+20;
		}
		Y=quota;
		setPreferredSize(new Dimension(X,Y));
	}
	
	void eliminaNotifica(Notifica notifica) {
		//PROVARE AD ELIMINARE DIRETTAMENTE LA NOTIFICA E LA RELATIVA CARD, E POI RIDISEGNARE NON FUNZIONA! VIENE SEMPRE E COMUNQUE MANTENUTA DISEGNATA LA VECCHIA CARD ZOMBIE
		grafica.eliminaNotifica(notifica);
	}
	
	void ridimensiona(int larghezza) {
		X=larghezza;
	}
	
	class CardNotifica extends JPanel {
		private static final long serialVersionUID = 1L;
		Color sfondoCard = new Color(220,220,220);
		Notifica n;
		int w;
		JLabel titolo, data, contenuto;
		JButton elimina;
		int Y=0, nRighe;
		
		CardNotifica (Notifica n, int w) {
			this.n=n;
			this.w=w;
			addMouseListener(new lambdaJPanel(this, CardNotifica.class, null,null));
		}

		protected void paintComponent(Graphics g) {
			g.setColor(sfondo);
			calcolaY();
			this.setPreferredSize(new Dimension(w,Y));
			g.fillRect(0, 0, w, Y);
			g.setColor(sfondoCard);
			g.fillRoundRect(0, 0, w, Y, Math.min(Math.max(w/10, 20),80), Math.min(Math.max(w/10, 20),80));
			g.setColor(Color.black);
			if (titolo==null) {titolo = new JLabel(n.getTitolo()); this.add(titolo);}
			titolo.setFont(testoBottoni);
			titolo.setBounds(w/10, 15, (int)(w*0.7), altezzaStringhe/5*6);
			if (data==null) {data = new JLabel(DateFormat.getInstance().format(n.getData().getTime())); this.add(data);}
			data.setFont(testo);
			data.setBounds(w/9, 30+titolo.getHeight(), (int)(w*0.8), altezzaStringhe);
			if (contenuto==null) {contenuto=new JLabel("<HTML>" + n.getContenuto() + "</HTML>"); this.add(contenuto);}
			contenuto.setFont(testo);
			contenuto.setBounds(w/9, 40+titolo.getHeight()+data.getHeight(), (int)(w*0.85), nRighe*altezzaStringhe);
			if (elimina==null) {elimina=new JButton("❌"); this.add(elimina); elimina.addActionListener(e -> eliminaNotifica(n));}
			elimina.setFont(testoBottoni);
			elimina.setBackground(sfondoCard);
			elimina.setBorderPainted(false);
			elimina.setBorder(null);
			elimina.setBounds(w/5*4, 15, altezzaStringhe*2, altezzaStringhe*2);
		}
		
		private int calcolaY() {
			nRighe=((int)testo.getStringBounds(n.getContenuto(), ((Graphics2D)g).getFontRenderContext()).getWidth() / (w/4*3) + 1);
			Y=80+altezzaStringhe/5*(11+5*nRighe);
			return Y;
		}
		
		int calcolaY(int larghezza) {
			return 80+altezzaStringhe/5*(11+5*((int)testo.getStringBounds(n.getContenuto(), ((Graphics2D)g).getFontRenderContext()).getWidth() / (larghezza/4*3) + 1));
		}
		
		int ridimensiona (int larghezza) {
			w=larghezza;
			calcolaY();
			return Y;
		}
	}
}