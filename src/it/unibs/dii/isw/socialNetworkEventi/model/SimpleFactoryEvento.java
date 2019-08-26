package it.unibs.dii.isw.socialNetworkEventi.model;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;

import it.unibs.dii.isw.socialNetworkEventi.utility.CategoriaEvento;
import it.unibs.dii.isw.socialNetworkEventi.utility.StatoEvento;

public class SimpleFactoryEvento {
	IPersistentStorageRepository db;
	
	public SimpleFactoryEvento (IPersistentStorageRepository db) {
		this.db = db;
	}
	
	public ArrayList<Evento> crea(CategoriaEvento cat, ResultSet dati) throws SQLException {
		ArrayList<Evento> risultato = new ArrayList<>();
		Evento ev;
		
		dati.beforeFirst();
		while (dati.next()) {
			//Estrapolazione dati comuni
			int id = dati.getInt(1);
			String titolo_evento = dati.getString(8);
			Calendar data_ora_termine_ultimo_iscrizione = Calendar.getInstance(); 
				data_ora_termine_ultimo_iscrizione.setTimeInMillis(dati.getTimestamp(4).getTime());
			Calendar data_ora_inizio_evento = Calendar.getInstance();
				data_ora_inizio_evento.setTimeInMillis(dati.getTimestamp(5).getTime());
			Calendar data_ora_termine_evento = Calendar.getInstance(); 
				if (dati.getTimestamp(11) != null) data_ora_termine_evento.setTimeInMillis(dati.getTimestamp(11).getTime()); 
				else data_ora_termine_evento=null;
			Calendar data_ora_termine_ritiro_iscrizione = Calendar.getInstance(); 
				if (dati.getTimestamp(12) != null) data_ora_termine_ritiro_iscrizione.setTimeInMillis(dati.getTimestamp(12).getTime()); 
				else data_ora_termine_ritiro_iscrizione=null;
			
			Utente creatore = db.selectUtente(dati.getString(2));
			String string_stato = dati.getString(14);
			
			//Estrapolazione dati specifici e creazione istanza
			if (cat == CategoriaEvento.PARTITA_CALCIO) {
				ev = new PartitaCalcio(
					id,
					creatore,
					dati.getString(3),
					data_ora_termine_ultimo_iscrizione,
					data_ora_inizio_evento,
					(Integer)dati.getInt(6),
					(Integer)dati.getInt(7), 
					titolo_evento,
					dati.getString(9),
					dati.getString(10),
					data_ora_termine_evento,
					data_ora_termine_ritiro_iscrizione,
					(Integer)dati.getInt(13),
					StatoEvento.convertiStringInStato(string_stato),
					(Integer)dati.getInt(15),
					(Integer)dati.getInt(16),
					(String)dati.getString(17));
				ev.setPartecipanti_campiOpt(db.selectUtentiDiEvento(ev));
				risultato.add(ev);
			}
			else if (cat == CategoriaEvento.SCII) {
				ev = new Scii(
					id,
					creatore,
					dati.getString(3),
					data_ora_termine_ultimo_iscrizione,
					data_ora_inizio_evento,
					(Integer)dati.getInt(6),
					(Integer)dati.getInt(7), 
					titolo_evento,
					dati.getString(9),
					dati.getString(10),
					data_ora_termine_evento,
					data_ora_termine_ritiro_iscrizione,
					(Integer)dati.getInt(13),
					StatoEvento.convertiStringInStato(string_stato),
					(Integer)dati.getInt(15),
					(Integer)dati.getInt(16),
					(Integer)dati.getInt(17));
				ev.setPartecipanti_campiOpt(db.selectUtentiDiEvento(ev));
				risultato.add(ev);
			}
		}
		
		return risultato;
	}
}