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
			String text = m.execute(game);
			text = text.substring(0, 1).toUpperCase() + text.substring(1).toLowerCase();
			Printer.printlnLeft(Format.obj.ANSI_CYAN() + text + Format.obj.ANSI_RESET());
			return;
		}
		// If you run out of turns, you lose
		game.lose();
	}
	
	private Map<Integer, List<Move>> moves;
	
	private static interface Move {
		String execute(Game game);
	}
			
	private static class Chain implements Move {
		
		Chain(Move m1, Move m2) {
			this.m1 = m1;
			this.m2 = m2;
		}
		
		@Override
		public String execute(Game game) {
			return m1.execute(game) + " and " + m2.execute(game);
		}
		
		private Move m1, m2;
	}
	
	private static class Damage implements Move {
		Damage(int dmg) {
			this.dmg = dmg;
		}
			
		@Override
		public String execute(Game game) {
			int actual = game.takeDamage(dmg);
			if (actual == dmg) {
				return "the enemy deals " + dmg + " damage to you";
			} else if (actual > 0) {
				return "the enemy tries to deal " + dmg + " damage, but only " + actual + " gets through";
			} else {
				return "the enemy tries to deal " + dmg + " damage, but it's all blocked";
			}
		}
		
		private int dmg;
	}
	
	private static class Steal implements Move {
		Steal(EnergyType type, int amt) {
			this.type = type;
			this.amt = amt;
		}
		
		@Override
		public String execute(Game game) {
			int actual = game.steal(type, amt);
			if (actual > 0) {
				return "the enemy steals " + actual + " " + EnergyType.name(type) + Format.obj.ANSI_CYAN()
						 + " energy from you";
			} else {
				return "the enemy tries to steal " + EnergyType.name(type) + Format.obj.ANSI_CYAN()
						+ " energy from you but fails";
			}
		}
		
		private int amt;
		private EnergyType type;
	}
	
	private static class StealAll implements Move {
		StealAll(EnergyType type) {
			this.type = type;
		}
		
		@Override
		public String execute(Game game) {
			int actual = game.stealAll(type);
			if (actual > 0) {
				return "the enemy steals all your " + EnergyType.name(type) + Format.obj.ANSI_CYAN()
						+ " from you";
			} else {
				return "the enemey tries to steal all your " + EnergyType.name(type) + Format.obj.ANSI_CYAN()
						+ " but fails";
			}
		}
		
		private EnergyType type;
	}
	
	private static class Heal implements Move {
		Heal(int amt) {
			this.amt = amt;
		}
		
		@Override
		public String execute(Game game) {
			game.buildEnemyWalls(amt);
			return "the enemy rebuilds " + amt + " walls";
		}
		private int amt;
	}
	
	private static class Pass implements Move {
		@Override
		public String execute(Game game) {
			return "the enemy prepares...";
		}
	}
}
