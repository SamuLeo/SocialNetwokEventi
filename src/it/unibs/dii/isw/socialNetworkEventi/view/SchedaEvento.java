package it.unibs.dii.isw.socialNetworkEventi.view;


import java.awt.Dimension;
import java.awt.Font;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.LinkedList;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import it.unibs.dii.isw.socialNetworkEventi.controller.Sessione;
import it.unibs.dii.isw.socialNetworkEventi.model.Campo;
import it.unibs.dii.isw.socialNetworkEventi.model.Evento;
import it.unibs.dii.isw.socialNetworkEventi.model.PartitaCalcio;
import it.unibs.dii.isw.socialNetworkEventi.utility.NomeCampi;
import it.unibs.dii.isw.socialNetworkEventi.utility.StatoEvento;

public class SchedaEvento extends JPanel {
	private static final long serialVersionUID = 1L;
	private int Y=0;
	private JLabel[] desc, val;
	JLabel titolo;
	JButton iscriviti;
	AnelloNumerico anello;

	@SuppressWarnings({ "rawtypes", "static-access" })
	public SchedaEvento(Evento e, Font testo, Font testoBottoni, int altezzaRighe, int larghezza) {
		super();
		setLayout(null);
		setBackground(Grafica.coloreSfondo);
		titolo = new JLabel((String)e.getCampi().get(NomeCampi.TITOLO).getContenuto());
		titolo.setFont(testoBottoni.deriveFont(testoBottoni.getSize()*2F));
		titolo.setHorizontalAlignment(SwingConstants.CENTER);
		titolo.setBounds(20, 20, larghezza-40,altezzaRighe/5*12);
		Y=20+altezzaRighe/5*12;
		add(titolo);
		//System.out.println(e.getUtenteCreatore().getId_utente() + " " + Sessione.getUtente_corrente().getId_utente());
		boolean termine_ritiro_scaduto = Calendar.getInstance().after((Calendar) e.getCampo(NomeCampi.D_O_TERMINE_RITIRO_ISCRIZIONE).getContenuto()),
				termineIscrizioneScaduto = Calendar.getInstance().after((Calendar) e.getCampo(NomeCampi.D_O_CHIUSURA_ISCRIZIONI).getContenuto()),
				eventoHaSpazio = e.getNumeroPartecipanti() < (e.getCampo(NomeCampi.TOLLERANZA_MAX)==null? (Integer)e.getCampo(NomeCampi.PARTECIPANTI).getContenuto() : (Integer)e.getCampo(NomeCampi.PARTECIPANTI).getContenuto() + (Integer)e.getCampo(NomeCampi.TOLLERANZA_MAX).getContenuto());
		//JOptionPane.showMessageDialog(null, termine_ritiro_scaduto + " "+ termineIscrizioneScaduto +" "+ eventoHaSpazio + " " + Sessione.utenteIscrittoAllaPartita((PartitaCalcio)e), "", JOptionPane.INFORMATION_MESSAGE);
		if (e.getUtenteCreatore().equals(Grafica.getIstance().chiediUtenteCorrente()) && !termine_ritiro_scaduto && e.getStato().compareTo(StatoEvento.APERTA)==0) {
			iscriviti = new JButton(" 🗑  Elimina evento");
			iscriviti.addActionListener(click -> Grafica.getIstance().eliminaEvento(e));
		} else if (!Sessione.utenteIscrittoAllaPartita((PartitaCalcio)e) && eventoHaSpazio && !termineIscrizioneScaduto) {
			iscriviti = new JButton(" 🖋  Iscriviti");
			iscriviti.addActionListener(click -> Grafica.getIstance().iscriviEvento(e));
		} else if (!termine_ritiro_scaduto && Sessione.utenteIscrittoAllaPartita((PartitaCalcio)e)) {
			iscriviti = new JButton(" ✖  Annulla iscrizione");
			iscriviti.addActionListener(click -> Grafica.getIstance().rimuoviIscrizioneEvento(e));
		}
		if (iscriviti != null) {
			iscriviti.setFont(testoBottoni);
			iscriviti.setBounds(20, Y+10, larghezza-40,altezzaRighe/5*6);
			iscriviti.setBackground(Grafica.getIstance().coloreBottoni);
			add(iscriviti);
		}
		
		
		Y+=20+altezzaRighe/5*6;
		LinkedList<Campo> llist = new LinkedList<>();
		for (Campo c: e.getCampi().values())
			if (c!=null) llist.add(c);
		Campo[] s = llist.toArray(new Campo[0]);
		desc=new JLabel[s.length];
		val=new JLabel[s.length];
		Object temp;
		DateFormat formattaDate = DateFormat.getInstance();
		for (int i=0; i<s.length;i++) {
			desc[i]= new JLabel(s[i].getDescrizione_campo());
			temp=s[i].getContenuto();
			if (temp instanceof Calendar) {
				Calendar t = (Calendar)temp;
				val[i]= new JLabel(formattaDate.format(t.getTime())); //+t.get(Calendar.HOUR_OF_DAY) +  '/'+ (t.get(Calendar.MONTH)+1) + '/' + t.get(Calendar.YEAR)+ ' ' + t.getTime().getHours() + '.'+ t.getTime().getMinutes());
			}
			else val[i] = new JLabel(temp.toString());
			desc[i].setFont(testo); val[i].setFont(testo);
			desc[i].setBounds(20, Y, (larghezza-60)/2, altezzaRighe);
			val[i].setBounds(20+(larghezza-60)/2, Y, (larghezza-60)/2, altezzaRighe);
			Y+= altezzaRighe+20;
			add(desc[i]); add(val[i]);
		}
		int partecipanti = (int)(e.getCampo(NomeCampi.PARTECIPANTI).getContenuto());
		if (e.getCampo(NomeCampi.TOLLERANZA_MAX) != null) partecipanti += (int) e.getCampo(NomeCampi.TOLLERANZA_MAX).getContenuto();
		anello=new AnelloNumerico(larghezza/3, partecipanti, e.getNumeroPartecipanti(), testoBottoni, altezzaRighe, Grafica.coloreSfondo);
		anello.setBounds(larghezza/3, Y+25, larghezza/3, larghezza/3);
		Y+=larghezza/3+50;
		add(anello);
		setPreferredSize(new Dimension(larghezza, Y));
	}
	
	void ridimensiona(int larghezza) {
		titolo.setSize(larghezza-40,titolo.getHeight());
		if (iscriviti != null) iscriviti.setSize(larghezza-40,iscriviti.getHeight());
		int altezzaVecchia = anello.getWidth();
		anello.ridimensiona(larghezza/3);
		anello.setBounds(larghezza/3, Y-25-altezzaVecchia, larghezza/3, larghezza/3);
		Y-=altezzaVecchia-larghezza/3;
		for (JLabel d: desc)
			d.setSize((larghezza-60)/2, d.getHeight());
		for (JLabel v: val)
			v.setBounds(20+(larghezza-60)/2, v.getY(), (larghezza-60)/2, v.getHeight());
		setPreferredSize(new Dimension(larghezza, Y));
	}
}
