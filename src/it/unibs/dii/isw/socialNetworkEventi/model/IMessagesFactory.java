package it.unibs.dii.isw.socialNetworkEventi.model;

import java.sql.SQLException;

public interface IMessagesFactory 
{
	public abstract void segnalaFallimentoEvento(Evento evento) throws SQLException;
	public abstract void segnalaChiusuraEvento(Evento evento) throws SQLException;	
	public abstract void segnalaConclusioneEvento(Evento evento) throws SQLException;
	public abstract void segnalaRitiroEvento(Evento evento) throws SQLException;
	public abstract void segnalaNuovoEventoAgliInteressati(Evento evento) throws SQLException;
	public abstract void segnalaEventoPerUtente(Evento evento, Utente utente_mittente, Utente utente_destinatario) throws SQLException;
	public abstract void setDB(IPersistentStorageRepository db);
}
