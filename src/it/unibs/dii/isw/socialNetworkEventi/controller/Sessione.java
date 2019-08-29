package it.unibs.dii.isw.socialNetworkEventi.controller;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Observable;
import java.util.Observer;
import java.util.Timer;
import java.util.TimerTask;
import java.util.ArrayList;

import it.unibs.dii.isw.socialNetworkEventi.model.*;
import it.unibs.dii.isw.socialNetworkEventi.utility.*;

public class Sessione implements IController
{
	private Utente utente_corrente;
	public Utente getUtente_corrente() {return utente_corrente;}
	
	private IPersistentStorageRepository db;
	public IPersistentStorageRepository getDb() {return db;}
	
	private IPureFabricationNotifiche messagesFactory;
	public IPureFabricationNotifiche getMessagesFactory() {return messagesFactory;}
	
	private Logger logger;
	private String percorso_file_log_sessione ;
	private Logger error_logger;
	private String percorso_file_error_sessione;
	
	public Sessione()
	{
		try 
		{configuraPercorsiFileLogger();} 
		catch (IOException e) 
		{e.printStackTrace();}
		
		creaLogger();
		connettiDB();
		initIPureFabricationNotifiche();
		
		new Timer().schedule(new TimerTask() {public void run() {aggiornatore.run();}}, 0, 1000);
	}
	
	
	private void connettiDB()
	{
		try 
		{
			String className = System.getProperty("social_network.db.class.name");
			db = (IPersistentStorageRepository)Class.forName(className).newInstance();
			db.initializeDatiRAM();
		} 
		catch(SQLException | InstantiationException | IllegalAccessException | ClassNotFoundException e) 
		{
			error_logger.scriviLog(Stringhe.E_DATABASE);
		}
	}
	
	
	private void initIPureFabricationNotifiche()
	{
		try 
		{
			String className = System.getProperty("social_network.messages_factory.class.name");
			messagesFactory = (IPureFabricationNotifiche)Class.forName(className).newInstance();
			messagesFactory.setDB(db);
		} 
		catch(InstantiationException | IllegalAccessException | ClassNotFoundException e) 
		{
			error_logger.scriviLog(Stringhe.E_MSG_FACTORY);
		}		
	}
	
	
	public boolean accedi(Utente utente) {
		 Utente u;
		try 
		{
			u = db.existUtente(utente);
			 if(u != null) {
				 utente_corrente = u;
				 logger.scriviLog("Effettuato accesso da parte di " + utente.getNome());
				 return true;
			 }
			 else return false;
		} 
		catch (SQLException e) 
		{
			error_logger.scriviLog(Stringhe.E_ACCEDI);			
		}
		return false;
	}
	
	private final Runnable aggiornatore = new Runnable() {
		public void run()
			{	
			try 
			{
				db.refreshDatiRAM();
				HashMap<CategoriaEvento, ArrayList<Evento>> eventi = db.getEventi();			
				for(CategoriaEvento categoria : eventi.keySet())
				{
					for(Evento evento : eventi.get(categoria))
					{
						if(evento.controllaStatoEvento() == true)
						{	//Stato cambiato
							db.updateEvento(evento);
							switch(evento.getStato())
							{
								case FALLITA : 
									logger.scriviLog(String.format(Stringhe.APERTO_FALLITO, evento.getId()));
									messagesFactory.segnalaFallimentoEvento(evento); 
									break;
								case CHIUSA : 
									logger.scriviLog(String.format(Stringhe.APERTO_CHIUSO, evento.getId()));
									messagesFactory.segnalaChiusuraEvento(evento);
									break;
								case CONCLUSA :
									logger.scriviLog(String.format(Stringhe.CHIUSO_CONCLUSO, evento.getId()));
									messagesFactory.segnalaConclusioneEvento(evento);
									break;
								default : break;
							}
						}		
					}
				}
				notificaOsservatori();
			} 
			catch(SQLException e) 
			{
				error_logger.scriviLog(Stringhe.E_AGGIORNATORE);
			}	
		}
	};

