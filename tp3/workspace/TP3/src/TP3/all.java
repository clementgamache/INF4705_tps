package TP3;

import java.io.IOException;

public class all
{
	public static void main(String[] args)
	{
		String fileName = "66_99.0";
		//String fileName = "tst.txt";
		Groupe groupe;
		try
		{
			groupe = new Groupe(fileName);
		}
		catch (IOException e)
		{
			System.out.println("Une erreur est survenue lors de la ceture du fichier.");
			e.printStackTrace();
			return;
		}
		//groupe.print();
		long startTime = System.currentTimeMillis();
		groupe.naif();
		double temps = 0.001*(System.currentTimeMillis() - startTime);
		System.out.println(temps);
	}
}
