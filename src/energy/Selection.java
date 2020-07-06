package energy;

import java.util.Scanner;

public class Selection {
	public interface Choice {
		String text();
	}
	
	static Scanner scan = new Scanner(System.in);
	
	static int makeSelection(Choice[] choices) {
		Printer.printlnLeft("Please choose from the following");
		for (int i = 0; i < choices.length; i++) {
//			if (i == 0)
//				Printer.printLeft(Format.ANSI_BOLD);
			Printer.printLeft((i + 1) + ": ");
			Printer.printLeft(choices[i].text());
//			if (i == 0)
//				Printer.printLeft(Format.ANSI_RESET);
			Printer.printlnLeft("");
		}
		Printer.flush();
		int index = -1;
		boolean first = true;
		while (index < 0 || index >= choices.length) {
			if (!first)
				System.out.println("Invalid option number, plese enter a valid option.");
			first = false;
			String line = scan.nextLine();
//			if (line.equals("")) {
//				index = 0;
//				break;
//			}
			try {
				index = Integer.parseInt(line) - 1;
			} catch (NumberFormatException e) {
				System.out.println("Failed to parse input; please enter only the number of the option you choose.");
			}
		}
		return index;
	}
}
