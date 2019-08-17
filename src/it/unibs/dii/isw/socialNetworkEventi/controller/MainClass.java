package it.unibs.dii.isw.socialNetworkEventi.controller;

import java.sql.SQLException;
import it.unibs.dii.isw.socialNetworkEventi.view.Grafica;

public class MainClass 
{	
	public static void main(String[] args) throws SQLException 
	{	
		Grafica.getIstance().crea();
		Grafica.getIstance().mostraLogin();		
		
		Sessione.getInstance();
	}
}
