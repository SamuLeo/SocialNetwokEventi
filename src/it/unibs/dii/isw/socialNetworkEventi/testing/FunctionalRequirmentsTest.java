package it.unibs.dii.isw.socialNetworkEventi.testing;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Random;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.jupiter.api.Test;
import org.junit.rules.ExpectedException;

import it.unibs.dii.isw.socialNetworkEventi.controller.Sessione;
import it.unibs.dii.isw.socialNetworkEventi.model.*;
import it.unibs.dii.isw.socialNetworkEventi.utility.CategoriaEvento;
import it.unibs.dii.isw.socialNetworkEventi.utility.Messaggi;
import it.unibs.dii.isw.socialNetworkEventi.utility.NomeCampo;
import it.unibs.dii.isw.socialNetworkEventi.utility.StatoEvento;

class FunctionalRequirmentsTest 
{	
	Sessione sessione =  Sessione.getInstance();
	Utente admin = sessione.getDb().selectUtente("admin");
	
	@Test
	void accessoConInserimentoPartitaDiCalcioNelDB()
	{
		sessione.accedi(admin);
		
		Calendar termineIscrizione = Calendar.getInstance();
		termineIscrizione.set(2030, 2, 15, 15, 00,0);
		Calendar dataInizioEvento = Calendar.getInstance();
		dataInizioEvento.set(2030, 3, 1, 15, 00,0);
	
		Evento evento = new PartitaCalcio(
				admin,
				"Mompiano",
				termineIscrizione,
				dataInizioEvento,
				10,
				5,
				"Parta",
				null,
				null,
				null,				
				null,				
				2,
				18,
				25,
				"maschi"
				);
		evento = sessione.aggiungiEvento(evento);

		boolean presenteNelDB = false;
		for(Evento event: sessione.getEventi().get(CategoriaEvento.PARTITA_CALCIO))
		{
			if(event.equals(evento))
				{
					presenteNelDB  = true;
					break;
				}			
		}		
		assertTrue(presenteNelDB);
	}
	
	@Test
	void accessoConTentativoInserimentoPartitaCalcioNonValida()
	{
		sessione.accedi(admin);		
		Calendar dataInizioEvento = Calendar.getInstance();
		dataInizioEvento.set(2030, 3, 1, 15, 00,0);

		try
		{
			new PartitaCalcio(
				admin,
				"Mompiano",
				null,
				dataInizioEvento,
				10,
				5,
				"Parta",
				null,
				null,
				null,				
				null,				
				2,
				18,
				25,
				"maschi"
				);
		}
		catch(IllegalArgumentException e)
		{		
			assertEquals("Necessario inserire una data di chiusura delle iscrizioni", e.getMessage());
		}
	}
	
	@Test
	void adesionePartitaDiCalcio()
	{
		Utente utente = new Utente("admin","");
		sessione.accedi(utente);
		
		Calendar termineIscrizione = Calendar.getInstance();
		termineIscrizione.set(2030, 2, 15, 15, 00,0);
		Calendar dataInizioEvento = Calendar.getInstance();
		dataInizioEvento.set(2030, 3, 1, 15, 00,0);
	
		Evento evento = new PartitaCalcio(
				utente,
				"Mompiano",
				termineIscrizione,
				dataInizioEvento,
				10,
				5,
				"Parta",
				null,
				null,
				null,				
				null,				
				2,
				18,
				25,
				"maschi"
				);
		evento = sessione.aggiungiEvento(evento);

		utente = new Utente("tester","");
		sessione.accedi(utente);		
		sessione.iscrizioneUtenteInEvento(evento);

		assertTrue(sessione.utenteIscrittoInEvento(evento));
	}
	
	@Test
	void doppiaAdesionePartitaDiCalcio()
	{
		Utente utente = new Utente("admin","");
		sessione.accedi(utente);
		
		Calendar termineIscrizione = Calendar.getInstance();
		termineIscrizione.set(2030, 2, 15, 15, 00,0);
		Calendar dataInizioEvento = Calendar.getInstance();
		dataInizioEvento.set(2030, 3, 1, 15, 00,0);
	
		Evento evento = new PartitaCalcio(
				utente,
				"Mompiano",
				termineIscrizione,
				dataInizioEvento,
				10,
				5,
				"Parta",
				null,
				null,
				null,				
				null,				
				2,
				18,
				25,
				"maschi"
				);
		evento = sessione.aggiungiEvento(evento);

		utente = new Utente("tester","");
		sessione.accedi(utente);		
		sessione.iscrizioneUtenteInEvento(evento);
		sessione.iscrizioneUtenteInEvento(evento);

		evento = sessione.getDb().selectEvento(evento.getId());
		int count = evento.getNumeroPartecipanti();
		assertTrue(count == 2);
	}
	
