package it.unibs.dii.isw.socialNetworkEventi.testing;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;
import org.junit.Test;

import java.util.Calendar;

import it.unibs.dii.isw.socialNetworkEventi.model.*;
import it.unibs.dii.isw.socialNetworkEventi.utility.StatoEvento;

public class AltriTest {
	
	@Test
	public void testValoriLimiteOK() {
		Calendar cal1 = Calendar.getInstance();
		Calendar cal2 = Calendar.getInstance();
		cal1.add(Calendar.MINUTE, 1);
		cal2.add(Calendar.MINUTE, 2);
		Evento eSoloOb = new PartitaCalcio(new Utente("Prova"), "Luogo", cal1, cal2, 2, 0, null, null, null, null, null, null, 10, 11, "qualsiasi");
		assertThat(eSoloOb.getStato(),is(equalTo(StatoEvento.VALIDA)));
	}
	
	@Test (expected = IllegalArgumentException.class)
	public void testValoriLimite_NO_UTENTE() {
		Calendar cal1 = Calendar.getInstance();
		Calendar cal2 = Calendar.getInstance();
		cal1.add(Calendar.MINUTE, 1);
		cal2.add(Calendar.MINUTE, 2);
		new PartitaCalcio(null, "Luogo", cal1, cal2, 2, 0, null, null, null, null, null, null, 10, 11, "qualsiasi");
	}
	
	@Test (expected = IllegalArgumentException.class)
	public void testValoriLimite_NO_LUOGO() {
		Calendar cal1 = Calendar.getInstance();
		Calendar cal2 = Calendar.getInstance();
		cal1.add(Calendar.MINUTE, 1);
		cal2.add(Calendar.MINUTE, 2);
		new PartitaCalcio(new Utente("Prova"), null, cal1, cal2, 2, 0, null, null, null, null, null, null, 10, 11, "qualsiasi");
	}
	
	@Test (expected = IllegalArgumentException.class)
	public void testValoriLimite_NO_DATE() {
		Calendar cal1 = Calendar.getInstance();
		Calendar cal2 = Calendar.getInstance();
		cal1.add(Calendar.MINUTE, 1);
		cal2.add(Calendar.MINUTE, 2);
		new PartitaCalcio(new Utente("Prova"), "Luogo", null, cal2, 2, 0, null, null, null, null, null, null, 10, 11, "qualsiasi");
	}
	
	@Test (expected = IllegalArgumentException.class)
	public void testValoriLimite_DATE_UGUALI() {
		Calendar cal2 = Calendar.getInstance();
		cal2.add(Calendar.MINUTE, 2);
		new PartitaCalcio(new Utente("Prova"), "Luogo", cal2, cal2, 2, 0, null, null, null, null, null, null, 10, 11, "qualsiasi");
	}
	
	@Test (expected = IllegalArgumentException.class)
	public void testValoriLimite_DATE_INVERTITE() {
		Calendar cal1 = Calendar.getInstance();
		Calendar cal2 = Calendar.getInstance();
		cal1.add(Calendar.MINUTE, 1);
		cal2.add(Calendar.MINUTE, 2);
		new PartitaCalcio(new Utente("Prova"), "Luogo", cal2, cal1, 2, 0, null, null, null, null, null, null, 10, 11, "qualsiasi");
	}

	@Test (expected = IllegalArgumentException.class)
	public void testValoriLimite_UN_PARTECIPANTE() {
		Calendar cal1 = Calendar.getInstance();
		Calendar cal2 = Calendar.getInstance();
		cal1.add(Calendar.MINUTE, 1);
		cal2.add(Calendar.MINUTE, 2);
		new PartitaCalcio(new Utente("Prova"), "Luogo", cal1, cal2, 1, 0, null, null, null, null, null, null, 10, 11, "qualsiasi");
	}
	

	@Test (expected = IllegalArgumentException.class)
	public void testValoriLimite_COSTO_NEGATIVO() {
		Calendar cal1 = Calendar.getInstance();
		Calendar cal2 = Calendar.getInstance();
		cal1.add(Calendar.MINUTE, 1);
		cal2.add(Calendar.MINUTE, 2);
		new PartitaCalcio(new Utente("Prova"), "Luogo", cal1, cal2, 2, -1, null, null, null, null, null, null, 10, 11, "qualsiasi");
	}
	
	@Test (expected = IllegalArgumentException.class)
	public void testValoriLimite_NUM_PARTECIPANTI_NEGATIVO() {
		Calendar cal1 = Calendar.getInstance();
		Calendar cal2 = Calendar.getInstance();
		cal1.add(Calendar.MINUTE, 1);
		cal2.add(Calendar.MINUTE, 2);
		new PartitaCalcio(new Utente("Prova"), "Luogo", cal1, cal2, -2, 0, null, null, null, null, null, null, 10, 11, "qualsiasi");
	}

