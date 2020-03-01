package doctor.parser.nodes;

import java.util.List;

import doctor.processes.IAccept;
import doctor.processes.IVisitor;

public class DOCTOR_RecursiveCall implements IAccept, DOCTOR_Call {
	
	private final List<DOCTOR_Call> subcalls;
	
	public DOCTOR_RecursiveCall(List<DOCTOR_Call> subcalls) {
		this.subcalls = subcalls;
	}
	
	public List<DOCTOR_Call> getSubcalls(){
		return subcalls;
	}

	@Override
	public void accept(IVisitor v) {
		v.visit(this);
	}
}
