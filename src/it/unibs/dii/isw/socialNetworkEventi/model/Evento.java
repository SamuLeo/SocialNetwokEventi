package it.unibs.dii.isw.socialNetworkEventi.model;

import it.unibs.dii.isw.socialNetworkEventi.utility.CategoriaEvento;
import it.unibs.dii.isw.socialNetworkEventi.utility.NomeCampo;
import it.unibs.dii.isw.socialNetworkEventi.utility.StatoEvento;
import it.unibs.dii.isw.socialNetworkEventi.utility.Stringhe;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.*;

@SuppressWarnings("rawtypes")
public abstract class Evento 
{
	private int id_evento;
	private Utente utente_creatore;
	private StatoEvento stato = StatoEvento.VALIDA;
	private CategoriaEvento nome_categoria = CategoriaEvento.DEFAULT;
	private final HashMap<NomeCampo, Campo> campi;
	private HashMap<Utente,HashMap<NomeCampo,Boolean>> partecipanti_campiOpt;
	
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
			) throws IllegalArgumentException {
		//controlli sui campi obbligatori 
		campi = new HashMap<>();
		partecipanti_campiOpt = new HashMap<Utente,HashMap<NomeCampo,Boolean>>();
		
		if(luogo == null) 																				throw new IllegalArgumentException("Necessario inserire un luogo");
		if(data_ora_termine_ultimo_iscrizione == null) 													throw new IllegalArgumentException("Necessario inserire una data di chiusura delle iscrizioni");
		//Rimosso questo controllo per non forzare che gli eventi siano tutti nel futuro, perché vanno ricreati ogni volta che vengono estratti dalla base dati. Vengono imposti vincoli solo sulla relativa posizione delle date
		//if(!dataNelFuturo(data_ora_termine_ultimo_iscrizione)) 										throw new IllegalArgumentException("Necessario inserire una data di chiusura delle iscrizioni posteriore alla data odierna");
		if(!data1PrecedenteData2(data_ora_termine_ultimo_iscrizione, data_ora_inizio_evento))			throw new IllegalArgumentException("Necessario inserire una data di inizio evento nel futuro e posteriore alla data di termine iscrizione");
		if(partecipanti < 2) 																			throw new IllegalArgumentException("Necessario inserire un numero di partecipanti superiore o uguale a 2");
		if(creatore == null) 																			throw new IllegalArgumentException("Necessario inserire un utente creatore");
		//if(costo < 0)																					throw new IllegalArgumentException("Necessario inserire un costo superiore o uguale a 0");
		//inserimento dei campi obbligatori nella HashMap dei campi	
		aggiungiCampo(luogo, true, NomeCampo.LUOGO, "Locazione evento");
		aggiungiCampo(data_ora_termine_ultimo_iscrizione, true, NomeCampo.D_O_CHIUSURA_ISCRIZIONI, "Termine iscrizioni");
		aggiungiCampo(data_ora_inizio_evento, true, NomeCampo.D_O_INIZIO_EVENTO, "Inizio evento");
		aggiungiCampo(partecipanti, true, NomeCampo.PARTECIPANTI, "Numero partecipanti");
		aggiungiCampo(costo, true, NomeCampo.COSTO, "Costo unitario");
		this.setUtenteCreatore(creatore);
		//partecipanti_campiOpt.add(creatore);
	}
	
	/** Costruttore con parametri obbligatori e facoltativi */
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
		    Calendar data_ora_termine_evento,
		    Calendar data_ora_termine_ritiro_iscrizione,
		    Integer tolleranza
			) throws IllegalArgumentException {
		this(creatore, luogo, data_ora_termine_ultimo_iscrizione, data_ora_inizio_evento, partecipanti, costo);

		if(titolo != null) 
			aggiungiCampo(titolo, false, NomeCampo.TITOLO, "Titolo evento");
		else
			aggiungiCampo("Evento Anonimo", false, NomeCampo.TITOLO, "Titolo evento");			
		if(note != null) aggiungiCampo(note, false, NomeCampo.NOTE, "Note aggiuntive ");			
		if(benefici_quota != null) aggiungiCampo(benefici_quota, false, NomeCampo.BENEFICI_QUOTA, "Servizi compresi");
		if(tolleranza != null && tolleranza>=0) aggiungiCampo(tolleranza, false, NomeCampo.TOLLERANZA_MAX, "Iscrizioni in esubero");
		if(tolleranza != null && tolleranza<0) throw new IllegalArgumentException("Il numero di partecipanti in esubero può essere solo positivo");
		if(data_ora_termine_evento != null)	{
			if(data1PrecedenteData2(data_ora_termine_evento, data_ora_inizio_evento)) throw new IllegalArgumentException("Necessario inserire una data di termine evento nel futuro e posteriore alla data di inizio evento");
				aggiungiCampo(data_ora_termine_evento, false, NomeCampo.D_O_TERMINE_EVENTO, "Fine evento");						
		}	
		if(data_ora_termine_ritiro_iscrizione != null) {
			if(data1PrecedenteData2(data_ora_termine_ultimo_iscrizione, data_ora_termine_ritiro_iscrizione)) throw new IllegalArgumentException("Necessario inserire una data di termine di ritiro delle iscrizioni nel futuro e anteriore alla data di termine delle iscrizioni");
				aggiungiCampo(data_ora_termine_ritiro_iscrizione, false, NomeCampo.D_O_TERMINE_RITIRO_ISCRIZIONE, "Termine ritiro iscrizioni");						
		}
		else aggiungiCampo(data_ora_termine_ultimo_iscrizione, false, NomeCampo.D_O_TERMINE_RITIRO_ISCRIZIONE, "Termine ritiro iscrizioni");
	}

	/*
	 * Costruttore con parametri obbligatori e facoltativi, id e stato
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
		    Calendar data_ora_ritiro_iscrizione,
		    Integer tolleranza_max,
		    StatoEvento stato
			){
		this(creatore, luogo, data_ora_termine_ultimo_iscrizione, data_ora_inizio_evento, partecipanti, costo, titolo, note, benefici_quota, data_ora_termine_evento,data_ora_ritiro_iscrizione,tolleranza_max);
		id_evento= id;
		this.stato = stato;
	}
	
	
	/**
	 * Questo metodo controlla e in caso sia cambiato aggiorna lo stato dell'evento, verificando se la data di chiusura iscrizioni ha superato la data odierna,
	 * se l'evento ha raggiunto la sua conclusione oppure se è concluso
	 * 
	 * Prima questa responsabilità era relegata al controller. Ora, per Expert, è stata relegata ad evento.
	 * 
	 * @return true se lo stato cambia
	 * @throws SQLException 
	 */
	public final boolean controllaStatoEvento() 
	{
		Calendar oggi = Calendar.getInstance();
		boolean DataChiusuraIscrizioniNelFuturo = oggi.before((Calendar)getContenutoCampo(NomeCampo.D_O_CHIUSURA_ISCRIZIONI));
		boolean termine_ritiro_scaduto = oggi.after((Calendar) getContenutoCampo(NomeCampo.D_O_TERMINE_RITIRO_ISCRIZIONE));
		boolean DataFineEventoNelFuturo;		
		if (getCampo(NomeCampo.D_O_TERMINE_EVENTO)==null) 
		{
			Calendar giorno_dopo_inizio_evento = ((Calendar)getContenutoCampo(NomeCampo.D_O_INIZIO_EVENTO));
			giorno_dopo_inizio_evento.add(Calendar.DAY_OF_YEAR,1);
			DataFineEventoNelFuturo= oggi.before(giorno_dopo_inizio_evento);
			giorno_dopo_inizio_evento.add(Calendar.DAY_OF_YEAR,-1);	//è incredibile, ma senza questa riga la vità è più difficile e infame
		}
		else 
			DataFineEventoNelFuturo = oggi.before((Calendar)getCampo(NomeCampo.D_O_TERMINE_EVENTO).getContenuto());
		int numero_iscritti_attuali = getNumeroPartecipanti();
		int numero_minimo_iscritti = (Integer)getCampo(NomeCampo.PARTECIPANTI).getContenuto();
		int numero_massimo_iscritti_possibili = numero_minimo_iscritti + (Integer)getCampo(NomeCampo.TOLLERANZA_MAX).getContenuto();
		
		StatoEvento statoEvento = getStato();
		//Ritirata, Fallita e Conclusa sono stati terminali. Gli unici con possibile evoluzione sono Aperta e Chiusa (e Valida)
		if (statoEvento.getString().equals("Aperta") && !DataChiusuraIscrizioniNelFuturo && numero_iscritti_attuali < numero_minimo_iscritti)
		{
			setStato(StatoEvento.FALLITA);
			return true;
		}
		if (statoEvento.getString().equals("Aperta") && !DataChiusuraIscrizioniNelFuturo && numero_iscritti_attuali >= numero_minimo_iscritti)
		{
			setStato(StatoEvento.CHIUSA);
			return true;
		}
		if (statoEvento.getString().equals("Aperta") && termine_ritiro_scaduto && numero_iscritti_attuali == numero_massimo_iscritti_possibili)
		{
			setStato(StatoEvento.CHIUSA);
			return true;
		}
		if(DataFineEventoNelFuturo == false && (statoEvento.getString().equals("Chiusa")))
		{
			setStato(StatoEvento.CONCLUSA);
			return true;
		}
		return false;
	}
	
	
	 
	//Polymorphism
	public abstract PreparedStatement getPSInsertEvento(Connection con) throws SQLException;
	public abstract PreparedStatement getPSInsertIscrizioneUtenteInEvento(Utente utente, Connection con) throws SQLException, Exception;
	
	
	//Template
	public final PreparedStatement getPSSelectUtenti(Connection con) throws SQLException {
		PreparedStatement ps = con.prepareStatement(Stringhe.ottieniStringaDesiderata(Stringhe.SELECT_SQL_ISCRITTI_EVENTO, getNomeCategoria()));
		ps.setInt(1, this.getId());
		return ps;
	}
	
	public final PreparedStatement getPSUpdateStatoEvento(Connection con) throws SQLException {
		PreparedStatement ps = con.prepareStatement(Stringhe.ottieniStringaDesiderata(Stringhe.UPDATE_SQL_STATO_EVENTO, getNomeCategoria()));
		ps.setString(1, this.getStato().getString());
		ps.setInt(2, this.getId());		
		return ps;
	};
	
	public final PreparedStatement getPSDeleteEvento(Connection con) throws SQLException {
		PreparedStatement ps = con.prepareStatement(Stringhe.ottieniStringaDesiderata(Stringhe.DELETE_SQL_EVENTO, getNomeCategoria()));
		ps.setInt(1, this.getId());
		return ps;
	};
	
	public final PreparedStatement getPSDeleteRelazioneEventoUtente(String nome_utente, Connection con) throws SQLException {
		PreparedStatement ps = con.prepareStatement(Stringhe.ottieniStringaDesiderata(Stringhe.DELETE_SQL_RELAZIONE_UTENTE_EVENTO, getNomeCategoria()));
		ps.setString(1, nome_utente);
		ps.setInt(2, this.getId());
		return ps;
	};


	/**
	 * Getter and Setters
	 */
	
	public int getId() {return id_evento;}
	public void setId(int id) {this.id_evento = id;}
	
	public StatoEvento getStato() {return stato;}
	public void setStato(StatoEvento stato) {this.stato = stato;}	
	
	public CategoriaEvento getNomeCategoria() {return nome_categoria;}
	public  void setNomeCategoria(CategoriaEvento nomeCategoria) {this.nome_categoria = nomeCategoria;}
	
	public <T>void setCampo(NomeCampo nome_campo, Campo campo) throws IllegalArgumentException
	{
		if(campi.get(nome_campo) == null) throw new IllegalArgumentException("Il campo desiderato non esiste");
		if(campo.getContenuto().getClass().isInstance(campi.get(nome_campo).getContenuto().getClass())) throw new IllegalArgumentException("La tipologia di campo che si desidera cambiare non corrisponde a quello specificato"); 
		campi.put(nome_campo, campo);
	}
	
	protected <T> void aggiungiCampo(T campo, boolean obbligatorio, NomeCampo titolo, String descrizione) {campi.put(titolo, new Campo<T>(campo, obbligatorio, descrizione));}
	public Campo getCampo(NomeCampo nomeCampo)	{return campi.get(nomeCampo);}
	public Object getContenutoCampo(NomeCampo nomeCampo) {return getCampo(nomeCampo).getContenuto();}
	
	public boolean aggiungiFruitore(Utente utente)
	{
		Calendar adesso = Calendar.getInstance();
		boolean termineIscrizioni = ((Calendar)(getContenutoCampo(NomeCampo.D_O_CHIUSURA_ISCRIZIONI))).before(adesso);
		if((Integer)getContenutoCampo(NomeCampo.PARTECIPANTI) + (Integer)getContenutoCampo(NomeCampo.TOLLERANZA_MAX) > getNumeroPartecipanti() && !termineIscrizioni)
		{
			if (utente_creatore.equals(utente)) return false;
			for (Utente u: partecipanti_campiOpt.keySet())
				if (u.equals(utente)) return false;
			partecipanti_campiOpt.put(utente,null);
			return true;
		}
		else return false;
	}
	
	public void rimuoviFruitore(Utente utente)
	{
		Calendar adesso = Calendar.getInstance();
		boolean termineRitiroScaduto = ((Calendar)(getContenutoCampo(NomeCampo.D_O_TERMINE_RITIRO_ISCRIZIONE))).before(adesso);
		if(getNumeroPartecipanti() == 0 || termineRitiroScaduto) return;
			partecipanti_campiOpt.remove(utente);
	}
	
	public int getNumeroPartecipanti() {
		if (utente_creatore == null) return partecipanti_campiOpt.keySet().size();
		boolean presente = false;
		for (Utente u: partecipanti_campiOpt.keySet())
			if (u.getNome().equals(utente_creatore.getNome())) {presente = true; break;}
		if (presente) return partecipanti_campiOpt.keySet().size();
		else return partecipanti_campiOpt.keySet().size() + 1;
	}
	
	public Utente getUtenteCreatore() {return utente_creatore;}
	public void setUtenteCreatore(Utente utente_creatore) {this.utente_creatore = utente_creatore;}
	public void setPartecipanti_campiOpt(HashMap<Utente,HashMap<NomeCampo,Boolean>> partecipanti_campiOpt) {this.partecipanti_campiOpt = partecipanti_campiOpt;}
	public HashMap<Utente,HashMap<NomeCampo,Boolean>> getPartecipanti_campiOpt() { return partecipanti_campiOpt;}
	public void setCampiOptPerUtente(Utente utente, HashMap<NomeCampo,Boolean> campi_opt){
		if (partecipanti_campiOpt.containsKey(utente)) partecipanti_campiOpt.remove(utente);
		partecipanti_campiOpt.put(utente, campi_opt);
	}

	public void setCampoOptPerUtente(Utente utente, NomeCampo nome_campo, Boolean bool)
	{		
		if(partecipanti_campiOpt.get(utente) == null)
		{
			HashMap<NomeCampo,Boolean> campi_opt = new HashMap<NomeCampo,Boolean>();
			campi_opt.put(nome_campo, bool);
			partecipanti_campiOpt.put(utente, campi_opt);
		}
		else partecipanti_campiOpt.get(utente).put(nome_campo, bool);
	}
	
	public Boolean getCampoOptDiUtente(Utente utente, NomeCampo nome_campi)
	{
		for(Utente u : partecipanti_campiOpt.keySet())
			if(u.equals(utente))
				return partecipanti_campiOpt.get(u).get(nome_campi);
		return null;
	}
	
	public HashMap<NomeCampo,Boolean> getCampiOptDiUtente(Utente utente)
	{
		for(Utente u : partecipanti_campiOpt.keySet())
			if(u.equals(utente))
				return partecipanti_campiOpt.get(u);
		return null;
	}
	
	public boolean contieneUtente(Utente utente)
	{
		for(Utente u : partecipanti_campiOpt.keySet())
			if(u.equals(utente))
				return true;
		return false;
	}
	
	//Metodi di confronto tra istanze
	
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
			else stringa.append(c.getDescrizione_campo() + " : " + c.getContenuto() + "\n");
		}
		stringa.append("Numero iscritti attuali: " + getNumeroPartecipanti() + "\n");
		return stringa.toString();
	}
	
	public boolean equals(Object obj)
	{
		return toString().equals(obj.toString());
	}
	
	//Utili per manipolazione date
	private boolean data1PrecedenteData2(Calendar data1, Calendar data2){return data1.compareTo(data2) < 0;}
	protected Timestamp creaTimestamp(Calendar c) {if (c==null) return null; else return new Timestamp (c.getTimeInMillis());}
}