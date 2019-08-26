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
import java.util.Observable;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import com.mysql.cj.jdbc.MysqlDataSource;
import java.util.ArrayList;
import java.util.Arrays;

import it.unibs.dii.isw.socialNetworkEventi.utility.CategoriaEvento;
import it.unibs.dii.isw.socialNetworkEventi.utility.Stringhe;
import it.unibs.dii.isw.socialNetworkEventi.utility.NomeCampo;

public class MySQLRepository extends Observable implements IPersistentStorageRepository
{
	private Connection con;
	private HashMap<CategoriaEvento,ArrayList<Evento>> eventi = new HashMap<>();
	private ArrayList<Utente> utenti = new ArrayList<>();
	public HashMap<CategoriaEvento,ArrayList<Evento>> getEventi() {return eventi;}
	public ArrayList<Utente> getUtenti() {return utenti;}

	public MySQLRepository() throws SQLException
	{
		MysqlDataSource dataSource = new MysqlDataSource();
		//	specifica dei dettagli necessari alla connessione
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
	}

	public void refreshDatiRAM() throws SQLException
	{
		utenti = selectUtentiAll();
		HashMap<CategoriaEvento,ArrayList<Evento>> eventiPrima = eventi;
		eventi = selectEventiAll();
		if (countObservers()>0 && !eventiPrima.equals(eventi)) setChanged();
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
		PreparedStatement ps = evento.getPSInsertEvento(con);		
		ps.executeUpdate();
		ResultSet rs = ps.getGeneratedKeys();
		rs.next();
		evento.setId(rs.getInt(1));
		eventi.get(evento.getNomeCategoria()).add(evento);
		return evento;
	}

	
	public void insertUtente(Utente utente) throws SQLException
	{		
		PreparedStatement ps = con.prepareStatement(Stringhe.INSERT_SQL_UTENTE, Statement.RETURN_GENERATED_KEYS);
		ps.setString(1, utente.getNome());
		ps.setString(2, utente.getPassword());
		ps.setInt(3, utente.getEtaMin());
		ps.setInt(4, utente.getEtaMax());
		ps.executeUpdate();
		utenti.add(utente);
	}
	
	public Notifica insertNotifica(Notifica notifica) throws SQLException
	{
		String titolo = notifica.getTitolo();
		String contenuto = notifica.getContenuto();
		Calendar data = notifica.getData();
	    
		PreparedStatement ps = con.prepareStatement(Stringhe.INSERT_SQL_NOTIFICA, Statement.RETURN_GENERATED_KEYS);
		ps.setString(1, titolo);
		ps.setString(2, contenuto);		
		ps.setTimestamp(3, this.creaTimestamp(data));
		
		ps.executeUpdate();
		ResultSet rs = ps.getGeneratedKeys();	
		rs.next();
		notifica.setIdNotifica(rs.getInt(1));
		return notifica;
	}
	
	
	public void collegaUtenteEvento(Utente utente, Evento evento) throws Exception
	{	
		PreparedStatement ps = evento.getPSInsertIscrizioneUtenteInEvento(utente, con);
		ps.executeUpdate();
		this.refreshDatiRAM();
	}
	
	
	public void collegaUtenteCategoria(Utente utente, CategoriaEvento nome_categoria) throws SQLException
	{	    
		PreparedStatement ps = con.prepareStatement(Stringhe.INSERT_SQL_UTENTE_CATEGORIA);
		ps.setString(1, utente.getNome());
		ps.setString(2, nome_categoria.toString().toLowerCase());		
		ps.executeUpdate();
	}
	

	public void collegaUtenteNotifica(String nome_utente, int id_notifica) throws SQLException
	{	    
		PreparedStatement ps = con.prepareStatement(Stringhe.INSERT_SQL_UTENTE_NOTIFICA, Statement.RETURN_GENERATED_KEYS);
		ps.setString(1, nome_utente);
		ps.setInt(2, id_notifica);		
		ps.executeUpdate();
	}

/*
* READ : OPERAZIONI DI SELECT
*/
	
