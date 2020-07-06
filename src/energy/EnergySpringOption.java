package energy;

public class EnergySpringOption implements Option {
	private static EnergySpringOption one = new EnergySpringOption(1);
	private static EnergySpringOption two = new EnergySpringOption(2);
	private static EnergySpringOption three = new EnergySpringOption(3);
	private static EnergySpringOption four = new EnergySpringOption(4);
	private static EnergySpringOption five = new EnergySpringOption(5);
	
	public static Option get(int numEnergySprings) {
		switch (numEnergySprings) {
		case 0:
			return one;
		case 1:
			return two;
		case 2:
			return three;
		case 3:
			return four;
		case 4:
			return five;
		case 5:
			return InvalidOption.val;
		default:
			throw new IllegalArgumentException("Invalid number of energy springs: " + numEnergySprings);
		}
	}
	
	private EnergySpringOption(int num) {
		this.num = num;
	}

	@Override
	public String text() {
		return "Fortify the " + Format.rainbow("energy spring") + ". 1 " +
				EnergyType.name(EnergyType.EARTH) + ", 1 " + 
				EnergyType.name(EnergyType.AIR) + ", 1 " +
				EnergyType.name(EnergyType.FIRE) + ", 1 " +
				EnergyType.name(EnergyType.WATER) + ", " +
				num + " " + EnergyType.name(EnergyType.RAW) +
				" -> +" + num + " " + EnergyType.name(EnergyType.RAW) + " per day";
	}

	@Override
	public boolean isAllowed(Game game) {
		return game.hasAir(1) && game.hasEarth(1) && game.hasWater(1) && game.hasFire(1) && game.hasRaw(num);
	}

	@Override
	public void execute(Game game) throws IllegalStateException {
		game.spend(EnergyType.AIR, 1);
		game.spend(EnergyType.EARTH, 1);
		game.spend(EnergyType.WATER, 1);
		game.spend(EnergyType.FIRE, 1);
		game.spend(EnergyType.RAW, num);
		game.numEnergySprings++;
	}
	final int num;
}
