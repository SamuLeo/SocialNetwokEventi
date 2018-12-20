package it.unibs.dii.isw.socialNetworkEventi.model;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Calendar;
import java.util.LinkedList;

import com.mysql.cj.jdbc.MysqlDataSource;
import java.util.ArrayList;
import it.unibs.dii.isw.socialNetworkEventi.utility.NomeCampi;

public class DataBase 
{
	private Connection con;
	private ArrayList<Evento> eventi;
	private ArrayList<Utente> utenti;
	
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
	
	public void insertEvento(Evento evento) throws SQLException
	{
		switch(evento.getClass().getSimpleName())
		{
		case "PartitaCalcio" : 
			{
//				Estrazione campi dall'oggetto evento
				int id_creatore							= evento.getFruitori().get(0).getId_utente();					
				String luogo 							= (String) evento.getCampo(NomeCampi.LUOGO).getContenuto();
				Date data_ora_termine_ultimo_iscrizione	= (Date) ((Calendar)evento.getCampo(NomeCampi.D_O_CHIUSURA_ISCRIZIONI).getContenuto()).getTime();
				Date data_ora_inizio_evento 			= (Date) ((Calendar)evento.getCampo(NomeCampi.D_O_INIZIO_EVENTO).getContenuto()).getTime();
				int partecipanti						= (Integer) evento.getCampo(NomeCampi.PARTECIPANTI).getContenuto();
				int costo								= (Integer) evento.getCampo(NomeCampi.COSTO).getContenuto();   
				String titolo							= (String) evento.getCampo(NomeCampi.TITOLO).getContenuto();
				String note								= (String) evento.getCampo(NomeCampi.NOTE).getContenuto();
				String benefici_quota					= (String) evento.getCampo(NomeCampi.BENEFICI_QUOTA).getContenuto();
			    Date data_ora_termine_evento			= (Date) ((Calendar)evento.getCampo(NomeCampi.D_O_TERMINE_EVENTO).getContenuto()).getTime();  
			    int eta_minima							= (Integer) evento.getCampo(NomeCampi.ETA_MINIMA).getContenuto();
			    int eta_massima							= (Integer) evento.getCampo(NomeCampi.ETA_MASSIMA).getContenuto();
			    String genere							= (String) evento.getCampo(NomeCampi.GENERE).getContenuto();
			    
//				Stringa contenente uno script sql per inserire la partita di calcio
				String sql = "INSERT INTO partita_calcio "
						+ "(luogo, "
						+ "data_ora_termine_ultimo_iscrizione, "
						+ "data_ora_inizio_evento,"
						+ "partecipanti"
						+ "costo,"
						+ "titolo,"
						+ "note,"
						+ "benefici_quota,"
						+ "data_ora_termine_evento,"
						+ "eta_minima,"
						+ "eta_massima,"
						+ "genere,"
						+ "id_creatore)" 
						+ " VALUES "
						+ "(?,?,?,?,?,?,?,?,?,?,?,?,?)";
				
//				script contenente la stringa sql precedentemente specificata inviato al DB, con prevenzione SQL Injection	
				PreparedStatement ps = getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
				ps.setString(1, luogo);
				ps.setDate(2, data_ora_termine_ultimo_iscrizione);		
				ps.setDate(3, data_ora_inizio_evento);	
				ps.setInt(4, partecipanti);
				ps.setInt(5, costo);
				ps.setString(6, titolo);
				ps.setString(7, note);
				ps.setString(8, benefici_quota);
				ps.setDate(9, data_ora_termine_evento);
				ps.setInt(10, eta_minima);
				ps.setInt(11, eta_massima);
				ps.setString(12, genere);
				ps.setInt(12, id_creatore);
				
				ps.executeUpdate();
				
				refreshDatiRAM();
//				il return restituisce la primary key della riga appena aggiunta		

			}
		default : return ;
		}

	}
	
	public ArrayList<Evento> selectEventiAll() throws SQLException
	{
		ArrayList<Evento> eventi = new ArrayList<>();
		
		String sql = "SELECT id, "
				+ "luogo, "
				+ "data_ora_termine_ultimo_iscrizione, "
				+ "data_ora_inizio_evento, "
				+ "partecipanti,"
				+ "costo,"
				+ "titolo,"
				+ "note,"
				+ "benefici_quota,"
				+ "data_ora_termine_evento,"
				+ "eta_minima,"
				+ "eta_massima,"
				+ "genere"
				+ "FROM partita_calcio";
		
		PreparedStatement ps = getConnection().prepareStatement(sql);
		
		ResultSet rs = ps.executeQuery();
		
		rs.beforeFirst();
		
		while(rs.next())
		{
			Calendar data_termine = Calendar.getInstance(); data_termine.setTime(rs.getDate(3));
			Calendar data_inizio = Calendar.getInstance(); data_inizio.setTime(rs.getDate(4));		
			Calendar data_fine = Calendar.getInstance(); data_fine.setTime(rs.getDate(10));
			
			PartitaCalcio partita = new PartitaCalcio(
					rs.getInt(14),
					rs.getString(2),
					data_termine,
					data_inizio,
					(Integer)rs.getInt(5),
					(Integer)rs.getInt(6), 
					rs.getString(7),
					rs.getString(8),
					rs.getString(9),
					data_fine,
					(Integer)rs.getInt(11),
					(Integer)rs.getInt(12),
					rs.getString(13));
			partita.setId(rs.getInt(1));
			
			eventi.add(partita);
		}
		
		return eventi;
	}
	
