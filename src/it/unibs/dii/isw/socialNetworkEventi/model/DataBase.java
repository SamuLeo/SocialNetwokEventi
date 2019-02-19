package it.unibs.dii.isw.socialNetworkEventi.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.LinkedList;

import com.mysql.cj.jdbc.MysqlDataSource;
import java.util.ArrayList;
import it.unibs.dii.isw.socialNetworkEventi.utility.NomeCampi;
import it.unibs.dii.isw.socialNetworkEventi.utility.StatoEvento;

public class DataBase 
{
	private static final String NOTIFICA_FALLIMENTO_EVENTO = "Caro utente,\n l'evento %s a cui si era iscritto è fallito a causa della scarsa richiesta di partecipazione";
	private static final String NOTIFICA_CONCLUSIONE_EVENTO = "Caro utente,\n l'evento %s a cui si era iscritto è concluso";
	private static final String NOTIFICA_CHIUSURA_EVENTO = "Caro utente,\n l'evento %s a cui si era iscritto ha raggiunto il numero massimo si iscrizioni, per tanto si svolgerà";

	
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
			
			eventi = new ArrayList<>();
			utenti = new ArrayList<>();
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
				String stato									= evento.getStato().getCodNomeCampi();
			   
				int eta_minima									= (Integer) evento.getCampo(NomeCampi.ETA_MINIMA).getContenuto();
			    int eta_massima									= (Integer) evento.getCampo(NomeCampi.ETA_MASSIMA).getContenuto();
			    String genere									= (String) evento.getCampo(NomeCampi.GENERE).getContenuto();
			    
//				Stringa contenente uno script sql per inserire la partita di calcio
				String sql = "INSERT INTO partita_calcio "
						+ "(id_creatore, luogo, data_ora_termine_ultimo_iscrizione, data_ora_inizio_evento, partecipanti, costo, titolo, note,"
						+ "benefici_quota, data_ora_termine_evento, stato, eta_minima, eta_massima, genere)"
						+ " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
				
//				script contenente la stringa sql precedentemente specificata inviato al DB, con prevenzione SQL Injection	
				PreparedStatement ps = getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
				ps.setInt(1, id_creatore);				
				ps.setString(2, luogo);
				ps.setTimestamp(3, this.creaTimestamp(data_ora_termine_ultimo_iscrizione));	
				ps.setTimestamp(4, this.creaTimestamp(data_ora_inizio_evento));	
				ps.setInt(5, partecipanti);
				ps.setInt(6, costo);
				ps.setString(7, titolo);
				ps.setString(8, note);
				ps.setString(9, benefici_quota);
				ps.setTimestamp(10, this.creaTimestamp(data_ora_termine_evento));
				ps.setString(11, stato);				
				ps.setInt(12, eta_minima);
				ps.setInt(13, eta_massima);
				ps.setString(14, (String)genere);

				
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
	    
		String sql = "INSERT INTO utente (nome, password) VALUES (?,?)";
		
		PreparedStatement ps = getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
		ps.setString(1, nome);
		ps.setString(2, password);		
	
		ps.executeUpdate();
		ResultSet rs = ps.getGeneratedKeys();	
		
		rs.next();
		utente.setId_utente(rs.getInt(1));
		
		refreshDatiRAM();

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
		ps.setTimestamp(3, this.creaTimestamp(data));
		
		ps.executeUpdate();
		ResultSet rs = ps.getGeneratedKeys();	

		rs.next();
		notifica.setIdNotifica(rs.getInt(1));
		
		refreshDatiRAM();

