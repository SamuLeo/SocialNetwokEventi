package it.unibs.dii.isw.socialNetworkEventi.view;

import java.awt.Color;
import it.unibs.dii.isw.socialNetworkEventi.model.*;
import it.unibs.dii.isw.socialNetworkEventi.utility.NomeCampi;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.util.Calendar;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;


public class Bacheca extends JPanel {
	private static final long serialVersionUID = 1L;
	private static final Color sfondoCard = new Color(220,220,220), sfondoAnello = new Color(200,200,200), sfondo = new Color(240,240,240);
	private cardEvento[] cards;
	private int X=0, Y=0;
	
	Bacheca(int larghezza, Font testo, Font testoBottoni, int altezzaStringhe) {
		super();
		setLayout(null);
		cards = new cardEvento[10];
		for (int i=0; i<10; i++) 
		{
			Calendar data= Calendar.getInstance(), datafine=Calendar.getInstance();
			datafine.set(2019, 1, 33*i %30, 17, 5*i); data.set(2019, 2, 33*i %28, 17*i %30, 5*i);
			cards[i]=new cardEvento(
					new PartitaCalcio("Brescia", datafine, data, 12, 0, "Partita di calcio", null, null, null, 18, 32, "Qualsiasi"),
					larghezza-60, testoBottoni, testo, altezzaStringhe);
			cards[i].setBounds(20, 20*(i+1)+(80+altezzaStringhe/5*21)*i, larghezza-60, 80+altezzaStringhe/5*21);
			this.add(cards[i]);
		}
		X=larghezza; Y= 20*11+(80+altezzaStringhe/5*21)*10;
		this.setPreferredSize(new Dimension(larghezza,Y));
	}
	
	public int getHeight() {return Y;}
	public int getWidth() {return X;}
	
	void ridimensiona(int larghezza) {
		for (int i=0; i<10; i++) {
			cards[i].setBounds(20, 20*(i+1)+cards[i].getHeight()*i, larghezza-60, cards[i].getHeight());
			cards[i].ridimensiona(larghezza-60);
		}
		X=larghezza;
		this.setPreferredSize(new Dimension(larghezza,Y));
	}
	
	class cardEvento extends JPanel {
		private static final long serialVersionUID = 1L;
		Evento e;
		int w, altezzaStringhe;
		Font testo, testoBottoni;
		JLabel titolo, data, ora, sesso, capienza;
		double rand = Math.random();
		
		cardEvento (Evento e, int w, Font testoBottoni, Font testo, int altezzaStringhe) {
			this.e=e;
			this.w=w;
			this.testoBottoni=testoBottoni;
			this.testo=testo;
			this.altezzaStringhe = altezzaStringhe;
		}
		protected void paintComponent(Graphics g) {
			g.setColor(sfondo);
			g.fillRect(0, 0, w, 80+altezzaStringhe/5*21);
			g.setColor(sfondoCard);
			g.fillRoundRect(0, 0, w, 80+altezzaStringhe/5*21, Math.min(Math.max(w/10, 20),80), Math.min(Math.max(w/10, 20),80));
			g.setColor(Color.black);
			g.setFont(testoBottoni);
			if (titolo==null) {titolo = new JLabel("Evento"); this.add(titolo);}
			titolo.setFont(testoBottoni);
			titolo.setBounds(w/10, 15, (int)(w*0.8), altezzaStringhe/5*6);
			Calendar dataOra= (Calendar)e.getCampi().get(NomeCampi.D_O_INIZIO_EVENTO).getCampo();
			if (data==null) {data = new JLabel("Data: " + dataOra.getTime().getDay() + '/'+ dataOra.getTime().getMonth() + '/' + (dataOra.getTime().getYear()+1900)); this.add(data);}
			data.setFont(testo);
			data.setBounds(w/9, 30+titolo.getHeight(), (int)(w*0.6), altezzaStringhe);
			if (ora==null) {ora = new JLabel("Orario: " + '\t' + dataOra.getTime().getHours() + '.'+ dataOra.getTime().getMinutes()); this.add(ora);}
			ora.setFont(testo);
			ora.setBounds(w/9, 40+titolo.getHeight()+data.getHeight(), (int)(w*0.6), altezzaStringhe);
			if (sesso==null) {sesso = new JLabel("Sesso: " + e.getCampi().get(NomeCampi.GENERE).getCampo()); this.add(sesso);}
			sesso.setFont(testo);
			sesso.setBounds(w/9, 50+titolo.getHeight()+data.getHeight()*2, (int)(w*0.6), altezzaStringhe);
			int partecipanti=(int) e.getCampi().get(NomeCampi.PARTECIPANTI).getCampo();
			int iscritti=(int)(rand*partecipanti);
			g.setColor(sfondoAnello);
			g.fillOval(w/3*2, altezzaStringhe/5*6 +15, 40+altezzaStringhe*3, 40+altezzaStringhe*3);
			g.setColor(new Color((int)(255*Math.pow(rand,0.5)),(int)(255-255*Math.pow(rand, 2)),30));
			g.fillArc(w/3*2, altezzaStringhe/5*6 +15, 40+altezzaStringhe*3, 40+altezzaStringhe*3, 0, 360/partecipanti*iscritti);
			g.setColor(sfondoCard);
			g.fillOval(w/3*2+10, altezzaStringhe/5*6+25, 20+altezzaStringhe*3, 20+altezzaStringhe*3);
			g.setColor(Color.darkGray);
			if (capienza==null) {capienza = new JLabel(""+iscritti +'/'+partecipanti); this.add(capienza);}
			capienza.setFont(testo);
			capienza.setBounds(w/3*2+30, altezzaStringhe/5*11+35, altezzaStringhe*3-20, altezzaStringhe);
			capienza.setHorizontalAlignment(SwingConstants.CENTER);
			this.setPreferredSize(new Dimension(w,80+altezzaStringhe/5*21));
		}
		void ridimensiona (int larghezza) {
			w=larghezza;
			repaint();
		}
	}
}