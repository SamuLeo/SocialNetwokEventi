package it.unibs.dii.isw.socialNetworkEventi.utility;

public class MsgLog 
{
//	public static final String  = ;	
	
//	PASSAGGI DI STATO DEGLI EVENTI
	public static final String APERTO_FALLITO 		= "Stato dell'evento con id : %d passato da APERTO a FALLITO";
	public static final String APERTO_CHIUSO 		= "Stato dell'evento con id : %d passato da APERTO a CHIUSO";
	public static final String CHIUSO_CONCLUSO 		= "Stato dell'evento con id : %d passato da CHIUSO a CONCLUSO";
	public static final String VALIDO_APERTO 		= "Stato dell'evento con id : %d passato da VALIDO a APERTO";
	public static final String APERTO_RITIRATO 		= "Stato dell'evento con id : %d passato da APERTO a RITIRATO";

//	ERRORI SESSIONE
	public static final String E_AGGIUNTA_EVENTO = "ERROR  : Non è stato possibile aggiungere l'evento %s al database";	
	public static final String E_RICERCA_POSSIBILI_U_INTERESSATI = "ERRORE : Non è stato possibile il caricamento dei possibili utenti interessati";
}
