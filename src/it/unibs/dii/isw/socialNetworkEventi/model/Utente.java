package it.unibs.dii.isw.socialNetworkEventi.model;
import java.util.LinkedList;
import java.util.stream.*;

public class Utente  {
	private int id_utente;
	private String nome;
	private String password;
	private int etaMin, etaMax;
	private LinkedList<Class<? extends Evento>> interessi;
	
	private LinkedList<Evento> eventi;
	private LinkedList<Notifica> notifiche;
	
	
	public Utente (String nome, String password) throws IllegalArgumentException 
	{
		if (nome==null || password == null) throw new IllegalArgumentException("Inserisca i campi necessari");
		if (password.length()<5 || password.length() > 50) throw new IllegalArgumentException("La password deve avere una lunghezza compresa tra 5 e 50 caratteri");
		if (nome.length()<5 || nome.length() > 100) throw new IllegalArgumentException("Il nome utente deve avere una lunghezza compresa tra 5 e 100 caratteri");
		this.nome = nome;
		this.password = password;
	}
	
	public Utente(int id, String nome, String password) {
		this(nome, password);
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
	 
	void aggiungiInteresse(Class<? extends Evento> interesse) {
		if (!interessi.contains(interesse)) interessi.add(interesse);
	}
	
	void rimuoviInteresse(Class<? extends Evento> interesse) {
		interessi.remove(interesse);
	}
	 
	public String getNome() {return nome;}
	public void setNome(String nome) {this.nome = nome;}
	public String getPassword() {return password;}
	public void setPassword(String password) {this.password = password;}
	public int getId_utente() {return id_utente;}
	public void setId_utente(int id_utente) {this.id_utente = id_utente;}
	public int getEtaMin() {return etaMin;}
	public void setEtaMin(int etaMin) {this.etaMin = etaMin;}
	public int getEtaMax() {return etaMax;}
	public void setEtaMax(int etaMax) {this.etaMax = etaMax;}
	public LinkedList<Evento> getEventi() {return eventi;}
	public LinkedList<Notifica> getNotifiche() {return notifiche;}
	public void setEventi(LinkedList<Evento> eventi) {this.eventi = eventi;}
	public void setNotifiche(LinkedList<Notifica> notifiche) {this.notifiche = notifiche;}
	public LinkedList<Class<? extends Evento>> getInteressi() {return interessi;}
	public void setInteressi(LinkedList<Class<? extends Evento>> interessi) {this.interessi = interessi;}
	public void setInteressi(Class<? extends Evento>[] interessi) {
		this.interessi = (LinkedList<Class<? extends Evento>>) Stream.of(interessi).collect(Collectors.toList());
	}

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