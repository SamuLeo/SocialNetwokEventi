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
import java.util.HashMap;
import java.util.LinkedList;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import com.mysql.cj.jdbc.MysqlDataSource;
import java.util.ArrayList;

import it.unibs.dii.isw.socialNetworkEventi.utility.CategorieEvento;
import it.unibs.dii.isw.socialNetworkEventi.utility.NomeCampi;
import it.unibs.dii.isw.socialNetworkEventi.utility.StatoEvento;

public class DataBase 
{
	private static final String NOTIFICA_FALLIMENTO_EVENTO 	= "Caro utente,\n l'evento %s a cui si era iscritto è fallito a causa della scarsa richiesta di partecipazione";
	private static final String NOTIFICA_CONCLUSIONE_EVENTO = "Caro utente,\n l'evento %s a cui si era iscritto è concluso";
	private static final String NOTIFICA_CHIUSURA_EVENTO 	= "Caro utente,\n l'evento %s a cui si era iscritto ha raggiunto il numero massimo si iscrizioni, per tanto si svolgerà";
	private static final String NOTIFICA_RITIRO_EVENTO 		= "Caro utente,\n siamo spiacenti: l'evento %s a cui era iscritto è stato ritirato";
	private static final String NOTIFICA_NUOVO_EVENTO 		= "Caro utente, la informiamo che un nuovo evento della categoria %s dal nome %s è disponibile";
	private static final String NOTIFICA_PER_INVITO_UTENTE	= "Caro utente, è stato invitato da %s all'evento di nome %s";
	private static final String tabelle_db_eventi[][] = {{CategorieEvento.PARTITA_CALCIO.getString(),"relazione_utente_partita"}};

	
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

	
/*
 * OPERAZIONI C.R.U.D. SU DB MYSQL
 */
	

		
/*
 * CREATE : OPERAZIONI DI INSERT	
 */
	
	public Evento insertEvento(Evento evento) throws SQLException
	{
		switch(evento.getNomeCategoria())
		{
		case PARTITA_CALCIO : 
			{
				//Estrazione campi dall'oggetto evento: obbligatori
				int id_creatore									= evento.getUtenteCreatore().getId_utente();
				String luogo 									= (String) evento.getCampo(NomeCampi.LUOGO).getContenuto();
				Calendar data_ora_termine_ultimo_iscrizione		= (Calendar) evento.getCampo(NomeCampi.D_O_CHIUSURA_ISCRIZIONI).getContenuto();
				Calendar data_ora_inizio_evento 				= (Calendar) evento.getCampo(NomeCampi.D_O_INIZIO_EVENTO).getContenuto();
				int partecipanti								= (Integer) evento.getCampo(NomeCampi.PARTECIPANTI).getContenuto();
				int costo										= (Integer) evento.getCampo(NomeCampi.COSTO).getContenuto();
				int eta_minima									= (Integer) evento.getCampo(NomeCampi.ETA_MINIMA).getContenuto();
			    int eta_massima									= (Integer) evento.getCampo(NomeCampi.ETA_MASSIMA).getContenuto();
			    String genere									= (String) evento.getCampo(NomeCampi.GENERE).getContenuto();
				//Campi opzionali
				String titolo									= (String)( evento.getCampo(NomeCampi.TITOLO) != null ? evento.getCampo(NomeCampi.TITOLO).getContenuto() : null);
				String note										= (String)( evento.getCampo(NomeCampi.NOTE) != null ? evento.getCampo(NomeCampi.NOTE).getContenuto() : null);
				String benefici_quota							= (String)( evento.getCampo(NomeCampi.BENEFICI_QUOTA) != null ? evento.getCampo(NomeCampi.BENEFICI_QUOTA).getContenuto() : null);
				Calendar data_ora_termine_evento				= evento.getCampo(NomeCampi.D_O_TERMINE_EVENTO) != null ? (Calendar) evento.getCampo(NomeCampi.D_O_TERMINE_EVENTO).getContenuto() : null;
				Calendar data_ora_termine_ritiro_iscrizione		= evento.getCampo(NomeCampi.D_O_TERMINE_RITIRO_ISCRIZIONE) != null ? (Calendar) evento.getCampo(NomeCampi.D_O_TERMINE_RITIRO_ISCRIZIONE).getContenuto() : null;
				int tolleranza_max								= evento.getCampo(NomeCampi.TOLLERANZA_MAX) != null ? (Integer)(evento.getCampo(NomeCampi.TOLLERANZA_MAX).getContenuto()) : 0;
			    
//				Stringa contenente uno script sql per inserire la partita di calcio
				String sql = "INSERT INTO partita_calcio "
						+ "(id_creatore, luogo, data_ora_termine_ultimo_iscrizione, data_ora_inizio_evento, partecipanti, costo, titolo, note,"
						+ "benefici_quota, data_ora_termine_evento, data_ora_termine_ritiro_iscrizione, tolleranza_max, stato, eta_minima, eta_massima, genere)"
						+ " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
				
//				script contenente la stringa sql precedentemente specificata inviato al DB, con prevenzione SQL Injection	
				PreparedStatement ps = getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
				ps.setInt		(1, id_creatore);				
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
				
				ps.setInt		(14, eta_minima);
				ps.setInt		(15, eta_massima);
				ps.setString	(16, (String)genere);

				ps.executeUpdate();
				ResultSet rs = ps.getGeneratedKeys();
	
				rs.next();
				evento.setId(rs.getInt(1));
				
				eventi = selectEventiAll(); 

				return evento;
			}
		default : return null;
		}

	}

