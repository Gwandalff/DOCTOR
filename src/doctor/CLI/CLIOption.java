package doctor.CLI;

public class CLIOption extends CLIEntry {
	
	private final char shortOption;
	private final String longOption;
	private final boolean hasArg;
	private final String argName;

	public CLIOption(String name, String description, char shortOption, String longOption) {
		super(name, description);
		this.longOption = longOption;
		this.shortOption = shortOption;
		this.hasArg = false;
		this.argName = null;
	}
	
	public CLIOption(String name, String description, char shortOption, String longOption, String argName) {
		super(name, description);
		this.longOption = longOption;
		this.shortOption = shortOption;
		this.hasArg = true;
		this.argName = argName;
	}
	
	public char getShortOption() {
		return shortOption;
	}

	public String getLongOption() {
		return longOption;
	}

	public boolean hasArg() {
		return hasArg;
	}
	
	public String getArgName() {
		return argName;
	}
}
