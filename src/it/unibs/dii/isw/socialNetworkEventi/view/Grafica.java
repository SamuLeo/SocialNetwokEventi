package it.unibs.dii.isw.socialNetworkEventi.view;


import java.awt.*;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.font.FontRenderContext;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Observable;
import java.util.Observer;

import javax.swing.*;
import javax.swing.plaf.basic.BasicArrowButton;
import javax.swing.plaf.basic.BasicScrollBarUI;

import it.unibs.dii.isw.socialNetworkEventi.controller.Sessione;
import it.unibs.dii.isw.socialNetworkEventi.model.Evento;
import it.unibs.dii.isw.socialNetworkEventi.model.Notifica;
import it.unibs.dii.isw.socialNetworkEventi.model.Utente;
import it.unibs.dii.isw.socialNetworkEventi.utility.CategoriaEvento;
import it.unibs.dii.isw.socialNetworkEventi.utility.NomeCampo;

public class Grafica implements Observer {
	private static Grafica me;
	private Grafica() {sessione = Sessione.getInstance();}
	public static Grafica getIstance() 
	{if (me==null) 
		{me = new Grafica(); 
			return me;}
		else return me;}
	
	private Sessione sessione;
	JFrame frame;
	static ImageIcon icona = ottieniIcona();
	static URL percorsoIcona;
	
	JButton btnNuovoEvento = new JButton(" ➕ Aggiungi Evento"), 
			btnBacheca = new JButton ("Bacheca 🏠"),
			btnConfermaCreazioneEvento = new JButton(" ✔ Conferma"), 
			btnAnnullaCreazioneEvento = new JButton(" ✖ Annulla"), 
			btnNotifiche = new JButton(" 💬  Notifiche"), 
			btnProfilo = new JButton(" 👤  Visualizza Profilo");
	JPanel toolbarBacheca = new JPanel(), 
			barraFunzioni = new JPanel(), 
			barraForm = new JPanel();
	Login loginPane;
	JScrollPane pannelloCentrale = new JScrollPane();
	Bacheca bacheca;
	CreazioneEvento form;
	PannelloNotifiche pannelloNotifiche;
	SchedaEvento schedaEvento;
	PannelloUtente schedaUtente;
	SceltaInviti invitatore;
	JComboBox<String> selettoreCategoria = new JComboBox<>();
	
	private ComponentListener listenerRidimensionamento = new listenerRidimensionamento();
	
	Dimension screenSize=Toolkit.getDefaultToolkit().getScreenSize();
	int screenW = (int)(screenSize.getWidth());
	int screenH = (int)(screenSize.getHeight());
	private int larghezzaStrPassword, altezzaStringhe, larghezzaCampiUtentePswd;
	static final Font fontTestoBottoni=new Font("DOBBIAMO TROVARE UN FONT CARINO", Font.PLAIN, Toolkit.getDefaultToolkit().getScreenResolution()/5);
	static final Font fontTesto=new Font("DOBBIAMO TROVARE UN FONT CARINO", Font.PLAIN, Toolkit.getDefaultToolkit().getScreenResolution()/6);
	static final Color coloreBottoni = new Color(255,255,255);
	static final Color coloreSfondo = new Color(244,244,244);
	static final Color coloreBarra = new Color(210,210,210);
	
