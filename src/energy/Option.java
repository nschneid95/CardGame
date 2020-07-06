package energy;

interface Option extends Selection.Choice {
	String text();
	boolean isAllowed(Game game);
	void execute(Game game) throws IllegalStateException;
}