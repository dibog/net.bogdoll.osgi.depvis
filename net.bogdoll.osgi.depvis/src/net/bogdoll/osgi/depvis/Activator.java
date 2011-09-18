package net.bogdoll.osgi.depvis;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.bogdoll.osgi.util.IterUtil;
import net.bogdoll.osgi.util.OsgiUtil;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.util.tracker.BundleTracker;
import org.osgi.util.tracker.BundleTrackerCustomizer;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

public class Activator implements BundleActivator, BundleTrackerCustomizer {

	private BundleTracker mBundleTracker;
	private Bundle mBundle;

	@Override
	public void start(BundleContext context) throws Exception {
		mBundleTracker = new BundleTracker(context, Bundle.ACTIVE|Bundle.STARTING, this);
		mBundleTracker.open();
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		mBundleTracker.close();
		mBundleTracker = null;
	}
	
	public void dumpDependeny(PrintStream out, Bundle[] aBundles) {
		Multimap<String, Bundle> exports = HashMultimap.create();
		for(Bundle b : aBundles) {
			if(b.getBundleId()>0 && b.getState()==Bundle.ACTIVE) {
				Map<String, List<String>> unpack = OsgiUtil.unpack((String)b.getHeaders().get("Export-Package"));
				for(String p : unpack.keySet()) {
					exports.put(p, b);
				}
			}
		}
		
		Multimap<Bundle, Object[]> imports = HashMultimap.create();		
		for(Bundle b : aBundles) {
			if(b.getBundleId()>0 && b.getState()==Bundle.ACTIVE) {
				Map<String, List<String>> unpack = OsgiUtil.unpack((String)b.getHeaders().get("Import-Package"));
				for(String p : unpack.keySet()) {
					Set<Bundle> set = new HashSet<Bundle>(exports.get(p));
					List<Bundle> bundles = new ArrayList<Bundle>(set);
					for(Bundle bb : bundles) {
						if(tryPackageInBundle(p,b,bb)) {
							imports.put(b, new Object[]{p,bb});
							break;
						}
					}
				}
			}
		}
		
		out.println("digraph osgi {");
		out.println("\tnode [shape=record];");
		for(Bundle b : aBundles) {
			out.printf("\t%s [shape=record,label=\"{%s|ver=%s}\"];\n", 
					b.getBundleId(),
					b.getSymbolicName(),
					b.getVersion());
		}
		
		for(Bundle b : imports.keySet()) {			
			for(Object[] map : imports.get(b)) {
				Bundle bb = (Bundle) map[1];
				String p = (String) map[0];
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
				
				out.printf("\t%s -> %s [label=\"%s [%s]\"];\n", 
						b.getBundleId(), 
						bb.getBundleId(),
						p, version);
			}
		}
		out.println("}");
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

	@Override
	public Object addingBundle(Bundle bundle, BundleEvent event) {
		updateVisualisation(bundle.getBundleContext().getBundles());
		return bundle;
	}

	@Override
	public void modifiedBundle(Bundle bundle, BundleEvent event, Object object) {
		updateVisualisation(bundle.getBundleContext().getBundles());
	}

	@Override
	public void removedBundle(Bundle bundle, BundleEvent event, Object object) {
		updateVisualisation(bundle.getBundleContext().getBundles());
	}
	
	private void updateVisualisation(Bundle[] aBundles) {
		FileOutputStream fout = null;
		try {
			fout = new FileOutputStream("osgi.dot");
			dumpDependeny(new PrintStream(fout), aBundles);
		}
		catch (FileNotFoundException e) {
			Logger.getLogger(Activator.class.getName()).log(Level.SEVERE, "Couldn't use file 'osgi.dot'", e);
		}
		finally {
			if(fout!=null) {
				try {
					fout.close();
				} catch(IOException e) {
					// ignoring the closing exception
				}
			}
		}
	}
}
