package it.unibs.dii.isw.socialNetworkEventi.view;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import java.util.Calendar;
import java.util.function.Consumer;

import it.unibs.dii.isw.socialNetworkEventi.model.Evento;
import it.unibs.dii.isw.socialNetworkEventi.model.PartitaCalcio;

public class CreazioneEvento extends JPanel {
	private static final long serialVersionUID = 1L;
	private static final String categoriaPartita = "Partita di calcio";
	private static final String[] nomeCampiComuni = {"Titolo", "Numero partecipanti", "Partecipanti in esubero ammessi", "Luogo", "Quota individuale", "Compreso nella quota", "Note"};
	private static final String[] nomeCampiPartitaDiCalcio = {"Eta minima", "Eta massima", "Sesso"};
	private static final String[] nomeCampiComuniData = {"Termine ultimo di iscrizione", "Data inizio", "Data conclusiva","Termine ritiro iscrizione"};
	private static final String[] nomeCampiComuniOra = {"Ora termine iscrizioni", "Ora inizio", "Ora conclusiva","Ora termine ritiro iscrizione"};
	private static final String[] GMA = {"G", "M", "A"};
	private static final String[] MO = {"h", "m"};
	public static final int cordinataX = 35;
	private Font testo;
	private int X=0, Y=0;
	JComboBox<String> comboBox = new JComboBox<>(), sesso;
	JLabel lblCategorie = new JLabel("Categoria");
	
	JLabel[] campiComuni = new JLabel[nomeCampiComuni.length];
	JTextField[] testoCampiComuni = new JTextField[nomeCampiComuni.length];
	
	JLabel[] campiComuniData = new JLabel[nomeCampiComuniData.length];
	JButton[] campiComuniCALENDARIO = new JButton[nomeCampiComuniData.length];
	JTextField[][] testoCampiComuniData = new JTextField[nomeCampiComuniData.length][3];
	JLabel[][] giornoMeseAnno = new JLabel[nomeCampiComuniData.length][3];
	
	JLabel[] campiComuniOra = new JLabel[nomeCampiComuniOra.length];
	JLabel[][] oreMinuti = new JLabel[nomeCampiComuniOra.length][2];
	JTextField[][] testoCampiComuniOra = new JTextField[nomeCampiComuniOra.length][2];
	
	JLabel[] campiPartitaCalcio;
	JTextField[] testoCampiPartitaCalcio;
	
