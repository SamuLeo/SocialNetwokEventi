package it.unibs.dii.isw.socialNetworkEventi.utility;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

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
							new FileOutputStream(fileDiLog),"UTF-8"),true);
			output.flush();
		} 
		catch (FileNotFoundException | UnsupportedEncodingException e) 
		{
		}
	}
	
	public void scriviLog(String messaggioDiLog)
	{
		if(fileDiLog.exists() && output != null)
		{
			output.append(messaggioDiLog + "\n" );
			output.flush();
		}
	}
	
}
