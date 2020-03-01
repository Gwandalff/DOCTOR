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

public class ASTVizualizer implements IVisitor {
	
	private String indent = "\t\t";

	@Override
	public void visit(DOCTOR_Root node) {
		System.out.println("Root");
		
		System.out.println("\tImports");
		for (DOCTOR_Import i : node.getImports()) {
			i.accept(this);
		}
		
		System.out.println("\tPrelude");
		DOCTOR_PlainText pre = node.getPrelude();
		if(pre != null) {
			pre.accept(this);
		}
		
		System.out.println("\tRules");
		for (DOCTOR_Rule r : node.getRules()) {
			r.accept(this);
		}
		
		System.out.println("\tOrdering");
		DOCTOR_Ordering order = node.getOrder();
		if(order != null) {
			order.accept(this);
		}
		
		System.out.println("\tPostlude");
		DOCTOR_PlainText post = node.getPostlude();
		if(post != null) {
			post.accept(this);
		}
	}

	@Override
	public void visit(DOCTOR_Import node) {
		System.out.println(indent+node.getURL());
	}

	@Override
	public void visit(DOCTOR_PlainText node) {
		System.out.println(indent+node.getContent().replace("\n", "\n"+indent));
	}

	@Override
	public void visit(DOCTOR_Rule node) {
		System.out.println(indent+node.getID());
		System.out.println(indent+"\t"+node.getBEFORE().getRegex());
		System.out.println(indent+"\t"+node.getAFTER().getTemplate().replace("\n", "\n\t"+indent));
	}

	@Override
	public void visit(DOCTOR_Ordering node) {
		List<DOCTOR_Call> calls = node.getRules();
		for (DOCTOR_Call call : calls) {
			call.accept(this);
		}
	}

	@Override
	public void visit(DOCTOR_Regex node) {
	}

	@Override
	public void visit(DOCTOR_Template node) {
	}

	@Override
	public void visit(DOCTOR_Call node) {
		System.err.println("NEEDED DOCTOR_CALL");
		if (node instanceof DOCTOR_DirectCall) {
			((DOCTOR_DirectCall) node).accept(this);
		} else {
			((DOCTOR_RecursiveCall) node).accept(this);
		}
	}

	@Override
	public void visit(DOCTOR_DirectCall node) {
		System.out.println(indent + node.getID());
	}

	@Override
	public void visit(DOCTOR_RecursiveCall node) {
		System.out.println(indent+"recursive");
		indent = indent + "\t";
		List<DOCTOR_Call> calls = node.getSubcalls();
		for (DOCTOR_Call call : calls) {
			call.accept(this);
		}
		indent = indent.substring(1);
	}

}
