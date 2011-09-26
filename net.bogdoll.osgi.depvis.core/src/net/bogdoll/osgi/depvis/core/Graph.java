package net.bogdoll.osgi.depvis.core;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Graph implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private Map<Long, Bundle> mBundles = new HashMap<Long, Bundle>();
	private Map<Long, Map<Long,String[]>> mEdges = new HashMap<Long, Map<Long,String[]>>();
	
	public void addVertex(long aBundleId, String aSymbolicName, String aVersion) {
		mBundles.put(aBundleId, new Bundle(aBundleId, aSymbolicName,aVersion));
	}

	public void addEdge(long aFrom, long aTo, String[] aPackages) {
		Map<Long, String[]> map = mEdges.get(aFrom);
		if(map==null) {
			map = new HashMap<Long, String[]>();
			mEdges.put(aFrom, map);
		}
		map.put(aTo, aPackages);
	}

	public Collection<Bundle> bundles() {
		return mBundles.values();
	}
	
	public Iterable<Dependency> dependencies() {
		List<Dependency> dependencies = new ArrayList<Dependency>();
		for(Long from : mEdges.keySet()) {
			Map<Long, String[]> map = mEdges.get(from);
			Bundle fromBundle = mBundles.get(from);
			for(Long to : map.keySet()) {
				Bundle toBundle = mBundles.get(to);
				dependencies.add(new Dependency(fromBundle, toBundle, map.get(to)));
			}
		}
		return dependencies;
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Graph[\n\tVertices:\n");
		for(Bundle b : bundles()) {
			sb.append(String.format("\t\t%s_%s\n", b.getSymbolicName(), b.getVersion()));
		}
		sb.append("\tEdges:\n");
		for(Dependency d : dependencies()) {
			StringBuilder sb2 = new StringBuilder();
			for(String p : d.getPackages()) {
				sb2.append(", ").append(p);
			}
			String p = sb2.length()<2 ? "" : sb2.substring(2);
			sb.append(
					String.format("\t\t%s_%s -> %s_%s [%s]\n", 
							d.getFromBundle().getSymbolicName(),
							d.getFromBundle().getVersion(),
							d.getToBundle().getSymbolicName(),
							d.getToBundle().getVersion(),
							p));
		}
		sb.append("]\n");
		return sb.toString();
	}
	
}
