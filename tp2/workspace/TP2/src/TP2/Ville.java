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
		int[][] revenu = new int[emplacements.size()][capaciteFournisseur + 1];
		// Transforme emplacement en tableau pour acces rapide
		Emplacement[] emplacements = this.emplacements.toArray(new Emplacement[this.emplacements.size()]);

		// Assignons a l'emplacements 0 tous les poulets inutiles en configurant
		// le revenu de l'emplacement 0 pour tous nombre de poulet equal ou plus grand
		// que la demande locale.
		for (int j = emplacements[0].getPoulet(); j <= capaciteFournisseur; ++j)
		{
			revenu[0][emplacements[0].getPoulet()] = emplacements[0].getRevenu();
		}

		// Algorithm dynamique.
		for (int i = 1; i < emplacements.length; i++) // Pour chaque emplacement suivant.
		{
			for (int j = 0; j < emplacements[i].getPoulet(); ++j)
			{
				revenu[i][j] = revenu[i - 1][j];
			}
			for (int j = emplacements[i].getPoulet(); j <= capaciteFournisseur; j++)
			{
				revenu[i][j] = Math.max(emplacements[i].getRevenu() + revenu[i - 1][j - emplacements[i].getPoulet()], revenu[i - 1][j]);
			}
		}

		// Calcul de la propagation inverse pour retrouver les emplacements optimaux choisi.
		int i = emplacements.length - 1;
		int j = capaciteFournisseur;
		int currentValue = revenu[i][j];
		List<Emplacement> used = new LinkedList<Emplacement>();
		while (currentValue > 0)
		{
			if (i > 0 && revenu[i - 1][j] == currentValue)
			{
				i--;
			}
			// else if (j > 0 && )
			else
			{
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
		int compte = 0;
		Emplacement[]     emplacements = this.emplacements.toArray(new Emplacement[this.emplacements.size()]);
		List<Emplacement> voraceList   = calculerVorace();
		Set<Emplacement>  voraceSet    = new HashSet<Emplacement>(voraceList);
		//boolean[]         choisi       = new boolean[emplacements.length];
		List<Integer> utilises = new ArrayList<Integer>();
		List<Integer> nonUtilises = new ArrayList<Integer>();
		for (int i = 0; i < emplacements.length; ++i) 
			if (voraceSet.contains(emplacements[i])) utilises.add(Integer.valueOf(i));
			else nonUtilises.add(Integer.valueOf(i));
		// Emplacement.printEmplacementsChoisis(meilleureSolution);
		int consommation = Emplacement.getConsommationTotale(voraceList);
		int revenu       = Emplacement.getRevenuTotal(voraceList);
		boolean changement = true;
		while (changement)
		{
			compte++;
			changement                = false;
			int iChoisi               = -1;
			int jChoisi               = -1;
			int kChoisi               = -1;
			int lChoisi               = -1;
			int meilleurRevenu        = revenu;
			int meilleureConsommation = consommation;
			for (int i = 0; i < utilises.size(); ++i)
			{
				for (int j = i + 1; j < utilises.size(); ++j)
				{
					for (int k = 0; k < nonUtilises.size(); ++k)
					{
//						if (i == 0 && j == 1) {
//							//ajoute k
//							int nouveauRevenu = revenu + emplacements[nonUtilises.get(k)].getRevenu();
//							int nouvelleConsommation = consommation + emplacements[nonUtilises.get(k)].getPoulet();
//							if (nouveauRevenu > meilleurRevenu && nouvelleConsommation <= capaciteFournisseur)
//							{
//								iChoisi = -1;
//								jChoisi = -1;
//								kChoisi = k;
//								lChoisi = -1;
//								meilleurRevenu = nouveauRevenu;
//								meilleureConsommation = nouvelleConsommation;
//								changement = true;
//							}
//
//						}
						for (int l = k + 1; l < nonUtilises.size(); ++l) 
						{
							// Enleve i et j, ajoute k et l.
							int nouveauRevenu = revenu - emplacements[utilises.get(i)].getRevenu() - emplacements[utilises.get(j)].getRevenu() + emplacements[nonUtilises.get(k)].getRevenu() + emplacements[nonUtilises.get(l)].getRevenu();
							int nouvelleConsommation = consommation - emplacements[utilises.get(i)].getPoulet() - emplacements[utilises.get(j)].getPoulet() + emplacements[nonUtilises.get(k)].getPoulet() + emplacements[nonUtilises.get(l)].getPoulet();
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
						int nouveauRevenu = revenu - emplacements[utilises.get(i)].getRevenu() - emplacements[utilises.get(j)].getRevenu() + emplacements[nonUtilises.get(k)].getRevenu();
						int nouvelleConsommation = consommation - emplacements[utilises.get(i)].getPoulet() - emplacements[utilises.get(j)].getPoulet() + emplacements[nonUtilises.get(k)].getPoulet();
						if (nouveauRevenu > meilleurRevenu && nouvelleConsommation <= capaciteFournisseur)
						{
							System.out.println("ici");
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
				for (int k = 0; k < nonUtilises.size(); ++k)
				{
					for (int l = k + 1; l < nonUtilises.size(); ++l)
					{
						// Enleve i, ajoute k et l.
						int nouveauRevenu = revenu - emplacements[utilises.get(i)].getRevenu() + emplacements[nonUtilises.get(k)].getRevenu() + emplacements[nonUtilises.get(l)].getRevenu();
						int nouvelleConsommation = consommation - emplacements[utilises.get(i)].getPoulet() + emplacements[nonUtilises.get(k)].getPoulet() + emplacements[nonUtilises.get(l)].getPoulet();
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
					int nouveauRevenu = revenu - emplacements[utilises.get(i)].getRevenu() + emplacements[nonUtilises.get(k)].getRevenu();
					int nouvelleConsommation = consommation - emplacements[utilises.get(i)].getPoulet() + emplacements[nonUtilises.get(k)].getPoulet();
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
				if (iChoisi >= 0) {utilises.remove(Integer.valueOf(iChoisi)); nonUtilises.add(Integer.valueOf(iChoisi));};
				if (jChoisi >= 0) {utilises.remove(Integer.valueOf(jChoisi)); nonUtilises.add(Integer.valueOf(jChoisi));};
				if (kChoisi >= 0) {nonUtilises.remove(Integer.valueOf(kChoisi)); utilises.add(Integer.valueOf(kChoisi));};
				if (lChoisi >= 0) {nonUtilises.remove(Integer.valueOf(lChoisi)); utilises.add(Integer.valueOf(lChoisi));};
				revenu = meilleurRevenu;
				consommation = meilleureConsommation;
				System.out.println(Integer.valueOf(revenu));
				
				List<Emplacement> m = new LinkedList<Emplacement>();
				for (int i = 0; i < utilises.size(); ++i)  m.add(emplacements[utilises.get(i)]);
				Emplacement.printEmplacementsChoisis(m);
			}
		}

		System.out.println(Integer.valueOf(compte));
		List<Emplacement> meilleureSolution = new LinkedList<Emplacement>();
		for (int i = 0; i < utilises.size(); ++i)  meilleureSolution.add(emplacements[utilises.get(i)]);
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
