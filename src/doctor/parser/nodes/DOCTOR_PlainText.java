package doctor.parser.nodes;

import doctor.processes.IAccept;
import doctor.processes.IVisitor;

public class DOCTOR_PlainText implements IAccept {
	
	private final String content;
	
	public DOCTOR_PlainText(String content) {
		this.content = content;
	}

	public String getContent() {
		return content;
	}

	@Override
	public void accept(IVisitor v) {
		v.visit(this);
	}

}
