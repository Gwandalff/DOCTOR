package doctor.parser.nodes;

import doctor.processes.IAccept;
import doctor.processes.IVisitor;

public class DOCTOR_Rule implements IAccept {
	
	private final String ID;
	private final DOCTOR_Regex BEFORE;
	private final DOCTOR_Template AFTER;
	
	public DOCTOR_Rule(String id, DOCTOR_Regex before, DOCTOR_Template after) {
		this.ID = id;
		this.BEFORE = before;
		this.AFTER = after;
	}

	public String getID() {
		return ID;
	}

	public DOCTOR_Regex getBEFORE() {
		return BEFORE;
	}

	public DOCTOR_Template getAFTER() {
		return AFTER;
	}

	@Override
	public void accept(IVisitor v) {
		v.visit(this);
	}

}
