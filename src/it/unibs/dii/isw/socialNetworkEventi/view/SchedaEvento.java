package it.unibs.dii.isw.socialNetworkEventi.view;


import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.text.DateFormat;
import java.util.Calendar;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import it.unibs.dii.isw.socialNetworkEventi.model.Evento;
import it.unibs.dii.isw.socialNetworkEventi.model.PartitaCalcio;
import it.unibs.dii.isw.socialNetworkEventi.model.Scii;
import it.unibs.dii.isw.socialNetworkEventi.model.Utente;
import it.unibs.dii.isw.socialNetworkEventi.utility.NomeCampo;
import it.unibs.dii.isw.socialNetworkEventi.utility.StatoEvento;

public class SchedaEvento extends JPanel {
	private static final long serialVersionUID = 1L;
	private int Y=0, X;
	private JLabel[] obbligatori = new JLabel[7],
			valObbligatori = new JLabel[7],
			opzionali = new JLabel[4],
			valOpzionali = new JLabel[4],
			dipendenti = new JLabel[3],
			valDipendenti = new JLabel[3];
	JLabel titolo;
	JButton iscriviti;
	AnelloNumerico anello;
	Font testo, testoBottoni;
	int altezzaRighe;
	DateFormat formattaDate = DateFormat.getInstance();
	
