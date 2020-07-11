package energy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Prayers {
	static final Color color = Color.BrightPurple;
	
	public static List<Option> getOptions() {
		List<Option> ret = new LinkedList<Option>();
		ret.add(Research.levelOne);
		if (maxLevel > 1)
			ret.add(Research.levelTwo);
		if (maxLevel > 2)
			ret.add(Research.levelThree);
		ret.add(new NextPrayerLevel());
		return ret;
	}
	
	public static int numPrayers() {
		return remaining.getOrDefault(maxLevel, List.of()).size();
	}
	
	public static int getLevel() { return maxLevel; }
	
	private static int maxLevel = 1;

	private static class NextPrayerLevel implements Option {
		public ColoredString text() {
			return new ColoredString("Empower Spirit", color).append(": " + (5 * maxLevel) + " ")
					.append(EnergyType.airName).append(", " + maxLevel + " ").append(EnergyType.airName)
					.append(" per week -> more powerful prayers");
		}
		
		public boolean isAllowed(Game game) {
			return maxLevel < 3 && remaining.get(maxLevel).size() == 0 && game.hasAir(5 * maxLevel);
		}
		
		public void execute(Game game) {
			if (maxLevel == 1)
				Printer.printLeft("The air spirit hungrily gulps down the energy. "
						+ "Their new powerful aura makes you feel uneasy.", color);
			else
				Printer.printLeft("The air spirit visibly grows larger. "
					+ "You feel your mind beginning to crumble when in their presence.", color);
			game.spend(EnergyType.AIR, maxLevel * 5);
			game.addDailyAirCost(maxLevel);
			maxLevel++;
			
		}
	}
	
	static class Research implements Option {
		
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
			return new ColoredString("Request a new " + adj + "prayer", color);
		}
		
		@Override
		public boolean isAllowed(Game game) {
			return game.hasAir(game.researchCosts.prayerCost(level)) && remaining.get(level).size() > 0;
		}
		
		@Override
		public void execute(Game game) throws IllegalStateException {
			game.spend(EnergyType.AIR, game.researchCosts.prayerCost(level));
			Prayer p = remaining.get(level).remove((int)(Math.random() * remaining.get(level).size()));
			Printer.printlnLeft(p.text().reColor(Color.Default, color));
			if (remaining.get(maxLevel).isEmpty())
				Printer.printlnLeft("You've run out of prayers, but the spirit whispers of a forbidden "
						+ "ritual to strengthen themselves...", color);
			p.execute(game);
		}
		
		private int level;
	}
	
	private static Map<Integer, List<Prayer>> remaining = new HashMap<Integer, List<Prayer>>(Map.of(
			1, new ArrayList<Prayer>(List.of(new NextLevel(), new ResearchPoint(), new Channel(EnergyType.AIR))),
			2, new ArrayList<Prayer>(List.of(new Channel(EnergyType.WATER), new ResearchPoint(), new NextLevel())),
			3, new ArrayList<Prayer>(List.of(new Channel(EnergyType.EARTH), new Channel(EnergyType.FIRE), new ResearchPoint()))));
	
	private interface Prayer {
		void execute(Game game);
		ColoredString text();
	}
	
	private static class NextLevel implements Prayer {
		NextLevel() {}
		
		@Override
		public void execute(Game game) {
			DefensiveSpells.maxLevel++;
			OffensiveSpells.maxLevel++;
		}
		
		@Override
		public ColoredString text() {
			if (maxLevel == 1) {
				return new ColoredString("Your body morphs and changes to something barely recognizable as you "
						+ "gain the ability to cast very powerful spells.");
			} else {
				return new ColoredString("Your body flows like pudding and hardens into a solid crystal that "
						+ "can channel immense amounts of energy and cast unthinkable spells.");
			}
		}
	}
	
	private static class ResearchPoint implements Prayer {
		ResearchPoint() { }
		
		@Override
		public void execute(Game game) {
			game.researchCosts = ResearchCosts.AddResearchPoint(game.researchCosts);
			numPoints++;
		}
		
		@Override
		public ColoredString text() {
			switch (numPoints) {
			case 0:
				return new ColoredString("You gain a lifetime worth of spell theory in a single moment. "
						+ "You can research spells quicker.");
			case 1:
				return new ColoredString("Your mind struggles to keep up as you gain a deeper understanding "
						+ "of the world than every other creature combined. Researching new seplls is easy.");
			case 2:
				return new ColoredString("Your personality dissolves as the knowledge of a god overwhelms your "
						+ "mind. Researching complex new spells is as easy as addition.");
			}
			throw new RuntimeException("Unexpected number of research points: " + numPoints);
		}
		
		static int numPoints = 0;
	}
	
	private static class Channel implements Prayer {
		Channel(EnergyType type) {
			this.type = type;
		}
		
		@Override
		public void execute(Game game) {
			EnergyChannelOption.typeToOption.get(type).learn();
		}
		
		@Override
		public ColoredString text() {
			return new ColoredString("The mythical ").append(EnergyType.name(type))
					.append(" Channel appears in your hands! ").append(EnergyType.rawName)
					.append(" can now be converted to ").append(EnergyType.name(type))
					.append(" more efficiently!");
		}
		
		private final EnergyType type;
	}
}