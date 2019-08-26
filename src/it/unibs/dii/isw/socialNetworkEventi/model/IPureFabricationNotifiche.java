package it.unibs.dii.isw.socialNetworkEventi.model;

import java.sql.SQLException;

public interface IPureFabricationNotifiche 
{
	public void segnalaFallimentoEvento(Evento evento) throws SQLException;
	public void segnalaChiusuraEvento(Evento evento) throws SQLException;	
	public void segnalaConclusioneEvento(Evento evento) throws SQLException;
	public void segnalaRitiroEvento(Evento evento) throws SQLException;
	public void segnalaNuovoEventoAgliInteressati(Evento evento) throws SQLException;
	public void segnalaEventoPerUtente(Evento evento, Utente utente_mittente, Utente utente_destinatario) throws SQLException;
	public void setDB(IPersistentStorageRepository db);
}
