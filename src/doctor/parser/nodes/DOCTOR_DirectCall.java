package doctor.parser.nodes;

import doctor.processes.IAccept;
import doctor.processes.IVisitor;

public class DOCTOR_DirectCall implements IAccept, DOCTOR_Call {
	private final String ID;
	
	public DOCTOR_DirectCall(String id) {
		this.ID = id;
	}
	
	public String getID() {
		return this.ID;
	}
	
	@Override
	public void accept(IVisitor v) {
		v.visit(this);
	}
}
