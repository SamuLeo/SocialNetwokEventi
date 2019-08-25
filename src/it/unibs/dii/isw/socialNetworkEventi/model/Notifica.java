package it.unibs.dii.isw.socialNetworkEventi.model;

import java.io.Serializable;
import java.util.Calendar;

public class Notifica implements Serializable
{
	private static final long serialVersionUID = 1L;

	private int id_notifica;
	private String titolo;
	private String contenuto;
	private Calendar data ;
	
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
		builder.append("Notifica " /*+ this.id_notifica + "\n"*/);
		builder.append("\nData \n");
		java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd-MM-yyyy HH:mm");
		builder.append(sdf.format(data.getTime()) + "\n");
		builder.append("\nTitolo\n");
		builder.append(titolo + "\n");
		builder.append("\nContenuto\n");
		builder.append(contenuto + "\n");

		return builder.toString();
	}
	
	@Override
	public boolean equals(Object obj)
	{
		//return this.toString().equals(obj.toString());
		Notifica notifica = (Notifica)obj;
		boolean equals = titolo.equals(notifica.getTitolo()) && contenuto.equals(notifica.getContenuto());
		return equals;
	}

	public String getTitolo() {return titolo;}
	public String getContenuto() {return contenuto;}
	public Calendar getData() {return data;}
	public int getIdNotifica() {return id_notifica;}
	public void setIdNotifica(int id) {this.id_notifica = id;}
}
