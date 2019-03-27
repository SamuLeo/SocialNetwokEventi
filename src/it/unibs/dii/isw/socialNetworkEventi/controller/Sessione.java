package it.unibs.dii.isw.socialNetworkEventi.controller;

import java.sql.SQLException;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.ArrayList;

import javax.swing.JOptionPane;

import it.unibs.dii.isw.socialNetworkEventi.model.*;
import it.unibs.dii.isw.socialNetworkEventi.utility.NomeCampi;
import it.unibs.dii.isw.socialNetworkEventi.utility.StatoEvento;
import it.unibs.dii.isw.socialNetworkEventi.view.Grafica;

public class Sessione 
{
	
	private static Utente utente_corrente;
	private static DataBase db;
	
	public static void main(String[] args) 
	{
		connettiDB();
		
		Grafica.getIstance().crea();
		Grafica.getIstance().mostraLogin();		
		
		new Timer().schedule(new TimerTask() {public void run() {aggiornatore.run();}}, 0, 15000);
	}
	
	
	private static void connettiDB()
	{
		db = new DataBase();		
		try
		{
			db.getConnection();
			db.refreshDatiRAM();
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
	}
	
	public static boolean accedi(Utente utente) {
		 try {
			 Integer id_utente = db.existUtente(utente);
			 if(id_utente != null) {
				 utente.setId_utente(id_utente);
				 utente_corrente = utente;
				 return true;
			 }
			 else return false;
		} 
		catch (SQLException e) {e.printStackTrace();}
		return false;
	}

	
	public static Runnable aggiornatore = new Runnable() {
		public void run(){	
		try 
		{
			db.refreshDatiRAM();
			ArrayList<Evento> eventi = new ArrayList<>();
			eventi = db.getEventi();
			
			for(Evento evento : eventi)
			{
				if(controllaStatoEvento(evento) == true)
				{
					db.updateStatoPartitaCalcio(evento);
					switch(evento.getStato())
					{
						case FALLITA : db.segnalaFallimentoEvento(evento);
						break;
						case CONCLUSA : db.segnalaConclusioneEvento(evento);
						break;
						default : return;
					}
				}
			}
		} 
		catch (SQLException e)  {e.printStackTrace();}
	}
};
	
	
	/**
	 * 
	 * @return true se lo stato cambia
	 * @throws SQLException 
	 */
	public static boolean controllaStatoEvento(Evento evento) throws SQLException 
	{
		boolean DataChiusuraIscrizioniNelFuturo = Calendar.getInstance().compareTo((Calendar)evento.getCampo(NomeCampi.D_O_CHIUSURA_ISCRIZIONI).getContenuto()) < 0;
		boolean DataFineEventoNelFuturo;
		
		if (evento.getCampo(NomeCampi.D_O_TERMINE_EVENTO)==null) 
			DataFineEventoNelFuturo= Calendar.getInstance().compareTo((Calendar)evento.getCampo(NomeCampi.D_O_INIZIO_EVENTO).getContenuto()) < 0;
		else 
			DataFineEventoNelFuturo = Calendar.getInstance().compareTo((Calendar)evento.getCampo(NomeCampi.D_O_TERMINE_EVENTO).getContenuto()) < 0;
		
		StatoEvento statoEvento = evento.getStato();
		
		if(DataChiusuraIscrizioniNelFuturo == false && (statoEvento.getCodNomeCampi().equals("Aperta")))
		{
			evento.setStato(StatoEvento.FALLITA);
			return true;
		}
		else if(DataFineEventoNelFuturo == false && (statoEvento.getCodNomeCampi().equals("Chiusa")))
		{
			evento.setStato(StatoEvento.CONCLUSA);
			return true;
		}
		return false;
	}
	
	
	public static boolean aggiungiEvento(Evento evento)
	{
		try
		{
			db.insertEvento(evento); 
			iscrizioneUtenteInEvento(evento);
			return true;
		}
		catch(SQLException e) {e.printStackTrace();}
		catch(Exception e) {System.out.println("ECCEZIONE");e.printStackTrace();}
		return false;
	}
	
	public static boolean insertNotificaUtenteCorrente(Notifica notifica)
	{
		try
		{
			notifica = db.insertNotifica(notifica); 
			db.collegaUtenteNotifica(utente_corrente.getId_utente(), notifica.getIdNotifica());
			return true;
		}
		catch(SQLException e)
		{
			e.printStackTrace();
			return false;
		}
	}
	
	
	public static boolean insertNotifica(Notifica notifica, Utente utente)
	{
		try
		{
			notifica = db.insertNotifica(notifica); 
			db.collegaUtenteNotifica(utente.getId_utente(), notifica.getIdNotifica());
			return true;
		}
		catch(SQLException e)
		{
			e.printStackTrace();
			return false;
		}
	}
	
	
	public static boolean deleteEvento(Evento evento)
	{
		try
		{db.deletePartita(evento.getId()); return true;}
		catch(SQLException e)
		{return false;}
	}
	
	
	public static ArrayList<Evento> getEventi() {
		return db.getEventi();
	}
	
	
	public static PartitaCalcio selectPartita(int id_partita) {
		return db.selectPartitaCalcio(id_partita);
	}
	
	
	public static boolean insertUtente(Utente utente) 
	{
		try 
		{
			if(db.existUtente(utente) != null)
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
	
	
	public static LinkedList<Notifica> getNotificheUtente() 
	{
		
		LinkedList<Notifica> notifiche = null;
		
		try 
		{
			notifiche = db.selectNotificheDiUtente(utente_corrente.getId_utente());
			utente_corrente.setNotifiche(notifiche);
		} 
		catch (SQLException e) 
		{
			System.out.println("Errore durante il caricamento delle notifiche utente");
			e.printStackTrace();
		}
		
		return notifiche;
	}
	
	
	public static LinkedList<Notifica> eliminaNotificaUtente(Notifica notifica)
	{
		try 
		{
			db.deleteCollegamentoNotificaUtente(utente_corrente, notifica);
		} 
		catch (SQLException e) 
		{
			e.printStackTrace();
			System.out.println("Errore durante l'eliminazione della notifica selezionata");
		}
		
		return getNotificheUtente();
	}
	
	
	public static void iscrizioneUtenteInEvento(Evento evento)
	{
		if(utente_corrente == null)
		{
			System.out.println("L'utente corrente è null");
			return;
		}
		
		switch(evento.getClass().getSimpleName())
		{
		case "PartitaCalcio" : 
			{
				PartitaCalcio partita = (PartitaCalcio)evento;
				try
				{
					if(utenteIscrittoAllaPartita(partita))
					{
						System.out.println("Utente già iscritto alla partita");
						return;
					}	
					//se il giocatore occupa l'ultimo posto disponibile allora si notificano gli altri giocatori che la partita è chiusa, ossia si farà
					if(db.getNumeroUtentiDiEvento(partita) == ((Integer)partita.getCampo(NomeCampi.PARTECIPANTI).getContenuto()-1))
					{
						db.collegaUtentePartita(utente_corrente, partita);
						db.segnalaChiusuraEvento(partita);
						partita.setStato(StatoEvento.CHIUSA);
						db.updateStatoPartitaCalcio(partita);
					}
					else if (db.getNumeroUtentiDiEvento(partita) < ((Integer)partita.getCampo(NomeCampi.PARTECIPANTI).getContenuto()))
						db.collegaUtentePartita(utente_corrente, partita);
					else
						return;
				} 
				catch (SQLException e) 
				{
					System.out.println("Errore durante l'iscrizione dell'utente corrente alla partita selezionata");
					e.printStackTrace();
				}
			}
			break;
		default : return;
		}
	}
	
	
	public static void disiscrizioneUtenteEvento(Evento evento) throws RuntimeException{
		if(utente_corrente == null) throw new RuntimeException("L'utente corrente è null");
		
		switch(evento.getClass().getSimpleName()) {
			case "PartitaCalcio" : try {
				PartitaCalcio partita = (PartitaCalcio)evento;
				if(!utenteIscrittoAllaPartita(partita)) throw new RuntimeException ("Utente non iscritto alla partita");	
				db.deleteCollegamentoPartitaCalcioUtente(utente_corrente.getId_utente(), partita.getId());
			} catch (SQLException e) {
				e.printStackTrace();
				throw new RuntimeException ("Errore durante l'eliminazione dell'utente corrente dalla partita selezionata");
			} break;
		}
	}
	
	
	
	public static boolean utenteIscrittoAllaPartita(PartitaCalcio partita)
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
			e.printStackTrace();
		}
		
		return false;
	}

	
	public static LinkedList<PartitaCalcio> getEventiUtenteCorrente()
	{
		try
		{
			return db.selectPartiteDiUtente(utente_corrente);
		} 
		catch (SQLException e) 
		{
			System.out.println("Errore durante il caricamento degli eventi utente");
			e.printStackTrace();
		}
		return null;
	}

	public static Utente getUtente_corrente() {
		return utente_corrente;
	}


	public static void setUtente_corrente(Utente utente_corrente) {
		Sessione.utente_corrente = utente_corrente;
	}
}
