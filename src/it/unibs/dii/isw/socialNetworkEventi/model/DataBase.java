package it.unibs.dii.isw.socialNetworkEventi.model;

import java.awt.Color;
import java.awt.Font;
import java.awt.Toolkit;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import com.mysql.cj.jdbc.MysqlDataSource;
import java.util.ArrayList;

import it.unibs.dii.isw.socialNetworkEventi.utility.CategorieEvento;
import it.unibs.dii.isw.socialNetworkEventi.utility.Messaggi;
import it.unibs.dii.isw.socialNetworkEventi.utility.NomeCampi;
import it.unibs.dii.isw.socialNetworkEventi.utility.StatoEvento;

public class DataBase 
{	
//	WARNING : modificare questa matrice solo in corrispondenza di variazioni di nomi di tabelle a livello DB
	private static final String tabelle_db_eventi[][] 		= {{CategorieEvento.PARTITA_CALCIO.getString(),	"relazione_utente_" + CategorieEvento.PARTITA_CALCIO.getString()},
															   {CategorieEvento.SCII.getString(),			"relazione_utente_" + CategorieEvento.SCII.getString()}};
	
	private Connection con;
	private HashMap<CategorieEvento,ArrayList<Evento>> eventi;
	private ArrayList<Utente> utenti;
	public HashMap<CategorieEvento,ArrayList<Evento>> getEventi() {return eventi;}
	public ArrayList<Utente> getUtenti() {return utenti;}
	
//	Connessione a mysql creata tramite pattern Singleton
	public Connection getConnection() throws SQLException
	{
		if(con == null)
		{
			MysqlDataSource dataSource = new MysqlDataSource();
//			specifica dei dettagli necessari alla connessione
			dataSource.setDatabaseName("social_network_db");
			dataSource.setPortNumber(3306);
			dataSource.setServerName("localhost");
			dataSource.setUser("admin_social");
			dataSource.setPassword("StefanoLoveLinux");
			
			try {con = dataSource.getConnection();}
			catch (SQLException e) {
				e.printStackTrace();
				Font f = new Font("sans", Font.PLAIN, Toolkit.getDefaultToolkit().getScreenResolution()/6);
				UIManager.put("OptionPane.messageFont", f);
				UIManager.put("OptionPane.buttonFont", f);
				UIManager.put("Button.background", Color.white);
				UIManager.put("Button.select", new Color(240,255,245));
				JOptionPane.showMessageDialog(null, "Impossibile connettersi alla base di dati", "Errore di connessione", JOptionPane.ERROR_MESSAGE);
				System.exit(1);
			}
			
			eventi = new HashMap<CategorieEvento,ArrayList<Evento>>();
			utenti = new ArrayList<>();
		}
		return con;
	}
	

	public void refreshDatiRAM() throws SQLException
	{
		utenti = selectUtentiAll();
		eventi = selectEventiAll();
	}
	
	public void initializeDatiRAM() throws SQLException
	{
		utenti = selectUtentiAll();
		eventi = selectEventiAll();
		utenti = selectUtentiAll();
	}

	
/*
 * OPERAZIONI C.R.U.D. SU DB MYSQL
 */
	

		
/*
 * CREATE : OPERAZIONI DI INSERT	
 */
	
