package doctor.parser;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

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

public class JSON_Parser extends AbstractParser<JSONObject> {

	public JSON_Parser(String path) {
		super(path);
	}

	@Override
	protected JSONObject loadSource(String path) {
		try {
			String json = new String(Files.readAllBytes(Paths.get(path)));
			JSONParser parser = new JSONParser();
			JSONObject model = ((JSONObject) parser.parse(json));
			return model;
		} catch (IOException e) {
			// TODO : Throw appropriate exception
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO : Throw appropriate exception
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public DOCTOR_Root parse() {
		List<DOCTOR_Import> imports = null;
		DOCTOR_PlainText prelude = null;
		List<DOCTOR_Rule> rules = null;
		DOCTOR_Ordering order = null;
		DOCTOR_PlainText postlude = null;

		if (source().containsKey("imports")) {
			imports = parseImports(((JSONArray) source().get("imports")));
		}
		
		if (source().containsKey("prelude")) {
			prelude = new DOCTOR_PlainText(((String) source().get("prelude"))); 
		}
		
		if (source().containsKey("rules")) {
			rules = parseRules(((JSONArray) source().get("rules")));
		}
		
		if (source().containsKey("ordering")) {
			order = parseOrdering(((JSONArray) source().get("ordering")));
		}
		
		if (source().containsKey("postlude")) {
			postlude = new DOCTOR_PlainText(((String) source().get("postlude")));
		}
		return new DOCTOR_Root(imports, prelude, rules, order, postlude);
	}
	
	private List<DOCTOR_Import> parseImports(JSONArray imports){
		List<DOCTOR_Import> out = new ArrayList();
		for (Object url : imports) {
			out.add(new DOCTOR_Import((String) url));
		}
		return out;
	}
	
	private List<DOCTOR_Rule> parseRules(JSONArray rules){
		List<DOCTOR_Rule> out = new ArrayList();
		for (Object r : rules) {
			JSONObject rule = (JSONObject) r;
			String id = "";
			DOCTOR_Regex before = null;
			DOCTOR_Template after = null;
			
			if (rule.containsKey("id")) {
				id = (String) rule.get("id");
			}
			
			if (rule.containsKey("before")) {
				String regex = (String) rule.get("before");
				before = new DOCTOR_Regex(regex);
			}
			
			if (rule.containsKey("after")) {
				String template = (String) rule.get("after");
				after = new DOCTOR_Template(template);
			}
			
			out.add(new DOCTOR_Rule(id, before, after));
		}
		return out;
	}
	
	private DOCTOR_Ordering parseOrdering(JSONArray actions) {
		return new DOCTOR_Ordering(parseNestedCall(actions));
	}
	
	private List<DOCTOR_Call> parseNestedCall(JSONArray actions) {
		List<DOCTOR_Call> out = new ArrayList();
		for (Object action : actions) {
			if(action instanceof String) {
				out.add(new DOCTOR_DirectCall((String) action));
			} else if (action instanceof JSONArray) {
				DOCTOR_RecursiveCall call = new DOCTOR_RecursiveCall(parseNestedCall((JSONArray) action));
				out.add(call);
			}
		}
		return out;
	}

	/*
	 * { 
	 * imports : [urls], 
	 * prelude : "", 
	 * rules : [
	 * 				{id:"", before : "", after : ""}
	 * 			]
	 * ordering : [
	 * 				"rule",
	 * 				["recursion"]
	 * 			  ], 
	 * postlude : "" 
	 * }
	 */

}
