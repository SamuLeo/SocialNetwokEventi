package it.unibs.dii.isw.socialNetworkEventi.model;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

import it.unibs.dii.isw.socialNetworkEventi.utility.CategoriaEvento;
import it.unibs.dii.isw.socialNetworkEventi.utility.NomeCampo;

public abstract interface IPersistentStorageRepository 
{
//	INSERT
	public abstract Evento insertEvento(Evento evento) throws SQLException;
	public abstract void insertUtente(Utente utente) throws SQLException;
	public abstract Notifica insertNotifica(Notifica notifica) throws SQLException;
	public abstract void collegaUtenteEvento(Utente utente, Evento evento) throws Exception;
	public abstract void collegaUtenteCategoria(Utente utente, CategoriaEvento nome_categoria) throws SQLException;
	public abstract void collegaUtenteNotifica(String nome_utente, int id_notifica) throws SQLException;	
	
//	SELECT
	public abstract HashMap<CategoriaEvento,ArrayList<Evento>> selectEventiAll() throws SQLException;	
	public abstract Evento selectEvento(int id_evento);
	public abstract ArrayList<Utente> selectUtentiAll() throws SQLException;
	public abstract Utente selectUtente(String nome);
	public abstract Notifica selectNotifica(int id_notifica) throws SQLException;
	public abstract LinkedList<Notifica> selectNotificheDiUtente(String nome_utente) throws SQLException;
	public abstract HashMap<CategoriaEvento,LinkedList<Evento>> selectEventiDiUtente(String nome_utente) throws SQLException;
	public abstract HashMap<Utente,HashMap<NomeCampo,Boolean>> selectUtentiDiEvento(Evento evento) throws SQLException;
	public abstract LinkedList<CategoriaEvento> selectCategorieDiUtente(String nome_utente) throws SQLException;
	public abstract LinkedList<Utente> selectUtentiInteressatiACategoria(CategoriaEvento nome_categoria) throws SQLException;
	public abstract HashMap<CategoriaEvento,LinkedList<Utente>> selectUtentiDaEventiPassati(String nome_utente_creatore) throws SQLException;	
	
//	UPDATE
	public abstract void updateEvento(Evento evento) throws SQLException;	
	public abstract void updateEtaMinUtente(String nome_utente, int eta_min) throws SQLException;	
	public abstract void updateEtaMaxtente(String nome_utente, int eta_max) throws SQLException;
	
//	DELETE
	public abstract void deleteEvento(Evento evento) throws SQLException;
	public abstract void deleteUtente(String nome_utente) throws SQLException;
	public abstract void deleteNotifica(int id_notifica) throws SQLException;
	public abstract void deleteCollegamentoNotificaUtente(Utente utente, Notifica notifica) throws SQLException;
	public abstract void deleteCollegamentoEventoUtente(String nome_utente, Evento evento) throws SQLException;	
	public abstract void deleteCollegamentoCategoriaUtente(String nome_utente, CategoriaEvento nome_categoria) throws SQLException;	
	public abstract void deleteEventiDiUtente(Utente utente) throws SQLException;
	
//	UTILITY
	public abstract void initializeDatiRAM() throws SQLException;	
	public abstract Utente existUtente(Utente utente) throws SQLException;	
	public abstract void refreshDatiRAM() throws SQLException;	
	public abstract HashMap<CategoriaEvento, ArrayList<Evento>> getEventi();	
	public abstract boolean existUtenteInEvento(Utente utente_corrente, Evento evento) throws SQLException;
	public abstract int getCostoEventoPerUtente(Evento evento, Utente utente) throws SQLException;
}