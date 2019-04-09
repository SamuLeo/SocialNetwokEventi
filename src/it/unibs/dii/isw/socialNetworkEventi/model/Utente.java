package it.unibs.dii.isw.socialNetworkEventi.model;
import java.util.HashMap;
import java.util.LinkedList;

import it.unibs.dii.isw.socialNetworkEventi.utility.CategorieEvento;

public class Utente  
{
	private String nome;
	private String password;
	private int eta_min;
	private int eta_max;
	private LinkedList<CategorieEvento> categorie_di_interesse = new LinkedList<>();
	
	private HashMap<CategorieEvento,LinkedList<Evento>> eventi;
	private LinkedList<Notifica> notifiche;

	
	public Utente(String nome) {
		if (nome==null || nome.length()<4) throw new IllegalArgumentException("Necessario inserire un nome per questo account lungo almeno 4 caratteri");
		this.nome = nome;
	}
	
	public Utente(String nome, String password) {
		this(nome);
		if (nome==null || nome.length()<4) throw new IllegalArgumentException("Necessario inserire una password per questo account lunga almeno 4 caratteri");
		this.password = password;
	}
	
	public Utente(String nome, String password, int eta_min, int eta_max) {
		this(nome, password);
		if (eta_min > eta_max) throw new IllegalArgumentException("La fascia di età minima non può superare la massima");
		this.eta_min = eta_min;
		this.eta_max = eta_max;
	}
	
		
	 @Override
	public boolean equals(Object utente) 
	 {
		return this.nome.equals(((Utente)utente).getNome());
	}

	public void aggiungiNotifica(Notifica notifica) {notifiche.add(notifica);}
	 
	 public boolean rimuoviNotifica(Notifica notifica) {
		 if(notifiche.contains(notifica))
		 	{notifiche.remove(notifica); return true;}
		 else return false;
	 }
	 
	public void rimuoviInteresse(CategorieEvento interesse) {
		categorie_di_interesse.remove(interesse);
	}
	
	public void aggiungiInteresse(CategorieEvento interesse) {
		if (!categorie_di_interesse.contains(interesse)) categorie_di_interesse.add(interesse);
	}
	 
	public String getNome() {return nome;}
	public void setNome(String nome) {this.nome = nome;}
	
	public String getPassword() {return password;}
	public void setPassword(String password) {this.password = password;}
	
	public int getEtaMin() {return eta_min;}
	public void setEtaMin(int etaMin) {this.eta_min = etaMin;}
	public int getEtaMax() {return eta_max;}
	public void setEtaMax(int etaMax) {this.eta_max = etaMax;}
	
	public HashMap<CategorieEvento,LinkedList<Evento>> getEventi() {return eventi;}
	public void setEventi(HashMap<CategorieEvento,LinkedList<Evento>> eventi) {this.eventi = eventi;}

	public LinkedList<Notifica> getNotifiche() {return notifiche;}
	public void setNotifiche(LinkedList<Notifica> notifiche) {this.notifiche = notifiche;}
	
	public LinkedList<CategorieEvento> getCategorieInteressi() {return categorie_di_interesse;}
	public void setCategorieInteressi(LinkedList<CategorieEvento> interessi) {this.categorie_di_interesse = interessi;}

	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("\n nome=");
		builder.append(nome);
		builder.append("\n password=");
		builder.append(password + "\n");
		builder.append("\n fascia eta superiore=");
		builder.append(eta_max + "\n");
		builder.append("\n fascia età inferiore=");
		builder.append(eta_min + "\n");
		builder.append("\n interessi=");
		for(CategorieEvento cat : categorie_di_interesse)
			builder.append(cat.getString() + "\n");
		return builder.toString();
	}
}