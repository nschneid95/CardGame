package energy;

class Printer {
	public static void printlnLeft(String s) {
		left.append(s);
		left.append('\n');
	}
	
	public static void printlnRight(String s) {
		right.append(s);
		right.append('\n');
	}
	
	public static void printLeft(String s) {
		left.append(s);
	}
	
	public static void printRight(String s) {
		right.append(s);
	}
	
	private static int visibleLength(StringBuilder sb) {
		boolean escaped = false;
		int len = 0;
		for (int i = 0; i < sb.length(); i++) {
			char c = sb.charAt(i);
			if (c == '\u001B')
				escaped = true;
			if (!escaped)
				len++;
			if (escaped && c == 'm')
				escaped = false;
		}
		return len;
	}
	
	private static int visibleLength(String s) {
		boolean escaped = false;
		int len = 0;
		for (int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);
			if (c == '\u001B')
				escaped = true;
			if (!escaped)
				len++;
			if (escaped && c == 'm')
				escaped = false;
		}
		return len;
	}
	
	private static String join(String l, String r) {
		StringBuilder sb = new StringBuilder(l);
		while (visibleLength(sb) < rightAlign)
			sb.append(' ');
		sb.append(r);
		return sb.toString();
	}
	
	public static void flush() {
		String[] leftLines = left.toString().split("\n");
		String[] rightLines = right.toString().split("\n");
		for (String l : leftLines)
			if (visibleLength(l) + 1 > rightAlign)
				rightAlign = visibleLength(l) + 1;
		int line = 0;
		for (; line < leftLines.length; line++) {
			if (line < rightLines.length) {
				System.out.println(join(leftLines[line], rightLines[line]));
			} else {
				System.out.println(leftLines[line]);
			}
		}
		for (; line < rightLines.length; line++) {
			System.out.println(join("", rightLines[line]));
		}
		left = new StringBuilder();
		right = new StringBuilder();
	}
	
	static StringBuilder left = new StringBuilder();
	static StringBuilder right = new StringBuilder();
	static int rightAlign = 120;
}
