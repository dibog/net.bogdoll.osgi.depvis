package net.bogdoll.osgi.depvis.core.impl;

import java.util.Enumeration;
import java.util.List;
import java.util.Map;

import net.bogdoll.osgi.depvis.core.DependencyToDot;
import net.bogdoll.osgi.util.IterUtil;
import net.bogdoll.osgi.util.OsgiUtil;

import org.osgi.framework.Bundle;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

public class DependencyToDotImpl implements DependencyToDot 
{
//	private final static Logger LOG = Logger.getLogger(DependencyToDotImpl.class.getName());
	
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
				
				sb.append(String.format("\t%s -> %s [label=\"%s [%s]\"];\n", 
							b.getBundleId(), 
							bb.getBundleId(),
							p, version));
			}
		}
		sb.append("}\n");
		
		return sb.toString();
	}

	private Multimap<Bundle, Pair<String,Bundle>> collectImportPackageInformations(
			Bundle[] aBundles, Multimap<String, Bundle> exports) {
		Multimap<Bundle, Pair<String,Bundle>> imports = HashMultimap.create();		
		for(Bundle b : aBundles) {
			if(b.getState()==Bundle.ACTIVE) {
				Map<String, List<String>> unpack = OsgiUtil.unpack((String)b.getHeaders().get("Import-Package"));
				for(String p : unpack.keySet()) {
					for(Bundle bb : exports.get(p)) {
						if(tryPackageInBundle(p,b,bb)) {
							imports.put(b, Pair.create(p,bb));
							//LOG.info(String.format("Bundle '%s (%s)' is using package '%s' from bundle '%s (%s)'",
								//	b.getSymbolicName(), b.getVersion(), p, bb.getSymbolicName(), bb.getVersion()));
							break;
						}
						else {
							//LOG.info(String.format("Bundle '%s (%s)' is NOT using package '%s' from bundle '%s (%s)'",
								//	b.getSymbolicName(), b.getVersion(), p, bb.getSymbolicName(), bb.getVersion()));
						}
					}
				}
			}
		}
		return imports;
	}

	private Multimap<String, Bundle> collectExportPackageInformations(
			Bundle[] aBundles) {
		Multimap<String, Bundle> exports = HashMultimap.create();
		for(Bundle b : aBundles) {
			if(b.getState()==Bundle.ACTIVE) {
				Map<String, List<String>> unpack = OsgiUtil.unpack((String)b.getHeaders().get("Export-Package"));
				for(String p : unpack.keySet()) {
					//String packageVersion = extractVersion(unpack.get(p));
					//LOG.info(String.format("Bundle '%s (%s)' exports '%s (%s)'", b.getSymbolicName(), b.getVersion(), p, packageVersion));
					exports.put(p, b);
				}
			}
		}
		return exports;
	}

	@SuppressWarnings("unused")
	private String extractVersion(List<String> aPackageInfo) {
		for(String elem : aPackageInfo) {
			if(elem.startsWith("version=")) {
				return elem.substring(8,elem.length()-1).replaceAll("\"", "");
			}
		}
		return "0";
	}

	private boolean tryPackageInBundle(String p, Bundle aParent, Bundle aCandidate) {
		@SuppressWarnings("unchecked")
		Enumeration<String> paths = (Enumeration<String>)aCandidate.getEntryPaths("/"+p.replace('.','/'));
		for(String path : IterUtil.toIterable(paths)) {
			if(path.endsWith(".class")) {
				String cname = extractClassName(path);
				if(cname!=null) {
					try {
						Class<?> c1 = aCandidate.loadClass(cname);
						Class<?> c2 = aParent.loadClass(cname);
						return c1.equals(c2);
					} catch (ClassNotFoundException e) {
					}
				}
			}
		}
		return false;
	}

	private String extractClassName(String path) {
		return path.substring(0, path.length()-6).replaceAll("/",".");
	}
}
