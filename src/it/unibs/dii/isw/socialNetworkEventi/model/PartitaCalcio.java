package it.unibs.dii.isw.socialNetworkEventi.model;

import java.util.Calendar;

import it.unibs.dii.isw.socialNetworkEventi.utility.NomeCampi;

public class PartitaCalcio extends Evento 
{
	public PartitaCalcio(
			String luogo,
			Calendar data_ora_termine_ultimo_iscrizione,	
			Calendar data_ora_inizio_evento,
			Integer partecipanti,
			Integer costo,
		    
			String titolo,				
			String note,
			String compreso_nella_quota,
		    Calendar data_ora_termine_evento,
		    
		    Integer eta_minima,
		    Integer eta_massima,
		    String genere
			)
	throws IllegalArgumentException
	{
		super(luogo, data_ora_termine_ultimo_iscrizione, data_ora_inizio_evento, partecipanti, costo, titolo, note, compreso_nella_quota, data_ora_termine_evento);
		
		if(eta_minima <= 0 || eta_massima <= 0 || eta_minima==null || eta_massima==null)     throw new IllegalArgumentException("Necessario inserire un età minima, massima dei partecipanti superiore a 0");
		if(genere == null || !(genere.equalsIgnoreCase("M") || genere.equalsIgnoreCase("F") || genere.equalsIgnoreCase("MF")))throw new IllegalArgumentException("Necessario inserire il genere dei partecipanti");		
		aggiungiCampo(eta_minima, true, NomeCampi.ETA_MINIMA, "Età minima per poter partecipare all'evento");
		aggiungiCampo(eta_massima, true, NomeCampi.ETA_MASSIMA, "Età massima per poter partecipare all'evento");
		aggiungiCampo(genere, true, NomeCampi.GENERE, "Genere richiesto per poter partecipare all'evento");	
	}
}