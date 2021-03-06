package TP2;

import java.io.*;
import java.util.*;

public class Ville
{

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
			int id = Integer.valueOf(lineValues[1]);
			int revenu = Integer.valueOf(lineValues[2]);
			int poulet = Integer.valueOf(lineValues[3]);
			emplacements.add(new Emplacement(id, revenu, poulet));
		}
		line = bufferedReader.readLine();
		line.trim();
		capaciteFournisseur = Integer.valueOf(line);
		bufferedReader.close();
	}

	public String getStats()
	{
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
			System.out.format("id=%4d, revenu=%4d, poulet=%4d\n", emplacement.getId(), emplacement.getRevenu(),
					emplacement.getPoulet());
		}
		System.out.println("capacit� fournisseur : " + String.valueOf(capaciteFournisseur));
	}

	public List<Emplacement> calculerVorace()
	{
		List<Emplacement> meilleursEmplacements = null;
		// compute 10 times
		for (int i = 0; i < 10; ++i)
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
				// la probabilite d'un emplacement sera sa rentabilite divisee
				// par sommerentabilite

				Random randomGenerator = new Random();
				double randomNumber = randomGenerator.nextDouble() * sommeRentabilite;
				double rentabiliteCumulee = 0;
				// emplacementUtilise est l<emplacement calcule
				Emplacement emplacementUtilise = null;
				for (Iterator<Emplacement> iter = emplacementsRestants.iterator(); iter.hasNext();)
				{
					Emplacement emplacement = iter.next();
					rentabiliteCumulee += emplacement.getRentabilite();
					if (randomNumber <= rentabiliteCumulee)
					{
						emplacementUtilise = emplacement;
						iter.remove();
						break;
					}
				}
				if (emplacementUtilise == null) continue; // In case of rounding error, retry.

				// Ajout de l<emplacement s'il est valide
				// System.out.println(Integer.valueOf(emplacementsRestants.size()).toString());
				if (pouletUtilise + emplacementUtilise.getPoulet() <= capaciteFournisseur)
				{
					pouletUtilise += emplacementUtilise.getPoulet();
					emplacementsUtilises.add(emplacementUtilise);
				}
				sommeRentabilite -= emplacementUtilise.getRentabilite();
			}
			if (meilleursEmplacements == null)
			{
				meilleursEmplacements = emplacementsUtilises;
			} else if (Emplacement.getRevenuTotal(emplacementsUtilises) > Emplacement.getRevenuTotal(meilleursEmplacements))
			{
				meilleursEmplacements = emplacementsUtilises;
			}
		}
		return meilleursEmplacements;
	}

	public String vorace(boolean print)
	{
		List<Emplacement> resultat = calculerVorace();
		if (print)
		{
			Emplacement.printEmplacementsChoisis(resultat);
		}
		return Emplacement.getRevenuTotal(resultat) + ";";

	}

	public List<Emplacement> calculerDynamique()
	{
		// revenu[i, j] est le revenu maximal pour combler une capacite de j avec
		// les i premiers emplacements
		int[][] revenu = new int[emplacements.size()][capaciteFournisseur+1];
		// Transforme emplacement en tableau pour acces rapide
		Emplacement[] emplacements = this.emplacements.toArray(new Emplacement[this.emplacements.size()]);

		

		//array[i, j] est le revenu maximal pour combler une capacite de j avec les i premiers emplacements
		Integer[][] array = new Integer[capaciteFournisseur+1][emplacements.length];
		for (int i = 0 ; i < capaciteFournisseur+1 ; i++) {
			for (int j = 0 ; j < emplacements.length; j++) {
				array[i][j] = 0;
			}
		}
		for (int j = 1 ; j < capaciteFournisseur + 1 ; j++) {
			if (emplacements[0].getPoulet() <= j) {
				array[j][0] = emplacements[0].getRevenu();
			}
			for (int i = 1 ; i < emplacements.length ; i++) {
				Emplacement emplacement = emplacements[i];
				int jmq = j - emplacement.getPoulet();
				int max1 = 0;
				if (jmq >= 0) {
					max1 = emplacement.getRevenu() + array[jmq][i-1];
				}
				array[j][i] = Math.max(max1, array[j][i-1]);
			}
		}
		int i = emplacements.length-1;
		int j = capaciteFournisseur;
		int currentValue = array[j][i];
		List<Emplacement> used = new LinkedList<Emplacement>(); 
		while (currentValue > 0) {
			if (i > 0 && array[j][i-1] == currentValue) {
				i--;
			}
			//else if (j > 0 && )
			else {
				Emplacement e = emplacements[i];
				used.add(e);
				currentValue -= e.getRevenu();
				j -= e.getPoulet();
			}
		}
		return used;
	}
	
	public String dynamique(boolean print)
	{
		List<Emplacement> used = calculerDynamique();
		if (print)
		{
			Emplacement.printEmplacementsChoisis(used);
		}
		return Integer.valueOf(Emplacement.getRevenuTotal(used)).toString() + ";";
	}

	public List<Emplacement> calculerLocal()
	{
		Emplacement[]     emplacements = this.emplacements.toArray(new Emplacement[this.emplacements.size()]);
		List<Emplacement> voraceList   = calculerVorace();
		Set<Emplacement>  voraceSet    = new HashSet<Emplacement>(voraceList);
		boolean[]         choisi       = new boolean[emplacements.length];
		for (int i = 0; i < emplacements.length; ++i) if (voraceSet.contains(emplacements[i])) choisi[i] = true;

		// Emplacement.printEmplacementsChoisis(meilleureSolution);
		int consommation = Emplacement.getConsommationTotale(voraceList);
		int revenu       = Emplacement.getRevenuTotal(voraceList);
		boolean changement = true;
		while (changement)
		{
			changement                = false;
			int iChoisi               = -1;
			int jChoisi               = -1;
			int kChoisi               = -1;
			int lChoisi               = -1;
			int meilleurRevenu        = revenu;
			int meilleureConsommation = consommation;
			for (int i = 0; i < emplacements.length; ++i) if (choisi[i])
			{
				for (int j = 0; j < i; ++j) if (choisi[j])
				{
					for (int k = 0; k < emplacements.length; ++k) if (!choisi[k])
					{
						for (int l = 0; l < k; ++l) if (!choisi[l])
						{
							// Enleve i et j, ajoute k et l.
							int nouveauRevenu = revenu - emplacements[i].getRevenu() - emplacements[j].getRevenu() + emplacements[k].getRevenu() + emplacements[l].getRevenu();
							int nouvelleConsommation = consommation - emplacements[i].getPoulet() - emplacements[j].getPoulet() + emplacements[k].getPoulet() + emplacements[l].getPoulet();
							if (nouveauRevenu > meilleurRevenu && nouvelleConsommation <= capaciteFournisseur)
							{
								iChoisi = i;
								jChoisi = j;
								kChoisi = k;
								lChoisi = l;
								meilleurRevenu = nouveauRevenu;
								meilleureConsommation = nouvelleConsommation;
								changement = true;
							}
						}

						// Enleve i et j, ajoute k.
						int nouveauRevenu = revenu - emplacements[i].getRevenu() - emplacements[j].getRevenu() + emplacements[k].getRevenu();
						int nouvelleConsommation = consommation - emplacements[i].getPoulet() - emplacements[j].getPoulet() + emplacements[k].getPoulet();
						if (nouveauRevenu > meilleurRevenu && nouvelleConsommation <= capaciteFournisseur)
						{
							iChoisi = i;
							jChoisi = j;
							kChoisi = k;
							lChoisi = -1;
							meilleurRevenu = nouveauRevenu;
							meilleureConsommation = nouvelleConsommation;
							changement = true;
						}
					}
				}
				for (int k = 0; k < emplacements.length; ++k) if (!choisi[k])
				{
					for (int l = 0; l < k; ++l) if (!choisi[l])
					{
						// Enleve i, ajoute k et l.
						int nouveauRevenu = revenu - emplacements[i].getRevenu() + emplacements[k].getRevenu() + emplacements[l].getRevenu();
						int nouvelleConsommation = consommation - emplacements[i].getPoulet() + emplacements[k].getPoulet() + emplacements[l].getPoulet();
						if (nouveauRevenu > meilleurRevenu && nouvelleConsommation <= capaciteFournisseur)
						{
							iChoisi = i;
							jChoisi = -1;
							kChoisi = k;
							lChoisi = l;
							meilleurRevenu = nouveauRevenu;
							meilleureConsommation = nouvelleConsommation;
							changement = true;
						}
					}

					// Enleve i, ajoute k.
					int nouveauRevenu = revenu - emplacements[i].getRevenu() + emplacements[k].getRevenu();
					int nouvelleConsommation = consommation - emplacements[i].getPoulet() + emplacements[k].getPoulet();
					if (nouveauRevenu > meilleurRevenu && nouvelleConsommation <= capaciteFournisseur)
					{
						iChoisi = i;
						jChoisi = -1;
						kChoisi = k;
						lChoisi = -1;
						meilleurRevenu = nouveauRevenu;
						meilleureConsommation = nouvelleConsommation;
						changement = true;
					}
				}
			}
			if (changement)
			{
				if (iChoisi >= 0) choisi[iChoisi] = false;
				if (jChoisi >= 0) choisi[jChoisi] = false;
				if (kChoisi >= 0) choisi[kChoisi] = true;
				if (lChoisi >= 0) choisi[lChoisi] = true;
				revenu = meilleurRevenu;
				consommation = meilleureConsommation;
			}
		}

		List<Emplacement> meilleureSolution = new LinkedList<Emplacement>();
		for (int i = 0; i < choisi.length; ++i) if (choisi[i]) meilleureSolution.add(emplacements[i]);
		return meilleureSolution;
	}

	public String local(boolean print)
	{
		List<Emplacement> meilleureSolution = calculerLocal();
		if (print)
		{
			print();
			Emplacement.printEmplacementsChoisis(meilleureSolution);
		}
		return Emplacement.getRevenuTotal(meilleureSolution) + ";";
	}

	public int getCapaciteFournisseur()
	{
		return capaciteFournisseur;
	}

	public int getNombreEmplacements()
	{
		return emplacements.size();
	}
}
