package TP2;

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
	public float getRentabilite()
	{
		return (float)revenu/(float)poulet;
	}
}
