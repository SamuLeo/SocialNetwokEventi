package it.unibs.dii.isw.socialNetworkEventi.model;
import java.util.LinkedList;
import java.util.stream.*;

import it.unibs.dii.isw.socialNetworkEventi.utility.CategorieEvento;

public class Utente  {
	private int id_utente;
	private String nome;
	private String password;
	private int eta_min;
	private int eta_max;
	private LinkedList<CategorieEvento> categorie_di_interesse;
	
	private LinkedList<Evento> eventi;
	private LinkedList<Notifica> notifiche;

	public Utente(String nome)
	{
		if (nome==null) throw new IllegalArgumentException("Necessario inserire un nome per questo account");
		this.nome = nome;
	}
	
	public Utente(String nome, String password, int eta_min, int eta_max)
	{
		this(nome);
		this.password = password;
		if (eta_min > eta_max) throw new IllegalArgumentException("La fascia di età minima non può superare la massima");
		this.eta_min = eta_min;
		this.eta_max = eta_max;
	}	
	
	public Utente(int id, String nome, String password, int eta_min, int eta_max) 
	{
		this(nome, password, eta_min, eta_max);
		id_utente = id;
	}
	
	public boolean equals(Utente utente) {
		if(nome.equals(utente.getNome()) && password.equals(utente.getPassword()))
			return true;
		else
			return false;
	}
	
	 public void aggiungiNotifica(Notifica notifica) {notifiche.add(notifica);}
	 
	 public boolean rimuoviNotifica(Notifica notifica) {
		 if(notifiche.contains(notifica))
		 	{notifiche.remove(notifica); return true;}
		 else return false;
	 }
	 
	void aggiungiInteresse(CategorieEvento interesse) 
	{
		if (!categorie_di_interesse.contains(interesse)) categorie_di_interesse.add(interesse);
	}
	

	 
	public String getNome() {return nome;}
	public void setNome(String nome) {this.nome = nome;}
	
	public String getPassword() {return password;}
	public void setPassword(String password) {this.password = password;}
	
	public int getId_utente() {return id_utente;}
	public void setId_utente(int id_utente) {this.id_utente = id_utente;}
	
	public int getEtaMin() {return eta_min;}
	public void setEtaMin(int etaMin) {this.eta_min = etaMin;}
	public int getEtaMax() {return eta_max;}
	public void setEtaMax(int etaMax) {this.eta_max = etaMax;}
	
	public LinkedList<Evento> getEventi() {return eventi;}
	public void setEventi(LinkedList<Evento> eventi) {this.eventi = eventi;}

	public LinkedList<Notifica> getNotifiche() {return notifiche;}
	public void setNotifiche(LinkedList<Notifica> notifiche) {this.notifiche = notifiche;}
	
	public LinkedList<CategorieEvento> getCategorieInteressi() {return categorie_di_interesse;}
	public void setCategorieInteressi(LinkedList<CategorieEvento> interessi) {this.categorie_di_interesse = interessi;}
//	public void setInteressi(LinkedList<Evento>[] interessi) 
//	{
//		this.interessi = (LinkedList<Evento>) Stream.of(interessi).collect(Collectors.toList());
//	}

	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Utente \nid_utente=");
		builder.append(id_utente);
		builder.append("\n nome=");
		builder.append(nome);
		builder.append("\n password=");
		builder.append(password + "\n");
		return builder.toString();
	}
}