	@Test (expected = IllegalArgumentException.class)
	public void testValoriLimite_ETA_NEGATIVA() {
		Calendar cal1 = Calendar.getInstance();
		Calendar cal2 = Calendar.getInstance();
		cal1.add(Calendar.MINUTE, 1);
		cal2.add(Calendar.MINUTE, 2);
		new PartitaCalcio(new Utente("Prova"), "Luogo", cal1, cal2, 2, 0, null, null, null, null, null, null, -10, 11, "qualsiasi");
	}

	@Test (expected = IllegalArgumentException.class)
	public void testValoriLimite_ETA_ZERO() {
		Calendar cal1 = Calendar.getInstance();
		Calendar cal2 = Calendar.getInstance();
		cal1.add(Calendar.MINUTE, 1);
		cal2.add(Calendar.MINUTE, 2);
		new PartitaCalcio(new Utente("Prova"), "Luogo", cal1, cal2, 2, 0, null, null, null, null, null, null, 0, 11, "qualsiasi");
	}
	
	@Test (expected = IllegalArgumentException.class)
	public void testValoriLimite_ETA_INVERTITE() {
		Calendar cal1 = Calendar.getInstance();
		Calendar cal2 = Calendar.getInstance();
		cal1.add(Calendar.MINUTE, 1);
		cal2.add(Calendar.MINUTE, 2);
		new PartitaCalcio(new Utente("Prova"), "Luogo", cal1, cal2, 2, 0, null, null, null, null, null, null, 12, 11, "qualsiasi");
	}
	
	@Test (expected = IllegalArgumentException.class)
	public void testValoriLimite_ETA_INSENSATE() {
		Calendar cal1 = Calendar.getInstance();
		Calendar cal2 = Calendar.getInstance();
		cal1.add(Calendar.MINUTE, 1);
		cal2.add(Calendar.MINUTE, 2);
		new PartitaCalcio(new Utente("Prova"), "Luogo", cal1, cal2, 2, 0, null, null, null, null, null, null, 12, 11009, "qualsiasi");
	}

	@Test (expected = IllegalArgumentException.class)
	public void testValoriLimite_NO_SESSO() {
		Calendar cal1 = Calendar.getInstance();
		Calendar cal2 = Calendar.getInstance();
		cal1.add(Calendar.MINUTE, 1);
		cal2.add(Calendar.MINUTE, 2);
		new PartitaCalcio(new Utente("Prova"), "Luogo", cal1, cal2, 2, 0, null, null, null, null, null, null, 12, 11009, null);
	}
	

	@Test (expected = IllegalArgumentException.class)
	public void testValoriLimite_SESSO_IMPROPRIO() {
		Calendar cal1 = Calendar.getInstance();
		Calendar cal2 = Calendar.getInstance();
		cal1.add(Calendar.MINUTE, 1);
		cal2.add(Calendar.MINUTE, 2);
		new PartitaCalcio(new Utente("Prova"), "Luogo", cal1, cal2, 2, 0, null, null, null, null, null, null, 12, 12, "");
	}
	
	@Test
	public void testValoriLimiteTuttiCampiOK() {
		Calendar tiscr = Calendar.getInstance(), inizio = Calendar.getInstance(),fine = Calendar.getInstance(), tritiro = Calendar.getInstance();
		tritiro.add(Calendar.MINUTE, 1);
		tiscr.add(Calendar.MINUTE, 2);
		inizio.add(Calendar.MINUTE, 3);
		fine.add(Calendar.MINUTE, 4);
		new PartitaCalcio(new Utente("Prova"), "Luogo", tiscr, inizio, 2, 0, "Testolandia", "Note", "Incluso", fine, tritiro, 1, 12, 12, "qualsiasi");
	}
	
	@Test (expected = IllegalArgumentException.class)
	public void testValoriLimiteTuttiCampi_TOLLERANZA_NEGATIVA() {
		Calendar tiscr = Calendar.getInstance(), inizio = Calendar.getInstance(),fine = Calendar.getInstance(), tritiro = Calendar.getInstance();
		tritiro.add(Calendar.MINUTE, 1);
		tiscr.add(Calendar.MINUTE, 2);
		inizio.add(Calendar.MINUTE, 3);
		fine.add(Calendar.MINUTE, 4);
		new PartitaCalcio(new Utente("Prova"), "Luogo", tiscr, inizio, 2, 0, "Testolandia", "Note", "Incluso", fine, tritiro, -1, 12, 12, "qualsiasi");
	}

	@Test (expected = IllegalArgumentException.class)
	public void testValoriLimiteTuttiCampi_DATE_SCOORDINATE1() {
		Calendar tiscr = Calendar.getInstance(), inizio = Calendar.getInstance(),fine = Calendar.getInstance(), tritiro = Calendar.getInstance();
		tritiro.add(Calendar.MINUTE, 1);
		tiscr.add(Calendar.MINUTE, 2);
		inizio.add(Calendar.MINUTE, 3);
		fine.add(Calendar.MINUTE, 4);
		new PartitaCalcio(new Utente("Prova"), "Luogo", tiscr, inizio, 2, 0, "Testolandia", "Note", "Incluso", tritiro, fine, 1, 12, 12, "qualsiasi");
	}