	public HashMap<CategoriaEvento,ArrayList<Evento>> selectEventiAll() throws SQLException
	{
		SimpleFactoryEvento factory = new SimpleFactoryEvento(this);
		HashMap<CategoriaEvento,ArrayList<Evento>> eventi = new HashMap<CategoriaEvento,ArrayList<Evento>>();
		//Toglie la categoria "default"
		CategoriaEvento[] categorie = Arrays.copyOfRange(CategoriaEvento.values(), 1, CategoriaEvento.values().length);
		for (CategoriaEvento cat: categorie) {
			PreparedStatement ps = con.prepareStatement(Stringhe.ottieniStringaDesiderata(Stringhe.SELECT_SQL_EVENTO, cat));
			eventi.put(cat, factory.crea(cat, ps.executeQuery()));
		}
		
		return eventi;
	}
	
	public Evento selectEvento(int id_evento) 
	{
		if(eventi == null)
			return null;
		for(CategoriaEvento categoria : eventi.keySet())
			for(Evento evento : eventi.get(categoria))
				if(evento.getId() == id_evento)
					return evento;
		return null;
	}
	
	
	public ArrayList<Utente> selectUtentiAll() throws SQLException
	{
		ArrayList<Utente> utenti = new ArrayList<>();
		PreparedStatement ps = con.prepareStatement(Stringhe.SELECT_SQL_UTENTI);
		ResultSet rs = ps.executeQuery();	
		if(rs == null) return new ArrayList<>();
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

	
	public Utente selectUtente(String nome)  
	{
		for (Utente u : utenti)
			if (u.getNome().equals(nome)) return u;
		return null;
	}

		
	public Notifica selectNotifica(int id_notifica) throws SQLException
	{		
		PreparedStatement ps = con.prepareStatement(Stringhe.SELECT_SQL_NOTIFICA);
		ps.setInt(1, id_notifica);		
		ResultSet rs = ps.executeQuery();
		rs.beforeFirst();		
		if(rs.next())
		{
			Calendar data = Calendar.getInstance(); data.setTimeInMillis(rs.getTimestamp(3).getTime());
			Notifica notifica = new Notifica(id_notifica,rs.getString(1), rs.getString(2), data);
			return notifica;
		}
		else return null;
	}
	
	
	public LinkedList<Notifica> selectNotificheDiUtente(String nome_utente) throws SQLException
	{
		LinkedList<Notifica> notifiche = new LinkedList<>();
		PreparedStatement ps = con.prepareStatement(Stringhe.SELECT_SQL_NOTIFICHE_UTENTE);
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
	
	
	public HashMap<CategoriaEvento,LinkedList<Evento>> selectEventiDiUtente(String nome_utente) throws SQLException
	{
		HashMap<CategoriaEvento,LinkedList<Evento>> hashmap = new HashMap<CategoriaEvento,LinkedList<Evento>>();
		if(eventi == null)
			eventi = this.selectEventiAll();
		for(CategoriaEvento categoria : eventi.keySet())
		{
			LinkedList<Evento> list = new LinkedList<>();
			for(Evento evento : eventi.get(categoria))
				if(evento.getPartecipanti_campiOpt().keySet().contains(selectUtente(nome_utente)))
					list.add(evento);
			hashmap.put(categoria, list);
		}
		return hashmap;
	}
	
	
	public HashMap<Utente,HashMap<NomeCampo,Boolean>> selectUtentiDiEvento(Evento evento) throws SQLException
	{
		HashMap<Utente,HashMap<NomeCampo,Boolean>> utenti_campiOpt = new HashMap<Utente,HashMap<NomeCampo,Boolean>>();
		PreparedStatement ps = evento.getPSSelectUtenti(con);
		ResultSet rs = ps.executeQuery();		
		rs.beforeFirst();	
		while(rs.next())	
		{
			Utente utente = selectUtente(rs.getString(1));
			if(rs.getMetaData().getColumnCount() == 1)
				utenti_campiOpt.put(utente, null);
			else
			{
				HashMap<NomeCampo,Boolean> campi_opt = new HashMap<NomeCampo,Boolean>();
				if(evento.getNomeCategoria().equals(CategoriaEvento.SCII))
				{
					campi_opt.put(NomeCampo.BIGLIETTO_BUS, rs.getBoolean(2));
					campi_opt.put(NomeCampo.PRANZO, rs.getBoolean(3));
					campi_opt.put(NomeCampo.AFFITTO_SCII, rs.getBoolean(4));
					utenti_campiOpt.put(utente, campi_opt);
				}
			}
		}				
		return utenti_campiOpt;
	}
	
	public LinkedList<CategoriaEvento> selectCategorieDiUtente(String nome_utente) throws SQLException
	{
		LinkedList<CategoriaEvento> categorie = new LinkedList<>();
		PreparedStatement ps = con.prepareStatement(Stringhe.SELECT_SQL_CATEGORIE_UTENTE);
		ps.setString(1, nome_utente);
		ResultSet rs = ps.executeQuery();
		rs.beforeFirst();	
		while(rs.next())
		{
			String nome_categoria = rs.getString(1);
			categorie.add(CategoriaEvento.convertiStringInCategoria(nome_categoria));
		}	
		return categorie;
	}
	
	public LinkedList<Utente> selectUtentiInteressatiACategoria(CategoriaEvento nome_categoria) throws SQLException
	{
		LinkedList<Utente> utenti = new LinkedList<>();
		PreparedStatement ps = con.prepareStatement(Stringhe.SELECT_SQL_UTENTI_CATEGORIA);
		ps.setString(1, nome_categoria.toString());
		
		ResultSet rs = ps.executeQuery();
		rs.beforeFirst();	
		while(rs.next())
			utenti.add(selectUtente(rs.getString(1)));
		return utenti;
	}
	

	public HashMap<CategoriaEvento,LinkedList<Utente>> selectUtentiDaEventiPassati(String nome_utente_creatore) throws SQLException 
	{
		HashMap<CategoriaEvento,LinkedList<Utente>> hash_map= new HashMap<>();
		for(CategoriaEvento nome_categoria: eventi.keySet())
		{
			LinkedList<Utente> utenti = new LinkedList<>();
			ResultSet rs = this.selectUtentiPassatiDiUtenteCreatoreDaCategoria(nome_utente_creatore, nome_categoria);
			if(rs == null)
				hash_map.put(nome_categoria, null);
			else
			{
				rs.beforeFirst();
				while(rs.next()) 
				{
					Utente utente = selectUtente(rs.getString(1));
					utenti.add(utente); 					
				}
				hash_map.put(nome_categoria, utenti);
			}
		}
		return hash_map;
	}		

	
	private ResultSet selectUtentiPassatiDiUtenteCreatoreDaCategoria(String nome_utente_creatore, CategoriaEvento nome_categoria) throws SQLException
	{
		PreparedStatement ps = null;
		ResultSet rs;
		ps = con.prepareStatement(Stringhe.ottieniStringaDesiderata(Stringhe.SELECT_SQL_UTENTI_PASSATI_EVENTO, nome_categoria));
		ps.setString(1, nome_utente_creatore);
		ps.setString(2, nome_utente_creatore);
		rs = ps.executeQuery();
		return rs;
	}


/*
 * UPDATE : OPERAZIONI DI AGGIORNAMENTO	
 */

	public void updateEvento(Evento evento) throws SQLException
	{
		PreparedStatement ps = evento.getPSUpdateStatoEvento(con);
		ps.executeUpdate();
		for(Evento e : eventi.get(evento.getNomeCategoria()))
		{
			if(e.equals(evento))
				e.setStato(evento.getStato());
		}
	}
	
	public void updateEtaMinUtente(String nome_utente, int eta_min) throws SQLException
	{
		PreparedStatement ps = con.prepareStatement(Stringhe.UPDATE_SQL_ETA_MIN_UTENTE);
		ps.setInt(1, eta_min);
		ps.setString(2, nome_utente);
		ps.executeUpdate();
	}
	
	public void updateEtaMaxtente(String nome_utente, int eta_max) throws SQLException {
		PreparedStatement ps = con.prepareStatement(Stringhe.UPDATE_SQL_ETA_MAX_UTENTE);
		ps.setInt(1, eta_max);
		ps.setString(2, nome_utente);
		ps.executeUpdate();
	}
	
	
	
/*
 * DELETE : OPERAZIONI DI DELETE	
 */
	
	
	
	
	public void deleteEvento(Evento evento) throws SQLException
	{
		PreparedStatement ps = evento.getPSDeleteEvento(con);
		ps.executeUpdate();

		eventi.get(evento.getNomeCategoria()).remove(evento);
	}
	
	
	public void deleteUtente(String nome_utente) throws SQLException
	{
		PreparedStatement ps = con.prepareStatement(Stringhe.DELETE_SQL_UTENTE);
		ps.setString(1, nome_utente);
		ps.executeUpdate();
		utenti.remove(this.selectUtente(nome_utente));
	}


	public void deleteNotifica(int id_notifica) throws SQLException
	{
		PreparedStatement ps = con.prepareStatement(Stringhe.DELETE_SQL_NOTIFICA);
		ps.setInt(1, id_notifica);
		ps.executeUpdate();
		this.refreshDatiRAM();
	}
	
	
	public void deleteCollegamentoNotificaUtente(Utente utente, Notifica notifica) throws SQLException
	{
//		Eliminazione collegamento tra notifica e utente nella tabella relazione_utente_notifica contenente le realzioni ManyToMany tra utenti e notifiche		
		PreparedStatement ps = con.prepareStatement(Stringhe.DELETE_SQL_RELAZIONE_UTENTE_NOTIFICA);
		ps.setString(1, utente.getNome());
		ps.setInt(2, notifica.getIdNotifica());
		ps.executeUpdate();
		
//		Controllo per verificare che la notifica da cui è stato tolto il collegamento abbia almeno ancora un utente che la referenzi, in caso non ci siano utenti la elimino
		PreparedStatement ps2 = con.prepareStatement(Stringhe.SELECT_SQL_UTENTI_DI_NOTIFICA);
		ps2.setInt(1, notifica.getIdNotifica());			
		ResultSet rs = ps2.executeQuery();
		
		rs.beforeFirst();
		if(!rs.next())
			deleteNotifica(notifica.getIdNotifica());
	}
	
	
	public void deleteCollegamentoEventoUtente(String nome_utente, Evento evento) throws SQLException 
	{
		PreparedStatement ps = evento.getPSDeleteRelazioneEventoUtente(nome_utente, con);
		ps.executeUpdate();		
		this.refreshDatiRAM();
	}

	
	public void deleteCollegamentoCategoriaUtente(String nome_utente, CategoriaEvento nome_categoria) throws SQLException 
	{
		PreparedStatement ps = con.prepareStatement(Stringhe.DELETE_SQL_RELAZIONE_UTENTE_CATEGORIA);
		ps.setString(1, nome_utente);
		ps.setString(2, nome_categoria.toString().toLowerCase());
		ps.executeUpdate();
	}
	
	public void deleteEventiDiUtente(Utente utente) throws SQLException
	{
		for(CategoriaEvento nome_categoria : eventi.keySet())
			this.deleteEventiDiUtenteDiCategoria(utente, nome_categoria);
		refreshDatiRAM();		
	}
	
	private void deleteEventiDiUtenteDiCategoria(Utente utente, CategoriaEvento nome_categoria) throws SQLException
	{
		PreparedStatement ps = null;
		ps = con.prepareStatement(Stringhe.ottieniStringaDesiderata(Stringhe.DELETE_SQL_EVENTI_UTENTE, nome_categoria));
		ps.setString(1, utente.getNome());
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
		LinkedList<Evento> eventi;
		if (selectEventiDiUtente(utente.getNome()) != null)
			eventi = selectEventiDiUtente(utente.getNome()).get(evento.getNomeCategoria());
		else
			return false;
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
		int costo = (Integer)evento.getCampo(NomeCampo.COSTO).getContenuto();
		HashMap<NomeCampo, Boolean> campi_opt = evento.getCampiOptDiUtente(utente);
		if(campi_opt != null)
		{
			for(NomeCampo nome_campo : campi_opt.keySet())
				if(campi_opt.get(nome_campo) == true)
					costo+=(Integer)evento.getCampo(nome_campo).getContenuto();
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
	
	public void setChangedForObservers() {
		setChanged();
	}
}