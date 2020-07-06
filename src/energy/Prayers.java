package energy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Prayers {
	
	public static Option offer = new MultiOption("Offer air to your guardian spirit", Offer.all);
	
	public static void getPrayers(Game game) {
		for (int lvl = maxLevel; lvl > 0; lvl--) {
			List<Prayer> prayers = remaining.get(lvl);
			while (prayers.size() > 0 && game.airDonations >= game.researchCosts.PrayerCost(lvl)) {
				game.airDonations -= game.researchCosts.PrayerCost(lvl);
				int index = (int)(Math.random() * prayers.size());
				Prayer p = prayers.remove(index);
				p.execute(game);
				Printer.printlnLeft(Format.ANSI_CYAN + p.text() + Format.ANSI_RESET);
			}
		}
	}
	
	public static int numPrayers(int level) {
		return remaining.getOrDefault(level, List.of()).size();
	}
	
	private static int maxLevel = 1;
	
	private static class Offer implements Option {
		static List<Option> all = List.of(
				new Offer(1), new Offer(2),
				new Offer(3), new Offer(4),
				new Offer(5), new Offer(6),
				new Offer(7), new Offer(8),
				new Offer(9), new Offer(10));
		
		Offer(int n) {
			this.n = n;
		}
		
		@Override
		public String text() {
			return "Sacrifice " + n + " Air energy as an offering to your guardian spirit";
		}
		
		@Override
		public boolean isAllowed(Game game) {
			return n > 0 && game.hasAir(n);
		}
		
		@Override
		public void execute(Game game) throws IllegalStateException {
			game.spend(EnergyType.AIR, n);
			game.airDonations += n;
		}
		
		private int n;
	}

	private static Map<Integer, List<Prayer>> remaining = new HashMap<Integer, List<Prayer>>(Map.of(
			1, new ArrayList<Prayer>(List.of(new NextLevel(), new ResearchPoint(), new HandSize(1), new Channel(EnergyType.AIR))),
			2, new ArrayList<Prayer>(List.of(new Channel(EnergyType.WATER), new HandSize(2), new ResearchPoint(), new NextLevel())),
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
	
	private static class HandSize implements Prayer {
		HandSize(int increase) {
			this.increase = increase;
		}
		
		@Override
		public void execute(Game game) {
			game.handSize += increase;
		}
		
		@Override
		public String text() {
			return "Hand size increased by " + increase;
		}
		
		private final int increase;
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