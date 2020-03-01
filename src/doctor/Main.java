package doctor;

import java.io.IOException;

import doctor.parser.DOCTOR_Parser;
import doctor.parser.nodes.DOCTOR_Root;
import doctor.processes.ASTVizualizer;
import doctor.processes.CompileSED;
import doctor.processes.PrettyPrinter;

/**
 * Main : Main class that process options and launch the compiler
 * @author gwandalf
 *
 */
public class Main {
	// TODO : Make a real launcher
	public static void main(String[] args) {
		try {
			DOCTOR_Parser parser = new DOCTOR_Parser(args[0]);
			DOCTOR_Root root = parser.parse();
			CompileSED compiler = new CompileSED();
			root.accept(compiler);
			compiler.generateFile(args[1]);
		} catch (IOException e) {
			System.out.println("ERROR during load of the source file");
		}

	}

}
