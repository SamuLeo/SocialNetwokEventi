package it.unibs.dii.isw.socialNetworkEventi.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Calendar;

import it.unibs.dii.isw.socialNetworkEventi.utility.CategoriaEvento;
import it.unibs.dii.isw.socialNetworkEventi.utility.NomeCampo;
import it.unibs.dii.isw.socialNetworkEventi.utility.StatoEvento;
import it.unibs.dii.isw.socialNetworkEventi.utility.Stringhe;

public class Scii extends Evento
{
	public Scii(
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
		    Integer tolleranza_max,
		    
		    Integer biglietto_bus,
		    Integer pranzo,
		    Integer affitto_scii
			)
	throws IllegalArgumentException
	{
		super(creatore, luogo, data_ora_termine_ultimo_iscrizione, data_ora_inizio_evento, partecipanti, costo, titolo, note, benefici_quota, data_ora_termine_evento, data_ora_termine_ritiro_iscrizione, tolleranza_max);
		
		if(biglietto_bus < 0) throw new IllegalArgumentException("Necessario inserire costo biglietto bus superiore o uguale a 0");
		if(pranzo < 0) throw new IllegalArgumentException("Necessario inserire costo pranzo superiore o uguale a 0");	
		if(affitto_scii < 0) throw new IllegalArgumentException("Necessario inserire costo affitto sci superiore o uguale a 0");
		aggiungiCampo(biglietto_bus, false, NomeCampo.BIGLIETTO_BUS, "Costo biglietto bus");
		aggiungiCampo(pranzo, false, NomeCampo.PRANZO, "Costo pranzo");
		aggiungiCampo(affitto_scii, false, NomeCampo.AFFITTO_SCII, "Costo affitto sci");
		
		setNomeCategoria(CategoriaEvento.SCII);
	}

	public Scii(
			Integer id_sciata,
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
		    Integer tolleranza_max,
		    StatoEvento stato,
		    
		    Integer biglietto_bus,
		    Integer pranzo,
		    Integer affitto_scii
			)
	{
		this(creatore,luogo,data_ora_termine_ultimo_iscrizione,data_ora_inizio_evento,partecipanti, costo, titolo, note, benefici_quota, data_ora_termine_evento,data_ora_termine_ritiro_iscrizione, tolleranza_max, biglietto_bus, pranzo, affitto_scii);
		setId(id_sciata);
		setStato(stato);
	}
	
	@Override
	public PreparedStatement getPSInsertEvento(Connection con) throws SQLException 
	{
		PreparedStatement ps = con.prepareStatement(Stringhe.ottieniStringaDesiderata(Stringhe.INSERT_SQL_EVENTO, CategoriaEvento.SCII), Statement.RETURN_GENERATED_KEYS);
		
		String nome_utente_creatore						= this.getUtenteCreatore().getNome();
		String luogo 									= (String) this.getCampo(NomeCampo.LUOGO).getContenuto();
		Calendar data_ora_termine_ultimo_iscrizione		= (Calendar) this.getCampo(NomeCampo.D_O_CHIUSURA_ISCRIZIONI).getContenuto();
		Calendar data_ora_inizio_evento 				= (Calendar) this.getCampo(NomeCampo.D_O_INIZIO_EVENTO).getContenuto();
		int partecipanti								= (Integer) this.getCampo(NomeCampo.PARTECIPANTI).getContenuto();
		int costo										= (Integer) this.getCampo(NomeCampo.COSTO).getContenuto();
//			Campi opzionali		
		String titolo									= (String)( this.getCampo(NomeCampo.TITOLO) != null ? this.getCampo(NomeCampo.TITOLO).getContenuto() : null);
		String note										= (String)( this.getCampo(NomeCampo.NOTE) != null ? this.getCampo(NomeCampo.NOTE).getContenuto() : null);
		String benefici_quota							= (String)( this.getCampo(NomeCampo.BENEFICI_QUOTA) != null ? this.getCampo(NomeCampo.BENEFICI_QUOTA).getContenuto() : null);
		Calendar data_ora_termine_evento				= this.getCampo(NomeCampo.D_O_TERMINE_EVENTO) != null ? (Calendar) this.getCampo(NomeCampo.D_O_TERMINE_EVENTO).getContenuto() : null;
		Calendar data_ora_termine_ritiro_iscrizione		= this.getCampo(NomeCampo.D_O_TERMINE_RITIRO_ISCRIZIONE) != null ? (Calendar) this.getCampo(NomeCampo.D_O_TERMINE_RITIRO_ISCRIZIONE).getContenuto() : null;
		int tolleranza_max								= this.getCampo(NomeCampo.TOLLERANZA_MAX) != null ? (Integer)(this.getCampo(NomeCampo.TOLLERANZA_MAX).getContenuto()) : 0;
//			Campi Scii
		int biglietto_bus								= (Integer) this.getCampo(NomeCampo.BIGLIETTO_BUS).getContenuto();
	    int pranzo										= (Integer) this.getCampo(NomeCampo.PRANZO).getContenuto();
	    int affitto_scii								= (Integer) this.getCampo(NomeCampo.AFFITTO_SCII).getContenuto();
	 
		ps.setString	(1, nome_utente_creatore);				
		ps.setString	(2, luogo);
		ps.setTimestamp	(3, this.creaTimestamp(data_ora_termine_ultimo_iscrizione));	
		ps.setTimestamp	(4, this.creaTimestamp(data_ora_inizio_evento));	
		ps.setInt		(5, partecipanti);
		ps.setInt		(6, costo);
		
		ps.setString	(7, titolo);
		ps.setString	(8, note);
		ps.setString	(9, benefici_quota);
		ps.setTimestamp	(10, this.creaTimestamp(data_ora_termine_evento));
		ps.setTimestamp	(11, this.creaTimestamp(data_ora_termine_ritiro_iscrizione));
		ps.setInt		(12, tolleranza_max);				
		ps.setString	(13, StatoEvento.APERTA.getString());
		ps.setInt		(14, biglietto_bus);
		ps.setInt		(15, pranzo);
		ps.setInt		(16, affitto_scii);
		
		return ps;
	}
	
	
	@Override
	public PreparedStatement getPSInsertIscrizioneUtenteInEvento(Utente utente, Connection con)throws Exception 
	{
		PreparedStatement ps = con.prepareStatement(Stringhe.ottieniStringaDesiderata(Stringhe.INSERT_SQL_ISCRIZIONE_EVENTO, CategoriaEvento.SCII));
		ps.setString(1, utente.getNome());
		ps.setInt(2, this.getId());
		boolean utente_con_campi_opt_settati = false;
		for(Utente u : this.getPartecipanti_campiOpt().keySet())
		{
		if(u.equals(utente))
			{
				utente_con_campi_opt_settati = true; 
				utente=u; 
				break;
			}
		}
		if(utente_con_campi_opt_settati)
		{
			ps.setBoolean(3,(Boolean)this.getPartecipanti_campiOpt().get(utente).get(NomeCampo.BIGLIETTO_BUS));
			ps.setBoolean(4,(Boolean)this.getPartecipanti_campiOpt().get(utente).get(NomeCampo.PRANZO));
			ps.setBoolean(5,(Boolean)this.getPartecipanti_campiOpt().get(utente).get(NomeCampo.AFFITTO_SCII));
		}
		else
			throw new Exception("Necessario inserire le scelte dell'utente riguardo i campi opzionali");
					
		return ps;
	}
}