	public void crea() {
		//Operazioni iniziali sul Frame e sulle variabili di classe
			frame = new JFrame();
			frame.setMinimumSize(new Dimension(screenH/3, (int) (screenH/2.25)));
			frame.setIconImage(icona.getImage());
			frame.setBounds(screenW/2-(int)(screenH/4.4), screenH/2-(int)(screenH/4.4), (int)(screenH/2.2), (int)(screenH/1.8));
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.setVisible(true);
			frame.getContentPane().setBackground(coloreBottoni);
			frame.addComponentListener(listenerRidimensionamento);
			calcolaDimensioniStringhe();
			UIManager.put("OptionPane.messageFont", fontTesto);
			UIManager.put("OptionPane.buttonFont", fontTestoBottoni);
			UIManager.put("TextField.font", fontTesto);
			UIManager.put("Button.background", coloreBottoni);
			UIManager.put("Button.select", new Color(240,255,245));
			UIManager.put("ScrollBar.background", coloreSfondo);
			UIManager.put("ComboBox.selectionBackground", coloreBottoni);
			UIManager.put("ComboBox.background", coloreBottoni);
		//Inizializzazione componenti
			btnNuovoEvento.setFont(fontTestoBottoni);
			btnNuovoEvento.setBackground(coloreBottoni);
			btnNuovoEvento.addActionListener(e -> iniziaCreazioneEvento());
			selettoreCategoria.setFont(fontTesto);
			selettoreCategoria.insertItemAt(" ⚽ Calcio", 0);
			selettoreCategoria.insertItemAt(" ⛷ Scii", 1);
			selettoreCategoria.setSelectedIndex(0);	//Istruzione che deve stare prima dell'aggiunta del listener
			selettoreCategoria.addItemListener(selezione -> visualizzaBacheca());
			selettoreCategoria.setBackground(coloreBottoni);
			selettoreCategoria.setFocusable(false);
			btnBacheca.setBackground(coloreBottoni);
			btnBacheca.setFont(fontTestoBottoni);
			btnBacheca.addActionListener(e -> visualizzaBacheca());
			btnNotifiche.setBackground(coloreBottoni);
			btnNotifiche.setFont(fontTestoBottoni);
			btnNotifiche.addActionListener(e -> visualizzaPannelloNotifiche());
			btnConfermaCreazioneEvento.setFont(fontTestoBottoni);
			btnConfermaCreazioneEvento.setBackground(coloreBottoni);
			btnAnnullaCreazioneEvento.setFont(fontTestoBottoni);
			btnAnnullaCreazioneEvento.setBackground(coloreBottoni);
			btnAnnullaCreazioneEvento.addActionListener(e -> visualizzaBacheca());
			btnProfilo.setBackground(coloreBottoni);
			btnProfilo.setFont(fontTestoBottoni);
			btnProfilo.addActionListener(click -> visualizzaProfilo());
			toolbarBacheca.setLayout(new BorderLayout(0, 0));
			toolbarBacheca.setBackground(coloreSfondo);
			toolbarBacheca.add(btnNuovoEvento, BorderLayout.EAST);
			toolbarBacheca.add(selettoreCategoria,BorderLayout.CENTER);
			barraFunzioni.setLayout(new GridLayout(1,2));
			barraFunzioni.add(btnBacheca);
			barraFunzioni.add(btnNotifiche);
			barraForm.setLayout(new BorderLayout(0, 0));
			barraForm.add(btnConfermaCreazioneEvento, BorderLayout.CENTER);
			barraForm.add(btnAnnullaCreazioneEvento, BorderLayout.EAST);
			pannelloCentrale.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
			pannelloCentrale.getVerticalScrollBar().setUnitIncrement(screenH/250);
	}
	
	public void svuotaFrame() {
		if (loginPane != null) {
			try {frame.getContentPane().remove(loginPane);} catch (Exception e) {}
			loginPane=null;
			frame.getContentPane().setLayout(new BorderLayout(0, 0));
		}
		try {frame.getContentPane().remove(toolbarBacheca);} catch (Exception e) {}
		try {frame.getContentPane().remove(barraFunzioni);} catch (Exception e) {}
		try {frame.getContentPane().remove(barraForm);} catch (Exception e) {}
		try {frame.getContentPane().remove(btnProfilo);} catch (Exception e) {}
		try {frame.getContentPane().remove(pannelloCentrale);} catch (Exception e) {}
		try {frame.getContentPane().remove(schedaUtente);} catch (Exception e) {}
		try {frame.getContentPane().remove(invitatore);} catch (Exception e) {}
		frame.getContentPane().revalidate();
	}
	
	public void mostraLogin() {
		frame.setTitle("Login");
		frame.setLayout(null);
		loginPane=new Login(fontTestoBottoni, fontTesto, larghezzaStrPassword , altezzaStringhe, larghezzaCampiUtentePswd);
		loginPane.setBounds((int)((frame.getContentPane().getWidth()-loginPane.getWidth())/2), (int)((frame.getContentPane().getHeight()-loginPane.getHeight())/2), loginPane.getWidth(), loginPane.getHeight());
		frame.setMinimumSize(new Dimension(Math.max(screenH/3,loginPane.getWidth()), Math.max((int) (screenH/2.25),loginPane.getHeight())));
		frame.getContentPane().add(loginPane);
		loginPane.focus();
		frame.repaint();
	}
	
