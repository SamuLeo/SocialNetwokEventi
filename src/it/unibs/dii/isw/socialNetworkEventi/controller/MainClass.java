package it.unibs.dii.isw.socialNetworkEventi.controller;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;

import it.unibs.dii.isw.socialNetworkEventi.utility.Stringhe;
import it.unibs.dii.isw.socialNetworkEventi.view.IView;

public class MainClass 
{	
	private static String operatingSystem = System.getProperty("os.name").toLowerCase();

	private static IController controller = null;
	@SuppressWarnings("unused")
	private static IView view = null;

	public static void main(String[] args) throws SQLException 
	{
		try 
		{configuraFileDiConfigurazione();} 
		catch (IOException e) 
		{e.printStackTrace();}
		
		initSessione();
		initGrafica();
	}
	
	private static void configuraFileDiConfigurazione() throws FileNotFoundException, IOException
	{
		if(operatingSystem.indexOf("linux") >= 0 || operatingSystem.indexOf("mac") >= 0) 
			System.getProperties().load(new FileInputStream(Stringhe.PERCORSO_FILE_CONFIG_LINUX));
		else if(operatingSystem.indexOf("win") >= 0) 
			System.getProperties().load(new FileInputStream(Stringhe.PERCORSO_FILE_CONFIG_WIN));
	}
	
	private static void initSessione()
	{
		try 
		{
			String className = System.getProperty("social_network.controller.class.name");
			controller = (IController)Class.forName(className).getDeclaredConstructor().newInstance();
		}
		catch(InstantiationException | IllegalAccessException | ClassNotFoundException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) 
		{
			e.printStackTrace();
		}
	}
	
	private static void initGrafica()
	{
		try 
		{
			String className = System.getProperty("social_network.grafica.class.name");
			view = (IView)Class.forName(className).getDeclaredConstructor(IController.class).newInstance(controller);
		} 
		catch(InstantiationException | IllegalAccessException | ClassNotFoundException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) 
		{
			e.printStackTrace();
		}
	}
}
