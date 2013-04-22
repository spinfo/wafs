package de.uni_koeln.wafs.datakeeper.util;

import java.io.PrintStream;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.logging.StreamHandler;

public class Logging {

	private static Logging INSTANCE = new Logging();
	private Logger logger;

	private Logging() {
	}

	public static Logging getInstance() {
		return INSTANCE;
	}

	public Logger getLogger(Class<?> clazz) {
		this.logger = Logger.getLogger(clazz.getName());
		this.logger.setUseParentHandlers(false);
		this.logger.setLevel(Level.ALL);
		Formatter formatter = initFormatter();
		PrintStream printStream = logger.getLevel().intValue() >= Level.WARNING
				.intValue() ? System.err : System.out;
		logger.addHandler(new StreamHandler(printStream, formatter));
		return this.logger;
	}

	private Formatter initFormatter() {
		Formatter formatter = new Formatter() {

			@Override
			public String format(LogRecord lg) {
				StringBuilder b = new StringBuilder();
				// b.append(lg.getSourceMethodName());
				// b.append(" ");
				b.append("[" + lg.getLevel().getName() + "]");
				b.append("\t");
				b.append(lg.getSourceClassName());
				// b.append(new Date());
				// b.append(" ");
				b.append("\t");
				b.append(lg.getMessage());
				b.append(System.getProperty("line.separator"));
				return b.toString();
			}

		};
		return formatter;
	}

}
