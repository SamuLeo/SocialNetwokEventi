package it.unibs.dii.isw.socialNetworkEventi.controller;

import java.sql.SQLException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.locks.ReentrantLock;
import java.util.ArrayList;

import it.unibs.dii.isw.socialNetworkEventi.model.*;
import it.unibs.dii.isw.socialNetworkEventi.utility.*;
import it.unibs.dii.isw.socialNetworkEventi.view.Grafica;

public class Sessione 
{
	
	private  static Sessione sessione;
	private  Utente utente_corrente;
	public  void setUtente_corrente(Utente utente_corrente) {this.utente_corrente = utente_corrente;}
	public  Utente getUtente_corrente() {return utente_corrente;}
	private  DataBase db;
	
	private  String nome_file_log_sessione ;
	private  Logger logger;
	private  String nome_file_error_sessione;
	private  Logger error_logger;
	
	
	public static Sessione getInstance()
	{
		ReentrantLock lock = new ReentrantLock();
		
		if(sessione == null)
		{
			lock.lock();
			sessione = new Sessione();
			lock.unlock();
		}
		return sessione;	
	}
	
	private Sessione()
	{
		connettiDB();
		creaLogger();
		
		new Timer().schedule(new TimerTask() {public void run() {aggiornatore.run();}}, 0, 15000);

	}

//	METODI DI GESTIONE	
	
	private void connettiDB()
	{
		try {
			db = DataBase.getInstance();
			db.initializeDatiRAM();
		} 
		catch(SQLException e) 
		{
			error_logger.scriviLog(Messaggi.E_DATABASE);
		}
	}
	
