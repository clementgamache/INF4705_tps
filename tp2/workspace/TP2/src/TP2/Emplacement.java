package TP2;

import java.util.List;

public class Emplacement {
	private int id;
	private int revenu;
	private int poulet;
	
	public Emplacement(int id, int revenu, int poulet)
	{
		this.id = id;
		this.revenu = revenu;
		this.poulet = poulet;
	}
	public int getId()
	{
		return id;
	}
	public int getRevenu()
	{
		return revenu;
	}
	public int getPoulet()
	{
		return poulet;
	}
	public double getRentabilite()
	{
		return (double)revenu/(double)poulet;
	}
	
	public static int getConsommationTotale(List<Emplacement> emplacements) {
		int consommation = 0;
		for (Emplacement emplacement : emplacements) {
			consommation += emplacement.poulet;
		}
		
		return consommation;
	}
	
	public static int getRevenuTotal(List<Emplacement> emplacements) {
		int revenu = 0;
		for (Emplacement emplacement : emplacements) {
			revenu += emplacement.revenu;
		}
		
		return revenu;
	}
	
	public static double getRentabiliteTotale(List<Emplacement> emplacements) {
		double revenu, consommation;
		revenu = consommation = 0;
		for (Emplacement emplacement : emplacements) {
			revenu += emplacement.revenu;
			consommation += emplacement.poulet;
		}
		
		return consommation > 0 ? revenu / consommation : 0;
	}
	
	public static void printEmplacementsChoisis(List<Emplacement> emplacements) {
		System.out.println("Nombre d'emplacements choisis : " + Integer.valueOf(emplacements.size()).toString());
		String emp = "";
		for (Emplacement emplacement : emplacements) {
			emp += Integer.valueOf(emplacement.getId()).toString() + " ";
		}
		System.out.println("Emplacements choisis : " + emp);
		System.out.println("Revenu total : " + Double.valueOf(getRevenuTotal(emplacements)).toString());
		
	}
}
