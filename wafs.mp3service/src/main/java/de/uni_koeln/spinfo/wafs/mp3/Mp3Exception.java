package de.uni_koeln.spinfo.wafs.mp3;

/**
 * 
 * @author sschwieb
 *
 */
public class Mp3Exception extends Exception {

	private static final long serialVersionUID = 8011073359271217523L;

	public Mp3Exception(Exception e) {
		super(e);
	}

	public Mp3Exception(String message, Exception cause) {
		super(message, cause);
	}

}
