package TP3;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Stack;

public class Groupe {
	private static final String List = null;
	//enfants tries par numero
	private Enfant[] enfantsNumero;
	//enfants tries par grandeur
	private Enfant[] enfantsGrandeur;
	//position des enfants en ordre de grandeur
	private int[] positions;
	//Permissions triees par grandeur du premier enfant
	private Permission[] permissions;
	//adjacence des écoliers triés par grandeur
	private int[][] adjacence;
	//indices des adjacences
	private int[][] quickAdj;
	
	public Groupe(String nomFichier) throws IOException {
		try {
			readFile(nomFichier);
		}
		catch (IOException ex) {
			System.out.println(ex.getMessage());
		}
	}
	
	public void print() {
		String o = "";
		for (int i = 0 ; i < enfantsNumero.length ; i++) {
			o += Integer.valueOf(enfantsNumero[i].grandeur).toString() + " " + Integer.valueOf(enfantsGrandeur[i].grandeur).toString() + "\n";
		}
		o +="\n";
		System.out.println(o);
		for (int i = 0 ; i < enfantsNumero.length ; i++) {
			for (int j = 0 ; j < enfantsNumero.length ; j++) {
				System.out.print(adjacence[i][j]);
			}
			System.out.print("\n");
		}
	}
	
	public boolean estSolution(Permission[] disposition) {
		return disposition.length == enfantsNumero.length -1;
	}
	
	private boolean tryPush(int position, Stack<Integer> solution,
		Stack<Integer> removals,
		Stack<Integer> nbRemovals,
		int[] restants, int nbLoners) {
		if (solution.isEmpty()) {
			solution.push(position);
			return true;
		}
		int peek = solution.peek();
		if (adjacence[peek][position] == 0) {
			//System.out.println(1);
			return false;
		}
		if (restants[position] == 1 && solution.size() < enfantsNumero.length -1) {
			//System.out.println(2);
			return false;
		}
		if (solution.size() > 1) {	
			int possibleLoners = nbLoners;
			for (int second : quickAdj[peek]) if (position != second && adjacence[peek][second] == 1){
				if (restants[second] - 1 == 1) {
					if (++possibleLoners > 1) {
						//System.out.println(3);
						return false;
					}
				}
			}
		}

		solution.push(position);
		//System.out.println("Push :" + Integer.valueOf(enfantsGrandeur[position].id).toString());
		int compte = 0;
		for (int second : quickAdj[peek]) {
			if (adjacence[peek][second] == 1) {
				compte += 1;
				adjacence[peek][second] = 0;
				adjacence[second][peek] = 0;
				removals.push(second);
				removals.push(peek);
				if (--restants[second] == 1) {
					nbLoners++;
				}
			}
		}
		nbRemovals.push(compte);
		return true;
	}
	
	private int pop(Stack<Integer> solution,
		Stack<Integer> removals,
		Stack<Integer> nbRemovals,
		int[] restants, int nbLoners) {
		if (!nbRemovals.isEmpty()) {
			int n = nbRemovals.pop();
			for (int i = 0 ; i < n ; i++) {
				int i1 = removals.pop();
				int i2 = removals.pop();
				adjacence[i1][i2] = 1;
				adjacence[i2][i1] = 1;
				if (++restants[i1] == 2) {
					nbLoners--;
				}
			}
		}
		int pop = solution.pop();
		//System.out.println("Pop :" + Integer.valueOf(pop).toString());
		return pop;
	}
	
	public void naif() {
		Stack<Integer> solution = new Stack<Integer>();
		Stack<Integer> removals = new Stack<Integer>();
		Stack<Integer> nbRemovals = new Stack<Integer>();
		int[] restants = new int[enfantsNumero.length];
		for (int i = 0 ; i < enfantsNumero.length ; i++) {
			for (int j = 0 ; j < enfantsNumero.length ; j++) {
				if (adjacence[i][j] == 1) {
					restants[i]++;
				}
			}
		}
		int nbLoners = 0;
		int bestLoner = -1;
		for (int i = 0 ; i < restants.length ; i++) {
			if (restants[i] == 1) {
				nbLoners++;
				bestLoner = i;
			}
		}
		if (nbLoners > 0) {
			solution.push(bestLoner);
			nbLoners--;
		}
		else {
			solution.push(enfantsNumero.length-1);	
		}
		while (solution.size() < enfantsNumero.length) {
			int next = -1;
			for (int i = enfantsNumero.length -1 ; i >= 0 ; i--) {
				if (tryPush(i, solution, removals, nbRemovals, restants, nbLoners)) {
					next = i;
					break;
				}
			}
			while (next == -1) {
				int pos = pop(solution, removals, nbRemovals, restants, nbLoners);
				
				for (int i = pos-1 ; i >= 0 ; i-- ) {
					if (tryPush(i, solution, removals, nbRemovals, restants, nbLoners)) {
						next = i;
						break;
					}
				}
			}
				
		}
		for (int i = 0 ; i < enfantsNumero.length ; i++) {
			System.out.println(enfantsGrandeur[solution.pop()].id);
		}
		System.out.println("reussi :)");
	}
	
