package it.unibs.dii.isw.socialNetworkEventi.utility;

public enum CategorieEvento 
{

	DEFAULT("Default"),
	PARTITA_CALCIO("partita_calcio"), //WARNING : non cambiare la string, coincide con il nome della tabella a livello DB
	SCIATA("sciata");
	
	private String codCategorieEventi;
	
	private CategorieEvento(String codCategorieEventi)
	{
		this.codCategorieEventi = codCategorieEventi;
	}
	
	public String getString()
	{
		return codCategorieEventi;
	}
	
	public static  CategorieEvento convertiStringInCategoria(String string_categoria)
	{
		switch(string_categoria)
		{
		case "partita_calcio" : return CategorieEvento.PARTITA_CALCIO;
		case "sciata" : return CategorieEvento.SCIATA;
		case "Default" : return CategorieEvento.DEFAULT;
		default : return null;
		}
	}
}
