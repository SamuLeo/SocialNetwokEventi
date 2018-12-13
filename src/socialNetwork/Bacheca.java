package socialNetwork;
import java.awt.Dimension;

import javax.swing.JLabel;
import javax.swing.JPanel;

public class Bacheca extends JPanel {
	private static final long serialVersionUID = 1L;
	private JLabel[] labels =new JLabel[30];
	
	Bacheca(int larghezza) {
		super();
		setLayout(null);
		for (int i=0; i<30; i++) {
			labels[i]=new JLabel("Prova " + i);
			labels[i].setBounds(0, 50*i, larghezza, 50);
			this.add(labels[i]);
		}
		this.setPreferredSize(new Dimension(larghezza,1500));
	}
	
	public int getHeight() {return 1500;}
	public int getWidth() {return 500;}
}