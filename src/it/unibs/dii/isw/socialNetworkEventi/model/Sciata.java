package it.unibs.dii.isw.socialNetworkEventi.model;

import java.util.Calendar;

import it.unibs.dii.isw.socialNetworkEventi.utility.CategorieEvento;
import it.unibs.dii.isw.socialNetworkEventi.utility.NomeCampi;
import it.unibs.dii.isw.socialNetworkEventi.utility.StatoEvento;

public class Sciata extends Evento
{
	public Sciata(
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
		    Integer affitto_sci
			)
	throws IllegalArgumentException
	{
		super(creatore, luogo, data_ora_termine_ultimo_iscrizione, data_ora_inizio_evento, partecipanti, costo, titolo, note, benefici_quota, data_ora_termine_evento, data_ora_termine_ritiro_iscrizione, tolleranza_max);
		
		if(biglietto_bus < 0) throw new IllegalArgumentException("Necessario inserire costo biglietto bus superiore o uguale a 0");
		if(pranzo < 0) throw new IllegalArgumentException("Necessario inserire costo pranzo superiore o uguale a 0");	
		if(affitto_sci < 0) throw new IllegalArgumentException("Necessario inserire costo affitto sci superiore o uguale a 0");
		aggiungiCampo(biglietto_bus, false, NomeCampi.BIGLIETTO_BUS, "Costo biglietto bus");
		aggiungiCampo(pranzo, false, NomeCampi.PRANZO, "Costo pranzo");
		aggiungiCampo(affitto_sci, false, NomeCampi.AFFITTO_SCI, "Costo affitto sci");
		
		setNomeCategoria(CategorieEvento.SCIATA);
	}
	
	

		public Sciata(
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
			    Integer affitto_sci
				)
		{
			this(creatore,luogo,data_ora_termine_ultimo_iscrizione,data_ora_inizio_evento,partecipanti, costo, titolo, note, benefici_quota, data_ora_termine_evento,data_ora_termine_ritiro_iscrizione, tolleranza_max, biglietto_bus, pranzo, affitto_sci);
			setId(id_sciata);
			setStato(stato);
		}
}
