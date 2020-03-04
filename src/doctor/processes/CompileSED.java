package doctor.processes;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import doctor.parser.AbstractParser;
import doctor.parser.GenericParser;
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
 * CompileSED : compiler of the DOCTOR Language to a bash script based on sed commands
 * @author gwandalf
 *
 */
public class CompileSED implements IVisitor {
	
	/**
	 * allRules : all of the rules that can be used in the compiler(imported or not)
	 */
	private final List<DOCTOR_Rule> allRules;
	
	/**
	 * output : content of the script to generate
	 */
	private final StringBuilder output;
	
	/**
	 * orderNestedLevel : index representing the depth in nested ordering(i.e. recursive rules)
	 */
	private int orderNestedLevel = 0;
	
	public CompileSED() {
		allRules = new ArrayList<>();
		output = new StringBuilder();
	}
	
	/**
	 * displayOutput : display the content of the script in the standard output
	 */
	public void displayOutput() {
		System.out.println(output);
	}
	
	/**
	 * generateFile : generate the script at a given location
	 * @param url : path of the generated file.
	 * @throws IOException
	 */
	public void generateFile(String url) throws IOException {
		BufferedWriter writer = new BufferedWriter(new FileWriter(url));
	    writer.write(output.toString());
	    writer.close();
	}

	@Override
	public void visit(DOCTOR_Root node) {
		System.out.println("Compile Root");
		output.append("#!/bin/bash\n");
		output.append("cat $1 > DoctorTemp\n\n");
		
		DOCTOR_PlainText pre = node.getPrelude();
		if(pre != null) {
			System.out.println("Register prelude");
			output.append("prelude=");
			pre.accept(this);
		}
		
		DOCTOR_PlainText post = node.getPostlude();
		if(post != null) {
			System.out.println("Register postlude");
			output.append("postlude=");
			post.accept(this);
		}
		
		if (node.getImports() != null) {
			System.out.println("Compile imports");
			for (DOCTOR_Import i : node.getImports()) {
				i.accept(this);
			}
		}
		
		if (node.getRules() != null) {
			System.out.println("Process Rules");
			for (DOCTOR_Rule r : node.getRules()) {
				allRules.add(r);
			}
		}		
		
		DOCTOR_Ordering order = node.getOrder();
		if(order != null) {
			System.out.println("Compile with ordering");
			order.accept(this);
		} else {
			System.out.println("Compile without order");
			for (DOCTOR_Rule rule : allRules) {
				rule.accept(this);
			}
		}
		
		System.out.println("Generate final merge");
		output.append("\necho -e $prelude > output.dr\n");
		output.append("cat DoctorTemp >> output.dr\n");
		output.append("echo -e $postlude >> output.dr\n");
		output.append("rm temp");

		
	}

	@Override
	public void visit(DOCTOR_Import node) {
		GenericParser importParser = new GenericParser(node.getURL());
		DOCTOR_Root importTree = importParser.parse();
		CompileSED compiler = new CompileSED();
		importTree.accept(compiler);
		allRules.addAll(compiler.allRules);

	}

	@Override
	public void visit(DOCTOR_PlainText node) {
		output.append("\"" + escape(node.getContent()) + "\"\n");
	}

	@Override
	public void visit(DOCTOR_Rule node) {
		String regex = node.getBEFORE().getRegex();
		Map<String,Integer> table = groupName(regex);
		String template = node.getAFTER().getTemplate();
		
		for(Map.Entry<String,Integer> entry : table.entrySet()){
			template = template.replaceAll("<"+entry.getKey()+">", ">>>"+entry.getValue());
		}
		
		String cleanRegex = regex.replaceAll("\\>",">").replaceAll("\\)<[A-Za-z0-9]*>", "\\)");
		
		StringBuilder cmd = new StringBuilder();
		cmd.append("sed -E 's<");
		cmd.append(cleanRegex);
		cmd.append("<");
		cmd.append(escapeSed(template));
		cmd.append("<' DoctorTemp > temp\n");
		cmd.append("cat temp > DoctorTemp\n");
		
		String sedRule = String.join("\\", cmd.toString().split(">>>"));
		
		output.append(sedRule);
	}

