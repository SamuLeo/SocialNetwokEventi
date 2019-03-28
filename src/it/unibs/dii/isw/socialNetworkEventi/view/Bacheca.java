package it.unibs.dii.isw.socialNetworkEventi.view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.stream.Collectors;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import it.unibs.dii.isw.socialNetworkEventi.model.Evento;
import it.unibs.dii.isw.socialNetworkEventi.utility.NomeCampi;
import it.unibs.dii.isw.socialNetworkEventi.utility.StatoEvento;


public class Bacheca extends JPanel {
	private static final long serialVersionUID = 1L;
	private static final Color sfondoCard = new Color(220,220,220), sfondo = new Color(240,240,240);
	private cardEvento[] cards;
	@SuppressWarnings("unused")
	private int X=0, Y=0;
	
	Bacheca(ArrayList<Evento> eventi, int larghezza, Font testo, Font testoBottoni, int altezzaStringhe) 
	{
		super();
		setLayout(null);
		eventi=new ArrayList<> (eventi.stream().filter(evento -> evento.getStato().compareTo(StatoEvento.APERTA)==0).collect(Collectors.toList()));
		cards = new cardEvento[eventi.size()];
		
		for (int i=0; i<eventi.size(); i++) 
		{
			if (eventi.get(i).getStato().compareTo(StatoEvento.APERTA)==0) {
				cards[i]=new cardEvento(eventi.get(i), larghezza-60, testoBottoni, testo, altezzaStringhe);
				cards[i].setBounds(20, 20*(i+1)+(80+altezzaStringhe/5*21)*i, larghezza-60, 80+altezzaStringhe/5*21);
				this.add(cards[i]);
				continue;
			}
		}
		
		X=larghezza; Y= 20*(cards.length+1)+(80+altezzaStringhe/5*21)*cards.length;
		this.setPreferredSize(new Dimension(larghezza,Y));
	}
	
	//public int getHeight() {return Y;}
	//public int getWidth() {return X;}
	
	void ridimensiona(int larghezza) 
	{
		for (int i=0; i<cards.length; i++) 
		{
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
		JLabel data, ora, sesso, capienza;
		JButton bottone_titolo = null;
		AnelloNumerico anello;
		
		cardEvento (Evento e, int w, Font testoBottoni, Font testo, int altezzaStringhe) {
			this.e=e;
			this.w=w;
			this.testoBottoni=testoBottoni;
			this.testo=testo;
			this.altezzaStringhe = altezzaStringhe;
		}
		
		@SuppressWarnings("deprecation")
		protected void paintComponent(Graphics g) {
			//Creazione sfondo della card
			g.setColor(sfondo);
			g.fillRect(0, 0, w, 80+altezzaStringhe/5*21);
			g.setColor(sfondoCard);
			g.fillRoundRect(0, 0, w, 80+altezzaStringhe/5*21, Math.min(Math.max(w/10, 20),80), Math.min(Math.max(w/10, 20),80));
			g.setColor(Color.black);
			//Titolo dell'evento
			if (bottone_titolo == null) 
			{
				String titolo = (e.getCampo(NomeCampi.TITOLO)) != null ? (String)(e.getCampo(NomeCampi.TITOLO).getContenuto()) : "Evento senza nome";
				bottone_titolo = new JButton(titolo);
				this.add(bottone_titolo);
			}
			bottone_titolo.setBorderPainted(false);
			bottone_titolo.addActionListener(event -> Grafica.getIstance().visualizzaEvento(e));
			bottone_titolo.setBackground(sfondoCard);
			bottone_titolo.setHorizontalAlignment(SwingConstants.LEFT);
			bottone_titolo.setFont(testoBottoni);
			bottone_titolo.setBounds(w/10, 15, (int)(w*0.8), altezzaStringhe/5*6);
			//Data ed ora dell'evento
			Calendar dataOra = (Calendar)(e.getCampo(NomeCampi.D_O_INIZIO_EVENTO).getContenuto());
			if (data==null) {data = new JLabel("Data: " + dataOra.getTime().getDate() + '/'+ (dataOra.getTime().getMonth()+1) + '/' + (dataOra.getTime().getYear()+1900)); this.add(data);}
			data.setFont(testo);
			data.setBounds(w/9, 30+bottone_titolo.getHeight(), (int)(w*0.6), altezzaStringhe);
			if (ora==null) {ora = new JLabel("Orario: " + '\t' + (dataOra.getTime().getHours()) + '.'+ dataOra.getTime().getMinutes()); this.add(ora);}
			ora.setFont(testo);
			ora.setBounds(w/9, 40+bottone_titolo.getHeight()+data.getHeight(), (int)(w*0.6), altezzaStringhe);
			//Sesso dei partecipanti alla partita
			if (sesso==null) {sesso = new JLabel("Sesso: " + e.getCampi().get(NomeCampi.GENERE).getContenuto()); this.add(sesso);}
			sesso.setFont(testo);
			sesso.setBounds(w/9, 50+bottone_titolo.getHeight()+data.getHeight()*2, (int)(w*0.6), altezzaStringhe);
			//Anello di visualizzazione degli iscritti
			int latoAnello = 40+altezzaStringhe*3;
			if (anello==null) {
				int partecipanti=(int) e.getCampi().get(NomeCampi.PARTECIPANTI).getContenuto();
				int iscritti=(int)(e.getNumeroPartecipanti());
				anello=new AnelloNumerico(latoAnello,partecipanti,iscritti,testo,altezzaStringhe, sfondoCard);
				add(anello);
			}
			anello.setBounds(Math.min(w/5*4, w-w/10-latoAnello), altezzaStringhe/5*6 +15, latoAnello, latoAnello);
			/*g.setColor(sfondoAnello);
			g.fillOval(w/3*2, altezzaStringhe/5*6 +15, 40+altezzaStringhe*3, 40+altezzaStringhe*3);
			g.setColor(new Color((int)(255*Math.pow((double)iscritti/(double)partecipanti,0.5)),(int)(255-255*Math.pow((double)iscritti/(double)partecipanti, 2)),30));
			g.fillArc(w/3*2, altezzaStringhe/5*6 +15, 40+altezzaStringhe*3, 40+altezzaStringhe*3, 90, -360/partecipanti*iscritti);
			g.setColor(sfondoCard);
			g.fillOval(w/3*2+10, altezzaStringhe/5*6+25, 20+altezzaStringhe*3, 20+altezzaStringhe*3);
			g.setColor(Color.darkGray);
			if (capienza==null) {capienza = new JLabel(""+iscritti +'/'+partecipanti); this.add(capienza);}
			capienza.setFont(testo);
			capienza.setBounds(w/3*2+30, altezzaStringhe/5*11+35, altezzaStringhe*3-20, altezzaStringhe);
			capienza.setHorizontalAlignment(SwingConstants.CENTER);*/
			this.setPreferredSize(new Dimension(w,80+altezzaStringhe/5*21));
		}
		
		void ridimensiona (int larghezza) {w=larghezza;}
	}
}