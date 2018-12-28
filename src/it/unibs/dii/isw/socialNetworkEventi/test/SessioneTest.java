package it.unibs.dii.isw.socialNetworkEventi.test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Calendar;

import org.junit.jupiter.api.Test;

import it.unibs.dii.isw.socialNetworkEventi.controller.Sessione;
import it.unibs.dii.isw.socialNetworkEventi.model.*;


class SessioneTest {

	@Test
	void testCreaUtente() 
	{
		Utente utente= new Utente("Samuele", "pwsicura123");
		Sessione.main(null);
		Sessione.insertUtente(utente);
		assertEquals(utente, Sessione.getUtente_corrente());
	}
	
	@Test
	void testAggiungiPartitaCalcioAlDB() 
	{
		Sessione.main(null);
		Calendar data_termine = Calendar.getInstance(); data_termine.set(2018, 12, 25, 24, 00);
		Calendar data_inizio = Calendar.getInstance(); data_inizio.set(2018, 12, 28, 15, 00);		
		Calendar data_fine = Calendar.getInstance(); data_fine.set(2018, 12, 28, 16, 00);

		PartitaCalcio partita_calcio = new PartitaCalcio(
				Sessione.getUtente_corrente(),
				"Mompiano",
				data_termine,
				data_inizio,
				10,
				5,
				null,
				null,
				null,
				data_fine,
				18,
				25,
				"maschi"
						);
		Sessione.aggiungiEventoAlDB(partita_calcio);
		assertEquals(partita_calcio, Sessione.selectPartita(partita_calcio.getId()));
	}

	@Test
	void testRimuoviPartitaCalcio() 
	{
		Sessione.main(null);
		Calendar data_termine = Calendar.getInstance(); data_termine.set(2018, 12, 25, 24, 00);
		Calendar data_inizio = Calendar.getInstance(); data_inizio.set(2018, 12, 28, 15, 00);		
		Calendar data_fine = Calendar.getInstance(); data_fine.set(2018, 12, 28, 16, 00);

		PartitaCalcio partita_calcio = new PartitaCalcio(
				Sessione.getUtente_corrente(),
				"Mompiano",
				data_termine,
				data_inizio,
				10,
				5,
				null,
				null,
				null,
				data_fine,
				18,
				25,
				"maschi"
						);
		Sessione.aggiungiEventoAlDB(partita_calcio);
		Sessione.deleteEvento(partita_calcio);
		assertEquals(Sessione.selectPartita(partita_calcio.getId()), null);
	}

	@Test
	void testMostraBacheca() 
	{
		Sessione.main(null);
		Calendar data_termine = Calendar.getInstance(); data_termine.set(2018, 12, 25, 24, 00);
		Calendar data_inizio = Calendar.getInstance(); data_inizio.set(2018, 12, 28, 15, 00);		
		Calendar data_fine = Calendar.getInstance(); data_fine.set(2018, 12, 28, 16, 00);

		PartitaCalcio partita_calcio1 = new PartitaCalcio(
				Sessione.getUtente_corrente(),
				"Mompiano",
				data_termine,
				data_inizio,
				10,
				5,
				null,
				null,
				null,
				data_fine,
				18,
				25,
				"maschi"
						);
		
		PartitaCalcio partita_calcio2 = new PartitaCalcio(
				Sessione.getUtente_corrente(),
				"San Polo",
				data_termine,
				data_inizio,
				10,
				5,
				null,
				null,
				null,
				data_fine,
				18,
				25,
				"femmine"
						);
		Sessione.aggiungiEventoAlDB(partita_calcio1);
		Sessione.aggiungiEventoAlDB(partita_calcio2);
		ArrayList<PartitaCalcio> partite = new ArrayList<>();
		partite.add(partita_calcio1);
		partite.add(partita_calcio2);
//		N.B. dovrebbe dare errore perchè le partite non hanno l'id prima di essere salvate su DB
		assertEquals(Sessione.selectEventi(), partite);
	}

	@Test
	void testCaricaPartita() {
		fail("Not yet implemented");
		Sessione.main(null);
	}

	@Test
	void testAccedi() {
		fail("Not yet implemented");
		Sessione.main(null);
	}

	@Test
	void testGetNotificheUtente() {
		fail("Not yet implemented");
		Sessione.main(null);
	}

	@Test
	void testEliminaNotificaUtente() {
		fail("Not yet implemented");
		Sessione.main(null);
	}

	@Test
	void testIscrizionePartita() {
		fail("Not yet implemented");
		Sessione.main(null);
	}

	@Test
	void testUtenteIscrittoAllaPartita() {
		fail("Not yet implemented");
		Sessione.main(null);
	}

}
