package energy;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

public class Enemy {
	public Enemy(int difficulty) {
		int rev = 10 - difficulty;
		moves = new HashMap<Integer, List<Move>>();
		// Level 1
		List<Move> list = new ArrayList<Move>();
		list.addAll(Collections.nCopies(2 + rev / 2, new Pass()));
		list.addAll(Collections.nCopies(2, new Damage(1)));
		list.add(new Damage(2));
		moves.put(1, list);
		// Level 2
		list = new ArrayList<Move>();
		list.addAll(Collections.nCopies(2 + rev / 3, new Damage(1)));
		list.addAll(Collections.nCopies(2, new Damage(2)));
		list.add(new Damage(3));
		list.replaceAll(x -> new Chain(x, new Heal(1)));
		moves.put(2, list);
		// Level 3
		list = new ArrayList<Move>();
		list.addAll(Collections.nCopies(1 + rev / 4, new Damage(2)));
		list.addAll(Collections.nCopies(2, new Damage(3)));
		list.add(new Damage(4));
		list.add(new Steal(EnergyType.RAW, 4));
		list.replaceAll(x -> new Chain(x, new Heal(2)));
		moves.put(3, list);
		// Level 4
		list = new ArrayList<Move>();
		list.addAll(Collections.nCopies(2 + rev / 5, new Damage(4)));
		list.add(new Steal(EnergyType.RAW, 4));
		list.add(new Steal(EnergyType.AIR, 4));
		list.add(new Steal(EnergyType.WATER, 4));
		list.replaceAll(x -> new Chain(x, new Heal(4)));
		moves.put(4, list);
		// Level 5
		list = new ArrayList<Move>();
		list.addAll(Collections.nCopies(1 + rev / 6, new Damage(4)));
		list.add(new Damage(5));
		list.add(new Steal(EnergyType.RAW, 4));
		list.add(new Steal(EnergyType.EARTH, 4));
		list.add(new Steal(EnergyType.FIRE, 4));
		list.replaceAll(x -> new Chain(x, new Heal(5)));
		moves.put(5, list);
		// Level 6
		list = new ArrayList<Move>();
		list.addAll(Collections.nCopies(1 + rev / 7, new Damage(5)));
		list.add(new Damage(5));
		list.add(new StealAll(EnergyType.RAW));
		list.add(new StealAll(EnergyType.AIR));
		list.add(new StealAll(EnergyType.WATER));
		list.replaceAll(x -> new Chain(x, new Heal(5)));
		moves.put(6, list);
		// Level 7
		list = new ArrayList<Move>();
		list.addAll(Collections.nCopies(1 + rev / 8, new Damage(5)));
		list.add(new Damage(6));
		list.add(new StealAll(EnergyType.RAW));
		list.add(new StealAll(EnergyType.EARTH));
		list.add(new StealAll(EnergyType.FIRE));
		list.replaceAll(x -> new Chain(x, new Heal(5)));
		moves.put(7, list);
		// Level 8
		list = new ArrayList<Move>();
		list.addAll(Collections.nCopies(1 + rev / 9, new Damage(5)));
		list.add(new Damage(7));
		list.add(new Damage(10));
		list.add(new Chain(new StealAll(EnergyType.AIR), new StealAll(EnergyType.WATER)));
		list.add(new Chain(new StealAll(EnergyType.FIRE), new StealAll(EnergyType.EARTH)));
		list.replaceAll(x -> new Chain(x, new Heal(8)));
		moves.put(8, list);
	}
	
	public void execute(Game game) {
		for (int i : moves.keySet().stream().sorted().collect(Collectors.toList())) {
			List<Move> list = moves.get(i);
			if (list.isEmpty())
				continue;
			int index = (int)(Math.random() * list.size());
			Move m = list.remove(index);
			Printer.printlnLeft(m.execute(game).capitalizeFirst());
			return;
		}
		// If you run out of turns, you lose
		game.lose("You wake up to see the entire galactic fleet outside your window for a second before your entire base is vaporized.");
	}
	
	public int numTurns() {
		return moves.values().stream().mapToInt(List::size).sum();
	}
	
	private Map<Integer, List<Move>> moves;
	
	private static interface Move {
		ColoredString execute(Game game);
	}
			
	private static class Chain implements Move {
		
		Chain(Move m1, Move m2) {
			this.m1 = m1;
			this.m2 = m2;
		}
		
		@Override
		public ColoredString execute(Game game) {
			return m1.execute(game).append(" and ", Color.Cyan).append(m2.execute(game));
		}
		
		private Move m1, m2;
	}
	
	private static class Damage implements Move {
		Damage(int dmg) {
			this.dmg = dmg;
		}
			
		@Override
		public ColoredString execute(Game game) {
			int actual = game.takeDamage(dmg);
			if (actual == dmg) {
				return new ColoredString("the enemy deals " + dmg + " damage to you", Color.Cyan);
			} else if (actual > 0) {
				return new ColoredString("the enemy tries to deal " + dmg + " damage, but only " + actual
						+ " gets through", Color.Cyan);
			} else {
				return new ColoredString("the enemy tries to deal " + dmg + " damage, but it's all blocked", Color.Cyan);
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
		public ColoredString execute(Game game) {
			int actual = game.steal(type, amt);
			if (actual > 0) {
				return new ColoredString("the enemy steals " + actual + " ", Color.Cyan)
						.append(EnergyType.name(type)).append(" energy from you", Color.Cyan);
			} else {
				return new ColoredString("the enemy tries to steal ", Color.Cyan).append(EnergyType.name(type))
						.append(" energy from you but fails", Color.Cyan);
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
		public ColoredString execute(Game game) {
			int actual = game.stealAll(type);
			if (actual > 0) {
				return new ColoredString("the enemy steals all your ", Color.Cyan).append(EnergyType.name(type))
						.append(" from you", Color.Cyan);
			} else {
				return new ColoredString("the enemey tries to steal all your ", Color.Cyan)
						.append(EnergyType.name(type)).append(" but fails", Color.Cyan);
			}
		}
		
		private EnergyType type;
	}
	
	private static class Heal implements Move {
		Heal(int amt) {
			this.amt = amt;
		}
		
		@Override
		public ColoredString execute(Game game) {
			game.buildEnemyWalls(amt);
			return new ColoredString("the enemy rebuilds " + amt + " walls", Color.Cyan);
		}
		private int amt;
	}
	
	private static class Pass implements Move {
		@Override
		public ColoredString execute(Game game) {
			return new ColoredString("the enemy prepares...", Color.Cyan);
		}
	}
}
