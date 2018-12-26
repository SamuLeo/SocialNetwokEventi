package it.unibs.dii.isw.socialNetworkEventi.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;

import com.mysql.cj.jdbc.MysqlDataSource;
import java.util.ArrayList;
import it.unibs.dii.isw.socialNetworkEventi.utility.NomeCampi;

public class DataBase 
{
	private Connection con;
	private ArrayList<Evento> eventi = new ArrayList<>();
	private ArrayList<Utente> utenti = new ArrayList<>();
	
//	Connessione a mysql creata tramite pattern Singleton
	public Connection getConnection() throws SQLException
	{
		if(con == null)
		{
			MysqlDataSource dataSource = new MysqlDataSource();
//			specifica dei dettagli necessari alla connessione
			dataSource.setDatabaseName("social_network_db");
			dataSource.setPortNumber(3306);
			dataSource.setServerName("127.0.0.1");
			dataSource.setUser("admin_social");
			dataSource.setPassword("StefanoLoveLinux");
			
			con = dataSource.getConnection();

		}
		return con;
	}
	
	
	public void refreshDatiRAM() throws SQLException
	{
		eventi = selectEventiAll();
		utenti = selectUtentiAll();
	}

/*
 * 
 * OPERAZIONI C.R.U.D. SU DB MYSQL
 * 
 */
	
	
/*
 * CREATE : OPERAZIONI DI INSERT	
 */
	
	
	public Evento insertEvento(Evento evento) throws SQLException
	{
		switch(evento.getClass().getSimpleName())
		{
		case "PartitaCalcio" : 
			{
//				Estrazione campi dall'oggetto evento
				int id_creatore									= evento.getUtenteCreatore().getId_utente();					
				String luogo 									= (String) evento.getCampo(NomeCampi.LUOGO).getContenuto();
				Calendar data_ora_termine_ultimo_iscrizione		= (Calendar) evento.getCampo(NomeCampi.D_O_CHIUSURA_ISCRIZIONI).getContenuto();
				Calendar data_ora_inizio_evento 				= (Calendar) evento.getCampo(NomeCampi.D_O_INIZIO_EVENTO).getContenuto();
				int partecipanti								= (Integer) evento.getCampo(NomeCampi.PARTECIPANTI).getContenuto();
				int costo										= (Integer) evento.getCampo(NomeCampi.COSTO).getContenuto(); 
				
				String titolo									= (String)( evento.getCampo(NomeCampi.TITOLO) != null ? evento.getCampo(NomeCampi.TITOLO).getContenuto() : null);
				String note										= (String)( evento.getCampo(NomeCampi.NOTE) != null ? evento.getCampo(NomeCampi.TITOLO).getContenuto() : null);
				String benefici_quota							= (String)( evento.getCampo(NomeCampi.BENEFICI_QUOTA) != null ? evento.getCampo(NomeCampi.TITOLO).getContenuto() : null);
				Calendar data_ora_termine_evento				= evento.getCampo(NomeCampi.D_O_TERMINE_EVENTO) != null ? (Calendar) evento.getCampo(NomeCampi.D_O_TERMINE_EVENTO).getContenuto() : null;  
			   
				int eta_minima									= (Integer) evento.getCampo(NomeCampi.ETA_MINIMA).getContenuto();
			    int eta_massima									= (Integer) evento.getCampo(NomeCampi.ETA_MASSIMA).getContenuto();
			    String genere									= (String) evento.getCampo(NomeCampi.GENERE).getContenuto();
			    
//				Stringa contenente uno script sql per inserire la partita di calcio
				String sql = "INSERT INTO partita_calcio "
						+ "(id_creatore, luogo, data_ora_termine_ultimo_iscrizione, data_ora_inizio_evento, partecipanti, costo, titolo, note,"
						+ "benefici_quota, data_ora_termine_evento, eta_minima, eta_massima, genere)"
						+ " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
				
//				script contenente la stringa sql precedentemente specificata inviato al DB, con prevenzione SQL Injection	
				PreparedStatement ps = getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
				ps.setInt(1, id_creatore);				
				ps.setString(2, luogo);
				ps.setTimestamp(3, new java.sql.Timestamp(data_ora_termine_ultimo_iscrizione.getTimeInMillis()));		
				ps.setTimestamp(4, new java.sql.Timestamp(data_ora_inizio_evento.getTimeInMillis()));	
				ps.setInt(5, partecipanti);
				ps.setInt(6, costo);
				ps.setString(7, titolo);
				ps.setString(8, note);
				ps.setString(9, benefici_quota);
				ps.setTimestamp(10, new java.sql.Timestamp(data_ora_termine_evento.getTimeInMillis()));
				ps.setInt(11, eta_minima);
				ps.setInt(12, eta_massima);
				ps.setString(13, (String)genere);
				
				ps.executeUpdate();
				ResultSet rs = ps.getGeneratedKeys();
	
				rs.next();
				evento.setId(rs.getInt(1));
				
				refreshDatiRAM();

				return evento;
			}
		default : return null;
		}

	}
	
	
	public Utente insertUtente(Utente utente) throws SQLException
	{
		String nome = utente.getNome();
		String password = utente.getPassword();
	    
		String sql = "INSERT INTO utente "
				+ "(nome, password)" 
				+ " VALUES "
				+ "(?,?)";
		
		PreparedStatement ps = getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
		ps.setString(1, nome);
		ps.setString(2, password);		
	
		ps.executeUpdate();
		ResultSet rs = ps.getGeneratedKeys();	
		
		refreshDatiRAM();
		
		rs.beforeFirst();
		utente.setId_utente(rs.getInt(1));
		
		return utente;
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
		ps.setTimestamp(3, new java.sql.Timestamp(data.getTimeInMillis()));
		
		ps.executeUpdate();
		ResultSet rs = ps.getGeneratedKeys();	

		refreshDatiRAM();
		
		rs.beforeFirst();
		notifica.setIdNotifica(rs.getInt(1));
		
		return notifica;
	}
	
	
	public int collegaUtenteNotifica(int id_utente, int id_notifica) throws SQLException
	{	    
		String sql = "INSERT INTO relazione_utente_notifica "
				+ "(id_user, id_notifica)" 
				+ " VALUES "
				+ "(?,?)";
		
		PreparedStatement ps = getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
		ps.setInt(1, id_utente);
		ps.setInt(2, id_notifica);		
		
		ps.executeUpdate();
		ResultSet rs = ps.getGeneratedKeys();
		rs.beforeFirst();
		return rs.getInt(1);
	}
	
