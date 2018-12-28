package it.unibs.dii.isw.socialNetworkEventi.model;

import java.io.Serializable;
import java.sql.Date;
import java.util.Calendar;

import it.unibs.dii.isw.socialNetworkEventi.utility.TipoNotifica;

public class Notifica implements Serializable
{

	private static final long serialVersionUID = 1L;

	private int id_notifica;

	private String titolo;
	private String contenuto;
	private Calendar data ;
	private TipoNotifica tipo = TipoNotifica.GENERICA;
	
	public Notifica(String titolo, String contenuto) 
	{
		this.titolo=titolo;
		this.contenuto=contenuto;
		data = Calendar.getInstance();
	}
	
	public Notifica(String titolo, String contenuto, Calendar data)
	{
		this(titolo, contenuto);
		this.data = data;
	}
	
	public Notifica(int id_notifica, String titolo, String contenuto, Calendar data)
	{
		this(titolo, contenuto, data);
		this.id_notifica=id_notifica;
	}
	
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Notifica \ntitolo : ");
		builder.append(titolo);
		builder.append("\ncontenuto: ");
		builder.append(contenuto);
		builder.append("\ndata : ");
		java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd-MM-yyyy HH:mm");
		builder.append(sdf.format(data.getTime()) + "\n");

		return builder.toString();
	}

	public String getTitolo() {return titolo;}
	public String getContenuto() {return contenuto;}
	public Calendar getData() {return data;}
	public int getIdNotifica() {return id_notifica;}
	public void setIdNotifica(int id) {this.id_notifica = id;}
}