	public void aggiorna() {aggiornatore.run();}
	
	
//	METODI SETTER

	
	public Evento aggiungiEvento(Evento evento)
	{
		try
		{
			evento.setUtenteCreatore(utente_corrente);
			int id_evento = db.insertEvento(evento).getId();
			db.collegaUtenteEvento(utente_corrente, evento);
			evento = db.selectEvento(id_evento);
			logger.scriviLog(String.format(Stringhe.VALIDO_APERTO, evento.getId()));
			messagesFactory.segnalaNuovoEventoAgliInteressati(evento);
			
			return evento;
		}
		catch(Exception e) 
		{error_logger.scriviLog(String.format(Stringhe.E_INSERT_E,evento.getCampo(NomeCampo.TITOLO).getContenuto()));}
		return null;
	}
	
	public boolean insertNotificaUtenteCorrente(Notifica notifica)
	{
		try
		{
			notifica = db.insertNotifica(notifica); 
			db.collegaUtenteNotifica(utente_corrente.getNome(), notifica.getIdNotifica());
			return true;
		}
		 catch(SQLException e) 
		 {
			 error_logger.scriviLog(String.format(Stringhe.E_COLLEGAMENTO_U_N, utente_corrente.getNome(), notifica.getIdNotifica()));
			 return false;
		 }
	}
	
	public void notificaUtentePerEvento (Evento evento, Utente utente_destinatario) 
	{
		try 
		{
			messagesFactory.segnalaEventoPerUtente(evento, utente_corrente, utente_destinatario);
		} 
		catch(SQLException e) 
		{
			error_logger.scriviLog(String.format(Stringhe.E_COLLEGAMENTO_U_N_PER_E, utente_destinatario.getNome(), evento.getId()));
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
			error_logger.scriviLog(String.format(Stringhe.E_INSERT_U, utente.getNome()));
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
		}
		catch(SQLException e) 
		{
			error_logger.scriviLog(String.format(Stringhe.E_COLLEGAMENTO_U_C, utente_corrente.getNome(), nome_categoria.getString()));
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
			return true;
		}
		catch(SQLException e) 
		{
			error_logger.scriviLog(String.format(Stringhe.E_INSERT_U, notifica.getTitolo()));
			return false;
		}
	}
	
	public void iscrizioneUtenteInEvento(Evento evento)
	{
		if(utente_corrente == null) return;
		try
		{
			if(utenteIscrittoInEvento(evento)) return;
			int numero_iscritti_attuali = evento.getNumeroPartecipanti();
			int tolleranza;
			if (evento.getCampo(NomeCampo.TOLLERANZA_MAX) == null) tolleranza = 0; else tolleranza = (Integer)evento.getContenutoCampo(NomeCampo.TOLLERANZA_MAX);
			int numero_massimo_iscritti_possibili = (Integer)evento.getContenutoCampo(NomeCampo.PARTECIPANTI) + tolleranza;
			Calendar termine_ritiro_iscrizioni = (Calendar) evento.getCampo(NomeCampo.D_O_TERMINE_RITIRO_ISCRIZIONE).getContenuto();
			boolean termine_ritiro_scaduto = Calendar.getInstance().compareTo(termine_ritiro_iscrizioni)>0;
			Calendar chiusura_iscrizioni = (Calendar) evento.getContenutoCampo(NomeCampo.D_O_CHIUSURA_ISCRIZIONI);
			boolean chiusura_iscrizioni_superato = Calendar.getInstance().compareTo(chiusura_iscrizioni)>0;
			//se il giocatore occupa l'ultimo posto disponibile e il termine ritiro è scaduto allora si notificano gli altri giocatori che la partita è chiusa, ossia si farà
			if(numero_iscritti_attuali == numero_massimo_iscritti_possibili && termine_ritiro_scaduto)
			{
				db.collegaUtenteEvento(utente_corrente, evento);
				evento = db.selectEvento(evento.getId());
				messagesFactory.segnalaChiusuraEvento(evento);
				evento.setStato(StatoEvento.CHIUSA);
				db.updateEvento(evento);
				logger.scriviLog(String.format(Stringhe.APERTO_CHIUSO, evento.getId()));
			}
			else if ((numero_iscritti_attuali <= numero_massimo_iscritti_possibili && !chiusura_iscrizioni_superato)) 
				db.collegaUtenteEvento(utente_corrente, evento);	
			else return;
			utente_corrente = db.selectUtente(utente_corrente.getNome());
		} 
		catch(Exception e) 
		{
			e.printStackTrace();
			error_logger.scriviLog(String.format(Stringhe.E_COLLEGAMENTO_U_E, utente_corrente.getNome(), evento.getId()));
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
			error_logger.scriviLog(String.format(Stringhe.E_GET_N, utente_corrente.getNome()));
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
			error_logger.scriviLog(String.format(Stringhe.E_GET_POSSIBILI_U_INTERESSATI, utente.getNome()));
		}
		return null;
	}
	
