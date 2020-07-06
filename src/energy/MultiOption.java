package energy;

import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Stream;

class MultiSpell implements Spell {
	MultiSpell(Supplier<String> color, String name, String desc, Supplier<Stream<Option>> options) {
		this.name = color.get() + name + Format.obj.ANSI_RESET();
		this.desc = desc;
		this.options = options;
	}

	@Override
	public String text() {
		return name;
	}
	
	@Override
	public String description() {
		return name + ": " + desc;
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

	private String name, desc;
	private Supplier<Stream<Option>> options;
}