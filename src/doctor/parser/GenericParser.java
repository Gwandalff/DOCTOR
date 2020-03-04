package doctor.parser;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import doctor.parser.nodes.DOCTOR_Root;

public class GenericParser extends AbstractParser<String> {
	
	private String path;

	public GenericParser(String path) {
		super(path);
		this.path = path;
	}

	@Override
	protected String loadSource(String path) {
		String out;
		try {
			out = new String ( Files.readAllBytes( Paths.get(path) ) );
			return out;
		} catch (IOException e) {
			// TODO : Throw appropriate exceptions
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public DOCTOR_Root parse() {
		if (path.endsWith(".doctor")) {
			DOCTOR_Parser dParser = new DOCTOR_Parser(path);
			return dParser.parse();
		} else if (path.endsWith(".json")) {
			JSON_Parser jParser = new JSON_Parser(path);
			return jParser.parse();
		}
		return null;
	}

}
