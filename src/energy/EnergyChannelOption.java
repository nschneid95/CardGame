package energy;

import java.util.List;
import java.util.Map;

public class EnergyChannelOption implements Option {
	public static EnergyChannelOption fire = new EnergyChannelOption(EnergyType.FIRE);
	public static EnergyChannelOption water = new EnergyChannelOption(EnergyType.WATER);
	public static EnergyChannelOption earth = new EnergyChannelOption(EnergyType.EARTH);
	public static EnergyChannelOption air = new EnergyChannelOption(EnergyType.AIR);
	
	public static List<EnergyChannelOption> all = List.of(fire, water, earth, air);
	
	public static Map<EnergyType, EnergyChannelOption> typeToOption = Map.of(
			EnergyType.FIRE, fire, EnergyType.WATER, water, EnergyType.AIR, air, EnergyType.EARTH, earth);
	
	private EnergyChannelOption(EnergyType type) {
		this.type = type;
		learned = false;
	}
	
	@Override
	public String text() {
		return "Channel 3 " + EnergyType.name(EnergyType.RAW) + " energy into 2 " + EnergyType.name(type) + " energy";
	}
	
	@Override
	public boolean isAllowed(Game game) {
		return learned && game.deck.getOrDefault(EnergyType.RAW, 0) >= 3;
	}
	
	@Override
	public void execute(Game game) throws IllegalStateException {
		game.spend(EnergyType.RAW, 3);
		game.insert(type, 2);
	}
	
	public void learn() {
		learned = true;
	}
	
	private final EnergyType type;
	private boolean learned;
}
