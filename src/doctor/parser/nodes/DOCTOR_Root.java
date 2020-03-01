package doctor.parser.nodes;

import java.util.LinkedList;
import java.util.List;

import doctor.processes.IAccept;
import doctor.processes.IVisitor;

public class DOCTOR_Root implements IAccept{
	
	private final DOCTOR_PlainText 		prelude;
	private final DOCTOR_PlainText 		postlude;
	private final DOCTOR_Ordering 		order;
	private final List<DOCTOR_Import> 	imports;
	private final List<DOCTOR_Rule> 	rules;
	
	public DOCTOR_Root(List<DOCTOR_Import> imports,
			DOCTOR_PlainText prelude,
			List<DOCTOR_Rule> rules,
			DOCTOR_Ordering order,
			DOCTOR_PlainText postlude){
		this.imports = imports;
		this.prelude = prelude;
		this.rules = rules;
		this.order = order;
		this.postlude = postlude;
	}

	public DOCTOR_PlainText getPrelude() {
		return prelude;
	}

	public DOCTOR_PlainText getPostlude() {
		return postlude;
	}

	public DOCTOR_Ordering getOrder() {
		return order;
	}

	public List<DOCTOR_Import> getImports() {
		return imports;
	}

	public List<DOCTOR_Rule> getRules() {
		return rules;
	}

	@Override
	public void accept(IVisitor v) {
		v.visit(this);
	}
	
}
