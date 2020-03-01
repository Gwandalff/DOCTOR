package doctor.processes;

import doctor.parser.nodes.*;

/**
 * IVisitor : interface of the computations on the DOCTOR Language
 * @author gwandalf
 *
 */
public interface IVisitor {
	void visit(DOCTOR_Root node);
	
	void visit(DOCTOR_Import node);
	void visit(DOCTOR_PlainText node);
	void visit(DOCTOR_Rule node);
	void visit(DOCTOR_Ordering node);
	
	void visit(DOCTOR_Regex node);
	void visit(DOCTOR_Template node);
	
	void visit(DOCTOR_Call node);
	void visit(DOCTOR_DirectCall node);
	void visit(DOCTOR_RecursiveCall node);
}
