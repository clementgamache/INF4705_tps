package tp3;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Groupe {
	private Enfant[] enfants;
	private Permission[] permissions;
	private int nbEnfants;
	private int nbPermissions;
	
	public Groupe(String nomFichier) throws IOException {
		try {
			readFile(nomFichier);
		}
		catch (IOException ex) {
			
		}
	}
	
	public void print() {
		String o = "";
		for (int i = 0 ; i < nbEnfants ; i++) {
			o += Integer.valueOf(enfants[i].grandeur).toString() + " ";
		}
		o +="\n";
		for (int i = 0 ; i < nbPermissions ; i++) {
			o += "(" + Integer.valueOf(permissions[i].precedent.id).toString() + ", " + Integer.valueOf(permissions[i].prochain.id).toString() + ") ";
		}
		System.out.println(o);
	}
	
	private void readFile(String nomFichier) throws IOException {
		FileReader fileReader = new FileReader(nomFichier);
		BufferedReader bufferedReader = new BufferedReader(fileReader);
		String line = bufferedReader.readLine();
		line.trim();
		nbEnfants = Integer.valueOf(line);
		line = bufferedReader.readLine();
		line.trim();
		nbPermissions = Integer.valueOf(line);
		for (int i = 0; i < nbEnfants; ++i)
		{
			line = bufferedReader.readLine();
			if (line == null)
			{
				bufferedReader.close();
				throw new IOException();
			}
			line.trim();
			enfants[i] = new Enfant(i, Integer.valueOf(line));
		}
		for (int i = 0; i < nbPermissions; ++i)
		{
			line = bufferedReader.readLine();
			if (line == null)
			{
				bufferedReader.close();
				throw new IOException();
			}
			line.trim();
			String[] lineValues = line.split("\\s+");
			int id1 = Integer.valueOf(lineValues[1]);
			int id2 = Integer.valueOf(lineValues[2]);
			permissions[i] = new Permission(enfants[id1-1], enfants[id2-1]);
		}
		bufferedReader.close();
	}
}
