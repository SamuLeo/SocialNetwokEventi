package socialNetwork.view;

public enum NomeCampi
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
//	Campi dell'evento partita di calcio
	ETA_MINIMA(9),
	ETA_MASSIMA(10),
	GENERE(11);

	
	private int codCampo;
	
	private NomeCampi(int codCampo)
	{
		this.codCampo = codCampo;
	}
	
	public int getCodNomeCampi()
	{
		return codCampo;
	}
}
