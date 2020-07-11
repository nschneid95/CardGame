package energy;

interface Option extends Selection.Choice {
	ColoredString text();
	boolean isAllowed(Game game);
	void execute(Game game) throws IllegalStateException;
}