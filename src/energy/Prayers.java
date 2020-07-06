package energy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Prayers {
	
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
		return ret;
	}
	
	public static int numPrayers(int level) {
		return remaining.getOrDefault(level, List.of()).size();
	}
	
	private static int maxLevel = 1;
	
	static class Research implements Option {
		
		public static Option levelOne = new Research(1);
		public static Option levelTwo = new Research(2);
		public static Option levelThree = new Research(3);
		
		private Research(int level) {
			this.level = level;
		}
		
		@Override
		public String text() {
			return "Request a new level " + level + " prayer";
		}
		
		@Override
		public boolean isAllowed(Game game) {
			return game.hasAir(game.researchCosts.PrayerCost(level)) && remaining.get(level).size() > 0;
		}
		
		@Override
		public void execute(Game game) throws IllegalStateException {
			game.spend(EnergyType.AIR, game.researchCosts.PrayerCost(level));
			Prayer p = remaining.get(level).remove((int)(Math.random() * remaining.get(level).size()));
			Printer.printlnLeft(Format.ANSI_CYAN + p.text() + Format.ANSI_RESET);
			p.execute(game);
		}
		
		private int level;
	}
	
	private static Map<Integer, List<Prayer>> remaining = new HashMap<Integer, List<Prayer>>(Map.of(
			1, new ArrayList<Prayer>(List.of(new NextLevel(), new ResearchPoint(), new Channel(EnergyType.AIR))),
			2, new ArrayList<Prayer>(List.of(new Channel(EnergyType.WATER), new ResearchPoint(), new NextLevel())),
			3, new ArrayList<Prayer>(List.of(new Channel(EnergyType.EARTH), new Channel(EnergyType.FIRE), new ResearchPoint()))));
	//TODO Add energy storage
	
	private interface Prayer {
		void execute(Game game);
		String text();
	}
	
	private static class NextLevel implements Prayer {
		NextLevel() {}
		
		@Override
		public void execute(Game game) {
			DefensiveSpells.maxLevel++;
			OffensiveSpells.maxLevel++;
			maxLevel++;
		}
		
		@Override
		public String text() {
			return "New spell and prayer level unlocked!";
		}
	}
	
	private static class ResearchPoint implements Prayer {
		ResearchPoint() {}
		
		@Override
		public void execute(Game game) {
			game.researchCosts = ResearchCosts.AddResearchPoint(game.researchCosts);
		}
		
		@Override
		public String text() {
			return "Research Point gained!";
		}
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
		public String text() {
			return EnergyType.name(type) + Format.ANSI_CYAN + " Channel aquired!";
		}
		
		private final EnergyType type;
	}
}