	public void visualizzaBacheca() {
		//Riconfigurazione del Frame
		frame.setTitle("Bacheca di @" + chiediUtenteCorrente().getNome());
		svuotaFrame();
		frame.getContentPane().add(toolbarBacheca, BorderLayout.NORTH);
		frame.getContentPane().add(barraFunzioni, BorderLayout.SOUTH);
		btnBacheca.setText("\u21BA Aggiorna contenuti");
		if (form != null) form.setVisible(false);
		if (schedaEvento != null) schedaEvento.setVisible(false);
		if (pannelloNotifiche != null) pannelloNotifiche.setVisible(false);
		if (bacheca != null) bacheca.setVisible(true);
		if (schedaUtente != null) schedaUtente.setVisible(false);
		//Creazione pannello principale
		//Se la Bacheca è già in mostra, va solo aggiornata
		if (bacheca != null && bacheca.isVisible()) sessione.aggiornatore.run();
		//JOptionPane.showMessageDialog(null, "Frame: " + frame.getWidth() + " C.P.: " + frame.getContentPane().getWidth() + " Pannello: " +pannelloCentrale.getWidth(), "Partecipanti", JOptionPane.INFORMATION_MESSAGE);
		CategoriaEvento catSelezionata = selettoreCategoria.getSelectedIndex() == 0 ? CategoriaEvento.PARTITA_CALCIO : CategoriaEvento.SCII;
		ArrayList<Evento> listaEventi = sessione.getEventi().get(catSelezionata);
		if (listaEventi == null) listaEventi = new ArrayList<>();
		bacheca = new Bacheca(listaEventi, frame.getContentPane().getWidth(),fontTesto, fontTestoBottoni, altezzaStringhe);
		iniziaOsservazione();
		pannelloCentrale = new JScrollPane(bacheca);
		pannelloCentrale.getVerticalScrollBar().setUnitIncrement(screenH/250);
		pannelloCentrale.getVerticalScrollBar().setUI(new BellaScrlb());
		pannelloCentrale.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		pannelloCentrale.setPreferredSize(new Dimension(frame.getContentPane().getWidth(),frame.getContentPane().getHeight()-toolbarBacheca.getHeight()-barraFunzioni.getHeight()));
		frame.getContentPane().add(pannelloCentrale, BorderLayout.CENTER);
		barraFunzioni.repaint(200);
		frame.getContentPane().revalidate();
		toolbarBacheca.repaint(200);
	}
	
	public void iniziaCreazioneEvento() {
		//Riconfigurazione del Frame
		frame.setTitle("Crea evento");
		fermaOsservazione();
		svuotaFrame();
		frame.getContentPane().add(barraForm, BorderLayout.SOUTH);
		if (form != null) form.setVisible(true);
		if (schedaEvento != null) schedaEvento.setVisible(false);
		if (pannelloNotifiche != null) pannelloNotifiche.setVisible(false);
		if (bacheca != null) bacheca.setVisible(false);
		if (schedaUtente != null) schedaUtente.setVisible(false);
		//Creazione pannello principale
		form = new CreazioneEvento(fontTesto, frame.getContentPane().getWidth(), altezzaStringhe, coloreSfondo);
		pannelloCentrale = new JScrollPane(form);
		pannelloCentrale.getVerticalScrollBar().setUnitIncrement(screenH/250);
		pannelloCentrale.getVerticalScrollBar().setUI(new BellaScrlb());
		pannelloCentrale.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		pannelloCentrale.setPreferredSize(new Dimension(frame.getContentPane().getWidth(),frame.getContentPane().getHeight()-barraForm.getHeight()));
		frame.getContentPane().add(pannelloCentrale, BorderLayout.CENTER);
		frame.getContentPane().revalidate();
		barraForm.repaint(200);
	}
	
