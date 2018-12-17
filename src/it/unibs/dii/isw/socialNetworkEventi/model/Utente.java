package it.unibs.dii.isw.socialNetworkEventi.model;
import java.io.Serializable;

public class Utente implements Serializable 
{
	private static final long serialVersionUID = 1L;
	private String nome;
	private String password;
	
	
	public Utente (String nome, String password) throws IllegalArgumentException {
		if (nome==null || password == null) throw new IllegalArgumentException("Inserisca i campi necessari");
		if (password.length()<5) throw new IllegalArgumentException("Password troppo breve (minimo 5 caratteri)");
		if (nome.length()<3) throw new IllegalArgumentException("Nome utente troppo breve (minimo 3 caratteri)");
		this.nome = nome;
		this.password = password;
	}
	
	
	String getNome() {return nome;}
	void setNome(String nome) {this.nome = nome;}
	public String getPassword() {return password;}
	void setPassword(String password) {this.password = password;}
}