	public int collegaUtentePartita(int id_utente, int id_partita) throws SQLException
	{	    
		String sql = "INSERT INTO relazione_utente_partita "
				+ "(id_utente, id_partita)" 
				+ " VALUES "
				+ "(?,?)";
		
		PreparedStatement ps = getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
		ps.setInt(1, id_utente);
		ps.setInt(2, id_partita);		
		
		ps.executeUpdate();
		ResultSet rs = ps.getGeneratedKeys();	
		rs.beforeFirst();
		return rs.getInt(1);
	}
	
	
/*
 * READ : OPERAZIONI DI SELECT
 */
	public ArrayList<Evento> selectEventiAll() throws SQLException
	{
		ArrayList<Evento> eventi = new ArrayList<>();
		
		String sql = "SELECT id, "
				+ " id_creatore,"
				+ " luogo, "
				+ " data_ora_termine_ultimo_iscrizione, "
				+ " data_ora_inizio_evento, "
				+ " partecipanti,"
				+ " costo,"
				+ " titolo,"
				+ " note,"
				+ " benefici_quota,"
				+ " data_ora_termine_evento,"
				+ " eta_minima,"
				+ " eta_massima,"
				+ " genere"
				+ " FROM partita_calcio";
		
		PreparedStatement ps = getConnection().prepareStatement(sql);
		
		ResultSet rs = ps.executeQuery();
		
		rs.beforeFirst();
		
		while(rs.next())
		{
			Calendar data_termine = Calendar.getInstance(); data_termine.setTimeInMillis(rs.getTimestamp(4).getTime());
			Calendar data_inizio = Calendar.getInstance(); data_inizio.setTimeInMillis(rs.getTimestamp(5).getTime());		
			Calendar data_fine = Calendar.getInstance(); data_fine.setTimeInMillis(rs.getTimestamp(11).getTime());
			Utente creatore = selectUtente(rs.getInt(2));
			
			PartitaCalcio partita = new PartitaCalcio(
					rs.getInt(1),
					creatore,
					rs.getString(3),
					data_termine,
					data_inizio,
					(Integer)rs.getInt(6),
					(Integer)rs.getInt(7), 
					rs.getString(8),
					rs.getString(9),
					rs.getString(10),
					data_fine,
					(Integer)rs.getInt(12),
					(Integer)rs.getInt(13),
					(String)rs.getString(14));

			eventi.add(partita);
		}
		
		return eventi;
	}
	
