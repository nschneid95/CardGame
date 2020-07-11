package energy;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class OffensiveSpells {

	static final Color color = Color.BrightYellow;
	
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
		public ColoredString text() {
			String adj = "";
			switch (level) {
			case 2:
				adj = "powerful ";
				break;
			case 3:
				adj = "legendary ";
				break;
			}
			return new ColoredString("Research a new " + adj + "offensive spell", color);
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
			Printer.printLeft("You learned: ", Color.Cyan);
			Printer.printlnLeft(spell.description().reColor(Color.Default, Color.Cyan));
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
	private static Spell earthquake = new MultiSpell("Earthquake", color, "Shake the earth to shatter walls on both sides of the battlefield.", Earthquake::all);
	// Level 3
	private static Spell matrix = new MultiSpell("Attack Matrix", color, "A ledgendary spell whose damage is only limited by your available power.", Matrix::all);
	private static Spell asteroid = new SimpleOffensive("Asteroid", "Call down an asteroid the size of a football field to crush your enemies.", new EnergyType.Counter().addFire(2).addEarth(2).addAir(1), 5);
	private static Spell mirror = new MultiSpell("Mirror Shield", color, "An inpenetrable shield that reflects any damage dealt to it.", Mirror::all);
	
	private static Map<Integer, List<Spell>> unknown = new HashMap<Integer, List<Spell>>(Map.of(
			1, new ArrayList<Spell>(List.of(lava, steam, superFB)),
			2, new ArrayList<Spell>(List.of(focus, meteors, earthquake)),
			3, new ArrayList<Spell>(List.of(matrix, asteroid, mirror))));
	private static List<Spell> learned = new LinkedList<Spell>(List.of(fireball));
	static int maxLevel = 1;
	
	private static class SimpleOffensive implements Spell {
		SimpleOffensive(String name, String desc, EnergyType.Counter cost, int dmg) {
			this.name = new ColoredString(name, color);
			this.desc = new ColoredString(desc, Color.Cyan);
			this.cost = cost.getMap();
			this.dmg = dmg;
		}
		
		@Override
		public ColoredString text() {
			ColoredString.Builder str = new ColoredString.Builder();
			str.append(name);
			str.append(": ");
			str.append(ColoredString.join(", ",
					cost.entrySet().stream()
					.sorted((x, y) -> x.getKey().compareTo(y.getKey()))
					.map(x -> new ColoredString(x.getValue() + " ").append(EnergyType.name(x.getKey())))
					.collect(Collectors.toList())));
			
			str.append(" -> ");
			str.append(((Integer)dmg).toString());
			str.append(" damage");
			return str.toColoredString();
		}
		
		@Override
		public ColoredString description() {
			return name.append(": ").append(desc);
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
			game.dealDamage(dmg);
		}
		
		private ColoredString name, desc;
		private Map<EnergyType, Integer> cost;
		private int dmg;
	}
	
	private static class Focus implements Spell {
		Focus() {}
		
		@Override
		public ColoredString text() {
			return new ColoredString("Focus", color).append(": 3 ").append(EnergyType.rawName).append(", 2 ")
					.append(EnergyType.earthName).append(" -> 2x damage next attack");
		}
		
		@Override
		public ColoredString description() {
			return new ColoredString("Focus", color)
					.append(": Use grounding magic to remove distractions and double the damage of your next attack.");
		}
		
		@Override
		public boolean isAllowed(Game game) {
			return !game.isFocused() && game.hasEarth(2) && game.hasRaw(3);
		}
		
		@Override
		public void execute(Game game) throws IllegalStateException{
			game.spend(EnergyType.EARTH, 2);
			game.spend(EnergyType.RAW, 3);
			game.focus();
		}
	}
	
	private static class Earthquake implements Option {
		static Stream<Option> all() {
			return IntStream.iterate(1, x -> x + 1).mapToObj(Earthquake::new);
		}
		
		Earthquake(int n) {
			this.n = n;
		}
		
		@Override
		public ColoredString text() {
			return new ColoredString("Earthquake", color).append(": " + n + " ").append(EnergyType.rawName)
					.append(", 1 ").append(EnergyType.earthName)
					.append(" -> " + n + " damage to yourself and enemy");
		}
		
		@Override
		public boolean isAllowed(Game game) {
			return game.hasEarth(1) && game.hasRaw(n) && n > 0;
		}
		
		@Override
		public void execute(Game game) {
			game.spend(EnergyType.EARTH, 1);
			game.spend(EnergyType.RAW, n);
			game.takeDamage(n);
			game.dealDamage(n);
		}
		
		private final int n;
	}
	
	private static class Matrix implements Option {
		static Stream<Option> all() {
			return IntStream.iterate(1, x -> x + 1).mapToObj(Matrix::new);
		}
		
		Matrix(int n) {
			this.n = n;
		}
		
		@Override
		public ColoredString text() {
			return new ColoredString("Attack matrix", color).append(": " + (2 * n) + " ")
					.append(EnergyType.rawName).append(" -> " + n + " damage");
		}
		
		@Override
		public boolean isAllowed(Game game) {
			return n > 0 && game.hasRaw(2 * n);
		}
		
		@Override
		public void execute(Game game) throws IllegalStateException {
			game.spend(EnergyType.RAW, 2 * n);
			game.dealDamage(n);
		}
		
		private final int n;
	}

	private static class Mirror implements Option {
		static Stream<Option> all() {
			return IntStream.iterate(1, x -> x + 1).mapToObj(Mirror::new);
		}
		
		Mirror(int n) {
			this.n = n;
		}
		
		@Override
		public ColoredString text() {
			return new ColoredString("Mirror shield", color).append(": " + n + " ").append(EnergyType.rawName)
					.append(", 1 ").append(EnergyType.waterName).append(", 1 ").append(EnergyType.earthName)
					.append(", 1 ").append(EnergyType.airName).append(", 1 ").append(EnergyType.fireName)
					.append(" -> reflect all damage for " + n + " days");
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
			game.summonMirrorShield(n);
		}
		
		private final int n;
	}
}
