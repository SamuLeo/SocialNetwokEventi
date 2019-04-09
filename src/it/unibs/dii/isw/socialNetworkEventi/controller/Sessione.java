package it.unibs.dii.isw.socialNetworkEventi.controller;

import java.sql.SQLException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.ArrayList;

import it.unibs.dii.isw.socialNetworkEventi.model.*;
import it.unibs.dii.isw.socialNetworkEventi.utility.*;
import it.unibs.dii.isw.socialNetworkEventi.view.Grafica;

public class Sessione 
{
	private static Utente utente_corrente;
	public static void setUtente_corrente(Utente utente_corrente) {Sessione.utente_corrente = utente_corrente;}
	public static Utente getUtente_corrente() {return utente_corrente;}
	private static DataBase db;
	
	private static String nome_file_log_sessione ;
	private static Logger logger;
	private static String nome_file_error_sessione;
	private static Logger error_logger;
	
	public static void main(String[] args) throws SQLException 
	{
		creaLogger();
		connettiDB();
		
		Grafica.getIstance().crea();
		Grafica.getIstance().mostraLogin();		
		
		new Timer().schedule(new TimerTask() {public void run() {aggiornatore.run();}}, 0, 15000);
	}
	

//	METODI DI GESTIONE	
	
	
	private static void connettiDB()
	{
		db = new DataBase();		
		try {
			db.getConnection();
			db.initializeDatiRAM();
		} 
		catch(SQLException e) 
		{
			error_logger.scriviLog(Messaggi.E_DATABASE);
		}
	}
	
	private static void creaLogger() {
		String operatingSystem = System.getProperty("os.name").toLowerCase();
		
		if(operatingSystem.indexOf("linux") >= 0 || operatingSystem.indexOf("mac") >= 0) 
			{
				nome_file_log_sessione = "Dati//file_log//log_sessione.log";
				nome_file_error_sessione = "Dati//file_log//error_sessione.log";
			}
		else if(operatingSystem.indexOf("win") >= 0) 
		{
			nome_file_log_sessione = "Dati\\file_log\\log_sessione.log";
			nome_file_error_sessione = "Dati\\file_log\\error_sessione.log";
		}		
		logger = new Logger(nome_file_log_sessione);
		error_logger = new Logger(nome_file_error_sessione);
	}
	
	public static boolean accedi(Utente utente) {
		 try {
			 Utente u = db.existUtente(utente);
			 if(u != null) {
				 utente_corrente = u;
				 logger.scriviLog("Effettuato accesso da parte di " + utente.getNome());
				 return true;
			 }
			 else return false;
		 } 
		 catch(SQLException e) 
		 {
			 error_logger.scriviLog(String.format(Messaggi.E_ACCEDI, utente.getNome()));
		 }
		 return false;
	}
	
	public static Runnable aggiornatore = new Runnable() {
		public void run()
		{	
		try 
		{
			db.refreshDatiRAM();
			HashMap<CategorieEvento, ArrayList<Evento>> eventi = db.getEventi();			
//			primo for per ottenere tutte le chiavi, il secondo for per ottenere le LinkedList delle categorie di eventi
			for(CategorieEvento categoria : eventi.keySet())
			{
				for(Evento evento : eventi.get(categoria))
				{
					if(controllaStatoEvento(evento) == true)
					{	//Stato cambiato
						db.updateEvento(evento);
						switch(evento.getStato())
						{
						case FALLITA : db.segnalaFallimentoEvento(evento); break;
						case CHIUSA : db.segnalaChiusuraEvento(evento); break;
						case CONCLUSA : db.segnalaConclusioneEvento(evento); break;
						default : break;
						}
					}		
				}
			}
		} 
		catch(SQLException e) 
		{
			error_logger.scriviLog(Messaggi.E_AGGIORNATORE);
		}	
		}
};
	
