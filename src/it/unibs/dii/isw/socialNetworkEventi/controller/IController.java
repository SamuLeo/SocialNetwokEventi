package it.unibs.dii.isw.socialNetworkEventi.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Observer;

import it.unibs.dii.isw.socialNetworkEventi.model.Evento;
import it.unibs.dii.isw.socialNetworkEventi.model.IMessagesFactory;
import it.unibs.dii.isw.socialNetworkEventi.model.IPersistentStorageRepository;
import it.unibs.dii.isw.socialNetworkEventi.model.Notifica;
import it.unibs.dii.isw.socialNetworkEventi.model.Utente;
import it.unibs.dii.isw.socialNetworkEventi.utility.CategoriaEvento;

public interface IController 
{
	public void aggiorna();
	public boolean accedi(Utente utente);
	public boolean controllaStatoEvento(Evento evento);
	public Evento aggiungiEvento(Evento evento);
	public boolean insertNotificaUtenteCorrente(Notifica notifica);
	public void notificaUtentePerEvento (Evento evento, Utente utente_destinatario);
	public boolean insertUtente(Utente utente);
	public boolean aggiungiInteresseUtenteCorrente(CategoriaEvento nome_categoria);
	public boolean insertNotifica(Notifica notifica, Utente utente);
	public void iscrizioneUtenteInEvento(Evento evento);
	
	public HashMap<CategoriaEvento,ArrayList<Evento>> getEventi();
	public LinkedList<Notifica> getNotificheUtente();
	public HashMap<CategoriaEvento,LinkedList<Utente>> getPossibiliUtentiInteressati(Utente utente);
	public LinkedList<Utente> getUtentiDaEventiPassati(CategoriaEvento nome_categoria);
	public  boolean utenteIscrittoInEvento(Evento evento);
	public  HashMap<CategoriaEvento,LinkedList<Evento>> getEventiUtenteCorrente();

	public  boolean updateFasciaEta(int eta_min, int eta_max);
	public  LinkedList<Notifica> eliminaNotificaUtente(Notifica notifica);
	public  void disiscrizioneUtenteEvento(Evento evento) throws RuntimeException;
	public  boolean eliminaInteresseUtenteCorrente(CategoriaEvento nome_categoria);
	public  void deleteEvento(Evento evento) throws RuntimeException;
	public boolean deleteNotificheUtenteAll();
	public boolean deleteEventiDiUtente() throws RuntimeException;

	public IPersistentStorageRepository getDb();
	public  Utente getUtente_corrente();
	public IMessagesFactory getMessagesFactory();
	
	public void iniziaOsservazione(Observer grafica);
	public void fermaOsservazione(Observer grafica);
}