	public Evento insertEvento(Evento evento) throws SQLException
	{
//		Estrazione campi dall'oggetto evento(comuni a tutte le categorie)
//		Campi obbligatori
		String nome_utente_creatore						= evento.getUtenteCreatore().getNome();
		String luogo 									= (String) evento.getCampo(NomeCampi.LUOGO).getContenuto();
		Calendar data_ora_termine_ultimo_iscrizione		= (Calendar) evento.getCampo(NomeCampi.D_O_CHIUSURA_ISCRIZIONI).getContenuto();
		Calendar data_ora_inizio_evento 				= (Calendar) evento.getCampo(NomeCampi.D_O_INIZIO_EVENTO).getContenuto();
		int partecipanti								= (Integer) evento.getCampo(NomeCampi.PARTECIPANTI).getContenuto();
		int costo										= (Integer) evento.getCampo(NomeCampi.COSTO).getContenuto();
//		Campi opzionali		
		String titolo									= (String)( evento.getCampo(NomeCampi.TITOLO) != null ? evento.getCampo(NomeCampi.TITOLO).getContenuto() : null);
		String note										= (String)( evento.getCampo(NomeCampi.NOTE) != null ? evento.getCampo(NomeCampi.NOTE).getContenuto() : null);
		String benefici_quota							= (String)( evento.getCampo(NomeCampi.BENEFICI_QUOTA) != null ? evento.getCampo(NomeCampi.BENEFICI_QUOTA).getContenuto() : null);
		Calendar data_ora_termine_evento				= evento.getCampo(NomeCampi.D_O_TERMINE_EVENTO) != null ? (Calendar) evento.getCampo(NomeCampi.D_O_TERMINE_EVENTO).getContenuto() : null;
		Calendar data_ora_termine_ritiro_iscrizione		= evento.getCampo(NomeCampi.D_O_TERMINE_RITIRO_ISCRIZIONE) != null ? (Calendar) evento.getCampo(NomeCampi.D_O_TERMINE_RITIRO_ISCRIZIONE).getContenuto() : null;
		int tolleranza_max								= evento.getCampo(NomeCampi.TOLLERANZA_MAX) != null ? (Integer)(evento.getCampo(NomeCampi.TOLLERANZA_MAX).getContenuto()) : 0;

		PreparedStatement ps;
		String sql;
		
		switch(evento.getNomeCategoria())
		{
		case PARTITA_CALCIO : 
			{

				int eta_minima									= (Integer) evento.getCampo(NomeCampi.ETA_MINIMA).getContenuto();
			    int eta_massima									= (Integer) evento.getCampo(NomeCampi.ETA_MASSIMA).getContenuto();
			    String genere									= (String) evento.getCampo(NomeCampi.GENERE).getContenuto();
			    
//				Stringa contenente script sql per inserire la partita di calcio
				sql = "INSERT INTO " + tabelle_db_eventi[0][0] 
						+ " (nome_utente_creatore, luogo, data_ora_termine_ultimo_iscrizione, data_ora_inizio_evento, partecipanti, costo, titolo, note,"
						+ "benefici_quota, data_ora_termine_evento, data_ora_termine_ritiro_iscrizione, tolleranza_max, stato, eta_minima, eta_massima, genere)"
						+ " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
				
				ps = getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);				
				ps.setInt		(14, eta_minima);
				ps.setInt		(15, eta_massima);
				ps.setString	(16, (String)genere);
				break;
			}
		case SCII : 
			{
				int biglietto_bus							= (Integer) evento.getCampo(NomeCampi.BIGLIETTO_BUS).getContenuto();
			    int pranzo									= (Integer) evento.getCampo(NomeCampi.PRANZO).getContenuto();
			    int affitto_scii							= (Integer) evento.getCampo(NomeCampi.AFFITTO_SCII).getContenuto();
			    
//				Stringa contenente script sql per inserire la partita di calcio
				sql = "INSERT INTO " + tabelle_db_eventi[1][0]
						+ " (nome_utente_creatore, luogo, data_ora_termine_ultimo_iscrizione, data_ora_inizio_evento, partecipanti, costo, titolo, note,"
						+ "benefici_quota, data_ora_termine_evento, data_ora_termine_ritiro_iscrizione, tolleranza_max, stato, biglietto_bus, pranzo, affitto_scii)"
						+ " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
				
//				script contenente la stringa sql precedentemente specificata inviato al DB, con prevenzione SQL Injection	
				ps = getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
				ps.setInt		(14, biglietto_bus);
				ps.setInt		(15, pranzo);
				ps.setInt		(16, affitto_scii);
				break;
			}
		default : return null;
		}
		
//		script contenente la stringa sql precedentemente specificata inviato al DB, con prevenzione SQL Injection	
		ps.setString	(1, nome_utente_creatore);				
		ps.setString	(2, luogo);
		ps.setTimestamp	(3, this.creaTimestamp(data_ora_termine_ultimo_iscrizione));	
		ps.setTimestamp	(4, this.creaTimestamp(data_ora_inizio_evento));	
		ps.setInt		(5, partecipanti);
		ps.setInt		(6, costo);
		
		ps.setString	(7, titolo);
		ps.setString	(8, note);
		ps.setString	(9, benefici_quota);
		ps.setTimestamp	(10, this.creaTimestamp(data_ora_termine_evento));
		ps.setTimestamp	(11, this.creaTimestamp(data_ora_termine_ritiro_iscrizione));
		ps.setInt		(12, tolleranza_max);				
		ps.setString	(13, StatoEvento.APERTA.getString());
		
		ps.executeUpdate();
		ResultSet rs = ps.getGeneratedKeys();

		rs.next();
		evento.setId(rs.getInt(1));
		
		eventi = selectEventiAll(); 

