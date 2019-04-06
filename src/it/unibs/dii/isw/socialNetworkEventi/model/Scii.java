package it.unibs.dii.isw.socialNetworkEventi.model;

import java.util.Calendar;

import it.unibs.dii.isw.socialNetworkEventi.utility.CategorieEvento;
import it.unibs.dii.isw.socialNetworkEventi.utility.NomeCampi;
import it.unibs.dii.isw.socialNetworkEventi.utility.StatoEvento;

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
		aggiungiCampo(biglietto_bus, false, NomeCampi.BIGLIETTO_BUS, "Costo biglietto bus");
		aggiungiCampo(pranzo, false, NomeCampi.PRANZO, "Costo pranzo");
		aggiungiCampo(affitto_scii, false, NomeCampi.AFFITTO_SCII, "Costo affitto sci");
		
		setNomeCategoria(CategorieEvento.SCII);
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
}
