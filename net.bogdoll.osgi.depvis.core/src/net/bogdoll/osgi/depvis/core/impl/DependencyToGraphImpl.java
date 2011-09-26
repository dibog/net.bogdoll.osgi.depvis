package net.bogdoll.osgi.depvis.core.impl;

import java.util.List;
import java.util.Map;

import net.bogdoll.osgi.depvis.core.DependencyToGraph;
import net.bogdoll.osgi.depvis.core.Graph;
import net.bogdoll.osgi.util.OsgiUtil;

import org.osgi.framework.Bundle;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

public class DependencyToGraphImpl extends Impl implements DependencyToGraph
{
	@Override
	public Graph toGraph(Bundle[] aBundles) {
		Multimap<String, Bundle> exports = collectExportPackageInformations(aBundles);		
		Multimap<Bundle, Pair<String,Bundle>> imports = collectImportPackageInformations(aBundles, exports);
		Graph g = createGraph(imports, aBundles);
		System.out.println(g);
		return g;
	}
	
	private Graph createGraph(Multimap<Bundle, Pair<String,Bundle>> aImports, Bundle[] aBundles) {
		Graph g = new Graph();
		for(Bundle b : aBundles) {
			g.addVertex(b.getBundleId(), b.getSymbolicName(), b.getVersion().toString());
		}
		
		for(Bundle b : aImports.keySet()) {			
			Multimap<Bundle,String> packages = HashMultimap.create();
			for(Pair<String,Bundle> pair : aImports.get(b)) {
				Bundle bb = pair.second;
				String p = pair.first;
				String exp = bb.getHeaders().get("Export-Package").toString();
				Map<String, List<String>> unpack = OsgiUtil.unpack(exp);
				String version = null;
				for(String c : unpack.get(p)) {
					if(c.startsWith("version=")) {
						version = c;
						break;
					}
				}
				
				version = (version==null) ? "?" : version.substring(8).replaceAll("\"","");
				packages.put(bb, String.format("%s [%s]", p, version));			
			}
			
			for(Bundle bb : packages.keySet()) {
				g.addEdge(b.getBundleId(), bb.getBundleId(), packages.get(bb).toArray(new String[0]));
			}
		}

		return g;
	}
}