		return evento;

	}

	public void insertUtente(Utente utente) throws SQLException
	{
		String sql = "INSERT INTO utente (nome, password, eta_min, eta_max) VALUES (?,?,?,?)";
		
		PreparedStatement ps = getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
		ps.setString(1, utente.getNome());
		ps.setString(2, utente.getPassword());		
		ps.setInt(3, utente.getEtaMin());	
		ps.setInt(4, utente.getEtaMax());		

		ps.executeUpdate();
		
		utenti = selectUtentiAll();
	}
	
	public Notifica insertNotifica(Notifica notifica) throws SQLException
	{
		String titolo = notifica.getTitolo();
		String contenuto = notifica.getContenuto();
		Calendar data = notifica.getData();
	    
		String sql = "INSERT INTO notifica (titolo,contenuto,data) VALUES (?,?,?)";
		
		PreparedStatement ps = getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
		ps.setString(1, titolo);
		ps.setString(2, contenuto);		
		ps.setTimestamp(3, this.creaTimestamp(data));
		
		ps.executeUpdate();
		ResultSet rs = ps.getGeneratedKeys();	
		rs.next();
		notifica.setIdNotifica(rs.getInt(1));
		return notifica;
	}
	
	
	public void collegaUtenteNotifica(String nome_utente, int id_notifica) throws SQLException
	{	    
		String sql = "INSERT INTO relazione_utente_notifica (nome_utente, id_notifica) VALUES (?,?)";
		PreparedStatement ps = getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
		ps.setString(1, nome_utente);
		ps.setInt(2, id_notifica);		
		ps.executeUpdate();
	}
	
	public void collegaUtenteEvento(Utente utente, Evento evento) throws Exception
	{	
		String sql = null;
		
		switch(evento.getNomeCategoria())
		{
		case PARTITA_CALCIO :
			{
				sql = "INSERT INTO " + tabelle_db_eventi[0][1] + " (nome_utente, id_evento) VALUES (?,?)";	
				PreparedStatement ps = getConnection().prepareStatement(sql);
				ps.setString(1, utente.getNome());
				ps.setInt(2, evento.getId());
				
				ps.executeUpdate();
				break;
			}
		case SCII :
			{
				sql = "INSERT INTO " + tabelle_db_eventi[1][1] + " (nome_utente, id_evento, biglietto_bus, pranzo, affitto_scii) VALUES (?,?,?,?,?)";	

				PreparedStatement ps = getConnection().prepareStatement(sql);
				ps.setString(1, utente.getNome());
				ps.setInt(2, evento.getId());
				if(evento.getPartecipanti_campiOpt().get(utente) != null)
				{
					ps.setBoolean(3,(Boolean)evento.getPartecipanti_campiOpt().get(utente).get(NomeCampi.BIGLIETTO_BUS));
					ps.setBoolean(4,(Boolean)evento.getPartecipanti_campiOpt().get(utente).get(NomeCampi.PRANZO));
					ps.setBoolean(5,(Boolean)evento.getPartecipanti_campiOpt().get(utente).get(NomeCampi.AFFITTO_SCII));
				}
				else
					throw new Exception("Necessario inserire le scelte dell'utente rigurado i campi opzionali");
				ps.executeUpdate();			
				break;
			}
		default: return;
		}
	}
	
	
	public void collegaUtenteCategoria(Utente utente, CategorieEvento nome_categoria) throws SQLException
	{	    
		String sql = "INSERT INTO relazione_utente_categoria (nome_utente, nome_categoria) VALUES (?,?)";
		PreparedStatement ps = getConnection().prepareStatement(sql);
		ps.setString(1, utente.getNome());
		ps.setString(2, nome_categoria.toString().toLowerCase());		
		ps.executeUpdate();
	}
	

	public void segnalaFallimentoEvento(Evento evento) throws SQLException 
	{
		String titolo_evento = (evento.getCampo(NomeCampi.TITOLO).getContenuto() != null) ? (String)evento.getCampo(NomeCampi.TITOLO).getContenuto() : "" ;
		String titolo = String.format(Messaggi.TITOLO_FALLIMENTO_EVENTO, titolo_evento);
		String contenuto = String.format(Messaggi.NOTIFICA_FALLIMENTO_EVENTO,titolo_evento);	
		Notifica notifica = new Notifica(titolo, contenuto);
		notifica = insertNotifica(notifica);
		
		HashMap<Utente,HashMap<NomeCampi,Boolean>> list_utenti = selectUtentiDiEvento(evento);
		for(Utente utente : list_utenti.keySet())
		{
			collegaUtenteNotifica(utente.getNome(), notifica.getIdNotifica());
			deleteCollegamentoEventoUtente(utente.getNome(), evento);
		}
	}
	
	
	public void segnalaChiusuraEvento(Evento evento) throws SQLException 
	{
		java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd-MM-yyyy HH:mm");
		Date data_inizio_evento = ((Calendar)evento.getCampo(NomeCampi.D_O_INIZIO_EVENTO).getContenuto()).getTime();
		String titolo_evento = (evento.getCampo(NomeCampi.TITOLO).getContenuto() != null) ? (String)evento.getCampo(NomeCampi.TITOLO).getContenuto() : "" ;
		String titolo = String.format(Messaggi.TITOLO_CHIUSURA_EVENTO,titolo_evento);		
		
		HashMap<Utente,HashMap<NomeCampi,Boolean>> list_utenti = selectUtentiDiEvento(evento);	
		for(Utente utente : list_utenti.keySet())
		{
			String contenuto = String.format(Messaggi.NOTIFICA_CHIUSURA_EVENTO, titolo_evento, sdf.format(data_inizio_evento), getCostoEventoPerUtente(evento, utente));
			Notifica notifica = insertNotifica(new Notifica(titolo, contenuto));
			collegaUtenteNotifica(utente.getNome(), notifica.getIdNotifica());
			//deleteCollegamentoEventoUtente(utente.getNome(), evento);
		}
	}
	
	
	public void segnalaConclusioneEvento(Evento evento) throws SQLException {
		String titolo_evento = (evento.getCampo(NomeCampi.TITOLO).getContenuto() != null) ? (String)evento.getCampo(NomeCampi.TITOLO).getContenuto() : "" ;
		String titolo = String.format(Messaggi.TITOLO_CONCLUSIONE_EVENTO, titolo_evento);
		String contenuto = String.format(Messaggi.NOTIFICA_CONCLUSIONE_EVENTO, titolo_evento);		
		Notifica notifica = insertNotifica(new Notifica(titolo, contenuto));
		
		HashMap<Utente,HashMap<NomeCampi,Boolean>> list_utenti = selectUtentiDiEvento(evento);		
		for(Utente utente : list_utenti.keySet())
			collegaUtenteNotifica(utente.getNome(), notifica.getIdNotifica());
	}
	
	
	public void segnalaRitiroEvento(Evento evento) throws SQLException
	{
		String titolo_evento = (evento.getCampo(NomeCampi.TITOLO).getContenuto() != null) ? (String)evento.getCampo(NomeCampi.TITOLO).getContenuto() : "" ;
		String titolo = String.format(Messaggi.TITOLO_RITIRO_EVENTO, titolo_evento);
		String contenuto = String.format(Messaggi.NOTIFICA_RITIRO_EVENTO, titolo_evento);		
		Notifica notifica = insertNotifica(new Notifica(titolo, contenuto));

		HashMap<Utente,HashMap<NomeCampi,Boolean>> list_utenti = selectUtentiDiEvento(evento);		
		for(Utente utente : list_utenti.keySet())
		{
			collegaUtenteNotifica(utente.getNome(), notifica.getIdNotifica());
			deleteCollegamentoEventoUtente(utente.getNome(), evento);
		}
	}
	
	
	public void segnalaNuovoEventoAgliInteressati(Evento evento) throws SQLException
	{
		String titolo_evento = (evento.getCampo(NomeCampi.TITOLO).getContenuto() != null) ? (String)evento.getCampo(NomeCampi.TITOLO).getContenuto() : "" ;
		String nome_categoria = evento.getNomeCategoria().getString().replaceAll("_", " di ");
		String titolo = String.format(Messaggi.TITOLO_NUOVO_EVENTO, nome_categoria);
		String contenuto = String.format(Messaggi.NOTIFICA_NUOVO_EVENTO, nome_categoria, titolo_evento);	
		
		Notifica notifica = insertNotifica(new Notifica(titolo, contenuto));
		
		LinkedList<Utente> list_utenti = selectUtentiInteressatiACategoria(evento.getNomeCategoria());
//		rimozione utente creatore per non notificarlo del suo evento appena creato in caso abbia mostrato interesse verso la categoria dell'evento da lui creato
		list_utenti.remove(selectEvento(evento.getId()).getUtenteCreatore());		
		for(Utente utente : list_utenti)
			collegaUtenteNotifica(utente.getNome(), notifica.getIdNotifica());
	}
	
		
	public void segnalaEventoPerUtente(Evento evento, Utente utente_mittente, Utente utente_destinatario) throws SQLException
	{
		String nome_categoria = evento.getNomeCategoria().getString().replaceAll("_", " di ");
		String titolo = String.format(Messaggi.TITOLO_INVITO_EVENTO, nome_categoria);
		String contenuto = String.format(Messaggi.NOTIFICA_PER_INVITO_UTENTE, utente_mittente.getNome(), evento.getCampo(NomeCampi.TITOLO).getContenuto());
		Notifica notifica = new Notifica(titolo, contenuto);
		
		notifica = insertNotifica(notifica);
		collegaUtenteNotifica(utente_destinatario.getNome(), notifica.getIdNotifica());
	}
	
