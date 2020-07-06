package energy;

import java.util.Map;
import java.util.function.Supplier;

abstract class Format {
	abstract String ANSI_RESET();
	abstract String ANSI_BLACK();
	abstract String ANSI_RED();
	abstract String ANSI_GREEN();
	abstract String ANSI_YELLOW();
	abstract String ANSI_BLUE();
	abstract String ANSI_PURPLE();
	abstract String ANSI_CYAN();
	abstract String ANSI_WHITE();
	abstract String ANSI_BOLD();
	
	public static void enableColors() {
		obj = new Colors();
	}
	
	public static void disableColors() {
		obj = new NoColors();
	}
	
	static Format obj = new Colors();
	
	private Map<Integer, Supplier<String>> rainbowMap = Map.of(
			0, this::ANSI_RED,
			1, this::ANSI_YELLOW,
			2, this::ANSI_GREEN,
			3, this::ANSI_BLUE,
			4, this::ANSI_PURPLE);
	
	public String rainbow(String s) {
		StringBuilder sb = new StringBuilder();
		int color = 0;
		for (char c : s.toCharArray()) {
			sb.append(rainbowMap.get(color).get());
			color = (color + 1) % 5;
			sb.append(c);
		}
		sb.append(ANSI_RESET());
		return sb.toString();
	}
}

final class Colors extends Format {
	public String ANSI_RESET() { return "\u001B[0m"; }
	public String ANSI_BLACK() { return "\u001B[30m"; }
	public String ANSI_RED() { return "\u001B[31m"; }
	public String ANSI_GREEN() { return "\u001B[32m"; }
	public String ANSI_YELLOW() { return "\u001B[33m"; }
	public String ANSI_BLUE() { return "\u001B[34m"; }
	public String ANSI_PURPLE() { return "\u001B[35m"; }
	public String ANSI_CYAN() { return "\u001B[36m"; }
	public String ANSI_WHITE() { return "\u001B[37m"; }
	public String ANSI_BOLD() { return "\u001B[1m"; }
}

final class NoColors extends Format {
	public String ANSI_RESET() { return ""; }
	public String ANSI_BLACK() { return ""; }
	public String ANSI_RED() { return ""; }
	public String ANSI_GREEN() { return ""; }
	public String ANSI_YELLOW() { return ""; }
	public String ANSI_BLUE() { return ""; }
	public String ANSI_PURPLE() { return ""; }
	public String ANSI_CYAN() { return ""; }
	public String ANSI_WHITE() { return ""; }
	public String ANSI_BOLD() { return ""; }
}
