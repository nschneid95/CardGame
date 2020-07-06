package energy;

import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class OffensiveSpells {

	
	public static List<Option> getOptions() {
		List<Option> ret = new LinkedList<Option>();
		switch (maxLevel) {
		case 3:
			ret.add(Research.levelThree);
		case 2:
			ret.add(Research.levelTwo);
		case 1:
			ret.add(Research.levelOne);
			break;
		default:
			throw new IllegalStateException("Unexpected offensive maxLevel: " + maxLevel);
		}
		for (Map.Entry<Integer, List<Spell>> entry : all.entrySet()) {
			int level = entry.getKey();
			if (level > maxLevel)
				continue;
			for (Option spell : entry.getValue()) {
				if (!unknown.containsKey(level) || !unknown.get(level).contains(spell))
					ret.add(spell);
			}
		}
		return ret;
	}
	
	public static int numUnknown(int lvl) {
		return unknown.getOrDefault(lvl, List.of()).size();
	}
	
	private static class Research implements Option {
		
		public static Option levelOne = new Research(1);
		public static Option levelTwo = new Research(2);
		public static Option levelThree = new Research(3);
		
		private Research(int level) {
			this.level = level;
		}
		
		@Override
		public String text() {
			String adj = "";
			switch (level) {
			case 2:
				adj = "powerful ";
				break;
			case 3:
				adj = "legendary ";
				break;
			}
			return "Research a new " + adj + "offensive spell";
		}
		
		@Override
		public boolean isAllowed(Game game) {
			return game.hasWater(game.researchCosts.spellCost(level)) && unknown.get(level).size() > 0;
		}
		
		@Override
		public void execute(Game game) throws IllegalStateException {
			game.spend(EnergyType.WATER, game.researchCosts.spellCost(level));
			Printer.printlnLeft(Format.obj.ANSI_CYAN() + "You learned: " + Format.obj.ANSI_RESET()
					+ unknown.get(level).remove((int)(Math.random() * unknown.get(level).size())).description()
					+ Format.obj.ANSI_RESET());
		}
		
		private int level;
	}
	
	
	// Level 0
	private static Spell fireball = new SimpleOffensive("Fireball", "", new EnergyType.Counter().addFire(2), 1);
	// Level 1
	private static Spell lava = new SimpleOffensive("Lava Attack", "A large wave of lava that consumes everything it touches.", new EnergyType.Counter().addFire(2).addEarth(1), 2);
	private static Spell steam = new SimpleOffensive("Steam Attack", "A superheated cloud of steam capable of cooking an egg in seconds.", new EnergyType.Counter().addFire(1).addWater(1), 1);
	private static Spell superFB = new SimpleOffensive("Super Fireball", "An advanced fireball that can hold more energy and does more damage.", new EnergyType.Counter().addFire(3), 2);
	// Level 2
	private static Spell focus = new Focus();
	private static Spell meteors = new SimpleOffensive("Meteors", "Hundreds of beachball sized flaming rocks rain down on your enemies.", new EnergyType.Counter().addFire(2).addEarth(1).addAir(1), 3);
	private static Spell earthquake = new MultiSpell("Earthquake", "Earthquake: Shake the earth to shatter walls on both sides of the battlefield.",  Earthquake.all);
	// Level 3
	private static Spell matrix = new MultiSpell("Attack Matrix", "Attack Matrix: A ledgendary spell whose damage is only limited by your available power.", Matrix.all);
	private static Spell asteroid = new SimpleOffensive("Asteroid", "Call down an asteroid the size of a football field to crush your enemies.", new EnergyType.Counter().addFire(2).addEarth(2).addAir(1), 5);
	private static Spell mirror = new MultiSpell("Mirror Shield", "Mirror Shield: An inpenetrable shield that reflects any damage dealt to it.", Mirror.all);
	
	private static Map<Integer, List<Spell>> all = Map.of(
			0, List.of(fireball),
			1, List.of(lava, steam, superFB),
			2, List.of(focus, meteors, earthquake),
			3, List.of(matrix, asteroid, mirror));
	private static Map<Integer, List<Spell>> unknown = new HashMap<Integer, List<Spell>>(Map.of(
			1, new ArrayList<Spell>(List.of(lava, steam, superFB)),
			2, new ArrayList<Spell>(List.of(focus, meteors, earthquake)),
			3, new ArrayList<Spell>(List.of(matrix, asteroid, mirror))));
	static int maxLevel = 1;
	
	private static class SimpleOffensive implements Spell {
		SimpleOffensive(String name, String desc, EnergyType.Counter cost, int dmg) {
			this.name = name;
			this.desc = desc;
			this.cost = cost.getMap();
			this.dmg = dmg;
		}
		
		@Override
		public String text() {
			StringBuilder str = new StringBuilder();
			str.append(name);
			str.append(": ");
			str.append(String.join(", ",
					cost.entrySet().stream()
					.sorted((x, y) -> x.getKey().compareTo(y.getKey()))
					.map(x -> x.getValue() + " " + EnergyType.name(x.getKey()))
					.toArray(String[]::new)));
			str.append(" -> ");
			str.append(dmg);
			str.append(" damage");
			return str.toString();
		}
		
		@Override
		public String description() {
			return name + ": " + desc;
		}
		
		@Override
		public boolean isAllowed(Game game) {
			for (Map.Entry<EnergyType, Integer> entry : cost.entrySet()) {
				if (game.deck.getOrDefault(entry.getKey(), 0) < entry.getValue()) {
					return false;
				}
			}
			return true;
		}
		
		@Override
		public void execute(Game game) throws IllegalStateException {
			for (Map.Entry<EnergyType, Integer> entry : cost.entrySet()) {
				game.spend(entry.getKey(), entry.getValue());
			}
			game.enemyWalls -= dmg;
			if (game.enemyWalls <= 0) {
				game.win();
			}
		}
		
		private String name, desc;
		private Map<EnergyType, Integer> cost;
		private int dmg;
	}
	
	private static class Focus implements Spell {
		Focus() {}
		
		@Override
		public String text() {
			return "Focus: 3 " + EnergyType.name(EnergyType.RAW) + ", 2 " + EnergyType.name(EnergyType.EARTH)
				+ " -> 2x damage next attack";
		}
		
		@Override
		public String description() {
			return "Focus: Use grounding magic to remove distractions and double the damage of your next attack.";
		}
		
		@Override
		public boolean isAllowed(Game game) {
			return !game.focused && game.hasEarth(2) && game.hasRaw(3);
		}
		
		@Override
		public void execute(Game game) throws IllegalStateException{
			game.spend(EnergyType.EARTH, 2);
			game.spend(EnergyType.RAW, 3);
			game.focused = true;
		}
	}
	
	private static class Earthquake implements Option {
		static List<Option> all = List.of(
				new Earthquake(1), new Earthquake(2),
				new Earthquake(3), new Earthquake(4),
				new Earthquake(5), new Earthquake(6),
				new Earthquake(7), new Earthquake(8),
				new Earthquake(9), new Earthquake(10));
		
		Earthquake(int n) {
			this.n = n;
		}
		
		@Override
		public String text() {
			return "Earthquake: " + n + " " + EnergyType.name(EnergyType.RAW) + ", 1 "
					+ EnergyType.name(EnergyType.EARTH) + ", " + " -> " + n + " damage to yourself and enemy";
		}
		
		@Override
		public boolean isAllowed(Game game) {
			return game.hasEarth(1) && game.hasRaw(n) && n > 0;
		}
		
		@Override
		public void execute(Game game) {
			game.spend(EnergyType.EARTH, 1);
			game.spend(EnergyType.RAW, n);
			game.playerWalls -= n;
			if (game.playerWalls <= 0)
				game.lose();
			game.enemyWalls -= n;
			if (game.enemyWalls <= 0)
				game.win();
		}
		
		private final int n;
	}
	
	private static class Matrix implements Option {
		static List<Option> all = List.of(new Matrix(1), new Matrix(2), new Matrix(3), new Matrix(4), new Matrix(5));
		
		Matrix(int n) {
			this.n = n;
		}
		
		@Override
		public String text() {
			return "Attack matrix: " + (2 * n) + " " + EnergyType.name(EnergyType.RAW)
				+ " -> " + n + " damage";
		}
		
		@Override
		public boolean isAllowed(Game game) {
			return n > 0 && game.hasRaw(2 * n);
		}
		
		@Override
		public void execute(Game game) throws IllegalStateException {
			game.spend(EnergyType.RAW, 2 * n);
			game.enemyWalls -= n;
			if (game.enemyWalls <= 0)
				game.win();
		}
		
		private final int n;
	}

	private static class Mirror implements Option {
		static List<Option> all = List.of(
				new Mirror(1), new Mirror(2),
				new Mirror(3), new Mirror(4),
				new Mirror(5), new Mirror(6),
				new Mirror(7), new Mirror(8),
				new Mirror(9), new Mirror(10));
		
		Mirror(int n) {
			this.n = n;
		}
		
		@Override
		public String text() {
			return "Mirror shield: " + n + " " + EnergyType.name(EnergyType.RAW) + ", 1 "
					+ EnergyType.name(EnergyType.WATER) + ", 1 " + EnergyType.name(EnergyType.EARTH)
					+ ", 1 " + EnergyType.name(EnergyType.AIR) + ", 1 " + EnergyType.name(EnergyType.FIRE)
					+ " -> reflect all damage for " + n + " days";
		}
		
		@Override
		public boolean isAllowed(Game game) {
			return game.hasAir(1) && game.hasWater(1) && game.hasEarth(1) && game.hasFire(1) && n > 0 && game.hasRaw(n);
		}
		
		@Override
		public void execute(Game game) {
			game.spend(EnergyType.AIR, 1);
			game.spend(EnergyType.WATER, 1);
			game.spend(EnergyType.EARTH, 1);
			game.spend(EnergyType.FIRE, 1);
			game.spend(EnergyType.RAW, n);
			game.mirrorShieldLifetime += n;
		}
		
		private final int n;
	}
}
