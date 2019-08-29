package it.unibs.dii.isw.socialNetworkEventi.view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.stream.Collectors;

import javax.swing.JLabel;
import javax.swing.JPanel;

import it.unibs.dii.isw.socialNetworkEventi.model.Evento;
import it.unibs.dii.isw.socialNetworkEventi.utility.NomeCampo;
import it.unibs.dii.isw.socialNetworkEventi.utility.StatoEvento;


public class Bacheca extends JPanel {
	private Grafica grafica;
	private static final long serialVersionUID = 1L;
	private static final Color sfondo = new Color(240,240,240);
	private cardEvento[] cards;
	@SuppressWarnings("unused")
	private int X=0, Y=0;
	
	Bacheca(Grafica grafica, ArrayList<Evento> eventi, int larghezza, Font testo, Font testoBottoni, int altezzaStringhe) 
	{
		super();
		setLayout(null);
		this.grafica = grafica;
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
		Color sfondoCard = new Color(220,220,220);
		Evento e;
		int w, altezzaStringhe;
		Font testo, testoBottoni;
		JLabel titolo, data, ora, catDip, capienza;
		AnelloNumerico anello;
		ActionListener azione;
		
		cardEvento (Evento e, int w, Font testoBottoni, Font testo, int altezzaStringhe) {
			this.e=e;
			this.w=w;
			this.testoBottoni=testoBottoni;
			this.testo=testo;
			this.altezzaStringhe = altezzaStringhe;
			azione = event -> grafica.visualizzaEvento(e);
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
			if (titolo == null) {
				String titoloEvento = (e.getCampo(NomeCampo.TITOLO)) != null ? (String)(e.getCampo(NomeCampo.TITOLO).getContenuto()) : "Evento senza nome";
				titolo = new JLabel(titoloEvento);
				this.add(titolo);
			}
			titolo.setBackground(sfondoCard);
			titolo.setFont(testoBottoni);
			titolo.setBounds(w/10, 15, (int)(w*0.8), altezzaStringhe/5*6);
			//Data ed ora dell'evento
			Calendar dataOra = (Calendar)(e.getCampo(NomeCampo.D_O_INIZIO_EVENTO).getContenuto());
			if (data==null) {data = new JLabel("Data: " + dataOra.getTime().getDate() + '/'+ (dataOra.getTime().getMonth()+1) + '/' + (dataOra.getTime().getYear()+1900)); this.add(data);}
			data.setFont(testo);
			data.setBounds(w/9, 30+titolo.getHeight(), (int)(w*0.6), altezzaStringhe);
			if (ora==null) {ora = new JLabel("Orario: " + '\t' + (dataOra.getTime().getHours()) + '.'+ dataOra.getTime().getMinutes()); this.add(ora);}
			ora.setFont(testo);
			ora.setBounds(w/9, 40+titolo.getHeight()+data.getHeight(), (int)(w*0.6), altezzaStringhe);
			//Sesso dei partecipanti alla partita
			if (e.getCampo(NomeCampo.GENERE) != null) 
				if (catDip==null) 
					{catDip = new JLabel("Sesso: " + e.getCampo(NomeCampo.GENERE).getContenuto()); this.add(catDip);}
			if (e.getCampo(NomeCampo.AFFITTO_SCII) != null)
				if (catDip==null) 
				{catDip = new JLabel("Luogo: " + e.getCampo(NomeCampo.LUOGO).getContenuto()); this.add(catDip);}
			catDip.setFont(testo);
			catDip.setBounds(w/9, 50+titolo.getHeight()+data.getHeight()*2, (int)(w*0.6), altezzaStringhe);
			//Anello di visualizzazione degli iscritti
			int latoAnello = 40+altezzaStringhe*3;
			if (anello==null) {
				int partecipanti=(int) e.getCampo(NomeCampo.PARTECIPANTI).getContenuto();
				if (e.getCampo(NomeCampo.TOLLERANZA_MAX) != null) partecipanti += (int) e.getCampo(NomeCampo.TOLLERANZA_MAX).getContenuto();
				int iscritti=(int)(e.getNumeroPartecipanti());
				anello=new AnelloNumerico(latoAnello,partecipanti,iscritti,testo,altezzaStringhe, sfondoCard);
				//anello.addMouseListener();
				add(anello);
				addMouseListener(new lambdaJPanel(this, cardEvento.class, new lambdaJPanel(anello, AnelloNumerico.class, null,azione),azione));
			}
			anello.setBounds(Math.min(w/5*4, w-w/10-latoAnello), altezzaStringhe/5*6 +15, latoAnello, latoAnello);
			this.setPreferredSize(new Dimension(w,80+altezzaStringhe/5*21));
		}
		
		void ridimensiona (int larghezza) {w=larghezza;}
	}
}