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
	
	public String getStats() {
		String o = "";
		o += Integer.valueOf(emplacements.size()).toString() + ";";
		o += Integer.valueOf(capaciteFournisseur).toString() + ";";
		return o;
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
			double sommeRentabilite = 0;
			for (Emplacement emplacement : emplacementsRestants)
			{
				sommeRentabilite += emplacement.getRentabilite();
			}
			while (pouletUtilise < capaciteFournisseur && emplacementsRestants.size() > 0)
			{
				//la probabilite d'un emplacement sera sa rentabilite divisee par sommerentabilite
				
				Random randomGenerator = new Random();
				double randomNumber = randomGenerator.nextDouble() * sommeRentabilite;
				double rentabiliteCumulee = 0;
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
				//System.out.println(Integer.valueOf(emplacementsRestants.size()).toString());
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
	
	public String vorace(boolean print)
	{
		List<Emplacement> resultat = calculerVorace();
		if (print) {
			Emplacement.printEmplacementsChoisis(resultat);			
		}
		return Integer.valueOf(Emplacement.getRevenuTotal(resultat)).toString() + ";";
		
	}
	
	public String dynamique(boolean print)
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
		return Integer.valueOf(Emplacement.getRevenuTotal(used)).toString() + ";";
	}
	
	public String local(boolean print)
	{
		List<Emplacement> meilleureSolution = calculerVorace();
		List<Emplacement> solutionPrecedente = calculerVorace();
		List<Emplacement> inutilises = new ArrayList<Emplacement>();
		inutilises.addAll(emplacements);
		for (Emplacement emplacement : meilleureSolution) {
			inutilises.remove(emplacement);
		}
		
		
		
		//Emplacement.printEmplacementsChoisis(meilleureSolution);
		boolean changement = true;
		long currentTime = System.currentTimeMillis();
		List<Emplacement> addons;
		List<Emplacement> removals;
		Amelioration nouvelleAmelioration;
		int jeu = 0;
		while (changement) {
			changement = false;
			jeu = capaciteFournisseur - Emplacement.getConsommationTotale(meilleureSolution);
			
			//Emplacement.printEmplacementsChoisis(meilleureSolution);
			
			Amelioration.updateSetting(meilleureSolution, inutilises, jeu);

				for (int j = 0 ; j < inutilises.size() ; j++) {
					Emplacement eChange1 = inutilises.get(j);
					//ajouter un nouveau
					if (Amelioration.allowed(eChange1)) {				
						addons = new ArrayList<Emplacement>();
						addons.add(eChange1);
						nouvelleAmelioration = new Amelioration(addons, new ArrayList<Emplacement>());
						nouvelleAmelioration.setBestAmelioration();
					}
					
					for (int i = 0 ; i < meilleureSolution.size() ; i++) {
						Emplacement eCourant1 = meilleureSolution.get(i);
						//Echanger 1 courant contre 1 nouveau
						if (Amelioration.allowed(eChange1, eCourant1)) {					
							addons = new ArrayList<Emplacement>();
							removals = new ArrayList<Emplacement>();
							addons.add(eChange1);
							removals.add(eCourant1);
							nouvelleAmelioration = new Amelioration(addons, removals);
							nouvelleAmelioration.setBestAmelioration();
						}
					
					
					for (int k = i + 1 ; k < meilleureSolution.size(); k++) {
						Emplacement eCourant2 = meilleureSolution.get(k);
						//echanger 2 courants contre 1 nouveau
						if (Amelioration.allowed(eChange1, eCourant1, eCourant2, false)) {					
							addons = new ArrayList<Emplacement>();
							removals = new ArrayList<Emplacement>();
							addons.add(eChange1);
							removals.add(eCourant1);
							removals.add(eCourant2);
							nouvelleAmelioration = new Amelioration(addons, removals);
							nouvelleAmelioration.setBestAmelioration();
						}
						
						for (int l = j + 1 ; l < inutilises.size() ; l++) {
							Emplacement eChange2 = inutilises.get(l);
							//Echanger 1 courant contre 2 nouveaux
							if (Amelioration.allowed(eChange1, eCourant1, eChange2, true)) {						
								addons = new ArrayList<Emplacement>();
								removals = new ArrayList<Emplacement>();
								addons.add(eChange1);
								addons.add(eChange2);
								removals.add(eCourant1);
								nouvelleAmelioration = new Amelioration(addons, removals);
								nouvelleAmelioration.setBestAmelioration();
							}
							
							//Echanger 2 courants contre 2 nouveaux
							if (Amelioration.allowed(eChange1, eChange2, eCourant1, eCourant2)) {						
								addons = new ArrayList<Emplacement>();
								removals = new ArrayList<Emplacement>();
								addons.add(eChange1);
								addons.add(eChange2);
								removals.add(eCourant1);
								removals.add(eCourant2);
								nouvelleAmelioration = new Amelioration(addons, removals);
								nouvelleAmelioration.setBestAmelioration();
							}
						}
					}
					double timeConv = 0.001*(System.currentTimeMillis() - currentTime);
					if (timeConv > 30) {
						Amelioration.apply();
						return Integer.valueOf(Emplacement.getRevenuTotal(meilleureSolution)).toString() + ";";
					}
				}
			}
			Amelioration.apply();
			if (!Amelioration.isNull()) changement = true;
		}

		
		if (print) {
			print();
			Emplacement.printEmplacementsChoisis(meilleureSolution);			
		}
		return Integer.valueOf(Emplacement.getRevenuTotal(meilleureSolution)).toString() + ";";
	}
	
	
	
	
}