	public PartitaCalcio selectPartitaCalcio(int id_partita) throws SQLException
	{

		String sql = "SELECT id, "
				+ " id_creatore"
				+ " luogo, "
				+ " data_ora_termine_ultimo_iscrizione, "
				+ " data_ora_inizio_evento, "
				+ " partecipanti,"
				+ " costo,"
				+ " titolo,"
				+ " note,"
				+ " benefici_quota,"
				+ " data_ora_termine_evento,"
				+ " eta_minima,"
				+ " eta_massima,"
				+ " genere"
				+ " FROM partita_calcio WHERE id LIKE " + id_partita;

		PreparedStatement ps = getConnection().prepareStatement(sql);
		
		ResultSet rs = ps.executeQuery();
		
		rs.beforeFirst();
		
		if(rs.next())
		{
			Calendar data_termine = Calendar.getInstance(); data_termine.setTimeInMillis(rs.getTimestamp(4).getTime());
			Calendar data_inizio = Calendar.getInstance(); data_inizio.setTimeInMillis(rs.getTimestamp(5).getTime());		
			Calendar data_fine = Calendar.getInstance(); data_fine.setTimeInMillis(rs.getTimestamp(5).getTime());
			Utente creatore = selectUtente(rs.getInt(2));
			
			PartitaCalcio partita = new PartitaCalcio(
					rs.getInt(1),
					creatore,
					rs.getString(3),
					data_termine,
					data_inizio,
					(Integer)rs.getInt(6),
					(Integer)rs.getInt(7), 
					rs.getString(8),
					rs.getString(9),
					rs.getString(10),
					data_fine,
					(Integer)rs.getInt(12),
					(Integer)rs.getInt(13),
					rs.getString(14));
			return partita;
		}
		else
			return null;
	}

	
	public ArrayList<Utente> selectUtentiAll() throws SQLException
	{
		ArrayList<Utente> utenti = new ArrayList<>();
		
		String sql = "SELECT id,"
				+ "nome,"
				+ "password"
				+ " FROM utente";
		
		PreparedStatement ps = getConnection().prepareStatement(sql);
		
		ResultSet rs = ps.executeQuery();
		
		rs.beforeFirst();
		
		while(rs.next())
		{		
			Utente utente = new Utente(rs.getString(2), rs.getString(3));
			utente.setId_utente(rs.getInt(1));
			
			utenti.add(utente);
		}
		
		return utenti;
	}
	
	
	public Utente selectUtente(int id_utente) throws SQLException
	{
		Utente utente = null;
		
		String sql = "SELECT id,"
				+ "nome,"
				+ "password"
				+ " FROM utente";
		
		PreparedStatement ps = getConnection().prepareStatement(sql);
		
		ResultSet rs = ps.executeQuery();
		
		rs.beforeFirst();
		
		if(rs.next())
		{		
			utente = new Utente(rs.getString(2), rs.getString(3));
			utente.setId_utente(rs.getInt(1));
			
			utenti.add(utente);
			return utente;
		}
		else
			return null;
	}	
	
	
	public LinkedList<Notifica> selectNotificheDiUtente(int id_utente) throws SQLException
	{
		LinkedList<Notifica> notifiche = new LinkedList<>();
		
		String sql = "SELECT id, id_user, id_notifica FROM relazione_utente_notifica";
		
		PreparedStatement ps = getConnection().prepareStatement(sql);
		
		ResultSet rs = ps.executeQuery();
		
		rs.beforeFirst();
		
		while(rs.next())
		{		
			if(rs.getInt(2) == id_utente);
				notifiche.add(selectNotifica(rs.getInt(3)));
		}
		
		return notifiche;
	}
	
	
	public ArrayList<PartitaCalcio> selectPartiteDiUtente(int id_utente) throws SQLException
	{
		ArrayList<PartitaCalcio> partite = new ArrayList<>();
		
		String sql = "SELECT id, id_utente, id_partita FROM relazione_utente_partita";
		
		PreparedStatement ps = getConnection().prepareStatement(sql);
		
		ResultSet rs = ps.executeQuery();
		
		rs.beforeFirst();
		
		while(rs.next())
		{		
			if(rs.getInt(2) == id_utente);
				partite.add(selectPartitaCalcio(rs.getInt(3)));
		}
		
		return partite;
	}
	
	
	public Notifica selectNotifica(int id_notifica) throws SQLException
	{		
		String sql = "SELECT id, titolo, contenuto, data FROM notifica";
		
		PreparedStatement ps = getConnection().prepareStatement(sql);
		
		ResultSet rs = ps.executeQuery();

		rs.beforeFirst();
		
		if(rs.next())
		{
			Calendar data = Calendar.getInstance(); data.setTimeInMillis(rs.getTimestamp(4).getTime());
			Notifica notifica = new Notifica(rs.getString(2), rs.getString(3), data);
			notifica.setIdNotifica(rs.getInt(1));
			return notifica;
		}
		else
			return null;
	}
	
	
	
/*
 * DELETE : OPERAZIONI DI DELETE	
 */
	
	
	public void deleteEvento(Evento evento) throws SQLException
	{
		switch(evento.getClass().getSimpleName())
		{
		case "PartitaCalcio" : 
			{
				String sql = "DELETE FROM partita_calcio WHERE id = " + evento.getId();
				PreparedStatement ps = getConnection().prepareStatement(sql);
				ps.executeUpdate();
				refreshDatiRAM();
				break;
			}
		default : return;
		}
	}
	
	
	public void deleteUtente(int id_utente) throws SQLException
	{
		String sql = "DELETE FROM utente WHERE id = " + id_utente;
		
		PreparedStatement ps = getConnection().prepareStatement(sql);
		
		ps.executeUpdate();
		
		refreshDatiRAM();
	}