	public Utente insertUtente(Utente utente) throws SQLException
	{
		String sql = "INSERT INTO utente (nome, password, eta_min, eta_max) VALUES (?,?,?,?)";
		
		PreparedStatement ps = getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
		ps.setString(1, utente.getNome());
		ps.setString(2, utente.getPassword());		
		ps.setInt(3, utente.getEtaMin());	
		ps.setInt(4, utente.getEtaMax());		

		ps.executeUpdate();
		ResultSet rs = ps.getGeneratedKeys();	
		rs.next();
		utente.setId_utente(rs.getInt(1));
		
		utenti = selectUtentiAll();

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
		return notifica;
	}
	
	
	public void collegaUtenteNotifica(int id_utente, int id_notifica) throws SQLException
	{	    
		String sql = "INSERT INTO relazione_utente_notifica (id_user, id_notifica) VALUES (?,?)";
		PreparedStatement ps = getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
		ps.setInt(1, id_utente);
		ps.setInt(2, id_notifica);		
		ps.executeUpdate();
	}
	
	public void collegaUtenteEvento(Utente utente, Evento evento) throws SQLException
	{	
		switch(evento.getNomeCategoria())
		{
		case PARTITA_CALCIO:
			{
				String sql = "INSERT INTO relazione_utente_partita (id_utente, id_partita) VALUES (?,?)";
				PreparedStatement ps = getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
				ps.setInt(1, utente.getId_utente());
				ps.setInt(2, evento.getId());		
				ps.executeUpdate();
				break;
			}
		default:
			break;
		}
	}
	
	
	public void collegaUtenteCategoria(Utente utente, CategorieEvento nome_categoria) throws SQLException
	{	    
		String sql = "INSERT INTO relazione_utente_categoria (id_u, nome_categoria) VALUES (?,?)";
		PreparedStatement ps = getConnection().prepareStatement(sql);
		ps.setInt(1, utente.getId_utente());
		ps.setString(2, nome_categoria.toString().toLowerCase());		
		ps.executeUpdate();
	}
	

