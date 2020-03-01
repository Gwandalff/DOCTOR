package doctor.parser.nodes;

import java.util.List;

import doctor.processes.IAccept;
import doctor.processes.IVisitor;

public class DOCTOR_Ordering implements IAccept {
	
	private final List<DOCTOR_Call> rules;
	
	public DOCTOR_Ordering(List<DOCTOR_Call> rules) {
		this.rules = rules;
	}

	public List<DOCTOR_Call> getRules() {
		return rules;
	}

	@Override
	public void accept(IVisitor v) {
		v.visit(this);
	}

}
