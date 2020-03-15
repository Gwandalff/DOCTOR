package doctor.CLI;

import java.util.Map.Entry;
import java.util.Set;

public class CLIArgumentParser {
	
	private CLIConfiguration configuration;

	public CLIArgumentParser(CLIConfiguration config) {
		this.configuration = config;
	}
	
	public CLIArgumentParser parse(String... args) {
		int iParameter = 0;
		for (int i = 0; i < args.length; i++) {
			if(args[i].startsWith("-")) {
				CLIOption option = configuration.getOption(args[i]);
				if(option.hasArg()) {
					i++;
					String value = args[i];
					configuration.setValue(option, value);
				} else {
					configuration.setValue(option, args[i]);
				}
			} else {
				configuration.setValue(iParameter, args[i]);
				iParameter++;
			}
		}
		return this;
	}
	
	public CLIArgumentParser validate() {
		boolean needHelp = configuration.getOptionByName("HELP") != null;
		if (needHelp) {
			System.out.println(configuration.help());
			System.exit(0);
		}
		boolean notEnoughParam = configuration.parameters.containsValue(null);
		Set<Entry<CLIOption, String>> mapping = configuration.options.entrySet();
		boolean argArePassed = true;
		for (Entry<CLIOption, String> entry : mapping) {
			argArePassed &= entry.getKey().hasArg() && entry.getValue() != null;
		}
		
		return this;
	}

}