	public void segnalaFallimentoEvento(Evento evento) throws SQLException 
	{
		String titolo_evento = (evento.getCampo(NomeCampi.TITOLO).getContenuto() != null) ? (String)evento.getCampo(NomeCampi.TITOLO).getContenuto() : "" ;

		switch(evento.getNomeCategoria()) 
		{
			case PARTITA_CALCIO :
					segnalaFallimentoPartitaCalcio((PartitaCalcio)evento, titolo_evento);
					break;
			default : break;
		}
	}
	
	
	public void segnalaFallimentoPartitaCalcio(PartitaCalcio partita, String titolo_evento) throws SQLException 
	{
		String titolo = String.format("Evento %s fallito", titolo_evento);
		String contenuto = String.format(NOTIFICA_FALLIMENTO_EVENTO,titolo_evento);	
		Notifica notifica = new Notifica(titolo, contenuto);
		notifica = insertNotifica(notifica);
		LinkedList<Utente>list_utenti = selectUtentiDiEvento(partita);
		
		for(Utente utente : list_utenti)
		{
			collegaUtenteNotifica(utente.getId_utente(), notifica.getIdNotifica());
			deleteCollegamentoEventoUtente(utente.getId_utente(), partita);
		}
	}

	
	public void segnalaConclusioneEvento(Evento evento) throws SQLException {
		String titolo_evento = (evento.getCampo(NomeCampi.TITOLO).getContenuto() != null) ? (String)evento.getCampo(NomeCampi.TITOLO).getContenuto() : "" ;
		switch(evento.getClass().getSimpleName()) {
			case "PartitaCalcio" :
					segnalaConclusionePartitaCalcio((PartitaCalcio)evento, titolo_evento);
					break;
			default : break;
		}
	
	}
	
	
	public void segnalaConclusionePartitaCalcio(PartitaCalcio partita, String titolo_evento) throws SQLException
	{
		String titolo = String.format("Evento %s concluso", titolo_evento);
		String contenuto = String.format(NOTIFICA_CONCLUSIONE_EVENTO,titolo_evento);		
		Notifica notifica = insertNotifica(new Notifica(titolo, contenuto));
		LinkedList<Utente> list_utenti = selectUtentiDiEvento(partita);
		
		for(Utente utente : list_utenti)
		{
			collegaUtenteNotifica(utente.getId_utente(), notifica.getIdNotifica());
			deleteCollegamentoEventoUtente(utente.getId_utente(), partita);
		}
	}
	
	
	public void segnalaChiusuraEvento(Evento evento) throws SQLException {
		String titolo_evento = (evento.getCampo(NomeCampi.TITOLO).getContenuto() != null) ? (String)evento.getCampo(NomeCampi.TITOLO).getContenuto() : "" ;
		switch(evento.getClass().getSimpleName()) {
			case "PartitaCalcio" : 
					segnalaChiusuraPartitaCalcio((PartitaCalcio)evento, titolo_evento);
					break;
			default : break;
		}
	}
	
	
	public void segnalaChiusuraPartitaCalcio(PartitaCalcio partita, String titolo_evento) throws SQLException
	{
		String titolo = String.format("Iscrizioni dell'evento %s concluse", titolo_evento);
		String contenuto = String.format(NOTIFICA_CHIUSURA_EVENTO, titolo_evento);		
		Notifica notifica = insertNotifica(new Notifica(titolo, contenuto));
		LinkedList<Utente> list_utenti = selectUtentiDiEvento(partita);
		
		for(Utente utente : list_utenti)
			collegaUtenteNotifica(utente.getId_utente(), notifica.getIdNotifica());
	}
	
	
	public void segnalaRitiroEvento(Evento evento) throws SQLException
	{
		String titolo_evento = (evento.getCampo(NomeCampi.TITOLO).getContenuto() != null) ? (String)evento.getCampo(NomeCampi.TITOLO).getContenuto() : "" ;
		switch(evento.getClass().getSimpleName()) {
			case "PartitaCalcio" : 
					segnalaRitiroPartitaCalcio((PartitaCalcio)evento, titolo_evento);
					break;
			default : break;
		}
	}
	
	
	public void segnalaRitiroPartitaCalcio(PartitaCalcio partita, String titolo_evento) throws SQLException
	{
		String titolo = String.format("Partita %s ritirata", titolo_evento);
		String contenuto = String.format(NOTIFICA_RITIRO_EVENTO, titolo_evento);		
		Notifica notifica = insertNotifica(new Notifica(titolo, contenuto));
		LinkedList<Utente> list_utenti = selectUtentiDiEvento(partita);
		
		for(Utente utente : list_utenti)
		{
			collegaUtenteNotifica(utente.getId_utente(), notifica.getIdNotifica());
			deleteCollegamentoEventoUtente(utente.getId_utente(), partita);
		}
	}
	
