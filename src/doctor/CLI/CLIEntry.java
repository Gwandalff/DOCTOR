package doctor.CLI;

public abstract class CLIEntry {
	
	protected final String name;
	protected final String description;

	public CLIEntry(String name, String description) {
		this.description = description;
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}
}
