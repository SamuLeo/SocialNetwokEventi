package it.unibs.dii.isw.socialNetworkEventi.utility;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Calendar;

public class Logger 
{
	File fileDiLog;
	PrintWriter output;
	
	public Logger(String percorsoFile)
	{
		fileDiLog = new File(percorsoFile);
		
		try 
		{
			output = new PrintWriter(
						new OutputStreamWriter(
							new FileOutputStream(fileDiLog,true),"UTF-8"),true);  //il primo true serve per fare l'append su file, il secondo per fare l'autoFlush per println
			output.flush();
		} 
		catch (FileNotFoundException | UnsupportedEncodingException e) 
		{
		}
	}
	
	public void scriviLog(String messaggioDiLog)
	{
		java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd-MM-yyyy HH:mm");
		Calendar now = Calendar.getInstance();
		
		if(fileDiLog.exists() && output != null)
		{
			output.append(sdf.format(now.getTime()) + "  :  " + messaggioDiLog + "\n" );
			output.flush();
		}
	}
	
}
