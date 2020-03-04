package doctor.parser;

import doctor.parser.nodes.DOCTOR_Root;

public abstract class AbstractParser<T> {
	private T source;
	
	/**
	 * This abstract constructor load the source relying on specialized method
	 * @param path
	 */
	public AbstractParser(String path) {
		source = loadSource(path);
	}
	
	/**
	 * Getter of the loaded source
	 * @return the loaded source
	 */
	protected final T source() {
		return source;
	}
	
	/**
	 * Provide the behavior of loading a source of type T
	 * @param path
	 * @return the loaded source
	 */
	protected abstract T loadSource(String path);
	
	/**
	 * The core function used to parse the source
	 * @return the AST of the source in the form of it's root
	 */
	public abstract DOCTOR_Root parse();
}
