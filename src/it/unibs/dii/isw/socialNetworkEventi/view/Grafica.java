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

import it.unibs.dii.isw.socialNetworkEventi.controller.IController;
import it.unibs.dii.isw.socialNetworkEventi.model.Evento;
import it.unibs.dii.isw.socialNetworkEventi.model.Notifica;
import it.unibs.dii.isw.socialNetworkEventi.model.Utente;
import it.unibs.dii.isw.socialNetworkEventi.utility.*;

public class Grafica implements IView, Observer
{
	private IController sessione;
	private JFrame frame;
	static ImageIcon icona = ottieniIcona();
	static URL percorsoIcona;
	private MsgBox msgbox = new MsgBox();
	private ComponentListener listenerRidimensionamento = new listenerRidimensionamento();
	
	private Dimension screenSize=Toolkit.getDefaultToolkit().getScreenSize();
	private int screenW = (int)(screenSize.getWidth());
	private int screenH = (int)(screenSize.getHeight());
	private int larghezzaStrPassword, altezzaStringhe, larghezzaCampiUtentePswd;
	
	private static final Font fontTestoBottoni=new Font("Default", Font.PLAIN, Toolkit.getDefaultToolkit().getScreenResolution()/5);
	private static final Font fontTesto=new Font("Default", Font.PLAIN, Toolkit.getDefaultToolkit().getScreenResolution()/6);
	static final Color coloreBottoni = Color.white;
	static final Color coloreSfondo = new Color(244,244,244);
	static final Color coloreBarra = new Color(210,210,210);
	
	JButton btnNuovoEvento = new JButton(" ➕ Aggiungi Evento"), 
			btnBacheca = new JButton ("Bacheca 🏠"),
			btnConfermaCreazioneEvento = new JButton(" ✔ Conferma"), 
			btnAnnullaCreazioneEvento = new JButton(" ✖ Annulla"), 
			btnNotifiche = new JButton(" 💬  Notifiche"), 
			btnProfilo = new JButton(" 👤  Visualizza Profilo");
	JPanel toolbarBacheca = new JPanel(), 
			barraFunzioni = new JPanel(), 
			barraForm = new JPanel();
	private Login loginPane;
	JScrollPane pannelloCentrale = new JScrollPane();
	private Bacheca bacheca;
	CreazioneEvento form;
	private PannelloNotifiche pannelloNotifiche;
	private SchedaEvento schedaEvento;
	private PannelloUtente schedaUtente;
	private SceltaInviti invitatore;
	private JComboBox<String> selettoreCategoria = new JComboBox<>();
	
	public Grafica(IController sessione) 
	{
		this.sessione = sessione;
		crea();
		mostraLogin();
	}

	private void crea() {
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
			UIManager.put("Button.background", coloreBottoni);
			UIManager.put("Button.select", new Color(240,255,245));
			UIManager.put("TextField.font", fontTesto);
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
		if (form != null) form.setVisible(false);
		if (schedaEvento != null) schedaEvento.setVisible(false);
		if (pannelloNotifiche != null) pannelloNotifiche.setVisible(false);
		if (bacheca != null) bacheca.setVisible(false);
		if (schedaUtente != null) schedaUtente.setVisible(false);
	}
	
	public void mostraLogin() {
		frame.setTitle("Login");
		frame.setLayout(null);
		loginPane=new Login(this,fontTestoBottoni, fontTesto, larghezzaStrPassword , altezzaStringhe, larghezzaCampiUtentePswd);
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
		if (bacheca != null) bacheca.setVisible(true);
		//Creazione pannello principale
		//Se la Bacheca è già in mostra, va solo aggiornata
		if (bacheca != null && bacheca.isVisible()) sessione.aggiorna();
		CategoriaEvento catSelezionata = selettoreCategoria.getSelectedIndex() == 0 ? CategoriaEvento.PARTITA_CALCIO : CategoriaEvento.SCII;
		ArrayList<Evento> listaEventi = sessione.getEventi().get(catSelezionata);
		if (listaEventi == null) listaEventi = new ArrayList<>();
		bacheca = new Bacheca(this,listaEventi, frame.getContentPane().getWidth(),fontTesto, fontTestoBottoni, altezzaStringhe);
		pannelloCentrale = new JScrollPane(bacheca);
		pannelloCentrale.getVerticalScrollBar().setUnitIncrement(screenH/250);
		pannelloCentrale.getVerticalScrollBar().setUI(new BellaScrlb());
		pannelloCentrale.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		pannelloCentrale.setPreferredSize(new Dimension(frame.getContentPane().getWidth(),frame.getContentPane().getHeight()-toolbarBacheca.getHeight()-barraFunzioni.getHeight()));
		frame.getContentPane().add(pannelloCentrale, BorderLayout.CENTER);
		barraFunzioni.repaint(200);
		frame.getContentPane().revalidate();
		toolbarBacheca.repaint(200);
		iniziaOsservazione();
	}
	