	public void visualizzaPannelloNotifiche() {
		frame.setTitle("Notifche");
		fermaOsservazione();
		svuotaFrame();
		frame.getContentPane().add(barraFunzioni, BorderLayout.SOUTH);
		frame.getContentPane().add(btnProfilo, BorderLayout.NORTH);
		btnBacheca.setText("Bacheca 🏠");
		if (form != null) form.setVisible(false);
		if (bacheca != null) bacheca.setVisible(false);
		if (schedaEvento != null) schedaEvento.setVisible(false);
		if (pannelloNotifiche != null) pannelloNotifiche.setVisible(false);
		if (schedaUtente != null) schedaUtente.setVisible(false);
		//Creazione pannello principale
		pannelloNotifiche=new PannelloNotifiche(sessione.getNotificheUtente(), frame.getContentPane().getWidth(), fontTesto, fontTestoBottoni, altezzaStringhe);
		pannelloCentrale = new JScrollPane(pannelloNotifiche);
		pannelloCentrale.getVerticalScrollBar().setUnitIncrement(screenH/250);
		pannelloCentrale.getVerticalScrollBar().setUI(new BellaScrlb());
		pannelloCentrale.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		pannelloCentrale.setPreferredSize(new Dimension(frame.getContentPane().getWidth(),frame.getContentPane().getHeight()-barraFunzioni.getHeight()-btnProfilo.getHeight()));
		frame.getContentPane().add(pannelloCentrale, BorderLayout.CENTER);
		barraFunzioni.repaint(200);
		frame.getContentPane().revalidate();
		btnProfilo.repaint(200);
	}
	
	public void visualizzaEvento(Evento e) {
		if (e==null) return;
		frame.setTitle((String)e.getCampi().get(NomeCampo.TITOLO).getContenuto());
		fermaOsservazione();
		svuotaFrame();
		frame.getContentPane().add(barraFunzioni, BorderLayout.SOUTH);
		btnBacheca.setText("Bacheca 🏠");
		if (form != null) form.setVisible(false);
		if (bacheca != null) bacheca.setVisible(false);
		if (pannelloNotifiche != null) pannelloNotifiche.setVisible(true);
		if (schedaEvento != null) schedaEvento.setVisible(true);
		if (schedaUtente != null) schedaUtente.setVisible(false);
		//Creazione pannello principale
		schedaEvento=new SchedaEvento(e, fontTesto, fontTestoBottoni, altezzaStringhe, frame.getContentPane().getWidth());
		pannelloCentrale = new JScrollPane(schedaEvento);
		pannelloCentrale.getVerticalScrollBar().setUnitIncrement(screenH/250);
		pannelloCentrale.getVerticalScrollBar().setUI(new BellaScrlb());
		pannelloCentrale.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		pannelloCentrale.setPreferredSize(new Dimension(frame.getContentPane().getWidth(),frame.getContentPane().getHeight()-barraFunzioni.getHeight()));
		frame.getContentPane().add(pannelloCentrale, BorderLayout.CENTER);
		frame.getContentPane().revalidate();
		barraFunzioni.repaint(200);
	}
	
	public void visualizzaProfilo() {
		//Riconfigurazione del Frame
		frame.setTitle("Profilo di @" + chiediUtenteCorrente().getNome());
		fermaOsservazione();
		svuotaFrame();
		frame.getContentPane().add(barraFunzioni, BorderLayout.SOUTH);
		btnBacheca.setText("Bacheca 🏠");
		if (form != null) form.setVisible(false);
		if (schedaEvento != null) schedaEvento.setVisible(false);
		if (pannelloNotifiche != null) pannelloNotifiche.setVisible(false);
		if (bacheca != null) bacheca.setVisible(false);
		if (schedaUtente != null) schedaUtente.setVisible(true);
		//Creazione pannello principale
		schedaUtente = new PannelloUtente(chiediUtenteCorrente(), fontTesto, fontTestoBottoni, altezzaStringhe, frame.getContentPane().getWidth());
		pannelloCentrale = new JScrollPane(schedaUtente);
		pannelloCentrale.getVerticalScrollBar().setUnitIncrement(screenH/250);
		pannelloCentrale.getVerticalScrollBar().setUI(new BellaScrlb());
		pannelloCentrale.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		pannelloCentrale.setPreferredSize(new Dimension(frame.getContentPane().getWidth(),frame.getContentPane().getHeight()-barraFunzioni.getHeight()));
		frame.getContentPane().add(pannelloCentrale, BorderLayout.CENTER);
		frame.getContentPane().revalidate();
		barraFunzioni.repaint(200);
		schedaUtente.ridimensiona(frame.getContentPane().getWidth());
	}
	
