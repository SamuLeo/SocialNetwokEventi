package it.unibs.dii.isw.socialNetworkEventi.view;

import it.unibs.dii.isw.socialNetworkEventi.model.Evento;
import it.unibs.dii.isw.socialNetworkEventi.model.Notifica;
import it.unibs.dii.isw.socialNetworkEventi.utility.CategoriaEvento;

public interface IView 
{
	public void mostraLogin();
	public void visualizzaBacheca();
	public void iniziaCreazioneEvento();
	public void visualizzaPannelloNotifiche();
	public void visualizzaEvento(Evento e);
	public void visualizzaProfilo();
	public void visualizzaFormInviti(Evento e, CategoriaEvento categoria);
	public void aggiornaDatiUtente(Integer etm, Integer etM, String[] elencoCategorie, boolean[] selezionata);
	public void aggiungiEvento(Evento e);
	public void eliminaEvento(Evento e);
	public void iscriviEvento(Evento e);
	public void rimuoviIscrizioneEvento(Evento e);
	public void eliminaNotifica(Notifica n);
}