package it.unibs.dii.isw.socialNetworkEventi.utility;

public class Stringhe 
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
	public static final String E_MSG_FACTORY = "ERROR : Creazione IMessagesFactory fallita";	
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

//	PERCORSI FILE ESTERNI
	public static final String PERCORSO_FILE_LOG_LINUX 		= "Dati//file_log//log_sessione.log";
	public static final String PERCORSO_FILE__ERROR_LOG_LINUX 	= "Dati//file_log//error_sessione.log";
	public static final String PERCORSO_FILE_CONFIG_LINUX 	= "Dati//file_config//SocialNetwork.properties";

	public static final String PERCORSO_FILE_LOG_WIN		= "Dati\\file_log\\log_sessione.log";
	public static final String PERCORSO_FILE_ERROR_LOG_WIN		= "Dati\\file_log\\error_sessione.log";
	public static final String PERCORSO_FILE_CONFIG_WIN 	= "Dati\\file_config\\SocialNetwork.properties";

//	NOMI TABELLE DB
	public static String NOME_TABELLA_NOTIFICA = System.getProperty("nome_tabella_notifica");
	public static String NOME_TABELLA_PARTITA_CALCIO = System.getProperty("nome_tabella_partita_calcio");
	public static String NOME_TABELLA_RELAZIONE_UTENTE_NOME_CATEGORIA = System.getProperty("nome_tabella_relazione_utente_nome_categoria");
	public static String NOME_TABELLA_RELAZIONE_UTENTE_NOTIFICA = System.getProperty("nome_tabella_relazione_utente_notifica");
	public static String NOME_TABELLA_RELAZIONE_UTENTE_PARTITA_CALCIO = System.getProperty("nome_tabella_relazione_utente_partita_calcio");
	public static String NOME_TABELLA_RELAZIONE_UTENTE_SCII = System.getProperty("nome_tabella_reazione_utente_scii");
	public static String NOME_TABELLA_SCII = System.getProperty("nome_tabella_scii");
	public static String NOME_TABELLA_UTENTE = System.getProperty("nome_tabella_utente");
	
//	STRINGHE SQL
	
//	Insert
	public static final String INSERT_SQL_PARTITA_CALCIO = "INSERT INTO " + NOME_TABELLA_PARTITA_CALCIO 
			+ " (nome_utente_creatore, luogo, data_ora_termine_ultimo_iscrizione, data_ora_inizio_evento, partecipanti, costo, titolo, note,"
			+ "benefici_quota, data_ora_termine_evento, data_ora_termine_ritiro_iscrizione, tolleranza_max, stato, eta_minima, eta_massima, genere)"
			+ " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
	public static final String INSERT_SQL_SCII = "INSERT INTO " + NOME_TABELLA_SCII
			+ " (nome_utente_creatore, luogo, data_ora_termine_ultimo_iscrizione, data_ora_inizio_evento, partecipanti, costo, titolo, note,"
			+ "benefici_quota, data_ora_termine_evento, data_ora_termine_ritiro_iscrizione, tolleranza_max, stato, biglietto_bus, pranzo, affitto_scii)"
			+ " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
	public static final String INSERT_SQL_UTENTE = "INSERT INTO " + NOME_TABELLA_UTENTE + " (nome, password, eta_min, eta_max) VALUES (?,?,?,?)";
	public static final String INSERT_SQL_NOTIFICA = "INSERT INTO " + NOME_TABELLA_NOTIFICA + " (titolo,contenuto,data) VALUES (?,?,?)";
	public static final String INSERT_SQL_UTENTE_NOTIFICA ="INSERT INTO " + NOME_TABELLA_RELAZIONE_UTENTE_NOTIFICA + " (nome_utente, id_notifica) VALUES (?,?)";
	public static final String INSERT_SQL_UTENTE_PARTITA_CALCIO = "INSERT INTO " + NOME_TABELLA_RELAZIONE_UTENTE_PARTITA_CALCIO + " (nome_utente, id_evento) VALUES (?,?)";	
	public static final String INSERT_SQL_UTENTE_SCII =	"INSERT INTO " + NOME_TABELLA_RELAZIONE_UTENTE_SCII + " (nome_utente, id_evento, biglietto_bus, pranzo, affitto_scii) VALUES (?,?,?,?,?)";	
	public static final String INSERT_SQL_UTENTE_CATEGORIA = "INSERT INTO " +  NOME_TABELLA_RELAZIONE_UTENTE_NOME_CATEGORIA + " (nome_utente, nome_categoria) VALUES (?,?)";