	public void visualizzaFormInviti(Evento e, CategoriaEvento categoria) {
		LinkedList<Utente> invitabili = sessione.getUtentiDaEventiPassati(categoria);
		if (invitabili.size() == 0) {visualizzaBacheca(); return;}
		frame.setTitle("Scegli chi invitare al tuo evento");
		svuotaFrame();
		if (form != null) form.setVisible(false);
		if (schedaEvento != null) schedaEvento.setVisible(false);
		if (pannelloNotifiche != null) pannelloNotifiche.setVisible(false);
		if (bacheca != null) bacheca.setVisible(false);
		if (schedaUtente != null) schedaUtente.setVisible(false);
		//Creazione pannello principale
		invitatore = new SceltaInviti(e, invitabili, fontTesto, frame.getContentPane().getWidth(), altezzaStringhe);
		pannelloCentrale = new JScrollPane(invitatore);
		pannelloCentrale.getVerticalScrollBar().setUnitIncrement(screenH/250);
		pannelloCentrale.getVerticalScrollBar().setUI(new BellaScrlb());
		pannelloCentrale.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		pannelloCentrale.setPreferredSize(new Dimension(frame.getContentPane().getWidth(),frame.getContentPane().getHeight()));
		frame.getContentPane().add(pannelloCentrale, BorderLayout.CENTER);
		frame.getContentPane().revalidate();
	}
	
	private void calcolaDimensioniStringhe() {
		FontRenderContext frc = ((Graphics2D)frame.getGraphics()).getFontRenderContext();
		larghezzaStrPassword = (int)fontTesto.getStringBounds("Password: ", frc).getWidth();
		altezzaStringhe = (int)fontTesto.getStringBounds("abj", frc).getHeight();
		larghezzaCampiUtentePswd = (int)fontTesto.getStringBounds("abcdefghijklmnopqrst", frc).getWidth();
	}
	
	static ImageIcon ottieniIcona() {
		try {
			percorsoIcona = Grafica.class.getResource("Icona.png");
			return new ImageIcon(percorsoIcona);
		} catch (Exception e) {return null;}
	}
	
	private class listenerRidimensionamento implements ComponentListener{
		public void componentHidden(ComponentEvent arg0) {}
		public void componentShown(ComponentEvent arg0) {}
		public void componentMoved(ComponentEvent arg0) {}
		public void componentResized(ComponentEvent arg0) {
			if (loginPane != null && loginPane.isVisible()) {
				loginPane.setBounds((int)((frame.getContentPane().getWidth()-loginPane.getWidth())/2), (int)((frame.getContentPane().getHeight()-loginPane.getHeight())/2), loginPane.getWidth(), loginPane.getHeight());
			} else if (bacheca != null && pannelloCentrale.isVisible() && bacheca.isVisible()) {
				pannelloCentrale.setPreferredSize(new Dimension(frame.getContentPane().getWidth(),frame.getContentPane().getHeight()-toolbarBacheca.getHeight()-barraFunzioni.getHeight()));
				bacheca.ridimensiona(frame.getContentPane().getWidth());
			} else if (form != null && pannelloCentrale.isVisible() && form.isVisible()) {
				pannelloCentrale.setPreferredSize(new Dimension(frame.getContentPane().getWidth(),frame.getContentPane().getHeight()-barraForm.getHeight()));
				form.ridimensiona(frame.getContentPane().getWidth());
			} else if (pannelloNotifiche != null && pannelloCentrale.isVisible() && pannelloNotifiche.isVisible()) {
				pannelloCentrale.setPreferredSize(new Dimension(frame.getContentPane().getWidth(),frame.getContentPane().getHeight()-barraFunzioni.getHeight()-btnProfilo.getHeight()));
				pannelloNotifiche.ridimensiona(frame.getContentPane().getWidth());
			} else if (schedaEvento != null && pannelloCentrale.isVisible() && schedaEvento.isVisible()) {
				pannelloCentrale.setPreferredSize(new Dimension(frame.getContentPane().getWidth(),frame.getContentPane().getHeight()-barraFunzioni.getHeight()));
				schedaEvento.ridimensiona(frame.getContentPane().getWidth());
			} else if (schedaUtente != null && pannelloCentrale.isVisible() && schedaUtente.isVisible()) {
				pannelloCentrale.setPreferredSize(new Dimension(frame.getContentPane().getWidth(),frame.getContentPane().getHeight()-barraFunzioni.getHeight()));
				schedaUtente.ridimensiona(frame.getContentPane().getWidth());
			} else if (invitatore != null && pannelloCentrale.isVisible() && invitatore.isVisible()) {
				pannelloCentrale.setPreferredSize(new Dimension(frame.getContentPane().getWidth(),frame.getContentPane().getHeight()));
				invitatore.ridimensiona(frame.getContentPane().getWidth());
			}
		}
	}
	