		return notifica;
	}
	
	
	public int collegaUtenteNotifica(int id_utente, int id_notifica) throws SQLException
	{	    
		String sql = "INSERT INTO relazione_utente_notifica (id_user, id_notifica) VALUES (?,?)";
		
		PreparedStatement ps = getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
		ps.setInt(1, id_utente);
		ps.setInt(2, id_notifica);		
		
		ps.executeUpdate();
		ResultSet rs = ps.getGeneratedKeys();
		rs.next();
		return rs.getInt(1);
	}
	
	public int collegaUtentePartita(Utente utente, PartitaCalcio partita_calcio) throws SQLException
	{	    
		String sql = "INSERT INTO relazione_utente_partita (id_utente, id_partita) VALUES (?,?)";
		
		PreparedStatement ps = getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
		ps.setInt(1, utente.getId_utente());
		ps.setInt(2, partita_calcio.getId());		
		
		ps.executeUpdate();
		ResultSet rs = ps.getGeneratedKeys();	
		rs.next();
		return rs.getInt(1);
	}
	
	public void segnalaFallimentoEvento(Evento evento) throws SQLException
	{
		String titolo_evento = (evento.getCampo(NomeCampi.TITOLO).getContenuto() != null) ? (String)evento.getCampo(NomeCampi.TITOLO).getContenuto() : "" ;
		String titolo = String.format("Evento %s fallito", titolo_evento);
		String contenuto = String.format(NOTIFICA_FALLIMENTO_EVENTO,titolo_evento);		
		Notifica notifica = insertNotifica(new Notifica(titolo, contenuto));
		LinkedList<Utente> list_utenti = new LinkedList<>();
		
		switch(evento.getClass().getSimpleName())
		{
			case "PartitaCalcio" : 
				{
					list_utenti = selectUtentiDiPartita((PartitaCalcio)evento);
					break;
				}
			default : break;
		}
		for(Utente utente : list_utenti)
			collegaUtenteNotifica(utente.getId_utente(), notifica.getIdNotifica());
	}
	
	public void segnalaConclusioneEvento(Evento evento) throws SQLException
	{
		String titolo_evento = (evento.getCampo(NomeCampi.TITOLO).getContenuto() != null) ? (String)evento.getCampo(NomeCampi.TITOLO).getContenuto() : "" ;
		String titolo = String.format("Evento %s concluso", titolo_evento);
		String contenuto = String.format(NOTIFICA_CONCLUSIONE_EVENTO,titolo_evento);		
		Notifica notifica = insertNotifica(new Notifica(titolo, contenuto));
		LinkedList<Utente> list_utenti = new LinkedList<>();
		
		switch(evento.getClass().getSimpleName())
		{
			case "PartitaCalcio" : 
				{
					list_utenti = selectUtentiDiPartita((PartitaCalcio)evento);
					break;
				}
			default : break;
		}
		for(Utente utente : list_utenti)
			collegaUtenteNotifica(utente.getId_utente(), notifica.getIdNotifica());
	}
	
	
	public void segnalaChiusuraEvento(Evento evento) throws SQLException
	{
		String titolo_evento = (evento.getCampo(NomeCampi.TITOLO).getContenuto() != null) ? (String)evento.getCampo(NomeCampi.TITOLO).getContenuto() : "" ;
		String titolo = String.format("Iscrizioni dell'evento %s concluse", titolo_evento);
		String contenuto = String.format(NOTIFICA_CHIUSURA_EVENTO, titolo_evento);		
		Notifica notifica = insertNotifica(new Notifica(titolo, contenuto));
		LinkedList<Utente> list_utenti = new LinkedList<>();
		
		switch(evento.getClass().getSimpleName())
		{
			case "PartitaCalcio" : 
				{
					list_utenti = selectUtentiDiPartita((PartitaCalcio)evento);
					break;
				}
			default : break;
		}
		for(Utente utente : list_utenti)
			collegaUtenteNotifica(utente.getId_utente(), notifica.getIdNotifica());
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
				+ " stato,"
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
					StatoEvento.valueOf(rs.getString(12)),
					(Integer)rs.getInt(13),
					(Integer)rs.getInt(14),
					(String)rs.getString(15));
			
			partita.setFruitori(selectUtentiDiPartita(partita));
			eventi.add(partita);
		}
		
		return eventi;
	}
	
	public PartitaCalcio selectPartitaCalcio(int  id_partita_calcio) throws SQLException
	{

		PartitaCalcio partita;
		
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
				+ " stato,"
				+ " eta_minima,"
				+ " eta_massima,"
				+ " genere"
				+ " FROM partita_calcio WHERE id LIKE ?";


		PreparedStatement ps = getConnection().prepareStatement(sql);
		ps.setInt(1, id_partita_calcio);
		
		ResultSet rs = ps.executeQuery();
		
		rs.beforeFirst();
		
		if(rs.next())
		{
			Calendar data_termine = Calendar.getInstance(); data_termine.setTimeInMillis(rs.getTimestamp(4).getTime());
			Calendar data_inizio = Calendar.getInstance(); data_inizio.setTimeInMillis(rs.getTimestamp(5).getTime());		
			Calendar data_fine = Calendar.getInstance(); data_fine.setTimeInMillis(rs.getTimestamp(11).getTime());
			Utente creatore = selectUtente(rs.getInt(2));
			
			 partita = new PartitaCalcio(
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
					StatoEvento.valueOf(rs.getString(12)),
					(Integer)rs.getInt(13),
					(Integer)rs.getInt(14),
					(String)rs.getString(15));

			eventi.add(partita);
		}
		else
			return null;
		
		partita.setFruitori(selectUtentiDiPartita(partita));
		
		return partita;

	}

	
	public ArrayList<Utente> selectUtentiAll() throws SQLException
	{
		ArrayList<Utente> utenti = new ArrayList<>();
		
		String sql = "SELECT id,nome, password FROM utente";
		
		PreparedStatement ps = getConnection().prepareStatement(sql);
		
		ResultSet rs = ps.executeQuery();
		
		rs.beforeFirst();
		
		while(rs.next())
		{		
			Utente utente = new Utente(rs.getInt(1), rs.getString(2), rs.getString(3));			
			utenti.add(utente);
		}
		
		return utenti;
	}
	
	
	public Utente selectUtente(int id_utente) throws SQLException
	{
		Utente utente = null;
		
		String sql = "SELECT id, nome, password FROM utente";
		
		PreparedStatement ps = getConnection().prepareStatement(sql);
		
		ResultSet rs = ps.executeQuery();
		
		rs.beforeFirst();
		
		if(rs.next())
		{		
			utente = new Utente(rs.getInt(1), rs.getString(2), rs.getString(3));
			
			utenti.add(utente);
			return utente;
		}
		else
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
	
	
	public LinkedList<Notifica> selectNotificheDiUtente(int id_utente) throws SQLException
	{
		LinkedList<Notifica> notifiche = new LinkedList<>();
		
		String sql = "SELECT id_notifica FROM relazione_utente_notifica WHERE id_user=?";
		
		PreparedStatement ps = getConnection().prepareStatement(sql);
		ps.setInt(1, id_utente);
		
		ResultSet rs = ps.executeQuery();
		
		rs.beforeFirst();
		
		while(rs.next())
		{		
			Notifica notifica = selectNotifica(rs.getInt(1));
			notifiche.add(notifica);
		}
		
		return notifiche;
	}
	
	
	public LinkedList<PartitaCalcio> selectPartiteDiUtente(Utente utente) throws SQLException
	{
		LinkedList<PartitaCalcio> partite = new LinkedList<>();
		
		String sql = "SELECT id_partita FROM relazione_utente_partita WHERE id_utente=?";
		
		PreparedStatement ps = getConnection().prepareStatement(sql);
		ps.setInt(1, utente.getId_utente());
		
		ResultSet rs = ps.executeQuery();
		
		rs.beforeFirst();
		
		while(rs.next())
		{		
			partite.add(selectPartitaCalcio(rs.getInt(1)));
		}
		
		return partite;
	}
	
	public LinkedList<Utente> selectUtentiDiPartita(PartitaCalcio partita) throws SQLException
	{
		LinkedList<Utente> utenti = new LinkedList<>();
		
		String sql = "SELECT id_utente FROM relazione_utente_partita WHERE id_partita=?";
		
		PreparedStatement ps = getConnection().prepareStatement(sql);
		ps.setInt(1, partita.getId());
		
		ResultSet rs = ps.executeQuery();
		
		rs.beforeFirst();
		
		while(rs.next())
		{		
			utenti.add(selectUtente(rs.getInt(1)));
		}
		
		return utenti;
	}
	
	
	public int getNumeroUtentiDiEvento(Evento evento) throws SQLException
	{
		int counter = 0;
				
		String sql = "SELECT id_utente FROM relazione_utente_partita WHERE id_partita=?";
		
		PreparedStatement ps = getConnection().prepareStatement(sql);
		ps.setInt(1, evento.getId());
		
		ResultSet rs = ps.executeQuery();
		
		rs.beforeFirst();
		
		while(rs.next())
		{		
			counter++;
		}
		
		return counter;
	}
	

	
	
	
/*
 * UPDATE : OPERAZIONI DI AGGIORNAMENTO	
 */
	
	public void updateStatoPartitaCalcio(Evento evento) throws SQLException
	{
		switch(evento.getClass().getSimpleName())
		{
		case "PartitaCalcio" : 
			{
				String sql = "UPDATE partita_calcio SET stato = ? WHERE id = ?";
				PreparedStatement ps = getConnection().prepareStatement(sql);
				ps.setString(1, evento.getStato().getCodNomeCampi());
				ps.setInt(2, evento.getId());
				refreshDatiRAM();
				break;
			}
		default : return;
		}

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
				String sql = "DELETE FROM partita_calcio WHERE id = ?" ;
				PreparedStatement ps = getConnection().prepareStatement(sql);
				ps.setInt(1, evento.getId());
				ps.executeUpdate();
				refreshDatiRAM();
				break;
			}
		default : return;
		}
	}
	
	
	public void deleteUtente(int id_utente) throws SQLException
	{
		String sql = "DELETE FROM utente WHERE id = ?";
		
		PreparedStatement ps = getConnection().prepareStatement(sql);
		ps.setInt(1, id_utente);
		
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
		String sql = "DELETE FROM relazione_utente_notifica WHERE id_user=? AND id_notifica=?" ;
		
		PreparedStatement ps = getConnection().prepareStatement(sql);
		ps.setInt(1, utente.getId_utente());
		ps.setInt(2, notifica.getIdNotifica());
		ps.executeUpdate();
//		Controllo per verificare che la notifica da cui è stato tolto il collegamento abbia almeno ancora un utente che la referenzi, in caso non ci siano utenti la si elimina
		String sql2 = "SELECT id data FROM relazione_utente_notifica WHERE id_notifica=?";
		PreparedStatement ps2 = getConnection().prepareStatement(sql2);
		ps2.setInt(1, notifica.getIdNotifica());	
		
		ResultSet rs = ps2.executeQuery();
		
		rs.beforeFirst();
		
		if(!rs.next())
			deleteNotifica(notifica.getIdNotifica());
	}
	
	
	public void deleteCollegamentoPartitaCalcioUtente(Utente utente, PartitaCalcio partita_calcio) throws SQLException
	{
//		Eliminazione collegamento tra notifica e utente nella tabella relazione_utente_notifica contenente le realzioni ManyToMany tra utenti e notifiche
		String sql = "DELETE FROM relazione_utente_partita WHERE id_utente=? AND id_partita=?" ;
		
		PreparedStatement ps = getConnection().prepareStatement(sql);
		ps.setInt(1, utente.getId_utente());
		ps.setInt(2, partita_calcio.getId());
		ps.executeUpdate();
	}
	
	
	
	
