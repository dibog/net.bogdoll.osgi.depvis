package net.bogdoll.osgi.depvis.core.impl;

import java.util.List;
import java.util.Map;

import net.bogdoll.osgi.depvis.core.DependencyToDot;
import net.bogdoll.osgi.util.OsgiUtil;

import org.osgi.framework.Bundle;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

public class DependencyToDotImpl extends Impl implements DependencyToDot 
{
	@Override
	public String toDot(Bundle[] aBundles) {
		Multimap<String, Bundle> exports = collectExportPackageInformations(aBundles);		
		Multimap<Bundle, Pair<String,Bundle>> imports = collectImportPackageInformations(aBundles, exports);
		String asDot = createDotRepresentation(imports, aBundles);
		return asDot;
	}
	
	private String createDotRepresentation(Multimap<Bundle, Pair<String,Bundle>> aImports, Bundle[] aBundles) {
		StringBuilder sb = new StringBuilder();
		sb.append("digraph osgi {\n");
		sb.append("\tnode [shape=record];\n");
		for(Bundle b : aBundles) {
			sb.append(String.format("\t%s [shape=record,label=\"{%s|ver=%s}\"];\n", 
						b.getBundleId(),
						b.getSymbolicName(),
						b.getVersion()));
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
				packages.put(bb, String.format("\\n%s [%s]", p, version));			
			}
			
			for(Bundle bb : packages.keySet()) {
				StringBuilder sb2 = new StringBuilder();
				for(String p : packages.get(bb)) {
					sb2.append(p);
				}
				sb.append(String.format("\t%s -> %s [label=\"%s\"];\n", 
						b.getBundleId(), 
						bb.getBundleId(), 
						sb2.toString().substring(2))); 
			}
		}
		sb.append("}\n");
		
		return sb.toString();
	}
}
