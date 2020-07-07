package energy;

import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

class DefensiveSpells {
	
	static Supplier<String> color = Format.obj::ANSI_BRIGHT_GREEN;
	
	public static List<Option> getOptions() {
		List<Option> ret = new LinkedList<Option>();
		ret.add(Research.levelOne);
		if (maxLevel > 1)
			ret.add(Research.levelTwo);
		if (maxLevel > 2)
			ret.add(Research.levelThree);
		for (Spell spell : learned) {
			ret.add(spell);
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
			return color.get() + "Research a new " + adj + "defensive spell" + Format.obj.ANSI_RESET();
		}
		
		@Override
		public boolean isAllowed(Game game) {
			return game.hasWater(game.researchCosts.spellCost(level)) && unknown.get(level).size() > 0;
		}
		
		@Override
		public void execute(Game game) throws IllegalStateException {
			game.spend(EnergyType.WATER, game.researchCosts.spellCost(level));
			Spell spell = unknown.get(level).remove((int)(Math.random() * unknown.get(level).size()));
			learned.add(spell);
			Printer.printlnLeft(Format.obj.ANSI_CYAN() + "You learned: "
					+ spell.description());
		}
		
		private int level;
	}
	
	// Level 0
	private static Spell walls = new SimpleDefensive("Walls", "", new EnergyType.Counter().addEarth(2), 1);
	// Level 1
	private static Spell sandStorm = new SimpleDefensive("Sandstorm", "A sandstorm capable of grinding entire creatures to nothing but dust.", new EnergyType.Counter().addEarth(1).addAir(1), 1);
	private static Spell strongWalls = new SimpleDefensive("Strong Walls", "A more powerful wall with iron reinforcments.", new EnergyType.Counter().addEarth(3), 2);
	private static Spell quickSand = new SimpleDefensive("Quicksand", "An invisible pool that drowns creatures in minutes.", new EnergyType.Counter().addEarth(2).addWater(1), 2);
	// Level 2
	private static Spell golems = new Golems();
	private static Spell shield = new Shield();
	private static Spell moat = new SimpleDefensive("Lava Moat", "A bubbling moat of lava. There's no bridge.", new EnergyType.Counter().addEarth(2).addFire(2), 3);
	// Level 3
	private static Spell fireGolems = new FireGolems();
	private static Spell matrix = new MultiSpell(color, "Defense Matrix", "Summon an efficient but temporary matrix to protect your base.",Matrix::all);
	private static Spell energyShield = new MultiSpell(color, "Energy Shield", "Summon a shield protecting your energy deck from attack.", EnergyShield::all);

	private static Map<Integer, List<Spell>> unknown = new HashMap<Integer, List<Spell>>(Map.of(
			1, new ArrayList<Spell>(List.of(sandStorm, strongWalls, quickSand)),
			2, new ArrayList<Spell>(List.of(golems, shield, moat)),
			3, new ArrayList<Spell>(List.of(fireGolems, matrix, energyShield))));
	private static List<Spell> learned = new LinkedList<Spell>(List.of(walls));
	static int maxLevel = 1;
	
	private static class SimpleDefensive implements Spell {
		SimpleDefensive(String name, String desc, EnergyType.Counter cost, int def) {
			this.name = color.get() + name + Format.obj.ANSI_RESET();
			this.desc = desc;
			this.cost = cost.getMap();
			this.def = def;
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
			str.append(def);
			str.append(" walls");
			return str.toString();
		}
		
		@Override
		public String description() {
			return name + ": " + desc;
		}
		
		@Override
		public boolean isAllowed(Game game) {
			for (Map.Entry<EnergyType, Integer> entry : cost.entrySet()) {
				if (!game.has(entry.getKey(), entry.getValue()))
					return false;
			}
			return true;
		}
		
		@Override
		public void execute(Game game) throws IllegalStateException {
			for (Map.Entry<EnergyType, Integer> entry : cost.entrySet()) {
				game.spend(entry.getKey(), entry.getValue());
			}
			game.buildWalls(def);
		}
		
		private String name, desc;
		private Map<EnergyType, Integer> cost;
		private int def;
	}

	private static class Golems implements Spell {
		Golems() {}
		
		@Override
		public String text() {
			return color.get() + "Golems" + Format.obj.ANSI_RESET() + ": 1 "
					+ EnergyType.name(EnergyType.RAW) + ", 1 " + EnergyType.name(EnergyType.WATER)
					+ ", 1 " + EnergyType.name(EnergyType.EARTH) + " -> 2 golems";
		}
		
		@Override
		public String description() {
			return color.get() + "Golems" + Format.obj.ANSI_RESET()
					+ ": Humanoids sculpted out of mud that protect your base and still deal 1 damage per turn.";
		}
		
