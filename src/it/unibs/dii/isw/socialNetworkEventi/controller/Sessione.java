package it.unibs.dii.isw.socialNetworkEventi.controller;

import java.sql.SQLException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.ArrayList;

import javax.swing.JOptionPane;

import it.unibs.dii.isw.socialNetworkEventi.model.*;
import it.unibs.dii.isw.socialNetworkEventi.utility.CategorieEvento;
import it.unibs.dii.isw.socialNetworkEventi.utility.Logger;
import it.unibs.dii.isw.socialNetworkEventi.utility.NomeCampi;
import it.unibs.dii.isw.socialNetworkEventi.utility.StatoEvento;
import it.unibs.dii.isw.socialNetworkEventi.view.Grafica;

public class Sessione 
{
	
	private static Utente utente_corrente;
	private static DataBase db;
	
	private static  String nome_file_log_sessione ;
	private static Logger logger;
	
	public static void main(String[] args) 
	{
		connettiDB();
		
		creaLogger();
		
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

	
	
	private static void creaLogger()
	{
		String operatingSystem = System.getProperty("os.name").toLowerCase();
		
		if(operatingSystem.indexOf("linux") >= 0 || operatingSystem.indexOf("mac") >= 0) 
			nome_file_log_sessione = "Dati//file_log//log_sessione";
		else if(operatingSystem.indexOf("win") >= 0) 
			nome_file_log_sessione = "Dati\\file_log\\log_sessione";
		
		logger = new Logger(nome_file_log_sessione);
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
	
	public static void setUtente_corrente(Utente utente_corrente) 
	{
		Sessione.utente_corrente = utente_corrente;
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
	 * Questo metodo controlla e in caso sia cambiato aggiorna lo stato dell'evento, verificando se la data di chiusura iscrizioni ha superato la data odierna,
	 * se l'evento ha raggiunto la sua conclusione oppure se è concluso
	 * @return true se lo stato cambia
	 * @throws SQLException 
	 */
	public static boolean controllaStatoEvento(Evento evento) throws SQLException 
	{
		boolean DataChiusuraIscrizioniNelFuturo = Calendar.getInstance().compareTo((Calendar)evento.getCampo(NomeCampi.D_O_CHIUSURA_ISCRIZIONI).getContenuto()) < 0;
		//Calendar termine_ritiro_iscrizioni = (Calendar) evento.getCampo(NomeCampi.D_O_TERMINE_RITIRO_ISCRIZIONE).getContenuto();	
		//boolean termine_ritiro_scaduto = Calendar.getInstance().compareTo(termine_ritiro_iscrizioni)>0;
		boolean DataFineEventoNelFuturo;		
		if (evento.getCampo(NomeCampi.D_O_TERMINE_EVENTO)==null) 
			DataFineEventoNelFuturo= Calendar.getInstance().compareTo((Calendar)evento.getCampo(NomeCampi.D_O_INIZIO_EVENTO).getContenuto()) < 0;		
		else 
			DataFineEventoNelFuturo = Calendar.getInstance().compareTo((Calendar)evento.getCampo(NomeCampi.D_O_TERMINE_EVENTO).getContenuto()) < 0;
		int numero_iscritti_attuali = db.getNumeroUtentiDiEvento(evento);
		int numero_minimo_iscritti = (Integer)evento.getCampo(NomeCampi.PARTECIPANTI).getContenuto();
		//int numero_massimo_iscritti_possibili = numero_minimo_iscritti + (Integer)evento.getCampo(NomeCampi.TOLLERANZA_MAX).getContenuto();
		
		StatoEvento statoEvento = evento.getStato();
		
		if(DataChiusuraIscrizioniNelFuturo == false && (statoEvento.getString().equals("Aperta")) && (numero_iscritti_attuali < numero_minimo_iscritti))
		{
			evento.setStato(StatoEvento.FALLITA);
			logger.scriviLog(String.format("Stato dell'evento con id : %d passato da APERTO a FALLITO", evento.getId()));
			return true;
		}
		else if(DataChiusuraIscrizioniNelFuturo == false && (statoEvento.getString().equals("Aperta")) && (numero_iscritti_attuali > numero_minimo_iscritti))
		{
			evento.setStato(StatoEvento.CHIUSA);
			logger.scriviLog(String.format("Stato dell'evento con id : %d passato da APERTO a CHIUSO", evento.getId()));
			return true;
		}
		else if(DataFineEventoNelFuturo == false && (statoEvento.getString().equals("Chiusa")))
		{
			evento.setStato(StatoEvento.CONCLUSA);
			logger.scriviLog(String.format("Stato dell'evento con id : %d passato da CHIUSO a CONCLUSO", evento.getId()));

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
			logger.scriviLog(String.format("Stato dell'evento con id : %d passato da VALIDO a APERTO", evento.getId()));
			db.segnalaNuovoEventoAgliInteressati(evento);
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
	
	public static boolean aggiungiInteresseUtenteCorrente(CategorieEvento nome_categoria)
	{
		try
		{
			if(utente_corrente.getCategorieInteressi().contains(nome_categoria))
				return true;
			else
				db.collegaUtenteCategoria(getUtente_corrente(), nome_categoria);
		}
		catch(SQLException e)
		{
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	public static boolean eliminaInteresseUtenteCorrente(String nome_categoria)
	{
		try
		{
			if(utente_corrente.getCategorieInteressi().contains(nome_categoria))
				db.deleteCollegamentoCategoriaUtente(getUtente_corrente().getId_utente(), nome_categoria);
			else
				return true;
		}
		catch(SQLException e)
		{
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	public static boolean updateFasciaEtaUtente(Utente utente)
	{
		try
		{
			db.updateEtaMinUtente(utente.getId_utente(), utente.getEtaMin());
			db.updateEtaMaxtente(utente.getId_utente(), utente.getEtaMax());
		}
		catch(SQLException e)
		{
			e.printStackTrace();
			return false;
		}
		return true;
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
					int numero_iscritti_attuali = db.getNumeroUtentiDiEvento(partita);
					int numero_massimo_iscritti_possibili = ((Integer)partita.getCampo(NomeCampi.PARTECIPANTI).getContenuto() + (Integer)partita.getCampo(NomeCampi.TOLLERANZA_MAX).getContenuto());
					Calendar termine_ritiro_iscrizioni = (Calendar) evento.getCampo(NomeCampi.D_O_TERMINE_RITIRO_ISCRIZIONE).getContenuto();
					boolean termine_ritiro_scaduto = Calendar.getInstance().compareTo(termine_ritiro_iscrizioni)>0;
					//se il giocatore occupa l'ultimo posto disponibile e il termine ritiro è scaduto allora si notificano gli altri giocatori che la partita è chiusa, ossia si farà
					if((numero_iscritti_attuali == (numero_massimo_iscritti_possibili-1)) && termine_ritiro_scaduto)
					{
						db.collegaUtentePartita(utente_corrente, partita);
						db.segnalaChiusuraEvento(partita);
						partita.setStato(StatoEvento.CHIUSA);
						db.updateStatoPartitaCalcio(partita);
						logger.scriviLog(String.format("Stato dell'evento con id : %d passato da APERTO a CHIUSO", evento.getId()));
					}
					else if (numero_iscritti_attuali < numero_massimo_iscritti_possibili)
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
	
	public static void deleteEvento(Evento evento) throws RuntimeException
	{
		try
		{
			Calendar termine_ritiro_iscrizioni = (Calendar) evento.getCampo(NomeCampi.D_O_TERMINE_RITIRO_ISCRIZIONE).getContenuto();	
			boolean termine_ritiro_scaduto = Calendar.getInstance().compareTo(termine_ritiro_iscrizioni)>0;
			if(termine_ritiro_scaduto)
				throw new RuntimeException("L'evento non può essere annullato a causa del superamento della data massima per poter effettuare questa operazione");
			else
			{
				evento.setStato(StatoEvento.RITIRATA);
				db.updateStatoPartitaCalcio(evento); 
				db.segnalaRitiroEvento(evento);
				logger.scriviLog(String.format("Stato dell'evento con id : %d passato da APERTO a RITIRATO", evento.getId()));
			}
		}
		catch(SQLException e)
		{throw new RuntimeException("L'evento non può essere annullato a causa del superamento della data massima per poter effettuare questa operazione");}
	}
	
	
	
	
	public static PartitaCalcio selectPartita(int id_partita) 
	{
		return db.selectPartitaCalcio(id_partita);
	}
	
	
	public static ArrayList<Evento> getEventi() {
		return db.getEventi();
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
	
	public HashMap<CategorieEvento,LinkedList<Utente>> getPossibiliUtentiInteressati(Utente utente)
	{
		
		try 
		{
			return db.selectUtentiDaEventiPassati(utente.getId_utente());
		} 
		catch (SQLException e) 
		{
			System.out.println("Errore durante il caricamento dei possibili utenti interessati");
			e.printStackTrace();
		}
		
		return null;
	}

	
	
	
	
	public static LinkedList<Notifica> eliminaNotificaUtente(Notifica notifica)
	{
		try 
		{
			db.deleteCollegamentoNotificaUtente(utente_corrente, notifica);
			utente_corrente.rimuoviNotifica(notifica);
		} 
		catch (SQLException e) 
		{
			e.printStackTrace();
			System.out.println("Errore durante l'eliminazione della notifica selezionata");
		}
		
		return getNotificheUtente();
	}
	
	public static void disiscrizioneUtenteEvento(Evento evento) throws RuntimeException{
		if(utente_corrente == null) throw new RuntimeException("L'utente corrente è null");
		
		Calendar termine_ritiro_iscrizioni = (Calendar) evento.getCampo(NomeCampi.D_O_TERMINE_RITIRO_ISCRIZIONE).getContenuto();	
		boolean termine_ritiro_scaduto = Calendar.getInstance().compareTo(termine_ritiro_iscrizioni)>0;
		if(termine_ritiro_scaduto)
			throw new RuntimeException("L'iscrizione non può essere annullata a causa del superamento del termine della possibilità di ritiro");
		
		switch(evento.getClass().getSimpleName()) {
		case "PartitaCalcio" : try {
			PartitaCalcio partita = (PartitaCalcio)evento;
			if(!utenteIscrittoAllaPartita(partita)) throw new RuntimeException ("Utente non iscritto alla partita");	
			db.deleteCollegamentoPartitaCalcioUtente(utente_corrente.getId_utente(), partita.getId());
		} catch (SQLException e) 
		{
			e.printStackTrace();
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
	
	public static LinkedList<Evento> getEventiUtenteCorrente()
	{
		try
		{
			return db.selectEventiDiUtente(utente_corrente.getId_utente());
		} 
		catch (SQLException e) 
		{
			System.out.println("Errore durante il caricamento degli eventi utente");
			e.printStackTrace();
		}
		return null;
	}

	public static Utente getUtente_corrente() 
	{
		return utente_corrente;
	}
}
