package it.unibs.dii.isw.socialNetworkEventi.model;

import it.unibs.dii.isw.socialNetworkEventi.utility.NomeCampi;
import it.unibs.dii.isw.socialNetworkEventi.utility.StatoEvento;
import java.util.*;

public abstract class Evento {
	
	private int id_evento;
	private Utente utente_creatore;
	private StatoEvento stato; 
	
	private HashMap<NomeCampi, Campo> campi;
	private LinkedList<Utente> fruitori;
	
	/**
	 * Costruttore con parametri obbligatori
	 */
	public Evento(
			Utente creatore,
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
		if(!dataSuccessiva(data_ora_termine_ultimo_iscrizione, data_ora_inizio_evento))throw new IllegalArgumentException("Necessario inserire una data di inizio evento nel futuro e posteriore alla data di termine iscrizione");
		if(partecipanti < 2) 																			throw new IllegalArgumentException("Necessario inserire un numero di partecipanti superiore o uguale a 2");
		if(creatore == null) 																			throw new IllegalArgumentException("Necessario inserire un utente creatore");
		//inserimento dei campi obbligatori nella HashMap dei campi	
		aggiungiCampo(luogo, true, NomeCampi.LUOGO, "Locazione evento");
		aggiungiCampo(data_ora_termine_ultimo_iscrizione, true, NomeCampi.D_O_CHIUSURA_ISCRIZIONI, "Termine iscrizioni");
		aggiungiCampo(data_ora_inizio_evento, true, NomeCampi.D_O_INIZIO_EVENTO, "Inizio evento");
		aggiungiCampo(partecipanti, true, NomeCampi.PARTECIPANTI, "Numero partecipanti");
		aggiungiCampo(costo, true, NomeCampi.COSTO, "Costo unitario");
		this.setUtenteCreatore(creatore);
		fruitori.add(creatore);
		
		stato = StatoEvento.VALIDA;
	}
	
	/*
	 * Costruttore con parametri obbligatori e facoltativi
	 */
	public Evento(
			Utente creatore,
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

		if(titolo != null) aggiungiCampo(titolo, false, NomeCampi.TITOLO, "Titolo evento");
		if(note != null) aggiungiCampo(note, false, NomeCampi.NOTE, "Note aggiuntive ");			
		if(benefici_quota != null) aggiungiCampo(benefici_quota, false, NomeCampi.BENEFICI_QUOTA, "Servizi compresi");						
		if(data_ora_termine_evento != null)		
		{
			if(!dataSuccessiva(data_ora_inizio_evento, data_ora_termine_evento)) throw new IllegalArgumentException("Necessario inserire una data di inizio evento nel futuro e posteriore alla data di inizio evento");
			aggiungiCampo(data_ora_termine_evento, false, NomeCampi.D_O_TERMINE_EVENTO, "Fine evento");						
		}	
	}

	/*
	 * Costruttore con parametri obbligatori e facoltativi e id
	 */
	
	public Evento(
			Integer id,
			Utente creatore,
			String luogo,
			Calendar data_ora_termine_ultimo_iscrizione,	
			Calendar data_ora_inizio_evento,
			Integer partecipanti,
			Integer costo,
		    
			String titolo,				
			String note,
			String benefici_quota,
		    Calendar data_ora_termine_evento,
		    StatoEvento stato
			)
	{
		this(creatore, luogo, data_ora_termine_ultimo_iscrizione, data_ora_inizio_evento, partecipanti, costo, titolo, note, benefici_quota, data_ora_termine_evento);
		id_evento= id;
		this.stato = stato;
	}
	
	
	protected <T> void aggiungiCampo(T campo, boolean obbligatorio, NomeCampi titolo, String descrizione)
	{
		campi.put(titolo, new Campo<T>(campo, obbligatorio, descrizione));
	}
	
	
	public boolean dataNelFuturo(Calendar data) 
	{
		return Calendar.getInstance().compareTo(data) < 0; 
	}
		 

	private boolean dataSuccessiva(Calendar base, Calendar data_da_controllare)
	 {
	  if(!dataNelFuturo(base)) return false;
	  else return base.compareTo(data_da_controllare) < 0;
	 }
	 
	 public boolean aggiungiFruitore(Utente utente)
	 {
		 if((Integer)getCampo(NomeCampi.PARTECIPANTI).getContenuto() > fruitori.size())
			 {fruitori.add(utente); return true;}
		 else return false;
	 }
	 
	 public boolean rimuoviFruitore(Utente utente)
	 {
		 if(fruitori.size()==0 || fruitori.get(0).equals(utente)) return false;
		 return fruitori.remove(utente);
	 }

/**
 * Getter and Setters
 */

	public <T>void setCampo(NomeCampi nome_campo, Campo campo) throws IllegalArgumentException
	{
		 if(campi.get(nome_campo) == null) throw new IllegalArgumentException("Il campo desiderato non esiste");
		 if(campo.getContenuto().getClass().isInstance(campi.get(nome_campo).getContenuto().getClass())) throw new IllegalArgumentException("La tipologia di campo che si desidera cambiare non corrisponde a quello specificato"); 
		 campi.put(nome_campo, campo);
	} 
	 
	public HashMap<NomeCampi, Campo> getCampi()	{return campi;}
	public Campo getCampo(NomeCampi nomeCampo)	{return campi.get(nomeCampo);}

	public LinkedList<Utente> getFruitori() { return fruitori; }
	public int getNumeroPartecipanti() {return fruitori.size();}
	public Utente getUtenteCreatore() { return utente_creatore; }
	public void setUtenteCreatore(Utente utente_creatore) {this.utente_creatore = utente_creatore;}
	public void setFruitori(LinkedList<Utente> fruitori) {this.fruitori = fruitori;}

	public int getId() {return id_evento;}
	public void setId(int id) {this.id_evento = id;}
	public StatoEvento getStato() {return stato;}
	public void setStato(StatoEvento stato) {this.stato = stato;}
	
	@Override
	public String toString() 
	{
		StringBuffer stringa = new StringBuffer();
		Collection<Campo> v = campi.values();
		for (Campo c : v)
		{
			if(c.getContenuto().getClass().getSimpleName().equals("GregorianCalendar"))
				{
					java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd-MM-yyyy HH:mm");
					stringa.append(c.getDescrizione_campo() + " : " + sdf.format(((Calendar) c.getContenuto()).getTime()) + "\n");

				}
			else	
				stringa.append(c.getDescrizione_campo() + " : " + c.getContenuto() + "\n");

		}
		return stringa.toString();
	}
	
	
}