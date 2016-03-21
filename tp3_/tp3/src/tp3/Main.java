package tp3;

import java.io.IOException;

public class Main {
	
	public static void main() {
		String fileName = "66_99.0";
		Groupe groupe;
		try
		{
			groupe = new Groupe(fileName);
		}
		catch (IOException e)
		{
			System.out.println("Une erreur est survenue lors de la ceture du fichier.");
			e.printStackTrace();
			return;
		}
		groupe.print();
	}
}
