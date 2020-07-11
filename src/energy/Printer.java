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
		printRight(s);
		printRight("\n");
	}
	
	public static void printlnRight(String s) {
		printRight(s);
		printRight("\n");
	}
	
	public static void printlnRight(String s, Color c) {
		printRight(s, c);
		printRight("\n");
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
		if (singleCol) {
			throw new IllegalStateException("Cannot print to right side while using single column mode!");
		}
		right.append(s);
	}
	
	public static void printRight(String s) {
		printRight(new ColoredString(s));
	}
	
	public static void printRight(String s, Color c) {
		printRight(new ColoredString(s, c));
	}
	
	public static void printBanner(String s) {
		if (left.length() > 0 || right.length() > 0)
			throw new IllegalStateException("Cannot print a banner with pending text!");
		int padding = (width - s.length() - 2) / 2;
		System.out.print("=".repeat(padding));
		System.out.print(" ");
		System.out.print(s);
		System.out.print(" ");
		System.out.println("=".repeat(padding));
	}
	
	private static ColoredString join(ColoredString l, ColoredString r) {
		StringBuilder pad = new StringBuilder();
		for (int i = l.length(); i < leftWidth(); i++)
			pad.append(' ');
		return l.append(pad.toString()).append(r);
	}
	
	private static ColoredString pad(ColoredString r) {
		StringBuilder pad = new StringBuilder();
		for (int i = 0; i < leftWidth(); i++)
			pad.append(' ');
		return new ColoredString(pad.toString()).append(r);
	}
	
	private static List<ColoredString> wrapLine(ColoredString line, int maxWidth, int indent) {
		if (line.length() < maxWidth)
			return List.of(line);
		List<ColoredString> ret = new LinkedList<ColoredString>();
		List<ColoredString> curr = new LinkedList<ColoredString>();
		int currLength = 0;
		List<ColoredString> parts = line.split(' ');
		for (ColoredString part : parts) {
			if (part.length() + 1 + currLength < maxWidth) {
				curr.add(part);
				currLength += 1 + part.length();
			} else {
				ret.add(ColoredString.join(" ", curr));
				curr = new LinkedList<ColoredString>();
				StringBuilder b = new StringBuilder();
				for (int i = 0; i < indent; i++)
					b.append(' ');
				if (indent > 0)
					curr.add(new ColoredString(b.toString()));
				curr.add(part);
				currLength = indent + part.length();
			}
		}
		ret.add(ColoredString.join(" ", curr));
		return ret;
	}
	
	public static void flush() {
		List<ColoredString> leftLines = left.toColoredString().split('\n');
		List<ColoredString> rightLines = right.toColoredString().split('\n');
		
		// Wrap left text
		ListIterator<ColoredString> itr = leftLines.listIterator();
		while (itr.hasNext()) {
			ColoredString curr = itr.next();
			itr.remove();
			for (ColoredString cs : wrapLine(curr, leftWidth(), leftIndent))
				itr.add(cs);
		}
		
		// Wrap right text
		itr = rightLines.listIterator();
		while (itr.hasNext()) {
			ColoredString curr = itr.next();
			itr.remove();
			for (ColoredString cs : wrapLine(curr, rightWidth(), rightIndent))
				itr.add(cs);
		}
		int line = 0;
		for (; line < leftLines.size(); line++) {
			if (line < rightLines.size()) {
				System.out.println(join(leftLines.get(line), rightLines.get(line)).consolidate());
			} else {
				System.out.println(leftLines.get(line).consolidate());
			}
		}
		for (; line < rightLines.size(); line++) {
			System.out.println(pad(rightLines.get(line)).consolidate());
		}
		left = new ColoredString.Builder();
		right = new ColoredString.Builder();
	}
	
	public static void setWidth(int newWidth) {
		if (left.length() != 0 || right.length() != 0)
			throw new IllegalStateException("Cannot change the alignment with pending text!");
		width = newWidth;
	}
	
	public static void setIndents(int lIndent, int rIndent) {
		if (left.length() != 0 || right.length() != 0)
			throw new IllegalStateException("Cannot change the alignment with pending text!");
		leftIndent = lIndent;
		rightIndent = rIndent;
	}
	
	public static void setIndent(int lIndent) {
		if (!singleCol)
			throw new IllegalStateException("Must set both indents unless in single column mode!");
		leftIndent = lIndent;
	}
	
	public static void setSingleCol(boolean singleCol) {
		Printer.singleCol = singleCol;
	}
	
	private static int leftWidth() {
		return singleCol ? width : width * 3 / 4;
	}
	
	private static int rightWidth() {
		assert !singleCol;
		return width - leftWidth();
	}
	
	private static ColoredString.Builder left = new ColoredString.Builder();
	private static ColoredString.Builder right = new ColoredString.Builder();
	private static int width = 80;
	private static int leftIndent = 3;
	private static int rightIndent = 1;
	private static boolean singleCol = false;
}