/*
 *  METODI DI UTILITY
 */
	
	
	public Integer existUtente(Utente utente) throws SQLException
	{
		refreshDatiRAM();
		if(utenti.isEmpty())
			return null;
		
		for(Utente utente1 : utenti)
		{
			if(utente1.equals(utente))
				return utente1.getId_utente();
		}
		return null;
	}

	
	public boolean existUtenteInPartita(Utente utente, PartitaCalcio partita) throws SQLException
	{
		LinkedList<PartitaCalcio> partite = selectPartiteDiUtente(utente);
		
		if(partite == null)
			return false;

		for(PartitaCalcio elemento : partite)
		{
			if(elemento.getId() == partita.getId())
				return true;
		}
		return false;
	}
	
	
	public Timestamp creaTimestamp(Calendar cal)
	{	
	      LocalDateTime ldt = LocalDateTime.ofInstant(cal.toInstant(), ZoneId.systemDefault());
	      return Timestamp.valueOf(ldt);
	}


	public String convertiCalendar (Calendar calendar)
	{

		java.util.Date dt = calendar.getTime();

		java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

		String dateTime = sdf.format(dt);
		
		return dateTime;
	}
	

	public ArrayList<Evento> getEventi() {return eventi;}

	
	public ArrayList<Utente> getUtenti() {return utenti;}	

}
