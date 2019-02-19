package it.unibs.dii.isw.socialNetworkEventi.view;


import java.awt.Dimension;
import java.awt.Font;
import java.util.Calendar;
import java.util.LinkedList;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import it.unibs.dii.isw.socialNetworkEventi.model.Campo;
import it.unibs.dii.isw.socialNetworkEventi.model.Evento;

public class SchedaEvento extends JPanel {
	private static final long serialVersionUID = 1L;
	private int Y=0;
	private JLabel[] desc, val;
	JLabel titolo;
	JButton iscriviti;

	@SuppressWarnings({ "deprecation", "rawtypes", "static-access" })
	public SchedaEvento(Evento e, Font testo, Font testoBottoni, int altezzaRighe, int larghezza) {
		super();
		setLayout(null);
		titolo = new JLabel((String)e.getCampi().get(NomeCampi.TITOLO).getContenuto());
		titolo.setFont(testoBottoni);
		titolo.setHorizontalAlignment(SwingConstants.CENTER);
		titolo.setBounds(20, 20, larghezza-40,altezzaRighe/5*6);
		Y=20+altezzaRighe/5*6;
		add(titolo);
		iscriviti = new JButton("Iscriviti");
		iscriviti.setFont(testoBottoni);
		iscriviti.setBounds(20, Y+10, larghezza-40,altezzaRighe/5*6);
		iscriviti.setBackground(Grafica.getIstance().coloreBottoni);
		add(iscriviti);
		Y+=20+altezzaRighe/5*6;
		LinkedList<Campo> llist = new LinkedList<>();
		for (Campo c: e.getCampi().values())
			if (c!=null) llist.add(c);
		Campo[] s = llist.toArray(new Campo[0]);
		desc=new JLabel[s.length];
		val=new JLabel[s.length];
		Object temp;
		for (int i=0; i<s.length;i++) {
			desc[i]= new JLabel(s[i].getDescrizione_campo());
			temp=s[i].getContenuto();
			if (temp instanceof Calendar) {
				Calendar t = (Calendar)temp;
				val[i]= new JLabel(""+t.getTime().getDay() + '/'+ t.getTime().getMonth() + '/' + (t.getTime().getYear()+1900)+ ' ' + t.getTime().getHours() + '.'+ t.getTime().getMinutes());
			}
			else val[i] = new JLabel(temp.toString());
			desc[i].setFont(testo); val[i].setFont(testo);
			desc[i].setBounds(20, Y, (larghezza-60)/2, altezzaRighe);
			val[i].setBounds(20+(larghezza-60)/2, Y, (larghezza-60)/2, altezzaRighe);
			Y+= altezzaRighe+20;
			add(desc[i]); add(val[i]);
		}
		setPreferredSize(new Dimension(larghezza, Y));
	}
	
	void ridimensiona(int larghezza) {
		titolo.setSize(larghezza-40,titolo.getHeight());
		iscriviti.setSize(larghezza-40,iscriviti.getHeight());
		for (JLabel d: desc)
			d.setSize((larghezza-60)/2, d.getHeight());
		for (JLabel v: val)
			v.setBounds(20+(larghezza-60)/2, v.getY(), (larghezza-60)/2, v.getHeight());
		setPreferredSize(new Dimension(larghezza, Y));
	}
}
