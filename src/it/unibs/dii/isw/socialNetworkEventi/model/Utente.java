package it.unibs.dii.isw.socialNetworkEventi.model;
import java.io.Serializable;
import java.util.LinkedList;

import it.unibs.dii.isw.socialNetworkEventi.utility.NomeCampi;

public class Utente implements Serializable 
{
	private static final long serialVersionUID = 1L;
	private int id_utente;

	private String nome;
	private String password;
	
	private LinkedList<Evento> eventi;
	private LinkedList<Notifica> notifiche;
	
	
	public Utente (String nome, String password) throws IllegalArgumentException 
	{
		if (nome==null || password == null) throw new IllegalArgumentException("Inserisca i campi necessari");
		if (password.length()<5) throw new IllegalArgumentException("Password troppo breve (minimo 5 caratteri)");
		if (nome.length()<3) throw new IllegalArgumentException("Nome utente troppo breve (minimo 3 caratteri)");
		this.nome = nome;
		this.password = password;
	}
	
	public boolean equals(Utente utente)
	{
		if(nome.equals(utente.getNome()) && password.equals(utente.getPassword()))
			return true;
		else
			return false;
		
	}
	
	 public void aggiungiNotifica(Notifica notifica) {notifiche.add(notifica);}
	 
	 public boolean rimuoviNotifica(Utente notifica)
	 {

		 if(notifiche.contains(notifica))
		 	{notifiche.remove(notifica); return true;}
		 else
			 return false;
	 }
	 
	 public LinkedList<Notifica> getNotifiche()
	 {
		 return notifiche;
	 }
	 
	String getNome() {return nome;}
	void setNome(String nome) {this.nome = nome;}
	public String getPassword() {return password;}
	void setPassword(String password) {this.password = password;}
	public int getId_utente() {return id_utente;}
	public void setId_utente(int id_utente) {this.id_utente = id_utente;}
}