package it.unibs.dii.isw.socialNetworkEventi.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Calendar;

import it.unibs.dii.isw.socialNetworkEventi.utility.*;

public class PartitaCalcio extends Evento 
{		
	public PartitaCalcio(
			Utente creatore, String luogo, Calendar data_ora_termine_ultimo_iscrizione,	Calendar data_ora_inizio_evento, Integer partecipanti, Integer costo,
			String titolo, String note, String benefici_quota, Calendar data_ora_termine_evento,  Calendar data_ora_termine_ritiro_iscrizione, Integer tolleranza_max,
		    Integer eta_minima,
		    Integer eta_massima,
		    String genere
			) throws IllegalArgumentException {
		super(creatore, luogo, data_ora_termine_ultimo_iscrizione, data_ora_inizio_evento, partecipanti, costo, titolo, note, benefici_quota, data_ora_termine_evento, data_ora_termine_ritiro_iscrizione, tolleranza_max);
		setNomeCategoria(CategoriaEvento.PARTITA_CALCIO);
		if(eta_minima==null || eta_massima==null || eta_minima <= 0 || eta_massima <= 0 ||  eta_minima>eta_massima || eta_massima>140)     throw new IllegalArgumentException("Necessario inserire un età minima, massima dei partecipanti superiore a 0");
		if(genere == null || !(genere.equalsIgnoreCase("maschi") || genere.equalsIgnoreCase("femmine") || genere.equalsIgnoreCase("qualsiasi")))throw new IllegalArgumentException("Necessario inserire il genere dei partecipanti");		
		aggiungiCampo(eta_minima, true, NomeCampo.ETA_MINIMA, "Età minima");
		aggiungiCampo(eta_massima, true, NomeCampo.ETA_MASSIMA, "Età massima");
		aggiungiCampo(genere, true, NomeCampo.GENERE, "Genere richiesto");
	}

	public PartitaCalcio(
			Integer id_partita,
			Utente creatore, String luogo, Calendar data_ora_termine_ultimo_iscrizione,	Calendar data_ora_inizio_evento, Integer partecipanti, Integer costo,
			String titolo, String note, String benefici_quota, Calendar data_ora_termine_evento,  Calendar data_ora_termine_ritiro_iscrizione, Integer tolleranza_max,
		    StatoEvento stato,
		    Integer eta_minima, Integer eta_massima, String genere)
	{
		this(creatore,luogo,data_ora_termine_ultimo_iscrizione,data_ora_inizio_evento,partecipanti, costo, titolo, note, benefici_quota, data_ora_termine_evento,data_ora_termine_ritiro_iscrizione, tolleranza_max, eta_minima, eta_massima, genere);
		setId(id_partita);
		setStato(stato);
	}

	@Override
	public PreparedStatement getPSInsertEvento(Connection con) throws SQLException 
	{
		PreparedStatement ps = con.prepareStatement(Stringhe.ottieniStringaDesiderata(Stringhe.INSERT_SQL_EVENTO, CategoriaEvento.PARTITA_CALCIO), Statement.RETURN_GENERATED_KEYS);
		
		String nome_utente_creatore						= getUtenteCreatore().getNome();
		String luogo 									= (String) getContenutoCampo(NomeCampo.LUOGO);
		Calendar data_ora_termine_ultimo_iscrizione		= (Calendar) getContenutoCampo(NomeCampo.D_O_CHIUSURA_ISCRIZIONI);
		Calendar data_ora_inizio_evento 				= (Calendar) getContenutoCampo(NomeCampo.D_O_INIZIO_EVENTO);
		int partecipanti								= (Integer) getContenutoCampo(NomeCampo.PARTECIPANTI);
		int costo										= (Integer) getContenutoCampo(NomeCampo.COSTO);
//			Campi opzionali		
		String titolo									= (String)(getCampo(NomeCampo.TITOLO) != null ? getContenutoCampo(NomeCampo.TITOLO) : null);
		String note										= (String)(getCampo(NomeCampo.NOTE) != null ? getContenutoCampo(NomeCampo.NOTE) : null);
		String benefici_quota							= (String)(getCampo(NomeCampo.BENEFICI_QUOTA) != null ? getContenutoCampo(NomeCampo.BENEFICI_QUOTA) : null);
		Calendar data_ora_termine_evento				= getCampo(NomeCampo.D_O_TERMINE_EVENTO) != null ? (Calendar) getContenutoCampo(NomeCampo.D_O_TERMINE_EVENTO) : null;
		Calendar data_ora_termine_ritiro_iscrizione		= getCampo(NomeCampo.D_O_TERMINE_RITIRO_ISCRIZIONE) != null ? (Calendar) getContenutoCampo(NomeCampo.D_O_TERMINE_RITIRO_ISCRIZIONE) : null;
		int tolleranza_max								= getCampo(NomeCampo.TOLLERANZA_MAX) != null ? (Integer)(getContenutoCampo(NomeCampo.TOLLERANZA_MAX)) : 0;
//			Campi Partita Calcio
		int eta_minima									= (Integer) getContenutoCampo(NomeCampo.ETA_MINIMA);
	    int eta_massima									= (Integer) getContenutoCampo(NomeCampo.ETA_MASSIMA);
	    String genere									= (String) getContenutoCampo(NomeCampo.GENERE);
	    
		ps.setString	(1, nome_utente_creatore);				
		ps.setString	(2, luogo);
		ps.setTimestamp	(3, creaTimestamp(data_ora_termine_ultimo_iscrizione));	
		ps.setTimestamp	(4, creaTimestamp(data_ora_inizio_evento));	
		ps.setInt		(5, partecipanti);
		ps.setInt		(6, costo);
		ps.setString	(7, titolo);
		ps.setString	(8, note);
		ps.setString	(9, benefici_quota);
		ps.setTimestamp	(10, creaTimestamp(data_ora_termine_evento));
		ps.setTimestamp	(11, creaTimestamp(data_ora_termine_ritiro_iscrizione));
		ps.setInt		(12, tolleranza_max);
		ps.setString	(13, StatoEvento.APERTA.getString());
		ps.setInt		(14, eta_minima);
		ps.setInt		(15, eta_massima);
		ps.setString	(16, (String)genere);
		
		return ps;
	}


	@Override
	public PreparedStatement getPSInsertIscrizioneUtenteInEvento(Utente utente, Connection con)throws SQLException 
	{
		PreparedStatement ps = con.prepareStatement(Stringhe.ottieniStringaDesiderata(Stringhe.INSERT_SQL_ISCRIZIONE_EVENTO, CategoriaEvento.PARTITA_CALCIO));
		ps.setString(1, utente.getNome());
		ps.setInt(2, getId());
		
		return ps;
	}
}