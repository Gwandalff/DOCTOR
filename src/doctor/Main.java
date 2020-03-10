package doctor;

import java.io.IOException;

import doctor.CLI.CLIArgumentBuilder;
import doctor.CLI.CLICommand;
import doctor.CLI.CLIConfiguration;
import doctor.CLI.CLIOption;
import doctor.CLI.CLIParameter;
import doctor.parser.GenericParser;
import doctor.parser.nodes.DOCTOR_Root;
import doctor.processes.CompileSED;

/**
 * Main : Main class that process options and launch the compiler
 * 
 * @author gwandalf
 *
 */
public class Main extends CLICommand<Integer>{

	private String inputPath;

	private String outputPath = "doctor.out";

	public static void main(String[] args) {
		//new Main().launch(args);
		new Main().launch("-h");
	}

	@Override
	protected CLIConfiguration createConfiguration() {
		CLIArgumentBuilder builder = new CLIArgumentBuilder();
		return builder
				.addOption(   new CLIOption   ("OUTPUT", "The path of the resulting text processor", 'o', "output", "output"))
				.addParameter(new CLIParameter("INPUT",  "The DOCTOR file to compile"))
				.setCommandVersion("v0.1-SNAPSHOT")
				.setCommandName("doctor")
				.build();
	}
	
	@Override
	public void acceptConfiguration(CLIConfiguration conf) {
		inputPath = conf.getParameterByName("INPUT");
		String output = conf.getOptionByName("OUTPUT");
		if(output != null) {
			outputPath = output;
		}
	}
	
	@Override
	public Integer execute() {
		GenericParser parser = new GenericParser(inputPath);
		DOCTOR_Root root = parser.parse();

		CompileSED compiler = new CompileSED();
		root.accept(compiler);

		try {
			compiler.generateFile(outputPath);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 0;
	}

}