	@Override
	public void visit(DOCTOR_Ordering node) {
		List<DOCTOR_Call> actions = node.getRules();
		for(DOCTOR_Call action : actions){
			if(action instanceof DOCTOR_DirectCall){
				((DOCTOR_DirectCall) action).accept(this);
			} else if(action instanceof DOCTOR_RecursiveCall){
				orderNestedLevel++;
				((DOCTOR_RecursiveCall) action).accept(this);
				orderNestedLevel--;
			}
		}
	}

	@Override
	public void visit(DOCTOR_Regex node) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(DOCTOR_Template node) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(DOCTOR_Call node) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(DOCTOR_DirectCall node) {
		DOCTOR_Rule ruleToCall = null;
		for (DOCTOR_Rule rule : allRules) {
			if(rule.getID().equals(node.getID())) {
				ruleToCall = rule;
			}
		}
		if(ruleToCall != null) {
			ruleToCall.accept(this);
		}
	}

	@Override
	public void visit(DOCTOR_RecursiveCall node) {
		List<DOCTOR_Call> actions = node.getSubcalls();
		StringBuilder out = new StringBuilder();
		output.append("cat DoctorTemp > TempRecursion"+orderNestedLevel+"\n");
		output.append("while : ; do\n");
		for(DOCTOR_Call action : actions){
			if(action instanceof DOCTOR_DirectCall){
				((DOCTOR_DirectCall) action).accept(this);
			} else if(action instanceof DOCTOR_RecursiveCall){
				orderNestedLevel++;
				((DOCTOR_RecursiveCall) action).accept(this);
				orderNestedLevel--;
			}
		}
		output.append("diff -q DoctorTemp TempRecursion"+orderNestedLevel+"\n");
		output.append("[[ $? -ne 0 ]] || break\n");
		output.append("cat DoctorTemp > TempRecursion"+orderNestedLevel+"\n");
		output.append("done\n");
	}
	
	/**
	 * groupName : map the name given to  group to the index used by sed
	 * @param rule
	 * @return the mapping name-index
	 */
	private Map<String,Integer> groupName(String rule){
		int counter = 1;
		int toName = -1;
		String name = "";
		
		boolean escaped = false;
		boolean closeBracket = false;
		boolean namedGroup = false;
		
		String[] chars = rule.split("");
		List<Integer> groupList = new ArrayList();
		Map<String,Integer> table = new HashMap();
		
		for(int i = 0; i < chars.length;i++){
			
			// End of named group
			// save the name and position to table and reset name
			if(!escaped && namedGroup && chars[i].equals(">")) {
				namedGroup = false;
				table.put(name,toName);
				name = "";
			}
			
			// In named group : add current letter to the name
			if(namedGroup) name += chars[i];
			
			// Start of a named group
			if(closeBracket && chars[i].equals("<")) {
				namedGroup = true;
			}
			
			// Start of a group save its depth to further use and update depth
			if(!escaped && chars[i].equals("(")) {
				groupList.add(counter);
				counter++;
			}
			
			// End of group : update depth and resolve the pending group
			// save group number to associate its name after
			if(!escaped && chars[i].equals(")")) {
				counter--;
				for(int g = 0; g < groupList.size(); g++){
					if(groupList.get(g) == counter){
						groupList.set(g,0);
						toName = g+1;
					}
				}
				closeBracket = true;
			} else {
				closeBracket = false;
			}
			
			// update the flag for escaped character
			escaped = chars[i].equals("\\");
		}
		return table;
	}

	/**
	 * escape : escape special chars that will be used in bash strings
	 * @param s
	 * @return s with more escape
	 */
	private String escape(String s){
		String out = s.replace("\\","\\\\");
		out = out.replace("\"","\\\"");
		out = out.replace("\'","\\\'");
		out = out.replace("\t","\\t");
		out = out.replace("\n","\\n");
		out = out.replace("\r","\\r");
		out = out.replace("\b","\\b");
		return out;
	}
	
	/**
	 * escapeSed : escape special chars for the sed command
	 * @param s
	 * @return s with more escape
	 */
	private String escapeSed(String s){
		String out = s.replace("\\","\\\\");
		out = out.replace("&","\\&");
		out = out.replace("\'","\\\'");
		out = out.replace("\t","\\t");
		out = out.replace("\n","\\n");
		out = out.replace("\r","\\r");
		out = out.replace("\b","\\b");
		return out;
	}
}
