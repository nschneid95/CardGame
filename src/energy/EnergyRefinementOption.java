package energy;

import java.util.List;

public class EnergyRefinementOption implements Option {
	public static EnergyRefinementOption fire = new EnergyRefinementOption(EnergyType.FIRE);
	public static EnergyRefinementOption water = new EnergyRefinementOption(EnergyType.WATER);
	public static EnergyRefinementOption earth = new EnergyRefinementOption(EnergyType.EARTH);
	public static EnergyRefinementOption air = new EnergyRefinementOption(EnergyType.AIR);
	
	public static List<EnergyRefinementOption> all = List.of(water, earth, fire, air);
	
	private EnergyRefinementOption(EnergyType type) {
		this.type = type;
	}
	
	@Override
	public ColoredString text() {
		return EnergyType.name(type).append(" refinement: 2 ").append(EnergyType.rawName).append(" -> 1 ")
				.append(EnergyType.name(type));
	}
	
	@Override
	public boolean isAllowed(Game game) {
		return game.hasRaw(2);
	}
	
	@Override
	public void execute(Game game) throws IllegalStateException {
		game.spend(EnergyType.RAW, 2);
		game.insert(type, 1);
	}
	
	private final EnergyType type;

}
