package de.uni_koeln.spinfo.wafs.fslistener.data;

import java.io.File;


public class UnknownFSObject extends PersistentFileObject {

	public UnknownFSObject() {
		
	}
	
	public UnknownFSObject(File f) {
		super.setPath(f.toURI());
	}

	private static final long serialVersionUID = 7740432135682931836L;

}
