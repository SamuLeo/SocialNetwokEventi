package socialNetwork;

import java.io.Serializable;

public class Campo<T> implements Serializable
{
	private static final long serialVersionUID = 1L;
	
	private T campo;
	private boolean obbligatorio=false;
	private String descrizione_campo;
	
	public Campo(T campo, boolean obbligatorio, String descrzione_campo)
	{
		this.campo=campo;
		this.obbligatorio=obbligatorio;
		this.descrizione_campo=descrizione_campo;
	}

	/*
	 * Getters and setters
	 */
	public T 		getCampo() {return campo;}
	public void 	setCampo(T campo) {this.campo = campo;}
	public boolean 	isObbligatorio() {return obbligatorio;}
	public void 	setObbligatorio(boolean obbligatorio) {this.obbligatorio = obbligatorio;}
}
