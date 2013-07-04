package de.uni_koeln.spinfo.wafs.gwt.shared;

import com.google.gwt.user.client.rpc.AsyncCallback;

import de.uni_koeln.wafs.datakeeper.query.Result;
import de.uni_koeln.wafs.datakeeper.query.WAFSQuery;

public interface MusicServiceAsync {
	
	public void sayHello(AsyncCallback<String> callback);
	
	public void query(WAFSQuery query, AsyncCallback<Result> result);

}
