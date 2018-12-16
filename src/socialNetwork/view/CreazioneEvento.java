package socialNetwork.view;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import java.awt.event.ItemListener;
import java.util.Calendar;
import java.util.Date;
import java.awt.event.ItemEvent;
import socialNetwork.content.*;

public class CreazioneEvento extends JPanel {
	private static final long serialVersionUID = 1L;
	private static final String categoriaPartita = "Partita di calcio";
	private static final String[] nomeCampiComuni = {"Titolo", "Numero partecipanti", "Termine ultimo di iscrizione", "Luogo", "Data", "Ora", "Durata", "Quota individuale", "Compreso nella quota", "Data conclusiva", "Ora conclusiva", "Note"};
	private static final String[] nomeCampiPartitaDiCalcio = {"Genere", "Eta minima", "Eta massima"};
	JComboBox<String> comboBox = new JComboBox<>();
	JLabel[] campiComuni = new JLabel[nomeCampiComuni.length];
	JTextField[] testoCampiComuni = new JTextField[nomeCampiComuni.length];
	JLabel[] campiPartitaCalcio;
	JTextField[] testoCampiPartitaCalcio;
	public static final int cordinataX = 35;
	private Font testo;
	private int X=0, Y=0;
	
	/**
	 * @param testo Font usato per il testo
	 * @param frameWidth Larghezza della finestra (di conseguenza anche del pannello)
	 * @param fontHeight Parametro che indica l'altezza di una stringa disegnata con il font prestabilito sullo schermo in uso (dpiende dai DPI)
	 */
	CreazioneEvento (Font testo, int frameWidth, int fontHeight) {
		this.testo=testo;
		X=frameWidth;
		this.setLayout(null);
		comboBox.setBounds(cordinataX, 20+(int)(fontHeight*1.1), frameWidth-100, (int)(fontHeight*1.1));
		this.add(comboBox);
		comboBox.addItem(categoriaPartita);
		comboBox.setSelectedItem(null);
		comboBox.setFont(testo);
		comboBox.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent arg0) {
				formCategoria((String)comboBox.getSelectedItem(), X, fontHeight);
			}
		});
		JLabel lblCategorie = new JLabel("Categoria");
		lblCategorie.setFont(testo);
		lblCategorie.setBounds(cordinataX, 20, frameWidth-100, (int)(fontHeight*1.1));
		this.add(lblCategorie);
		Y=20+(int)(fontHeight*1.1)+(int)(fontHeight*1.1);
		this.setPreferredSize(new Dimension(frameWidth,Y));
		Grafica.getIstance().btnConfermaCreazioneEvento.addActionListener(e -> conferma());
	}
	
	/**crea i vari componenti dei campi comuni a tutte le attivita
	 * 
	 * @param frameWidth Larghezza della finestra
	 * @param fontHeight Parametro che indica l'altezza di una stringa disegnata con il font prestabilito sullo schermo in uso (dpiende dai DPI)
	 */
	void formCampiComuni(int frameWidth, int fontHeight) {
		for(int i = 0; i < nomeCampiComuni.length; i++) {
			campiComuni[i]=new JLabel(nomeCampiComuni[i]);
			this.add(campiComuni[i]);
			campiComuni[i].setBounds(cordinataX, Y+10, frameWidth-100, (int)(fontHeight*1.1));
			campiComuni[i].setFont(testo);
			testoCampiComuni[i]=new JTextField();
			this.add(testoCampiComuni[i]);
			testoCampiComuni[i].setBounds(cordinataX, Y+20+(int)(fontHeight*1.1), frameWidth-100, (int)(fontHeight*1.1));
			testoCampiComuni[i].setFont(testo);
			Y+=20+(int)(fontHeight*1.1)*2;
		}
		this.setPreferredSize(new Dimension(frameWidth,Y));
	}
	
	//crea i vari componenti in base alla categoria
	void formCategoria(String comboBox, int frameWidth, int fontHeight) {
		formCampiComuni(frameWidth, fontHeight);
		if(comboBox.equals(categoriaPartita)) {
			campiPartitaCalcio = new JLabel[nomeCampiPartitaDiCalcio.length];
			testoCampiPartitaCalcio = new JTextField[nomeCampiPartitaDiCalcio.length];
			for(int i = 0; i < nomeCampiPartitaDiCalcio.length; i++) {
				campiPartitaCalcio[i]=new JLabel(nomeCampiPartitaDiCalcio[i]);
				this.add(campiPartitaCalcio[i]);
				campiPartitaCalcio[i].setBounds(cordinataX, Y+10, frameWidth-100, (int)(fontHeight*1.1));
				campiPartitaCalcio[i].setFont(testo);
				testoCampiPartitaCalcio[i]=new JTextField();
				this.add(testoCampiPartitaCalcio[i]);
				testoCampiPartitaCalcio[i].setBounds(cordinataX, Y+20+(int)(fontHeight*1.1), frameWidth-100, (int)(fontHeight*1.1));
				testoCampiPartitaCalcio[i].setFont(testo);
				Y+=20+(int)(fontHeight*1.1)*2;
			}
			Y+=20;
		}
		this.setPreferredSize(new Dimension(frameWidth,Y));
	}
	
	void ridimensiona(int frameWidth) {
		X=frameWidth;
		for (Component c : this.getComponents())
			c.setSize(frameWidth-100, c.getHeight());
		this.setPreferredSize(new Dimension(frameWidth,Y));
	}
	
	public int getWidth() {
		return X;
	}
	
	public int getHeight() {
		return Y;
	}
	
	private void conferma() {
		//Tutti i campi numerici devono essere controllati che contengano un numero
		//I campi che contengono date devono essere compressi in oggetti Calendar
		try { Evento e;
		if (comboBox.getSelectedItem()==null) return;
		if (comboBox.getSelectedItem().equals(categoriaPartita))
			e = new PartitaCalcio(testoCampiComuni[3].getText(), null, null, Integer.parseInt(testoCampiComuni[1].getText()), Integer.parseInt(testoCampiComuni[7].getText()), testoCampiComuni[0].getText(), testoCampiComuni[11].getText(), testoCampiComuni[8].getText(), null, Integer.parseInt(testoCampiPartitaCalcio[1].getText()), Integer.parseInt(testoCampiPartitaCalcio[2].getText()), testoCampiPartitaCalcio[0].getText());
		} catch(NumberFormatException e) {JOptionPane.showMessageDialog(null, "Avete inserito testo non valido in campi numerici", "Errore compilazione", JOptionPane.INFORMATION_MESSAGE); return;}
		catch (Exception e) {JOptionPane.showMessageDialog(null, e.getMessage(), "Errore compilazione", JOptionPane.INFORMATION_MESSAGE); return;}
	}
}