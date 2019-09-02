package it.unibs.dii.isw.socialNetworkEventi.view;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionListener;
import java.util.LinkedList;

import javax.swing.*;

import it.unibs.dii.isw.socialNetworkEventi.model.Evento;
import it.unibs.dii.isw.socialNetworkEventi.model.Utente;

public class SceltaInviti extends JPanel 
{
	private Grafica grafica;
	private static final long serialVersionUID = 1L;
	private int X, Y;
	private Dimension screenSize=Toolkit.getDefaultToolkit().getScreenSize();
	private int screenW = (int)(screenSize.getWidth());
	private int screenH = (int)(screenSize.getHeight());
	private JCheckBox[] utenti;
	private LinkedList<Utente> invitabili;
	private JLabel intestazione;
	private JButton chiudi;
	private Evento evento;

	SceltaInviti(Grafica grafica, Evento e, LinkedList<Utente> invitabili, Font font, int larghezza, int altezzaStr) {
		super();
		this.grafica = grafica;
		evento = e;
		this.invitabili=invitabili;
		X = larghezza;
		setLayout(null);
		setBackground(Grafica.coloreSfondo);
		
		intestazione = new JLabel("Manda un invito per notifica a:");
		intestazione.setFont(font);
		intestazione.setBounds(20, 20, X-40, altezzaStr);
		add(intestazione);
		Y = 30 + altezzaStr;
		if (invitabili != null) {
			utenti = new JCheckBox[invitabili.size()];
			for (int i=0; i<utenti.length; i++) {
				utenti[i] = new JCheckBox();
				utenti[i].setFont(font);
				utenti[i].setBackground(Grafica.coloreSfondo);
				utenti[i].setBounds(20, Y, X-40, altezzaStr);
				Y += 30 + altezzaStr;
				add(utenti[i]);
				utenti[i].setText(invitabili.get(i).getNome());
			}
		}
		chiudi = new JButton("Conferma ✔");
		chiudi.setBackground(Grafica.coloreBottoni);
		chiudi.setFont(font);
		chiudi.setBounds(20, Y, X-40, (int)(altezzaStr * 1.1));
		Y+= 30 + altezzaStr * 1.1;
		chiudi.addActionListener(conferma);
		add(chiudi);
		
		setPreferredSize(new Dimension(X,Y));
		setBounds(screenW/2-X/2, screenH/2-Y/2, X, Y);
	}
	
	void ridimensiona(int larghezza) {
		X=larghezza;
		for (Component c: getComponents())
			c.setSize(X-40, c.getHeight());
		setPreferredSize(new Dimension(X,Y));
	}
	
	ActionListener conferma = click -> {
		if (utenti != null) {
			for (int i=0; i<utenti.length; i++) {
				if (utenti[i].isSelected()) 
					grafica.invitaUtenteAdEvento(evento, invitabili.get(i));
			}
		}
		grafica.visualizzaBacheca();
	};
}