	/**
	 * @param testo Font usato per il testo
	 * @param frameWidth Larghezza della finestra (di conseguenza anche del pannello)
	 * @param fontHeight Parametro che indica l'altezza di una stringa disegnata con il font prestabilito sullo schermo in uso (dpiende dai DPI)
	 */
	CreazioneEvento (Font testo, int frameWidth, int fontHeight, Color sfondo) {
		this.testo=testo;
		X=frameWidth;
		this.setLayout(null);
		setBackground(sfondo);
		comboBox.setBounds(cordinataX, 20+(int)(fontHeight*1.1), frameWidth-100, (int)(fontHeight*1.1));
		this.add(comboBox);
		comboBox.addItem(categoriaPartita);
		comboBox.setSelectedItem(null);
		comboBox.setFont(testo);
		comboBox.addFocusListener(tabList);
		comboBox.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent arg0) {
				formCategoria((String)comboBox.getSelectedItem(), X, fontHeight);
			}
		});
		lblCategorie.setFont(testo);
		lblCategorie.setBounds(cordinataX, 20, frameWidth-100, (int)(fontHeight*1.1));
		this.add(lblCategorie);
		Y=20+(int)(fontHeight*1.1)+(int)(fontHeight*1.1);
		this.setPreferredSize(new Dimension(frameWidth,Y));
		Grafica.getIstance().btnConfermaCreazioneEvento.addActionListener(e -> conferma());
		comboBox.setSelectedIndex(0);
	}

	/**crea i vari componenti dei campi comuni a tutte le attivita
	 * 
	 * @param frameWidth Larghezza della finestra
	 * @param fontHeight Parametro che indica l'altezza di una stringa disegnata con il font prestabilito sullo schermo in uso (dipende dai DPI)
	 */
	void formCampiComuni(int frameWidth, int fontHeight) {
		for(int i = 0; i < nomeCampiComuni.length; i++) {
			campiComuni[i]=new JLabel(nomeCampiComuni[i]);
			this.add(campiComuni[i]);
			campiComuni[i].setBounds(cordinataX, Y+10, frameWidth-100, (int)(fontHeight*1.1));
			campiComuni[i].setFont(testo);
			testoCampiComuni[i]= new JTextField();
			testoCampiComuni[i].addFocusListener(tabList);
			this.add(testoCampiComuni[i]);
			testoCampiComuni[i].setBounds(cordinataX, Y+20+(int)(fontHeight*1.1), frameWidth-100, (int)(fontHeight*1.1));
			testoCampiComuni[i].setFont(testo);
			Y+=20+(int)(fontHeight*1.1)*2;
		}
		Y-=10+(int)(fontHeight*1.1);
		for(int i = 0; i < nomeCampiComuniData.length; i++) {
			campiComuniData[i]= new JLabel(nomeCampiComuniData[i]);
			this.add(campiComuniData[i]);
			campiComuniData[i].setBounds(cordinataX + (int)(fontHeight*1.1) + 30, Y+20+(int)(fontHeight*1.1), frameWidth-130-(int)(fontHeight*1.1), (int)(fontHeight*1.1));
			campiComuniData[i].setFont(testo);
			
			campiComuniCALENDARIO[i] = new JButton("📅");
			campiComuniCALENDARIO[i].setFont(testo.deriveFont(testo.getSize()*0.8F));
			campiComuniCALENDARIO[i].setForeground(new Color(255,50,50));
			campiComuniCALENDARIO[i].setBorderPainted(false);
			campiComuniCALENDARIO[i].setBackground(new Color(230,230,230));
			campiComuniCALENDARIO[i].setBounds(cordinataX, Y+20+(int)(fontHeight*1.1), (int)(fontHeight*1.5), (int)(fontHeight*1.1));
			this.add(campiComuniCALENDARIO[i]);
			campiComuniCALENDARIO[i].addActionListener(new RunnableCalendario(i, testo, frameWidth/3*2));
			Y+=20+(int)(fontHeight*1.1)*2;
			for(int j = 0; j < 3; j++) {
				testoCampiComuniData[i][j]= new JTextField();
				testoCampiComuniData[i][j].addFocusListener(tabList);
				this.add(testoCampiComuniData[i][j]);
				testoCampiComuniData[i][j].setBounds(cordinataX+(j*((frameWidth-100)/3))+(frameWidth-100)/6, Y+10, (frameWidth-100)/6, (int)(fontHeight*1.1));
				testoCampiComuniData[i][j].setFont(testo);
				giornoMeseAnno[i][j]= new JLabel(GMA[j]);
				this.add(giornoMeseAnno[i][j]);
				giornoMeseAnno[i][j].setBounds(cordinataX+j*((frameWidth-100)/3), Y+10, (frameWidth-100)/6, (int)(fontHeight*1.1));
				giornoMeseAnno[i][j].setFont(testo);
				giornoMeseAnno[i][j].setHorizontalAlignment(SwingConstants.CENTER);
			}
		}
		for(int i = 0; i < nomeCampiComuniOra.length; i++) {
			campiComuniOra[i]= new JLabel(nomeCampiComuniOra[i]);
			this.add(campiComuniOra[i]);
			campiComuniOra[i].setBounds(cordinataX, Y+20+(int)(fontHeight*1.1), frameWidth-100, (int)(fontHeight*1.1));
			campiComuniOra[i].setFont(testo);
			Y+=20+(int)(fontHeight*1.1)*2;
			for(int j = 0; j < 2; j++) {
				testoCampiComuniOra[i][j]= new JTextField();
				testoCampiComuniOra[i][j].addFocusListener(tabList);
				this.add(testoCampiComuniOra[i][j]);
				testoCampiComuniOra[i][j].setBounds(cordinataX+j*((frameWidth-100)/2)+(frameWidth-100)/6, Y+10, (frameWidth-100)/3, (int)(fontHeight*1.1));
				testoCampiComuniOra[i][j].setFont(testo);
				oreMinuti[i][j]= new JLabel(MO[j]);
				this.add(oreMinuti[i][j]);
				oreMinuti[i][j].setBounds(cordinataX+j*((frameWidth-100)/2), Y+10, (frameWidth-100)/6, (int)(fontHeight*1.1));
				oreMinuti[i][j].setFont(testo);
				oreMinuti[i][j].setHorizontalAlignment(SwingConstants.CENTER);
			}
		}
		Y+=10+(int)(fontHeight*1.1);
		this.setPreferredSize(new Dimension(frameWidth,Y));
	}
	
	/**
	 * crea i vari componenti in base alla categoria
	 */
	void formCategoria(String comboBox, int frameWidth, int fontHeight) {
		formCampiComuni(frameWidth, fontHeight);
		if(comboBox.equals(categoriaPartita)) {
			campiPartitaCalcio = new JLabel[nomeCampiPartitaDiCalcio.length];
			testoCampiPartitaCalcio = new JTextField[nomeCampiPartitaDiCalcio.length-1];
			for(int i = 0; i < nomeCampiPartitaDiCalcio.length; i++) {
				campiPartitaCalcio[i]=new JLabel(nomeCampiPartitaDiCalcio[i]);
				this.add(campiPartitaCalcio[i]);
				campiPartitaCalcio[i].setBounds(cordinataX, Y, frameWidth-100, (int)(fontHeight*1.1));
				campiPartitaCalcio[i].setFont(testo);
				Y+=10+(int)(fontHeight*1.1);
				if (i<testoCampiPartitaCalcio.length) {
					testoCampiPartitaCalcio[i]=new JTextField();
					testoCampiPartitaCalcio[i].addFocusListener(tabList);
					this.add(testoCampiPartitaCalcio[i]);
					testoCampiPartitaCalcio[i].setBounds(cordinataX, Y, frameWidth-100, (int)(fontHeight*1.1));
					testoCampiPartitaCalcio[i].setFont(testo);
					Y+=10+(int)(fontHeight*1.1);
				}
			}
			sesso=new JComboBox<>();
			sesso.addFocusListener(tabList);
			sesso.addItem("Maschi"); sesso.addItem("Femmine"); sesso.addItem("Qualsiasi");
			sesso.setSelectedIndex(2);
			sesso.setFont(testo);
			sesso.setBounds(cordinataX, Y, frameWidth-100, (int)(fontHeight*1.1));
			add(sesso);
			Y+=20+(int)(fontHeight*1.1)*2;
		}
		this.setPreferredSize(new Dimension(frameWidth,Y));
	}

	void ridimensiona(int frameWidth) {
		X=frameWidth;
		lblCategorie.setSize(frameWidth-100, lblCategorie.getHeight());
		comboBox.setSize(frameWidth-100, comboBox.getHeight());
		if (campiComuni[0] != null) {
			int a =campiComuni[0].getHeight();
			for (JLabel l: campiComuni)
				l.setSize(frameWidth-100, a);
			for (JTextField f: testoCampiComuni)
				f.setSize(frameWidth-100, a);
			for (JLabel l: campiComuniData)
				l.setSize(frameWidth-100, a);
			for (JLabel l: campiComuniOra)
				l.setSize(frameWidth-100, a);
			for (JTextField[] j: testoCampiComuniData)
				for (int i=0; i<j.length; i++)
					j[i].setBounds(cordinataX+(i*((frameWidth-100)/3))+(frameWidth-100)/6, j[i].getY(), (frameWidth-100)/6, (int)(a*1.1));
			for (JLabel[] j: giornoMeseAnno)
				for (int i=0; i<j.length; i++)
					j[i].setBounds(cordinataX+i*((frameWidth-100)/3), j[i].getY(), (frameWidth-100)/6, (int)(a*1.1));
			for (JLabel[] j: oreMinuti)
				for (int i=0; i<j.length; i++)
					j[i].setBounds(cordinataX+i*((frameWidth-100)/2), j[i].getY(), (frameWidth-100)/6, (int)(a*1.1));
			for (JTextField[] j: testoCampiComuniOra)
				for (int i=0; i<j.length; i++)
					j[i].setBounds(cordinataX+i*((frameWidth-100)/2)+(frameWidth-100)/6, j[i].getY(), (frameWidth-100)/3, (int)(a*1.1));
			if (campiPartitaCalcio != null) 
				for (JLabel l : campiPartitaCalcio)
					if (l.isVisible()) l.setSize(frameWidth-100, a);
			if (testoCampiPartitaCalcio != null) 
				for (JTextField f : testoCampiPartitaCalcio)
					if (f.isVisible()) f.setSize(frameWidth-100, a);
			if (sesso!=null && sesso.isVisible())
				sesso.setSize(frameWidth-100, a);
		}
		this.setPreferredSize(new Dimension(frameWidth,Y));
	}

	public int getWidth() {
		return X;
	}
	
	public int getHeight() {
		return Y;
	}
	
	FocusListener tabList = new FocusListener() {
		public void focusGained(FocusEvent ev) {
			try {
				int ah = ((JComponent)(ev.getSource())).getY();
				JScrollBar jscb = Grafica.getIstance().pannelloCentrale.getVerticalScrollBar();
				if (ah>(jscb.getValue()+jscb.getVisibleAmount())*0.9) {
					for (int val = jscb.getValue(); val <= ah - jscb.getVisibleAmount()/2; val++)
						jscb.setValue(val); //Thread.currentThread().sleep(10);
				}
				else if (ah < jscb.getValue()*1.1) {
					for (int val = jscb.getValue(); val >= ah - jscb.getVisibleAmount()/2; val--)
						jscb.setValue(val);
				}
				//System.out.println("min: " + jscb.getMinimum() + " max: " + jscb.getMaximum() + " attuale: " + jscb.getValue() + " visibile: " + jscb.getVisibleAmount());
			}
			catch (ClassCastException /*| InterruptedException*/ e) {}
		}
		public void focusLost(FocusEvent arg0) {}
	};

	private void conferma() {
		//Tutti i campi numerici devono essere controllati che contengano un numero
		//I campi che contengono date devono essere compressi in oggetti Calendar
		try 
		{ 
			Evento e;
			if (comboBox.getSelectedItem()==null) return;	//Nessuna categoria selezionata
			
			if (comboBox.getSelectedItem().equals(categoriaPartita)){
				Calendar termineIscrizione, dataInizioEvento, dataFineEvento = null, termineRitiroIscrizione = null;
				//Acquisizione data e ora, l'aggiunta del "- 1" è posta in corrispondenza del mese in quanto Java associa lo 0 a Gennaio				
				termineIscrizione = Calendar.getInstance();
				termineIscrizione.set(
						Integer.parseInt(testoCampiComuniData[0][2].getText()),
						Integer.parseInt(testoCampiComuniData[0][1].getText())-1,
						Integer.parseInt(testoCampiComuniData[0][0].getText()),
						Integer.parseInt(testoCampiComuniOra[0][0].getText()),
						Integer.parseInt(testoCampiComuniOra[0][1].getText()),0);
				dataInizioEvento = Calendar.getInstance();
				dataInizioEvento.set(
						Integer.parseInt(testoCampiComuniData[1][2].getText()),
						Integer.parseInt(testoCampiComuniData[1][1].getText())-1,
						Integer.parseInt(testoCampiComuniData[1][0].getText()),
						Integer.parseInt(testoCampiComuniOra[1][0].getText()),
						Integer.parseInt(testoCampiComuniOra[1][1].getText()),0);
				
				//Questo parametro è opzionale percui si compila solo se è presente
				if (!testoCampiComuniData[2][2].getText().equals("") || !testoCampiComuniData[2][1].getText().equals("") || !testoCampiComuniData[2][0].getText().equals("") || !testoCampiComuniOra[2][1].getText().equals("") || !testoCampiComuniOra[2][0].getText().equals("")) {
					dataFineEvento = Calendar.getInstance();
					dataFineEvento.set(
							Integer.parseInt(testoCampiComuniData[2][2].getText()),
							Integer.parseInt(testoCampiComuniData[2][1].getText())-1,
							Integer.parseInt(testoCampiComuniData[2][0].getText()),
							Integer.parseInt(testoCampiComuniOra[2][0].getText()),
							Integer.parseInt(testoCampiComuniOra[2][1].getText()),0);
				}
				//Questo parametro è opzionale percui si compila solo se è presente
				if (!testoCampiComuniData[3][2].getText().equals("") || !testoCampiComuniData[3][1].getText().equals("") || !testoCampiComuniData[3][0].getText().equals("") || !testoCampiComuniOra[3][1].getText().equals("") || !testoCampiComuniOra[3][0].getText().equals("")) {
					termineRitiroIscrizione = Calendar.getInstance();
					termineRitiroIscrizione.set(
							Integer.parseInt(testoCampiComuniData[3][2].getText()),
							Integer.parseInt(testoCampiComuniData[3][1].getText())-1,
							Integer.parseInt(testoCampiComuniData[3][0].getText()),
							Integer.parseInt(testoCampiComuniOra[3][0].getText()),
							Integer.parseInt(testoCampiComuniOra[3][1].getText()),0);
				}
				//Controllo se le date inserite sono tutte nl futuro
				Calendar adesso = Calendar.getInstance();
				if (termineIscrizione.before(adesso) || dataInizioEvento.before(adesso) || (dataFineEvento==null? false: dataFineEvento.before(adesso)) || (termineRitiroIscrizione==null? false: termineRitiroIscrizione.before(adesso)))
					throw new IllegalArgumentException("Necessario inserire date nel futuro");
				
				int tolleranza;
				if (testoCampiComuni[2].getText().equals("")) tolleranza = 0;
				else tolleranza = Integer.parseInt(testoCampiComuni[2].getText());
				if (tolleranza <0 ) throw new IllegalArgumentException ("<HTML>La tolleranza è il numero di iscritti che possono esserci o non, oltre alla base obbligatoria espressa cal campo \"Numero Partecipanti\". Pertanto non può essere negativa</HTML>");
				
				e = new PartitaCalcio(
						Grafica.getIstance().chiediUtenteCorrente(),
/*Ob.	LUOGO*/			testoCampiComuni[3].getText(),
/*Ob.	Data FINE ISCR*/termineIscrizione,
/*Ob.	Data-ora iniz*/	dataInizioEvento,
/*Ob.	Partecip.*/		Integer.parseInt(testoCampiComuni[1].getText()),
/*Ob.	COSTO*/			Integer.parseInt(testoCampiComuni[4].getText()),
/*Opz.	TITOLO*/		(testoCampiComuni[0].getText().equals("")? "Evento anonimo" : testoCampiComuni[0].getText()),
/*Opz.	NOTE*/			testoCampiComuni[6].getText(),
/*Opz.	COMPRESO*/		testoCampiComuni[5].getText(),
/*Opz.	Data-ora FINE*/	dataFineEvento,
/*Opz.	Termine ritiro*/termineRitiroIscrizione,
/*Opz.	Tolleranza*/	tolleranza,
/*Ob.	ETA MIN*/		Integer.parseInt(testoCampiPartitaCalcio[0].getText()),
/*Ob.	ETA MAS*/		Integer.parseInt(testoCampiPartitaCalcio[1].getText()),
/*Ob.	GENERE*/		(String)sesso.getSelectedItem());
				
				System.out.println("Creata una partita di calcio con successo: ora salvo");
				Grafica.getIstance().aggiungiEvento(e);
			}
		} 
		catch(NumberFormatException e) {
			JOptionPane.showMessageDialog(null, "Avete inserito testo non valido o inesistente in campi numerici", "Errore compilazione", JOptionPane.WARNING_MESSAGE); 
			return;
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, e.getMessage(), "Errore compilazione", JOptionPane.WARNING_MESSAGE); 
			return;
		}
	}
}

class RunnableCalendario implements Runnable, ActionListener {
	int i, lato;
	Font testo;
	
	public RunnableCalendario(int i, Font testo, int lato) {
		this.i=i;
		this.testo=testo;
		this.lato=lato;
	}
	
	public void run() {
		new Calendario(testo, lato, new ConsumerCalendario(i));
	}
	public void actionPerformed(ActionEvent click) {
		new Calendario(testo, lato, new ConsumerCalendario(i));
	}
}
class ConsumerCalendario implements Consumer<Calendar> {
	int i;
	
	public ConsumerCalendario (int i) {
		this.i=i;
	}
	
	public void accept(Calendar data) {
		//System.out.println("Box " + i + ": " + data.get(Calendar.DATE));
		Grafica.getIstance().form.testoCampiComuniData[i][0].setText("" + data.get(Calendar.DATE));
		Grafica.getIstance().form.testoCampiComuniData[i][1].setText("" + (data.get(Calendar.MONTH) +1));
		Grafica.getIstance().form.testoCampiComuniData[i][2].setText("" + data.get(Calendar.YEAR));
	}	
}