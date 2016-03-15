package TP2;

import java.util.ArrayList;
import java.util.List;

public class Amelioration {
	private List<Emplacement> addons_;
	private List<Emplacement> removals_;
	private int gain_;
	private static List<Emplacement> currentSolution_;
	private static List<Emplacement> currentGarbage_;
	private static int jeu_;
	private static Amelioration bestAmelioration_;
	
	public Amelioration(List<Emplacement> addons, List<Emplacement> removals) {
		removals_ = new ArrayList<Emplacement>();
		addons_ = new ArrayList<Emplacement>();
		removals_.addAll(removals);
		addons_.addAll(addons);
		setGain();
	}
	
	public static boolean isNull() {
		return bestAmelioration_ == null;
	}
	
	public void setBestAmelioration() {
		bestAmelioration_ = this;
	}
	
	public static void updateSetting(List<Emplacement> currentSolution, List<Emplacement> currentGarbage, int jeu) {
		currentSolution_ = currentSolution;
		currentGarbage_ = currentGarbage;
		jeu_ = jeu;
		bestAmelioration_ = null;
	}
	
	private void setGain() {
		gain_ = 0;
		for (Emplacement addon : addons_) {
			gain_ += addon.getRevenu();
		}
		for (Emplacement removal : removals_) {
			gain_ -= removal.getRevenu();
		}
	}
	
	public static boolean allowed(Emplacement e) {
		int diffPoulet = e.getPoulet();
		int diffRevenu = e.getRevenu();
		return allowed(diffPoulet, diffRevenu);
	}
	public static boolean allowed(Emplacement addon, Emplacement removal) {
		int diffPoulet = addon.getPoulet() - removal.getPoulet();
		int diffRevenu = addon.getRevenu() - removal.getRevenu();
		return allowed(diffPoulet, diffRevenu);
	}
	public static boolean allowed(Emplacement addon1, Emplacement addon2, Emplacement removal1, Emplacement removal2) {
		int diffPoulet = addon1.getPoulet() - removal1.getPoulet() + addon2.getPoulet() - removal2.getPoulet();
		int diffRevenu = addon1.getRevenu() - removal1.getRevenu() + addon2.getRevenu() - removal2.getRevenu();
		return allowed(diffPoulet, diffRevenu);
	}
	public static boolean allowed(Emplacement addon, Emplacement removal, Emplacement hybrid, boolean isAddon) {
		int mod = isAddon ? 1 : -1;
		int diffPoulet = addon.getPoulet() - removal.getPoulet() + (mod * hybrid.getPoulet());
		int diffRevenu = addon.getRevenu() - removal.getRevenu() + (mod * hybrid.getRevenu());
		return allowed(diffPoulet, diffRevenu);
		
	}
	
	private static boolean allowed(int diffPoulet, int diffRevenu) {
		int toBeat = isNull() ? 0 : bestAmelioration_.gain_;
		return diffPoulet <= jeu_ && diffRevenu > toBeat;
	}
	
	
	
	
	
	public static void apply() {
		if (isNull()) return;
		for (Emplacement addon : bestAmelioration_.addons_) {
			currentSolution_.add(addon);
			currentGarbage_.remove(addon);
		}
		for (Emplacement removal : bestAmelioration_.removals_) {
			currentSolution_.remove(removal);
			currentGarbage_.add(removal);
		}
	}
}
