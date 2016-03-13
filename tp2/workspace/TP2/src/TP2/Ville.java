package TP2;

import java.io.*;
import java.util.*;

public class Ville {
	
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
		int nombreEmplacement = Integer.valueOf(line);
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
			String[] lineValues = line.split("\\s+");
			int id     = Integer.valueOf(lineValues[1]);
			int revenu = Integer.valueOf(lineValues[2]);
			int poulet = Integer.valueOf(lineValues[3]);
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
		System.out.println("nombre d'emplacement : " + String.valueOf(emplacements.size()));
		for (Emplacement emplacement : emplacements)
		{
			System.out.format("id=%4d, revenu=%4d, poulet=%4d\n", emplacement.getId(), emplacement.getRevenu(), emplacement.getPoulet());
		}
		System.out.println("capacit� fournisseur : " + String.valueOf(capaciteFournisseur));
	}
	
	private List<Emplacement> calculerVorace() {
		List<Emplacement> meilleursEmplacements = null;
		//compute 10 times
		for (int i = 0; i < 10 ; ++i)
		{
			List<Emplacement> emplacementsRestants = new LinkedList<Emplacement>();
			List<Emplacement> emplacementsUtilises = new LinkedList<Emplacement>();
			emplacementsRestants.addAll(emplacements);
			int pouletUtilise = 0;
			float sommeRentabilite = 0;
			for (Emplacement emplacement : emplacementsRestants)
			{
				sommeRentabilite += emplacement.getRentabilite();
			}
			while (pouletUtilise < capaciteFournisseur && emplacementsRestants.size() > 0)
			{
				//la probabilite d'un emplacement sera sa rentabilite divisee par sommerentabilite
				
				Random randomGenerator = new Random();
				float randomNumber = randomGenerator.nextFloat() * sommeRentabilite;
				float rentabiliteCumulee = 0;
				//emplacementUtilise est l<emplacement calcule
				Emplacement emplacementUtilise= null;
				for (Emplacement emplacement : emplacementsRestants)
				{
					rentabiliteCumulee += emplacement.getRentabilite();
					if (randomNumber <= rentabiliteCumulee)
					{
						emplacementUtilise = emplacement;
						break;
					}
				}

				//Ajout de l<emplacement s'il est valide
				if (pouletUtilise + emplacementUtilise.getPoulet() <= capaciteFournisseur) {
					pouletUtilise += emplacementUtilise.getPoulet();
					emplacementsUtilises.add(emplacementUtilise);
				}
				sommeRentabilite -= emplacementUtilise.getRentabilite();
				emplacementsRestants.remove(emplacementUtilise);
			}
			if (meilleursEmplacements == null) {
				meilleursEmplacements = emplacementsUtilises;
			}
			else if (Emplacement.getRevenuTotal(emplacementsUtilises) > Emplacement.getRevenuTotal(meilleursEmplacements)) {
				meilleursEmplacements = emplacementsUtilises;
			}
		}
		return meilleursEmplacements;
	}
	
	public void vorace(boolean print)
	{
		List<Emplacement> resultat = calculerVorace();
		if (print) {
			Emplacement.printEmplacementsChoisis(resultat);			
		}
		
	}
	
	public void dynamique(boolean print)
	{
		//array[i, j] est le revenu maximal pour combler une capacite de j avec les i premiers emplacements
		Integer[][] array = new Integer[capaciteFournisseur+1][emplacements.size()];
		for (int i = 0 ; i < capaciteFournisseur+1 ; i++) {
			for (int j = 0 ; j < emplacements.size(); j++) {
				array[i][j] = 0;
			}
		}
		for (int j = 1 ; j < capaciteFournisseur + 1 ; j++) {
			if (emplacements.get(0).getPoulet() <= j) {
				array[j][0] = emplacements.get(0).getRevenu();
			}
			for (int i = 1 ; i < emplacements.size() ; i++) {
				Emplacement emplacement = emplacements.get(i);
				int jmq = j - emplacement.getPoulet();
				int max1 = 0;
				if (jmq >= 0) {
					max1 = emplacement.getRevenu() + array[jmq][i-1];
				}
				array[j][i] = Math.max(max1, array[j][i-1]);
			}
		}
		int i = emplacements.size()-1;
		int j = capaciteFournisseur;
		int currentValue = array[j][i];
		List<Emplacement> used = new LinkedList<Emplacement>(); 
		while (currentValue > 0) {
			if (i > 0 && array[j][i-1] == currentValue) {
				i--;
			}
			//else if (j > 0 && )
			else {
				Emplacement e = emplacements.get(i);
				used.add(e);
				currentValue -= e.getRevenu();
				j -= e.getPoulet();
			}
		}
		if (print) {
			Emplacement.printEmplacementsChoisis(used);			
		}
		
	}
	
	public void local(boolean print)
	{
		List<Emplacement> meilleureSolution = calculerVorace();
		Emplacement.printEmplacementsChoisis(meilleureSolution);
		boolean changement = true;
		while (changement) {
			changement = false;
			for (int i = 0 ; i < meilleureSolution.size() ; i++) {
				for (int j = 0 ; j < emplacements.size() ; j++) {
					Emplacement eCourant = meilleureSolution.get(i);
					Emplacement eChange = emplacements.get(j);
					if (
							meilleureSolution.contains(eChange) || 
							eCourant.getRevenu() >= eChange.getRevenu() ||
							eChange.getPoulet() - eCourant.getPoulet() > capaciteFournisseur - Emplacement.getConsommationTotale(meilleureSolution)
							) 
					{}
					else {
						meilleureSolution.set(i, eChange);
						changement = true;
					}
				}
			}
			for (int i = 0 ; i < meilleureSolution.size() ; i++) {
				for (int j = 0 ; j < emplacements.size() ; j++) {
					for (int k = j + 1 ; k < emplacements.size(); k++) {
						
						Emplacement eCourant = meilleureSolution.get(i);
						Emplacement eChange1 = emplacements.get(j);
						Emplacement eChange2 = emplacements.get(k);
						if (
								meilleureSolution.contains(eChange1) || 
								meilleureSolution.contains(eChange2) ||
								eCourant.getRevenu() >= eChange1.getRevenu() + eChange2.getRevenu() ||
								eChange1.getPoulet() + eChange2.getPoulet() - eCourant.getPoulet() > capaciteFournisseur - Emplacement.getConsommationTotale(meilleureSolution)
								) 
						{}
						else {
							meilleureSolution.set(i, eChange1);
							meilleureSolution.add(eChange2);
							changement = true;
						}
					}
				}
			}
			for (int i = 0 ; i < meilleureSolution.size() ; i++) {
				for (int j = 0 ; j < meilleureSolution.size() ; j++) {
					for (int k = j + 1 ; k < emplacements.size(); k++) {
						
						Emplacement eCourant1 = meilleureSolution.get(i);
						Emplacement eCourant2 = meilleureSolution.get(j);
						Emplacement eChange = emplacements.get(k);
						if (
								meilleureSolution.contains(eChange) || 
								eCourant1.getRevenu() + eCourant2.getRevenu() >= eChange.getRevenu() ||
								eChange.getPoulet()- eCourant1.getPoulet() - eCourant2.getPoulet()> capaciteFournisseur - Emplacement.getConsommationTotale(meilleureSolution)
								) 
						{}
						else {
							meilleureSolution.set(i, eChange);
							meilleureSolution.remove(eCourant2);
							changement = true;
						}
					}
				}
			}
			for (int i = 0 ; i < meilleureSolution.size() ; i++) {
				for (int j = 0 ; j < meilleureSolution.size() ; j++) {
					for (int k = j + 1 ; k < emplacements.size(); k++) {
						for (int l = k + 1 ; l < emplacements.size() ; l++) {
							
							Emplacement eCourant1 = meilleureSolution.get(i);
							Emplacement eCourant2 = meilleureSolution.get(j);
							Emplacement eChange1 = emplacements.get(k);
							Emplacement eChange2 = emplacements.get(l);
							if (
									meilleureSolution.contains(eChange1) || 
									meilleureSolution.contains(eChange2) ||
									eCourant1.getRevenu() + eCourant2.getRevenu() >= eChange1.getRevenu() + eChange2.getRevenu() ||
											eChange1.getPoulet() + eChange2.getPoulet() - eCourant1.getPoulet() - eCourant2.getPoulet()> capaciteFournisseur - Emplacement.getConsommationTotale(meilleureSolution)
									) 
							{}
							else {
								meilleureSolution.set(i, eChange1);
								meilleureSolution.set(j, eChange2);
								changement = true;
							}	
						}
					}
				}
			}
		}
		if (print) {
			Emplacement.printEmplacementsChoisis(meilleureSolution);			
		}
	}
	
	
	
	
}