	void accedi(String utente, String password) 
	{
		try 
		{
			if (sessione.accedi(new Utente(utente, password))) 
			{
				frame.setMinimumSize(new Dimension(screenH/3, (int) (screenH/2.25)));
				visualizzaBacheca();
			}
		} 
		catch (IllegalArgumentException r) 
		{
			JOptionPane.showMessageDialog(null, r.getMessage(), "Errore", JOptionPane.ERROR_MESSAGE);
			loginPane.ripulisci();
		}
//		if (Sessione.accedi(new Utente(utente, password))) {
//			frame.setMinimumSize(new Dimension(screenH/3, (int) (screenH/2.25)));
//			visualizzaBacheca();
//		} else loginPane.ripulisci();
	}
	
	Utente chiediUtenteCorrente() {
		return sessione.getUtente_corrente();
	}
	
	void creaUtente(String utente, String password) 
	{
		try {
			if(sessione.insertUtente(new Utente(utente, password)))
					visualizzaProfilo(); 
		} 
		catch (IllegalArgumentException r) 
		{
			JOptionPane.showMessageDialog(null, r.getMessage(), "Errore", JOptionPane.ERROR_MESSAGE);
			loginPane.ripulisci();
		}
//		if (Sessione.insertUtente(new Utente(utente, password))) visualizzaProfilo();
//		else loginPane.ripulisci();
	}
	
	void aggiornaDatiUtente(Integer etm, Integer etM, String[] elencoCategorie, boolean[] selezionata) {
		if (elencoCategorie == null || selezionata == null || elencoCategorie.length != selezionata.length) return;
		for (int i=0; i<elencoCategorie.length; i++)
			if (selezionata[i]) sessione.aggiungiInteresseUtenteCorrente(CategoriaEvento.convertiStringInCategoria(elencoCategorie[i]));
			else sessione.eliminaInteresseUtenteCorrente(CategoriaEvento.convertiStringInCategoria(elencoCategorie[i]));
		sessione.updateFasciaEta(etm, etM);
	}
	
	void aggiungiEvento(Evento e) {
		if (sessione.aggiungiEvento(e) != null) {
			try {barraForm.remove(btnConfermaCreazioneEvento);} catch (Exception ex) {}
			btnConfermaCreazioneEvento = new JButton(" ✔ Conferma");
			btnConfermaCreazioneEvento.setFont(fontTestoBottoni);
			btnConfermaCreazioneEvento.setBackground(coloreBottoni);
			barraForm.add(btnConfermaCreazioneEvento, BorderLayout.CENTER);
			visualizzaFormInviti(e, e.getNomeCategoria());
		}	
		else JOptionPane.showMessageDialog(null, "Impossibile creare l'evento", "Errore DB", JOptionPane.ERROR_MESSAGE);
	}
	void eliminaEvento (Evento e) {
		try {
			sessione.deleteEvento(e);
			visualizzaBacheca(); 
		} catch (RuntimeException r) {
			JOptionPane.showMessageDialog(null, r.getMessage(), "Errore", JOptionPane.ERROR_MESSAGE);
		}
	}
	void iscriviEvento(Evento e) {
		sessione.iscrizioneUtenteInEvento(e);
		visualizzaBacheca();
	}
	void rimuoviIscrizioneEvento(Evento e) {
		try {sessione.disiscrizioneUtenteEvento(e);
			visualizzaBacheca();
		} catch (RuntimeException exc) {JOptionPane.showMessageDialog(null, exc.getMessage(), "Impossibile disiscriversi", JOptionPane.ERROR_MESSAGE); return;}
	}
	void eliminaNotifica(Notifica n) {
		frame.getContentPane().remove(pannelloCentrale);
		pannelloNotifiche =new PannelloNotifiche(sessione.eliminaNotificaUtente(n), frame.getContentPane().getWidth(), fontTesto, fontTestoBottoni, altezzaStringhe);
		pannelloCentrale = new JScrollPane(pannelloNotifiche);
		pannelloCentrale.getVerticalScrollBar().setUnitIncrement(screenH/250);
		pannelloCentrale.getVerticalScrollBar().setUI(new BellaScrlb());
		pannelloCentrale.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		pannelloCentrale.setPreferredSize(new Dimension(frame.getContentPane().getWidth(),frame.getContentPane().getHeight()-barraFunzioni.getHeight()));
		frame.getContentPane().add(pannelloCentrale, BorderLayout.CENTER);
		frame.getContentPane().revalidate();
	}
	void invitaUtenteAdEvento (Evento e, Utente invitato) {
		sessione.notificaUtentePerEvento(e, invitato);
	}
	HashMap<NomeCampo,Boolean> sceltePersonali (){
		HashMap<NomeCampo,Boolean> scelte = new HashMap<>();
		scelte.put(NomeCampo.BIGLIETTO_BUS, 
				JOptionPane.showOptionDialog(null, "Vuoi acquistare il trasporto?", "Scelte personali", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, null) == 0);
		scelte.put(NomeCampo.PRANZO, 
				JOptionPane.showOptionDialog(null, "Vuoi acquistare il pranzo?", "Scelte personali", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, null) == 0);
		scelte.put(NomeCampo.AFFITTO_SCII, 
				JOptionPane.showOptionDialog(null, "Vuoi noleggiare le attrezzature?", "Scelte personali", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, null) == 0);
		return scelte;
	}
	