/*
 * READ : OPERAZIONI DI SELECT
 */
	
	public HashMap<CategorieEvento,ArrayList<Evento>> selectEventiAll() throws SQLException
	{
		HashMap<CategorieEvento,ArrayList<Evento>> eventi = new HashMap<CategorieEvento,ArrayList<Evento>>();
		
		eventi.put(CategorieEvento.PARTITA_CALCIO, selectParititeCalcioAll());
		eventi.put(CategorieEvento.SCII, selectEventiSciiAll());
		
		return eventi;
	}

	
	private ArrayList<Evento> selectParititeCalcioAll() throws SQLException
	{
		ArrayList<Evento> partite = new ArrayList<Evento>();
	
		String sql = "SELECT id, nome_utente_creatore, luogo, data_ora_termine_ultimo_iscrizione, data_ora_inizio_evento, partecipanti,"
				+ " costo, titolo, note, benefici_quota, data_ora_termine_evento, data_ora_termine_ritiro_iscrizione,"
				+ " tolleranza_max, stato, eta_minima, eta_massima, genere FROM " + tabelle_db_eventi[0][0];
		
		PreparedStatement ps = getConnection().prepareStatement(sql);
		ResultSet rs = ps.executeQuery();
		rs.beforeFirst();
		while(rs.next())
		{	//Lettura e configurazione dei campi
			int id_partita = rs.getInt(1);
			String titolo_evento = rs.getString(8);
			Calendar data_ora_termine_ultimo_iscrizione = Calendar.getInstance(); data_ora_termine_ultimo_iscrizione.setTimeInMillis(rs.getTimestamp(4).getTime());
			Calendar data_ora_inizio_evento = Calendar.getInstance(); data_ora_inizio_evento.setTimeInMillis(rs.getTimestamp(5).getTime());
			Calendar data_ora_termine_evento = Calendar.getInstance(); 
				if (rs.getTimestamp(11) != null) data_ora_termine_evento.setTimeInMillis(rs.getTimestamp(11).getTime()); 
				else data_ora_termine_evento=null;
			Calendar data_ora_termine_ritiro_iscrizione = Calendar.getInstance(); 
				if (rs.getTimestamp(12) != null) data_ora_termine_ritiro_iscrizione.setTimeInMillis(rs.getTimestamp(12).getTime()); 
				else data_ora_termine_ritiro_iscrizione=null;
			
			Utente creatore = selectUtente(rs.getString(2));
			String string_stato = rs.getString(14);
			
			PartitaCalcio partita = new PartitaCalcio(
					id_partita,
					creatore,
					rs.getString(3),
					data_ora_termine_ultimo_iscrizione,
					data_ora_inizio_evento,
					(Integer)rs.getInt(6),
					(Integer)rs.getInt(7), 
					titolo_evento,
					rs.getString(9),
					rs.getString(10),
					data_ora_termine_evento,
					data_ora_termine_ritiro_iscrizione,
					(Integer)rs.getInt(13),
					StatoEvento.convertiStringInStato(string_stato),
					(Integer)rs.getInt(15),
					(Integer)rs.getInt(16),
					(String)rs.getString(17));

				partita.setPartecipanti_campiOpt(selectUtentiDiEvento(partita));
				partite.add(partita);
		}
		return partite;
	}
	
	private ArrayList<Evento> selectEventiSciiAll() throws SQLException
	{
		ArrayList<Evento> eventi_scii = new ArrayList<Evento>();
	
		String sql = "SELECT id, nome_utente_creatore, luogo, data_ora_termine_ultimo_iscrizione, data_ora_inizio_evento, partecipanti,"
				+ " costo, titolo, note, benefici_quota, data_ora_termine_evento, data_ora_termine_ritiro_iscrizione,"
				+ " tolleranza_max, stato, biglietto_bus, pranzo, affitto_scii FROM " + tabelle_db_eventi[1][0];
		
		PreparedStatement ps = getConnection().prepareStatement(sql);
		ResultSet rs = ps.executeQuery();
		rs.beforeFirst();
		while(rs.next())
		{	//Lettura e configurazione dei campi
			int id = rs.getInt(1);
			String titolo_evento = rs.getString(8);
			Calendar data_ora_termine_ultimo_iscrizione = Calendar.getInstance(); data_ora_termine_ultimo_iscrizione.setTimeInMillis(rs.getTimestamp(4).getTime());
			Calendar data_ora_inizio_evento = Calendar.getInstance(); data_ora_inizio_evento.setTimeInMillis(rs.getTimestamp(5).getTime());
			Calendar data_ora_termine_evento = Calendar.getInstance(); 
				if (rs.getTimestamp(11) != null) data_ora_termine_evento.setTimeInMillis(rs.getTimestamp(11).getTime()); 
				else data_ora_termine_evento=null;
			Calendar data_ora_termine_ritiro_iscrizione = Calendar.getInstance(); 
				if (rs.getTimestamp(12) != null) data_ora_termine_ritiro_iscrizione.setTimeInMillis(rs.getTimestamp(12).getTime()); 
				else data_ora_termine_ritiro_iscrizione=null;
			
			Utente creatore = selectUtente(rs.getString(2));
			String string_stato = rs.getString(14);
			
			Scii scii = new Scii(
					id,
					creatore,
					rs.getString(3),
					data_ora_termine_ultimo_iscrizione,
					data_ora_inizio_evento,
					(Integer)rs.getInt(6),
					(Integer)rs.getInt(7), 
					titolo_evento,
					rs.getString(9),
					rs.getString(10),
					data_ora_termine_evento,
					data_ora_termine_ritiro_iscrizione,
					(Integer)rs.getInt(13),
					StatoEvento.convertiStringInStato(string_stato),
					(Integer)rs.getInt(15),
					(Integer)rs.getInt(16),
					(Integer)rs.getInt(17));

				scii.setPartecipanti_campiOpt(selectUtentiDiEvento(scii));
				eventi_scii.add(scii);
		}
		return eventi_scii;
	}
	
	
	public Evento selectEvento(int id_evento) 
	{
		if(eventi == null)
			return null;
		for(CategorieEvento categoria : eventi.keySet())
			for(Evento evento : eventi.get(categoria))
				if(evento.getId() == id_evento)
					return evento;
		return null;
	}


	public ArrayList<Utente> selectUtentiAll() throws SQLException
	{
		ArrayList<Utente> utenti = new ArrayList<>();
		String sql = "SELECT nome, password, eta_min, eta_max FROM utente";		
		PreparedStatement ps = getConnection().prepareStatement(sql);
		
		ResultSet rs = ps.executeQuery();	
		if(rs == null)
			return null;
		rs.beforeFirst();		
		while(rs.next())
		{		
			Utente utente = new Utente(rs.getString(1), rs.getString(2), rs.getInt(3), rs.getInt(4));	
			utente.setCategorieInteressi(selectCategorieDiUtente(utente.getNome()));
			utente.setEventi(selectEventiDiUtente(utente.getNome()));
			utente.setNotifiche(selectNotificheDiUtente(utente.getNome()));
			utenti.add(utente);
		}		
		return utenti;
	}

	
	public Utente selectUtente(String nome) {
		for (Utente u : utenti)
			if (u.getNome().equals(nome)) return u;
		return null;
	}

		
	public Notifica selectNotifica(int id_notifica) throws SQLException
	{		
		String sql = "SELECT titolo, contenuto, data FROM notifica WHERE id=?";		
		PreparedStatement ps = getConnection().prepareStatement(sql);
		ps.setInt(1, id_notifica);		
		ResultSet rs = ps.executeQuery();

		rs.beforeFirst();		
		if(rs.next())
		{
			Calendar data = Calendar.getInstance(); data.setTimeInMillis(rs.getTimestamp(3).getTime());
			Notifica notifica = new Notifica(id_notifica,rs.getString(1), rs.getString(2), data);
			return notifica;
		}
		else
			return null;
	}
	
	
	public LinkedList<Notifica> selectNotificheDiUtente(String nome_utente) throws SQLException
	{
		LinkedList<Notifica> notifiche = new LinkedList<>();
		String sql = "SELECT id_notifica FROM relazione_utente_notifica WHERE nome_utente=?";		
		PreparedStatement ps = getConnection().prepareStatement(sql);
		ps.setString(1, nome_utente);
		
		ResultSet rs = ps.executeQuery();	
		rs.beforeFirst();		
		while(rs.next())
		{		
			Notifica notifica = selectNotifica(rs.getInt(1));
			notifiche.add(notifica);
		}
		return notifiche;
	}
	
	
	public HashMap<CategorieEvento,LinkedList<Evento>> selectEventiDiUtente(String nome_utente) throws SQLException
	{
		HashMap<CategorieEvento,LinkedList<Evento>> hashmap = new HashMap<CategorieEvento,LinkedList<Evento>>();
		if(eventi == null)
		{
			String sql;
			for(int i =0; i<tabelle_db_eventi.length; i++)
			{
				String nome_tabella_relazione = tabelle_db_eventi[i][1];
				sql = "SELECT id_evento FROM " + nome_tabella_relazione + " WHERE nome_utente=?";	
				LinkedList<Evento> eventi_di_categoria = new LinkedList<>();
				PreparedStatement ps = getConnection().prepareStatement(sql);
				ps.setString(1, nome_utente);
		
				ResultSet rs = ps.executeQuery();		
				rs.beforeFirst();		
				while(rs.next()) 
				{
					eventi_di_categoria.add(selectEvento(rs.getInt(1)));
				}	
				CategorieEvento nome_categoria = CategorieEvento.convertiStringInCategoria(tabelle_db_eventi[i][0]);
				hashmap.put(nome_categoria, eventi_di_categoria);
			}
			return hashmap;
		}
		for(CategorieEvento categoria : eventi.keySet())
		{
			LinkedList<Evento> list = new LinkedList<>();
			for(Evento evento : eventi.get(categoria))
				if(evento.getPartecipanti_campiOpt().keySet().contains(selectUtente(nome_utente)))
					list.add(evento);
			hashmap.put(categoria, list);
		}		
		return hashmap;

	}
	
	
	public HashMap<Utente,HashMap<NomeCampi,Boolean>> selectUtentiDiEvento(Evento evento) throws SQLException
	{
		String sql = null;
		HashMap<Utente,HashMap<NomeCampi,Boolean>> utenti_campiOpt = new HashMap<Utente,HashMap<NomeCampi,Boolean>>();

		switch(evento.getNomeCategoria())
		{
		case PARTITA_CALCIO :
			{
				sql = "SELECT nome_utente FROM " + tabelle_db_eventi[0][1] + " WHERE id_evento=?";
				PreparedStatement ps = getConnection().prepareStatement(sql);
				ps.setInt(1, evento.getId());
				
				ResultSet rs = ps.executeQuery();		
				rs.beforeFirst();		
				while(rs.next())
				{
					Utente utente = selectUtente(rs.getString(1));
					utenti_campiOpt.put(utente, null);
				}
				break;
			}
		case SCII :
			{
				sql = "SELECT nome_utente, biglietto_bus, pranzo, affitto_scii FROM " + tabelle_db_eventi[1][1] + " WHERE id_evento=?";
				PreparedStatement ps = getConnection().prepareStatement(sql);
				ps.setInt(1, evento.getId());
				
				ResultSet rs = ps.executeQuery();		
				rs.beforeFirst();		
				while(rs.next())	
				{
					Utente utente = selectUtente(rs.getString(1));
					HashMap<NomeCampi,Boolean> campi_opt = new HashMap<NomeCampi,Boolean>();
					campi_opt.put(NomeCampi.BIGLIETTO_BUS, rs.getBoolean(2));
					campi_opt.put(NomeCampi.PRANZO, rs.getBoolean(3));
					campi_opt.put(NomeCampi.AFFITTO_SCII, rs.getBoolean(4));
					utenti_campiOpt.put(utente, campi_opt);
				}				
				break;
			}
		default: return null;
		}
		return utenti_campiOpt;
	}
	
	private LinkedList<CategorieEvento> selectCategorieDiUtente(String nome_utente) throws SQLException
	{
		LinkedList<CategorieEvento> categorie = new LinkedList<>();
		
		String sql = "SELECT nome_categoria FROM relazione_utente_categoria WHERE nome_utente=?";	
		PreparedStatement ps = getConnection().prepareStatement(sql);
		ps.setString(1, nome_utente);
		
		ResultSet rs = ps.executeQuery();
		rs.beforeFirst();	
		while(rs.next())
		{
			String nome_categoria = rs.getString(1);
			categorie.add(CategorieEvento.convertiStringInCategoria(nome_categoria));
		}	
		return categorie;
	}
	
	public LinkedList<Utente> selectUtentiInteressatiACategoria(CategorieEvento nome_categoria) throws SQLException
	{
		LinkedList<Utente> utenti = new LinkedList<>();
		String sql = "SELECT nome_utente FROM relazione_utente_categoria WHERE nome_categoria=?";	
		PreparedStatement ps = getConnection().prepareStatement(sql);
		ps.setString(1, nome_categoria.toString());
		
		ResultSet rs = ps.executeQuery();
		rs.beforeFirst();	
		while(rs.next())
			utenti.add(selectUtente(rs.getString(1)));
		return utenti;
	}
	

	public HashMap<CategorieEvento,LinkedList<Utente>> selectUtentiDaEventiPassati(String nome_utente_creatore) throws SQLException 
	{
		HashMap<CategorieEvento,LinkedList<Utente>> hash_map= new HashMap<>();
		String sql1 = "SELECT id FROM %s WHERE nome_utente_creatore=?";
		String tabella, sql_con_nome_tabella, sql2, nome_tabella_relazione, sql_con_nome_relazione;
		LinkedList<Utente> utenti;
		for(int i=0 ; i < tabelle_db_eventi.length ; i++) {
			utenti = new LinkedList<>();
			tabella = tabelle_db_eventi[i][0];
			sql_con_nome_tabella = String.format(sql1, tabella);
			
			PreparedStatement ps1 = getConnection().prepareStatement(sql_con_nome_tabella);
			ps1.setString(1, nome_utente_creatore);
			ResultSet rs1 = ps1.executeQuery();
			
			rs1.beforeFirst();
			while(rs1.next()) {
				sql2 = "SELECT nome_utente FROM %s WHERE id_evento=? AND nome_utente!=?";
				nome_tabella_relazione = tabelle_db_eventi[i][1];
				sql_con_nome_relazione = String.format(sql2, nome_tabella_relazione);
				
				PreparedStatement ps2 = getConnection().prepareStatement(sql_con_nome_relazione);
				ps2.setInt(1, rs1.getInt(1));
				ps2.setString(2, nome_utente_creatore);
				ResultSet rs2 = ps2.executeQuery();
				
				rs2.beforeFirst();
				while(rs2.next()) {
					Utente utente = selectUtente(rs2.getString(1));
						if(utenti.contains(utente) == false)
							{utenti.add(utente); System.out.println(utente.getNome());}
				}
			}
			hash_map.put(CategorieEvento.convertiStringInCategoria(tabella), utenti);
		}		
		return hash_map;
	}


