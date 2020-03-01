package doctor.processes;

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

public class PrettyPrinter implements IVisitor {

	@Override
	public void visit(DOCTOR_Root node) {
		List<DOCTOR_Import> imports = node.getImports();
		for (DOCTOR_Import i : imports) {
			i.accept(this);
		}
		
		DOCTOR_PlainText pre = node.getPrelude();
		if(pre != null) {
			System.out.print("prelude ");
			pre.accept(this);
		}

		List<DOCTOR_Rule> rules = node.getRules();
		for (DOCTOR_Rule r : rules) {
			r.accept(this);
		}
		
		DOCTOR_Ordering order = node.getOrder();
		if(order != null) {
			order.accept(this);
		}
		
		
		DOCTOR_PlainText post = node.getPostlude();
		if(post != null) {
			System.out.print("postlude ");
			post.accept(this);
		}
	}

	@Override
	public void visit(DOCTOR_Import node) {
		System.out.println("import " + node.getURL());
	}

	@Override
	public void visit(DOCTOR_PlainText node) {
		System.out.println("{");
		System.out.println(node.getContent());
		System.out.println("}");
	}

	@Override
	public void visit(DOCTOR_Rule node) {
		System.out.print("rule ");
		node.getBEFORE().accept(this);
		System.out.print(" -> ");
		node.getAFTER().accept(this);
	}

	@Override
	public void visit(DOCTOR_Ordering node) {
		System.out.println("order {");
		List<DOCTOR_Call> rules = node.getRules();
		for (DOCTOR_Call r : rules) {
			r.accept(this);
		}
		System.out.println("}");
	}

	@Override
	public void visit(DOCTOR_Regex node) {
		System.out.print("\"" + node.getRegex() + "\"");
	}

	@Override
	public void visit(DOCTOR_Template node) {
		System.out.println("`\n" + node.getTemplate() + "\n`");
	}

	@Override
	public void visit(DOCTOR_DirectCall node) {
		System.out.println(node.getID());

	}

	@Override
	public void visit(DOCTOR_RecursiveCall node) {
		System.out.println("recursive {");
		List<DOCTOR_Call> rules = node.getSubcalls();
		for (DOCTOR_Call r : rules) {
			r.accept(this);
		}
		System.out.println("}");
	}

	@Override
	public void visit(DOCTOR_Call node) {
		if(node instanceof DOCTOR_DirectCall) {
			((DOCTOR_DirectCall) node).accept(this);
		} else {
			((DOCTOR_RecursiveCall) node).accept(this);
		}
	}

}
