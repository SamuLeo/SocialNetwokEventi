package it.unibs.dii.isw.socialNetworkEventi.view;

import java.util.*;
import java.util.function.Consumer;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.*;
import javax.swing.table.*;
 
public class Calendario extends JFrame {
	private static final long serialVersionUID = 1L;
	private Dimension screenSize=Toolkit.getDefaultToolkit().getScreenSize();
	private int screenW = (int)(screenSize.getWidth());
	private int screenH = (int)(screenSize.getHeight());
	private DefaultTableModel model;
	private Calendar cal = new GregorianCalendar();
	private JLabel nomeMese;
	private JTable table;
	private int lato;
	 
	Calendario(Font font, int lato, Consumer<Calendar> azione) {
		this.lato=lato;
		
		setIconImage(Grafica.icona.getImage());
		
	    setTitle("Scegli una data");
	    setSize(lato, lato);
	    setBounds(screenW/2-lato/2, screenH/2-lato/2, lato, lato);
	    setLayout(new BorderLayout());
	    setVisible(true);
	    nomeMese = new JLabel();
	    nomeMese.setHorizontalAlignment(SwingConstants.CENTER);
	    nomeMese.setFont(font);
	    nomeMese.setBackground(Color.white);
	    JButton indietro = new JButton("←");
	    indietro.setFont(font);
	    indietro.setBackground(Color.white);
	    indietro.addActionListener(w -> {
	    	cal.add(Calendar.MONTH, -1);
	    	updateMonth();
	    });
 
	    JButton avanti = new JButton("→");
	    avanti.setFont(font);
	    avanti.setBackground(Color.white);
	    avanti.addActionListener(ae -> {
	    	cal.add(Calendar.MONTH, +1);
	    	updateMonth();
	    });
 
	    JPanel panel = new JPanel();
	    panel.setBackground(new Color(250,250,250));
	    panel.setLayout(new BorderLayout());
	    panel.add(indietro,BorderLayout.WEST);
	    panel.add(nomeMese,BorderLayout.CENTER);
	    panel.add(avanti,BorderLayout.EAST);
	    

	    cal.setFirstDayOfWeek(Calendar.MONDAY);
	    String [] columns = {"Lun","Mar","Mer","Gio","Ven","Sab","Dom"};
	    model = new DefaultTableModel(null,columns);
	    table = new JTable(model);
	    table.setRowHeight(lato/7);
	    table.setFont(font);
	    table.getTableHeader().setFont(font);
	    table.setCellSelectionEnabled(true);
	    table.setColumnSelectionAllowed(false);
	    table.setDragEnabled(false);
	    table.setRowSelectionAllowed(false);
	    JScrollPane pane = new JScrollPane(table);
	    table.addMouseListener(new MouseListener () {
	    	public void mouseClicked(MouseEvent e) {
	    		JTable target = (JTable)e.getSource();
	    		int row = target.getSelectedRow();
	    		int column = target.getSelectedColumn();
	    		//System.out.println(row + " " + column + " val " + target.getValueAt(row, column));
	    		Object valpar = target.getValueAt(row, column);
	    		if (valpar == null) return;
	    		int val = (int)valpar;
	    		Calendar dataSelezionata = Calendar.getInstance();
	    		dataSelezionata.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), val);
	    		azione.accept(dataSelezionata);
	    		setVisible(false);
			}
			public void mouseEntered(MouseEvent arg0) {}
			public void mouseExited(MouseEvent arg0) {}
			public void mousePressed(MouseEvent arg0) {}
			public void mouseReleased(MouseEvent arg0) {}
		});
	    this.add(panel,BorderLayout.NORTH);
	    this.add(pane,BorderLayout.CENTER);
 
	    this.updateMonth();
	}
 
	void updateMonth() {
		//In questo caso i salvataggi e ripristini non servono a nulla perché la data non viene usata altrove, ma si usa il contenuto della cella cliccata nelle lambda 
		//Date prima = cal.getTime();	//Si porta temporaneamente la data al primo del mese (selezionato sul calendario adesso!) per scoprire che giorno è
		
		cal.setFirstDayOfWeek(GregorianCalendar.MONDAY);
	    String mese = cal.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.ITALY).toUpperCase();
	    nomeMese.setText(mese + " " +  cal.get(Calendar.YEAR));
    	cal.set(Calendar.DAY_OF_MONTH, 1);
	    int startDay = cal.get(Calendar.DAY_OF_WEEK) == 1? 6 : cal.get(Calendar.DAY_OF_WEEK)-2;
	    int numberOfDays = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
	    
	    //cal.setTime(prima);	//E poi si ripristina la data a quella di prima
	    
	    model.setRowCount(0);
	    model.setRowCount(1);
	    int offset = startDay % 7;
	    boolean offsetzero = offset==0;
	    for(int day=0;day<numberOfDays;day++){
	    	if ((day+offset)%7==0) {
	    		if (offsetzero) offsetzero = false;
	    		else model.addRow(new Object [0]);
	    	}
	    	model.setValueAt(day+1, (day+offset)/7 , (day+offset)%7 );
	    }
	    revalidate();
	    setBounds(getX(), getY(), lato, (model.getRowCount()+1)*lato/7+nomeMese.getHeight()+table.getTableHeader().getHeight());
	}
}