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
			return "Research a new level " + level + " offensive spell";
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
	private static Option fireball = new SimpleOffensive("Fireball", new EnergyType.Counter().addFire(2), 1);
	// Level 1
	private static Option lava = new SimpleOffensive("Lava Attack", new EnergyType.Counter().addFire(2).addEarth(1), 2);
	private static Option steam = new SimpleOffensive("Steam Attack", new EnergyType.Counter().addFire(1).addWater(1), 1);
	private static Option superFB = new SimpleOffensive("Super Fireball", new EnergyType.Counter().addFire(3), 2);
	// Level 2
	private static Option focus = new Focus();
	private static Option meteors = new SimpleOffensive("Meteors", new EnergyType.Counter().addFire(2).addEarth(1).addAir(1), 3);
	private static Option earthquake = new MultiOption("Cause an earthquake that damages both yourself and the enemy", Earthquake.all);
	// Level 3
	private static Option matrix = new MultiOption("Summon an attack matrix", Matrix.all);
	private static Option asteroid = new SimpleOffensive("Asteroid", new EnergyType.Counter().addFire(2).addEarth(2).addAir(1), 5);
	private static Option mirror = new MultiOption("Summon a mirror shield to reflect damage", Mirror.all);

	private static Map<Integer, List<Option>> initList(boolean includeZero) {
		Map<Integer, List<Option>> ret = new HashMap<Integer, List<Option>>();
		List<Option> zero = new ArrayList<Option>();
		zero.add(fireball);
		List<Option> one = new ArrayList<Option>();
		one.add(lava);
		one.add(steam);
		one.add(superFB);
		List<Option> two = new ArrayList<Option>();
		two.add(focus);
		two.add(meteors);
		two.add(earthquake);
		List<Option> three = new ArrayList<Option>();
		three.add(matrix);
		three.add(asteroid);
		three.add(mirror);
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
	
	private static class SimpleOffensive implements Option {
		SimpleOffensive(String name, EnergyType.Counter cost, int dmg) {
			this.name = name;
			this.cost = cost.getMap();
			this.dmg = dmg;
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
			str.append(dmg);
			str.append(" damage");
			return str.toString();
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
		
		private String name;
		private Map<EnergyType, Integer> cost;
		private int dmg;
	}
	
	private static class Focus implements Option {
		Focus() {}
		
		@Override
		public String text() {
			return "Focus: Doubles damage from next attack. Costs 2 Fire and 1 Raw energy";
		}
		
		@Override
		public boolean isAllowed(Game game) {
			return !game.focused && game.hasFire(2) && game.hasRaw(1);
		}
		
		@Override
		public void execute(Game game) throws IllegalStateException{
			game.spend(EnergyType.FIRE, 2);
			game.spend(EnergyType.RAW, 1);
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
			return "Spend 1 Earth and " + n + " Raw energy to cause an earthquake dealing " + n + " damage to yourself and the enemy";
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
			return "Spend" + 2 * n + " Raw energy to summon an attack matrix dealing " + n + " damage";
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
			return "Mirror shield: Spend " + n +
					" Raw energy and one each of Earth, Air, Water, and Fire energy to summon a shield that reflects all damage from " + n + " days";
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
