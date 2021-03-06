package energy;

import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Stream;

public class Game {
	// Difficulty ranges from 1 (easiest) to 10 (hardest)
	public Game(int difficulty) {
		deck = new HashMap<EnergyType, Integer>();
		deck.put(EnergyType.RAW, 8);
		deck.put(EnergyType.AIR, 2);
		deck.put(EnergyType.FIRE, 2);
		deck.put(EnergyType.EARTH, 2);
		deck.put(EnergyType.WATER, 2);
		discard = new HashMap<EnergyType, Integer>();
		turnNum = 1;
		playerWalls = 10;
		numGolems = 0;
		numFireGolems = 0;
		enemyWalls = 10;
		baseEnergy = 11 - difficulty;
		numEnergySprings = 0;
		researchCosts = ResearchCosts.BaseCosts();
		focused = false;
		enemy = new Enemy(difficulty);
		dailyAirCost = 0;
		hadSpiritWarning = false;
		gameOverText = Optional.empty();
	}
	
	// Deck querying
	boolean has(EnergyType t, int n) {
		return deck.getOrDefault(t, 0) >= n;
	}
	boolean hasWater(int n) {
		return deck.getOrDefault(EnergyType.WATER, 0) >= n;
	}
	boolean hasAir(int n) {
		return deck.getOrDefault(EnergyType.AIR, 0) >= n;
	}
	boolean hasEarth(int n) {
		return deck.getOrDefault(EnergyType.EARTH, 0) >= n;
	}
	boolean hasFire(int n) {
		return deck.getOrDefault(EnergyType.FIRE, 0) >= n;
	}
	boolean hasRaw(int n) {
		return deck.getOrDefault(EnergyType.RAW, 0) >= n;
	}
	
	// Deck manipulation
	void spend(EnergyType type, int n) throws IllegalStateException {
		int newVal = deck.getOrDefault(type, 0) - n;
		if (newVal < 0) {
			throw new IllegalStateException("Cannot spend " + n + " " + EnergyType.name(type)
			+ " cards when there are only " + deck.getOrDefault(type, 0) + " in deck.");
		} else {
			deck.put(type, newVal);
		}
	}
	int steal(EnergyType type, int n) {
		if (energyShieldLifetime > 0)
			return 0;
		int curr = deck.getOrDefault(type, 0);
		if (curr >= n) {
			deck.put(type, curr - n);
			return n;
		} else {
			deck.put(type, 0);
			return curr;
		}
	}
	int stealAll(EnergyType type) {
		if (energyShieldLifetime > 0)
			return 0 ;
		int curr = deck.getOrDefault(type, 0);
		deck.put(type, 0);
		return curr;
	}
	void insert(EnergyType type, int n) {
		discard.merge(type, n, (x, y) -> x + y);
	}
	void addDailyAirCost(int n) { dailyAirCost += n; }
	
	// Endgame
	void win() {
		gameOverText = Optional.of("Congratulations, you won!! The world is safe from the evil capitalists!");
	}
	void lose(String text) {
		gameOverText = Optional.of(text);
	}
	private void gameOver() {
		Printer.printlnLeft(gameOverText.get());
		Printer.printlnLeft("Press return to exit");
		Printer.flush();
		try { System.in.read(); } catch (IOException e) { }
		System.exit(0);
	}
	
	// Walls and damge
	void buildWalls(int amt) { playerWalls += amt; }
	void buildTempWalls(int amt) { tempWalls += amt; }
	void buildEnemyWalls(int amt) { enemyWalls += amt; }
	int dealDamage(int amt) {
		if (focused)
			amt *= 2;
		focused = false;
		enemyWalls -= amt;
		if (enemyWalls <= 0)
			win();
		return amt;
	}
	int takeDamage(int amt) {
		if (hasShield)
			return 0;
		if (mirrorShieldLifetime > 0) {
			Printer.printlnLeft("Your mirror shield reflects " + amt + " damage!", Color.BrightWhite);
			enemyWalls -= amt;
			if (enemyWalls <= 0)
				win();
		}
		if (tempWalls >= amt) {
			tempWalls -= amt;
			return 0;
		}
		amt -= tempWalls;
		tempWalls = 0;
		if (numGolems >= amt) {
			numGolems -= amt;
			return 0;
		}
		amt -= numGolems;
		numGolems = 0;
		if (numFireGolems >= amt) {
			numFireGolems -= amt;
			return 0;
		}
		amt -= numFireGolems;
		numFireGolems = 0;
		playerWalls -= amt;
		if (playerWalls <= 0)
			lose("The soldier drones of the government climb over the ruins of your last wall and slaughter "
					+ "everyone in the base.");
		return amt;
	}
	
