package TP2;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Emplacement implements Comparable<Emplacement>
{
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
		return (double) revenu / (double) poulet;
	}

	public static int getConsommationTotale(List<Emplacement> emplacements)
	{
		int consommation = 0;
		for (Emplacement emplacement : emplacements)
		{
			consommation += emplacement.poulet;
		}

		return consommation;
	}

	public static int getRevenuTotal(List<Emplacement> emplacements)
	{
		int revenu = 0;
		for (Emplacement emplacement : emplacements)
		{
			revenu += emplacement.revenu;
		}

		return revenu;
	}

	public static double getRentabiliteTotale(List<Emplacement> emplacements)
	{
		double revenu, consommation;
		revenu = consommation = 0;
		for (Emplacement emplacement : emplacements)
		{
			revenu += emplacement.revenu;
			consommation += emplacement.poulet;
		}

		return consommation > 0 ? revenu / consommation : 0;
	}

	public static void printEmplacementsChoisis(List<Emplacement> emplacements)
	{
		Collections.sort(emplacements);
		StringBuilder buffer = new StringBuilder();
		buffer.append("Emplacements choisis :");
		for (Emplacement emplacement : emplacements)
		{
			buffer.append(' ').append(emplacement.getId());
		}
		System.out.println(buffer.toString());
		System.out.println("Revenu total : " + getRevenuTotal(emplacements));
	}

	public int compareTo(Emplacement comp)
	{
		return this.id - comp.id;
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Emplacement other = (Emplacement) obj;
		if (id != other.id)
			return false;
		return true;
	}
}
