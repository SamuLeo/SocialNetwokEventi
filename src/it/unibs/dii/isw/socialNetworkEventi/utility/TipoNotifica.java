package it.unibs.dii.isw.socialNetworkEventi.utility;

public enum TipoNotifica 
{

	GENERICA(0);
	
	private int codNotifica;
	
	private TipoNotifica(int codNotifica)
	{
		this.codNotifica = codNotifica;
	}
	
	public int getCodNomeCampi()
	{
		return codNotifica;
	}
}