	// Golems
	void summonGolems(int amt) { numGolems += amt; }
	void summonFireGolems(int amt) { numFireGolems += amt; }
	
	// Shields
	boolean hasShield() { return hasShield; }
	void summonShield() { hasShield = true; }
	void summonEnergyShield(int days) { energyShieldLifetime += days; }
	void summonMirrorShield(int days) { mirrorShieldLifetime += days; }
	
	boolean isFocused() { return focused; }
	void focus() { focused = true; }
	void addEnergySpring() { numEnergySprings++; }
	
	private void printWeekBanner() {
		int width = Printer.getWidth();
		String text = " WEEK " + turnNum;
		int padding = (width - text.length()) / 2;
		int gameLength = enemy.numTurns();
		for (int i = 0; i < padding; i++) {
			if (i * gameLength < width * turnNum)
				Printer.printLeft("=");
			else
				Printer.printLeft(" ");
		}
		Printer.printLeft(text);
		int offset = padding + text.length();
		for (int i = 0; i < padding; i++) {
			if ((i + offset) * gameLength < width * turnNum)
				Printer.printLeft("=");
			else
				Printer.printLeft(" ");
		}
		Printer.printlnLeft("");
	}
	
	private void printMap(Map<EnergyType, Integer> map) {
		ColoredString s = ColoredString.join(", ",
			map.entrySet().stream()
			.sorted((x, y) -> x.getKey().compareTo(y.getKey()))
			.filter(x -> x.getValue() > 0)
			.map(e -> new ColoredString(e.getValue() + " ").append(EnergyType.name(e.getKey())))
			.collect(Collectors.toList()));
		Printer.printlnLeft(s);
	}
	
	private String getValByLevel(Function<Integer, Integer> func, int maxLevel) {
		return String.join("/", 
				Stream.of(1, 2, 3)
				.sorted()
				.filter(x -> x <= maxLevel)
				.map(func)
				.map(x -> x.toString())
				.toArray(String[]::new));
	}
	
	private void printInfo() {
		Printer.printlnRight("Energy spring output: " + (baseEnergy + numEnergySprings * (numEnergySprings + 1) / 2));
		Printer.printlnRight("Your walls: " + playerWalls);
		Printer.printlnRight("Enemy walls: " + enemyWalls);
		if (tempWalls > 0)
			Printer.printlnRight("Temporary walls: " + tempWalls, Color.BrightRed);
		if (numGolems > 0)
			Printer.printlnRight("Golems: " + numGolems, Color.BrightCyan);
		if (numFireGolems > 0)
			Printer.printlnRight("Fire Golems: " + numFireGolems, Color.BrightCyan);
		int lvl = OffensiveSpells.maxLevel;
		// Spell costs
		Printer.printRight("Spell cost (");
		Printer.printRight(EnergyType.waterName);
		Printer.printRight("): ");
		Printer.printlnRight(getValByLevel(researchCosts::spellCost, lvl));
		// Remaining defensive
		Printer.printRight("Remaining defensive spells: ", DefensiveSpells.color);
		Printer.printlnRight(getValByLevel(DefensiveSpells::numUnknown, lvl), DefensiveSpells.color);
		// Remaining offensive
		Printer.printRight("Remaning offensive spells: ", OffensiveSpells.color);
		Printer.printlnRight(getValByLevel(OffensiveSpells::numUnknown, lvl), OffensiveSpells.color);
		// Prayer cost
		Printer.printRight("Prayer cost(", Prayers.color);
		Printer.printRight(EnergyType.airName);
		Printer.printlnRight("): " + researchCosts.prayerCost(Prayers.getLevel()), Prayers.color);
		// Remaining prayers
		Printer.printlnRight("Remaining prayers: " + Prayers.numPrayers(), Prayers.color);
		if (focused)
			Printer.printlnRight("Focused! Next attack gets 2x damage", Color.BrightRed);
		if (hasShield)
			Printer.printlnRight("Carbon Shield in place", Color.BrightBlue);
		if (mirrorShieldLifetime > 0)
			Printer.printlnRight("Mirror Shield acive for " + mirrorShieldLifetime + " days", Color.BrightBlue);
		if (energyShieldLifetime > 0)
			Printer.printlnRight("Energy Shield active for " + energyShieldLifetime + " days", Color.BrightBlue);
	}
	
	private int mapSize(Map<EnergyType, Integer> m) {
		return m.values().stream().reduce(0, (x, y) -> x + y, (x, y) -> x + y);
	}
	
