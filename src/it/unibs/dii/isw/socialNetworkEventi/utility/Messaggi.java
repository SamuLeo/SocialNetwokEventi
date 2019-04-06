package it.unibs.dii.isw.socialNetworkEventi.utility;

public class Messaggi 
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

	
//	NOTIFICHE
	public static final String TITOLO_FALLIMENTO_EVENTO 	= "Evento %s fallito";
	public static final String NOTIFICA_FALLIMENTO_EVENTO 	= "Caro utente,\n l'evento %s a cui si era iscritto è fallito a causa della scarsa richiesta di partecipazione";
	
	public static final String TITOLO_CONCLUSIONE_EVENTO 	= "Evento %s concluso";	
	public static final String NOTIFICA_CONCLUSIONE_EVENTO 	= "Caro utente,\n l'evento %s a cui si era iscritto è concluso";
	
	public static final String TITOLO_CHIUSURA_EVENTO 		= "Iscrizioni dell'evento %s concluse";	
	public static final String NOTIFICA_CHIUSURA_EVENTO 	= "Caro utente,\n l'evento %s a cui si era iscritto ha raggiunto il numero massimo si iscrizioni, per tanto si svolgerà in data %s \nLe ricordiamo che il costo, tenendo conto delle opzioni da lei scelte sarà di %d";
	
	public static final String TITOLO_RITIRO_EVENTO 		= "Evento %s ritirato dal creatore";
	public static final String NOTIFICA_RITIRO_EVENTO 		= "Caro utente,\n siamo spiacenti: l'evento %s a cui era iscritto è stato ritirato";
	
	public static final String TITOLO_NUOVO_EVENTO 			= "Nuova evento della categoria %s disponbile!";
	public static final String NOTIFICA_NUOVO_EVENTO 		= "Caro utente, la informiamo che un nuovo evento della categoria %s dal nome %s è disponibile";
	
	public static final String TITOLO_INVITO_EVENTO 		= "Sei stato invitato ad un nuovo evento della categoria %s!";
	public static final String NOTIFICA_PER_INVITO_UTENTE	= "Caro utente, è stato invitato da %s all'evento di nome %s";
}