	@Test (expected = IllegalArgumentException.class)
	public void testValoriLimiteTuttiCampi_DATE_SCOORDINATE2() {
		Calendar tiscr = Calendar.getInstance(), inizio = Calendar.getInstance(),fine = Calendar.getInstance(), tritiro = Calendar.getInstance();
		tritiro.add(Calendar.MINUTE, 1);
		tiscr.add(Calendar.MINUTE, 2);
		inizio.add(Calendar.MINUTE, 3);
		fine.add(Calendar.MINUTE, 4);
		new PartitaCalcio(new Utente("Prova"), "Luogo", tiscr, tritiro, 2, 0, "Testolandia", "Note", "Incluso", inizio, fine, 1, 12, 12, "qualsiasi");
	}
	
	@Test (expected = IllegalArgumentException.class)
	public void testValoriLimiteTuttiCampi_DATE_SCOORDINATE3() {
		Calendar tiscr = Calendar.getInstance(), inizio = Calendar.getInstance(),fine = Calendar.getInstance(), tritiro = Calendar.getInstance();
		tritiro.add(Calendar.MINUTE, 1);
		tiscr.add(Calendar.MINUTE, 2);
		inizio.add(Calendar.MINUTE, 3);
		fine.add(Calendar.MINUTE, 4);
		new PartitaCalcio(new Utente("Prova"), "Luogo", tiscr, inizio, 2, 0, "Testolandia", "Note", "Incluso", inizio, fine, 1, 12, 12, "qualsiasi");
	}
	
	private PartitaCalcio tuttiCampiOK() {
		Calendar tiscr = Calendar.getInstance(), inizio = Calendar.getInstance(),fine = Calendar.getInstance(), tritiro = Calendar.getInstance();
		tritiro.add(Calendar.SECOND, 1);
		tiscr.add(Calendar.SECOND, 2);
		inizio.add(Calendar.SECOND, 3);
		fine.add(Calendar.SECOND, 4);
		return new PartitaCalcio(new Utente("Prova"), "Luogo", tiscr, inizio, 2, 0, "Testolandia", "Note", "Incluso", fine, tritiro, 1, 12, 12, "qualsiasi");
	}
	
	@SuppressWarnings("static-access")
	@Test
	public void testEvoluzione() throws InterruptedException {
		PartitaCalcio p = tuttiCampiOK(), p2 = tuttiCampiOK(), p3 = tuttiCampiOK();
		assertEquals(p3.getNumeroPartecipanti(),1);
		assertThat(p.getStato(),is(equalTo(StatoEvento.VALIDA)));
		p.setStato(StatoEvento.APERTA); p2.setStato(StatoEvento.APERTA); p3.setStato(StatoEvento.APERTA);
		assertThat(p.getStato(),is(equalTo(StatoEvento.APERTA)));
		//Tutti gli eventi sono stati creati (uguali) e aperti. Iscritti minimi=2, massimi=3
		assertFalse(p.controllaStatoEvento());
		//Verificato che sono stabili, aperti
		Utente X = new Utente("abcd");
		p2.aggiungiFruitore(new Utente("abcd"));
		p3.aggiungiFruitore(X); p3.aggiungiFruitore(new Utente("bcdef"));
		//P2 ha raggiunto gli iscritti minimi, P1 è vuoto, P3 i massimi.
		assertFalse(p3.aggiungiFruitore(new Utente("cdefg")));
		assertEquals(p3.getNumeroPartecipanti(),3);
		//Controllato che P3, essendo pieno, non accetta altri iscritti
		Thread.currentThread().sleep(1000);
		p3.rimuoviFruitore(X);
		assertFalse(p3.aggiungiFruitore(X));
		//Dopo un secondo il termine di ritiro iscrizione è scaduto dunque non ci si dovrebbe più poter disiscrivere
		assertFalse(p.controllaStatoEvento());
		assertFalse(p2.controllaStatoEvento());
		assertTrue(p3.controllaStatoEvento());
		assertThat(p3.getStato(),is(equalTo(StatoEvento.CHIUSA)));
		assertFalse(p3.controllaStatoEvento());
		Thread.currentThread().sleep(1000);
		//Dopo 2 secondi non ci si può più iscrivere: gli eventi cambiano stato
		assertTrue(p.controllaStatoEvento());
		assertTrue(p2.controllaStatoEvento());
		assertFalse(p.controllaStatoEvento());
		assertFalse(p2.controllaStatoEvento());
		assertFalse(p3.controllaStatoEvento());
		assertThat(p.getStato(),is(equalTo(StatoEvento.FALLITA)));
		assertThat(p2.getStato(),is(equalTo(StatoEvento.CHIUSA)));
		Thread.currentThread().sleep(2000);
		assertFalse(p.controllaStatoEvento());
		assertTrue(p2.controllaStatoEvento());
		assertTrue(p3.controllaStatoEvento());
		assertThat(p2.getStato(),is(equalTo(StatoEvento.CONCLUSA)));
		assertThat(p3.getStato(),is(equalTo(StatoEvento.CONCLUSA)));
		assertFalse(p2.controllaStatoEvento());
		assertFalse(p3.controllaStatoEvento());
	}
}