	private void creaLogger() {
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

	

	
	public boolean accedi(Utente utente) {
		 try 
		 {
			 Utente u = db.existUtente(utente);
			 if(u != null) 
			 {
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
	
	public Runnable aggiornatore = new Runnable() {
		public void run()
		{	
		try 
		{
			db.refreshDatiRAM();
			HashMap<CategoriaEvento, ArrayList<Evento>> eventi = db.getEventi();			
//			primo for per ottenere tutte le chiavi, il secondo for per ottenere le LinkedList delle categorie di eventi
			for(CategoriaEvento categoria : eventi.keySet())
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
	private boolean controllaStatoEvento(Evento evento) {
		Calendar oggi = Calendar.getInstance();
		boolean DataChiusuraIscrizioniNelFuturo = oggi.before((Calendar)evento.getCampo(NomeCampi.D_O_CHIUSURA_ISCRIZIONI).getContenuto());
		boolean termine_ritiro_scaduto = oggi.after((Calendar) evento.getCampo(NomeCampi.D_O_TERMINE_RITIRO_ISCRIZIONE).getContenuto());
		boolean DataFineEventoNelFuturo;		
		if (evento.getCampo(NomeCampi.D_O_TERMINE_EVENTO)==null) 
		{
			Calendar giorno_dopo_inizio_evento = ((Calendar)evento.getContenutoCampo(NomeCampi.D_O_INIZIO_EVENTO));
			giorno_dopo_inizio_evento.add(Calendar.DAY_OF_YEAR,1);
			DataFineEventoNelFuturo= oggi.before(giorno_dopo_inizio_evento);		
		}
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
	
	
	public Evento aggiungiEvento(Evento evento)
	{
		try
		{
			evento.setUtenteCreatore(utente_corrente);
			evento = db.insertEvento(evento); 
			iscrizioneUtenteInEvento(evento);
			logger.scriviLog(String.format(Messaggi.VALIDO_APERTO, evento.getId()));
			db.segnalaNuovoEventoAgliInteressati(evento);
			return evento;
		}
		catch(SQLException e) 
		{error_logger.scriviLog(String.format(Messaggi.E_INSERT_E,evento.getCampo(NomeCampi.TITOLO)));}
		return null;
	}
	
	public boolean insertNotificaUtenteCorrente(Notifica notifica)
	{
		try
		{
			notifica = db.insertNotifica(notifica); 
			db.collegaUtenteNotifica(utente_corrente.getNome(), notifica.getIdNotifica());
			utente_corrente = db.selectUtente(utente_corrente.getNome());
			return true;
		}
		 catch(SQLException e) 
		 {
			 error_logger.scriviLog(String.format(Messaggi.E_COLLEGAMENTO_U_N, utente_corrente.getNome(), notifica.getIdNotifica()));
			 return false;
		 }
	}
	
	public void notificaUtentePerEvento (Evento evento, Utente utente_destinatario) 
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
	
	public boolean insertUtente(Utente utente) 
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
	
	public boolean aggiungiInteresseUtenteCorrente(CategoriaEvento nome_categoria) {
		try {
			if(utente_corrente.getCategorieInteressi().contains(nome_categoria)) return true;
			else {
				db.collegaUtenteCategoria(getUtente_corrente(), nome_categoria);
				utente_corrente.aggiungiInteresse(nome_categoria);
			}
			utente_corrente = db.selectUtente(utente_corrente.getNome());
		}
		 catch(SQLException e) 
		 {
			 error_logger.scriviLog(String.format(Messaggi.E_COLLEGAMENTO_U_C, utente_corrente.getNome(), nome_categoria.getString()));
			 return false;
		 }
		return true;
	}
	
	public boolean insertNotifica(Notifica notifica, Utente utente)
	{
		try
		{
			notifica = db.insertNotifica(notifica); 
			db.collegaUtenteNotifica(utente.getNome(), notifica.getIdNotifica());
			utente_corrente = db.selectUtente(utente_corrente.getNome());
			return true;
		}
		 catch(SQLException e) 
		 {
			 error_logger.scriviLog(String.format(Messaggi.E_INSERT_U, notifica.getTitolo()));
			 return false;
		 }
	}
	
	public void iscrizioneUtenteInEvento(Evento evento)
	{
		if(utente_corrente == null) return;
		try
		{
			if(utenteIscrittoInEvento(evento))
				return;
			int numero_iscritti_attuali = evento.getNumeroPartecipanti();
			int numero_massimo_iscritti_possibili = ((Integer)evento.getContenutoCampo(NomeCampi.PARTECIPANTI) + (Integer)evento.getCampo(NomeCampi.TOLLERANZA_MAX).getContenuto());
			Calendar termine_ritiro_iscrizioni = (Calendar) evento.getContenutoCampo(NomeCampi.D_O_TERMINE_RITIRO_ISCRIZIONE);
			boolean termine_ritiro_scaduto = Calendar.getInstance().compareTo(termine_ritiro_iscrizioni)>0;
			Calendar chiusura_iscrizioni = (Calendar) evento.getContenutoCampo(NomeCampi.D_O_CHIUSURA_ISCRIZIONI);
			boolean chiusura_iscrizioni_superato = Calendar.getInstance().compareTo(chiusura_iscrizioni)>0;
			//se il giocatore occupa l'ultimo posto disponibile e il termine ritiro è scaduto allora si notificano gli altri giocatori che la partita è chiusa, ossia si farà
			if(numero_iscritti_attuali == (numero_massimo_iscritti_possibili-1) && termine_ritiro_scaduto)
			{
				db.collegaUtenteEvento(utente_corrente, evento);
				db.segnalaChiusuraEvento(evento);
				evento.setStato(StatoEvento.CHIUSA);
				db.updateEvento(evento);
				logger.scriviLog(String.format(Messaggi.APERTO_CHIUSO, evento.getId()));
			}
			else if ((numero_iscritti_attuali < numero_massimo_iscritti_possibili && !chiusura_iscrizioni_superato)) 
					db.collegaUtenteEvento(utente_corrente, evento);
			else return;
			db.refreshDatiRAM();
			utente_corrente = db.selectUtente(utente_corrente.getNome());
		} 
		 catch(Exception e) 
		 {
			 error_logger.scriviLog(String.format(Messaggi.E_COLLEGAMENTO_U_E, utente_corrente.getNome(), evento.getId()));
		 }
		
	}

	
//	METODI GETTER
	
	
	public HashMap<CategoriaEvento,ArrayList<Evento>> getEventi() {return db.getEventi();}
	
	public LinkedList<Notifica> getNotificheUtente() 
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
	
	public HashMap<CategoriaEvento,LinkedList<Utente>> getPossibiliUtentiInteressati(Utente utente)
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
	
	public LinkedList<Utente> getUtentiDaEventiPassati(CategoriaEvento nome_categoria) {
		try {
			return db.selectUtentiDaEventiPassati(utente_corrente.getNome()).get(nome_categoria);
		} 
		 catch(SQLException e) 
		 {
			 error_logger.scriviLog(String.format(Messaggi.E_GET_POSSIBILI_U_INTERESSATI_A_C, utente_corrente.getNome(), nome_categoria.getString() ));
			 return new LinkedList<>();
		 }	
		}

	public boolean utenteIscrittoInEvento(Evento evento)
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
	
	public HashMap<CategoriaEvento,LinkedList<Evento>> getEventiUtenteCorrente()
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
	
	
	public boolean updateFasciaEta(int eta_min, int eta_max)
	{
		try
		{
			utente_corrente.setEtaMax(eta_max);
			utente_corrente.setEtaMin(eta_min);
			db.updateEtaMaxtente(utente_corrente.getNome(), eta_max);
			db.updateEtaMinUtente(utente_corrente.getNome(), eta_min);
			db.refreshDatiRAM();
			utente_corrente = db.selectUtente(utente_corrente.getNome());
		}
		 catch(SQLException e) 
		 {
			 error_logger.scriviLog(String.format(Messaggi.E_UPDATE_U, utente_corrente.getNome()));
				return false;
		 }
		return true;
	}
	


//	METODI DI ELIMINAZIONE
	
	
	public LinkedList<Notifica> eliminaNotificaUtente(Notifica notifica)
	{
		try 
		{
			db.deleteCollegamentoNotificaUtente(utente_corrente, notifica);
			utente_corrente = db.selectUtente(utente_corrente.getNome());
		} 
		 catch(SQLException e) 
		 {
			 error_logger.scriviLog(String.format(Messaggi.E_DELETE_N, notifica.getIdNotifica(), utente_corrente.getNome()));
		 }
		
		return getNotificheUtente();
	}
	
	public boolean deleteNotificheUtenteAll()
	{
		for(Notifica notifica : utente_corrente.getNotifiche())
		{
			eliminaNotificaUtente(notifica);
		}
		if(utente_corrente.getNotifiche().isEmpty()) 
			return true;
		else
			return false;
	}
	
	public void disiscrizioneUtenteEvento(Evento evento) throws RuntimeException{
		if(utente_corrente == null) throw new RuntimeException("L'utente corrente è null");

		Calendar termine_ritiro_iscrizioni = (Calendar) evento.getCampo(NomeCampi.D_O_TERMINE_RITIRO_ISCRIZIONE).getContenuto();	
		boolean termine_ritiro_scaduto = Calendar.getInstance().compareTo(termine_ritiro_iscrizioni)>0;
		if(termine_ritiro_scaduto)
			throw new RuntimeException("L'iscrizione non può essere annullata a causa del superamento del termine della possibilità di ritiro");
		try 
		{
			if(!utenteIscrittoInEvento(evento)) throw new RuntimeException ("Utente non iscritto alla partita");	
			db.deleteCollegamentoEventoUtente(utente_corrente.getNome(), evento);
			utente_corrente = db.selectUtente(utente_corrente.getNome());
		} 
		catch(SQLException e) 
		{
			error_logger.scriviLog(String.format(Messaggi.E_DELETE_U_DA_E, utente_corrente.getNome(), evento.getId() ));
		}
	}

	public boolean eliminaInteresseUtenteCorrente(CategoriaEvento nome_categoria)
	{
		try {
			if(utente_corrente.getCategorieInteressi().contains(nome_categoria)) {
				db.deleteCollegamentoCategoriaUtente(utente_corrente.getNome(), nome_categoria);
				utente_corrente.rimuoviInteresse(nome_categoria);
				
			} else
				return true;
			utente_corrente = db.selectUtente(utente_corrente.getNome());
		}
		 catch(SQLException e) 
		 {
			 error_logger.scriviLog(String.format(Messaggi.E_DELETE_C_DA_U, utente_corrente.getNome(), nome_categoria.getString()));
			 return false;
		 }
		return true;
	}
	
	public void deleteEvento(Evento evento) throws RuntimeException
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
	
	public boolean deleteEventiDiUtente() throws RuntimeException
	{
		try
		{
			db.deleteEventiDiUtente(utente_corrente);
			return true;
		}
		 catch(SQLException e) 
		 {
			 error_logger.scriviLog(String.format(Messaggi.E_DELETE_E_ALL_U, utente_corrente.getNome()));
			 return false;
		 }
	}
	
	public DataBase getDb() {
		return db;
	}
	public void setDb(DataBase db) {
		this.db = db;
	}
	
}
