package doctor;

import java.io.IOException;

import doctor.parser.GenericParser;
import doctor.parser.nodes.DOCTOR_Root;
import doctor.processes.CompileSED;

/**
 * Main : Main class that process options and launch the compiler
 * @author gwandalf
 *
 */
public class Main {
	// TODO : Make a real launcher
	public static void main(String[] args) {
		GenericParser parser = new GenericParser(args[0]);
		DOCTOR_Root root = parser.parse();
		
		CompileSED compiler = new CompileSED();
		root.accept(compiler);
		
		try {
			compiler.generateFile(args[1]);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
