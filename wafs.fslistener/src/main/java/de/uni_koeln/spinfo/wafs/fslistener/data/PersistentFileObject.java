package de.uni_koeln.spinfo.wafs.fslistener.data;

import java.io.Serializable;
import java.net.URI;

public class PersistentFileObject implements Serializable {

	private static final long serialVersionUID = -6387256968671749930L;
	
	private URI path;
	
	private long lastModified;

	@Override
	public String toString() {
		return "path=" + path + ", lastModified="
				+ lastModified + "";
	}

	public URI getPath() {
		return path;
	}

	public void setPath(URI path) {
		this.path = path;
	}

	public long getLastModified() {
		return lastModified;
	}

	public void setLastModified(long lastModified) {
		this.lastModified = lastModified;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((path == null) ? 0 : path.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PersistentFileObject other = (PersistentFileObject) obj;
		if (path == null) {
			if (other.path != null)
				return false;
		} else if (!path.equals(other.path))
			return false;
		return true;
	}
	
	

}
