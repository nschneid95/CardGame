package energy;

import java.util.Scanner;

public class Main {

	public static void main(String[] args) {
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
		Game game = new Game(1);
		game.play();
		// Note that this is just for the compiler - game.play will kill the JVM when it's done running.
		// We can't call close earlier since that will close System.in.
		scan.close();
	}

}
