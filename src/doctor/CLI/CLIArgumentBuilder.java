package doctor.CLI;

import java.util.ArrayList;
import java.util.List;

public class CLIArgumentBuilder {

	private List<CLIOption> options; 
	private List<CLIParameter> parameters;
	private String name;
	private String version;
	
	public CLIArgumentBuilder() {
		options = new ArrayList<CLIOption>();
		parameters = new ArrayList<CLIParameter>();
		this.name = "command";
		this.version = "v0.1-SNAPSHOT";
	}
	
	public CLIArgumentBuilder addOption(CLIOption option) {
		options.add(option);
		return this;
	}
	
	public CLIArgumentBuilder addParameter(CLIParameter parameter) {
		parameters.add(parameter);
		return this;
	}
	
	public CLIArgumentBuilder setCommandName(String name) {
		this.name = name;
		return this;
	}
	
	public CLIArgumentBuilder setCommandVersion(String version) {
		this.version = version;
		return this;
	}
	
	public CLIConfiguration build() {
		return new CLIConfiguration(options, parameters, name, version);
	}

}