	public PartitaCalcio selectPartitaCalcio(int id_partita) throws SQLException
	{

		String sql = "SELECT id, "
				+ "luogo, "
				+ "data_ora_termine_ultimo_iscrizione, "
				+ "data_ora_inizio_evento, "
				+ "partecipanti,"
				+ "costo,"
				+ "titolo,"
				+ "note,"
				+ "benefici_quota,"
				+ "data_ora_termine_evento,"
				+ "eta_minima,"
				+ "eta_massima,"
				+ "genere"
				+ "FROM partita_calcio WHERE id LIKE " + id_partita;

		PreparedStatement ps = getConnection().prepareStatement(sql);

		ResultSet rs = ps.executeQuery();
		
		rs.beforeFirst();

		if(rs.next())
		{

			Calendar data_termine = Calendar.getInstance(); data_termine.setTime(rs.getDate(3));
			Calendar data_inizio = Calendar.getInstance(); data_inizio.setTime(rs.getDate(4));		
			Calendar data_fine = Calendar.getInstance(); data_fine.setTime(rs.getDate(10));

			PartitaCalcio partita = new PartitaCalcio(
					rs.getInt(14),
					rs.getString(2),
					data_termine,
					data_inizio,
					(Integer)rs.getInt(5),
					(Integer)rs.getInt(6), 
					rs.getString(7),
					rs.getString(8),
					rs.getString(9),
					data_fine,
					(Integer)rs.getInt(11),
					(Integer)rs.getInt(12),
					rs.getString(13));
			partita.setId(rs.getInt(1));
			return partita;
		}
		else
			return null;
	}
	
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
	
	
	
	
	
	
	public int insertUtente(Utente utente) throws SQLException
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
		
		return rs.getInt(1);
	}
	
	public ArrayList<Utente> selectUtentiAll() throws SQLException
	{
		ArrayList<Utente> utenti = new ArrayList<>();
		
		String sql = "SELECT id,"
				+ "nome,"
				+ "password"
				+ "FROM utente";
		
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
	
	public Utente selectUtentel(int id_utente) throws SQLException
	{
		Utente utente = null;
		
		String sql = "SELECT id,"
				+ "nome,"
				+ "password"
				+ "FROM utente";
		
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
	
	public void deleteUtente(int id_utente) throws SQLException
	{
		String sql = "DELETE FROM utente WHERE id = " + id_utente;
		
		PreparedStatement ps = getConnection().prepareStatement(sql);
		
		ps.executeUpdate();
		
		refreshDatiRAM();
	}
	
	public boolean existUtente(Utente utente) throws SQLException
	{
		refreshDatiRAM();
		if(utenti == null)
			return false;
		
		for(Utente utente1 : utenti)
		{
			if(utente1.equals(utente))
				return true;
		}
		return false;
	}
	
	

	
	
	
	
	public int insertNotifica(Notifica notifica) throws SQLException
	{
		String titolo = notifica.getTitolo();
		String contenuto = notifica.getContenuto();
		Date data = (Date) notifica.getData().getTime();
	    
		String sql = "INSERT INTO notifica (titolo,contenuto,data) VALUES (?,?,?)";
		
		PreparedStatement ps = getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
		ps.setString(1, titolo);
		ps.setString(2, contenuto);		
		ps.setDate(3, data);
		
		ps.executeUpdate();
		ResultSet rs = ps.getGeneratedKeys();	
		return rs.getInt(1);
	}
	
	
	public Notifica selectNotifica(int id_notifica) throws SQLException
	{		
		String sql = "SELECT id, titolo, contenuto, data FROM notifica";
		
		PreparedStatement ps = getConnection().prepareStatement(sql);
		
		ResultSet rs = ps.executeQuery();

		rs.beforeFirst();
		
		if(rs.next())
		{
			Calendar data = Calendar.getInstance(); data.setTime(rs.getDate(4));
			Notifica notifica = new Notifica(rs.getString(2), rs.getString(3), data);
			notifica.setIdNotifica(rs.getInt(1));
			return notifica;
		}
		else
			return null;
	}
	
	public void deleteNotifica(int id_notifica) throws SQLException
	{
		String sql = "DELETE FROM notifica WHERE id = " + id_notifica;
		
		PreparedStatement ps = getConnection().prepareStatement(sql);
		
		ps.executeUpdate();
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
		return rs.getInt(1);
	}
	
	public boolean existUtenteInPartita(Utente utente, PartitaCalcio partita) throws SQLException
	{
		ArrayList<PartitaCalcio> partite = selectPartiteUtente(utente.getId_utente());

		for(PartitaCalcio elemento : partite)
		{
			if(elemento.getId() == partita.getId())
				return true;
		}
		return false;
	}
	
	public LinkedList<Notifica> selectNotificheUtente(int id_utente) throws SQLException
	{
		LinkedList<Notifica> notifiche = new LinkedList<>();
		
		String sql = "SELECT id, id_user, id_notifica"
				+ "FROM relazione_utente_notifica";
		
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
	
	public ArrayList<PartitaCalcio> selectPartiteUtente(int id_utente) throws SQLException
	{
		ArrayList<PartitaCalcio> partite = new ArrayList<>();
		
		String sql = "SELECT id, id_utente, id_partita"
				+ "FROM relazione_utente_partita";
		
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


	public ArrayList<Evento> getEventi() {return eventi;}


	public ArrayList<Utente> getUtenti() {return utenti;}
	
	
}
