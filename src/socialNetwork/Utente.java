package socialNetwork;
import java.io.Serializable;

class Utente implements Serializable {
	private static final long serialVersionUID = 1L;
	private String nome, password;
	
	String getNome() {return nome;}
	void setNome(String nome) {this.nome = nome;}
	String getPassword() {return password;}
	void setPassword(String password) {this.password = password;}
	
	Utente (String nome, String password){
		if (nome==null || password == null || password.length()<5 || nome.length()<3) throw new IllegalArgumentException();
		else {this.nome = nome; this.password = password;}
	}
}