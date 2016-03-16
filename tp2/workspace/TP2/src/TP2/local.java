package TP2;

import java.io.IOException;

public class local {
	public static void main(String [ ] args)
	{
		String fileName = "";
		boolean print = false;
		for (int i = 0; i < args.length; i++)
		{
			if (args[i].equals("-f"))
			{
				fileName = args[++i];
				
			}
			else if (args[i].equals("-p"))
			{
				print = true;
			}
		}
		
		Ville ville;
		try
		{
			ville = new Ville(fileName);
		}
		catch (IOException e)
		{
			System.out.println("Une erreur est survenue lors de la ceture du fichier.");
			e.printStackTrace();
			return;
		}
		long currentTime = System.currentTimeMillis();
		String result = ville.local(print);
		double timeConv = 0.001*(System.currentTimeMillis() - currentTime);
		result += Double.valueOf(timeConv) + " sec\n";
	}
}
