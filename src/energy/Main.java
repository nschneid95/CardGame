package energy;

import java.util.Scanner;

public class Main {

	public static void main(String[] args) {
		// Enable or disable colors
		ColoredString.enableColors = true;
		System.out.println(new ColoredString(
				"Enable colors (Y/N)? (Type Y if this text looks green and you want to see colors)", Color.Green)
				.toString());
		Scanner scan = new Scanner(System.in);
		String input = scan.nextLine().toLowerCase();
		while (!"yes".equals(input) && !"no".equals(input) && !"y".equals(input) && !"n".equals(input)) {
			System.out.println("Please type either Y or N.");
			input = scan.nextLine().toLowerCase();
		}
		if ("no".equals(input) || "n".equals(input))
			ColoredString.enableColors = false;
		
		int width = 80;
		if (width > 71) {
			System.out.println("   ______                               _____            _             ");
			System.out.println("  |  ____|                             / ____|          (_)            ");
			System.out.println("  | |__   _ __   ___ _ __ __ _ _   _  | (___  _ __  _ __ _ _ __   __ _ ");
			System.out.println("  |  __| | '_ \\ / _ \\ '__/ _` | | | |  \\___ \\| '_ \\| '__| | '_ \\ / _` |");
			System.out.println("  | |____| | | |  __/ | | (_| | |_| |  ____) | |_) | |  | | | | | (_| |");
			System.out.println("  |______|_| |_|\\___|_|  \\__, |\\__, | |_____/| .__/|_|  |_|_| |_|\\__, |");
			System.out.println("                          __/ | __/ |        | |                  __/ |");
			System.out.println("                         |___/ |___/         |_|                 |___/ ");
			System.out.println();
		}
		// Single column mode
		Printer.setRightAlign(width);
		Printer.setIndent(0);
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
		Printer.setRightAlign(3 * width / 4);
		Printer.setIndent(3);
		
		System.out.println("Please enter a difficulty ranging from 1 (easiest) to 10 (hardest):");
		int difficulty = -1;
		boolean first = true;
		while (difficulty < 1 || difficulty > 10) {
			if (!first)
				System.out.println("Please enter a number between 1 and 10");
			input = scan.nextLine();
			try {
				difficulty = Integer.parseInt(input);
			} catch(NumberFormatException e) { }
			first = false;
		}
		
		Game game = new Game(difficulty);
		game.play();
		// Note that this is just for the compiler - game.play will kill the JVM when it's done running.
		// We can't call close earlier since that will close System.in.
		scan.close();
	}

}
