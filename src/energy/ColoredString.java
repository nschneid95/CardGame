package energy;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

class ColoredString {
	public static boolean enableColors = true;
	
	// Groups same colored pieces together. This will only be visible with colors
	// enabled on a console that doesn't support them.
	ColoredString consolidate() {
		if (pieces.size() < 2)
			return this;
		List<Piece> list = new LinkedList<Piece>();
		Color currColor = pieces.get(0).c;
		StringBuilder currStr = new StringBuilder();
		for (Piece p : pieces) {
			if (p.s.trim().length() == 0 || currColor == p.c)
				currStr.append(p.s);
			else {
				list.add(new Piece(currStr.toString(), currColor));
				currStr = new StringBuilder();
				currStr.append(p.s);
				currColor = p.c;
			}
		}
		list.add(new Piece(currStr.toString(), currColor));
		return new ColoredString(list);
	}
	
	static ColoredString join(ColoredString delimiter, Iterable<ColoredString> list) {
		List<Piece> pieces = new LinkedList<Piece>();
		boolean first = true;
		for (ColoredString s : list) {
			if (!first)
				pieces.addAll(delimiter.pieces);
			pieces.addAll(s.pieces);
			first = false;
		}
		return new ColoredString(pieces);
	}
	
	static ColoredString join(String delimiter, Iterable<ColoredString> list) {
		return join(new ColoredString(delimiter), list);
	}
	
	ColoredString(String s, Color c) {
		pieces = Arrays.asList(new Piece(s, c));
	}
	
	ColoredString(String s) {
		pieces = Arrays.asList(new Piece(s, Color.Default));
	}
	
	private ColoredString(List<Piece> pieces) {
		this.pieces = pieces;
	}
	
	ColoredString append(ColoredString cs) {
		List<Piece> list = new LinkedList<Piece>();
		list.addAll(pieces);
		list.addAll(cs.pieces);
		return new ColoredString(list);
	}
	
	ColoredString append(String s) {
		return append(new ColoredString(s));
	}
	
	ColoredString append(String s, Color c) {
		return append(new ColoredString(s, c));
	}
	
	ColoredString capitalizeFirst() {
		if (pieces.size() == 0)
			return this;
		List<Piece> list = new LinkedList<Piece>();
		Piece first = pieces.get(0);
		list.add(new Piece(first.s.substring(0, 1).toUpperCase() + first.s.substring(1), first.c));
		for (int i = 1; i < pieces.size(); i++)
			list.add(pieces.get(i));
		return new ColoredString(list);
	}
	
	int length() {
		return pieces.stream().mapToInt(x -> x.length()).sum();
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (Piece piece : pieces)
			sb.append(piece.toString());
		return sb.toString();
	}
	
	List<ColoredString> split(Character ch) {
		List<ColoredString> ret = new LinkedList<ColoredString>();
		List<Piece> currList = new LinkedList<Piece>();
		StringBuilder currStr = new StringBuilder();
		Color currStrColor = null;
		for (Piece p : pieces) {
			for (int cp : p.s.codePoints().toArray()) {
				if (cp == ch) {
					if (currStr.length() > 0)
						currList.add(new Piece(currStr.toString(), currStrColor));
					else // Add an empty string to represent multiple delimiters
						currList.add(new Piece("", Color.Default));
					if (currList.size() > 0)
						ret.add(new ColoredString(currList));
					currList = new LinkedList<Piece>();
					currStr = new StringBuilder();
				} else {
					if (currStrColor == p.c) {
						currStr.appendCodePoint(cp);
					} else {
						if (currStr.length() > 0)
							currList.add(new Piece(currStr.toString(), currStrColor));
						currStr = new StringBuilder();
						currStr.appendCodePoint(cp);
						currStrColor = p.c;
					}
				}
			}
		}
		if (currStr.length() > 0)
			currList.add(new Piece(currStr.toString(), currStrColor));
		if (!currList.isEmpty())
			ret.add(new ColoredString(currList));
		return ret;
	}
	
	public ColoredString reColor(Color oldColor, Color newColor) {
		List<Piece> newList = new LinkedList<Piece>();
		for (Piece p : pieces) {
			if (p.c == oldColor)
				newList.add(new Piece(p.s, newColor));
			else
				newList.add(p);
		}
		return new ColoredString(newList);
	}
	
	final private List<Piece> pieces;
	
	public static class Builder {
		void append(ColoredString cs) {
			pieces.addAll(cs.pieces);
		}
		
		void append(String s) {
			append(new ColoredString(s));
		}
		
		int length() {
			return pieces.stream().mapToInt(x -> x.length()).sum();
		}
		
		public String toString() {
			int[] arr = pieces.stream().map(x -> x.toString().codePoints()).flatMapToInt(Function.identity()).toArray();
			return new String(arr, 0, arr.length);
		}
		
		ColoredString toColoredString() {
			return new ColoredString(pieces);
		}
		
		private List<Piece> pieces = new LinkedList<Piece>();
	}
	
	private static class Piece {
		Piece(String s, Color c) {
			this.s = s;
			this.c = c;
			if (c == null)
				throw new IllegalArgumentException();
		}
		
		public String toString() {
			if (enableColors) {
				return colorCodes.get(c) + s + resetCode;
			} else {
				return s;
			}
		}
		
		int length() {
			return s.length();
		}
		
		final private String s;
		final private Color c;
		
		private final static String resetCode = "\u001B[0m";
		private static Map<Color, String> colorCodes = new HashMap<Color, String>();
		static {
			colorCodes.put(Color.Black, "\u001B[30m");
			colorCodes.put(Color.Red, "\u001B[31m");
			colorCodes.put(Color.Green, "\u001B[32m");
			colorCodes.put(Color.Yellow, "\u001B[33m");
			colorCodes.put(Color.Blue, "\u001B[34m");
			colorCodes.put(Color.Purple, "\u001B[35m");
			colorCodes.put(Color.Cyan, "\u001B[36m");
			colorCodes.put(Color.White, "\u001B[37m");
			colorCodes.put(Color.BrightBlack, "\u001B[30;1m");
			colorCodes.put(Color.BrightRed, "\u001B[31;1m");
			colorCodes.put(Color.BrightGreen, "\u001B[32;1m");
			colorCodes.put(Color.BrightYellow, "\u001B[33;1m");
			colorCodes.put(Color.BrightBlue, "\u001B[34;1m");
			colorCodes.put(Color.BrightPurple, "\u001B[35;1m");
			colorCodes.put(Color.BrightCyan, "\u001B[36;1m");
			colorCodes.put(Color.BrightWhite, "\u001B[37;1m");
			colorCodes.put(Color.Default, "");
		}
	}
}

enum Color {
	Black,
	Red,
	Green,
	Yellow,
	Blue,
	Purple,
	Cyan,
	White,
	BrightBlack,
	BrightRed,
	BrightGreen,
	BrightYellow,
	BrightBlue,
	BrightPurple,
	BrightCyan,
	BrightWhite,
	Default;
}