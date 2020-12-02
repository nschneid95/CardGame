package energy;

import java.util.List;
import java.util.Map;
import java.util.Arrays;
import java.util.HashMap;

public class EnergyChannelOption implements Option {
	public static EnergyChannelOption fire = new EnergyChannelOption(EnergyType.FIRE);
	public static EnergyChannelOption water = new EnergyChannelOption(EnergyType.WATER);
	public static EnergyChannelOption earth = new EnergyChannelOption(EnergyType.EARTH);
	public static EnergyChannelOption air = new EnergyChannelOption(EnergyType.AIR);
	
	public static List<EnergyChannelOption> all = Arrays.asList(water, earth, fire, air);
	
	public static Map<EnergyType, EnergyChannelOption> typeToOption = new HashMap<EnergyType, EnergyChannelOption>();
	static {
		typeToOption.put(EnergyType.FIRE, fire);
		typeToOption.put(EnergyType.WATER, water);
		typeToOption.put(EnergyType.AIR, air);
		typeToOption.put(EnergyType.EARTH, earth);
	}
	
	private EnergyChannelOption(EnergyType type) {
		this.type = type;
		learned = false;
	}
	
	@Override
	public ColoredString text() {
		return EnergyType.name(type).append(" Channel: 3 ").append(EnergyType.rawName).append(" -> 2 ")
				.append(EnergyType.name(type));
	}
	
	@Override
	public boolean isAllowed(Game game) {
		return learned && game.hasRaw(3);
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
