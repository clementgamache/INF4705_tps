import java.io.*;
import java.util.*;


public class MatriceCarree {
	
	public MatriceCarree(int taille) {
		int plusPetitCarre = 1;
		while(true) {
			plusPetitCarre *=2;
			if (taille <= plusPetitCarre)
				break;
		}
		this.n = plusPetitCarre;
		this.data = new int[this.n][this.n];
		
	}
	
	public MatriceCarree(String nomFichier) {
		//n est le contenu de la premiere ligne du fichier
		try {
			BufferedReader br = new BufferedReader(new FileReader(nomFichier));
			String line = br.readLine();
			line = br.readLine();
			line.trim();
			int lineNum = 0;
			String[] numbers = line.split("\t");
			this.n = numbers.length;
			this.data = new int[this.n][this.n];
			while (line != null)
			{
				for (int i = 0 ; i < this.n; ++i)
				{
					this.data[lineNum][i] = Integer.parseInt(numbers[i]);
				}
				line = br.readLine();
				if (line != null)
				{
					line.trim();
					numbers = line.split("\t");
				}
				lineNum++;
			}
			br.close();
				
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public void afficher() {
		System.out.println(Arrays.deepToString(data));
	}

	public MatriceCarree multConventionnel(MatriceCarree mat) {
		MatriceCarree res = new MatriceCarree(n);
		for (int i = 0 ; i < n ; i++) {
			for (int j = 0 ; j < n ; j++) {
				res.data[i][j] = 0;
				for (int k = 0 ; k < n ; k++) {
					res.data[i][j]  +=this.data[i][k] * mat.data[k][j];
				}
			}
		}
		return res;
	}
	
	public MatriceCarree getIndice(int indice) {
		int decalageHor, decalageVer;
		decalageHor = indice < 2 ? 0 : this.n/2;
		decalageVer = indice % 2 == 0 ? 0 : this.n/2;
		MatriceCarree res = new MatriceCarree(this.n/2);
		
		for (int i = 0 ; i < this.n/2 ; i++ ) {
			for (int j = 0 ; j < this.n/2 ; j++ ) {
				res.data[i][j] = this.data[i+decalageHor][j+decalageVer];
			}
		}
		
		return res;
	}
	
	public MatriceCarree additionner(MatriceCarree mat) {
		MatriceCarree res = new MatriceCarree(this.n);
		for (int i = 0 ; i < this.n ; i++) {
			for (int j = 0 ; j < this.n ; j++) {
				res.data[i][j] = this.data[i][j] + mat.data[i][j]; 
			}
		}
		return res;
	}
	
	public MatriceCarree soustraire(MatriceCarree mat) {
		MatriceCarree res = new MatriceCarree(this.n);
		for (int i = 0 ; i < this.n ; i++) {
			for (int j = 0 ; j < this.n ; j++) {
				res.data[i][j] = this.data[i][j] - mat.data[i][j]; 
			}
		}
		return res;
	}
	
	public MatriceCarree multDiv(MatriceCarree mat, int threshold) {
	  if (this.n <= threshold) return multConventionnel(mat);
	  MatriceCarree res = new MatriceCarree(this.n);
	  MatriceCarree a00 = this.getIndice(0);
	  MatriceCarree a01 = this.getIndice(1);
	  MatriceCarree a10 = this.getIndice(2);
	  MatriceCarree a11 = this.getIndice(3);
	  MatriceCarree b00 = mat.getIndice(0);
	  MatriceCarree b01 = mat.getIndice(1);
	  MatriceCarree b10 = mat.getIndice(2);
	  MatriceCarree b11 = mat.getIndice(3);

	  MatriceCarree m1 ,m2, m3, m4, m5, m6, m7;
	  m1 = a00.additionner(a11).multDiv(b00.additionner(b11), threshold);
	  m2 = a10.additionner(a11).multDiv(b00, threshold);
	  m3 = a00.multDiv(b01.soustraire(b11), threshold);
	  m4 = a11.multDiv(b10.soustraire(b00), threshold);
	  m5 = a00.additionner(a01).multDiv(b11, threshold);
	  m6 = a10.soustraire(a00).multDiv(b00.additionner(b01), threshold);
	  m7 = a01.soustraire(a11).multDiv(b10.additionner(b11), threshold);

	  MatriceCarree c1 ,c2, c3, c4;
	  c1 = m1.additionner(m4).soustraire(m5).additionner(m7);
	  c2 = m3.additionner(m5);
	  c3 = m2.additionner(m4);
	  c4 = m1.soustraire(m2).additionner(m3).additionner(m6);

	  int decalage = this.n/2;
	  for (int i = 0; i < decalage; i++){
	    for (int j = 0; j < decalage; j++){
	      res.data[i]           [j]            = c1.data[i][j];
	      res.data[i]           [j + decalage] = c2.data[i][j];
	      res.data[i + decalage][j]            = c3.data[i][j];
	      res.data[i + decalage][j + decalage] = c4.data[i][j];
	    }
	  }
		return res;
		
	}

	
	private int n;
	private int[][] data;
}
