package de.uni_koeln.spinfo.wafs.fslistener;


public class CorruptDBException extends Exception {

	private static final long serialVersionUID = -2233879124401385144L;

	public CorruptDBException(String message, Exception cause) {
		super(message, cause);
	}

}
