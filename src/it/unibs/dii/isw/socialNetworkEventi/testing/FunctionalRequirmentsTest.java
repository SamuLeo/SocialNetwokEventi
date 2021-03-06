package it.unibs.dii.isw.socialNetworkEventi.testing;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.After;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Random;

import it.unibs.dii.isw.socialNetworkEventi.controller.Sessione;
import it.unibs.dii.isw.socialNetworkEventi.model.*;
import it.unibs.dii.isw.socialNetworkEventi.utility.CategoriaEvento;
import it.unibs.dii.isw.socialNetworkEventi.utility.Stringhe;
import it.unibs.dii.isw.socialNetworkEventi.utility.NomeCampo;
import it.unibs.dii.isw.socialNetworkEventi.utility.StatoEvento;

class FunctionalRequirmentsTest
{
	Sessione sessione =  new Sessione();
	Utente 	admin = sessione.getDb().selectUtente("admin"),
			tester = sessione.getDb().selectUtente("tester");
	
	@Test
	synchronized void accessoConInserimentoPartitaDiCalcioNelDB()
	{
		assertTrue(sessione.accedi(admin));
		
		Calendar termineIscrizione = Calendar.getInstance();
		termineIscrizione.set(2030, 2, 15, 15, 00,0);
		Calendar dataInizioEvento = Calendar.getInstance();
		dataInizioEvento.set(2030, 3, 1, 15, 00,0);
	
		Evento evento = new PartitaCalcio(
				admin,
				"Giappone",
				termineIscrizione,
				dataInizioEvento,
				10,
				5,
				"ProveISO",
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
	synchronized void accessoConTentativoInserimentoPartitaCalcioNonValida()
	{
		assertTrue(sessione.accedi(admin));		
		Calendar dataInizioEvento = Calendar.getInstance();
		dataInizioEvento.set(2030, 3, 1, 15, 00,0);

		try
		{
			new PartitaCalcio(
				admin,
				"Tavoliere",
				null,
				dataInizioEvento,
				10,
				5,
				"Puglia Calcio",
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
	synchronized void adesionePartitaDiCalcio()
	{
		assertTrue(sessione.accedi(admin));
		
		Calendar termineIscrizione = Calendar.getInstance();
		termineIscrizione.set(2030, 2, 15, 15, 00,0);
		Calendar dataInizioEvento = Calendar.getInstance();
		dataInizioEvento.set(2030, 3, 1, 15, 00,0);
	
		Evento evento = new PartitaCalcio(
				admin,
				"Stromboli",
				termineIscrizione,
				dataInizioEvento,
				10,
				5,
				"Calcio scottante",
				null,
				null,
				null,				
				null,				
				2,
				18,
				25,
				"qualsiasi"
				);
		evento = sessione.aggiungiEvento(evento);
		
		assertTrue(sessione.accedi(tester));
		sessione.iscrizioneUtenteInEvento(evento);

		assertTrue(sessione.utenteIscrittoInEvento(evento));
	}
	
	@Test
	synchronized void doppiaAdesionePartitaDiCalcio()
	{
		assertTrue(sessione.accedi(admin));
		
		Calendar termineIscrizione = Calendar.getInstance();
		termineIscrizione.set(2030, 2, 15, 15, 00,0);
		Calendar dataInizioEvento = Calendar.getInstance();
		dataInizioEvento.set(2030, 3, 1, 15, 00,0);
	
		Evento evento = new PartitaCalcio(
				admin,
				"Monte Etna",
				termineIscrizione,
				dataInizioEvento,
				10,
				5,
				"Calcio bollente",
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
		
		assertTrue(sessione.accedi(tester));		
		sessione.iscrizioneUtenteInEvento(evento);
		sessione.iscrizioneUtenteInEvento(evento);

		evento = sessione.getDb().selectEvento(evento.getId());
		int count = evento.getNumeroPartecipanti();
		assertTrue(count == 2);
	}
	
	@Test
	synchronized void disiscrizioneEntroDataDiRitiro()
	{
		assertTrue(sessione.accedi(admin));
		
		Calendar termineIscrizione = Calendar.getInstance();
		termineIscrizione.set(2030, 2, 15, 15, 00,0);
		Calendar dataInizioEvento = Calendar.getInstance();
		dataInizioEvento.set(2030, 3, 1, 15, 00,0);
		Calendar dataRitiro = Calendar.getInstance();
		dataRitiro.set(2030, 2, 10, 15, 00,0);
	
		Evento evento = new PartitaCalcio(
				admin,
				"Reykjavik",
				termineIscrizione,
				dataInizioEvento,
				2,
				5,
				"Calcio serio",
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
		
		assertTrue(sessione.accedi(tester));		
		sessione.iscrizioneUtenteInEvento(evento);
		sessione.disiscrizioneUtenteEvento(evento);

		evento = sessione.getDb().selectEvento(evento.getId());
		int count = evento.getNumeroPartecipanti();
		assertTrue(count == 1);
	}
	
	@Test
	synchronized void confermaEventoInCasoDiAdesioniSufficientiRaggiunteAlTermineIscrizioni()
	{
		assertTrue(sessione.accedi(admin));
		
		Calendar termineIscrizione = Calendar.getInstance();
		termineIscrizione.setTime(new Date());
		termineIscrizione.add(Calendar.SECOND, 1);
		Calendar dataInizioEvento = Calendar.getInstance();
		dataInizioEvento.set(2030, 3, 1, 15, 00,0);
	
		Evento evento = new PartitaCalcio(
				admin,
				"Vatnajokull",
				termineIscrizione,
				dataInizioEvento,
				2,
				5,
				"Calcio Vatna",
				null,
				null,
				null,				
				null,				
				0,
				18,
				25,
				"qualsiasi"
				);
		evento = sessione.aggiungiEvento(evento);
		
		assertTrue(sessione.accedi(tester));	
		sessione.aggiornaUtenti();
		sessione.iscrizioneUtenteInEvento(evento);
		
		try {Thread.sleep(1200,0);
		} catch (InterruptedException e) {e.printStackTrace();}
		
		sessione.aggiorna();
		sessione.aggiornaUtenti();
	
		String titolo = String.format(Stringhe.TITOLO_CHIUSURA_EVENTO, evento.getContenutoCampo(NomeCampo.TITOLO));
		java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd-MM-yyyy HH:mm");
		String messaggio = String.format(Stringhe.NOTIFICA_CHIUSURA_EVENTO, evento.getContenutoCampo(NomeCampo.TITOLO), 
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
	synchronized void confermaEventoInCasoDiAdesioniMassimeRaggiunteDopoTermineRitiro()
	{
		assertTrue(sessione.accedi(admin));
		
		Calendar termineIscrizione = Calendar.getInstance();
		termineIscrizione.set(2030, 2, 1, 15, 00,0);
		Calendar dataInizioEvento = Calendar.getInstance();
		dataInizioEvento.set(2030, 3, 1, 15, 00,0);
		Calendar dataRitiroIscrizioni = Calendar.getInstance();
		dataRitiroIscrizioni.setTime(new Date());
	
		Evento evento = new PartitaCalcio(
				admin,
				"Livorno",
				termineIscrizione,
				dataInizioEvento,
				2,
				5,
				"Campionato",
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
		
		assertTrue(sessione.accedi(tester));		
		sessione.iscrizioneUtenteInEvento(evento);
		
		sessione.aggiorna();
		sessione.aggiornaUtenti();
	
		String titolo = String.format(Stringhe.TITOLO_CHIUSURA_EVENTO, evento.getContenutoCampo(NomeCampo.TITOLO));
		java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd-MM-yyyy HH:mm");
		String messaggio = String.format(Stringhe.NOTIFICA_CHIUSURA_EVENTO, 
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
	synchronized void ritiroEventoPrimaDellaDataDiRitiro()
	{
		assertTrue(sessione.accedi(admin));
		
		Calendar termineIscrizione = Calendar.getInstance();
		termineIscrizione.set(2030, 2, 1, 15, 00,0);
		Calendar dataInizioEvento = Calendar.getInstance();
		dataInizioEvento.set(2030, 3, 1, 15, 00,0);
		Calendar dataRitiroIscrizioni = Calendar.getInstance();
		dataRitiroIscrizioni.set(2030, 2, 1, 10, 00,0);
	
		Evento evento = new PartitaCalcio(
				admin,
				"Brescia",
				termineIscrizione,
				dataInizioEvento,
				2,
				5,
				"Calcio femminile",
				null,
				null,
				null,				
				dataRitiroIscrizioni,				
				0,
				18,
				25,
				"femmine"
				);
		evento = sessione.aggiungiEvento(evento);
		
		sessione.deleteEvento(evento);
		
		assertTrue(sessione.getDb().selectEvento(evento.getId()).getStato().equals(StatoEvento.RITIRATA));
	}
	
	@Test
	synchronized void fallimentoInCasoDiMancatoRaggiungimentoNumeroIscrizioni()
	{
		assertTrue(sessione.accedi(admin));
		
		Calendar termineIscrizione = Calendar.getInstance();
		termineIscrizione.setTime(new Date());
		Calendar dataInizioEvento = Calendar.getInstance();
		dataInizioEvento.set(2030, 3, 1, 15, 00,0);
	
		Evento evento = new PartitaCalcio(
				admin,
				"Bovezzo",
				termineIscrizione,
				dataInizioEvento,
				2,
				5,
				"Partita",
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
		sessione.aggiorna();
		
		assertTrue(sessione.getDb().selectEvento(evento.getId()).getStato().equals(StatoEvento.FALLITA));			
	}
	
	@Test
	synchronized void passaggioChiusoConcluso() 
	{
		assertTrue(sessione.accedi(admin));
		
		Calendar termineIscrizione = Calendar.getInstance();
		termineIscrizione.add(Calendar.SECOND,1);
		Calendar dataInizioEvento = Calendar.getInstance();
		dataInizioEvento.add(Calendar.SECOND,2);
		Calendar dataTermineEvento = Calendar.getInstance();
		dataTermineEvento.add(Calendar.SECOND,2);

	
		Evento evento = new PartitaCalcio(
				admin,
				"Concesio",
				termineIscrizione,
				dataInizioEvento,
				2,
				5,
				"Partitella",
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
		
		assertTrue(sessione.accedi(tester));		
		sessione.iscrizioneUtenteInEvento(evento);
		
		try {
			Thread.sleep(2500, 0);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
//		serve aggiornare 2 volte perchè l'evento passa da APERTO a CHIUSO a CONCLUSO
//		(l'aggiornatore lo farebbe in automatico ma per ragioni di tempo viene forzato in modo da testare la logica)
		sessione.aggiorna();
		sessione.aggiorna();
		
		assertTrue(sessione.getDb().selectEvento(evento.getId()).getStato().equals(StatoEvento.CONCLUSA));	
	}
	
	@Test
	synchronized void modificaCampiFacoltativiUtente()
	{
		assertTrue(sessione.accedi(admin));
		int eta_min = Math.abs(new Random().nextInt()%30);
		int eta_max = eta_min+1;
		sessione.updateFasciaEta(eta_min, eta_max);
		assertTrue(sessione.getUtente_corrente().getEtaMin() == eta_min && 
				sessione.getUtente_corrente().getEtaMax() == eta_max);
	}
	
	@Test
	synchronized void notificaPerNuovoEventoNellaCategoriaDiInteresse()
	{
		assertTrue(sessione.accedi(admin));		
		sessione.eliminaInteresseUtenteCorrente(CategoriaEvento.PARTITA_CALCIO);
		boolean check = !sessione.getUtente_corrente().getCategorieInteressi().contains(CategoriaEvento.PARTITA_CALCIO);
		sessione.aggiungiInteresseUtenteCorrente(CategoriaEvento.PARTITA_CALCIO);
		check = check && sessione.getUtente_corrente().getCategorieInteressi().contains(CategoriaEvento.PARTITA_CALCIO);
		
		assertTrue(check);
	}
	
	@Test
	synchronized void invitoAgliAderentiPassatiDiUnProprioEvento()
	{
		assertTrue(sessione.accedi(admin));
		
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
		
		assertTrue(sessione.accedi(tester));
		sessione.iscrizioneUtenteInEvento(evento);

		assertTrue(sessione.accedi(admin));
		evento = sessione.aggiungiEvento(evento);
		try 
		{
			HashMap<CategoriaEvento,LinkedList<Utente>> hash_map= new HashMap<>();
			hash_map = sessione.getDb().selectUtentiDaEventiPassati(admin.getNome());
			if(hash_map != null && hash_map.get(CategoriaEvento.PARTITA_CALCIO).contains(tester))
				sessione.getMessagesFactory().segnalaEventoPerUtente(evento, admin, tester);
		} 
		catch (SQLException e) 
		{
			e.printStackTrace();
		}
		
		assertTrue(sessione.accedi(tester));
		String nome_categoria = evento.getNomeCategoria().getString().replaceAll("_", " di ");
		String titolo = String.format(Stringhe.TITOLO_INVITO_EVENTO, nome_categoria);
		String messaggio = String.format(Stringhe.NOTIFICA_PER_INVITO_UTENTE, admin.getNome(), evento.getContenutoCampo(NomeCampo.TITOLO));	

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
	synchronized void iscrizioneSciiConScelte()
	{
		assertTrue(sessione.accedi(admin));
		
		Calendar termineIscrizione = Calendar.getInstance();
		termineIscrizione.set(2030, 2, 1, 15, 00,0);
		Calendar dataInizioEvento = Calendar.getInstance();
		dataInizioEvento.set(2030, 3, 1, 15, 00,0);
	
		Evento evento = new Scii(
				admin,
				"Monte Bianco",
				termineIscrizione,
				dataInizioEvento,
				2,
				5,
				"Sciata bianca",
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
	synchronized void promemoriaSciiConCostoDipendenteDalleProprieScelte()
	{
		assertTrue(sessione.accedi(admin));
		
		Calendar termineIscrizione = Calendar.getInstance();
		termineIscrizione.set(2030, 2, 1, 15, 00,0);
		Calendar dataInizioEvento = Calendar.getInstance();
		dataInizioEvento.set(2030, 3, 1, 15, 00,0);
		Calendar dataRitiroIscrizioni = Calendar.getInstance();
		dataRitiroIscrizioni.setTime(new Date());
	
		Evento evento = new Scii(
				admin,
				"Monte Rosa",
				termineIscrizione,
				dataInizioEvento,
				2,
				5,
				"Sciata rosa",
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
		
		assertTrue(sessione.accedi(tester));				
		evento.setCampiOptPerUtente(tester, scelte);
		
		sessione.iscrizioneUtenteInEvento(evento);
		
		sessione.aggiorna();
		sessione.aggiorna();
		sessione.aggiornaUtenti();
		
		String titolo = String.format(Stringhe.TITOLO_CHIUSURA_EVENTO, evento.getContenutoCampo(NomeCampo.TITOLO));
		java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd-MM-yyyy HH:mm");
		int costo = (Integer)evento.getContenutoCampo(NomeCampo.COSTO) + (Integer)evento.getContenutoCampo(NomeCampo.BIGLIETTO_BUS) +
				(Integer)evento.getContenutoCampo(NomeCampo.PRANZO) + (Integer)evento.getContenutoCampo(NomeCampo.AFFITTO_SCII);
		String messaggio = String.format(Stringhe.NOTIFICA_CHIUSURA_EVENTO, 
				evento.getContenutoCampo(NomeCampo.TITOLO), 
				sdf.format(((Calendar)evento.getContenutoCampo(NomeCampo.D_O_INIZIO_EVENTO)).getTime()),
				costo);
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
	
	@After
	synchronized void eliminaEventiDiTest()
	{
		assertTrue(sessione.accedi(admin));
		sessione.deleteEventiDiUtente();
		assertTrue(sessione.getUtente_corrente().getEventi().isEmpty());
	}
	
	@After
	synchronized void eliminaNotificheDiTest()
	{
		assertTrue(sessione.accedi(admin));
		boolean eliminate = sessione.deleteNotificheUtenteAll();
		assertTrue(sessione.accedi(tester));
		eliminate = eliminate && sessione.deleteNotificheUtenteAll();
		assertTrue(eliminate);
	}
	
	@After
	synchronized void eliminaMaterialeDiTesting()
	{
		assertTrue(sessione.accedi(admin));
		sessione.deleteEventiDiUtente();
		sessione.deleteNotificheUtenteAll();
		assertTrue(sessione.accedi(tester));
		sessione.deleteEventiDiUtente();
		sessione.deleteNotificheUtenteAll();
	}
}