	public LinkedList<Utente> getUtentiDaEventiPassati(CategoriaEvento nome_categoria) {
		try {
			return db.selectUtentiDaEventiPassati(utente_corrente.getNome()).get(nome_categoria);
		} 
		catch(SQLException e) 
		{
			 error_logger.scriviLog(String.format(Stringhe.E_GET_POSSIBILI_U_INTERESSATI_A_C, utente_corrente.getNome(), nome_categoria.getString() ));
			 return new LinkedList<>();
		}	
	}

	public  boolean utenteIscrittoInEvento(Evento evento)
	{
		if(utente_corrente == null) return false;
		try {
			return db.existUtenteInEvento(utente_corrente, evento);
		} 
		catch(SQLException e) 
		{
			error_logger.scriviLog(String.format(Stringhe.E_GET_U_ISCRITTO_IN_E, utente_corrente.getNome(), evento.getId()));
		}
		return false;
	}
	
	public  HashMap<CategoriaEvento,LinkedList<Evento>> getEventiUtenteCorrente()
	{
		try {
			return db.selectEventiDiUtente(utente_corrente.getNome());
		} 
		catch(SQLException e) 
		{
			error_logger.scriviLog(String.format(Stringhe.E_GET_E, utente_corrente.getNome()));
			return null;
		}
	}
	

//	METODI DI AGGIORNAMENTO
	
	
	public  boolean updateFasciaEta(int eta_min, int eta_max)
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
			error_logger.scriviLog(String.format(Stringhe.E_UPDATE_U, utente_corrente.getNome()));
			return false;
		}
		return true;
	}
	