	public void update(Observable o, Object arg) {
		CategoriaEvento catSelezionata = selettoreCategoria.getSelectedIndex() == 0 ? CategoriaEvento.PARTITA_CALCIO : CategoriaEvento.SCII;
		@SuppressWarnings("unchecked") ArrayList<Evento> listaEventi = ((HashMap<CategoriaEvento,ArrayList<Evento>>) arg).get(catSelezionata);
		if (listaEventi == null) listaEventi = new ArrayList<>();
		if (bacheca != null && bacheca.isVisible()) {
			bacheca = new Bacheca(listaEventi, frame.getContentPane().getWidth(), fontTesto, fontTestoBottoni, altezzaStringhe);
			try {frame.getContentPane().remove(pannelloCentrale);} catch (Exception e) {}
			pannelloCentrale = new JScrollPane(bacheca);
			pannelloCentrale.getVerticalScrollBar().setUnitIncrement(screenH/250);
			pannelloCentrale.getVerticalScrollBar().setUI(new BellaScrlb());
			pannelloCentrale.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
			pannelloCentrale.setPreferredSize(new Dimension(frame.getContentPane().getWidth(),frame.getContentPane().getHeight()-toolbarBacheca.getHeight()-barraFunzioni.getHeight()));
			frame.getContentPane().add(pannelloCentrale, BorderLayout.CENTER);
			barraFunzioni.repaint(200);
			frame.getContentPane().revalidate();
			toolbarBacheca.repaint(200);
		}
	}
	
	private void iniziaOsservazione() {
		sessione.iniziaOsservazione(this);
	}
	
	private void fermaOsservazione() {
		sessione.fermaOsservazione(this);
	}
}

final class BellaScrlb extends BasicScrollBarUI {
	protected void configureScrollBarColors() {
		thumbColor = Grafica.coloreBarra;
		thumbDarkShadowColor = Grafica.coloreBarra;
		thumbHighlightColor = Grafica.coloreBarra;
		thumbLightShadowColor = Grafica.coloreBarra;
		trackColor = Grafica.coloreSfondo;
		trackHighlightColor = Grafica.coloreBarra;
	}
	protected JButton createDecreaseButton(int orientation) {
		JButton button = new BasicArrowButton(orientation);
	    button.setBackground(Grafica.coloreBottoni);
	    button.setForeground(Grafica.coloreBarra);
	    return button;
	 }
	protected JButton createIncreaseButton(int orientation) {
		 JButton button = new BasicArrowButton(orientation);
		 button.setBackground(Grafica.coloreBottoni);
		 button.setForeground(Grafica.coloreBarra);
		 return button;
	}
}