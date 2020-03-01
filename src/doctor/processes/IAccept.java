package doctor.processes;

/**
 * IAccept : interface for nodes that can be visited by an {@link IVisitor}
 * @author gwandalf
 *
 */
public interface IAccept {
	void accept(IVisitor v);
}
