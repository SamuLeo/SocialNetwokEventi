package it.unibs.dii.isw.socialNetworkEventi.model;

import java.util.Calendar;

import it.unibs.dii.isw.socialNetworkEventi.utility.CategorieEvento;
import it.unibs.dii.isw.socialNetworkEventi.utility.NomeCampi;
import it.unibs.dii.isw.socialNetworkEventi.utility.StatoEvento;

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
		aggiungiCampo(eta_minima, true, NomeCampi.ETA_MINIMA, "Età minima");
		aggiungiCampo(eta_massima, true, NomeCampi.ETA_MASSIMA, "Età massima");
		aggiungiCampo(genere, true, NomeCampi.GENERE, "Genere richiesto");
		
		setNomeCategoria(CategorieEvento.PARTITA_CALCIO);
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
		
		
}
