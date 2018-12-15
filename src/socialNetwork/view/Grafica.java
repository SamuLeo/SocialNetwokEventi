package socialNetwork.view;
import socialNetwork.controller.*;

import java.awt.*;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.font.FontRenderContext;
import javax.swing.*;

public class Grafica {
	private static Grafica me;
	private Grafica() {}
	public static Grafica getIstance() {
		if (me==null) {me = new Grafica(); return me;}
		else return me;
	}
	
	JFrame frame;
	JButton btnNuovoEvento = new JButton("Aggiungi Evento"), btnBacheca = new JButton ("Bacheca"), btnConfermaCreazioneEvento = new JButton("Conferma"), btnAnnullaCreazioneEvento = new JButton("Annulla");
	JPanel toolbarBacheca = new JPanel(), barraFunzioni = new JPanel(), barraForm = new JPanel();
	Login loginPane;
	JScrollPane pannelloCentrale = new JScrollPane();
	Bacheca bacheca;
	CreazioneEvento form;
	JComboBox<String> selettoreCategoria = new JComboBox<>();
	
	private AdjustmentListener listenerScroll = new listenerScroll();
	private ComponentListener listenerRidimensionamento = new listenerRidimensionamento();
	
	Dimension screenSize=Toolkit.getDefaultToolkit().getScreenSize();
	int screenW = (int)(screenSize.getWidth());
	int screenH = (int)(screenSize.getHeight());
	private int larghezzaStrPassword, altezzaStringhe, larghezzaCampiUtentePswd;
	static final Font testoBottoni=new Font("Segoe UI", Font.PLAIN, Toolkit.getDefaultToolkit().getScreenResolution()/5);
	static final Font testo=new Font("Segoe UI", Font.PLAIN, Toolkit.getDefaultToolkit().getScreenResolution()/6);
	static final Color coloreBottoni = new Color(255,255,255);
	static final Color coloreSfondo = new Color(240,240,240);
	static final Color coloreBarra = new Color(200,200,200);
	
	public void crea() {
		//Operazioni iniziali sul Frame e sulle variabili di classe
			frame = new JFrame();
			frame.setMinimumSize(new Dimension(screenH/3, (int) (screenH/2.25)));
			frame.setIconImage(new ImageIcon("Icona.jpg").getImage());
			frame.setBounds(screenW/2-(int)(screenH/4.4), screenH/2-(int)(screenH/4.4), (int)(screenH/2.2), (int)(screenH/1.8));
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.setVisible(true);
			frame.getContentPane().setBackground(coloreBottoni);
			frame.addComponentListener(listenerRidimensionamento);
			calcolaDimensioniStringhe();
			UIManager.put("OptionPane.messageFont", testo);
			UIManager.put("OptionPane.buttonFont", testoBottoni);
		//Inizializzazione componenti
			btnNuovoEvento.setFont(testoBottoni);
			btnNuovoEvento.setBackground(coloreBottoni);
			btnNuovoEvento.addActionListener(e -> iniziaCreazioneEvento());
			selettoreCategoria.setFont(testo);
			selettoreCategoria.insertItemAt("Calcio", 0);
			selettoreCategoria.setSelectedIndex(0);
			selettoreCategoria.setBackground(coloreBottoni);
			selettoreCategoria.setFocusable(false);
			btnBacheca.setBackground(coloreBottoni);
			btnBacheca.setFont(testoBottoni);
			btnConfermaCreazioneEvento.setFont(testoBottoni);
			btnConfermaCreazioneEvento.setBackground(coloreBottoni);
			btnAnnullaCreazioneEvento.setFont(testoBottoni);
			btnAnnullaCreazioneEvento.setBackground(coloreBottoni);
			btnAnnullaCreazioneEvento.addActionListener(e -> visualizzaBacheca());
			toolbarBacheca.setLayout(new BorderLayout(0, 0));
			toolbarBacheca.setBackground(coloreSfondo);
			toolbarBacheca.add(btnNuovoEvento, BorderLayout.EAST);
			toolbarBacheca.add(selettoreCategoria,BorderLayout.CENTER);
			barraFunzioni.setLayout(new BorderLayout(0, 0));
			barraFunzioni.add(btnBacheca, BorderLayout.CENTER);
			barraForm.setLayout(new BorderLayout(0, 0));
			barraForm.add(btnConfermaCreazioneEvento, BorderLayout.CENTER);
			barraForm.add(btnAnnullaCreazioneEvento, BorderLayout.EAST);
			pannelloCentrale.getVerticalScrollBar().addAdjustmentListener(listenerScroll);
			pannelloCentrale.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
			pannelloCentrale.getVerticalScrollBar().setUnitIncrement(screenH/400);
			//JOptionPane.showMessageDialog(null, loginPane.getX() + " " + loginPane.getY(), "Partecipanti", JOptionPane.INFORMATION_MESSAGE);
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
		try {frame.getContentPane().remove(pannelloCentrale);} catch (Exception e) {}
		frame.getContentPane().revalidate();
	}
	
