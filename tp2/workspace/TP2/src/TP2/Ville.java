package TP2;

import java.io.*;
import java.util.*;

public class Ville {
	
	private int nombreEmplacement;
	private List<Emplacement> emplacements;
	private int capaciteFournisseur;
	
	public Ville(String fileName) throws IOException
	{
		readFile(fileName);
	}
	
	private void readFile(String fileName) throws IOException
	{
		FileReader fileReader = new FileReader(fileName);
		BufferedReader bufferedReader = new BufferedReader(fileReader);
		String line = bufferedReader.readLine();
		line.trim();
		nombreEmplacement = Integer.valueOf(line);
		emplacements = new LinkedList<Emplacement>();
		for (int i = 0; i < nombreEmplacement; ++i)
		{
			line = bufferedReader.readLine();
			if (line == null)
			{
				bufferedReader.close();
				throw new IOException();
			}
			line.trim();
			String[] lineValues = line.split("\t");
			int id     = Integer.valueOf(lineValues[0]);
			int revenu = Integer.valueOf(lineValues[1]);
			int poulet = Integer.valueOf(lineValues[2]);
			emplacements.add(new Emplacement(id, revenu, poulet));
		}
		line = bufferedReader.readLine();
		line.trim();
		capaciteFournisseur = Integer.valueOf(line);
		bufferedReader.close();
	}
	
	public void print()
	{
		System.out.println("Ville :");
		System.out.println("nombre d'emplacement : " + String.valueOf(nombreEmplacement));
		for (Emplacement emplacement : emplacements)
		{
			System.out.format("id=%4d, revenu=%4d, poulet=%4d\n", emplacement.getId(), emplacement.getRevenu(), emplacement.getPoulet());
		}
		System.out.println("capacit� fournisseur : " + String.valueOf(capaciteFournisseur));
	}
	
	public void vorace(boolean print)
	{
		for (int i = 0; i < 10 ; ++i)
		{
			List<Emplacement> temp = new LinkedList<Emplacement>();
			List<Emplacement> refuser = new LinkedList<Emplacement>();
			int sommepoulet = 0;
			boolean done = false;
			while (sommepoulet < capaciteFournisseur)
			{
				float sommeRentabilite = 0;
				for (Emplacement emplacement : emplacements)
				{
					sommeRentabilite += emplacement.getRentabilite();
				}
				Random randomGenerator = new Random();
				float randomNumber = randomGenerator.nextFloat() * sommeRentabilite;
				float a = 0;
				for (Emplacement emplacement : emplacements)
				{
					a += emplacement.getRentabilite();
					if (randomNumber <= a)
					{
						sommepoulet += emplacement.getPoulet();
						if (sommepoulet > capaciteFournisseur)
						{
							sommepoulet -= emplacement.getPoulet();
							refuser.add(emplacement);
							emplacements.remove(emplacement);
							break;
						}
						temp.add(emplacement);
						emplacements.remove(emplacement);
						
						break;
					}
				}
				done = emplacements.size() == 0;
				
			}
		}
	}
	
	public void dynamique(boolean print)
	{
		
	}
	
	public void local(boolean print)
	{
		
	}
	
}
