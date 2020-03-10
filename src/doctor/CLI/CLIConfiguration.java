package doctor.CLI;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class CLIConfiguration {
	
	Map<CLIOption,String> options;
	Map<CLIParameter,String> parameters;
	private String[] paramOrder;
	private String name;
	private String version;

	public CLIConfiguration(List<CLIOption> options, List<CLIParameter> parameters, String name, String version) {
		this.options = new HashMap<CLIOption, String>();
		this.parameters = new HashMap<CLIParameter, String>();
		this.options.put(new CLIOption("HELP", "Show help", 'h', "help"), null);
		for (CLIOption opt : options) {
			this.options.put(opt, null);
		}
		for (CLIParameter param : parameters) {
			this.parameters.put(param, null);
		}
		this.name = name;
		this.version = version;
		this.paramOrder = new String[parameters.size()];
		int i = 0;
		for (CLIParameter cliParameter : parameters) {
			paramOrder[i] = cliParameter.name;
			i++;
		}
	}
	
	public String getOptionByName(String name) {
		Set<CLIOption> opts = options.keySet();
		for (CLIOption option : opts) {
			if(name.equals(option.getName())){
				return options.get(option);
			}
		}
		return null;
	}
	
	public String getParameterByName(String name) {
		Set<CLIParameter> params = parameters.keySet();
		for (CLIParameter parameter : params) {
			if(name.equals(parameter.getName())){
				return parameters.get(parameter);
			}
		}
		return null;
	}
	
	public CLIOption getOption(String opt) {
		Set<CLIOption> opts = options.keySet();
		for (CLIOption option : opts) {
			if(opt.equals("-"+option.getShortOption()) || opt.equals("--"+option.getLongOption())) {
				return option;
			}
		}
		return null;
	}
	
	public void setValue(CLIOption option, String arg) {
		boolean validOption = this.options.containsKey(option);
		if(validOption) {
			this.options.put(option, arg);
		} else {
			// throw appropriate exception
		}
	}
	
	public void setValue(int parameterIndex, String arg) {
		String name = paramOrder[parameterIndex];
		
		Set<CLIParameter> params = parameters.keySet();
		for (CLIParameter parameter : params) {
			if(name.equals(parameter.name)) {
				this.parameters.put(parameter, arg);
				return;
			}
		}
		// throw appropriate exception
	}
	
	public String help() {
		String out = "usage: "+name;
		for (String param : paramOrder) {
			out += " "+param;
		}
		Set<CLIOption> optionSet = options.keySet();
		Map<String,String> optionDesc = new HashMap<String,String>();
		int maxlength = 0;
		for (CLIOption option : optionSet) {
			String shortOpt = "-"+option.getShortOption();
			String longOpt = option.getLongOption()==null?"":", --"+option.getLongOption();
			String desc = option.description==null?"":(option.description);
			String arg = option.hasArg()?(" <"+option.getArgName()+">"):"";
			
			if (maxlength < shortOpt.length() + longOpt.length()) {
				maxlength = shortOpt.length() + longOpt.length();
			}
			
			out += " [" + shortOpt + arg + "]";
			optionDesc.put(shortOpt+longOpt, desc);
		}
		out += "\n";
		Set<String> descSet = optionDesc.keySet();
		for (String str : descSet) {
			out += String.format("%1$-" + maxlength + "s", str);
			out += "\t" + optionDesc.get(str)+"\n";
		}
		
		
		return out;
	}

}
