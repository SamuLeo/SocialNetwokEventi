package it.unibs.dii.isw.socialNetworkEventi.utility;

public enum CategoriaEvento 
{

	DEFAULT("Default"),
	PARTITA_CALCIO("partita_calcio"), //WARNING : non cambiare la string, coincide con il nome della tabella a livello DB
	SCII("scii");
	
	private String codCategorieEventi;
	
	private CategoriaEvento(String codCategorieEventi)
	{
		this.codCategorieEventi = codCategorieEventi;
	}
	
	public String getString()
	{
		return codCategorieEventi;
	}
	
	public static  CategoriaEvento convertiStringInCategoria(String string_categoria)
	{
		switch(string_categoria)
		{
		case "partita_calcio" : return CategoriaEvento.PARTITA_CALCIO;
		case "scii" : return CategoriaEvento.SCII;
		case "Default" : return CategoriaEvento.DEFAULT;
		default : return null;
		}
	}
}
