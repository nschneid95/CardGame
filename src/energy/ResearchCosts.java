package energy;

public abstract class ResearchCosts {
	abstract int prayerCost(int level) throws IllegalArgumentException;
	abstract int spellCost(int level) throws IllegalArgumentException;
	
	public static ResearchCosts BaseCosts() {
		return Zero.obj;
	}
	
	public static ResearchCosts AddResearchPoint(ResearchCosts currCosts) throws IllegalArgumentException {
		if (currCosts == Zero.obj) {
			return One.obj;
		} else if (currCosts == One.obj) {
			return Two.obj;
		} else if (currCosts == Two.obj) {
			return Three.obj;
		} else {
			throw new IllegalArgumentException("Cannot have more than 3 research points!");
		}
	}

	private static class Zero extends ResearchCosts {
		private Zero() {}
		static Zero obj = new Zero();
		
		@Override
		public int prayerCost(int level) throws IllegalArgumentException {
			switch (level) {
			case 1:
				return 3;
			case 2:
				return 6;
			case 3:
				return 9;
			default:
				throw new IllegalArgumentException("Invalid prayer level: " + level);
			}
		}
		
		@Override
		public int spellCost(int level) throws IllegalArgumentException {
			switch (level) {
			case 1:
				return 2;
			case 2:
				return 4;
			case 3:
				return 8;
			default:
				throw new IllegalArgumentException("Invalid spell level: " + level);
			}
		}
	}
	
	private static class One extends ResearchCosts {
		private One() {}
		static One obj = new One();
		
		@Override
		public int prayerCost(int level) throws IllegalArgumentException {
			switch (level) {
			case 1:
				return 2;
			case 2:
				return 4;
			case 3:
				return 7;
			default:
				throw new IllegalArgumentException("Invalid prayer level: " + level);
			}
		}
		
		@Override
		public int spellCost(int level) throws IllegalArgumentException {
			switch (level) {
			case 1:
				return 2;
			case 2:
				return 3;
			case 3:
				return 4;
			default:
				throw new IllegalArgumentException("Invalid spell level: " + level);
			}
		}
	}
	
	private static class Two extends ResearchCosts {
		private Two() {}
		static Two obj = new Two();
		
		@Override
		public int prayerCost(int level) throws IllegalArgumentException {
			switch (level) {
			case 1:
				return 1;
			case 2:
				return 3;
			case 3:
				return 5;
			default:
				throw new IllegalArgumentException("Invalid prayer level: " + level);
			}
		}
		
		@Override
		public int spellCost(int level) throws IllegalArgumentException {
			switch (level) {
			case 1:
				return 1;
			case 2:
				return 2;
			case 3:
				return 3;
			default:
				throw new IllegalArgumentException("Invalid spell level: " + level);
			}
		}
	}
	
	private static class Three extends ResearchCosts {
		private Three() {}
		static Three obj = new Three();
		
		@Override
		public int prayerCost(int level) throws IllegalArgumentException {
			switch (level) {
			case 1:
				return 1;
			case 2:
				return 2;
			case 3:
				return 3;
			default:
				throw new IllegalArgumentException("Invalid prayer level: " + level);
			}
		}
		
		@Override
		public int spellCost(int level) throws IllegalArgumentException {
			switch (level) {
			case 1:
				return 1;
			case 2:
				return 1;
			case 3:
				return 2;
			default:
				throw new IllegalArgumentException("Invalid spell level: " + level);
			}
		}
	}
}