package energy;

import java.util.List;

class MultiOption implements Option {
	MultiOption(String summary, List<Option> options) {
		this.summary = summary;
		this.options = options;
	}

	@Override
	public String text() {
		return summary;
	}

	@Override
	public boolean isAllowed(Game game) {
		for (Option o : options) {
			if (o.isAllowed(game))
				return true;
		}
		return false;
	}

	@Override
	public void execute(Game game) throws IllegalStateException {
		Option[] filteredOptions = options.stream().filter(x -> x.isAllowed(game)).toArray(Option[]::new);
		int i = Selection.makeSelection(filteredOptions);
		filteredOptions[i].execute(game);
	}

	private String summary;
	private List<Option> options;
}


class MultiSpell extends MultiOption implements Spell {
	MultiSpell(String summary, String description, List<Option> options) {
		super(summary, options);
		desc = description;
	}
	
	@Override
	public String description() {
		return desc;
	}
	
	private String desc;
}