/*
 * UPDATE : OPERAZIONI DI AGGIORNAMENTO	
 */

	public void updateEvento(Evento evento) throws SQLException
	{
//		scorrendo la prima colonna di tabbelle_db_eventi si ottengono i nomi delle tabelle degli eventi
		for(int i=0; i < tabelle_db_eventi.length; i++)
		{
			if(evento.getNomeCategoria().getString().equals(tabelle_db_eventi[i][0]))
			{				
				String sql = "UPDATE " + tabelle_db_eventi[i][0] + " SET stato = ? WHERE id = ?";
				PreparedStatement ps = getConnection().prepareStatement(sql);
				ps.setString(1, evento.getStato().getString());
				ps.setInt(2, evento.getId());
				ps.executeUpdate();
				refreshDatiRAM();
			}
		}
	}
	
	public void updateEtaMinUtente(String nome_utente, int eta_min) throws SQLException
	{
		String sql = "UPDATE utente SET eta_min = ? WHERE nome = ?";
		PreparedStatement ps = getConnection().prepareStatement(sql);
		ps.setInt(1, eta_min);
		ps.setString(2, nome_utente);
		ps.executeUpdate();
	}
	
	public void updateEtaMaxtente(String nome_utente, int eta_max) throws SQLException {
		String sql = "UPDATE utente SET eta_max = ? WHERE nome = ?";
		PreparedStatement ps = getConnection().prepareStatement(sql);
		ps.setInt(1, eta_max);
		ps.setString(2, nome_utente);
		ps.executeUpdate();
	}
	
