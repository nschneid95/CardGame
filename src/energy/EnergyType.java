package energy;

import java.util.Map;
import java.util.Collections;
import java.util.HashMap;

public enum EnergyType {
	RAW,
	WATER,
	EARTH,
	FIRE,
	AIR;
	
	public static ColoredString name(EnergyType type) {
		switch (type) {
		case RAW:
			return new ColoredString("Raw", Color.Purple);
		case WATER:
			return new ColoredString("Water", Color.Blue);
		case EARTH:
			return new ColoredString("Earth", Color.Green);
		case FIRE:
			return new ColoredString("Fire", Color.Red);
		case AIR:
			return new ColoredString("Air", Color.Yellow);
		default:
			throw new RuntimeException("Unexpected energy type: " + type);
		}
	}
	
	public final static ColoredString rawName = name(RAW);
	public final static ColoredString waterName = name(WATER);
	public final static ColoredString earthName = name(EARTH);
	public final static ColoredString fireName = name(FIRE);
	public final static ColoredString airName = name(AIR);

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