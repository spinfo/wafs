package de.uni_koeln.spinfo.wafs.gwt.shared;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import de.uni_koeln.wafs.datakeeper.query.Result;
import de.uni_koeln.wafs.datakeeper.query.WAFSQuery;

@RemoteServiceRelativePath("rpc/music")
public interface MusicService extends RemoteService {
	
	public String sayHello();
	
	public Result query(WAFSQuery query);
	
}