
public class strassenSeuil1
{
	public static void main(String[] args)
	{
		String name1 = "";
		String name2 = "";
		boolean print = false;
		for (int i = 0; i < args.length; i++)
		{
			if (args[i].equals("-f"))
			{
				name1 = args[++i];
				name2 = args[++i];

			}
			else if (args[i].equals("-p"))
			{
				print = true;
			}
		}
		MatriceCarree mat1 = new MatriceCarree(name1);
		MatriceCarree mat2 = new MatriceCarree(name2);
		long currentTime = System.currentTimeMillis();
		MatriceCarree mat3 = mat1.multDiv(mat2, 2);
		double timestrassen = 0.001*(System.currentTimeMillis() - currentTime);
		System.out.format("Temps de cacul pour la multiplcation avec le seuil 1 (n=1, 2x2) : %8.3f", timestrassen);
		if (print) mat3.afficher();
	}
}