	/**
	 * Questo metodo controlla e in caso sia cambiato aggiorna lo stato dell'evento, verificando se la data di chiusura iscrizioni ha superato la data odierna,
	 * se l'evento ha raggiunto la sua conclusione oppure se è concluso
	 * @return true se lo stato cambia
	 * @throws SQLException 
	 */
	public static boolean controllaStatoEvento(Evento evento) {
		Calendar oggi = Calendar.getInstance();
		boolean DataChiusuraIscrizioniNelFuturo = oggi.before((Calendar)evento.getCampo(NomeCampi.D_O_CHIUSURA_ISCRIZIONI).getContenuto());
		boolean termine_ritiro_scaduto = oggi.after((Calendar) evento.getCampo(NomeCampi.D_O_TERMINE_RITIRO_ISCRIZIONE).getContenuto());
		boolean DataFineEventoNelFuturo;		
		if (evento.getCampo(NomeCampi.D_O_TERMINE_EVENTO)==null) 
			DataFineEventoNelFuturo= oggi.before((Calendar)evento.getCampo(NomeCampi.D_O_INIZIO_EVENTO).getContenuto());		
		else 
			DataFineEventoNelFuturo = oggi.before((Calendar)evento.getCampo(NomeCampi.D_O_TERMINE_EVENTO).getContenuto());
		int numero_iscritti_attuali = evento.getNumeroPartecipanti();
		int numero_minimo_iscritti = (Integer)evento.getCampo(NomeCampi.PARTECIPANTI).getContenuto();
		int numero_massimo_iscritti_possibili = numero_minimo_iscritti + (Integer)evento.getCampo(NomeCampi.TOLLERANZA_MAX).getContenuto();
		
		StatoEvento statoEvento = evento.getStato();
		
		if(DataChiusuraIscrizioniNelFuturo == false && (statoEvento.getString().equals("Aperta")) && (numero_iscritti_attuali < numero_minimo_iscritti))
		{
			evento.setStato(StatoEvento.FALLITA);
			logger.scriviLog(String.format(Messaggi.APERTO_FALLITO, evento.getId()));
			return true;
		}
		else if(((DataChiusuraIscrizioniNelFuturo == false  && numero_iscritti_attuali > numero_minimo_iscritti) || (termine_ritiro_scaduto && numero_iscritti_attuali == numero_massimo_iscritti_possibili)) && (statoEvento.getString().equals("Aperta")))
		{
			evento.setStato(StatoEvento.CHIUSA);
			logger.scriviLog(String.format(Messaggi.APERTO_CHIUSO, evento.getId()));
			return true;
		}
		else if(DataFineEventoNelFuturo == false && (statoEvento.getString().equals("Chiusa")))
		{
			evento.setStato(StatoEvento.CONCLUSA);
			logger.scriviLog(String.format(Messaggi.CHIUSO_CONCLUSO, evento.getId()));
			return true;
		}
		return false;
	}
	
	
//	METODI SETTER
	
	
	public static boolean aggiungiEvento(Evento evento)
	{
		try
		{
			evento.setUtenteCreatore(utente_corrente);
			db.insertEvento(evento); 
			iscrizioneUtenteInEvento(evento);
			logger.scriviLog(String.format(Messaggi.VALIDO_APERTO, evento.getId()));
			db.segnalaNuovoEventoAgliInteressati(evento);
			return true;
		}
		catch(SQLException e) 
		{
			error_logger.scriviLog(String.format(Messaggi.E_INSERT_E,evento.getCampo(NomeCampi.TITOLO)));}
		return false;
	}
	
	public static boolean insertNotificaUtenteCorrente(Notifica notifica)
	{
		try
		{
			notifica = db.insertNotifica(notifica); 
			db.collegaUtenteNotifica(utente_corrente.getNome(), notifica.getIdNotifica());
			return true;
		}
		 catch(SQLException e) 
		 {
			 error_logger.scriviLog(String.format(Messaggi.E_COLLEGAMENTO_U_N, utente_corrente.getNome(), notifica.getIdNotifica()));
			 return false;
		 }
	}
	
	public static void notificaUtentePerEvento (Evento evento, Utente utente_destinatario) 
	{
		try 
		{
			db.segnalaEventoPerUtente(evento, utente_corrente, utente_destinatario);
		} 
		 catch(SQLException e) 
		 {
			 error_logger.scriviLog(String.format(Messaggi.E_COLLEGAMENTO_U_N_PER_E, utente_destinatario.getNome(), evento.getId()));
		 }
	}
	
	public static boolean insertUtente(Utente utente) 
	{
		try 
		{
			if(db.existUtente(utente) != null)
				return false;
			
			db.insertUtente(utente);
			utente_corrente = utente;
		} 
		 catch(SQLException e) 
		 {
			 error_logger.scriviLog(String.format(Messaggi.E_INSERT_U, utente.getNome()));
		 }
		return true;
	}
	
