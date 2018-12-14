package socialNetwork;

import java.io.Serializable;
import java.util.HashMap;
import java.util.*;

public abstract class Evento implements Serializable
{
	private static final long serialVersionUID = 1L;
	
	private HashMap<String, Campo> campi;
	
	/**
	 * Costruttore con parametri obbligatori
	 */
	public Evento(
			String luogo,
			Calendar data_ora_termine_ultimo_iscrizione,	
			Calendar data_ora_inizio_evento,
		    Integer partecipanti,
		    Integer costo
			)
	throws IllegalArgumentException
	{
		//controlli sui campi obbligatori 
		campi = new HashMap<>();
		if(luogo == null) 																				throw new IllegalArgumentException("Necessario inserire un luogo");
		if(data_ora_termine_ultimo_iscrizione == null) 													throw new IllegalArgumentException("Necessario inserire una data di chiusura delle iscrizioni");
		if(!dataNelFuturo(data_ora_termine_ultimo_iscrizione)) 											throw new IllegalArgumentException("Necessario inserire una data di chiusura delle iscrizioni posteriore alla data odierna");
		if(!dataSuccessivaTermineIscrizioni(data_ora_termine_ultimo_iscrizione, data_ora_inizio_evento))throw new IllegalArgumentException("Necessario inserire una data di inizio evento nel futuro e posteriore alla data di termine iscrizione");
		if(partecipanti <= 1) 																			throw new IllegalArgumentException("Necessario inserire un numero di partecipanti superiore o uguale a 2");
		//inserimento dei campi obbligatori nella HashMap dei campi	
		aggiungiCampo(luogo, true, "Luogo", "Dove si svolgerà l'evento");
		aggiungiCampo(data_ora_termine_ultimo_iscrizione, true, "Chiusura Iscrizioni", "Data e ora di chiusura delle iscrizioni");
		aggiungiCampo(data_ora_inizio_evento, true, "Inizio Evento", "Data e ora di inzio dell'evento");
		aggiungiCampo(partecipanti, true, "Partecipanti", "Numero esatto dei partecipanti");
		aggiungiCampo(costo, true, "Costo", "Costo unitario per partecipante");
		
//		campi.put("Luogo",								new Campo<String>(luogo, true, "Luogo", "Dove si svolgerà l'evento"));
//		campi.put("Data_ora_termine_ultimo_iscrizione", new Campo<Calendar>(data_ora_termine_ultimo_iscrizione, true, "Chiusura Iscrizioni", "Data e ora di chiusura delle iscrizioni"));
//		campi.put("Data_ora_inizio_evento", 			new Campo<Calendar>(data_ora_inizio_evento, true, "Inizio Evento", "Data e ora di inzio dell'evento"));
//		campi.put("Partecipanti", 						new Campo<Integer>(partecipanti, true, "Partecipanti", "Numero esatto dei partecipanti"));
//		campi.put("Costo", 								new Campo<Integer>(costo, true, "Costo", "Costo unitario per partecipante"));
	}
	
	/*
	 * Costruttore con parametri obbligatori e facoltativi
	 */
	public Evento(
			String luogo,
			Calendar data_ora_termine_ultimo_iscrizione,	
			Calendar data_ora_inizio_evento,
			Integer partecipanti,
			Integer costo,
		    
			String titolo,				
			String note,
			String compreso_nella_quota,
		    Calendar data_ora_termine_evento
			)
	throws IllegalArgumentException

