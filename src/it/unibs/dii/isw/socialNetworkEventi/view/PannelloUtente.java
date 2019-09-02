package it.unibs.dii.isw.socialNetworkEventi.view;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.util.LinkedList;

import javax.swing.*;

import it.unibs.dii.isw.socialNetworkEventi.model.Utente;
import it.unibs.dii.isw.socialNetworkEventi.utility.CategoriaEvento;
import it.unibs.dii.isw.socialNetworkEventi.utility.MsgBox;

class PannelloUtente extends JPanel {
	private Grafica grafica;
	private static final long serialVersionUID = 1L;
	private static final int xInizio = 20;
	private int Y, nRighe=1;
	private JLabel nomignolo, eta, cat, info;
	private JTextField etamin, etamas;
	private JButton salva;
	private LinkedList<JCheckBox> categorie;
	private Font testo;
	
	PannelloUtente (Grafica grafica, Utente u, Font testo, Font testoBottoni, int altezzaStringhe, int larghezza) {
		super();
		setLayout(null);
		setBackground(Grafica.coloreSfondo);
		this.testo = testo;
		this.grafica = grafica;
		int ampiezza = larghezza-2*xInizio;
		
		nomignolo = new JLabel(u.getNome());
		nomignolo.setFont(testoBottoni.deriveFont(testoBottoni.getSize()*2F));
		nomignolo.setHorizontalAlignment(SwingConstants.LEFT);
		nomignolo.setBounds(xInizio, 20, ampiezza, altezzaStringhe/5*12);
		Y+=altezzaStringhe/5*12+20;
		add(nomignolo);
		
		eta = new JLabel ("Fascia d'età:"); cat = new JLabel ("Caregorie d'interesse:");
		eta.setFont(testo); cat.setFont(testo);
		eta.setHorizontalAlignment(SwingConstants.LEFT); cat.setHorizontalAlignment(SwingConstants.LEFT);
		eta.setBounds(xInizio, 20 + Y, ampiezza, altezzaStringhe);
		Y+=20+altezzaStringhe;
		add(eta);
		
		etamin = new JTextField(); etamas = new JTextField();
		etamin.setFont(testo); etamas.setFont(testo);
		etamin.setHorizontalAlignment(SwingConstants.CENTER); etamas.setHorizontalAlignment(SwingConstants.CENTER);
		etamin.setBounds(xInizio, Y+20, (ampiezza-xInizio)/2, (int)(altezzaStringhe*1.1));
		etamas.setBounds(2*xInizio+ampiezza/2, Y+20, (ampiezza-xInizio)/2, (int)(altezzaStringhe*1.1));
		Y+=20+(int)(altezzaStringhe*1.1);
		add(etamin); add(etamas);
		
		etamin.setText("" + u.getEtaMin());
		etamas.setText("" + u.getEtaMax());
		
		
		cat.setBounds(xInizio, 20 + Y, ampiezza, altezzaStringhe);
		Y+=20+altezzaStringhe;
		add(cat);
		
		JCheckBox temp;
		categorie = new LinkedList<>();
		for (String cl : new String[] {"partita_calcio","scii"}) {
			temp = new JCheckBox(cl);
			temp.setFont(testo);
			temp.setBackground(Grafica.coloreSfondo);
			temp.setBounds(xInizio, Y+20, ampiezza, altezzaStringhe);
			Y+=20+altezzaStringhe;
			add(temp);
			categorie.add(temp);
		}
		
		for (CategoriaEvento c : u.getCategorieInteressi()) {
			for (JCheckBox chk_cat : categorie)
				if (chk_cat.getText().equalsIgnoreCase(c.toString()))
					chk_cat.setSelected(true);
		}
			
		salva = new JButton(" 💾  Salva");
		salva.setFont(testoBottoni);
		salva.setBackground(Grafica.coloreBottoni);
		salva.setBounds(xInizio, 20 + Y, ampiezza, altezzaStringhe/5*8);
		Y+=20+altezzaStringhe/5*8;
		salva.addActionListener(click -> impostaPreferito(click));
		add(salva);
		
		info = new JLabel("<HTML>Verranno ricevute notifiche per i nuovi eventi delle categorie selezionate</HTML>");
		info.setFont(testo.deriveFont(Font.ITALIC));
		info.setHorizontalAlignment(SwingConstants.LEFT);
		info.setBounds(xInizio, 20 + Y, ampiezza, nRighe*altezzaStringhe);
		Y+=20+nRighe*altezzaStringhe;
		add(info);
		
		Y+=20;
		setPreferredSize(new Dimension(larghezza,Y));
		//for (Class cl : )
	}
	
	void ridimensiona(int larghezza) {
		int ampiezza = larghezza-2*xInizio;
		int altezzaStringhe = info.getHeight()/nRighe;
		int altezzaprima = info.getHeight();
		nRighe=((int)testo.getStringBounds(info.getText(), ((Graphics2D)getGraphics()).getFontRenderContext()).getWidth() / (ampiezza-2*xInizio) + 1);
		nomignolo.setSize(ampiezza, nomignolo.getHeight());
		eta.setSize(ampiezza, eta.getHeight());
		cat.setSize(ampiezza, cat.getHeight());
		for (JCheckBox chk : categorie)
			chk.setSize(ampiezza, chk.getHeight());
		etamin.setSize((ampiezza-xInizio)/2, etamin.getHeight());
		etamas.setBounds(2*xInizio+ampiezza/2, etamas.getY(), (ampiezza-xInizio)/2, etamas.getHeight());
		salva.setSize(ampiezza, salva.getHeight());
		info.setBounds(xInizio, 20 + Y, ampiezza, nRighe*altezzaStringhe);
		Y+=nRighe*altezzaStringhe - altezzaprima;
		setPreferredSize(new Dimension(larghezza,Y));
	}
	
	private void impostaPreferito(ActionEvent e) {
		try {
			Integer etm = (etamin.getText().equals("") ? -1 : Integer.parseInt(etamin.getText())),
					etM = (etamas.getText().equals("") ? -1 : Integer.parseInt(etamas.getText()));
			if (etm<0 || etM<0 || etm>etM) throw new NumberFormatException();
			String[] elenco = new String[categorie.size()];
			boolean[] selezionata = new boolean[elenco.length];
			for (int i=0; i<elenco.length; i++) {
				elenco[i] = categorie.get(i).getText();
				selezionata[i] = categorie.get(i).isSelected();
			}
			grafica.aggiornaDatiUtente(etm, etM, elenco, selezionata);
			new MsgBox().messaggioSemplice("Profilo utente", "Modifiche salvate");
		} catch (NumberFormatException ex) {
			new MsgBox().messaggioAvviso("Errore di compilazione", "Sono stati inseriti dati scorretti nei campi riguardanti l'età");
		}
	}
}