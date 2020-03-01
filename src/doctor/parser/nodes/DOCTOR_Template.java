package doctor.parser.nodes;

import doctor.processes.IAccept;
import doctor.processes.IVisitor;

public class DOCTOR_Template implements IAccept {
	
	private final String template;
	
	public DOCTOR_Template(String template) {
		this.template = template;
	}
	
	public String getTemplate() {
		return template;
	}

	@Override
	public void accept(IVisitor v) {
		v.visit(this);
	}

}
