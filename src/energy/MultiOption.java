package energy;

import java.util.List;

public class MultiOption implements Option {
	public MultiOption(String summary, List<Option> options) {
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
