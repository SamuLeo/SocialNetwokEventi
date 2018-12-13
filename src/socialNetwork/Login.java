package socialNetwork;
import java.awt.Color;
import java.awt.Font;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.JLabel;

public class Login extends JPanel {
	static final Color coloreBottoni = new Color(255,255,255);
	static final Color coloreSfondo = new Color(250,250,250);
	private static final long serialVersionUID = 1L;
	private int X=0, Y=0;
	private JTextField utente;
	private JPasswordField password;
	
	public Login (Font testoBottoni, Font testo, int pswdWidth, int wordHeight, int fieldWidth) {
		setLayout(null);
		this.setBackground(coloreSfondo);
		JButton accedi = new JButton("Accedi"), nuovoUt = new JButton("Registrati");
		utente = new JTextField(); password = new JPasswordField();
		JLabel utlabel = new JLabel("Utente: "), pswdlabel = new JLabel("Password: ");
		accedi.setBackground(coloreBottoni); nuovoUt.setBackground(coloreBottoni);
		utlabel.setFont(testo); pswdlabel.setFont(testo);
		utente.setFont(testo); password.setFont(testo);
		accedi.setFont(testoBottoni); nuovoUt.setFont(testoBottoni);
		int labelX = pswdWidth, labelY = (int)(wordHeight*1.2);
		int utenteX = fieldWidth, utenteY = (int)(wordHeight*1.2);
		utenteY=labelY=Math.max(utenteY,labelY);
		int bottoneX = (labelX+utenteX+20)/2-10, bottoneY = (int)(utenteY*1.2);
		X=20+labelX+20+utenteX+20;
		Y=40+utenteY+20+labelY+20+bottoneY+40;
		utlabel.setBounds(20, 40, labelX, labelY);
		pswdlabel.setBounds(20, 40+labelY+20, labelX, labelY);
		utente.setBounds(20+labelX+20, 40, utenteX, utenteY);
		password.setBounds(20+labelX+20, 40+labelY+20, utenteX, utenteY);
		accedi.setBounds(20, 40+labelY+20+labelY+20, bottoneX, bottoneY);
		nuovoUt.setBounds(20+bottoneX+20, 40+labelY+20+labelY+20, bottoneX, bottoneY);
		accedi.addActionListener(e -> Grafica.getIstance().accedi(utente.getText(), new String(password.getPassword())));
		nuovoUt.addActionListener(e -> Grafica.getIstance().creaUtente(utente.getText(), new String(password.getPassword())));
		password.addActionListener(e -> Grafica.getIstance().accedi(utente.getText(), new String(password.getPassword())));
		this.add(utlabel);this.add(pswdlabel);
		this.add(utente);this.add(password);
		this.add(nuovoUt);this.add(accedi);
	}
	
	public void focus() {
		if (utente != null) utente.grabFocus();
	}
	
	public int getWidth() {
		return X;
	}
	public int getHeight() {
		return Y;
	}
	
	public void ripulisci() {
		password.setText(""); utente.setText("");
	}
}