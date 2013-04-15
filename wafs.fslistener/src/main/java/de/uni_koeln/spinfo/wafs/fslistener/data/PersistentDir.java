package de.uni_koeln.spinfo.wafs.fslistener.data;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.HashMap;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlTransient;

import org.apache.log4j.Logger;


@XmlRootElement
@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlSeeAlso(PersistentFile.class)
public class PersistentDir extends PersistentFileObject {
	
	private static final long serialVersionUID = 5103050140612020237L;
	
	private HashMap<URI, PersistentFileObject> children = new HashMap<>();
	
	private transient static Logger logger = Logger.getLogger(PersistentDir.class);
	
	private static final boolean debug = logger.isDebugEnabled();
	
	public int getNumberOfChildren() {
		return children.size();
	}

	public Collection<? extends PersistentFileObject> getChildObjects() {
		return children.values();
	}

	public void setChildren(HashMap<URI, PersistentFileObject> children) {
		this.children = children;
	}

	public HashMap<URI, PersistentFileObject> getChildren() {
		return children;
	}

	@XmlTransient
	public void setChildren(Collection<? extends PersistentFileObject> children) {
		this.children = new HashMap<URI, PersistentFileObject>();
		for (PersistentFileObject child : children) {
			this.children.put(child.getPath(), child);
		}
	}
	
	public PersistentDir findOrCreate(File f) throws URISyntaxException {
		if(f.equals(new File(getPath()))) return this;
		if(f.isFile()) {
			throw new IllegalArgumentException("Parameter must be a directory, but was a file!");
		}
		URI rel = super.getPath().relativize(f.toURI());
		String[] segments = rel.toString().split("/");
		URI childURI = new URI(getPath() + (getPath().toString().endsWith("/") ? "" : "/") + segments[0]);
		PersistentDir child = (PersistentDir) getChildren().get(childURI);
		if(child == null) {
			child = new PersistentDir();
			child.setPath(childURI);
			child.setLastModified(new File(childURI).lastModified());
			children.put(childURI, child);
		}
		return child.findOrCreate(f);
	}

	public boolean updateTimeStamp(URI uri, long lastModified) throws URISyntaxException {
		URI rel = super.getPath().relativize(uri);
		if(uri.equals(getPath()) || rel.toString().length() == 0) {
			if(lastModified == getLastModified()) {
				if(debug )
					logger.debug("Directory unchanged: " + getPath());
				return false;
			}
			if(debug )
				logger.debug("Directory changed: " + getPath());
			setLastModified(lastModified);
			return true;
		}
		String[] segments = rel.toString().split("/");
		URI childURI = new URI(getPath() + (getPath().toString().endsWith("/") ? "" : "/") + segments[0]);
		PersistentDir child = (PersistentDir) getChildren().get(childURI);
		if(child == null) {
			if(debug) 
				logger.debug("New directory: " + childURI + " not found in " + getPath());
			return true;
		}
		return child.updateTimeStamp(uri, lastModified);

	}

	public PersistentFileObject remove(UnknownFSObject unknownFile) throws URISyntaxException {
		File file = new File(unknownFile.getPath());
		File parent = file.getParentFile();
		PersistentDir parentDir = findOrCreate(parent);
		PersistentFileObject removed = parentDir.getChildren().remove(unknownFile.getPath());
		return removed;
	}

	public void addFile(PersistentFile file) {
		children.put(file.getPath(), file);
	}

}