	public void mostraLogin() {
		frame.setTitle("Login");
		frame.setLayout(null);
		loginPane=new Login(testoBottoni, testo, larghezzaStrPassword , altezzaStringhe, larghezzaCampiUtentePswd);
		loginPane.setBounds((int)((frame.getContentPane().getWidth()-loginPane.getWidth())/2), (int)((frame.getContentPane().getHeight()-loginPane.getHeight())/2), loginPane.getWidth(), loginPane.getHeight());
		frame.getContentPane().add(loginPane);
		loginPane.focus();
		frame.repaint();
	}
	
	public void visualizzaBacheca() {
		/*
		LinkedList<Evento> le = Sessione.mostraBacheca(); 
		*/
		//Riconfigurazione del Frame
		frame.setTitle("Bacheca");
		svuotaFrame();
		frame.getContentPane().add(toolbarBacheca, BorderLayout.NORTH);
		frame.getContentPane().add(barraFunzioni, BorderLayout.SOUTH);
		if (form != null) form.setVisible(false);
		if (bacheca != null) bacheca.setVisible(true);
		//Creazione pannello principale
		bacheca = new Bacheca(frame.getWidth(),testo, testoBottoni, altezzaStringhe);
		pannelloCentrale = new JScrollPane(bacheca);
		pannelloCentrale.getVerticalScrollBar().setUnitIncrement(screenH/400);
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
		svuotaFrame();
		frame.getContentPane().add(barraForm, BorderLayout.SOUTH);
		if (form != null) form.setVisible(true);
		if (bacheca != null) bacheca.setVisible(false);
		//Creazione pannello principale
		form = new CreazioneEvento(testo, frame.getWidth(), altezzaStringhe);
		pannelloCentrale = new JScrollPane(form);
		pannelloCentrale.getVerticalScrollBar().setUnitIncrement(screenH/400);
		pannelloCentrale.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		pannelloCentrale.setPreferredSize(new Dimension(frame.getContentPane().getWidth(),frame.getContentPane().getHeight()-barraForm.getHeight()));
		frame.getContentPane().add(pannelloCentrale, BorderLayout.CENTER);
		frame.getContentPane().revalidate();
		barraForm.repaint(200);
	}
	
	private void calcolaDimensioniStringhe() {
		FontRenderContext frc = ((Graphics2D)frame.getGraphics()).getFontRenderContext();
		larghezzaStrPassword = (int)testo.getStringBounds("Password: ", frc).getWidth();
		altezzaStringhe = (int)testo.getStringBounds("abj", frc).getHeight();
		larghezzaCampiUtentePswd = (int)testo.getStringBounds("abcdefghijklmnopqrst", frc).getWidth();
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
				bacheca.ridimensiona(frame.getWidth());
			} else if (form != null && pannelloCentrale.isVisible() && form.isVisible()) {
				pannelloCentrale.setPreferredSize(new Dimension(frame.getContentPane().getWidth(),frame.getContentPane().getHeight()-barraForm.getHeight()));
				form.ridimensiona(frame.getWidth());
				form.repaint(200);
			}
		}
	}
	
	class listenerScroll implements AdjustmentListener{
		public void adjustmentValueChanged(AdjustmentEvent e) {
			//Listener attivato quando faccio scroll sulla bacheca
		}
	}
	
	public void accedi(String utente, String password) {
		if (Sessione.accedi(utente, password)) visualizzaBacheca();
		else loginPane.ripulisci();
	}
	public void creaUtente(String utente, String password) {
		if (Sessione.creaUtente(utente, password)) visualizzaBacheca();
		else loginPane.ripulisci();
	}
}