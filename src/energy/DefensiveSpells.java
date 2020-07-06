package energy;

import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

class DefensiveSpells {
	
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
			throw new IllegalStateException("Unexpected defensive maxLevel: " + maxLevel);
		}
		for (Map.Entry<Integer, List<Option>> entry : all.entrySet()) {
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
			return "Research a new level " + level + " defensive spell";
		}
		
		@Override
		public boolean isAllowed(Game game) {
			return game.hasWater(game.researchCosts.SpellCost(level)) && unknown.get(level).size() > 0;
		}
		
		@Override
		public void execute(Game game) throws IllegalStateException {
			game.spend(EnergyType.WATER, game.researchCosts.SpellCost(level));
			Printer.printlnLeft(Format.ANSI_CYAN + "You learned: " + 
					unknown.get(level).remove((int)(Math.random() * unknown.get(level).size())).text() + Format.ANSI_RESET);
			
		}
		
		private int level;
	}
	
	
	// Level 0
	private static Option walls = new SimpleDefensive("Walls", new EnergyType.Counter().addEarth(2), 1);
	// Level 1
	private static Option dust = new SimpleDefensive("Dust Cloud", new EnergyType.Counter().addEarth(1).addAir(1), 1);
	private static Option strongWalls = new SimpleDefensive("Strong Walls", new EnergyType.Counter().addEarth(3), 2);
	private static Option mud = new SimpleDefensive("Mud Trap", new EnergyType.Counter().addEarth(2).addWater(1), 2);
	// Level 2
	private static Option golems = new Golems();
	private static Option shield = new Shield();
	private static Option moat = new SimpleDefensive("Lava Moat", new EnergyType.Counter().addEarth(2).addFire(2), 3);
	// Level 3
	private static Option fireGolems = new FireGolems();
	private static Option matrix = new MultiOption("Summon a defensive matrix", Matrix.all);
	private static Option energyShield = new MultiOption("Summon an energy shield to protect your energy deck", EnergyShield.all);

	private static Map<Integer, List<Option>> initList(boolean includeZero) {
		Map<Integer, List<Option>> ret = new HashMap<Integer, List<Option>>();
		List<Option> zero = new ArrayList<Option>();
		zero.add(walls);
		List<Option> one = new ArrayList<Option>();
		one.add(dust);
		one.add(strongWalls);
		one.add(mud);
		List<Option> two = new ArrayList<Option>();
		two.add(golems);
		two.add(shield);
		two.add(moat);
		List<Option> three = new ArrayList<Option>();
		three.add(fireGolems);
		three.add(matrix);
		three.add(energyShield);
		if (includeZero)
			ret.put(0, zero);
		ret.put(1, one);
		ret.put(2, two);
		ret.put(3, three);
		return ret;
	}
	
	private static Map<Integer, List<Option>> unknown = initList(false);
	private static Map<Integer, List<Option>> all = initList(true);
	static int maxLevel = 1;
	
	private static class SimpleDefensive implements Option {
		SimpleDefensive(String name, EnergyType.Counter cost, int def) {
			this.name = name;
			this.cost = cost.getMap();
			this.def = def;
		}
		
		@Override
		public String text() {
			StringBuilder str = new StringBuilder();
			str.append(name);
			str.append(": ");
			boolean first = true;
			for (Map.Entry<EnergyType, Integer> entry : cost.entrySet()) {
				if (!first) {
					str.append(", ");
				}
				str.append(entry.getValue());
				str.append(" ");
				str.append(EnergyType.name(entry.getKey()));
				first = false;
			}
			str.append(" -> ");
			str.append(def);
			str.append(" walls");
			return str.toString();
		}
		
		@Override
		public boolean isAllowed(Game game) {
			for (Map.Entry<EnergyType, Integer> entry : cost.entrySet()) {
				if (game.hand.getOrDefault(entry.getKey(), 0) < entry.getValue()) {
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
			game.playerWalls += def;
			if (game.enemyWalls <= 0) {
				game.win();
			}
		}
		
		private String name;
		private Map<EnergyType, Integer> cost;
		private int def;
	}

	private static class Golems implements Option {
		Golems() {}
		
		@Override
		public String text() {
			return "Spend one each of Earth, Water, and Raw energy to sculpt two golems that attack for you and protect you.";
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
			game.numGolems += 2;
		}
	}

	private static class Shield implements Option {
		Shield() {}
		
		@Override
		public String text() {
			return "Craft an impenetrable obsidian shield that lasts until the end of the day";
		}
		
		@Override
		public boolean isAllowed(Game game) {
			return !game.hasShield && game.hasEarth(2) && game.hasRaw(2);
		}
		
		@Override
		public void execute(Game game) {
			game.spend(EnergyType.EARTH, 2);
			game.spend(EnergyType.RAW, 2);
			game.hasShield = true;
		}
	}
	
	private static class FireGolems implements Option {
		FireGolems() {}
		
		@Override
		public String text() {
			return "Spend two Raw energy and one each of Earth, Water, and Fire energy to sculpt three fire golems that attack for you and protect you.";
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
			game.numFireGolems += 3;
		}
	}

	private static class Matrix implements Option {
		static List<Option> all = List.of(new Matrix(1), new Matrix(2), new Matrix(3), new Matrix(4), new Matrix(5));
		
		Matrix(int n) {
			this.n = n;
		}
		
		@Override
		public String text() {
			return "Spend " + 2 * n + " Raw energy to summon a defensive matrix that absorbs up to " + n + " damage today only.";
		}
		
		@Override
		public boolean isAllowed(Game game) {
			return n > 0 && game.hasRaw(2 * n);
		}
		
		@Override
		public void execute(Game game) {
			game.spend(EnergyType.RAW, 2 * n);
			game.tempWalls += n;
		}
		
		private final int n;
	}

	private static class EnergyShield implements Option {
		static List<Option> all = List.of(
				new EnergyShield(1), new EnergyShield(2),
				new EnergyShield(3), new EnergyShield(4),
				new EnergyShield(5), new EnergyShield(6),
				new EnergyShield(7), new EnergyShield(8),
				new EnergyShield(9), new EnergyShield(10));
		
		EnergyShield(int n) {
			this.n = n;
		}
		
		@Override
		public String text() {
			return "Spend " + n + " Raw energy and one each of Earth, Water, Air, and Fire energy to summon an energy " +
					"shield that protects your energy deck from attack for " + n + " days.";
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
			game.energyShieldLifetime += n;
		}
		
		private final int n;
	}
}