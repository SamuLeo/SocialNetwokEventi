package it.unibs.dii.isw.socialNetworkEventi.utility;

public enum StatoEvento 
{

	VALIDA("Valida"),
	APERTA("Aperta"),
	CHIUSA("Chiusa"),
	CONCLUSA("Conclusa"),
	FALLITA("Fallita");
	
	private String codStatoEvento;
	
	private StatoEvento(String codStatoEvento)
	{
		this.codStatoEvento = codStatoEvento;
	}
	
	public String getCodNomeCampi()
	{
		return codStatoEvento;
	}
}