	private void readFile(String nomFichier) throws IOException {
		FileReader fileReader = new FileReader(nomFichier);
		BufferedReader bufferedReader = new BufferedReader(fileReader);
		String line = bufferedReader.readLine();
		line.trim();
		int nbEnfants = Integer.valueOf(line);
		line = bufferedReader.readLine();
		line.trim();
		int nbPermissions = Integer.valueOf(line);
		
		//Construction du tableau d'enfants et du tableau de grandeurs triees
		int max = 0;
		enfantsNumero = new Enfant[nbEnfants];
		for (int i = 0; i < enfantsNumero.length; ++i)
		{
			line = bufferedReader.readLine();
			if (line == null)
			{
				bufferedReader.close();
				throw new IOException();
			}
			line.trim();
			int val = Integer.valueOf(line);
			enfantsNumero[i] = new Enfant(i+1, val);
			max = Math.max(max, val);
		}
		
		//compte d'enfants qui ont chaque grandeur
		int[] grandeurs = new int[max+1];
		for (Enfant e : enfantsNumero) {
			grandeurs[e.grandeur] += 1;
		}
		
		//ordre associee a chaque grandeur, commence a 1
		int[] ordre = new int[max + 1];
		int compte = 1;
		for (int i = 0 ; i < grandeurs.length ; i++) {
			int g = grandeurs[i];
			if (g > 0) {
				ordre[i] = compte;
				compte += g;
				if (g > 1) {
					ordre[i] += 0xFFFFF*(g-1);
				}
			}
		}

		enfantsGrandeur = new Enfant[nbEnfants];
		for (Enfant e : enfantsNumero) {
			int g = e.grandeur;
			if (ordre[g] > 0) { //il existe au moins un enfant de grandeur i
				int pos = ordre[g];
				int indice = pos / 0xFFFFF  + pos % 0xFFFFF -1;
				if (pos > 0xFFFFF) {
					ordre[g] -= 0xFFFFF;
				}
				enfantsGrandeur[indice] = e;
			}
		}
		
		positions = new int[nbEnfants];
		for (int i = 0 ; i < enfantsGrandeur.length ; i++) {
			positions[enfantsGrandeur[i].id - 1] = i;
		}

		
		
		adjacence = new int[nbEnfants][nbEnfants];
		//permissions = new Permission[nbPermissions];
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
			int id1 = Integer.valueOf(lineValues[0]);
			int id2 = Integer.valueOf(lineValues[1]);
			//permissions[i] = new Permission(enfants[id1-1], enfants[id2-1]);
			adjacence[positions[id1-1]][positions[id2-1]] = 1;
			adjacence[positions[id2-1]][positions[id1-1]] = 1;
		}
		
		ArrayList<LinkedList<Integer>> l = new ArrayList<LinkedList<Integer>>(enfantsNumero.length);
		for (int i = 0; i < enfantsNumero.length; i++) {
			  l.add(new LinkedList<Integer>());
			}
		for (int i = 0 ; i < enfantsNumero.length ; i++) {
			for (int j = 0 ; j < enfantsNumero.length ; j++) {
				if (adjacence[i][j] == 1) {
					l.get(i).add(Integer.valueOf(j));
				}
			}
		}
		quickAdj = new int[nbEnfants][];
		for (int i = 0 ; i < quickAdj.length ; i++) {
			LinkedList<Integer> list = l.get(i);
			quickAdj[i] = new int[list.size()];
			for(int j = 0;j < list.size();j++) quickAdj[i][j] = list.get(j);
		}
		
		bufferedReader.close();
	}
}