	public void deleteNotifica(int id_notifica) throws SQLException
	{
		String sql = "DELETE FROM notifica WHERE id = " + id_notifica;
		
		PreparedStatement ps = getConnection().prepareStatement(sql);
		
		ps.executeUpdate();
	}
	
	
	
/*
 *  METODI DI UTILITY
 */
	
	
	public boolean existUtente(Utente utente) throws SQLException
	{
		refreshDatiRAM();
		if(utenti.isEmpty())
			return false;
		
		for(Utente utente1 : utenti)
		{
			if(utente1.equals(utente))
				return true;
		}
		return false;
	}

	
	public boolean existUtenteInPartita(Utente utente, PartitaCalcio partita) throws SQLException
	{
		ArrayList<PartitaCalcio> partite = selectPartiteDiUtente(utente.getId_utente());

		for(PartitaCalcio elemento : partite)
		{
			if(elemento.getId() == partita.getId())
				return true;
		}
		return false;
	}
	
//	public String (Calendar calendar)
//	{
//
//		java.util.Date dt = calendar.getTime();
//
//		java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
//
//		String dateTime = sdf.format(dt);
//		
//		return dateTime;
//	}

	public ArrayList<Evento> getEventi() {return eventi;}

	
	public ArrayList<Utente> getUtenti() {return utenti;}	

}

