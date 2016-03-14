package TP2;

import java.util.ArrayList;
import java.util.List;

public class Amelioration {
	private List<Emplacement> addons_;
	private List<Emplacement> removals_;
	private int gain_;
	private static List<Emplacement> currentSolution_;
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
		if (bestAmelioration_ == null || this.gain_ > bestAmelioration_.gain_){
			bestAmelioration_ = this;
		}
	}
	
	public static void updateSetting(List<Emplacement> currentSolution, int jeu) {
		currentSolution_ = currentSolution;
		jeu_ = jeu;
		bestAmelioration_ = null;
	}
	
	private boolean isAllowed() {
		int difference = 0;
		int nouveauJeu = jeu_;
		for (Emplacement addon : addons_) {
			if (currentSolution_.contains(addon)) return false;
			difference += addon.getRevenu();
			nouveauJeu -= addon.getPoulet();
		}
		for (Emplacement removal : removals_) {
			difference -= removal.getRevenu();
			nouveauJeu += removal.getPoulet();
		}
		if (difference <= 0) return false;
		if (nouveauJeu < 0) return false;
		return true;
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
		return e.getPoulet() <= jeu_ && !currentSolution_.contains(e);
	}
	public static boolean allowed(Emplacement addon, Emplacement removal) {
		return addon.getPoulet() - removal.getPoulet() <= jeu_ &&
				!currentSolution_.contains(addon) &&
				addon.getRevenu() > removal.getRevenu();
	}
	public static boolean allowed(Emplacement addon1, Emplacement addon2, Emplacement removal1, Emplacement removal2) {
		return addon1.getPoulet() + addon2.getPoulet() - removal1.getPoulet() - removal2.getPoulet() <= jeu_ &&
				!currentSolution_.contains(addon1) &&
				!currentSolution_.contains(addon2) &&
				addon1.getRevenu() + addon2.getRevenu() > removal1.getRevenu() + removal2.getRevenu();
	}
	public static boolean allowed(Emplacement addon, Emplacement removal, Emplacement hybrid, boolean isAddon) {
		if (isAddon) {
			return addon.getPoulet() + hybrid.getPoulet()- removal.getPoulet() <= jeu_ &&
					!currentSolution_.contains(addon) &&
					!currentSolution_.contains(hybrid) &&
					addon.getRevenu() + hybrid.getRevenu() > removal.getRevenu();
		}
		else {
			return addon.getPoulet() - removal.getPoulet() - hybrid.getPoulet() <= jeu_ &&
					!currentSolution_.contains(addon) &&
					addon.getRevenu() > removal.getRevenu() + hybrid.getRevenu();
		}
	}
	
	public static void apply() {
		if (isNull()) return;
		for (Emplacement addon : bestAmelioration_.addons_) {
			currentSolution_.add(addon);
		}
		for (Emplacement removal : bestAmelioration_.removals_) {
			currentSolution_.remove(removal);
		}
	}
}