	private void playCards() {
		// Keep making choices until the player discards the rest of the cards
		class BoolHolder {
			boolean val = false;
		}
		final BoolHolder done = new BoolHolder();
		while (!done.val) {
			if (mapSize(deck) == 0)
				break;
			printInfo();
			Printer.printLeft("Your deck: ");
			printMap(deck);
			// Get the list of all possible actions
			List<Option> options = new ArrayList<Option>();
			options.add(new Option() {
				@Override
				public ColoredString text() { return new ColoredString("Discard remaining cards"); }
				@Override
				public boolean isAllowed(Game game) { return true; }
				@Override
				public void execute(Game game) { done.val = true; }
			});
			options.addAll(EnergyRefinementOption.all);
			options.addAll(EnergyChannelOption.all);
			options.add(EnergySpringOption.get());
			options.addAll(DefensiveSpells.getOptions());
			options.addAll(OffensiveSpells.getOptions());
			options.addAll(Prayers.getOptions());
			
			// Filter the list to only allowed actions
			Option[] filteredOptions = options.stream().filter(o -> o.isAllowed(this)).toArray(Option[]::new);
			
			// Present to the user and make a choice
			if (filteredOptions.length == 1) {
				Printer.printlnLeft("Press return to discard your remaining cards");
				Printer.flush();
				try {
					System.in.read();
					System.in.skip(System.in.available());
				} catch (IOException e) { }
				done.val = true;
			} else {
				int choice = Selection.makeSelection(filteredOptions);
				filteredOptions[choice].execute(this);
			}
			
			if (gameOverText.isPresent())
				gameOver();
		}
		
		// Discard the rest of the deck
		for (Map.Entry<EnergyType, Integer> entry : deck.entrySet())
			discard.merge(entry.getKey(), entry.getValue(), (x, y) -> x + y);
	}

	private void playDay() {
		printWeekBanner();
		// 0: Daily air cost
		if (dailyAirCost > 0) {
			if (hasAir(dailyAirCost)) {
				spend(EnergyType.AIR, dailyAirCost);
				Printer.printLeft("The spirit eats " + dailyAirCost + " ", Prayers.color);
				Printer.printlnLeft(EnergyType.airName);
			} else {
				if (hadSpiritWarning) {
					if (Prayers.getLevel() == 2)
						lose("\"You missed your payment.\" whispers the spirit. They snap their fingers and all the air on the "
								+ "planet dissapears with them. Everyone is dead in minutes.");
					else
						lose("\"You missed your payment!\" screams the spirit as reality warps and splinters around them. "
								+ "In a fit of anger they tear through the barrier between the two planes, causing the spirit "
								+ "plane and the physical plane to collide. The Raw energy created explodes outwards destroying "
								+ "everything it touches until all reality is destroyed.");
				} else {
					Printer.printlnLeft("You missed your daily air payment. The spirit has a brief talk with you that leaves you "
							+ "quivering...");
					hadSpiritWarning = true;
				}
			}
		}
		if (gameOverText.isPresent())
			gameOver();
		
		// 1: Play cards
		playCards();
		
		// 2: Handle golem damage
		enemyWalls -= numGolems;
		if (numGolems > 0)
			Printer.printlnLeft("Your golems deal " + numGolems + " damage!", Color.BrightWhite);
		enemyWalls -= 2 * numFireGolems;
		if (numFireGolems > 0)
			Printer.printlnLeft("Your fire golems deal " + 2 * numFireGolems + " damage!", Color.BrightWhite);
		if (enemyWalls <= 0)
			win();
		
		if (gameOverText.isPresent())
			gameOver();
		
		// 3: Re-shuffle deck and add raw energy
		deck = discard;
		discard = new HashMap<EnergyType, Integer>();
		int newEnergy = baseEnergy + numEnergySprings * (numEnergySprings + 1) / 2;
		deck.merge(EnergyType.RAW, newEnergy, (x, y) -> x + y);
		
		// 4: Enemy turn
		enemy.execute(this);
		turnNum++;
		if (gameOverText.isPresent())
			gameOver();
		
		// 5: Clear temporary effects
		tempWalls = 0;
		hasShield = false;
		if (mirrorShieldLifetime > 0)
			mirrorShieldLifetime--;
		if (energyShieldLifetime > 0)
			energyShieldLifetime--;
		
		Printer.flush();
	}
	
	// Note: this function never returns; instead it ends the program once it finishes
	void play() {
		while (true)
			playDay();
	}
	
	private Map<EnergyType, Integer> deck;
	private Map<EnergyType, Integer> discard;
	private int turnNum;
	private int playerWalls;
	private int numGolems;
	private int numFireGolems;
	private int enemyWalls;
	private int baseEnergy;
	private int numEnergySprings;
	ResearchCosts researchCosts;
	private boolean focused;
	private int mirrorShieldLifetime;
	private boolean hasShield;
	private int tempWalls;
	private int energyShieldLifetime;
	private Enemy enemy;
	private int dailyAirCost;
	private boolean hadSpiritWarning;
	Optional<String> gameOverText;
}
