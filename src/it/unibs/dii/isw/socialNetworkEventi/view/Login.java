package it.unibs.dii.isw.socialNetworkEventi.view;
import java.awt.Color;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.ActionListener;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.JLabel;

public class Login extends JPanel 
{
	private IView grafica;
	static final Color coloreBottoni = new Color(255,255,255);
	static final Color coloreSfondo = new Color(250,250,250);
	private static final long serialVersionUID = 1L;
	private int X=0, Y=0;
	private JTextField utente;
	private JPasswordField password;
	
	private final ActionListener accediLambda = e -> grafica.accedi(utente.getText(), new String(password.getPassword()));
	private final ActionListener creaUtLambda = e -> grafica.creaUtente(utente.getText(), new String(password.getPassword()));
	
	public Login (IView grafica, Font testoBottoni, Font testo, int pswdWidth, int wordHeight, int fieldWidth) 
	{
		this.grafica = grafica;
		JLabel img = new JLabel();
		try { img = new JLabel(new ImageIcon(ImageIO.read(Grafica.percorsoIcona).getScaledInstance(Math.min(pswdWidth+fieldWidth,180), Math.min(pswdWidth+fieldWidth,180), Image.SCALE_SMOOTH)));}
		catch (IOException e) {e.printStackTrace();}
		
		setLayout(null);
		setBackground(coloreSfondo);
		
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
		Y=240+utenteY+20+labelY+20+bottoneY+40;
		
		img.setBounds(Math.max(10, X/2-Math.min(pswdWidth+fieldWidth,180)/2),10,Math.min(pswdWidth+fieldWidth,180),Math.min(pswdWidth+fieldWidth,180));
		utlabel.setBounds(20, 240, labelX, labelY);
		pswdlabel.setBounds(20, 240+labelY+20, labelX, labelY);
		utente.setBounds(20+labelX+20, 240, utenteX, utenteY);
		password.setBounds(20+labelX+20, 240+labelY+20, utenteX, utenteY);
		accedi.setBounds(20, 240+labelY+20+labelY+20, bottoneX, bottoneY);
		nuovoUt.setBounds(20+bottoneX+20, 240+labelY+20+labelY+20, bottoneX, bottoneY);
		
		accedi.addActionListener(accediLambda);
		nuovoUt.addActionListener(creaUtLambda);
		password.addActionListener(accediLambda);
		
		add(img);
		add(utlabel); add(pswdlabel);
		add(utente); add(password);
		add(nuovoUt); add(accedi);
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