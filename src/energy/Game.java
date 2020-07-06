package energy;

import java.util.Map;
import java.util.function.Function;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Game {
	// Difficulty ranges from 1 (easiest) to 6 (hardest)
	public Game(int difficulty) {
		deck = new HashMap<EnergyType, Integer>();
		deck.put(EnergyType.RAW, 8);
		deck.put(EnergyType.AIR, 2);
		deck.put(EnergyType.FIRE, 2);
		deck.put(EnergyType.EARTH, 2);
		deck.put(EnergyType.WATER, 2);
		discard = new HashMap<EnergyType, Integer>();
		hand = new HashMap<EnergyType, Integer>();
		turnNum = 1;
		playerWalls = 10;
		numGolems = 0;
		numFireGolems = 0;
		enemyWalls = 10;
		baseEnergy = 8 - difficulty;
		numEnergySprings = 0;
		researchCosts = ResearchCosts.BaseCosts();
		focused = false;
		enemy = new Enemy();
		handSize = 6;
	}
	
	boolean hasWater(int n) {
		return hand.getOrDefault(EnergyType.WATER, 0) >= n;
	}
	boolean hasAir(int n) {
		return hand.getOrDefault(EnergyType.AIR, 0) >= n;
	}
	boolean hasEarth(int n) {
		return hand.getOrDefault(EnergyType.EARTH, 0) >= n;
	}
	boolean hasFire(int n) {
		return hand.getOrDefault(EnergyType.FIRE, 0) >= n;
	}
	boolean hasRaw(int n) {
		return hand.getOrDefault(EnergyType.RAW, 0) >= n;
	}
	
	void spend(EnergyType type, int n) throws IllegalStateException {
		int newVal = hand.getOrDefault(type, 0) - n;
		if (newVal < 0) {
			throw new IllegalStateException("Cannot spend " + n + " " + EnergyType.name(type) + " cards when there are only "
					+ hand.getOrDefault(type, 0) + " in hand.");
		} else {
			hand.put(type, newVal);
		}
	}
	void insert(EnergyType type, int n) {
		discard.merge(type, n, (x, y) -> x + y);
	}
	
	void win() {
		System.out.println("Congratulations you won!! Press any key to exit");
		try { System.in.read(); } catch (IOException e) { }
		System.exit(0);
	}
	
	void lose() {
		System.out.println("Your base has been overriden and the capitalistic pigs have taken the Energy Spring for themselves.");
		System.out.println("Better luck next time! Press any key to exit");
		try { System.in.read(); } catch (IOException e) { }
		System.exit(0);
	}
	
	private void printMap(Map<EnergyType, Integer> map) {
		String s = String.join(", ",
			map.entrySet().stream()
			.sorted((x, y) -> x.getKey().compareTo(y.getKey()))
			.filter(x -> x.getValue() > 0)
			.map(e -> e.getValue() + " " + EnergyType.name(e.getKey()))
			.toArray(String[]::new));
		Printer.printlnLeft(s);
	}
	
	private String getValByLevel(Function<Integer, Integer> func, int maxLevel) {
		return String.join("/", List.of(1, 2, 3).stream().sorted().filter(x -> x <= maxLevel).map(func).map(x -> x.toString()).toArray(String[]::new));
	}
	
	private void printInfo() {
		Printer.printlnRight("Your walls: " + playerWalls);
		Printer.printlnRight("Enemy walls: " + enemyWalls);
		if (numGolems > 0)
			Printer.printlnRight("Golems: " + numGolems);
		if (numFireGolems > 0)
			Printer.printlnRight("FireGolems: " + numFireGolems);
		int lvl = OffensiveSpells.maxLevel;
		Printer.printlnRight("Spell cost (" + EnergyType.name(EnergyType.WATER) + "): "
			+ getValByLevel(researchCosts::SpellCost, lvl));
		Printer.printlnRight("Remaning offensive spells: " + getValByLevel(OffensiveSpells::numUnknown, lvl));
		Printer.printlnRight("Remaining defensive spells: " + getValByLevel(DefensiveSpells::numUnknown, lvl));
		Printer.printlnRight("Prayer cost(" + EnergyType.name(EnergyType.AIR) + "): "
			+ getValByLevel(researchCosts::PrayerCost, lvl));
		Printer.printlnRight("Remaining prayers: " + getValByLevel(Prayers::numPrayers, lvl));
		Printer.printlnRight("Today's current " + EnergyType.name(EnergyType.AIR) + " donations: " + airDonations);
	}
	
	private EnergyType drawRandomCard() {
		int numCards = 0;
		for (int n : deck.values())
			numCards += n;
		int index = (int)(numCards * Math.random());
		for (Map.Entry<EnergyType, Integer> entry : deck.entrySet()) {
			index -= entry.getValue();
			if (index < 0)
				return entry.getKey();
		}
		throw new RuntimeException("You fucked up the weighted random function");
	}
	
	private int mapSize(Map<EnergyType, Integer> m) {
		return m.values().stream().reduce(0, (x, y) -> x + y, (x, y) -> x + y);
	}
	
	private void playHand() {
		printInfo();
		// Draw a new hand
		Printer.printlnLeft(Format.ANSI_CYAN + "Drawing a new hand..." + Format.ANSI_RESET);
		for (int i = 0; i < handSize && mapSize(deck) > 0; i++) {
			EnergyType type = drawRandomCard();
			deck.put(type, deck.get(type) - 1);
			hand.put(type, hand.getOrDefault(type, 0) + 1);
		}
		
		// Keep making choices until the player discards the rest of the cards
		class BoolHolder {
			boolean val = false;
		}
		final BoolHolder done = new BoolHolder();
		while (!done.val) {
			if (mapSize(hand) == 0)
				break;
			// Print the hand to the user
			Printer.printLeft("Your hand: ");
			printMap(hand);
			// Get the list of all possible actions
			List<Option> options = new ArrayList<Option>();
			options.add(new Option() {
				@Override
				public String text() { return "Discard remaining cards"; }
				@Override
				public boolean isAllowed(Game game) { return true; }
				@Override
				public void execute(Game game) { done.val = true; }
			});
			options.addAll(EnergyRefinementOption.all);
			options.addAll(EnergyChannelOption.all);
			options.add(EnergySpringOption.get(numEnergySprings));
			options.addAll(DefensiveSpells.getOptions());
			options.addAll(OffensiveSpells.getOptions());
			options.add(Prayers.offer);
			
			// Filter the list to only allowed actions
			Option[] filteredOptions = options.stream().filter(o -> o.isAllowed(this)).toArray(Option[]::new);
			
			// Present to the user and make a choice
			if (filteredOptions.length == 1) {
				Printer.printlnLeft("Press any key to discard your remaining cards");
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
		}
		
		// Discard the rest of the hand
		for (Map.Entry<EnergyType, Integer> entry : hand.entrySet())
			discard.merge(entry.getKey(), entry.getValue(), (x, y) -> x + y);
		hand.clear();
	}

	private void playDay() {
		System.out.println("======================================== DAY " + turnNum + " =======================================");
		Printer.printLeft("Your deck: ");
		printMap(deck);
		// 1: Play all the hands
		while (mapSize(deck) > 0) {
			playHand();
		}
		
		// 1.5: Handle prayers
		Prayers.getPrayers(this);
		airDonations = 0;
		
		// 2: Handle golem damage
		enemyWalls -= numGolems;
		enemyWalls -= 2 * numFireGolems;
		if (enemyWalls <= 0)
			win();
		
		// 3: Re-shuffle deck and add raw energy
		deck = discard;
		discard = new HashMap<EnergyType, Integer>();
		int newEnergy = baseEnergy + numEnergySprings * (numEnergySprings + 1) / 2;
		deck.merge(EnergyType.RAW, newEnergy, (x, y) -> x + y);
		
		// 4: Enemy turn
		enemy.execute(this);
		turnNum++;
	}
	
	// Note: this function never returns; instead it ends the program once it finishes
	void play() {
		while (true)
			playDay();
	}
	
	Map<EnergyType, Integer> deck;
	Map<EnergyType, Integer> discard;
	Map<EnergyType, Integer> hand;
	private int turnNum;
	int handSize;
	int airDonations;
	int playerWalls;
	int numGolems;
	int numFireGolems;
	int enemyWalls;
	private int baseEnergy;
	int numEnergySprings;
	ResearchCosts researchCosts;
	boolean focused;
	int mirrorShieldLifetime;
	boolean hasShield;
	int tempWalls;
	int energyShieldLifetime;
	Enemy enemy;
}
