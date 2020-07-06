package energy;

import java.util.Map;

final class Format {
//	public static final String ANSI_RESET = "\u001B[0m";
//	public static final String ANSI_BLACK = "\u001B[30m";
//	public static final String ANSI_RED = "\u001B[31m";
//	public static final String ANSI_GREEN = "\u001B[32m";
//	public static final String ANSI_YELLOW = "\u001B[33m";
//	public static final String ANSI_BLUE = "\u001B[34m";
//	public static final String ANSI_PURPLE = "\u001B[35m";
//	public static final String ANSI_CYAN = "\u001B[36m";
//	public static final String ANSI_WHITE = "\u001B[37m";
//	public static final String ANSI_BOLD = "\u001B[1m";
	
	public static final String ANSI_RESET = "";
	public static final String ANSI_BLACK = "";
	public static final String ANSI_RED = "";
	public static final String ANSI_GREEN = "";
	public static final String ANSI_YELLOW = "";
	public static final String ANSI_BLUE = "";
	public static final String ANSI_PURPLE = "";
	public static final String ANSI_CYAN = "";
	public static final String ANSI_WHITE = "";
	public static final String ANSI_BOLD = "";
	
	private static Map<Integer, String> rainbowMap = Map.of(
			0, ANSI_RED, 1, ANSI_YELLOW, 2, ANSI_GREEN, 3, ANSI_BLUE, 4, ANSI_PURPLE);
	
	public static String rainbow(String s) {
		StringBuilder sb = new StringBuilder();
		int color = 0;
		for (char c : s.toCharArray()) {
			sb.append(rainbowMap.get(color));
			color = (color + 1) % 5;
			sb.append(c);
		}
		sb.append(ANSI_RESET);
		return sb.toString();
	}
}
