package it.unibs.dii.isw.socialNetworkEventi.view;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.util.Calendar;
import javax.swing.JLabel;
import javax.swing.JPanel;

import it.unibs.dii.isw.socialNetworkEventi.model.*;
import it.unibs.dii.isw.socialNetworkEventi.utility.NomeCampi;

public class Bacheca extends JPanel {
	private static final long serialVersionUID = 1L;
	private static final Color sfondo = new Color(210,210,210);
	private cardEvento[] cards;
	private int X=0, Y=0;
	
	Bacheca(int larghezza, Font testo, Font testoBottoni, int altezzaStringhe) {
		super();
		setLayout(null);
		cards = new cardEvento[10];
		for (int i=0; i<10; i++) {
			Calendar data= Calendar.getInstance(), datafine=Calendar.getInstance();
			datafine.set(2019, 1, 33*i %30, 17, 5*i); data.set(2019, 2, 33*i %28, 17*i %30, 5*i);
			cards[i]=new cardEvento(new PartitaCalcio("Brescia", datafine, data, 12, 0, "Partita di calcio", null, null, null, 18, 32, "MF"), larghezza, testoBottoni, testo, altezzaStringhe);
			cards[i].setBounds(20, 20*(i+1)+(60+altezzaStringhe/5*21)*i, larghezza, 60+altezzaStringhe/5*21);
			this.add(cards[i]);
		}
		X=larghezza; Y= 20*11+(60+altezzaStringhe/5*21)*10;
		this.setPreferredSize(new Dimension(larghezza,Y));
	}
	
	public int getHeight() {return Y;}
	public int getWidth() {return X;}
	
	void ridimensiona(int larghezza) {
		for (int i=0; i<10; i++) {
			cards[i].setBounds(20, 20*(i+1)+cards[i].getHeight()*i, larghezza, cards[i].getHeight());
			cards[i].ridimensiona(larghezza);
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
		
		cardEvento (Evento e, int w, Font testoBottoni, Font testo, int altezzaStringhe) {
			this.e=e;
			this.w=w;
			this.testoBottoni=testoBottoni;
			this.testo=testo;
			this.altezzaStringhe = altezzaStringhe;
		}
		protected void paintComponent(Graphics g) {
			g.setColor(sfondo);
			g.fillRoundRect(0, 0, w-80, 60+altezzaStringhe/5*21, Math.min(Math.max(w/10, 20),80), Math.min(Math.max(w/10, 20),80));
			g.setColor(Color.black);
			g.setFont(testoBottoni);
			if (titolo==null) {titolo = new JLabel("Evento"); this.add(titolo);}
			titolo.setFont(testoBottoni);
			titolo.setBounds(w/10, 15, (int)(w*0.8), altezzaStringhe/5*6);
			Calendar dataOra= (Calendar)e.getCampi().get(NomeCampi.D_O_INIZIO_EVENTO).getCampo();
			if (data==null) {data = new JLabel("Data: " + '\t' + dataOra.getTime().getDay() + '/'+ dataOra.getTime().getMonth() + '/' + dataOra.getTime().getYear()); this.add(data);}
			data.setFont(testo);
			data.setBounds(w/9, 25+titolo.getHeight(), (int)(w*0.6), altezzaStringhe);
			if (ora==null) {ora = new JLabel("Orario: " + '\t' + dataOra.getTime().getHours() + '.'+ dataOra.getTime().getMinutes()); this.add(ora);}
			ora.setFont(testo);
			ora.setBounds(w/9, 35+titolo.getHeight()+data.getHeight(), (int)(w*0.6), altezzaStringhe);
			if (sesso==null) {sesso = new JLabel("Sesso: " + '\t' + "Maschietti e femminucce"); this.add(sesso);}
			sesso.setFont(testo);
			sesso.setBounds(w/9, 45+titolo.getHeight()+data.getHeight()*2, (int)(w*0.6), altezzaStringhe);
		}
		void ridimensiona (int larghezza) {
			w=larghezza;
			repaint();
		}
	}
}