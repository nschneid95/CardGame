package energy;

import java.util.Scanner;

public class Main {

	public static void main(String[] args) {
		// Enable or disable colors
		Format.enableColors();
		System.out.println(Format.obj.ANSI_GREEN()
				+ "Enable colors (Y/N)? (Type Y if this text looks green and you want to see colors)"
				+ Format.obj.ANSI_RESET());
		Scanner scan = new Scanner(System.in);
		String input = scan.nextLine().toLowerCase();
		while (!"yes".equals(input) && !"no".equals(input) && !"y".equals(input) && !"n".equals(input)) {
			System.out.println("Please type either Y or N.");
			input = scan.nextLine().toLowerCase();
		}
		if ("no".equals(input) || "n".equals(input))
			Format.disableColors();
		
		System.out.println("   ______                               _____            _             ");
		System.out.println("  |  ____|                             / ____|          (_)            ");
		System.out.println("  | |__   _ __   ___ _ __ __ _ _   _  | (___  _ __  _ __ _ _ __   __ _ ");
		System.out.println("  |  __| | '_ \\ / _ \\ '__/ _` | | | |  \\___ \\| '_ \\| '__| | '_ \\ / _` |");
		System.out.println("  | |____| | | |  __/ | | (_| | |_| |  ____) | |_) | |  | | | | | (_| |");
		System.out.println("  |______|_| |_|\\___|_|  \\__, |\\__, | |_____/| .__/|_|  |_|_| |_|\\__, |");
		System.out.println("                          __/ | __/ |        | |                  __/ |");
		System.out.println("                         |___/ |___/         |_|                 |___/ ");
		System.out.println();
		System.out.println(
				"You live in a world where magic and spirits are commonplace.\n" +
				"The galactic government has descended into complete capitalism and authoritarianism:\n" +
				"using the resources of the galaxy to satisfy the whims of the few chosen elite while\n" +
				"the majority is left to starve and suffer. You work for a heroic group of rebels that\n" +
				"fights for freedom.\n\n" +
				
				"As most know, energy comes in four \"flavors\" or types: Water, Earth, Air, and Fire.\n" +
				"By themselves, these energies have little intereaction with the physical world. However\n" +
				"there are complex spells that can use these energies to modify the physical world. Simple\n" +
				"spells such as heating a cup of water are relativly wide known, but more intensive spells\n" +
				"such as constructing an earthen wall or even condensing fire energy into a physical fireball\n" +
				"exist.\n\n" +
				
				"All four flavors of energy come from the same source: the interaction of the spirit plane with\n" +
				"the physical plane. In fact, it has recently been discovered that there is a fifth flavor\n" +
				"of energy: a so-called Raw flavor. It is theorized that this is the energy produced by the\n" +
				"interaction of the two planes but it quickly gets converted into the four everyday flavors.\n" +
				"Thus this Raw energy is normally practically undetectable. However, there have been rumours that\n" +
				"the government has found an Energy Spring: a rip in the fabric dividing the two planes. Such\n" +
				"a place would constantly generate immense amounts of Raw energy that could be harnessed to do\n" +
				"unthinkable things, similar to the mythological Energy Channels and the fabled destruction\n" + 
				"they caused. This must not fall into the hands of the goverment!\n\n" +
				
				"We recently intercepted a communication that confirms that the rumours are true and\n" +
				"lists the whereabouts of the Energy Spring. A small base has already been set up there to\n" +
				"safeguard this power from the government. You are the only person we trust enough to lead\n" +
				"this essential operation. Even though we got there first and have seized control of the\n" +
				"Energy Spring, the government will be setting up their own base as soon as they can.\n" +
				"Thankfully the Spring is in a remote location and it will take the government some time\n" +
				"to get fully up and running. Be warned, however, that they cannot be underestimated!\n\n" +
				
				"Once you arrive at the base you will be in charge of managing weekly operations. Our engineers\n" +
				"have designed containers capable of containing the large amounts of energy you will be dealing with.\n" +
				"Our scientists have invented a new process that can refine Raw energy into normal flavors; this\n" +
				"should let you harness the power of the Energy Spring. They have also theorized a process to\n" +
				"\"enhance\" the Energy Spring: by reinforcing the tear with a balanced energy support we should be\n" +
				"able to increase the amount of Raw Energy that is produced. We have included in your travel supplies\n" +
				"instructions for two simple spells that can help protect our base and attack the government's;\n" +
				"any other spells you need will have to be researched and invented on site. There is a large\n" +
				"magic university on a nearby desest planet; by converting some Water Energy into water you should\n" +
				"be able to recruit their help researching new spells. Finally, we have pooled together all of our\n" +
				"resources to hire the services of a powerful air spirit. This spirit has agreed to accompany you\n" +
				"and will grant you incredibly powerful prayers in exchange for a suitable sacrifice of Air energy.\n\n" +
				
				"Good luck. You are our last hope.");
		
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
