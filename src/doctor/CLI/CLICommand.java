package doctor.CLI;

public abstract class CLICommand<T> {

	public CLICommand() {
	}

	protected abstract CLIConfiguration createConfiguration();
	protected abstract void acceptConfiguration(CLIConfiguration conf);
	protected abstract T execute();
	protected final T launch(String... args) {
		CLIConfiguration conf = this.createConfiguration();
		CLIArgumentParser parser = new CLIArgumentParser(conf);
		parser.parse(args).validate();
		this.acceptConfiguration(conf);
		return this.execute();
	}
}