//	METODI DI ELIMINAZIONE
	
	public  LinkedList<Notifica> eliminaNotificaUtente(Notifica notifica)
	{
		try 
		{
			db.deleteCollegamentoNotificaUtente(utente_corrente, notifica);
			utente_corrente.rimuoviNotifica(notifica);
		} 
		catch(SQLException e) 
		{
			error_logger.scriviLog(String.format(Stringhe.E_DELETE_N, notifica.getIdNotifica(), utente_corrente.getNome()));
		}
		return getNotificheUtente();
	}
	
	public  void disiscrizioneUtenteEvento(Evento evento) throws RuntimeException{
		if(utente_corrente == null) throw new RuntimeException("L'utente corrente è null");

		Calendar termine_ritiro_iscrizioni = (Calendar) evento.getCampo(NomeCampo.D_O_TERMINE_RITIRO_ISCRIZIONE).getContenuto();	
		boolean termine_ritiro_scaduto = Calendar.getInstance().compareTo(termine_ritiro_iscrizioni)>0;
		if(termine_ritiro_scaduto) throw new RuntimeException("L'iscrizione non può essere annullata a causa del superamento del termine della possibilità di ritiro");
		try 
		{
			if(!utenteIscrittoInEvento(evento)) throw new RuntimeException ("Utente non iscritto alla partita");	
			db.deleteCollegamentoEventoUtente(utente_corrente.getNome(), evento);
		} 
		catch(SQLException e) 
		{
			error_logger.scriviLog(String.format(Stringhe.E_DELETE_U_DA_E, utente_corrente.getNome(), evento.getId() ));
		}
	}

	public  boolean eliminaInteresseUtenteCorrente(CategoriaEvento nome_categoria)
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
			error_logger.scriviLog(String.format(Stringhe.E_DELETE_C_DA_U, utente_corrente.getNome(), nome_categoria.getString()));
			return false;
		}
		return true;
	}
	
	public  void deleteEvento(Evento evento) throws RuntimeException
	{
		try
		{
			Calendar termine_ritiro_iscrizioni = (Calendar) evento.getCampo(NomeCampo.D_O_TERMINE_RITIRO_ISCRIZIONE).getContenuto();	
			boolean termine_ritiro_scaduto = Calendar.getInstance().compareTo(termine_ritiro_iscrizioni)>0;
			if(termine_ritiro_scaduto)
				throw new RuntimeException("L'evento non può essere annullato a causa del superamento della data massima per poter effettuare questa operazione");
			else
			{
				evento.setStato(StatoEvento.RITIRATA);
				db.updateEvento(evento); 
				messagesFactory.segnalaRitiroEvento(evento);
				logger.scriviLog(String.format(Stringhe.APERTO_RITIRATO, evento.getId()));
			}
		}
		catch(SQLException e) 
		{
			error_logger.scriviLog(String.format(Stringhe.E_DELETE_E, evento.getId()));
		}
	}
	
	public boolean deleteNotificheUtenteAll()
	{
		for(Notifica notifica : utente_corrente.getNotifiche())
			eliminaNotificaUtente(notifica);
		if(utente_corrente.getNotifiche().isEmpty()) return true;
		else return false;
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
			error_logger.scriviLog(String.format(Stringhe.E_DELETE_E_ALL_U, utente_corrente.getNome()));
			return false;
		}
	}
	

	public void iniziaOsservazione (Observer obs) {
		((Observable)db).addObserver(obs);
	}
	
	public void fermaOsservazione (Observer obs) {
		((Observable)db).deleteObserver(obs);
	}
	
	public void notificaOsservatori() {
		//getDb().setChangedForObservers();
		((Observable)db).notifyObservers(getEventi());
	}
	
	
	//LOGGER	
	
	private void creaLogger() 
	{		
		try {
			logger = new Logger(percorso_file_log_sessione);
			error_logger = new Logger(percorso_file_error_sessione);
		} catch (UnsupportedEncodingException | FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	private void configuraPercorsiFileLogger() throws FileNotFoundException, IOException
	{
		String operatingSystem = System.getProperty("os.name").toLowerCase();
		if(operatingSystem.indexOf("linux") >= 0 || operatingSystem.indexOf("mac") >= 0) 
		{
			percorso_file_log_sessione = Stringhe.PERCORSO_FILE_LOG_LINUX;
			percorso_file_error_sessione = Stringhe.PERCORSO_FILE__ERROR_LOG_LINUX;
			System.getProperties().load(new FileInputStream(Stringhe.PERCORSO_FILE_CONFIG_LINUX));
		}
		else if(operatingSystem.indexOf("win") >= 0) 
		{
			percorso_file_log_sessione = Stringhe.PERCORSO_FILE_LOG_WIN;
			percorso_file_error_sessione = Stringhe.PERCORSO_FILE_ERROR_LOG_WIN;
			System.getProperties().load(new FileInputStream(Stringhe.PERCORSO_FILE_CONFIG_WIN));
		}
	}
}