	public void segnalaNuovoEventoAgliInteressati(Evento evento) throws SQLException
	{
		String titolo_evento = (evento.getCampo(NomeCampi.TITOLO).getContenuto() != null) ? (String)evento.getCampo(NomeCampi.TITOLO).getContenuto() : "" ;

		switch(evento.getNomeCategoria())
		{
			case PARTITA_CALCIO :
					segnalaNuovaPartitaCalcio(evento.getId(), titolo_evento);
					break;
			default : break;
		}
	}
	
	
	public void segnalaNuovaPartitaCalcio(int id_partita, String titolo_evento) throws SQLException
	{
		String titolo = String.format("Nuova partita di calcio disponbile!");
		String contenuto = String.format(NOTIFICA_NUOVO_EVENTO, CategorieEvento.PARTITA_CALCIO.toString(), titolo_evento);		
		Notifica notifica = insertNotifica(new Notifica(titolo, contenuto));
		LinkedList<Utente> list_utenti = selectUtentiInteressatiACategoria(CategorieEvento.PARTITA_CALCIO);
//		rimozione utente creatore per non notificarlo del suo evento appena creato in caso abbia mostrato interesse verso la categoria dell'evento da lui creato
		list_utenti.remove(selectPartitaCalcio(id_partita).getUtenteCreatore());
		
		for(Utente utente : list_utenti)
			collegaUtenteNotifica(utente.getId_utente(), notifica.getIdNotifica());
	}
	
	
	public void segnalaEventoPerUtente(Evento evento, Utente utente_mittente, Utente utente_destinatario) throws SQLException
	{
		String nome_categoria = evento.getNomeCategoria().getString().replaceAll("_", " di ");
		String titolo = String.format("Sei stato invitato ad un nuovo evento della categoria %s!", nome_categoria);
		String contenuto = String.format(NOTIFICA_PER_INVITO_UTENTE, utente_mittente.getNome(), evento.getCampo(NomeCampi.TITOLO).getContenuto());
		Notifica notifica = new Notifica(titolo, contenuto);
		
		notifica = insertNotifica(notifica);
		collegaUtenteNotifica(utente_destinatario.getId_utente(), notifica.getIdNotifica());
	}
	
/*
 * READ : OPERAZIONI DI SELECT
 */
	
