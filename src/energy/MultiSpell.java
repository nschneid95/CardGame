package energy;

import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Stream;

class MultiSpell implements Spell {
	MultiSpell(ColoredString name, ColoredString desc, Supplier<Stream<Option>> options) {
		this.name = name;
		this.desc = desc;
		this.options = options;
	}
	
	MultiSpell(String name, Color nameColor, ColoredString desc, Supplier<Stream<Option>> options) {
		this(new ColoredString(name, nameColor), desc, options);
	}
	
	MultiSpell(String name, Color nameColor, String desc, Supplier<Stream<Option>> options) {
		this(new ColoredString(name, nameColor), new ColoredString(desc), options);
	}

	@Override
	public ColoredString text() {
		return name;
	}
	
	@Override
	public ColoredString description() {
		return name.append(new ColoredString(": ")).append(desc);
	}

	@Override
	public boolean isAllowed(Game game) {
		Optional<Option> first = options.get().findFirst();
		return first.isPresent() && first.get().isAllowed(game);
	}

	@Override
	public void execute(Game game) throws IllegalStateException {
		Option[] filteredOptions = options.get().takeWhile(x -> x.isAllowed(game)).toArray(Option[]::new);
		int i = Selection.makeSelection(filteredOptions);
		filteredOptions[i].execute(game);
	}

	private ColoredString name, desc;
	private Supplier<Stream<Option>> options;
}