package socialNetwork.content;
import java.io.Serializable;

public class Utente implements Serializable 
{
	private static final long serialVersionUID = 1L;
	private String nome;
	private String password;
	
	
	public Utente (String nome, String password)
	{
		if (nome==null || password == null || password.length()<5 || nome.length()<3) 
			throw new IllegalArgumentException();
//		for(String nomeDB : utentiNelDB.getNome()
//			if(nome.equals(nomeDB)) throw new IllegalArgumentException();
		this.nome = nome;
		this.password = password;
	}
	
	
	String getNome() {return nome;}
	void setNome(String nome) {this.nome = nome;}
	public String getPassword() {return password;}
	void setPassword(String password) {this.password = password;}
}