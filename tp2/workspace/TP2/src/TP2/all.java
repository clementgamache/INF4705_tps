package TP2;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

public class all {
	public static void main(String [ ] args)
	{
		
		File folder = new File(System.getProperty("user.dir"));
		File[] listOfFiles = folder.listFiles();
		List<String> fileNames = new LinkedList<String>();
		for (int i = 0; i < listOfFiles.length; i++) {
		  File file = listOfFiles[i];
		  if (file.isFile() && file.getName().endsWith(".txt")) {
		    fileNames.add(file.getName());
		  } 
		}
		
		try
		{
			for (String fileName : fileNames) {
					
				Ville ville;
				long currentTime;
				double timeConv;
				ville = new Ville(fileName);
				String o = "";
				o += fileName + ";";
				o += ville.getStats();
				//vorace
				currentTime = System.currentTimeMillis();
				o += ville.vorace(false);
				timeConv = 0.001*(System.currentTimeMillis() - currentTime);
				o += Double.valueOf(timeConv) + ";";
				//dynamique
				currentTime = System.currentTimeMillis();
				o += ville.dynamique(false);
				timeConv = 0.001*(System.currentTimeMillis() - currentTime);
				o += Double.valueOf(timeConv) + ";";
				//local
				currentTime = System.currentTimeMillis();
				o += ville.local(false);
				timeConv = 0.001*(System.currentTimeMillis() - currentTime);
				o += Double.valueOf(timeConv);
				System.out.println(o);
				
			}
		}
			catch (Exception e)
			{
				System.out.println("Une erreur est survenue.");
				
				e.printStackTrace();
				return;
			}
	}

}