	public static boolean aggiungiInteresseUtenteCorrente(CategorieEvento nome_categoria) {
		try {
			if(utente_corrente.getCategorieInteressi().contains(nome_categoria)) return true;
			else {
				db.collegaUtenteCategoria(getUtente_corrente(), nome_categoria);
				utente_corrente.aggiungiInteresse(nome_categoria);
			}
		}
		 catch(SQLException e) 
		 {
			 error_logger.scriviLog(String.format(Messaggi.E_COLLEGAMENTO_U_C, utente_corrente.getNome(), nome_categoria.getString()));
			 return false;
		 }
		return true;
	}
	
	public static boolean insertNotifica(Notifica notifica, Utente utente)
	{
		try
		{
			notifica = db.insertNotifica(notifica); 
			db.collegaUtenteNotifica(utente.getNome(), notifica.getIdNotifica());
			return true;
		}
		 catch(SQLException e) 
		 {
			 error_logger.scriviLog(String.format(Messaggi.E_INSERT_U, notifica.getTitolo()));
			 return false;
		 }
	}
	
	public static void iscrizioneUtenteInEvento(Evento evento)
	{
		if(utente_corrente == null) return;
		try
		{
			if(utenteIscrittoInEvento(evento))
				return;
			int numero_iscritti_attuali = evento.getNumeroPartecipanti();
			int numero_massimo_iscritti_possibili = ((Integer)evento.getCampo(NomeCampi.PARTECIPANTI).getContenuto() + (Integer)evento.getCampo(NomeCampi.TOLLERANZA_MAX).getContenuto());
			Calendar termine_ritiro_iscrizioni = (Calendar) evento.getCampo(NomeCampi.D_O_TERMINE_RITIRO_ISCRIZIONE).getContenuto();
			boolean termine_ritiro_scaduto = Calendar.getInstance().compareTo(termine_ritiro_iscrizioni)>0;
			//se il giocatore occupa l'ultimo posto disponibile e il termine ritiro è scaduto allora si notificano gli altri giocatori che la partita è chiusa, ossia si farà
			if(numero_iscritti_attuali == numero_massimo_iscritti_possibili && termine_ritiro_scaduto)
			{
				db.collegaUtenteEvento(utente_corrente, evento);
				db.segnalaChiusuraEvento(evento);
				evento.setStato(StatoEvento.CHIUSA);
				db.updateEvento(evento);
				logger.scriviLog(String.format(Messaggi.APERTO_CHIUSO, evento.getId()));
			}
			else if (numero_iscritti_attuali < numero_massimo_iscritti_possibili || (!termine_ritiro_scaduto && numero_iscritti_attuali == numero_massimo_iscritti_possibili)) 
				db.collegaUtenteEvento(utente_corrente, evento);
			else return;
		} 
		 catch(Exception e) 
		 {
			 error_logger.scriviLog(String.format(Messaggi.E_COLLEGAMENTO_U_E, utente_corrente.getNome(), evento.getId()));
		 }
	}

	
//	METODI GETTER
	
	
	public static HashMap<CategorieEvento,ArrayList<Evento>> getEventi() {return db.getEventi();}
	
	public static LinkedList<Notifica> getNotificheUtente() 
	{
		LinkedList<Notifica> notifiche = null;
		try {
			notifiche = db.selectNotificheDiUtente(utente_corrente.getNome());
			utente_corrente.setNotifiche(notifiche);
		} 
		 catch(SQLException e) 
		 {
			 error_logger.scriviLog(String.format(Messaggi.E_GET_N, utente_corrente.getNome()));
		 }
		return notifiche;
	}
	
	public HashMap<CategorieEvento,LinkedList<Utente>> getPossibiliUtentiInteressati(Utente utente)
	{
		try {
			return db.selectUtentiDaEventiPassati(utente.getNome());
		} 
		 catch(SQLException e) 
		 {
			 error_logger.scriviLog(String.format(Messaggi.E_GET_POSSIBILI_U_INTERESSATI, utente.getNome()));
		 }
		return null;
	}
	
	public static LinkedList<Utente> getUtentiDaEventiPassati(CategorieEvento nome_categoria) {
		try {
			return db.selectUtentiDaEventiPassati(utente_corrente.getNome()).get(nome_categoria);
		} 
		 catch(SQLException e) 
		 {
			 error_logger.scriviLog(String.format(Messaggi.E_GET_POSSIBILI_U_INTERESSATI_A_C, utente_corrente.getNome(), nome_categoria.getString() ));
			 return new LinkedList<>();
		 }	
		}

