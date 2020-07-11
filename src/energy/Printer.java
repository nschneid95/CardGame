package energy;

import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

class Printer {
	public static void printlnLeft(ColoredString s) {
		left.append(s);
		left.append("\n");
	}
	
	public static void printlnLeft(String s) {
		left.append(s);
		left.append("\n");
	}
	
	public static void printlnLeft(String s, Color c) {
		printlnLeft(new ColoredString(s, c));
	}
	
	public static void printlnRight(ColoredString s) {
		right.append(s);
		right.append("\n");
	}
	
	public static void printlnRight(String s) {
		right.append(s);
		right.append("\n");
	}
	
	public static void printlnRight(String s, Color c) {
		printlnRight(new ColoredString(s, c));
	}
	
	public static void printLeft(ColoredString s) {
		left.append(s);
	}
	
	public static void printLeft(String s) {
		left.append(s);
	}
	
	public static void printLeft(String s, Color c) {
		printLeft(new ColoredString(s, c));
	}
	
	public static void printRight(ColoredString s) {
		right.append(s);
	}
	
	public static void printRight(String s) {
		right.append(s);
	}
	
	public static void printRight(String s, Color c) {
		printRight(new ColoredString(s, c));
	}
	
	private static ColoredString join(ColoredString l, ColoredString r) {
		return l.append(new ColoredString(new String(" ".repeat(rightAlign - l.length())))).append(r);
	}
	
	private static ColoredString pad(ColoredString r) {
		return new ColoredString(new String(" ".repeat(rightAlign))).append(r);
	}
	
	private static List<ColoredString> wrapLine(ColoredString line) {
		if (line.length() < rightAlign)
			return List.of(line);
		List<ColoredString> ret = new LinkedList<ColoredString>();
		List<ColoredString> curr = new LinkedList<ColoredString>();
		int currLength = 0;
		List<ColoredString> parts = line.split(' ');
		for (ColoredString part : parts) {
			if (part.length() + 1 + currLength < rightAlign) {
				curr.add(part);
				currLength += 1 + part.length();
			} else {
				ret.add(ColoredString.join(" ", curr));
				curr = new LinkedList<ColoredString>();
				if (indent > 0)
					curr.add(new ColoredString(" ".repeat(indent)));
				curr.add(part);
				currLength = part.length();
			}
		}
		ret.add(ColoredString.join(" ", curr));
		return ret;
	}
	
	public static void flush() {
		List<ColoredString> leftLines = left.toColoredString().split('\n');
		List<ColoredString> rightLines = right.toColoredString().split('\n');
		
		ListIterator<ColoredString> itr = leftLines.listIterator();
		while (itr.hasNext()) {
			ColoredString curr = itr.next();
			itr.remove();
			for (ColoredString cs : wrapLine(curr))
				itr.add(cs);
		}
		int line = 0;
		for (; line < leftLines.size(); line++) {
			if (line < rightLines.size()) {
				System.out.println(join(leftLines.get(line), rightLines.get(line)));
			} else {
				System.out.println(leftLines.get(line));
			}
		}
		for (; line < rightLines.size(); line++) {
			System.out.println(pad(rightLines.get(line)));
		}
		left = new ColoredString.Builder();
		right = new ColoredString.Builder();
	}
	
	public static void setRightAlign(int newAlign) {
		if (left.length() != 0 || right.length() != 0)
			throw new IllegalStateException("Cannot change the alignment with pending text!");
		rightAlign = newAlign;
	}
	
	public static void setIndent(int newIndent) {
		if (left.length() != 0 || right.length() != 0)
			throw new IllegalStateException("Cannot change the alignment with pending text!");
		indent = newIndent;
	}
	
	private static ColoredString.Builder left = new ColoredString.Builder();
	private static ColoredString.Builder right = new ColoredString.Builder();
	private static int rightAlign = 60;
	private static int indent = 3;
}
