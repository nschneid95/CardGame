package energy;

public class InvalidOption implements Option {
	public static InvalidOption val = new InvalidOption();
	
	private InvalidOption() {}

	@Override
	public ColoredString text() {
		return new ColoredString("Invalid option");
	}

	@Override
	public boolean isAllowed(Game game) {
		return false;
	}

	@Override
	public void execute(Game game) throws IllegalStateException {
		throw new IllegalStateException("Invalid option was called");
	}

}
