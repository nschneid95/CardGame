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
			String adj = "";
			switch (level) {
			case 2:
				adj = "powerful ";
				break;
			case 3:
				adj = "legendary ";
				break;
			}
			return "Request a new " + adj + "prayer";
		}
		
		@Override
		public boolean isAllowed(Game game) {
			return game.hasAir(game.researchCosts.prayerCost(level)) && remaining.get(level).size() > 0;
		}
		
		@Override
		public void execute(Game game) throws IllegalStateException {
			game.spend(EnergyType.AIR, game.researchCosts.prayerCost(level));
			Prayer p = remaining.get(level).remove((int)(Math.random() * remaining.get(level).size()));
			Printer.printlnLeft(Format.obj.ANSI_CYAN() + p.text() + Format.obj.ANSI_RESET());
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
			if (maxLevel == 1) {
				return "Your body morphs and changes to something barely recognizable as you gain the ability to cast very powerful spells.";
			} else {
				return "Your body flows like pudding and hardens into a solid crystal that can channel immense amounts of energy and cast unthinkable spells.";
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
		public String text() {
			switch (numPoints) {
			case 0:
				return "You gain a lifetime worth of spell theory in a single moment. You can research spells quicker.";
			case 1:
				return "Your mind struggles to keep up as you gain a deeper understanding of the world than every other creature combined. Researching new seplls is easy.";
			case 2:
				return "Your personality dissolves as the knowledge of a god overwhelms your mind. Researching complex new spells is as easy as addition.";
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
		public String text() {
			return "The mythical " + EnergyType.name(type) + Format.obj.ANSI_CYAN() + " Channel appears in your hands! "
					+ EnergyType.name(EnergyType.RAW) + Format.obj.ANSI_CYAN() + " can now be converted to "
					+ EnergyType.name(type) + Format.obj.ANSI_CYAN() + " more efficiently!";
		}
		
		private final EnergyType type;
	}
}