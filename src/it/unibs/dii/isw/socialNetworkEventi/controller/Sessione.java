package it.unibs.dii.isw.socialNetworkEventi.controller;

import java.sql.SQLException;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.ArrayList;

import javax.swing.JOptionPane;

import it.unibs.dii.isw.socialNetworkEventi.model.*;
import it.unibs.dii.isw.socialNetworkEventi.utility.Logger;
import it.unibs.dii.isw.socialNetworkEventi.view.Grafica;

public class Sessione 
{
	private static Utente utente_corrente;
	private static DataBase db;
	
	public static void main(String[] args) 
	{
		connettiDB();
//		Utente utente = new Utente("Samuele","pwsicura123");
//		accedi(utente);
//		Calendar data_termine = Calendar.getInstance(); data_termine.set(2018, 12, 25, 24, 00);
//		Calendar data_inizio = Calendar.getInstance(); data_inizio.set(2018, 12, 28, 15, 00);		
//		Calendar data_fine = Calendar.getInstance(); data_fine.set(2018, 12, 28, 16, 00);
//
//		PartitaCalcio partita_calcio = new PartitaCalcio(
//				Sessione.getUtente_corrente(),
//				"Mompiano",
//				data_termine,
//				data_inizio,
//				10,
//				5,
//				null,
//				null,
//				null,
//				data_fine,
//				18,
//				25,
//				"maschi"
//						);
//		
//		aggiungiEventoAlDB(partita_calcio);
		
		ArrayList<Evento> e = selectEventi();
		for(Evento el : e)
			System.out.println(el);

//		Grafica.getIstance().crea();
//		Grafica.getIstance().mostraLogin();		
	}
	
	
	private static void connettiDB()
	{
		db = new DataBase();		
		try
		{db.getConnection();}
		catch(SQLException e)
		{e.printStackTrace();}
	}
	
	
	public static boolean aggiungiEventoAlDB(Evento evento)
	{
		try
		{db.insertEvento(evento); return true;}
		catch(SQLException e)
		{
			e.printStackTrace();
			return false;
		}
	}
	
	
	public static boolean deleteEvento(Evento evento)
	{
		try
		{db.deleteEvento(evento); return true;}
		catch(SQLException e)
		{return false;}
	}
	
	
	public static ArrayList<Evento> selectEventi()
	{
		try 
		{
			db.refreshDatiRAM();
		} 
		catch (SQLException e) 
		{
			System.out.println("Errore durante il caricamento dati della bacheca");
		}
		
		return db.getEventi();
	}
	
	
	public static PartitaCalcio selectPartita(int id_partita)
	{
		try
		{return db.selectPartitaCalcio(id_partita);}
		catch(SQLException e)
		{return null;}
	}
	
	
	public static boolean creaUtente(Utente utente) 
	{
		try 
		{
			if(db.existUtente(utente))
				return false;
			utente_corrente = db.insertUtente(utente);
		} 
		catch (SQLException e) 
		{
			System.out.println("L'inserzione utente non è andata a buon fine");
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, e.getMessage(), "Errore compilazione", JOptionPane.INFORMATION_MESSAGE);
		}
		return true;
	}
	
	
	public static boolean accedi(Utente utente)
	{

		 try 
		 {
			if(db.existUtente(utente))
			 {
				 utente_corrente = utente;
				 return true;
			 }
			 else
				 return false;
		} 
		 catch (SQLException e) 
		 {
			e.printStackTrace();
		}
		 return true;
	}
	
	
	public LinkedList<Notifica> getNotificheUtente(Utente utente) 
	{
		
		LinkedList<Notifica> notifiche = null;
		
		try 
		{
			notifiche = db.selectNotificheDiUtente(utente.getId_utente());
			utente.setNotifiche(notifiche);
		} 
		catch (SQLException e) 
		{
			System.out.println("Errore durante il caricamento delle notifiche utente");
		}
		
		return notifiche;
	}
	
	
	public LinkedList<Notifica> eliminaNotificaUtente(Notifica notifica)
	{
		try 
		{
			db.deleteNotifica(notifica.getIdNotifica());
		} 
		catch (SQLException e) 
		{
			System.out.println("Errore durante l'eliminazione della notifica selezionata");
		}
		
		return getNotificheUtente(utente_corrente);
	}
	
	
	public void iscrizionePartita(PartitaCalcio partita)
	{
		if(utente_corrente == null)
			System.out.println("L'utente corrente è null");
		
		try
		{
			if(utenteIscrittoAllaPartita(partita))
			{
				System.out.println("Utente già iscritto alla partita");
				return;
			}	
			db.collegaUtentePartita(utente_corrente.getId_utente(), partita.getId());
		} 
		catch (SQLException e) 
		{
			System.out.println("Errore durante l'iscrizione dell'utente corrente alla partita selezionata");
		}
	}
	
	
	public boolean utenteIscrittoAllaPartita(PartitaCalcio partita)
	{
		if(utente_corrente == null)
			System.out.println("L'utente corrente è null");
		
		try 
		{
			return db.existUtenteInPartita(utente_corrente, partita);
		} 
		catch (SQLException e) 
		{
			System.out.println("L'utente non è iscritto alla partita selezionata");
		}
		
		return false;
	}


	public static Utente getUtente_corrente() {
		return utente_corrente;
	}


	public static void setUtente_corrente(Utente utente_corrente) {
		Sessione.utente_corrente = utente_corrente;
	}
	
	
//	public static boolean creaUtente(String utente, String password) 
//	{
//		try 
//		{
//			return DB.inserisciUtente(new Utente(utente, password));
//		} catch (IllegalArgumentException e) {
//			JOptionPane.showMessageDialog(null, e.getMessage(), "Errore compilazione", JOptionPane.INFORMATION_MESSAGE);
//			return false;}
//	}
//	
//	public static boolean accedi(String utente, String password) {
//		Utente io = DB.trovaUtente(utente);
//		if (io == null) {log("Utente non trovato"); return false;}
//		else if (io.getPassword().equals(password)) {log ("Accesso eseguito"); return true;}
//		else {log("Password errata " + password + " " + io.getPassword()); return false;}
//	}
	
//	public static void log(String l) {
//		System.out.println(String.format("%-70s %02d.%02d.%02d,%04d", l, Calendar.getInstance().get(Calendar.HOUR_OF_DAY),
//				Calendar.getInstance().get(Calendar.MINUTE), Calendar.getInstance().get(Calendar.SECOND), Calendar.getInstance().get(Calendar.MILLISECOND)));
//		}
}