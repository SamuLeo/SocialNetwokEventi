package it.unibs.dii.isw.socialNetworkEventi.utility;

public enum NomeCampo
{
//	Campi comuni a qualsiasi evento
	LUOGO(0),
	D_O_CHIUSURA_ISCRIZIONI(1),
	D_O_INIZIO_EVENTO(2),
	PARTECIPANTI(3),
	COSTO(4), 
	TITOLO(5), 
	NOTE(6), 
	BENEFICI_QUOTA(7),
	D_O_TERMINE_EVENTO(8),
	D_O_TERMINE_RITIRO_ISCRIZIONE(9),
	TOLLERANZA_MAX(10),
//	Campi dell'evento partita di calcio
	ETA_MINIMA(11),
	ETA_MASSIMA(12),
	GENERE(13),
//	Campi dell'evento sciata
	BIGLIETTO_BUS(14),
	PRANZO(15),
	AFFITTO_SCII(16);

	
	private int codCampo;
	
	private NomeCampo(int codCampo)
	{
		this.codCampo = codCampo;
	}
	
	public int getCodNomeCampi()
	{
		return codCampo;
	}
}
