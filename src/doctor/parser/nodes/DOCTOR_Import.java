package doctor.parser.nodes;

import doctor.processes.IAccept;
import doctor.processes.IVisitor;

public class DOCTOR_Import implements IAccept {
	
	private final String URL;
	
	public DOCTOR_Import(String url) {
		this.URL = url;
	}
	
	public String getURL() {
		return URL;
	}

	@Override
	public void accept(IVisitor v) {
		v.visit(this);
	}

}