		@Override
		public boolean isAllowed(Game game) {
			return game.hasEarth(1) && game.hasWater(1) && game.hasRaw(1);
		}
		
		@Override
		public void execute(Game game) throws IllegalStateException {
			game.spend(EnergyType.EARTH, 1);
			game.spend(EnergyType.WATER, 1);
			game.spend(EnergyType.RAW, 1);
			game.summonGolems(2);
		}
	}

	private static class Shield implements Spell {
		Shield() {}
		
		@Override
		public String text() {
			return color.get() + "Carbon shield" + Format.obj.ANSI_RESET() + ": 2 "
					+ EnergyType.name(EnergyType.RAW) + ", 2 "
					+ EnergyType.name(EnergyType.EARTH) + " -> No damage is taken today";
		}
		
		@Override
		public String description() {
			return color.get() + "Carbon shield" + Format.obj.ANSI_RESET()
					+ ": A powerful shield that guards against all damage for one day.";
		}
		
		@Override
		public boolean isAllowed(Game game) {
			return !game.hasShield() && game.hasEarth(2) && game.hasRaw(2);
		}
		
		@Override
		public void execute(Game game) {
			game.spend(EnergyType.EARTH, 2);
			game.spend(EnergyType.RAW, 2);
			game.summonShield();
		}
	}
	
	private static class FireGolems implements Spell {
		FireGolems() {}
		
		@Override
		public String text() {
			return color.get() + "Fire Golems" + Format.obj.ANSI_RESET() + ": 2 "
					+ EnergyType.name(EnergyType.RAW) + ", 1 " + EnergyType.name(EnergyType.WATER)
					+ ", 1 " + EnergyType.name(EnergyType.EARTH) + ", 1 " + EnergyType.name(EnergyType.FIRE)
					+ " -> 3 fire golems";
		}
		
		@Override
		public String description() {
			return color.get() + "Fire Golems" + Format.obj.ANSI_RESET()
					+ ": Powerful golems that protect your base and still deal 2 damage per turn.";
		}
		
		@Override
		public boolean isAllowed(Game game) {
			return game.hasEarth(1) && game.hasWater(1) && game.hasFire(1) && game.hasRaw(2);
		}
		
		@Override
		public void execute(Game game) throws IllegalStateException {
			game.spend(EnergyType.EARTH, 1);
			game.spend(EnergyType.WATER, 1);
			game.spend(EnergyType.FIRE, 1);
			game.spend(EnergyType.RAW, 2);
			game.summonFireGolems(3);
		}
	}

	private static class Matrix implements Option {
		static Stream<Option> all() {
			return IntStream.iterate(1,  x -> x + 1).mapToObj(Matrix::new);
		}
		
		Matrix(int n) {
			this.n = n;
		}
		
		@Override
		public String text() {
			return "Defense Matrix: " + (2 * n) + " " + EnergyType.name(EnergyType.RAW)
					+ " -> Absorb up to " + n + " damage taken today";
		}
		
		@Override
		public boolean isAllowed(Game game) {
			return n > 0 && game.hasRaw(2 * n);
		}
		
		@Override
		public void execute(Game game) {
			game.spend(EnergyType.RAW, 2 * n);
			game.buildTempWalls(n);
		}
		
		private final int n;
	}

	private static class EnergyShield implements Option {
		static Stream<Option> all() {
			return IntStream.iterate(1, x -> x + 1).mapToObj(EnergyShield::new);
		}
		
		EnergyShield(int n) {
			this.n = n;
		}
		
		@Override
		public String text() {
			return "Energy Shield: " + n + " " + EnergyType.name(EnergyType.RAW) + ", 1 "
					+ EnergyType.name(EnergyType.WATER) + ", 1 " + EnergyType.name(EnergyType.EARTH)
					+ ", 1 " + EnergyType.name(EnergyType.AIR) + ", 1 " + EnergyType.name(EnergyType.FIRE)
					+ " -> your energy deck is protected for " + n + " days";
		}
		
		@Override
		public boolean isAllowed(Game game) {
			return game.hasEarth(1) && game.hasWater(1) && game.hasFire(1) && game.hasAir(1) && game.hasRaw(n) && n > 0;
		}
		
		@Override
		public void execute(Game game) {
			game.spend(EnergyType.AIR, 1);
			game.spend(EnergyType.EARTH, 1);
			game.spend(EnergyType.FIRE, 1);
			game.spend(EnergyType.WATER, 1);
			game.spend(EnergyType.RAW, n);
			game.summonEnergyShield(n);
		}
		
		private final int n;
	}
}