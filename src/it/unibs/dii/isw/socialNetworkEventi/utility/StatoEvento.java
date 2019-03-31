package it.unibs.dii.isw.socialNetworkEventi.utility;

public enum StatoEvento 
{
	VALIDA("Valida"),		//Il form di compilazione è stato completato correttamente
	APERTA("Aperta"),		//Evento non scaduto che può accettare iscritti
	CHIUSA("Chiusa"),		//Evento ha raggiunto il numero massimo di iscritti ma non è ancora passato temporalmente
	CONCLUSA("Conclusa"),	//Evento andato a buon fine e passato
	FALLITA("Fallita"),		//Evento non andato a buon fine
	RITIRATA("Ritirata");	//Evento eliminato per scelta del suo creatore
	
	private String codStatoEvento;
	
	private StatoEvento(String codStatoEvento)
	{
		this.codStatoEvento = codStatoEvento;
	}
	
	public String getString()
	{
		return codStatoEvento;
	}
	
	public static StatoEvento convertiStringInStato(String string_stato)
	{
		switch(string_stato)
		{
		case "Valida" : return StatoEvento.VALIDA;
		case "Aperta" : return StatoEvento.APERTA;
		case "Chiusa" : return StatoEvento.CHIUSA;
		case "Conclusa" : return StatoEvento.CONCLUSA;
		case "Fallita" : return StatoEvento.FALLITA;
		default : return null;
		}
	}
}
