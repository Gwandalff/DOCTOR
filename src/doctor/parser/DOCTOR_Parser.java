package doctor.parser;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import doctor.parser.nodes.DOCTOR_Call;
import doctor.parser.nodes.DOCTOR_DirectCall;
import doctor.parser.nodes.DOCTOR_Import;
import doctor.parser.nodes.DOCTOR_Ordering;
import doctor.parser.nodes.DOCTOR_PlainText;
import doctor.parser.nodes.DOCTOR_RecursiveCall;
import doctor.parser.nodes.DOCTOR_Regex;
import doctor.parser.nodes.DOCTOR_Root;
import doctor.parser.nodes.DOCTOR_Rule;
import doctor.parser.nodes.DOCTOR_Template;

/**
 * DOCTOR_Parser : Parser for the DOCTOR Language
 * @author gwandalf
 *
 */
public class DOCTOR_Parser extends AbstractParser<String> { // TODO : create and throw appropriate exceptions
	
	private int offset = 0;
	
	private final List<Character> WS = Arrays.asList(' ','\r','\n','\t');
	
	public DOCTOR_Parser(String path) {
		super(path);
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
	
	/**
	 * checkKeyword : verify the presence of the keyword from index {@link DOCTOR_Parser#offset}
	 * if the keyword is found move the offset to the char after keyword else don't move
	 * @param keyword
	 * @return true if the keyword was found else false
	 */
	private boolean checkKeyword(String keyword) {
		int startOffset = offset;
		while(source().charAt(startOffset) != keyword.charAt(0)) {
			if(source().charAt(startOffset) == '#') {
				while(source().charAt(startOffset) != '\n') {
					startOffset++;
				}
			}
			if(!WS.contains(source().charAt(startOffset))) {
				return false;
			}
			startOffset++;
		}
		
		for (int i = 0; i < keyword.length(); i++) {
			if(source().charAt(startOffset+i) != keyword.charAt(i)) {
				return false;
			}
		}
		startOffset += keyword.length();
		offset = startOffset;
		return true;
	}

	/**
	 * ignoreSpace : move offset to the next non-blank char
	 */
	private void ignoreSpace() {
		while(WS.contains(source().charAt(offset))) {
			offset++;
		}
	}
	
	/**
	 * getBetween : return the string between start & end considering that offset is on start
	 * @param start : the starting char (need to be the char at source()[offset])
	 * @param end : ending char, when found stop
	 * @return the string between start and end
	 */
	private String getBetween(char start, char end) {
		StringBuilder text = new StringBuilder();
		if(source().charAt(offset) == start) {
			offset++;
			// TODO : check if not escaped
			while(source().charAt(offset) != end) {
				text.append(source().charAt(offset));
				offset++;
			}
			offset++;
			return text.toString();
		}
		return null;
	}
	
	/**
	 * cleanIndent : transform the indent of a multi-line text from raw to difference with closing char
	 * @param text : the raw text
	 * @return the text with indent as difference to enclosing char
	 */
	private String cleanIndent(String text) {
		String indent = "";
		for (int i = text.length() -1 ; i >= 0; i--) {
			char c = text.charAt(i);
			indent = c + indent;
			if(c == '\n')break;
		}
		String out = text.replace(indent, "\n");
		return out;
	}
	
	@Override
 	public DOCTOR_Root parse() {
		
		List<DOCTOR_Import> imports = new ArrayList<>();
		DOCTOR_Import i = null;
		while((i = parseImport()) != null) {
			imports.add(i);
		}
		
		DOCTOR_PlainText pre = parsePrelude();
		
		List<DOCTOR_Rule> rules = new ArrayList<>();
		DOCTOR_Rule r = null;
		while((r = parseRule()) != null) {
			rules.add(r);
		}
		
		DOCTOR_Ordering order = parseOrdering();
		
		DOCTOR_PlainText post = parsePostlude();
		
		return new DOCTOR_Root(imports, pre, rules, order, post);
	}
	
	private DOCTOR_Import parseImport() {
		if(!checkKeyword("import")) return null;
		ignoreSpace();
		String url = getBetween('"','"');
		if(url != null) return new DOCTOR_Import(url);
		// TODO : throw exceptions
		return null;
	}
	
	private DOCTOR_PlainText parsePrelude() {
		if(!checkKeyword("prelude")) return null;
		ignoreSpace();
		return parsePlainText();
	}
	
	private DOCTOR_Rule parseRule() {
		if(!checkKeyword("rule")) return null;
		ignoreSpace();
		String id = parseID();
		ignoreSpace();
		DOCTOR_Regex regex = parseRegex();
		ignoreSpace();
		if(!checkKeyword("->")) return null;
		ignoreSpace();
		DOCTOR_Template template = parseTemplate();
		if(regex == null || template == null) {
			//TODO : throw exception
			return null;
		}
		return new DOCTOR_Rule(id, regex, template);
	}
	
	private DOCTOR_Ordering parseOrdering() {
		if(!checkKeyword("order")) return null;
		ignoreSpace();
		List<DOCTOR_Call> order = parseCallBlock();
		return new DOCTOR_Ordering(order);
	}
	
	private DOCTOR_PlainText parsePostlude() {
		if(!checkKeyword("postlude")) return null;
		ignoreSpace();
		return parsePlainText();
	}
	
	private DOCTOR_PlainText parsePlainText() {
		String text = getBetween('{','}');
		if(text != null) {
			String cleanText = cleanIndent(text);
			int firstReturn = cleanText.indexOf("\n");
			cleanText = cleanText.substring(firstReturn+1,cleanText.length()-1);
			return new DOCTOR_PlainText(cleanText);
		}
		// TODO : throw exception
		return null;
	}
	
	private String parseID() {
		ignoreSpace();
		StringBuilder id = new StringBuilder();
		while(source().charAt(offset) != '"') {
			id.append(source().charAt(offset));
			offset++;
		}
		return id.toString();
	}
	
	private DOCTOR_Regex parseRegex() {
		String regex = getBetween('"','"');
		if(regex != null) return new DOCTOR_Regex(regex);
		// TODO : throw exception
		return null;
	}
	
	private DOCTOR_Template parseTemplate() {
		String template = getBetween('`','`');
		if(template != null) {
			String cleanTemplate = cleanIndent(template);
			int firstReturn = cleanTemplate.indexOf("\n");
			cleanTemplate = cleanTemplate.substring(firstReturn+1,cleanTemplate.length()-1);
			return new DOCTOR_Template(cleanTemplate);
		}
		// TODO : throw exception
		return null;
	}
	
	private List<DOCTOR_Call> parseCallBlock() {
		List<DOCTOR_Call> out = new ArrayList();
		if(!checkKeyword("{")) return out;
		ignoreSpace();
		while(source().charAt(offset) != '}') {
			DOCTOR_Call c = parseCall();
			out.add(c);
			ignoreSpace();
		}
		return out;
	}
	
	private DOCTOR_Call parseCall() {
		if(checkKeyword("recursive")) {
			ignoreSpace();
			List<DOCTOR_Call> subs = parseCallBlock();
			return new DOCTOR_RecursiveCall(subs);
		} else {
			String id = parseID();
			ignoreSpace();
			return new DOCTOR_DirectCall(id);
		}
	}

	
}
