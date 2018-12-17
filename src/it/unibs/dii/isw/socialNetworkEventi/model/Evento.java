package it.unibs.dii.isw.socialNetworkEventi.model;

import java.io.Serializable;
import java.util.HashMap;

import it.unibs.dii.isw.socialNetworkEventi.utility.NomeCampi;

import java.util.*;

public abstract class Evento implements Serializable
{
	private static final long serialVersionUID = 1L;
	
	private String titolo;
	private String descrizione;
	
	private HashMap<NomeCampi, Campo> campi;
	
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
		aggiungiCampo(luogo, true, NomeCampi.LUOGO, "Dove si svolgerà l'evento");
		aggiungiCampo(data_ora_termine_ultimo_iscrizione, true, NomeCampi.D_O_CHIUSURA_ISCRIZIONI, "Data e ora di chiusura delle iscrizioni");
		aggiungiCampo(data_ora_inizio_evento, true, NomeCampi.D_O_INIZIO_EVENTO, "Data e ora di inzio dell'evento");
		aggiungiCampo(partecipanti, true, NomeCampi.PARTECIPANTI, "Numero esatto dei partecipanti");
		aggiungiCampo(costo, true, NomeCampi.COSTO, "Costo unitario per partecipante");
		
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
			String benefici_quota,
		    Calendar data_ora_termine_evento
			)
	throws IllegalArgumentException

	{
		this(luogo, data_ora_termine_ultimo_iscrizione, data_ora_inizio_evento, partecipanti, costo);

		if(titolo != null)
			aggiungiCampo(titolo, false, NomeCampi.TITOLO, "Titolo dell'evento");
		if(note != null)
			aggiungiCampo(note, false, NomeCampi.NOTE, "Note aggiuntive riguardanti l'evento");			
		if(benefici_quota != null)
			aggiungiCampo(benefici_quota, false, NomeCampi.BENEFICI_QUOTA, "Servizi,Beni aggiuntivi compresi nella quota");						
		if(data_ora_termine_evento != null)		
		{
			if(!dataSuccessivaInizioEvento(data_ora_inizio_evento, data_ora_termine_evento)) throw new IllegalArgumentException("Necessario inserire una data di inizio evento nel futuro e posteriore alla data di inizio evento");
			aggiungiCampo(data_ora_termine_evento, false, NomeCampi.D_O_TERMINE_EVENTO, "Data e ora di fine evento");						
		}	
	}
	
	
	protected <T> void aggiungiCampo(T campo, boolean obbligatorio, NomeCampi titolo, String descrizione)
	{
		campi.put(titolo, new Campo<T>(campo, obbligatorio, descrizione));
	}

	
	public HashMap<NomeCampi, Campo> getCampi()						{ return campi;}
	
	public Campo 					 getCampo(NomeCampi nomeCampo)	{return campi.get(nomeCampo);}
	
	
	public <T>void setCampo(NomeCampi nome_campo, Campo campo)
			throws IllegalArgumentException
	{
		if(campi.get(nome_campo) == null) throw new IllegalArgumentException("Il campo desiderato non esiste");
		if(campo.getCampo().getClass().isInstance(campi.get(nome_campo).getCampo().getClass())) throw new IllegalArgumentException("La tipologia di campo che si desidera cambiare non corrisponde a quello specificato"); 
			campi.put(nome_campo, campo);
	}
	
	
	private boolean dataNelFuturo(Calendar data)
	 {
	  return Calendar.getInstance().compareTo(data) < 0;
	 }
	
	 
	 
	 private boolean dataSuccessivaTermineIscrizioni(Calendar data_termine, Calendar data_da_controllare)
	 {
	  if(!dataNelFuturo(data_da_controllare))
	   return false;
	  return data_termine.compareTo(data_da_controllare) < 0;
	 }
	 
	 
	 private boolean dataSuccessivaInizioEvento(Calendar data_inizio, Calendar data_da_controllare)
	 {
	  if(!dataNelFuturo(data_da_controllare))
	   return false;
	  return data_inizio.compareTo(data_da_controllare) < 0;
	 }

	public String getTitolo() {return titolo;}
	public void setTitolo(String titolo) {this.titolo = titolo;}
	public String getDescrizione() {return descrizione;}
	public void setDescrizione(String descrizione) {this.descrizione = descrizione;}	
}