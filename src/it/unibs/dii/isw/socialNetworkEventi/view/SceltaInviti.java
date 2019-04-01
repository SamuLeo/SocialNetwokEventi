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

public class SceltaInviti extends JPanel {
	private static final long serialVersionUID = 1L;
	int X, Y, nRighe=1;
	Dimension screenSize=Toolkit.getDefaultToolkit().getScreenSize();
	int screenW = (int)(screenSize.getWidth());
	int screenH = (int)(screenSize.getHeight());
	JCheckBox[] utenti;
	LinkedList<Utente> invitabili;
	JLabel intestazione;
	JButton chiudi;
	Font font;
	Evento evento;

	public SceltaInviti(Evento e, LinkedList<Utente> invitabili, Font font, int larghezza, int altezzaStr) {
		super();
		System.out.println("CI ENTRO");
		evento = e;
		this.invitabili=invitabili;
		X = larghezza;
		this.font=font;
		setLayout(null);
		setBackground(Grafica.coloreSfondo);
		
		intestazione = new JLabel("Manda un invito per notifica a:");
		intestazione.setFont(font);
		intestazione.setBounds(20, 20, X-40, altezzaStr);
		add(intestazione);
		Y = 30 + altezzaStr;
		System.out.println("CI ARRIVO");
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
		System.out.println("CI PASSO");
		//invita=new JButton ("Invita un fruitore specifico"); 
		//invita.setFont(font); 
		//invita.setBackground(Grafica.coloreBottoni); 
		//invita.setBounds(20, Y, X-40, (int)(altezzaStr * 1.1));
		//Y+= 30 + altezzaStr * 1.1;
		//add(invita);
		chiudi = new JButton("Conferma ✔");
		chiudi.setBackground(Grafica.coloreBottoni);
		chiudi.setFont(font);
		chiudi.setBounds(20, Y, X-40, (int)(altezzaStr * 1.1));
		Y+= 30 + altezzaStr * 1.1;
		chiudi.addActionListener(conferma);
		add(chiudi);
		
		setPreferredSize(new Dimension(X,Y));
		setBounds(screenW/2-X/2, screenH/2-Y/2, X, Y);
		System.out.println("CI ESCO");
	}
	
	void ridimensiona(int larghezza) {
		X=larghezza;
		for (Component c: getComponents())
			c.setSize(X-40, c.getHeight());
		/*int altezzaStringhe = partecipatoInPassato.getHeight()/nRighe;
		int altezzaprima = partecipatoInPassato.getHeight();
		nRighe=((int)font.getStringBounds(partecipatoInPassato.getText(), ((Graphics2D)getGraphics()).getFontRenderContext()).getWidth() / (X - 40) + 1);
		int delta = nRighe*altezzaStringhe - altezzaprima;
		Y+=delta;
		intestazione.setSize(X - 40, intestazione.getHeight());
		partecipatoInPassato.setSize(X - 40, nRighe*altezzaStringhe);
		invita.setBounds(20, invita.getY()+delta, X-40, invita.getHeight());
		chiudi.setBounds(20, chiudi.getY()+delta, X-40, chiudi.getHeight());*/
		setPreferredSize(new Dimension(X,Y));
	}
	
	ActionListener conferma = click -> {
		if (utenti != null) {
			for (int i=0; i<utenti.length; i++) {
				if (utenti[i].isSelected()) 
					Grafica.getIstance().invitaUtenteAdEvento(evento, invitabili.get(i));
			}
		}
		Grafica.getIstance().visualizzaBacheca();
	};
}