	@Test
	void disiscrizioneEntroDataDiRitiro()
	{
		sessione.accedi(admin);
		
		Calendar termineIscrizione = Calendar.getInstance();
		termineIscrizione.set(2030, 2, 15, 15, 00,0);
		Calendar dataInizioEvento = Calendar.getInstance();
		dataInizioEvento.set(2030, 3, 1, 15, 00,0);
		Calendar dataRitiro = Calendar.getInstance();
		dataRitiro.set(2030, 2, 10, 15, 00,0);
	
		Evento evento = new PartitaCalcio(
				admin,
				"Mompiano",
				termineIscrizione,
				dataInizioEvento,
				2,
				5,
				"Parta",
				null,
				null,
				null,				
				dataRitiro,				
				1,
				18,
				25,
				"maschi"
				);
		evento = sessione.aggiungiEvento(evento);
		
		admin = new Utente("tester","");
		sessione.accedi(admin);		
		sessione.iscrizioneUtenteInEvento(evento);
		sessione.disiscrizioneUtenteEvento(evento);

		evento = sessione.getDb().selectEvento(evento.getId());
		int count = evento.getNumeroPartecipanti();
		assertTrue(count == 1);
	}
	
	@Test
	void confermaEventoInCasoDiAdesioniSufficientiRaggiunteAlTermineIscrizioni()
	{
		sessione.accedi(admin);
		
		Calendar termineIscrizione = Calendar.getInstance();
		termineIscrizione.add(Calendar.SECOND, 1);
		Calendar dataInizioEvento = Calendar.getInstance();
		dataInizioEvento.set(2030, 3, 1, 15, 00,0);
	
		Evento evento = new PartitaCalcio(
				admin,
				"Mompiano",
				termineIscrizione,
				dataInizioEvento,
				2,
				5,
				"Parta",
				null,
				null,
				null,				
				null,				
				0,
				18,
				25,
				"maschi"
				);
		evento = sessione.aggiungiEvento(evento);
		
		admin = new Utente("tester","");
		sessione.accedi(admin);		
		sessione.iscrizioneUtenteInEvento(evento);
		
		try {
			Thread.sleep(2000,0);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		sessione.aggiornatore.run();
	
		String titolo = String.format(Messaggi.TITOLO_CHIUSURA_EVENTO, evento.getContenutoCampo(NomeCampo.TITOLO));
		java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd-MM-yyyy HH:mm");
		String messaggio = String.format(Messaggi.NOTIFICA_CHIUSURA_EVENTO, evento.getContenutoCampo(NomeCampo.TITOLO), 
				sdf.format(((Calendar)evento.getCampo(NomeCampo.D_O_INIZIO_EVENTO).getContenuto()).getTime()),evento.getContenutoCampo(NomeCampo.COSTO));
		Notifica notifica = new Notifica(titolo, messaggio);
		boolean presente = false;
		for(Notifica n : sessione.getUtente_corrente().getNotifiche())
		{
			if(n.equals(notifica))
				{
					presente = true;
					break;
				}
		}
		assertTrue(presente);
	}
	
	@Test
	void confermaEventoInCasoDiAdesioniMassimeRaggiunteDopoTermineRitiro()
	{
		sessione.accedi(admin);
		
		Calendar termineIscrizione = Calendar.getInstance();
		termineIscrizione.set(2030, 2, 1, 15, 00,0);
		Calendar dataInizioEvento = Calendar.getInstance();
		dataInizioEvento.set(2030, 3, 1, 15, 00,0);
		Calendar dataRitiroIscrizioni = Calendar.getInstance();
	
		Evento evento = new PartitaCalcio(
				admin,
				"Mompiano",
				termineIscrizione,
				dataInizioEvento,
				2,
				5,
				"Parta",
				null,
				null,
				null,				
				dataRitiroIscrizioni,				
				0,
				18,
				25,
				"maschi"
				);
		evento = sessione.aggiungiEvento(evento);
		
		Utente tester = new Utente("tester","");
		sessione.accedi(tester);		
		sessione.iscrizioneUtenteInEvento(evento);
		
//		try {
//			Thread.sleep(2000,0);
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
		sessione.aggiornatore.run();
	
		String titolo = String.format(Messaggi.TITOLO_CHIUSURA_EVENTO, evento.getContenutoCampo(NomeCampo.TITOLO));
		java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd-MM-yyyy HH:mm");
		String messaggio = String.format(Messaggi.NOTIFICA_CHIUSURA_EVENTO, 
				evento.getContenutoCampo(NomeCampo.TITOLO), 
				sdf.format(((Calendar)evento.getCampo(NomeCampo.D_O_INIZIO_EVENTO).getContenuto()).getTime()),
				evento.getContenutoCampo(NomeCampo.COSTO));
		Notifica notifica = new Notifica(titolo, messaggio);
		boolean presente = false;
		for(Notifica n : sessione.getUtente_corrente().getNotifiche())
		{
			if(n.equals(notifica))
				{
					presente = true;
					break;
				}
		}
		assertTrue(presente);
	}
	
	@Test
	void ritiroEventoPrimaDellaDataDiRitiro()
	{
		Utente utente = new Utente("admin","");
		sessione.accedi(utente);
		
		Calendar termineIscrizione = Calendar.getInstance();
		termineIscrizione.set(2030, 2, 1, 15, 00,0);
		Calendar dataInizioEvento = Calendar.getInstance();
		dataInizioEvento.set(2030, 3, 1, 15, 00,0);
		Calendar dataRitiroIscrizioni = Calendar.getInstance();
		dataRitiroIscrizioni.set(2030, 2, 1, 10, 00,0);
	
		Evento evento = new PartitaCalcio(
				utente,
				"Mompiano",
				termineIscrizione,
				dataInizioEvento,
				2,
				5,
				"Parta",
				null,
				null,
				null,				
				dataRitiroIscrizioni,				
				0,
				18,
				25,
				"maschi"
				);
		evento = sessione.aggiungiEvento(evento);
		
		sessione.deleteEvento(evento);
		
		assertTrue(sessione.getDb().selectEvento(evento.getId()).getStato().equals(StatoEvento.RITIRATA));
	}
	
	@Test
	void fallimentoInCasoDiMancatoRaggiungimentoNumeroIscrizioni()
	{
		Utente utente = new Utente("admin","");
		sessione.accedi(utente);
		
		Calendar termineIscrizione = Calendar.getInstance();
		Calendar dataInizioEvento = Calendar.getInstance();
		dataInizioEvento.set(2030, 3, 1, 15, 00,0);
	
		Evento evento = new PartitaCalcio(
				utente,
				"Mompiano",
				termineIscrizione,
				dataInizioEvento,
				2,
				5,
				"Parta",
				null,
				null,
				null,				
				null,				
				0,
				18,
				25,
				"maschi"
				);
		evento = sessione.aggiungiEvento(evento);
		
		try {
			Thread.sleep(500,0);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		sessione.aggiornatore.run();
		
		assertTrue(sessione.getDb().selectEvento(evento.getId()).getStato().equals(StatoEvento.FALLITA));			
	}
	
	@Test
	void passaggioChiusoConcluso() 
	{
		Utente utente = new Utente("admin","");
		sessione.accedi(utente);
		
		Calendar termineIscrizione = Calendar.getInstance();
		termineIscrizione.add(Calendar.SECOND,1);
		Calendar dataInizioEvento = Calendar.getInstance();
		dataInizioEvento.add(Calendar.SECOND,2);
		Calendar dataTermineEvento = Calendar.getInstance();
		dataTermineEvento.add(Calendar.SECOND,2);

	
		Evento evento = new PartitaCalcio(
				utente,
				"Mompiano",
				termineIscrizione,
				dataInizioEvento,
				2,
				5,
				"Parta",
				null,
				null,
				dataTermineEvento,				
				null,				
				0,
				18,
				25,
				"maschi"
				);
		evento = sessione.aggiungiEvento(evento);
		
		utente = new Utente("tester","");
		sessione.accedi(utente);		
		sessione.iscrizioneUtenteInEvento(evento);
		
		try {
			Thread.sleep(2500, 0);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
//		serve aggiornare 2 volte perchè l'evento passa da APERTO a CHIUSO a CONCLUSO
//		(l'aggiornatore lo farebbe in automatico ma per ragioni di tempo viene forzato in modo da testare la logica)
		sessione.aggiornatore.run();
		sessione.aggiornatore.run();
				
		assertTrue(sessione.getDb().selectEvento(evento.getId()).getStato().equals(StatoEvento.CONCLUSA));	
	}
	
	@Test
	void modificaCampiFacoltativiUtente()
	{
		Utente utente = new Utente("admin","");
		sessione.accedi(utente);
		int eta_min = Math.abs(new Random().nextInt()%30);
		int eta_max = eta_min+1;
		sessione.updateFasciaEta(eta_min, eta_max);
		assertTrue(sessione.getUtente_corrente().getEtaMin() == eta_min && 
				sessione.getUtente_corrente().getEtaMax() == eta_max);
	}
	
	@Test
	void notificaPerNuovoEventoNellaCategoriaDiInteresse()
	{
		Utente utente = new Utente("admin","");
		sessione.accedi(utente);		
		sessione.eliminaInteresseUtenteCorrente(CategoriaEvento.PARTITA_CALCIO);
		boolean check = !sessione.getUtente_corrente().getCategorieInteressi().contains(CategoriaEvento.PARTITA_CALCIO);
		sessione.aggiungiInteresseUtenteCorrente(CategoriaEvento.PARTITA_CALCIO);
		check = check && sessione.getUtente_corrente().getCategorieInteressi().contains(CategoriaEvento.PARTITA_CALCIO);
		
		assertTrue(check);
	}
	
//	Bisogna selezionare l'opzione da grafica
//	@Test
//	void invitoAgliAderentiPassatiDiUnProprioEvento()
//	{
//		Utente utente_mittente = new Utente("admin","");
//		sessione.accedi(utente_mittente);
//		
//		Calendar termineIscrizione = Calendar.getInstance();
//		termineIscrizione.set(2030, 2, 1, 15, 00,0);
//		Calendar dataInizioEvento = Calendar.getInstance();
//		dataInizioEvento.set(2030, 3, 1, 15, 00,0);
//		Calendar dataRitiroIscrizioni = Calendar.getInstance();
//		dataRitiroIscrizioni.add(Calendar.SECOND, 2);
//	
//		Evento evento = new PartitaCalcio(
//				utente,
//				"Mompiano",
//				termineIscrizione,
//				dataInizioEvento,
//				2,
//				5,
//				"Parta",
//				null,
//				null,
//				null,				
//				dataRitiroIscrizioni,				
//				0,
//				18,
//				25,
//				"maschi"
//				);
//		evento = sessione.aggiungiEvento(evento);
//		
//		Utente utente_destinatario = new Utente("tester","");
//		sessione.accedi(utente_destinatario);		
//		sessione.iscrizioneUtenteInEvento(evento);
//		
//		evento = sessione.aggiungiEvento(evento);
//		
//		String titolo = String.format(Messaggi.TITOLO_INVITO_EVENTO, CategoriaEvento.PARTITA_CALCIO);
//		String messaggio = String.format(Messaggi.NOTIFICA_PER_INVITO_UTENTE, utente_mittente.getNome(), evento.getCampo(NomeCampi.TITOLO).getContenuto());	
//
//		Notifica notifica = new Notifica(titolo, messaggio);
//		boolean presente = false;
//		for(Notifica n : sessione.getUtente_corrente().getNotifiche())
//		{
//			if(n.equals(notifica))
//				{
//					presente = true;
//					break;
//				}
//		}
//		assertTrue(presente);
//	}
	
	@Test
	void iscrizioneSciiConScelte()
	{
		sessione.accedi(admin);
		
		Calendar termineIscrizione = Calendar.getInstance();
		termineIscrizione.set(2030, 2, 1, 15, 00,0);
		Calendar dataInizioEvento = Calendar.getInstance();
		dataInizioEvento.set(2030, 3, 1, 15, 00,0);
	
		Evento evento = new Scii(
				admin,
				"Mompiano",
				termineIscrizione,
				dataInizioEvento,
				2,
				5,
				"Parta",
				null,
				null,
				null,				
				null,				
				0,
				1,
				1,
				1
				);
		HashMap<NomeCampo,Boolean> scelte = new HashMap<>();
		scelte.put(NomeCampo.BIGLIETTO_BUS, true);
		scelte.put(NomeCampo.AFFITTO_SCII, true);
		scelte.put(NomeCampo.PRANZO, true);				
		evento.setCampiOptPerUtente(admin, scelte);
		evento = sessione.aggiungiEvento(evento);
						
		boolean check = sessione.utenteIscrittoInEvento(evento);
		for(NomeCampo nome_campo : evento.getCampiOptDiUtente(admin).keySet())
		{
			check = check && evento.getCampoOptDiUtente(admin, nome_campo);
		}
		
		assertTrue(check);		
	}
	
	@Test
	void promemoriaSciiConCostoDipendenteDalleProprieScelte()
	{
		sessione.accedi(admin);
		
		Calendar termineIscrizione = Calendar.getInstance();
		termineIscrizione.set(2030, 2, 1, 15, 00,0);
		Calendar dataInizioEvento = Calendar.getInstance();
		dataInizioEvento.set(2030, 3, 1, 15, 00,0);
		Calendar dataRitiroIscrizioni = Calendar.getInstance();
	
		Evento evento = new Scii(
				admin,
				"Mompiano",
				termineIscrizione,
				dataInizioEvento,
				2,
				5,
				"Sciata",
				null,
				null,
				null,				
				dataRitiroIscrizioni,				
				0,
				1,
				1,
				1
				);
		HashMap<NomeCampo,Boolean> scelte = new HashMap<>();
		scelte.put(NomeCampo.BIGLIETTO_BUS, true);
		scelte.put(NomeCampo.AFFITTO_SCII, true);
		scelte.put(NomeCampo.PRANZO, true);				
		evento.setCampiOptPerUtente(admin, scelte);
		evento = sessione.aggiungiEvento(evento);
		
		Utente tester = new Utente("tester", "");
		sessione.accedi(tester);				
		evento.setCampiOptPerUtente(tester, scelte);
		
		sessione.iscrizioneUtenteInEvento(evento);
		
		sessione.aggiornatore.run();
		
		String titolo = String.format(Messaggi.TITOLO_CHIUSURA_EVENTO, evento.getContenutoCampo(NomeCampo.TITOLO));
		java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd-MM-yyyy HH:mm");
		int costo = (Integer)evento.getContenutoCampo(NomeCampo.COSTO) + (Integer)evento.getContenutoCampo(NomeCampo.BIGLIETTO_BUS) +
				(Integer)evento.getContenutoCampo(NomeCampo.PRANZO) + (Integer)evento.getContenutoCampo(NomeCampo.AFFITTO_SCII);
		String messaggio = String.format(Messaggi.NOTIFICA_CHIUSURA_EVENTO, 
				evento.getContenutoCampo(NomeCampo.TITOLO), 
				sdf.format(((Calendar)evento.getContenutoCampo(NomeCampo.D_O_INIZIO_EVENTO)).getTime()),
				costo);
		Notifica notifica = new Notifica(titolo, messaggio);
		System.out.print(notifica);
		boolean presente = false;
		for(Notifica n : sessione.getUtente_corrente().getNotifiche())
		{
			if(n.equals(notifica))
				{
					presente = true;
					break;
				}
		}
		assertTrue(presente);
	}
	
	@After
	void eliminaEventiDiTest()
	{
		sessione.accedi(admin);
		sessione.deleteEventiDiUtente();
		assertTrue(sessione.getUtente_corrente().getEventi().isEmpty());
	}
	
	@After
	void eliminaNotificheDiTest()
	{
		sessione.accedi(admin);
		boolean eliminate = sessione.deleteNotificheUtenteAll();
		Utente tester = new Utente("tester","");
		sessione.accedi(tester);
		eliminate = eliminate && sessione.deleteNotificheUtenteAll();
		assertTrue(eliminate);
	}
	
	@After
	void eliminaMaterialeDiTesting()
	{
		sessione.accedi(admin);
		sessione.deleteEventiDiUtente();
		sessione.deleteNotificheUtenteAll();
		Utente tester = new Utente("tester","");
		sessione.accedi(tester);
		sessione.deleteEventiDiUtente();
		sessione.deleteNotificheUtenteAll();
	}
}













