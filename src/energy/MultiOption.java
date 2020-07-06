package energy;

import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Stream;

class MultiOption implements Option {
	MultiOption(String summary, Supplier<Stream<Option>> options) {
		this.summary = summary;
		this.options = options;
	}

	@Override
	public String text() {
		return summary;
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

	private String summary;
	private Supplier<Stream<Option>> options;
}


class MultiSpell extends MultiOption implements Spell {
	MultiSpell(String summary, String description, Supplier<Stream<Option>> options) {
		super(summary, options);
		desc = description;
	}
	
	@Override
	public String description() {
		return desc;
	}
	
	private String desc;
}