	{
		this(luogo, data_ora_termine_ultimo_iscrizione, data_ora_inizio_evento, partecipanti, costo);

		if(titolo != null)
			aggiungiCampo(titolo, false, "Titolo", "Titolo dell'evento");
//			campi.put("Titolo", 					new Campo<String>(titolo, false, "Titolo", "Titolo dell'evento"));
		if(note != null)
			aggiungiCampo(note, false, "Note", "Note aggiuntive riguardanti l'evento");			
//			campi.put("Note", 						new Campo<String>(note, false, "Note", "Note aggiuntive riguardanti l'evento"));
		if(compreso_nella_quota != null)
			aggiungiCampo(compreso_nella_quota, false, "Benefici quota", "Servizi,Beni aggiuntivi compresi nella quota");						
//			campi.put("Compreso nella quota", 		new Campo<String>(compreso_nella_quota, false, "Benefici quota", "Servizi,Beni aggiuntivi compresi nella quota"));
		if(data_ora_termine_evento != null)		
		{
			if(!dataSuccessivaInizioEvento(data_ora_inizio_evento, data_ora_termine_evento)) throw new IllegalArgumentException("Necessario inserire una data di inizio evento nel futuro e posteriore alla data di inizio evento");
			aggiungiCampo(data_ora_termine_evento, false, "Termine evento", "Data e ora di fine evento");						
//				campi.put("Data ora termine evento",new Campo<Calendar>(data_ora_termine_evento, false, "Termine evento", "Data e ora di fine evento"));
		}	
	}
	
	
	protected <T> void aggiungiCampo(T campo, boolean obbligatorio, String titolo, String descrizione)
	{
		campi.put(titolo, new Campo<T>(campo, obbligatorio, descrizione));
	}

	
	public HashMap<String, Campo> getCampi(){ return campi;};
	
	
	public <T>void cambiaContenutoCampo(String nome_campo, Campo campo)
			throws IllegalArgumentException
	{
		if(campi.get(nome_campo) == null) throw new IllegalArgumentException("Il campo desiderato non esiste");
		if(campo.getCampo().getClass().isInstance(campi.get(nome_campo).getCampo().getClass())) throw new IllegalArgumentException("La tipologia di campo che si desidera cambiare non corrisponde a quello specificato"); 
			campi.put(nome_campo, campo);
	}
	
	
	private boolean dataNelFuturo(Calendar data)
	{
		Calendar data_attuale = Calendar.getInstance();
		if(		data.get(Calendar.YEAR) 	> data_attuale.get(Calendar.YEAR)  &&
				data.get(Calendar.MONTH)	> data_attuale.get(Calendar.MONTH) &&
				data.get(Calendar.HOUR) 	> data_attuale.get(Calendar.HOUR)  &&
				data.get(Calendar.MINUTE) 	> data_attuale.get(Calendar.MINUTE)
				)
			return true;
		else
			return false;
	}
	
	
	private boolean dataSuccessivaTermineIscrizioni(Calendar data_termine, Calendar data_da_controllare)
	{
		if(!dataNelFuturo(data_da_controllare))
			return false;
		if(		data_da_controllare.get(Calendar.YEAR) 		> data_termine.get(Calendar.YEAR)  &&
				data_da_controllare.get(Calendar.MONTH)		> data_termine.get(Calendar.MONTH) &&
				data_da_controllare.get(Calendar.HOUR) 		> data_termine.get(Calendar.HOUR)  &&
				data_da_controllare.get(Calendar.MINUTE) 	> data_termine.get(Calendar.MINUTE)
				)
			return true;
		else
			return false;
	}
	
	
	private boolean dataSuccessivaInizioEvento(Calendar data_inizio, Calendar data_da_controllare)
	{
		if(!dataNelFuturo(data_da_controllare))
			return false;
		if(		data_da_controllare.get(Calendar.YEAR) 		> data_inizio.get(Calendar.YEAR)  &&
				data_da_controllare.get(Calendar.MONTH)		> data_inizio.get(Calendar.MONTH) &&
				data_da_controllare.get(Calendar.HOUR) 		> data_inizio.get(Calendar.HOUR)  &&
				data_da_controllare.get(Calendar.MINUTE) 	> data_inizio.get(Calendar.MINUTE)
				)
			return true;
		else
			return false;
	}

}