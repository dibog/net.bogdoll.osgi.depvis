package net.bogdoll.osgi.depvis.core.impl;

import java.util.Enumeration;
import java.util.List;
import java.util.Map;

import net.bogdoll.osgi.util.IterUtil;
import net.bogdoll.osgi.util.OsgiUtil;

import org.osgi.framework.Bundle;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

class Impl 
{
	protected Multimap<Bundle, Pair<String,Bundle>> collectImportPackageInformations(
			Bundle[] aBundles, Multimap<String, Bundle> exports) {
		Multimap<Bundle, Pair<String,Bundle>> imports = HashMultimap.create();		
		for(Bundle b : aBundles) {
			Map<String, List<String>> unpack = OsgiUtil.unpack((String)b.getHeaders().get("Import-Package"));
			for(String p : unpack.keySet()) {
				for(Bundle bb : exports.get(p)) {
					if(tryPackageInBundle(p,b,bb)) {
						imports.put(b, Pair.create(p,bb));
						break;
					}
				}
			}
		}
		return imports;
	}

	protected Multimap<String, Bundle> collectExportPackageInformations(Bundle[] aBundles) {
		Multimap<String, Bundle> exports = HashMultimap.create();
		for(Bundle b : aBundles) {
			Map<String, List<String>> unpack = OsgiUtil.unpack((String)b.getHeaders().get("Export-Package"));
			for(String p : unpack.keySet()) {
				exports.put(p, b);
			}
		}
		return exports;
	}

	protected  String extractVersion(List<String> aPackageInfo) {
		for(String elem : aPackageInfo) {
			if(elem.startsWith("version=")) {
				return elem.substring(8,elem.length()-1).replaceAll("\"", "");
			}
		}
		return "0";
	}

	protected boolean tryPackageInBundle(String p, Bundle aParent, Bundle aCandidate) {
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

	protected String extractClassName(String path) {
		return path.substring(0, path.length()-6).replaceAll("/",".");
	}
}
