package tp3;

public class Permission {
	public Enfant precedent;
	public Enfant prochain;
	
	public Permission(Enfant prec, Enfant proc) {
		precedent = prec;
		prochain = proc;
	}
}
