package socialNetwork;

import java.util.Calendar;

public class Sessione {

	public static void main(String[] args) {
		DB.caricaUtenti();
		Grafica.getIstance().crea();
		Grafica.getIstance().mostraLogin();
	}
	
	public static boolean creaUtente(String utente, String password) {
		try {
			return DB.inserisciUtente(new Utente(utente, password));
		} catch (IllegalArgumentException e) {return false;}
	}
	
	public static boolean accedi(String utente, String password) {
		Utente io = DB.trovaUtente(utente);
		if (io == null) {log("Utente non trovato"); return false;}
		else if (io.getPassword().equals(password)) {log ("Accesso eseguito"); return true;}
		else {log("Password errata " + password + " " + io.getPassword()); return false;}
	}
	
	static void log(String l) {
		System.out.println(String.format("%-70s %02d.%02d.%02d,%04d", l, Calendar.getInstance().get(Calendar.HOUR_OF_DAY), Calendar.getInstance().get(Calendar.MINUTE), Calendar.getInstance().get(Calendar.SECOND), Calendar.getInstance().get(Calendar.MILLISECOND)));
		}
}