package it.unibs.dii.isw.socialNetworkEventi.model;

import java.io.Serializable;

public class Campo<T> implements Serializable
{
	private static final long serialVersionUID = 1L;
	
	private T contenuto;
	private boolean obbligatorio=false;
	private String descrizione_campo;
	
	public Campo(T campo, boolean obbligatorio, String descrizione_campo)
	{
		this.contenuto=campo;
		this.obbligatorio=obbligatorio;
		this.descrizione_campo=descrizione_campo;
	}

	/*
	 * Getters and setters
	 */
	public T 		getContenuto() {return contenuto != null ? contenuto : null;}
	public void 	setContenuto(T campo) {this.contenuto = campo;}
	public boolean 	isObbligatorio() {return obbligatorio;}
	public void 	setObbligatorio(boolean obbligatorio) {this.obbligatorio = obbligatorio;}
	public String getDescrizione_campo() {return descrizione_campo;}
	public void setDescrizione_campo(String descrizione_campo) {this.descrizione_campo = descrizione_campo;}

	public boolean equals(Campo<T> campo) 
	{
		if(contenuto.equals(campo.getContenuto()) && obbligatorio==campo.isObbligatorio())
			return true;
		else
			return false;
	}
}
