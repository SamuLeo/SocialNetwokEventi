package socialNetwork;

import java.awt.*;
import java.awt.event.AdjustmentListener;
import java.awt.font.FontRenderContext;
import javax.swing.*;

public class Grafica {
	private static Grafica me;
	private Grafica() {}
	public static Grafica getIstance() {
		if (me==null) {me = new Grafica(); return me; }
		else return me;
	}
	
	JFrame frame;
	JButton btnNuovoEvento = new JButton("Aggiungi Evento"), btnBacheca = new JButton ("Bacheca");
	JPanel toolbarBacheca = new JPanel(), barraFunzioni = new JPanel();
	Login loginPane;
	
	AdjustmentListener listenerBacheca = null;
	
	static final Color coloreBottoni = new Color(255,255,255);
	static final Color coloreSfondo = new Color(240,240,240);
	static final Color coloreBarra = new Color(200,200,200);
	Dimension screenSize=Toolkit.getDefaultToolkit().getScreenSize();
	int screenW = (int)(screenSize.getWidth());
	int screenH = (int)(screenSize.getHeight());
	static final Font testoBottoni=new Font("Segoe UI", Font.PLAIN, Toolkit.getDefaultToolkit().getScreenResolution()/5);
	static final Font testo=new Font("Segoe UI", Font.PLAIN, Toolkit.getDefaultToolkit().getScreenResolution()/6);
	
	public void crea() {
		//Operazioni iniziali sul Frame
			frame = new JFrame();
			frame.setMinimumSize(new Dimension(screenH/3, (int) (screenH/2.25)));
			frame.setTitle("Login");
			frame.setIconImage(new ImageIcon("Icona.jpg").getImage());
			frame.setBounds(screenW/2-(int)(screenH/4.4), screenH/2-(int)(screenH/4.4), (int)(screenH/2.2), (int)(screenH/1.8));
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		//Inizializzazione componenti
			btnNuovoEvento.setFont(testoBottoni);
			btnNuovoEvento.setBackground(coloreBottoni);
			btnBacheca.setBackground(coloreBottoni);
			btnBacheca.setFont(testoBottoni);
			//btnBacheca.setBorderPainted(false);
			toolbarBacheca.setLayout(new BorderLayout(0, 0));
			toolbarBacheca.setBackground(coloreSfondo);
			toolbarBacheca.add(btnNuovoEvento, BorderLayout.EAST);
			barraFunzioni.setLayout(new BorderLayout(0, 0));
			barraFunzioni.add(btnBacheca, BorderLayout.CENTER);
		//Creazione schermata di login
			frame.setLayout(null);
			frame.setResizable(false);
			frame.setVisible(true);
			FontRenderContext frc = ((Graphics2D)frame.getGraphics()).getFontRenderContext();
			loginPane=new Login(testoBottoni, testo, (int)testo.getStringBounds("Password: ", frc).getWidth(), (int)testo.getStringBounds("abj", frc).getHeight(), (int)testo.getStringBounds("abcdefghijklmnopqrst", frc).getWidth());
			loginPane.setBounds((int)((screenH/2.2-loginPane.getWidth())/2), (int)((screenH/1.8-loginPane.getHeight())/2), loginPane.getWidth(), loginPane.getHeight());
			frame.getContentPane().setBackground(coloreBottoni);
			frame.getContentPane().add(loginPane);
			loginPane.focus();
			//JOptionPane.showMessageDialog(null, loginPane.getX() + " " + loginPane.getY(), "Partecipanti", JOptionPane.INFORMATION_MESSAGE);
			UIManager.put("OptionPane.messageFont", testo);
			UIManager.put("OptionPane.buttonFont", testoBottoni);
	}
	
	public void visualizzaBacheca() {
		frame.setTitle("Bacheca");
		frame.setResizable(true);
		loginPane.setVisible(false);
		frame.getContentPane().setLayout(new BorderLayout(0, 0));
		frame.getContentPane().add(toolbarBacheca, BorderLayout.NORTH);
		frame.getContentPane().add(barraFunzioni, BorderLayout.SOUTH);
		JScrollPane pannelloBacheca = new JScrollPane(new Bacheca(frame.getWidth()-45));
		pannelloBacheca.getVerticalScrollBar().addAdjustmentListener(listenerBacheca);
		pannelloBacheca.setPreferredSize(new Dimension(frame.getWidth(),frame.getHeight()-toolbarBacheca.getHeight()-barraFunzioni.getHeight()));
		frame.getContentPane().add(pannelloBacheca, BorderLayout.CENTER);
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