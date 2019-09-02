package it.unibs.dii.isw.socialNetworkEventi.model;

import java.sql.SQLException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedList;

import it.unibs.dii.isw.socialNetworkEventi.utility.NomeCampo;
import it.unibs.dii.isw.socialNetworkEventi.utility.Stringhe;

public class PureFabricationNotifiche implements IPureFabricationNotifiche
{
	IPersistentStorageRepository db;
	
	public void setDB(IPersistentStorageRepository db) {this.db=db;}
	
	public Notifica creaNotifica(Evento evento, String baseCorpo, String baseTitolo) {
		String titolo_evento = (evento.getCampo(NomeCampo.TITOLO) != null) ? (String)evento.getContenutoCampo(NomeCampo.TITOLO) : "" ;
		String titolo = String.format(baseTitolo, titolo_evento);
		String contenuto = String.format(baseCorpo,titolo_evento);
		return new Notifica(titolo, contenuto);
	}
	
	public void segnalaFallimentoEvento(Evento evento) throws SQLException 
	{
		Notifica notifica = creaNotifica(evento, Stringhe.NOTIFICA_FALLIMENTO_EVENTO, Stringhe.TITOLO_FALLIMENTO_EVENTO);
		notifica = db.insertNotifica(notifica);
		
		HashMap<Utente,HashMap<NomeCampo,Boolean>> list_utenti = db.selectUtentiDiEvento(evento);
		for(Utente utente : list_utenti.keySet())
		{
			db.collegaUtenteNotifica(utente.getNome(), notifica.getIdNotifica());
			db.deleteCollegamentoEventoUtente(utente.getNome(), evento);
		}
	}
	
	public void segnalaConclusioneEvento(Evento evento) throws SQLException {
		Notifica notifica = creaNotifica(evento, Stringhe.NOTIFICA_CONCLUSIONE_EVENTO, Stringhe.TITOLO_CONCLUSIONE_EVENTO);
		notifica = db.insertNotifica(notifica);
		
		HashMap<Utente,HashMap<NomeCampo,Boolean>> list_utenti = db.selectUtentiDiEvento(evento);		
		for(Utente utente : list_utenti.keySet())
			db.collegaUtenteNotifica(utente.getNome(), notifica.getIdNotifica());
	}
	
	public void segnalaRitiroEvento(Evento evento) throws SQLException
	{
		Notifica notifica = creaNotifica(evento, Stringhe.NOTIFICA_RITIRO_EVENTO, Stringhe.TITOLO_RITIRO_EVENTO);
		notifica = db.insertNotifica(notifica);
		
		HashMap<Utente,HashMap<NomeCampo,Boolean>> list_utenti = db.selectUtentiDiEvento(evento);		
		for(Utente utente : list_utenti.keySet())
		{
			db.collegaUtenteNotifica(utente.getNome(), notifica.getIdNotifica());
			db.deleteCollegamentoEventoUtente(utente.getNome(), evento);
		}
	}
	
	public void segnalaChiusuraEvento(Evento evento) throws SQLException 
	{
		String data_inizio_evento = new java.text.SimpleDateFormat("dd-MM-yyyy HH:mm").format(((Calendar)evento.getContenutoCampo(NomeCampo.D_O_INIZIO_EVENTO)).getTime());
		String titolo_evento = (evento.getCampo(NomeCampo.TITOLO) != null) ? (String)evento.getContenutoCampo(NomeCampo.TITOLO) : "" ;
		String titolo = String.format(Stringhe.TITOLO_CHIUSURA_EVENTO,titolo_evento);		
		
		HashMap<Utente,HashMap<NomeCampo,Boolean>> list_utenti = db.selectUtentiDiEvento(evento);	
		for(Utente utente : list_utenti.keySet())
		{
			String contenuto = String.format(Stringhe.NOTIFICA_CHIUSURA_EVENTO, titolo_evento, data_inizio_evento, db.getCostoEventoPerUtente(evento, utente));
			Notifica notifica = db.insertNotifica(new Notifica(titolo, contenuto));
			db.collegaUtenteNotifica(utente.getNome(), notifica.getIdNotifica());
		}
	}
	
	public void segnalaNuovoEventoAgliInteressati(Evento evento) throws SQLException
	{
		String titolo_evento = (evento.getCampo(NomeCampo.TITOLO) != null) ? (String)evento.getContenutoCampo(NomeCampo.TITOLO) : "";
		String nome_categoria = evento.getNomeCategoria().getString().replaceAll("_", " di ");
		String titolo = String.format(Stringhe.TITOLO_NUOVO_EVENTO, nome_categoria);
		String contenuto = String.format(Stringhe.NOTIFICA_NUOVO_EVENTO, nome_categoria, titolo_evento);	
		
		Notifica notifica = db.insertNotifica(new Notifica(titolo, contenuto));
		
		LinkedList<Utente> list_utenti = db.selectUtentiInteressatiACategoria(evento.getNomeCategoria());
		//rimozione utente creatore per non notificarlo del suo evento appena creato in caso abbia mostrato interesse verso la categoria dell'evento da lui creato
		list_utenti.remove(db.selectEvento(evento.getId()).getUtenteCreatore());		
		for(Utente utente : list_utenti)
			db.collegaUtenteNotifica(utente.getNome(), notifica.getIdNotifica());
	}
	
	public void segnalaEventoPerUtente(Evento evento, Utente utente_mittente, Utente utente_destinatario) throws SQLException
	{
		String titolo_evento = (evento.getCampo(NomeCampo.TITOLO) != null) ? (String)evento.getContenutoCampo(NomeCampo.TITOLO) : "";
		String nome_categoria = evento.getNomeCategoria().getString().replaceAll("_", " di ");
		String titolo = String.format(Stringhe.TITOLO_INVITO_EVENTO, nome_categoria);
		String contenuto = String.format(Stringhe.NOTIFICA_PER_INVITO_UTENTE, utente_mittente.getNome(), titolo_evento);
		Notifica notifica = new Notifica(titolo, contenuto);
		
		notifica = db.insertNotifica(notifica);
		
		db.collegaUtenteNotifica(utente_destinatario.getNome(), notifica.getIdNotifica());
	}
}