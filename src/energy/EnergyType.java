package energy;

import java.util.Map;
import java.util.Collections;
import java.util.HashMap;

public enum EnergyType {
	RAW,
	EARTH,
	WATER,
	AIR,
	FIRE;
	
	public static String name(EnergyType type) {
		switch (type) {
		case RAW:
			return Format.ANSI_PURPLE + "Raw" + Format.ANSI_RESET;
		case EARTH:
			return Format.ANSI_GREEN + "Earth" + Format.ANSI_RESET;
		case WATER:
			return Format.ANSI_BLUE + "Water" + Format.ANSI_RESET;
		case AIR:
			return Format.ANSI_YELLOW + "Air" + Format.ANSI_RESET;
		case FIRE:
			return Format.ANSI_RED + "Fire" + Format.ANSI_RESET;
		default:
			throw new RuntimeException("Unexpected energy type: " + type);
		}
	}

	public static class Counter {
		public Counter() {
			map = new HashMap<EnergyType, Integer>();
		}
		
		public Counter addRaw(int num) {
			map.put(RAW, map.getOrDefault(RAW, 0) + num);
			return this;
		}
		
		public Counter addEarth(int num) {
			map.put(EARTH, map.getOrDefault(EARTH, 0) + num);
			return this;
		}
		
		public Counter addWater(int num) {
			map.put(WATER, map.getOrDefault(WATER, 0) + num);
			return this;
		}
		
		public Counter addAir(int num) {
			map.put(AIR,  map.getOrDefault(AIR, 0) + num);
			return this;
		}
		
		public Counter addFire(int num) {
			map.put(FIRE, map.getOrDefault(FIRE, 0) + num);
			return this;
		}
		
		public Map<EnergyType, Integer> getMap() {
			return Collections.unmodifiableMap(map);
		}
		
		private Map<EnergyType, Integer> map;
	}
}