	public static boolean utenteIscrittoInEvento(Evento evento)
	{
		if(utente_corrente == null)
			return false;
		
		try {
			return db.existUtenteInEvento(utente_corrente, evento);
		} 
		 catch(SQLException e) 
		 {
			 error_logger.scriviLog(String.format(Messaggi.E_GET_U_ISCRITTO_IN_E, utente_corrente.getNome(), evento.getId()));
		 }
		return false;
	}
	
	public static HashMap<CategorieEvento,LinkedList<Evento>> getEventiUtenteCorrente()
	{
		try {
			return db.selectEventiDiUtente(utente_corrente.getNome());
		} 
		 catch(SQLException e) 
		 {
			 error_logger.scriviLog(String.format(Messaggi.E_GET_E, utente_corrente.getNome()));
				return null;
		 }
	}
	

//	METODI DI AGGIORNAMENTO
	
	
	public static boolean updateFasciaEta(int eta_min, int eta_max)
	{
		try
		{
			utente_corrente.setEtaMax(eta_max);
			utente_corrente.setEtaMin(eta_min);
			db.updateEtaMaxtente(utente_corrente.getNome(), eta_max);
			db.updateEtaMinUtente(utente_corrente.getNome(), eta_min);
			db.refreshDatiRAM();
		}
		 catch(SQLException e) 
		 {
			 error_logger.scriviLog(String.format(Messaggi.E_UPDATE_U, utente_corrente.getNome()));
				return false;
		 }
		return true;
	}
	


//	METODI DI ELIMINAZIONE
	
	
	public static LinkedList<Notifica> eliminaNotificaUtente(Notifica notifica)
	{
		try 
		{
			db.deleteCollegamentoNotificaUtente(utente_corrente, notifica);
			utente_corrente.rimuoviNotifica(notifica);
		} 
		 catch(SQLException e) 
		 {
			 error_logger.scriviLog(String.format(Messaggi.E_DELETE_N, notifica.getIdNotifica(), utente_corrente.getNome()));
		 }
		
		return getNotificheUtente();
	}
	
	public static void disiscrizioneUtenteEvento(Evento evento) throws RuntimeException{
		if(utente_corrente == null) throw new RuntimeException("L'utente corrente è null");

		Calendar termine_ritiro_iscrizioni = (Calendar) evento.getCampo(NomeCampi.D_O_TERMINE_RITIRO_ISCRIZIONE).getContenuto();	
		boolean termine_ritiro_scaduto = Calendar.getInstance().compareTo(termine_ritiro_iscrizioni)>0;
		if(termine_ritiro_scaduto)
			throw new RuntimeException("L'iscrizione non può essere annullata a causa del superamento del termine della possibilità di ritiro");
		try 
		{
			if(!utenteIscrittoInEvento(evento)) throw new RuntimeException ("Utente non iscritto alla partita");	
			db.deleteCollegamentoEventoUtente(utente_corrente.getNome(), evento);
		} 
		catch(SQLException e) 
		{
			error_logger.scriviLog(String.format(Messaggi.E_DELETE_U_DA_E, utente_corrente.getNome(), evento.getId() ));
		}
	}

	public static boolean eliminaInteresseUtenteCorrente(CategorieEvento nome_categoria)
	{
		try {
			if(utente_corrente.getCategorieInteressi().contains(nome_categoria)) {
				db.deleteCollegamentoCategoriaUtente(utente_corrente.getNome(), nome_categoria);
				utente_corrente.rimuoviInteresse(nome_categoria);
			} else
				return true;
		}
		 catch(SQLException e) 
		 {
			 error_logger.scriviLog(String.format(Messaggi.E_DELETE_C_DA_U, utente_corrente.getNome(), nome_categoria.getString()));
			 return false;
		 }
		return true;
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
				db.updateEvento(evento); 
				db.segnalaRitiroEvento(evento);
				logger.scriviLog(String.format(Messaggi.APERTO_RITIRATO, evento.getId()));
			}
		}
		 catch(SQLException e) 
		 {
			 error_logger.scriviLog(String.format(Messaggi.E_DELETE_E, evento.getId()));
		 }
		}
}
