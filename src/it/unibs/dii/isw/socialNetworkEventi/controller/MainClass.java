package it.unibs.dii.isw.socialNetworkEventi.controller;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;

import it.unibs.dii.isw.socialNetworkEventi.utility.MsgBox;
import it.unibs.dii.isw.socialNetworkEventi.utility.Stringhe;
import it.unibs.dii.isw.socialNetworkEventi.view.IView;

public class MainClass 
{	
	private static String operatingSystem = System.getProperty("os.name").toLowerCase();

	public static void main(String[] args) throws SQLException 
	{
		MsgBox.configuraAspetto();
		
		try 
		{
			configuraFileDiConfigurazione();
		} 
		catch (IOException e) 
		{
			new MsgBox().messaggioErrore("Errore Avvio", "Non è stato possibile reperire i file di configurazione necessari all'avvio del programma");
			e.printStackTrace();
			return;
		}
		
		try
		{
			IController controller = initSessione();
			initGrafica(controller);
		}
		catch(InstantiationException | IllegalAccessException | ClassNotFoundException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) 
		{
			new MsgBox().messaggioErrore("Errore Avvio", "Non è stato possibile avviare il programma (impossibile istanziare le classi principali)");
			e.printStackTrace();
			return;
		}
	}
	
	private static void configuraFileDiConfigurazione() throws FileNotFoundException, IOException
	{
		if(operatingSystem.indexOf("linux") >= 0 || operatingSystem.indexOf("mac") >= 0) 
			System.getProperties().load(new FileInputStream(Stringhe.PERCORSO_FILE_CONFIG_LINUX));
		else if(operatingSystem.indexOf("win") >= 0) 
			System.getProperties().load(new FileInputStream(Stringhe.PERCORSO_FILE_CONFIG_WIN));
	}
	
	private static IController initSessione() throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, ClassNotFoundException
	{
		String className = System.getProperty("social_network.controller.class.name");
		return (IController)Class.forName(className).getDeclaredConstructor().newInstance();
	}
	
	private static IView initGrafica(IController controller) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, ClassNotFoundException
	{
		String className = System.getProperty("social_network.grafica.class.name");
		return (IView)Class.forName(className).getDeclaredConstructor(IController.class).newInstance(controller);
	}
}