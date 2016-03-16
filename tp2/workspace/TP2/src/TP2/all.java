package TP2;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

public class all
{
	public static void main(String[] args)
	{

		File folder = new File(System.getProperty("user.dir"));
		File[] listOfFiles = folder.listFiles();
		List<String> fileNames = new LinkedList<String>();
		for (int i = 0; i < listOfFiles.length; i++)
		{
			File file = listOfFiles[i];
			if (file.isFile() && file.getName().endsWith(".txt"))
			{
				fileNames.add(file.getName());
			}
		}

		try
		{
			System.out.println("             fichier,emp.,capa.,vora.,temp-vo.,dyna.,temp-dy.,local,temp-local");
			for (String fileName : fileNames)
			{
				Ville ville = new Ville(fileName);
				long startTime = System.currentTimeMillis();
				List<Emplacement> vorace = ville.calculerVorace();
				double tempsVorace = 0.001*(System.currentTimeMillis() - startTime);

				startTime = System.currentTimeMillis();
				List<Emplacement> dynamique = ville.calculerDynamique();
				double tempsDynamique = 0.001*(System.currentTimeMillis() - startTime);
				
				startTime = System.currentTimeMillis();
				List<Emplacement> local = ville.calculerLocal();
				double tempsLocal = 0.001*(System.currentTimeMillis() - startTime);

				System.out.format("%20s,%4d,%5d,%5d,%8.3f,%5d,%8.3f,%5d,%8.3f\n",
						fileName, ville.getNombreEmplacements(), ville.getCapaciteFournisseur(),
						Emplacement.getRevenuTotal(vorace), tempsVorace,
						Emplacement.getRevenuTotal(dynamique), tempsDynamique,
						Emplacement.getRevenuTotal(local), tempsLocal);
			}
		} catch (Exception e)
		{
			System.out.println("Une erreur est survenue.");

			e.printStackTrace();
			return;
		}
	}
}