	public void iniziaCreazioneEvento() {
		//Riconfigurazione del Frame
		frame.setTitle("Crea evento");
		fermaOsservazione();
		svuotaFrame();
		frame.getContentPane().add(barraForm, BorderLayout.SOUTH);
		if (form != null) form.setVisible(true);
		//Creazione pannello principale
		form = new CreazioneEvento(this,fontTesto, frame.getContentPane().getWidth(), altezzaStringhe, coloreSfondo);
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
		if (pannelloNotifiche != null) pannelloNotifiche.setVisible(false);
		//Creazione pannello principale
		pannelloNotifiche=new PannelloNotifiche(this, sessione.getNotificheUtente(), frame.getContentPane().getWidth(), fontTesto, fontTestoBottoni, altezzaStringhe);
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
		frame.setTitle((String)e.getCampo(NomeCampo.TITOLO).getContenuto());
		fermaOsservazione();
		svuotaFrame();
		frame.getContentPane().add(barraFunzioni, BorderLayout.SOUTH);
		btnBacheca.setText("Bacheca 🏠");
		if (schedaEvento != null) schedaEvento.setVisible(true);
		//Creazione pannello principale
		schedaEvento=new SchedaEvento(this, e, fontTesto, fontTestoBottoni, altezzaStringhe, frame.getContentPane().getWidth());
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
		if (schedaUtente != null) schedaUtente.setVisible(true);
		//Creazione pannello principale
		schedaUtente = new PannelloUtente(this, chiediUtenteCorrente(), fontTesto, fontTestoBottoni, altezzaStringhe, frame.getContentPane().getWidth());
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
		//Creazione pannello principale
		invitatore = new SceltaInviti(this, e, invitabili, fontTesto, frame.getContentPane().getWidth(), altezzaStringhe);
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
			int larghezza = frame.getContentPane().getWidth();
			if (loginPane != null && loginPane.isVisible()) loginPane.setBounds((int)((larghezza-loginPane.getWidth())/2), (int)((frame.getContentPane().getHeight()-loginPane.getHeight())/2), loginPane.getWidth(), loginPane.getHeight());
			else if (pannelloCentrale.isVisible()) {
				if (bacheca != null && bacheca.isVisible()) bacheca.ridimensiona(larghezza);
				else if (form != null && form.isVisible()) form.ridimensiona(larghezza);
				else if (pannelloNotifiche != null && pannelloNotifiche.isVisible()) pannelloNotifiche.ridimensiona(larghezza);
				else if (schedaEvento != null && schedaEvento.isVisible()) schedaEvento.ridimensiona(larghezza);
				else if (schedaUtente != null && schedaUtente.isVisible()) schedaUtente.ridimensiona(larghezza);
				else if (invitatore != null && invitatore.isVisible())  invitatore.ridimensiona(larghezza);
			}
		}
	}
	
	public void accedi(String utente, String password) 
	{
		try 
		{
			if (sessione.accedi(new Utente(utente, password))) 
			{
				frame.setMinimumSize(new Dimension(screenH/3, (int) (screenH/2.25)));
				visualizzaBacheca();
			}
			else 
			{
				loginPane.ripulisci();
				msgbox.messaggioAvviso("Errore accesso", "L'utente è inesistente oppure la password è errata");
			}
		} 
		catch (IllegalArgumentException r) 
		{
			msgbox.messaggioErrore("Errore accesso", r.getMessage());
			loginPane.ripulisci();
		}
	}
	
	Utente chiediUtenteCorrente() {
		return sessione.getUtente_corrente();
	}
	
	public void creaUtente(String utente, String password)
	{
		try {
			if(sessione.insertUtente(new Utente(utente, password)))
				visualizzaProfilo();
			else loginPane.ripulisci();
		} 
		catch (IllegalArgumentException r) 
		{
			msgbox.messaggioErrore("Errore creazione utente", r.getMessage());
			loginPane.ripulisci();
		}
	}
	
	public void aggiornaDatiUtente(Integer etm, Integer etM, String[] elencoCategorie, boolean[] selezionata) {
		if (elencoCategorie == null || selezionata == null || elencoCategorie.length != selezionata.length) return;
		for (int i=0; i<elencoCategorie.length; i++)
			if (selezionata[i]) sessione.aggiungiInteresseUtenteCorrente(CategoriaEvento.convertiStringInCategoria(elencoCategorie[i]));
			else sessione.eliminaInteresseUtenteCorrente(CategoriaEvento.convertiStringInCategoria(elencoCategorie[i]));
		sessione.updateFasciaEta(etm, etM);
	}
	
	public void aggiungiEvento(Evento e) {
		if (sessione.aggiungiEvento(e) != null) {
			try {barraForm.remove(btnConfermaCreazioneEvento);} catch (Exception ex) {}
			btnConfermaCreazioneEvento = new JButton(" ✔ Conferma");
			btnConfermaCreazioneEvento.setFont(fontTestoBottoni);
			btnConfermaCreazioneEvento.setBackground(coloreBottoni);
			barraForm.add(btnConfermaCreazioneEvento, BorderLayout.CENTER);
			visualizzaFormInviti(e, e.getNomeCategoria());
		}	
		else msgbox.messaggioErrore("Errore DB", "Impossibile creare l'evento");
	}
	public void eliminaEvento (Evento e) {
		try {
			sessione.deleteEvento(e);
			visualizzaBacheca(); 
		} catch (RuntimeException r) {
			msgbox.messaggioErrore("Errore DB", r.getMessage());
		}
	}
	public void iscriviEvento(Evento e) {
		sessione.iscrizioneUtenteInEvento(e);
		visualizzaBacheca();
	}
	public void rimuoviIscrizioneEvento(Evento e) {
		try {sessione.disiscrizioneUtenteEvento(e);
			visualizzaBacheca();
		} catch (RuntimeException exc) {
			msgbox.messaggioErrore("Impossibile disiscriversi", exc.getMessage());
			return;
		}
	}
	public void eliminaNotifica(Notifica n) {
		frame.getContentPane().remove(pannelloCentrale);
		pannelloNotifiche =new PannelloNotifiche(this, sessione.eliminaNotificaUtente(n), frame.getContentPane().getWidth(), fontTesto, fontTestoBottoni, altezzaStringhe);
		pannelloCentrale = new JScrollPane(pannelloNotifiche);
		pannelloCentrale.getVerticalScrollBar().setUnitIncrement(screenH/250);
		pannelloCentrale.getVerticalScrollBar().setUI(new BellaScrlb());
		pannelloCentrale.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		pannelloCentrale.setPreferredSize(new Dimension(frame.getContentPane().getWidth(),frame.getContentPane().getHeight()-barraFunzioni.getHeight()));
		frame.getContentPane().add(pannelloCentrale, BorderLayout.CENTER);
		frame.getContentPane().revalidate();
	}
	
	public void invitaUtenteAdEvento (Evento e, Utente invitato) {
		sessione.notificaUtentePerEvento(e, invitato);
	}
	
	HashMap<NomeCampo,Boolean> sceltePersonali (){
		HashMap<NomeCampo,Boolean> scelte = new HashMap<>();
		scelte.put(NomeCampo.BIGLIETTO_BUS, msgbox.sceltaSemplice("Scelte personali", "Vuoi acquistare il trasporto?"));
		scelte.put(NomeCampo.PRANZO, msgbox.sceltaSemplice("Scelte personali", "Vuoi acquistare il pranzo?"));
		scelte.put(NomeCampo.AFFITTO_SCII, msgbox.sceltaSemplice("Scelte personali", "Vuoi noleggiare le attrezzature?"));
		return scelte;
	}
	
	public void update(Observable o, Object arg) {
		CategoriaEvento catSelezionata = selettoreCategoria.getSelectedIndex() == 0 ? CategoriaEvento.PARTITA_CALCIO : CategoriaEvento.SCII;
		@SuppressWarnings("unchecked") ArrayList<Evento> listaEventi = ((HashMap<CategoriaEvento,ArrayList<Evento>>) arg).get(catSelezionata);
		if (listaEventi == null) listaEventi = new ArrayList<>();
		if (bacheca != null && bacheca.isVisible()) {
			int scroll = pannelloCentrale.getVerticalScrollBar().getValue();
			bacheca = new Bacheca(this, listaEventi, frame.getContentPane().getWidth(), fontTesto, fontTestoBottoni, altezzaStringhe);
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
			pannelloCentrale.getVerticalScrollBar().setValue(scroll);
			pannelloCentrale.getVerticalScrollBar().setValue(scroll);
			//Non ho idea del perché, ma chiamare quel metodo solo una volta porta a risultati sbagliati. Provare per credere!
		}
	}
	
	private void iniziaOsservazione() {sessione.iniziaOsservazione(this);}
	private void fermaOsservazione() {sessione.fermaOsservazione(this);}
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