/*
 * DELETE : OPERAZIONI DI DELETE	
 */
	
	public void deleteEvento(Evento evento) throws SQLException
	{
		for(int i=0; i < tabelle_db_eventi.length; i++)
		{
			if(evento.getNomeCategoria().getString().equals(tabelle_db_eventi[i][0]))
			{				
				String sql = "DELETE FROM " + tabelle_db_eventi[i][0] + " WHERE id = ?" ;
				PreparedStatement ps = getConnection().prepareStatement(sql);
				ps.setInt(1, evento.getId());
				ps.executeUpdate();
				refreshDatiRAM();
			}
		}
	}
	
	
	public void deleteUtente(String nome_utente) throws SQLException
	{
		String sql = "DELETE FROM utente WHERE id = ?";
		PreparedStatement ps = getConnection().prepareStatement(sql);
		ps.setString(1, nome_utente);
		ps.executeUpdate();
		refreshDatiRAM();
	}


	public void deleteNotifica(int id_notifica) throws SQLException
	{
		String sql = "DELETE FROM notifica WHERE id = " + id_notifica;
		PreparedStatement ps = getConnection().prepareStatement(sql);
		ps.executeUpdate();
	}
	
	
	public void deleteCollegamentoNotificaUtente(Utente utente, Notifica notifica) throws SQLException
	{
//		Eliminazione collegamento tra notifica e utente nella tabella relazione_utente_notifica contenente le realzioni ManyToMany tra utenti e notifiche
		String sql = "DELETE FROM relazione_utente_notifica WHERE nome_utente=? AND id_notifica=?" ;
		
		PreparedStatement ps = getConnection().prepareStatement(sql);
		ps.setString(1, utente.getNome());
		ps.setInt(2, notifica.getIdNotifica());
		ps.executeUpdate();
//		Controllo per verificare che la notifica da cui è stato tolto il collegamento abbia almeno ancora un utente che la referenzi, in caso non ci siano utenti la elimino
		String sql2 = "SELECT nome_utente data FROM relazione_utente_notifica WHERE id_notifica=?";
		PreparedStatement ps2 = getConnection().prepareStatement(sql2);
		ps2.setInt(1, notifica.getIdNotifica());	
		
		ResultSet rs = ps2.executeQuery();
		
		rs.beforeFirst();
		
		if(!rs.next())
			deleteNotifica(notifica.getIdNotifica());
	}
	
	
	public void deleteCollegamentoEventoUtente(String nome_utente, Evento evento) throws SQLException {
//		Eliminazione collegamento tra notifica e utente nella tabella relazione_utente_notifica contenente le realzioni ManyToMany tra utenti e notifiche
		String sql = null;
//		scorrendo la prima colonna di tabbelle_db_eventi si ottengono i nomi delle tabelle degli eventi
		for(int i=0; i < tabelle_db_eventi.length; i++)
		{
			if(evento.getNomeCategoria().getString().equals(tabelle_db_eventi[i][0]))
//				nella seconda colonna sono presenti i nomi a livello db delle relazioni tra utenti e lo specifico evento
			{
				sql = "DELETE FROM " + tabelle_db_eventi[i][1] + " WHERE nome_utente=? AND id_evento=?" ;
				break;
			}
		}
		PreparedStatement ps = getConnection().prepareStatement(sql);
		ps.setString(1, nome_utente);
		ps.setInt(2, evento.getId());
		ps.executeUpdate();
	}

	
	public void deleteCollegamentoCategoriaUtente(String nome_utente, CategorieEvento nome_categoria) throws SQLException {
//		Eliminazione collegamento tra notifica e utente nella tabella relazione_utente_notifica contenente le realzioni ManyToMany tra utenti e notifiche
		String sql = "DELETE FROM relazione_utente_categoria WHERE nome_utente=? AND nome_categoria=?" ;
		PreparedStatement ps = getConnection().prepareStatement(sql);
		ps.setString(1, nome_utente);
		ps.setString(2, nome_categoria.toString().toLowerCase());
		ps.executeUpdate();
	}
	
	
	
