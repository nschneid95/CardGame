package energy;

public class ResearchCosts {
	
	private ResearchCosts(int numPoints) {
		this.numPoints = numPoints;
	}
	
	int prayerCost(int level) {
		assert 1 <= level;
		assert level <= 3;
		return level * 3;
	}
	
	int spellCost(int level) throws IllegalArgumentException {
		int cost = 1;
		for (int i = 0; i < level; i++)
			cost *= 2;
		for (int i = 0; i < numPoints; i++)
			cost -= level == 3 ? 2 : 1;
		return cost > 0 ? cost : 1;
	}
	
	private int numPoints;
	
	public static ResearchCosts BaseCosts() {
		return new ResearchCosts(0);
	}
	
	public static ResearchCosts AddResearchPoint(ResearchCosts currCosts) {
		return new ResearchCosts(currCosts.numPoints + 1);
	}
}