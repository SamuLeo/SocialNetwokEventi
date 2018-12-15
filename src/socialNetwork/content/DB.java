package socialNetwork.content;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;

import socialNetwork.controller.Sessione;

public final class DB {
	static HashMap<String, Utente> utenti = new HashMap<>();
	
	public static Utente trovaUtente(String nome) {
		return utenti.get(nome);
	}
	
	public static boolean inserisciUtente(Utente utente) {
		if (utenti.containsKey(utente.getNome())) {
			Sessione.log("Utente già presente");
			return false;
		} else {
			utenti.put(utente.getNome(), utente);
			salvaUtenti();
			Sessione.log("Aggiunto un utente di nome " + utente.getNome());
			return true;
		}
	}
	
	static boolean rimuoviUtente(String nome) {
		if (utenti.remove(nome)!=null) 
			{salvaUtenti(); return true;}
		else return false;
	}
	
	@SuppressWarnings("unchecked")
	public
	static void caricaUtenti() {
		try (ObjectInputStream ingresso = new ObjectInputStream(new FileInputStream(new File("DB\\UtDB.dat")));) {
			utenti = (HashMap<String, Utente>) ingresso.readObject();}
		catch (Exception exc)	{exc.printStackTrace();}
	}
	
	static void salvaUtenti() {
		try (ObjectOutputStream Ch = new ObjectOutputStream(new FileOutputStream(new File("DB\\UtDB.dat")))) {
			new File("DB\\UtDB.dat").delete();
			Ch.writeObject(utenti);
			Ch.flush(); Ch.close();
		} catch (IOException e) {e.printStackTrace();}
	}
}