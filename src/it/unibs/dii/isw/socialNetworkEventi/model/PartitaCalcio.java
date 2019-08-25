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

public class PartitaCalcio extends Evento 
{		
	public PartitaCalcio(
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
		    
		    Integer eta_minima,
		    Integer eta_massima,
		    String genere
			)
	throws IllegalArgumentException
	{
		super(creatore, luogo, data_ora_termine_ultimo_iscrizione, data_ora_inizio_evento, partecipanti, costo, titolo, note, benefici_quota, data_ora_termine_evento, data_ora_termine_ritiro_iscrizione, tolleranza_max);
		
		if(eta_minima==null || eta_massima==null || eta_minima <= 0 || eta_massima <= 0 ||  eta_minima>eta_massima)     throw new IllegalArgumentException("Necessario inserire un età minima, massima dei partecipanti superiore a 0");
		if(genere == null || !(genere.equalsIgnoreCase("maschi") || genere.equalsIgnoreCase("femmine") || genere.equalsIgnoreCase("qualsiasi")))throw new IllegalArgumentException("Necessario inserire il genere dei partecipanti");		
		aggiungiCampo(eta_minima, true, NomeCampo.ETA_MINIMA, "Età minima");
		aggiungiCampo(eta_massima, true, NomeCampo.ETA_MASSIMA, "Età massima");
		aggiungiCampo(genere, true, NomeCampo.GENERE, "Genere richiesto");
		
		setNomeCategoria(CategoriaEvento.PARTITA_CALCIO);
	}
	
	

		public PartitaCalcio(
				Integer id_partita,
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
			    
			    Integer eta_minima,
			    Integer eta_massima,
			    String genere
				)
		{
			this(creatore,luogo,data_ora_termine_ultimo_iscrizione,data_ora_inizio_evento,partecipanti, costo, titolo, note, benefici_quota, data_ora_termine_evento,data_ora_termine_ritiro_iscrizione, tolleranza_max, eta_minima, eta_massima, genere);
			setId(id_partita);
			setStato(stato);
		}

		@Override
		public PreparedStatement getPSInsertEvento(Connection con) throws SQLException 
		{
			PreparedStatement ps = con.prepareStatement(Stringhe.INSERT_SQL_PARTITA_CALCIO, Statement.RETURN_GENERATED_KEYS);
			
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
//			Campi Partita Calcio
			int eta_minima									= (Integer) this.getCampo(NomeCampo.ETA_MINIMA).getContenuto();
		    int eta_massima									= (Integer) this.getCampo(NomeCampo.ETA_MASSIMA).getContenuto();
		    String genere									= (String) this.getCampo(NomeCampo.GENERE).getContenuto();

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
			ps.setInt		(14, eta_minima);
			ps.setInt		(15, eta_massima);
			ps.setString	(16, (String)genere);
			
			return ps;
		}


		@Override
		public PreparedStatement getPSInsertIscrizioneUtenteInEvento(Utente utente, Connection con)throws SQLException 
		{
			PreparedStatement ps = con.prepareStatement(Stringhe.INSERT_SQL_UTENTE_PARTITA_CALCIO);
			ps.setString(1, utente.getNome());
			ps.setInt(2, this.getId());
			
			return ps;
		}

		@Override
		public PreparedStatement getPSSelectUtenti(Connection con) throws SQLException 
		{
			PreparedStatement ps = con.prepareStatement(Stringhe.SELECT_SQL_UTENTI_PARTITA_CALCIO);
			ps.setInt(1, this.getId());
			
			return ps;
		}


		@Override
		public PreparedStatement getPSUpdateStatoEvento(Connection con) throws SQLException 
		{
			PreparedStatement ps = con.prepareStatement(Stringhe.UPDATE_SQL_STATO_PARTITA_CALCIO);
			ps.setString(1, this.getStato().getString());
			ps.setInt(2, this.getId());		
			
			return ps;
		}


		@Override
		public PreparedStatement getPSDeleteEvento(Connection con) throws SQLException 
		{
			PreparedStatement ps = con.prepareStatement(Stringhe.DELETE_SQL_PARTITA_CALCIO);
			ps.setInt(1, this.getId());
				
			return ps;
		}



		@Override
		public PreparedStatement getPSDeleteRelazioneEventoUtente(String nome_utente, Connection con) throws SQLException 
		{
			PreparedStatement ps = con.prepareStatement(Stringhe.DELETE_SQL_RELAZIONE_UTENTE_PARTITA_CALCIO);
			ps.setString(1, nome_utente);
			ps.setInt(2, this.getId());	
			
			return ps;
		}

}