	public SchedaEvento(Evento e, Font testo, Font testoBottoni, int altezzaRighe, int larghezza) {
		super();
		this.testo=testo; this.testoBottoni=testoBottoni; this.altezzaRighe=altezzaRighe; X=larghezza;
		setLayout(null);
		setBackground(Grafica.coloreSfondo);
		//Inizializzazione dati comuni ed obbligatori
		titolo = new JLabel((String)e.getCampi().get(NomeCampo.TITOLO).getContenuto());
		titolo.setFont(testoBottoni.deriveFont(testoBottoni.getSize()*2F));
		titolo.setHorizontalAlignment(SwingConstants.CENTER);
		add(titolo);
		
		boolean termine_ritiro_scaduto = Calendar.getInstance().after((Calendar) e.getCampo(NomeCampo.D_O_TERMINE_RITIRO_ISCRIZIONE).getContenuto()),
				termineIscrizioneScaduto = Calendar.getInstance().after((Calendar) e.getCampo(NomeCampo.D_O_CHIUSURA_ISCRIZIONI).getContenuto()),
				eventoHaSpazio = e.getNumeroPartecipanti() < (e.getCampo(NomeCampo.TOLLERANZA_MAX)==null? (Integer)e.getCampo(NomeCampo.PARTECIPANTI).getContenuto() : (Integer)e.getCampo(NomeCampo.PARTECIPANTI).getContenuto() + (Integer)e.getCampo(NomeCampo.TOLLERANZA_MAX).getContenuto());
		Utente uCorrente = Grafica.getIstance().chiediUtenteCorrente();
		if (e.getUtenteCreatore().equals(uCorrente) && !termine_ritiro_scaduto && e.getStato().compareTo(StatoEvento.APERTA)==0) {
			iscriviti = new JButton(" 🗑  Elimina evento");
			iscriviti.addActionListener(click -> Grafica.getIstance().eliminaEvento(e));
		} else if (!e.contieneUtente(uCorrente) && eventoHaSpazio && !termineIscrizioneScaduto) {
			iscriviti = new JButton(" 🖋  Iscriviti");
			iscriviti.addActionListener(click -> iscriviEvento(e));
		} else if (!termine_ritiro_scaduto && e.contieneUtente(uCorrente)){
			iscriviti = new JButton(" ✖  Annulla iscrizione");
			iscriviti.addActionListener(click -> Grafica.getIstance().rimuoviIscrizioneEvento(e));
		}
		if (iscriviti != null) {
			iscriviti.setFont(testoBottoni);
			iscriviti.setBackground(Grafica.coloreBottoni);
			add(iscriviti);
		}
		obbligatori[0]= new JLabel ("Utente creatore: "); valObbligatori[0] = new JLabel(e.getUtenteCreatore().getNome());
		obbligatori[1]= new JLabel ("Numero minimo partecipanti: "); valObbligatori[1] = new JLabel(e.getCampo(NomeCampo.PARTECIPANTI).getContenuto().toString());
		obbligatori[2]= new JLabel ("Ulteriori partecipanti ammessi: "); valObbligatori[2] = new JLabel(e.getCampo(NomeCampo.TOLLERANZA_MAX).getContenuto().toString());
		obbligatori[3]= new JLabel ("Luogo: "); valObbligatori[3] = new JLabel(e.getCampo(NomeCampo.LUOGO).getContenuto().toString());
		obbligatori[4]= new JLabel ("Quota di adesione: "); valObbligatori[4] = new JLabel(e.getCampo(NomeCampo.COSTO).getContenuto().toString() + " €");
		obbligatori[5]= new JLabel ("Inizio: "); valObbligatori[5] = new JLabel(formattaDate.format(((Calendar)e.getCampo(NomeCampo.D_O_INIZIO_EVENTO).getContenuto()).getTime()));
		obbligatori[6]= new JLabel ("Termine iscrizioni: "); valObbligatori[6] = new JLabel(formattaDate.format(((Calendar)e.getCampo(NomeCampo.D_O_CHIUSURA_ISCRIZIONI).getContenuto()).getTime()));
		for (int i=0; i<7; i++) {
			obbligatori[i].setFont(testo);
			valObbligatori[i].setFont(testo);
			add(valObbligatori[i]);
			add(obbligatori[i]);
		}
		
		int partecipanti = (int)(e.getCampo(NomeCampo.PARTECIPANTI).getContenuto());
		if (e.getCampo(NomeCampo.TOLLERANZA_MAX) != null) partecipanti += (int) e.getCampo(NomeCampo.TOLLERANZA_MAX).getContenuto();
		anello = new AnelloNumerico(larghezza/3, partecipanti, e.getNumeroPartecipanti(), testoBottoni, altezzaRighe, Grafica.coloreSfondo);
		add(anello);
		
		//Inizializzazione dati comuni non obbligatori
		if (e.getCampo(NomeCampo.D_O_TERMINE_RITIRO_ISCRIZIONE) != null) {
			opzionali[0] = new JLabel("Iscrizione ritirabile fino a: "); valOpzionali[0] = new JLabel(formattaDate.format(((Calendar)e.getCampo(NomeCampo.D_O_TERMINE_RITIRO_ISCRIZIONE).getContenuto()).getTime()));}
		if (e.getCampo(NomeCampo.D_O_TERMINE_EVENTO) != null) {
			opzionali[1] = new JLabel("Fine evento: "); valOpzionali[1] = new JLabel(formattaDate.format(((Calendar)e.getCampo(NomeCampo.D_O_TERMINE_EVENTO).getContenuto()).getTime()));}
		if (e.getCampo(NomeCampo.NOTE) != null) {
			opzionali[2] = new JLabel("Note: "); valOpzionali[2] = new JLabel(e.getCampo(NomeCampo.NOTE).getContenuto().toString());}
		if (e.getCampo(NomeCampo.BENEFICI_QUOTA) != null) {
			opzionali[3] = new JLabel("Incluso nella quota: "); valOpzionali[3] = new JLabel(e.getCampo(NomeCampo.BENEFICI_QUOTA).getContenuto().toString());}
		for (int i=0; i<4; i++) {
			if (opzionali[i] == null) continue;
			opzionali[i].setFont(testo);
			valOpzionali[i].setFont(testo);
			add(valOpzionali[i]);
			add(opzionali[i]);
		}
		
		//Configurazione dati dipendenti dalla categoria
		if (e instanceof PartitaCalcio) configura((PartitaCalcio)e);
		if (e instanceof Scii) configura((Scii)e);
	}
	