//	Select
	public static final String SELECT_SQL_PARTITE_CALCIO = "SELECT id, nome_utente_creatore, luogo, data_ora_termine_ultimo_iscrizione, data_ora_inizio_evento, partecipanti,"
			+ " costo, titolo, note, benefici_quota, data_ora_termine_evento, data_ora_termine_ritiro_iscrizione, tolleranza_max, stato, eta_minima, eta_massima, genere "
			+ "FROM " + NOME_TABELLA_PARTITA_CALCIO;
	public static final String SELECT_SQL_SCII = "SELECT id, nome_utente_creatore, luogo, data_ora_termine_ultimo_iscrizione, data_ora_inizio_evento, partecipanti,"
			+ " costo, titolo, note, benefici_quota, data_ora_termine_evento, data_ora_termine_ritiro_iscrizione,"
			+ " tolleranza_max, stato, biglietto_bus, pranzo, affitto_scii FROM " + NOME_TABELLA_SCII;
	public static final String SELECT_SQL_UTENTI = "SELECT nome, password, eta_min, eta_max FROM " + NOME_TABELLA_UTENTE;
	public static final String SELECT_SQL_NOTIFICA = "SELECT titolo, contenuto, data FROM "+ NOME_TABELLA_NOTIFICA +" WHERE id=?";
	public static final String SELECT_SQL_NOTIFICHE_UTENTE = "SELECT id_notifica FROM " + NOME_TABELLA_RELAZIONE_UTENTE_NOTIFICA + " WHERE nome_utente=?";
	public static final String SELECT_SQL_UTENTI_PARTITA_CALCIO = "SELECT nome_utente FROM " + NOME_TABELLA_RELAZIONE_UTENTE_PARTITA_CALCIO + " WHERE id_evento=?";
	public static final String SELECT_SQL_UTENTI_SCII = "SELECT nome_utente, biglietto_bus, pranzo, affitto_scii FROM " + NOME_TABELLA_RELAZIONE_UTENTE_SCII + " WHERE id_evento=?";
	public static final String SELECT_SQL_CATEGORIE_UTENTE = "SELECT nome_categoria FROM " + NOME_TABELLA_RELAZIONE_UTENTE_NOME_CATEGORIA + " WHERE nome_utente=?";
	public static final String SELECT_SQL_UTENTI_CATEGORIA = "SELECT nome_utente FROM " + NOME_TABELLA_RELAZIONE_UTENTE_NOME_CATEGORIA + " WHERE nome_categoria=?";
	public static final String SELECT_SQL_UTENTI_PASSATI_PARTITA_CALCIO= "SELECT DISTINCT nome_utente FROM " + NOME_TABELLA_RELAZIONE_UTENTE_PARTITA_CALCIO + " WHERE nome_utente!=? AND id_evento in (SELECT id FROM " + NOME_TABELLA_PARTITA_CALCIO + " WHERE nome_utente_creatore=?)";
	public static final String SELECT_SQL_UTENTI_PASSATI_SCII = "SELECT DISTINCT nome_utente FROM " + NOME_TABELLA_RELAZIONE_UTENTE_SCII + " WHERE nome_utente!=? AND id_evento in (SELECT id FROM " + NOME_TABELLA_SCII + " WHERE nome_utente_creatore=?)";
	public static final String SELECT_SQL_UTENTI_DI_NOTIFICA = "SELECT nome_utente FROM " + NOME_TABELLA_RELAZIONE_UTENTE_NOTIFICA + " WHERE id_notifica=?";
	
//	Update
	public static final String UPDATE_SQL_STATO_PARTITA_CALCIO = "UPDATE " + NOME_TABELLA_PARTITA_CALCIO + " SET stato = ? WHERE id = ?";
	public static final String UPDATE_SQL_STATO_SCII = "UPDATE " + NOME_TABELLA_SCII + " SET stato = ? WHERE id = ?";
	public static final String UPDATE_SQL_ETA_MIN_UTENTE= "UPDATE " + NOME_TABELLA_UTENTE + " SET eta_min = ? WHERE nome = ?";
	public static final String UPDATE_SQL_ETA_MAX_UTENTE= "UPDATE " + NOME_TABELLA_UTENTE + " SET eta_max = ? WHERE nome = ?";
	
//	Delete
	public static final String DELETE_SQL_PARTITA_CALCIO = "DELETE FROM " + NOME_TABELLA_PARTITA_CALCIO + " WHERE id = ?";
	public static final String DELETE_SQL_SCII = "DELETE FROM " + NOME_TABELLA_SCII + " WHERE id = ?";
	public static final String DELETE_SQL_UTENTE = "DELETE FROM " + NOME_TABELLA_UTENTE + " WHERE id = ?";
	public static final String DELETE_SQL_NOTIFICA = "DELETE FROM " + NOME_TABELLA_NOTIFICA + " WHERE id = ?";
	public static final String DELETE_SQL_RELAZIONE_UTENTE_NOTIFICA = "DELETE FROM " + NOME_TABELLA_RELAZIONE_UTENTE_NOTIFICA + " WHERE nome_utente=? AND id_notifica=?";
	public static final String DELETE_SQL_RELAZIONE_UTENTE_PARTITA_CALCIO = "DELETE FROM " + NOME_TABELLA_RELAZIONE_UTENTE_PARTITA_CALCIO + " WHERE nome_utente=? AND id_evento=?";
	public static final String DELETE_SQL_RELAZIONE_UTENTE_SCII = "DELETE FROM " + NOME_TABELLA_RELAZIONE_UTENTE_SCII + " WHERE nome_utente=? AND id_evento=?";
	public static final String DELETE_SQL_RELAZIONE_UTENTE_CATEGORIA = "DELETE FROM " + NOME_TABELLA_RELAZIONE_UTENTE_NOME_CATEGORIA + " WHERE nome_utente=? AND nome_categoria=?";
	public static final String DELETE_SQL_PARTITE_UTENTE = "DELETE FROM " + NOME_TABELLA_RELAZIONE_UTENTE_PARTITA_CALCIO + " WHERE nome_utente_creatore = ?";
	public static final String DELETE_SQL_SCIATE_UTENTE = "DELETE FROM " + NOME_TABELLA_RELAZIONE_UTENTE_SCII + " WHERE nome_utente_creatore = ?";

	//	public static final String _SQL_= ;

}