package it.unibs.dii.isw.socialNetworkEventi.model;

import java.io.Serializable;

import it.unibs.dii.isw.socialNetworkEventi.utility.NomeCampi;

import java.util.*;

public abstract class Evento implements Serializable
{
	private static final long serialVersionUID = 1L;
	
	private int id_evento;
	private int id_creatore;
	
	private HashMap<NomeCampi, Campo> campi;
	private LinkedList<Utente> fruitori;
	
	/**
	 * Costruttore con parametri obbligatori
	 */
	public Evento(
			Integer id_creatore,
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
		fruitori = new LinkedList<>();
		
		if(luogo == null) 																				throw new IllegalArgumentException("Necessario inserire un luogo");
		if(data_ora_termine_ultimo_iscrizione == null) 													throw new IllegalArgumentException("Necessario inserire una data di chiusura delle iscrizioni");
		if(!dataNelFuturo(data_ora_termine_ultimo_iscrizione)) 											throw new IllegalArgumentException("Necessario inserire una data di chiusura delle iscrizioni posteriore alla data odierna");
		if(!dataSuccessivaTermineIscrizioni(data_ora_termine_ultimo_iscrizione, data_ora_inizio_evento))throw new IllegalArgumentException("Necessario inserire una data di inizio evento nel futuro e posteriore alla data di termine iscrizione");
		if(partecipanti <= 1) 																			throw new IllegalArgumentException("Necessario inserire un numero di partecipanti superiore o uguale a 2");
		if(id_creatore == null) 																		throw new IllegalArgumentException("Necessario inserire un utente creatore");
		//inserimento dei campi obbligatori nella HashMap dei campi	
		aggiungiCampo(luogo, true, NomeCampi.LUOGO, "Locazione evento");
		aggiungiCampo(data_ora_termine_ultimo_iscrizione, true, NomeCampi.D_O_CHIUSURA_ISCRIZIONI, "Termine iscrizioni");
		aggiungiCampo(data_ora_inizio_evento, true, NomeCampi.D_O_INIZIO_EVENTO, "Inizio evento");
		aggiungiCampo(partecipanti, true, NomeCampi.PARTECIPANTI, "Numero partecipanti");
		aggiungiCampo(costo, true, NomeCampi.COSTO, "Costo unitario");
		this.setId_creatore(id_creatore);
	}
	
	/*
	 * Costruttore con parametri obbligatori e facoltativi
	 */
	public Evento(
			Integer creatore,
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
		this(creatore, luogo, data_ora_termine_ultimo_iscrizione, data_ora_inizio_evento, partecipanti, costo);

		if(titolo != null)
			aggiungiCampo(titolo, false, NomeCampi.TITOLO, "Titolo evento");
		if(note != null)
			aggiungiCampo(note, false, NomeCampi.NOTE, "Note aggiuntive ");			
		if(benefici_quota != null)
			aggiungiCampo(benefici_quota, false, NomeCampi.BENEFICI_QUOTA, "Servizi compresi");						
		if(data_ora_termine_evento != null)		
		{
			if(!dataSuccessivaInizioEvento(data_ora_inizio_evento, data_ora_termine_evento)) throw new IllegalArgumentException("Necessario inserire una data di inizio evento nel futuro e posteriore alla data di inizio evento");
			aggiungiCampo(data_ora_termine_evento, false, NomeCampi.D_O_TERMINE_EVENTO, "Fine evento");						
		}	
	}
	
	
	protected <T> void aggiungiCampo(T campo, boolean obbligatorio, NomeCampi titolo, String descrizione)
	{
		campi.put(titolo, new Campo<T>(campo, obbligatorio, descrizione));
	}

	
	public HashMap<NomeCampi, Campo> getCampi()						{ return campi;}
//	public LinkedList
	
	public Campo 					 getCampo(NomeCampi nomeCampo)	{return campi.get(nomeCampo);}
	
	
	public <T>void setCampo(NomeCampi nome_campo, Campo campo)
			throws IllegalArgumentException
	{
		if(campi.get(nome_campo) == null) throw new IllegalArgumentException("Il campo desiderato non esiste");
		if(campo.getContenuto().getClass().isInstance(campi.get(nome_campo).getContenuto().getClass())) throw new IllegalArgumentException("La tipologia di campo che si desidera cambiare non corrisponde a quello specificato"); 
			campi.put(nome_campo, campo);
	}
	
	
	private boolean dataNelFuturo(Calendar data)
	 {
	  return Calendar.getInstance().compareTo(data) < 0;
	 }
	
	 
	 
	 public int getId() {
		return id_evento;
	}

	public void setId(int id) {
		this.id_evento = id;
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
	 
	 public boolean aggiungiFruitore(Utente utente)
	 {
		 if((Integer)getCampo(NomeCampi.PARTECIPANTI).getContenuto() > fruitori.size())
			 {fruitori.add(utente); return true;}
		 else
			 return false;
	 }
	 
	 public boolean rimuoviFruitore(Utente utente)
	 {
		 if(fruitori.get(0).equals(utente))
			 return false;
		 if(fruitori.contains(utente))
		 	{fruitori.remove(utente); return true;}
		 else
			 return false;
	 }
	 
	 public LinkedList<Utente> getFruitori()
	 {
		 return fruitori;
	 }

	public int getId_creatore() {
		return id_creatore;
	}

	public void setId_creatore(int id_creatore) {
		this.id_creatore = id_creatore;
	}
}