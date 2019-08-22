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
	
//	Metodi di gestione
	public static final String E_DATABASE = "ERROR : Connessione al Database fallita";	
	public static final String E_ACCEDI = "ERROR : Tentativo di accesso dell'utente %s fallito";	
	public static final String E_AGGIORNATORE = "ERROR : Durante la segnalazione del cambiamento di stato di un evento nel metodo Aggiornatore";	
//	Metodi setter
	public static final String E_INSERT_E = "ERROR  : Non è stato possibile aggiungere l'evento dal nome %s al database";	
	public static final String E_INSERT_U = "ERROR  : Non è stato possibile aggiungere l'utente dal nome %s al database";	
	public static final String E_INSERT_N = "ERROR  : Non è stato possibile aggiungere la notifica dal titolo %s al database";	
	public static final String E_COLLEGAMENTO_U_E = "ERROR  : Non è stato possibile collegare l'utente %s all'evento %s oppure l'utente era precedentemente già iscritto";	
	public static final String E_COLLEGAMENTO_U_C = "ERROR  : Non è stato possibile collegare l'utente %s alla categoria %s";	
	public static final String E_COLLEGAMENTO_U_N = "ERROR  : Non è stato possibile collegare l'utente %s alla notifica %s";	
	public static final String E_COLLEGAMENTO_U_N_PER_E = "ERROR  : Non è stato possibile collegare l'utente %s alla notifica per informarlo dell'evento %s";	
//	Metodi getter
	public static final String E_GET_N = "ERROR  : Non è stato possibile recuperare dal database la lista di notifiche dell'utente %s";	
	public static final String E_GET_POSSIBILI_U_INTERESSATI = "ERROR  : Non è stato possibile recuperare dal database la lista di utenti probabilmente interessati ad eventi dell'utente %s";	
	public static final String E_GET_POSSIBILI_U_INTERESSATI_A_C = "ERROR  : Non è stato possibile recuperare dal database la lista di utenti probabilmente interessati ad eventi dell'utente %s della categoria %s";	
	public static final String E_GET_U_ISCRITTO_IN_E = "ERROR  : Non è stato possibile recuperare dal database se l'utente %s fosse iscritto all'evento %s";	
	public static final String E_GET_E = "ERROR  : Non è stato possibile recuperare dal database la lista degli eventi dell'utente %s";	
//	Metodi Update
	public static final String E_UPDATE_U = "ERRORE : Non è stato possibile aggiornare la fascia di età dell'utente %s";
//	Metodi Delete
	public static final String E_DELETE_N = "ERRORE : Non è stato possibile eliminare la notifica %s dell'utente %s";
	public static final String E_DELETE_U_DA_E = "ERRORE : Non è stato possibile eliminare l'utente %s dall'evento %s";
	public static final String E_DELETE_C_DA_U = "ERRORE : Non è stato possibile eliminare l'utente %s dalla categoria %s";
	public static final String E_DELETE_E = "ERRORE : Non è stato possibile eliminare l'evento %s";
	public static final String E_DELETE_E_ALL_U = "ERRORE : Non è stato possibile eliminare gli eventi dell'utente %s";	
	
	
//	NOTIFICHE
	public static final String TITOLO_FALLIMENTO_EVENTO 	= "Evento %s fallito";
	public static final String NOTIFICA_FALLIMENTO_EVENTO 	= "Caro utente,\nL'evento %s a cui si era iscritto è fallito a causa della scarsa richiesta di partecipazione";
	
	public static final String TITOLO_CONCLUSIONE_EVENTO 	= "Evento %s concluso";	
	public static final String NOTIFICA_CONCLUSIONE_EVENTO 	= "Caro utente,\nL'evento %s a cui si era iscritto è concluso";
	
	public static final String TITOLO_CHIUSURA_EVENTO 		= "Iscrizioni dell'evento %s concluse";	
	public static final String NOTIFICA_CHIUSURA_EVENTO 	= "Caro utente,\nL'evento %s a cui si era iscritto ha raggiunto il numero massimo si iscrizioni, per tanto si svolgerà in data e ora %s \nLe ricordiamo che il costo, tenendo conto delle opzioni da lei scelte sarà di %d euro";
	
	public static final String TITOLO_RITIRO_EVENTO 		= "Evento %s ritirato dal creatore";
	public static final String NOTIFICA_RITIRO_EVENTO 		= "Caro utente,\nSiamo spiacenti: l'evento %s a cui era iscritto è stato ritirato";
	
	public static final String TITOLO_NUOVO_EVENTO 			= "Nuova evento della categoria %s disponbile!";
	public static final String NOTIFICA_NUOVO_EVENTO 		= "Caro utente,\nLa informiamo che un nuovo evento della categoria %s dal nome %s è disponibile";
	
	public static final String TITOLO_INVITO_EVENTO 		= "Sei stato invitato ad un nuovo evento della categoria %s!";
	public static final String NOTIFICA_PER_INVITO_UTENTE	= "Caro utente,\nÉ stato invitato da %s all'evento di nome %s";
}
