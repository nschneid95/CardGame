package energy;

import java.util.Optional;
import java.util.Scanner;

public class Main {
	
	private static Scanner scan = new Scanner(System.in);
	
	private static boolean chooseYesNo(ColoredString prompt) {
		Printer.printlnLeft(prompt);
		Printer.flush();
		String input = scan.nextLine().toLowerCase();
		while (!"yes".equals(input) && !"no".equals(input) && !"y".equals(input) && !"n".equals(input)) {
			Printer.printlnLeft("Please type either Y or N.");
			Printer.flush();
			input = scan.nextLine().toLowerCase();
		}
		switch (input) {
		case "yes":
		case "y":
			return true;
		case "no":
		case "n":
			return false;
		default:
			throw new RuntimeException("Corruption? " + input);
		}
	}
	
	private static boolean chooseYesNo(String prompt) {
		return chooseYesNo(new ColoredString(prompt));
	}
	
	private static Optional<Integer> chooseInt(String prompt, String error) {
		if (prompt != null && prompt.length() > 0)
			System.out.println(prompt);
		while (true) {
			String input = scan.nextLine();
			if ("".equals(input))
				return Optional.empty();
			try {
				return Optional.of(Integer.parseInt(input));
			} catch(NumberFormatException e) {
				System.out.println(error);
			}
		}
	}

	public static void main(String[] args) {
		// Don't try to wrap text until we determine a size. For now let the console wrap for us.
		Printer.setSingleCol(true);
		Printer.setWidth(800);
		Printer.setIndent(0);
		// Choose width
		int width = 80;
		Optional<Integer> maybeWidth = chooseInt(
				"If you already know your window width, enter it here. Otherwise click return",
				"Please enter a number or an empty line.");
		if (maybeWidth.isPresent()) {
			width = maybeWidth.get();
		} else {
			// Let the user figure out the width
			int lower = 0;
			int upper = -1;
			while (width != lower) {
				System.out.println("=".repeat(width));
				if (chooseYesNo("Does the above bar fit on one line (Y/N)?")) {
					lower = width;
					if (upper > 0)
						width = (upper + width) / 2;
					else
						width *= 2;
				} else {
					upper = width;
					width = (lower + width) / 2;
				}
			}
			System.out.println("Chosen width: " + width);
		}
		Printer.setWidth(width);
		
		// Enable or disable colors
		ColoredString.enableColors = true;
		ColoredString.enableColors = chooseYesNo(new ColoredString(
				"Enable colors (Y/N)? (Type Y if this text looks green and you want to see colors)",
				Color.Green));

		// Introduction
		if (width > 71) {
			Printer.printlnLeft("   ______                               _____            _             ");
			Printer.printlnLeft("  |  ____|                             / ____|          (_)            ");
			Printer.printlnLeft("  | |__   _ __   ___ _ __ __ _ _   _  | (___  _ __  _ __ _ _ __   __ _ ");
			Printer.printlnLeft("  |  __| | '_ \\ / _ \\ '__/ _` | | | |  \\___ \\| '_ \\| '__| | '_ \\ / _` |");
			Printer.printlnLeft("  | |____| | | |  __/ | | (_| | |_| |  ____) | |_) | |  | | | | | (_| |");
			Printer.printlnLeft("  |______|_| |_|\\___|_|  \\__, |\\__, | |_____/| .__/|_|  |_|_| |_|\\__, |");
			Printer.printlnLeft("                          __/ | __/ |        | |                  __/ |");
			Printer.printlnLeft("                         |___/ |___/         |_|                 |___/ ");
		} else {
			Printer.printlnLeft("");
			Printer.printlnLeft("Energy Spring", Color.BrightWhite);
		}
		Printer.printlnLeft("");
		Printer.printlnLeft(
				"You live in a world where magic and spirits are commonplace. " +
				"The galactic government has descended into complete capitalism and authoritarianism: " +
				"using the resources of the galaxy to satisfy the whims of the few chosen elite while " +
				"the majority is left to starve and suffer. You work for a heroic group of rebels that " +
				"fights for freedom.");
		Printer.printlnLeft("");
		Printer.printlnLeft(
				"As most know, energy comes in four \"flavors\" or types: Water, Earth, Air, and Fire. " +
				"By themselves, these energies have little intereaction with the physical world. However " +
				"there are complex spells that can use these energies to modify the physical world. Simple " +
				"spells such as heating a cup of water are relativly wide known, but more intensive spells " +
				"such as constructing an earthen wall or even condensing fire energy into a physical fireball " +
				"exist.");
		Printer.printlnLeft("");
		Printer.printlnLeft(
				"All four flavors of energy come from the same source: the interaction of the spirit plane with" +
				"the physical plane. In fact, it has recently been discovered that there is a fifth flavor" +
				"of energy: a so-called Raw flavor. It is theorized that this is the energy produced by the" +
				"interaction of the two planes but it quickly gets converted into the four everyday flavors." +
				"Thus this Raw energy is normally practically undetectable. However, there have been rumours " +
				"that the government has found an Energy Spring: a rip in the fabric dividing the two planes. " +
				"Such a place would constantly generate immense amounts of Raw energy that could be " + 
				"harnessed to do unthinkable things, similar to the mythological Energy Channels and the " +
				"fabled destruction they caused. This must not fall into the hands of the goverment!");
		Printer.printlnLeft("");
		Printer.printlnLeft(
				"We recently intercepted a communication that confirms that the rumours are true and " +
				"lists the whereabouts of the Energy Spring. A small base has already been set up there to " +
				"safeguard this power from the government. You are the only person we trust enough to lead " +
				"this essential operation. Even though we got there first and have seized control of the " +
				"Energy Spring, the government will be setting up their own base as soon as they can. " +
				"Thankfully the Spring is in a remote location and it will take the government some time " +
				"to get fully up and running. Be warned, however, that they cannot be underestimated!");
		Printer.printlnLeft("");
		Printer.printlnLeft(
				"Once you arrive at the base you will be in charge of managing weekly operations. Our engineers " +
				"have designed containers capable of containing the large amounts of energy you will be dealing with. " +
				"Our scientists have invented a new process that can refine Raw energy into normal flavors; this " +
				"should let you harness the power of the Energy Spring. They have also theorized a process to " +
				"\"enhance\" the Energy Spring: by reinforcing the tear with a balanced energy support we should be " +
				"able to increase the amount of Raw Energy that is produced. We have included in your travel supplies " +
				"instructions for two simple spells that can help protect our base and attack the government's; " +
				"any other spells you need will have to be researched and invented on site. There is a large " +
				"magic university on a nearby desest planet; by converting some Water Energy into water you should " +
				"be able to recruit their help researching new spells. Finally, we have pooled together all of our " +
				"resources to hire the services of a powerful air spirit. This spirit has agreed to accompany you " +
				"and will grant you incredibly powerful prayers in exchange for a suitable sacrifice of Air energy.");
		Printer.printlnLeft("");
		Printer.printlnLeft("Good luck. You are our last hope.");
		Printer.flush();
		Printer.setSingleCol(false);
		Printer.setIndents(3, 1);
		
		System.out.println("Please enter a difficulty ranging from 1 (easiest) to 10 (hardest):");
		Optional<Integer> difficulty = Optional.empty();
		boolean first = true;
		while (difficulty.isEmpty() || difficulty.get() < 1 || difficulty.get() > 10) {
			if (!first)
				System.out.println("Please enter a number between 1 and 10");
			difficulty = chooseInt("", "Please enter a number between 1 and 10");
			first = false;
		}
		
		Game game = new Game(difficulty.get());
		game.play();
	}

}