	private void configura(PartitaCalcio p) {
		dipendenti[0] = new JLabel("Fascia di età: ");
		dipendenti[1] = new JLabel("Sesso: ");
		valDipendenti[0] = new JLabel(p.getCampo(NomeCampo.ETA_MINIMA).getContenuto() + " - " + p.getCampo(NomeCampo.ETA_MASSIMA).getContenuto());
		valDipendenti[1] = new JLabel(p.getCampo(NomeCampo.GENERE).getContenuto().toString());
		dipendenti[0].setFont(testo); dipendenti[1].setFont(testo);
		valDipendenti[0].setFont(testo); valDipendenti[1].setFont(testo);
		add(dipendenti[0]); add(dipendenti[1]);
		add(valDipendenti[0]); add(valDipendenti[1]);
	}
	
	private void configura(Scii s) {
		dipendenti[0] = new JLabel("Prezzo del trasporto: "); valDipendenti[0] = new JLabel(s.getCampo(NomeCampo.BIGLIETTO_BUS).getContenuto() + " €");
		dipendenti[1] = new JLabel("Prezzo del pasto: "); valDipendenti[1] = new JLabel(s.getCampo(NomeCampo.PRANZO).getContenuto() + " €");
		dipendenti[2] = new JLabel("Prezzo del noleggio: "); valDipendenti[2] = new JLabel(s.getCampo(NomeCampo.AFFITTO_SCII).getContenuto() + " €");
		dipendenti[0].setFont(testo); dipendenti[1].setFont(testo); dipendenti[2].setFont(testo);
		valDipendenti[0].setFont(testo); valDipendenti[1].setFont(testo); valDipendenti[2].setFont(testo);
		add(dipendenti[0]); add(dipendenti[1]); add(dipendenti[2]);
		add(valDipendenti[0]); add(valDipendenti[1]); add(valDipendenti[2]);
	}
	
	public void paintComponent (Graphics g) {
		super.paintComponent(g);
		titolo.setBounds(20, 20, X-40,altezzaRighe/5*12);
		Y=20+altezzaRighe/5*12;
		anello.ridimensiona(X/3);
		anello.setBounds(X/3, Y+25, X/3, X/3);
		Y+=X/3+50;
		int ldesc = (int)testo.getStringBounds("Ulteriori partecipanti ammessi:aa", ((Graphics2D)g).getFontRenderContext()).getWidth();
		for (int i=0; i<obbligatori.length; i++) {
			obbligatori[i].setBounds(20, Y, ldesc, altezzaRighe);
			valObbligatori[i].setBounds(30+ldesc, Y, X-50-ldesc, altezzaRighe);
			Y += altezzaRighe + 10;
		}
		for (int i=0; i<opzionali.length; i++) {
			if (opzionali[i] == null) continue;
			opzionali[i].setBounds(20, Y, ldesc, altezzaRighe);
			valOpzionali[i].setBounds(30+ldesc, Y, X-50-ldesc, altezzaRighe);
			Y += altezzaRighe + 10;
		}
		for (int i=0; i<dipendenti.length; i++) {
			if (dipendenti[i] == null) continue;
			dipendenti[i].setBounds(20, Y, ldesc, altezzaRighe);
			valDipendenti[i].setBounds(30+ldesc, Y, X-50-ldesc, altezzaRighe);
			Y += altezzaRighe + 10;
		}
		if (iscriviti != null) {
			iscriviti.setBounds(20, Y+10, X-40,altezzaRighe/5*6);
			Y+=20+altezzaRighe/5*6;
		}
		setPreferredSize(new Dimension(X, Y));
	}
	
	void ridimensiona(int larghezza) {X = larghezza;}
	
	void iscriviEvento(Evento e) {
		if (e instanceof Scii) e.setCampiOptPerUtente(Grafica.getIstance().chiediUtenteCorrente(), Grafica.getIstance().sceltePersonali());
		if (e instanceof PartitaCalcio) e.setCampiOptPerUtente(Grafica.getIstance().chiediUtenteCorrente(), null);
		Grafica.getIstance().iscriviEvento(e);
	}
}
