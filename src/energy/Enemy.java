package energy;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

public class Enemy {
	public Enemy() {
		moves = new HashMap<Integer, List<Move>>();
		// Level 1
		List<Move> list = new ArrayList<Move>();
		list.addAll(Collections.nCopies(5, new Pass()));
		list.addAll(Collections.nCopies(4, new Damage(1)));
		list.add(new Damage(2));
		moves.put(1, list);
		// Level 2
		list = new ArrayList<Move>();
		list.addAll(Collections.nCopies(5, new Damage(1)));
		list.addAll(Collections.nCopies(4, new Damage(2)));
		list.add(new Damage(3));
		list.replaceAll(x -> new Chain(x, new Heal(1)));
		moves.put(2, list);
		// Level 3
		list = new ArrayList<Move>();
		list.addAll(Collections.nCopies(2, new Damage(2)));
		list.addAll(Collections.nCopies(4, new Damage(3)));
		list.addAll(Collections.nCopies(2, new Damage(4)));
		list.addAll(Collections.nCopies(2, new Steal(EnergyType.RAW, 4)));
		list.replaceAll(x -> new Chain(x, new Heal(2)));
		moves.put(3, list);
		// Level 4
		list = new ArrayList<Move>();
		list.addAll(Collections.nCopies(4, new Damage(4)));
		list.addAll(Collections.nCopies(2, new Steal(EnergyType.RAW, 4)));
		for (EnergyType type : EnergyType.values())
			if (type != EnergyType.RAW)
				list.add(new Steal(type, 3));
		list.replaceAll(x -> new Chain(x, new Heal(4)));
		moves.put(4, list);
		// Level 5
		list = new ArrayList<Move>();
		list.addAll(Collections.nCopies(2, new Damage(4)));
		list.addAll(Collections.nCopies(2, new Damage(5)));
		list.addAll(Collections.nCopies(2, new StealAll(EnergyType.RAW)));
		for (EnergyType type : EnergyType.values())
			if (type != EnergyType.RAW)
				list.add(new StealAll(type));
		list.replaceAll(x -> new Chain(x, new Heal(5)));
		moves.put(5, list);
		// Level 6
		list = new ArrayList<Move>();
		list.addAll(Collections.nCopies(2, new Damage(4)));
		list.addAll(Collections.nCopies(2, new Damage(5)));
		list.addAll(Collections.nCopies(2, new StealAll(EnergyType.RAW)));
		for (EnergyType type : EnergyType.values())
			if (type != EnergyType.RAW)
				list.add(new StealAll(type));
		list.replaceAll(x -> new Chain(x, new Heal(6)));
		moves.put(6, list);
		// Level 7
		list = new ArrayList<Move>();
		list.addAll(Collections.nCopies(5, new Damage(5)));
		list.addAll(Collections.nCopies(2, new Damage(7)));
		list.add(new Damage(10));
		list.add(new Chain(new StealAll(EnergyType.AIR), new StealAll(EnergyType.WATER)));
		list.add(new Chain(new StealAll(EnergyType.FIRE), new StealAll(EnergyType.EARTH)));
		list.replaceAll(x -> new Chain(x, new Heal(8)));
		moves.put(7, list);
	}
	
	public void execute(Game game) {
		for (int i : moves.keySet().stream().sorted().collect(Collectors.toList())) {
			List<Move> list = moves.get(i);
			if (list.isEmpty())
				continue;
			int index = (int)(Math.random() * list.size());
			Move m = list.remove(index);
			String text = m.text();
			text = text.substring(0, 1).toUpperCase() + text.substring(1).toLowerCase();
			Printer.printlnLeft(Format.ANSI_CYAN + text + Format.ANSI_RESET);
			m.execute(game);
			return;
		}
		// If you run out of turns, you lose
		game.lose();
	}
	
	private Map<Integer, List<Move>> moves;
	
	private static interface Move {
		void execute(Game game);
		String text();
	}
			
	private static class Chain implements Move {
		
		Chain(Move m1, Move m2) {
			this.m1 = m1;
			this.m2 = m2;
		}
		
		@Override
		public void execute(Game game) {
			m1.execute(game);
			m2.execute(game);
		}
		
		@Override
		public String text() {
			return m1.text() + " and " + m2.text();
		}
		
		private Move m1, m2;
	}
	
	private static class Damage implements Move {
		Damage(int dmg) {
			this.dmg = dmg;
		}
			
		@Override
		public void execute(Game game) {
			game.playerWalls -= dmg;
			if (game.playerWalls <= 0)
				game.lose();
		}
		
		@Override
		public String text() {
			return "the enemy deals " + dmg + " damage to you";
		}
		
		private int dmg;
	}
	
	private static class Steal implements Move {
		Steal(EnergyType type, int amt) {
			this.type = type;
			this.amt = amt;
		}
		
		@Override
		public void execute(Game game) {
			int curr = game.deck.getOrDefault(type, 0);
			int newVal = curr > amt ? curr - amt : 0;
			game.deck.put(type, newVal);
		}
		
		@Override
		public String text() {
			return "the enemy steals up to " + amt + " " + EnergyType.name(type) + " energy from you";
		}
		
		private int amt;
		private EnergyType type;
	}
	
	private static class StealAll implements Move {
		StealAll(EnergyType type) {
			this.type = type;
		}
		
		@Override
		public void execute(Game game) {
			game.deck.put(type, 0);
		}
		
		@Override
		public String text() {
			return "the enemy steals all your " + EnergyType.name(type) + " from you";
		}
		
		private EnergyType type;
	}
	
	private static class Heal implements Move {
		Heal(int amt) {
			this.amt = amt;
		}
		
		@Override
		public void execute(Game game) {
			game.enemyWalls += amt;
		}
		
		@Override
		public String text() {
			return "the enemy rebuilds " + amt + " walls";
		}
		
		private int amt;
	}
	
	private static class Pass implements Move {
		@Override
		public void execute(Game game) {}
		
		@Override
		public String text() {
			return "the enemy prepares...";
		}
	}
}