	public HashMap<CategorieEvento,ArrayList<Evento>> selectEventiAll() throws SQLException
	{
		HashMap<CategorieEvento,ArrayList<Evento>> eventi = new HashMap<CategorieEvento,ArrayList<Evento>>();
		
		eventi.put(CategorieEvento.PARTITA_CALCIO, selectParititeCalcioAll());
		
		return eventi;
	}

	
	public ArrayList<Evento> selectParititeCalcioAll() throws SQLException
	{
		ArrayList<Evento> partite = new ArrayList<Evento>();
	
		String sql = "SELECT id, id_creatore, luogo, data_ora_termine_ultimo_iscrizione, data_ora_inizio_evento, partecipanti,"
				+ " costo, titolo, note, benefici_quota, data_ora_termine_evento, data_ora_termine_ritiro_iscrizione,"
				+ " tolleranza_max, stato, eta_minima, eta_massima, genere FROM partita_calcio";
		
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
			
			Utente creatore = selectUtente(rs.getInt(2));
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

				partita.setFruitori(selectUtentiDiEvento(partita));
				partite.add(partita);
		}
		return partite;
	}
	
	
	public PartitaCalcio selectPartitaCalcio(int id) 
	{
		if(eventi == null)
			return null;
		for (Evento e : eventi.get(CategorieEvento.PARTITA_CALCIO))
			if (e instanceof PartitaCalcio && ((PartitaCalcio)e).getId() == id) return (PartitaCalcio)e;
		return null;
	}


	public ArrayList<Utente> selectUtentiAll() throws SQLException
	{
		ArrayList<Utente> utenti = new ArrayList<>();
		String sql = "SELECT id, nome, password, eta_min, eta_max FROM utente";		
		PreparedStatement ps = getConnection().prepareStatement(sql);
		
		ResultSet rs = ps.executeQuery();		
		rs.beforeFirst();		
		while(rs.next())
		{		
			Utente utente = new Utente(rs.getInt(1), rs.getString(2), rs.getString(3), rs.getInt(4), rs.getInt(5));	
			utente.setCategorieInteressi(selectCateorieDiUtente(utente.getId_utente()));
			utente.setEventi(selectEventiDiUtente(utente.getId_utente()));
			utente.setNotifiche(selectNotificheDiUtente(utente.getId_utente()));
			utenti.add(utente);
		}		
		return utenti;
	}

	
	public Utente selectUtente(int id) {
		for (Utente u : utenti)
			if (u.getId_utente() == id) return u;
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
	
	
	public HashMap<CategorieEvento,LinkedList<Evento>> selectEventiDiUtente(int id_utente) throws SQLException
	{
		HashMap<CategorieEvento,LinkedList<Evento>> hashmap = new HashMap<CategorieEvento,LinkedList<Evento>>();
		if(eventi == null)
			return null;
		
		for(CategorieEvento categoria : eventi.keySet())
		{
			LinkedList<Evento> list = new LinkedList<>();
			for(Evento evento : eventi.get(categoria))
				if(evento.getFruitori().contains(selectUtente(id_utente)))
					list.add(evento);
			hashmap.put(categoria, list);
		}		
		return hashmap;
//		String sql = "SELECT id_partita FROM relazione_utente_partita WHERE id_utente=?";		
//		PreparedStatement ps = getConnection().prepareStatement(sql);
//		ps.setInt(1, id_utente);
//
//		ResultSet rs = ps.executeQuery();		
//		rs.beforeFirst();		
//		while(rs.next())
//			eventi.add(selectPartitaCalcio(rs.getInt(1)));		
//		}
//		return partite;
	}
	
	
	public LinkedList<Utente> selectUtentiDiEvento(Evento evento) throws SQLException
	{
		
		LinkedList<Utente> utenti = new LinkedList<>();
		
		String sql = "SELECT id_utente FROM relazione_utente_partita WHERE id_partita=?";		
		PreparedStatement ps = getConnection().prepareStatement(sql);
		ps.setInt(1, evento.getId());
		
		ResultSet rs = ps.executeQuery();		
		rs.beforeFirst();		
		while(rs.next())	
			utenti.add(selectUtente(rs.getInt(1)));
		return utenti;
	}
	
	private LinkedList<CategorieEvento> selectCateorieDiUtente(int id_utente) throws SQLException
	{
		LinkedList<CategorieEvento> categorie = new LinkedList<>();
		
		String sql = "SELECT nome_categoria FROM relazione_utente_categoria WHERE id_u=?";	
		PreparedStatement ps = getConnection().prepareStatement(sql);
		ps.setInt(1, id_utente);
		
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
		String sql = "SELECT id_u FROM relazione_utente_categoria WHERE nome_categoria=?";	
		PreparedStatement ps = getConnection().prepareStatement(sql);
		ps.setString(1, nome_categoria.toString());
		
		ResultSet rs = ps.executeQuery();
		rs.beforeFirst();	
		while(rs.next())
			utenti.add(selectUtente(rs.getInt(1)));
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
		while(rs.next()) counter++;
		return counter;
	}
	

	public HashMap<CategorieEvento,LinkedList<Utente>> selectUtentiDaEventiPassati(int id_utente_creatore) throws SQLException 
	{
		HashMap<CategorieEvento,LinkedList<Utente>> hash_map= new HashMap<>();
		String sql1 = "SELECT id FROM %s WHERE id_creatore=?";
		String tabella, sql_con_nome_tabella, sql2, nome_tabella_relazione, sql_con_nome_relazione;
		LinkedList<Utente> utenti;
		for(int i=0 ; i < tabelle_db_eventi.length ; i++) {
			utenti = new LinkedList<>();
			tabella = tabelle_db_eventi[i][0];
			sql_con_nome_tabella = String.format(sql1, tabella);
			
			PreparedStatement ps1 = getConnection().prepareStatement(sql_con_nome_tabella);
			ps1.setInt(1, id_utente_creatore);
			ResultSet rs1 = ps1.executeQuery();
			
			rs1.beforeFirst();
			while(rs1.next()) {
				sql2 = "SELECT id_utente FROM %s WHERE id_partita=? AND id_utente!=?";
				nome_tabella_relazione = tabelle_db_eventi[i][1];
				sql_con_nome_relazione = String.format(sql2, nome_tabella_relazione);
				
				PreparedStatement ps2 = getConnection().prepareStatement(sql_con_nome_relazione);
				ps2.setInt(1, rs1.getInt(1));
				ps2.setInt(2, id_utente_creatore);
				ResultSet rs2 = ps2.executeQuery();
				
				rs2.beforeFirst();
				while(rs2.next()) {
					Utente utente = selectUtente(rs2.getInt(1));
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

	public void updateStatoPartitaCalcio(Evento evento) throws SQLException
	{
		switch(evento.getClass().getSimpleName())
		{
		case "PartitaCalcio" : 
			{
				String sql = "UPDATE partita_calcio SET stato = ? WHERE id = ?";
				PreparedStatement ps = getConnection().prepareStatement(sql);
				ps.setString(1, evento.getStato().getString());
				ps.setInt(2, evento.getId());
				ps.executeUpdate();
				refreshDatiRAM();//   <---- Potrebbe causare un loop
				break;
			}
		default : return;
		}

	}
	
	public void updateEtaMinUtente(int id_utente, int eta_min) throws SQLException
	{
		String sql = "UPDATE utente SET eta_min = ? WHERE id = ?";
		PreparedStatement ps = getConnection().prepareStatement(sql);
		ps.setInt(1, eta_min);
		ps.setInt(2, id_utente);
		ps.executeUpdate();
		refreshDatiRAM();
	}
	
	public void updateEtaMaxtente(int id_utente, int eta_max) throws SQLException {
		String sql = "UPDATE utente SET eta_max = ? WHERE id = ?";
		PreparedStatement ps = getConnection().prepareStatement(sql);
		ps.setInt(1, eta_max);
		ps.setInt(2, id_utente);
		ps.executeUpdate();
		refreshDatiRAM();
	}
	
/*
 * DELETE : OPERAZIONI DI DELETE	
 */
	
	public void deletePartita(int id_partita) throws SQLException
	{
		String sql = "DELETE FROM partita_calcio WHERE id = ?" ;
		PreparedStatement ps = getConnection().prepareStatement(sql);
		ps.setInt(1, id_partita);
		ps.executeUpdate();
		refreshDatiRAM();
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
		String sql2 = "SELECT id_user data FROM relazione_utente_notifica WHERE id_notifica=?";
		PreparedStatement ps2 = getConnection().prepareStatement(sql2);
		ps2.setInt(1, notifica.getIdNotifica());	
		
		ResultSet rs = ps2.executeQuery();
		
		rs.beforeFirst();
		
		if(!rs.next())
			deleteNotifica(notifica.getIdNotifica());
	}
	
	
	public void deleteCollegamentoEventoUtente(int id_utente, Evento evento) throws SQLException {
//		Eliminazione collegamento tra notifica e utente nella tabella relazione_utente_notifica contenente le realzioni ManyToMany tra utenti e notifiche
		switch(evento.getNomeCategoria())
		{
		case PARTITA_CALCIO:
			{				
				String sql = "DELETE FROM relazione_utente_partita WHERE id_utente=? AND id_partita=?" ;
				PreparedStatement ps = getConnection().prepareStatement(sql);
				ps.setInt(1, id_utente);
				ps.setInt(2, evento.getId());
				ps.executeUpdate();
				break;
			}
		default:
			break;
		}
	}

	
	public void deleteCollegamentoCategoriaUtente(int id_utente, CategorieEvento nome_categoria) throws SQLException {
//		Eliminazione collegamento tra notifica e utente nella tabella relazione_utente_notifica contenente le realzioni ManyToMany tra utenti e notifiche
		String sql = "DELETE FROM relazione_utente_categoria WHERE id_u=? AND nome_categoria=?" ;
		PreparedStatement ps = getConnection().prepareStatement(sql);
		ps.setInt(1, id_utente);
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
		LinkedList<Evento> partite = selectEventiDiUtente(utente.getId_utente()).get(evento.getNomeCategoria());
		if(partite == null)
			return false;
		for(Evento elemento : partite) 
		{
			if(elemento.getId() == evento.getId())
				return true;
		}
		return false;
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