/*
 *  METODI DI UTILITY
 */
	public Utente existUtente(Utente utente) throws SQLException {
		utenti = selectUtentiAll();
		if(utenti.isEmpty()) return null;
		
		for(Utente u : utenti)
			if(u.equals(utente)) return u;
		return null;
	}

	public boolean existUtenteInEvento(Utente utente, Evento evento) throws SQLException 
	{
		LinkedList<Evento> eventi = selectEventiDiUtente(utente.getNome()).get(evento.getNomeCategoria());
		if(eventi == null)
			return false;
		for(Evento elemento : eventi) 
		{
			if(elemento.getId() == evento.getId())
				return true;
		}
		return false;
	}	
	
	public int getCostoEventoPerUtente(Evento evento, Utente utente) throws SQLException
	{
		if(existUtenteInEvento(utente, evento) == false)
			return 0;
		int costo = (Integer)evento.getCampo(NomeCampi.COSTO).getContenuto();
		HashMap<NomeCampi, Boolean> campi_opt = evento.getCampiOptDiUtente(utente);
		if(campi_opt != null)
		{
			for(NomeCampi nome_campo : campi_opt.keySet())
			{
				if(campi_opt.get(nome_campo) == true)
					costo+=(Integer)evento.getCampo(nome_campo).getContenuto();
			}
		}
		return costo;
	}
	
	public Timestamp creaTimestamp(Calendar c) {	
		if (c==null) return null; else return new Timestamp (c.getTimeInMillis());
	}

	public String convertiCalendar (Calendar calendar) {
		java.util.Date dt = calendar.getTime();
		java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
		String dateTime = sdf.format(dt);
		return dateTime;
	}
	
	
}