package doctor.parser.nodes;

import doctor.processes.IAccept;
import doctor.processes.IVisitor;

public class DOCTOR_Regex implements IAccept {
	
	// TODO : Make a real regex parser
	private final String regex;
	
	public DOCTOR_Regex(String regex) {
		this.regex = regex;
	}
	
	public String getRegex() {
		return regex;
	}

	@Override
	public void accept(IVisitor v) {
		v.visit(this);
	}

}
