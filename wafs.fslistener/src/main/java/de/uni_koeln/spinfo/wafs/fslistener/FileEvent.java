package de.uni_koeln.spinfo.wafs.fslistener;

import de.uni_koeln.spinfo.wafs.fslistener.data.PersistentDir;
import de.uni_koeln.spinfo.wafs.fslistener.data.PersistentFileObject;

public class FileEvent {
	
	public enum Type {
		ADDED, MODIFIED, DELETED;
	}
	
	private final PersistentFileObject object;
	
	private final Type type;
	
	public FileEvent(PersistentFileObject object, Type type) {
		if(object == null) throw new IllegalArgumentException("Parameter object must not be null!");
		if(type == null) throw new IllegalArgumentException("Parameter type must not be null!");
		this.object = object;
		this.type = type;
	}
	
	public boolean isDirectory() {
		return object instanceof PersistentDir;
	}

	public PersistentFileObject getObject() {
		return object;
	}

	public Type getType() {
		return type;
	}

	@Override
	public String toString() {
		return "FileEvent [" + object + " " + type + "]";
	}

	
	
	
}
