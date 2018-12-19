package it.unibs.dii.isw.socialNetworkEventi.model;

import java.io.Serializable;
import java.util.Calendar;

import it.unibs.dii.isw.socialNetworkEventi.utility.TipoNotifica;

public class Notifica implements Serializable
{

	private static final long serialVersionUID = 1L;

	private int id_notifica;

	private String titolo;
	private String contenuto;
	private Calendar data = Calendar.getInstance();
	private TipoNotifica tipo = TipoNotifica.GENERICA;
	
	public Notifica(String titolo, String contenuto) 
	{
		this.titolo=titolo;
		this.contenuto=contenuto;
	}
	
	public Notifica(String titolo, String contenuto, Calendar data)
	{
		this(titolo,contenuto);
		this.data = data;
	}
	
	public String getTitolo() {return titolo;}
	public String getContenuto() {return contenuto;}
	public Calendar getData() {return data;}
	public int getIdNotifica() {return id_notifica;}
	public void setIdNotifica(int id) {this.id_notifica = id;}
}
