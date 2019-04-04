package it.unibs.dii.isw.socialNetworkEventi.controller;

import java.sql.SQLException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.ArrayList;

import it.unibs.dii.isw.socialNetworkEventi.model.*;
import it.unibs.dii.isw.socialNetworkEventi.utility.CategorieEvento;
import it.unibs.dii.isw.socialNetworkEventi.utility.Logger;
import it.unibs.dii.isw.socialNetworkEventi.utility.MsgLog;
import it.unibs.dii.isw.socialNetworkEventi.utility.NomeCampi;
import it.unibs.dii.isw.socialNetworkEventi.utility.StatoEvento;
import it.unibs.dii.isw.socialNetworkEventi.view.Grafica;

public class Sessione 
{

	
	private static Utente utente_corrente;
	public static void setUtente_corrente(Utente utente_corrente) {Sessione.utente_corrente = utente_corrente;}
	public static Utente getUtente_corrente() {return utente_corrente;}
	private static DataBase db;
	
	private static  String nome_file_log_sessione ;
	private static Logger logger;
	private static  String nome_file_error_sessione;
	private static Logger error_logger;
	
	public static void main(String[] args) throws SQLException 
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
		try {
			db.getConnection();
			db.refreshDatiRAM();
		} catch(SQLException e) {e.printStackTrace();}
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
		catch (SQLException e) {e.printStackTrace();}
		return false;
	}
	
	public static Runnable aggiornatore = new Runnable() {
		public void run(){	
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
						db.updateStatoPartitaCalcio(evento);
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
		catch (SQLException e)  {e.printStackTrace();}
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
			logger.scriviLog(String.format(MsgLog.APERTO_FALLITO, evento.getId()));
			return true;
		}
		else if(((DataChiusuraIscrizioniNelFuturo == false  && numero_iscritti_attuali > numero_minimo_iscritti) || (termine_ritiro_scaduto && numero_iscritti_attuali == numero_massimo_iscritti_possibili)) && (statoEvento.getString().equals("Aperta")))
		{
			evento.setStato(StatoEvento.CHIUSA);
			logger.scriviLog(String.format(MsgLog.APERTO_CHIUSO, evento.getId()));
			return true;
		}
		else if(DataFineEventoNelFuturo == false && (statoEvento.getString().equals("Chiusa")))
		{
			evento.setStato(StatoEvento.CONCLUSA);
			logger.scriviLog(String.format(MsgLog.CHIUSO_CONCLUSO, evento.getId()));
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
			logger.scriviLog(String.format(MsgLog.VALIDO_APERTO, evento.getId()));
			db.segnalaNuovoEventoAgliInteressati(evento);
			return true;
		}
		catch(SQLException e) {
			e.printStackTrace();
			error_logger.scriviLog(String.format(MsgLog.E_AGGIUNTA_EVENTO,evento.getCampo(NomeCampi.TITOLO)));}
		catch(Exception e) {e.printStackTrace();}
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
		catch (SQLException e)
		{
			e.printStackTrace();
			return false;
		}
	}
	
	public static void notificaUtentePerEvento (Evento evento, Utente utente_destinatario) 
	{
		try 
		{
			db.segnalaEventoPerUtente(evento, utente_corrente, utente_destinatario);
		} 
		catch (SQLException exc) 
		{exc.printStackTrace();}
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
			e.printStackTrace();
//			JOptionPane.showMessageDialog(null, e.getMessage(), "Errore compilazione", JOptionPane.INFORMATION_MESSAGE);
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
			e.printStackTrace();
			return false;
		}
		return true;
	}


	
	public static boolean eliminaInteresseUtenteCorrente(CategorieEvento nome_categoria)
	{
		try {
			if(utente_corrente.getCategorieInteressi().contains(nome_categoria)) {
				db.deleteCollegamentoCategoriaUtente(getUtente_corrente().getId_utente(), nome_categoria);
				utente_corrente.rimuoviInteresse(nome_categoria);
			} else
				return true;
		}
		catch(SQLException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	public static boolean updateFasciaEta(int eta_min, int eta_max)
	{
		try
		{
			db.updateEtaMinUtente(utente_corrente.getId_utente(), eta_min);
			db.updateEtaMaxtente(utente_corrente.getId_utente(), eta_max);
			utente_corrente.setEtaMin(eta_min);
			utente_corrente.setEtaMax(eta_max);
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
			return;
		}
		
		switch(evento.getNomeCategoria()) {
		case PARTITA_CALCIO : 
			{
				PartitaCalcio partita = (PartitaCalcio)evento;
				try
				{
					if(utenteIscrittoInEvento(partita))
					{
						return;
					}	
					int numero_iscritti_attuali = db.getNumeroUtentiDiEvento(partita);
					int numero_massimo_iscritti_possibili = ((Integer)partita.getCampo(NomeCampi.PARTECIPANTI).getContenuto() + (Integer)partita.getCampo(NomeCampi.TOLLERANZA_MAX).getContenuto());
					Calendar termine_ritiro_iscrizioni = (Calendar) evento.getCampo(NomeCampi.D_O_TERMINE_RITIRO_ISCRIZIONE).getContenuto();
					boolean termine_ritiro_scaduto = Calendar.getInstance().compareTo(termine_ritiro_iscrizioni)>0;
					//se il giocatore occupa l'ultimo posto disponibile e il termine ritiro è scaduto allora si notificano gli altri giocatori che la partita è chiusa, ossia si farà
					if((numero_iscritti_attuali == (numero_massimo_iscritti_possibili-1)) && termine_ritiro_scaduto)
					{
						db.collegaUtenteEvento(utente_corrente, partita);
						db.segnalaChiusuraEvento(partita);
						partita.setStato(StatoEvento.CHIUSA);
						db.updateStatoPartitaCalcio(partita);
						logger.scriviLog(String.format(MsgLog.APERTO_CHIUSO, evento.getId()));
					}
					else if (numero_iscritti_attuali < numero_massimo_iscritti_possibili)
						db.collegaUtenteEvento(utente_corrente, partita);
					else
						return;
				} 
				catch (SQLException e) 
				{
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
				logger.scriviLog(String.format(MsgLog.APERTO_RITIRATO, evento.getId()));
			}
		}
		catch(SQLException e)
		{throw new RuntimeException("L'evento non può essere annullato a causa del superamento della data massima per poter effettuare questa operazione");}
	}
	
	
	public static HashMap<CategorieEvento,ArrayList<Evento>> getEventi() {return db.getEventi();}
	
	
	public static LinkedList<Notifica> getNotificheUtente() 
	{
		LinkedList<Notifica> notifiche = null;
		try {
			notifiche = db.selectNotificheDiUtente(utente_corrente.getId_utente());
			utente_corrente.setNotifiche(notifiche);
		} 
		catch (SQLException e) {
			e.printStackTrace();
		}
		return notifiche;
	}
	
	public HashMap<CategorieEvento,LinkedList<Utente>> getPossibiliUtentiInteressati(Utente utente)
	{
		try {
			return db.selectUtentiDaEventiPassati(utente.getId_utente());
		} catch (SQLException e) {
			error_logger.scriviLog(MsgLog.E_RICERCA_POSSIBILI_U_INTERESSATI);
			e.printStackTrace();
		}
		return null;
	}
	
	public static LinkedList<Utente> getUtentiDaEventiPassati(CategorieEvento nomeCategoria) {
		try {
			return db.selectUtentiDaEventiPassati(utente_corrente.getId_utente()).get(nomeCategoria);
		} catch (SQLException e) {e.printStackTrace(); return new LinkedList<>();}
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
		}
		
		return getNotificheUtente();
	}
	
	public static void disiscrizioneUtenteEvento(Evento evento) throws RuntimeException{
		if(utente_corrente == null) throw new RuntimeException("L'utente corrente è null");
		
		Calendar termine_ritiro_iscrizioni = (Calendar) evento.getCampo(NomeCampi.D_O_TERMINE_RITIRO_ISCRIZIONE).getContenuto();	
		boolean termine_ritiro_scaduto = Calendar.getInstance().compareTo(termine_ritiro_iscrizioni)>0;
		if(termine_ritiro_scaduto)
			throw new RuntimeException("L'iscrizione non può essere annullata a causa del superamento del termine della possibilità di ritiro");
		
		switch(evento.getNomeCategoria()) 
		{
			case PARTITA_CALCIO : 
				try 
				{
					PartitaCalcio partita = (PartitaCalcio)evento;
					if(!utenteIscrittoInEvento(partita)) throw new RuntimeException ("Utente non iscritto alla partita");	
					db.deleteCollegamentoEventoUtente(utente_corrente.getId_utente(), partita);
					break;
				} 
				catch (SQLException e) 
				{e.printStackTrace();}
		default:
			break;
		}
	}
		
	public static boolean utenteIscrittoInEvento(Evento evento)
	{
		if(utente_corrente == null)
			return false;
		
		try {
			return db.existUtenteInEvento(utente_corrente, evento);
		} 
		catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public static HashMap<CategorieEvento,LinkedList<Evento>> getEventiUtenteCorrente()
	{
		try {
			return db.selectEventiDiUtente(utente_corrente.getId_utente());
		} 
		catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
}
